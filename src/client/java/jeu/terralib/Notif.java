package jeu.terralib;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;

// HOG RIDERRRRRR

public class Notif {
    /*
        ===== Notification System =====
        very simple notification system that queues notifications:
        - title: main title
        - description: secondary text
        - icon: item icon
        - time: how long the notification should be displayed (in seconds)
     */

    private Notif() {} // no instantiation
    private static final float defaultTime = 3f; // default time
    private static final Deque<CustomToast> queue;
    private static boolean active = false;

    static {
        queue = new ArrayDeque<>();
    }
    // shut up IntelliJ, this is a library (its telling me that this method is not used :skull:)
    public static void queueNotif(String title, String description) {
        MinecraftClient.getInstance().getToastManager().add(new CustomToast(
                Text.literal(title),
                Text.literal(description),
                Items.PAPER,
                defaultTime
        ));
    }

    public static void queueNotif(String title, String description, Item item) {
        MinecraftClient.getInstance().getToastManager().add(new CustomToast(
                Text.literal(title),
                Text.literal(description),
                item,
                defaultTime
        ));
    }

    public static void queueNotif(String title, String description, Item item, double time) {
        MinecraftClient.getInstance().getToastManager().add(new CustomToast(
                Text.literal(title),
                Text.literal(description),
                item,
                time
        ));
    }

    private static class CustomToast implements Toast {
//        private static final Identifier TEXTURE = Identifier.of("just-enough-updates", "textures/gui/toast.png");
        private static final Identifier TEXTURE = Identifier.ofVanilla("toast/advancement");
        private final Text title;
        private final Text description;
        private final Item icon;
        private Visibility visibility = Visibility.SHOW;
        private final long displayTime; // default display time in milliseconds

        // TODO: add sound support
        public CustomToast(Text title, Text description, Item icon, double displayTime) {
            this.title = title;
            this.description = description;
            this.icon = icon;
            this.displayTime = (long) (displayTime * 1000); // adjust from seconds to milliseconds
        }


        // TODO: add inputs for width and height and positions
        @Override
        public void draw(DrawContext context, TextRenderer textRenderer, long startTime) {
            // Draw background

            context.drawGuiTexture(
                    RenderLayer::getGuiTextured, // Use the correct RenderLayer for GUI textures
                    TEXTURE,                // your Identifier
                    0, 0,                   // x, y on screen
                    getWidth(), getHeight(),// width, height to draw
                    0xFFFFFFFF              // color (ARGB)
            );

            context.drawItem(new ItemStack(icon), 8, 8);

            context.drawText(textRenderer, title, 30, 7, 0xFFFFFF, false);
            context.drawText(textRenderer, description, 30, 18, 0x888888, false);
        }

        @Override
        public Object getType() {
            return this;
        }

        @Override
        public int getWidth() {
            return Toast.BASE_WIDTH;
        }

        @Override
        public int getHeight() {
            return Toast.BASE_HEIGHT;
        }

        @Override
        public int getRequiredSpaceCount() {
            return 1;
        }

        @Override
        public Visibility getVisibility() {
            return visibility;
        }

        @Override
        public void update(ToastManager manager, long time) {
            if (time >= displayTime) {
                visibility = Visibility.HIDE;
            }
        }
        // TODO: add sound support
        @Override
        @Nullable
        public SoundEvent getSoundEvent() {
            // Return null for no sound, or a custom sound if desired
            return null;
        }

    }
}


