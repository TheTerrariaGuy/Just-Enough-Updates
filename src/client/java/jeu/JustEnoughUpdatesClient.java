package jeu;

import jeu.screens.ModConfig;
import jeu.terralib.APIUtils;
import jeu.terralib.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;
import java.util.Map;

public class JustEnoughUpdatesClient implements ClientModInitializer {
	public static String USERNAME, UUID;
	private static KeyBinding myKeyBinding;
	public static Map<String, Boolean> mixinEnabled;

	// TODO: refactor later, wow such spaghetti code
	@Override
	public void onInitializeClient() {
		// Intialization of variables
		System.out.println("[JEU] client loaded ...");
		USERNAME = MinecraftClient.getInstance().getSession().getUsername();
		System.out.println("Username: " + USERNAME);
		initUUID();

		HudManager.init();
		ModCommands.init();
//		PetInfoHUD.init();
		PartyCommands.init();
		loadFeatures();
	}
	private void initUUID(){
		APIUtils.getUUID(USERNAME).thenAccept(uuid -> {
			UUID = uuid;
			System.out.println("UUID: " + UUID);
		}).exceptionally(e -> {
			System.err.println("Failed to get UUID: " + e.getMessage());
			return null;
		});
	}
	public static void refreshFeatures(){
		// disable all first
		PartyCommands.dinit();
//		PetInfoHUD.dinit();
		loadFeatures();
	}
	public static void loadFeatures(){
		ModConfig.load();
		if(ModConfig.configs.isEmpty() || ModConfig.configs.keySet().size() < ModConfig.features.length) ModConfig.resetToDefault();
		HashMap<String, ModConfig.Config> confs = (HashMap<String, ModConfig.Config>) ModConfig.configs;
		mixinEnabled = new HashMap<>();
		mixinEnabled.put("ChatHudMixin", confs.get("Chat Copy").on);
		mixinEnabled.put("PartyFinderStatsMixin", confs.get("Dungeon Party Finder Stats").on);

		//  enable
		if(confs.get("Party Commands").on) PartyCommands.on();
//		if(confs.get("Pet HUD").on) PetInfoHUD.on();

	}
}