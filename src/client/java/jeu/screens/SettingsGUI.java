package jeu.screens;

import jeu.JustEnoughUpdatesClient;
import jeu.ModCommands;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;

import static jeu.screens.ModConfig.features;

public class SettingsGUI extends Screen {
    ArrayList<FeatureCardWidget> widgets;
    ArrayList<FeatureCardWidget> allWidgets; // Store all widgets for filtering
    private int scrollY = 0;
    private int maxScroll = 0;
    private int minScroll = 30; // Minimum scroll to bring content closer to top
    private final int padding = 10;
    private ButtonWidget exitButton;
    private TextFieldWidget searchField;
    private String currentSearchQuery = "";

    // Scroll bar properties
    private boolean isDraggingScrollbar = false;
    private int scrollbarWidth = 6;
    private int scrollbarX;
    private int scrollbarY = 40; // Start below search field
    private int scrollbarHeight;
    private int scrollbarThumbY;
    private int scrollbarThumbHeight;
    private int dragStartOffset; // Track initial mouse offset when dragging starts


    public SettingsGUI(int scroll) {
        super(Text.literal("Settings"));
        scrollY = scroll;
    }
    @Override
    public void removed() {
        super.removed();
        ModConfig.save();
        JustEnoughUpdatesClient.refreshFeatures();;
    }
    @Override
    public void init() {
        allWidgets = new ArrayList<>();
        widgets = new ArrayList<>();
        this.clearChildren();
        int searchFieldWidth = 200;
        int searchFieldX = (this.width - searchFieldWidth) / 2;
        int searchFieldY = padding;
        searchField = new TextFieldWidget(
            this.textRenderer,
            searchFieldX, searchFieldY,
            searchFieldWidth, 20,
            Text.literal("Search features...")
        );
        searchField.setPlaceholder(Text.literal("Search features..."));
        searchField.setChangedListener(this::onSearchChanged);
        this.addDrawableChild(searchField);

        int totalHeight = 50;
        if (features != null) {
            for (int i = 0; i < features.length; i++) {
                if(features[i].type.equals("number")) continue;
                FeatureCardWidget widget = new FeatureCardWidget(features[i], padding);
                allWidgets.add(widget);
                widgets.add(widget);
                totalHeight += widget.height;
                if (widget.toggleButton != null) {
                    this.addDrawableChild(widget.toggleButton);
                }
                if (widget.inputField != null) {
                    this.addDrawableChild(widget.inputField);
                }
                if (widget.positionButton != null) {
                    this.addDrawableChild(widget.positionButton);
                }
            }
        }
        totalHeight += padding;
        updateMaxScroll();
        scrollY = minScroll; // Start at minimum scroll to bring content closer to top

        // Exit button (fixed, not affected by scroll)
        exitButton = ButtonWidget.builder(
                Text.literal("X"),
                btn -> {
                    if (this.client != null) this.client.setScreen(null);
                }
        ).position(5, 5).size(20, 20).build();
    }

    private void onSearchChanged(String query) {
        currentSearchQuery = query.toLowerCase().trim();
        filterWidgets();
        updateMaxScroll();
        scrollY = minScroll; // Reset to minimum scroll when search changes
    }

    private void filterWidgets() {
        widgets.clear();

        if (currentSearchQuery.isEmpty()) {
            widgets.addAll(allWidgets);
        } else {
            ArrayList<ScoredWidget> scoredWidgets = new ArrayList<>();

            for (FeatureCardWidget widget : allWidgets) {
                int score = calculateSearchScore(widget, currentSearchQuery);
                if (score > 0) {
                    scoredWidgets.add(new ScoredWidget(widget, score));
                }
            }

            scoredWidgets.sort((a, b) -> Integer.compare(b.score, a.score));
            for (ScoredWidget scored : scoredWidgets) {
                widgets.add(scored.widget);
            }
        }

        for (FeatureCardWidget widget : allWidgets) {
            boolean shouldBeVisible = widgets.contains(widget);
            if (widget.toggleButton != null) {
                widget.toggleButton.visible = shouldBeVisible;
            }
            if (widget.positionButton != null) {
                widget.positionButton.visible = shouldBeVisible;
            }
            if (widget.inputField != null) {
                widget.inputField.visible = shouldBeVisible;
            }
        }
    }

    private int calculateSearchScore(FeatureCardWidget widget, String query) {
        ModConfig.Config config = getConfigForWidget(widget);
        if (config == null) return 0;

        String name = config.name.getString().toLowerCase();
        String description = config.description.getString().toLowerCase();
        String category = config.category != null ? config.category.toLowerCase() : "";

        int totalScore = 0;

        int nameScore = getStringScore(name, query);
        totalScore += nameScore * 100;

        int categoryScore = getStringScore(category, query);
        totalScore += categoryScore * 50;

        int descriptionScore = getStringScore(description, query);
        totalScore += descriptionScore * 20;

        return totalScore;
    }

    // guys i swear this isnt vibe code!1!!!1
    private int getStringScore(String target, String query) {
        if (target.isEmpty() || query.isEmpty()) return 0;

        // Exact match - perfect score
        if (target.equals(query)) {
            return 1000;
        }

        // Prefix match - very high score
        if (target.startsWith(query)) {
            return 800 + (query.length() * 10); // Longer prefixes score higher
        }

        // Exact substring match - high score
        if (target.contains(query)) {
            int position = target.indexOf(query);
            // Earlier positions score higher
            int positionBonus = Math.max(0, 50 - position);
            return 600 + (query.length() * 5) + positionBonus;
        }

        // Word boundary match - good score
        String[] words = target.split("\\s+");
        for (String word : words) {
            if (word.startsWith(query)) {
                return 400 + (query.length() * 5);
            }
        }

        // Fuzzy match - lower score based on how well it matches
        int fuzzyScore = getFuzzyMatchScore(target, query);
        if (fuzzyScore > 0) {
            return fuzzyScore;
        }

        return 0;
    }

    /**
     * Calculates fuzzy match score based on:
     * - How many characters match in sequence
     * - How close together the matching characters are
     * - What percentage of the query was matched
     */
    private int getFuzzyMatchScore(String target, String query) {
        int queryIndex = 0;
        int targetIndex = 0;
        int consecutiveMatches = 0;
        int maxConsecutive = 0;
        int totalMatches = 0;
        int lastMatchIndex = -1;
        int gapPenalty = 0;

        while (queryIndex < query.length() && targetIndex < target.length()) {
            if (query.charAt(queryIndex) == target.charAt(targetIndex)) {
                totalMatches++;

                // Track consecutive matches
                if (lastMatchIndex == targetIndex - 1) {
                    consecutiveMatches++;
                } else {
                    maxConsecutive = Math.max(maxConsecutive, consecutiveMatches);
                    consecutiveMatches = 1;

                    // Add gap penalty for non-consecutive matches
                    if (lastMatchIndex != -1) {
                        gapPenalty += (targetIndex - lastMatchIndex - 1);
                    }
                }

                lastMatchIndex = targetIndex;
                queryIndex++;
            }
            targetIndex++;
        }

        maxConsecutive = Math.max(maxConsecutive, consecutiveMatches);

        // Must match all characters to be considered a fuzzy match
        if (queryIndex < query.length()) {
            return 0;
        }

        // Calculate score based on match quality
        double completeness = (double) totalMatches / query.length();
        double consecutiveBonus = (double) maxConsecutive / query.length();
        double compactness = 1.0 - Math.min(1.0, (double) gapPenalty / (target.length() * 2));

        int baseScore = (int) (200 * completeness * consecutiveBonus * compactness);

        // Bonus for matching a high percentage of the target string
        if (query.length() >= target.length() * 0.7) {
            baseScore += 50;
        }

        return Math.max(1, baseScore); // Minimum score of 1 for any fuzzy match
    }

    private boolean matchesSearch(FeatureCardWidget widget, String query) {
        return calculateSearchScore(widget, query) > 0;
    }

    private ModConfig.Config getConfigForWidget(FeatureCardWidget widget) {
        for (ModConfig.Config config : features) {
            if (config.name.getString().equals(widget.name.getString())) {
                return config;
            }
        }
        return null;
    }

    /**
     * Fuzzy string matching algorithm - checks if all characters of query appear in target in order
     * Also handles exact substring matches for better UX
     */
    private boolean fuzzyMatch(String target, String query) {
        if (query.isEmpty()) return true;
        if (target.isEmpty()) return false;

        // Exact substring match gets priority
        if (target.contains(query)) {
            return true;
        }

        // Fuzzy matching - all characters must appear in order
        int queryIndex = 0;
        int targetIndex = 0;

        while (queryIndex < query.length() && targetIndex < target.length()) {
            if (query.charAt(queryIndex) == target.charAt(targetIndex)) {
                queryIndex++;
            }
            targetIndex++;
        }

        return queryIndex == query.length();
    }

    private void updateMaxScroll() {
        int totalHeight = 50; // Account for search bar
        for (FeatureCardWidget widget : widgets) {
            totalHeight += widget.height;
        }
        totalHeight += padding;
        maxScroll = Math.max(0, totalHeight - this.height + padding);
        updateScrollbarDimensions();
    }

    private void updateScrollbarDimensions() {
        // Position scrollbar on the right side
        scrollbarX = this.width - scrollbarWidth - 5;
        scrollbarHeight = this.height - scrollbarY - 10;

        // Calculate thumb size based on content ratio
        int totalScrollRange = maxScroll - minScroll;
        if (totalScrollRange > 0) {
            float visibleRatio = (float) this.height / (this.height + totalScrollRange);
            scrollbarThumbHeight = Math.max(20, (int) (scrollbarHeight * visibleRatio));
        } else {
            scrollbarThumbHeight = scrollbarHeight;
        }

        // Calculate thumb position
        int scrollableArea = scrollbarHeight - scrollbarThumbHeight;
        if (totalScrollRange > 0) {
            float scrollRatio = (float) (scrollY - minScroll) / totalScrollRange;
            scrollbarThumbY = scrollbarY + (int) (scrollableArea * scrollRatio);
        } else {
            scrollbarThumbY = scrollbarY;
        }
    }

    private void renderScrollbar(DrawContext context) {
        if (maxScroll <= minScroll) return; // No need for scrollbar if no scrolling

        // Draw scrollbar track
        context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, 0x66000000);

        // Draw scrollbar thumb
        int thumbColor = isDraggingScrollbar ? 0xAAFFFFFF : 0x88AAAAAA;
        context.fill(scrollbarX, scrollbarThumbY, scrollbarX + scrollbarWidth, scrollbarThumbY + scrollbarThumbHeight, thumbColor);
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        return mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
               mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeight;
    }

    private boolean isMouseOverScrollbarThumb(double mouseX, double mouseY) {
        return mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
               mouseY >= scrollbarThumbY && mouseY <= scrollbarThumbY + scrollbarThumbHeight;
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        return; // do not render background
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        // Render search results count
        if (!currentSearchQuery.isEmpty()) {
            String resultText = widgets.size() + " results";
            context.drawText(this.textRenderer, resultText, padding + 210, padding + 5, 0xAAAAAA, false);
        }

        int y = 50 - scrollY; // Start lower to account for search bar
        for (FeatureCardWidget widget : widgets) {
            boolean visible = y + widget.height > 0 && y < this.height;
            if (widget.toggleButton != null) widget.toggleButton.visible = visible;
            if (widget.positionButton != null) widget.positionButton.visible = visible;

            if (visible) {
                widget.updateWidgetPositions(padding, y);
                widget.renderCard(context, padding, y);
            }
            y += widget.height;
        }
        super.render(context, mouseX, mouseY, deltaTicks);
        exitButton.render(context, mouseX, mouseY, deltaTicks);
        renderScrollbar(context);
    }

    public boolean isDifferent(FeatureCardWidget widget){
        try{
            return !widget.inputField.getText().equals(ModConfig.configs.get(widget.name.getString()).value);
        }
        catch (Exception e){
            return true;
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (exitButton.mouseClicked(mouseX, mouseY, button)) return true;

        // Handle scrollbar interaction
        if (isMouseOverScrollbar(mouseX, mouseY)) {
            if (button == 0) { // Left click
                isDraggingScrollbar = true;
                // Store the offset between mouse position and thumb top
                dragStartOffset = (int) (mouseY - scrollbarThumbY);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDraggingScrollbar = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollY -= (int) (verticalAmount * 20);
        scrollY = Math.max(minScroll, Math.min(scrollY, maxScroll));
        updateScrollbarDimensions(); // Update scroll bar position when scrolling with mouse
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDraggingScrollbar && button == 0) {
            // Calculate new scroll position using the drag offset
            int scrollableHeight = maxScroll - minScroll;
            if (scrollableHeight > 0) {
                // Use the initial drag offset to maintain smooth dragging
                int adjustedMouseY = (int) (mouseY - dragStartOffset);
                int thumbAreaY = adjustedMouseY - scrollbarY;
                int scrollableArea = scrollbarHeight - scrollbarThumbHeight;

                float thumbRatio = (float) thumbAreaY / scrollableArea;
                int newScrollY = minScroll + (int) (thumbRatio * scrollableHeight);
                scrollY = Math.max(minScroll, Math.min(newScrollY, maxScroll));
                updateScrollbarDimensions(); // Update thumb position
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public class FeatureCardWidget {
        public int width, height, padding;
        private String type;
        private final Text name;
        private final java.util.List<OrderedText> wrappedLines;
        private final ButtonWidget toggleButton;
        private final ButtonWidget positionButton;
        private final TextFieldWidget inputField;

        public FeatureCardWidget(ModConfig.Config config, int padding) {
            int screenWidth = SettingsGUI.this.width;
            this.name = config.name;
            this.type = config.type;
            this.padding = padding;
            width = screenWidth - 2 * padding;

            wrappedLines = SettingsGUI.this.textRenderer.wrapLines(
                    config.description, width / 2 - padding
            );

            height = wrappedLines.size() * SettingsGUI.this.textRenderer.fontHeight + 2 * padding + 20;
//            if (height < 50 + 2 * padding && "hudToggle".equals(type)) height = 60 + 2 * padding; // no need for this right now
            if (height < 20 + 2 * padding) height = 20 + 2 * padding;

            if ("toggle".equals(type)) {
                this.toggleButton = ButtonWidget.builder(
                        Text.literal(config.on ? "ON" : "OFF"),
                        btn -> {
                            config.on = !config.on;
                            btn.setMessage(Text.literal(config.on ? "ON" : "OFF"));
                            ModConfig.save();
                        }
                ).position(0, 0).size(25, 25).build();
                this.inputField = null;
                this.positionButton = null;
            } else if ("hudToggle".equals(type)) {
//                this.inputField = new TextFieldWidget(
//                        SettingsGUI.this.textRenderer,
//                        0, 0, width/2, 20,
//                        Text.literal("Enter value")
//                );
//                this.inputField.setText(config.value != null ? config.value : "");
//                this.toggleButton = ButtonWidget.builder(
//                        Text.literal("Update"),
//                        btn -> {
//                            config.value = inputField.getText();
//                            ModConfig.save();
//                        }
//                ).position(0, 0).size(60, 20).build();
                this.toggleButton = ButtonWidget.builder(
                        Text.literal(config.on ? "ON" : "OFF"),
                        btn -> {
                            config.on = !config.on;
                            btn.setMessage(Text.literal(config.on ? "ON" : "OFF"));
                            ModConfig.save();
                        }
                ).position(0, 0).size(25, 25).build();

                this.positionButton = ButtonWidget.builder(
                        Text.literal("✎"),
                        btn -> {
                            if (client != null) client.setScreen(new PositionScreen(this.name.getString(), scrollY));
                        }
                ).position(0, 0).size(25, 25).build();

                this.inputField = null;
            } else {
                this.toggleButton = null;
                this.inputField = null;
                this.positionButton = null;
            }
        }

        public void updateWidgetPositions(int x, int y) {
            if ("toggle".equals(type) && toggleButton != null) {
                toggleButton.setX(x + padding + width - 50);
                toggleButton.setY(y + padding);
            }
            if ("hudToggle".equals(type) && positionButton != null && toggleButton != null) {
//                inputField.setX(x + (width * 3) / 8 + 20);
//                inputField.setY(y + padding);
//                toggleButton.setX(x + padding + width - 85);
//                toggleButton.setY(y + padding + 25);
                toggleButton.setX(x + padding + width - 50);
                toggleButton.setY(y + padding);
                positionButton.setX(x + padding + width - 80);
                positionButton.setY(y + padding);
            }
        }

        public void renderCard(DrawContext context, int x, int y) {
            context.fill(x, y, x + width, y + height, 0x99222222);
            context.drawText(SettingsGUI.this.textRenderer, name, x + padding, y + padding, 0xFFFFFF, false);

            int yOffset = 0;
            for (OrderedText line : wrappedLines) {
                context.drawText(SettingsGUI.this.textRenderer, line, x + padding, y + 20 + yOffset, 0xAAAAAA, false);
                yOffset += SettingsGUI.this.textRenderer.fontHeight;
            }
        }
    }

    /**
     * Helper class to store widgets with their search scores
     */
    private static class ScoredWidget {
        final FeatureCardWidget widget;
        final int score;

        ScoredWidget(FeatureCardWidget widget, int score) {
            this.widget = widget;
            this.score = score;
        }
    }
}
