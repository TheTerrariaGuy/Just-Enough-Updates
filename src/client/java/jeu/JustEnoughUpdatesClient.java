package jeu;

import jeu.features.*;
import jeu.screens.ModConfig;
import jeu.terralib.APIUtils;
import jeu.terralib.HologramUtils;
import jeu.terralib.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

import java.util.HashMap;
import java.util.Map;

public class JustEnoughUpdatesClient implements ClientModInitializer {
	public static String USERNAME, UUID;
	private static KeyBinding myKeyBinding;
//	public static Map<String, Boolean> mixinEnabled;

	/*
		 Feature loading:
		 	on()
		 	init()

		 Feature refresh:
		 	off()
		 	on()

	 */


	@Override
	public void onInitializeClient() {
		// Intialization of variables
		// TODO: refactor to handle new feature system
		System.out.println("[JEU] client loaded ...");
		USERNAME = MinecraftClient.getInstance().getSession().getUsername();
		System.out.println("Username: " + USERNAME);
		initUUID();
		loadFeatures();

		HudManager.init();
		ModCommands.init();
		HologramUtils.init();

		if(ModConfig.configs.isEmpty() || ModConfig.configs.keySet().size() < ModConfig.features.length) ModConfig.resetToDefault();
		HashMap<String, ModConfig.Config> confs = (HashMap<String, ModConfig.Config>) ModConfig.configs;

		for (String key : confs.keySet()) {
			if("number".equals(confs.get(key).type)) continue;
			try {
				ModConfig.featureClasses.get(key).getMethod("init").invoke(null); // wattasigm
			}
			catch (Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}

//		GlowingMushroomDetector.on();
//		GlowingMushroomDetector.init();
//		PetInfoHUD.init();
//		PestCooldownHUD.init();
//		PartyCommands.init();
//		TreeProgressHUD.init();


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
		if(ModConfig.configs.isEmpty() || ModConfig.configs.keySet().size() < ModConfig.features.length) ModConfig.resetToDefault();
		HashMap<String, ModConfig.Config> confs = (HashMap<String, ModConfig.Config>) ModConfig.configs;

		for (String key : confs.keySet()) {
			if("number".equals(confs.get(key).type)) continue;
			try {
				ModConfig.featureClasses.get(key).getMethod("off").invoke(null); // wattasigm
			}
			catch (Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}

//		PartyCommands.off();
//		PetInfoHUD.off();
//		PestCooldownHUD.off();
//		TreeProgressHUD.off();

		loadFeatures();

	}
	public static void loadFeatures(){
		ModConfig.load();
		if(ModConfig.configs.isEmpty() || ModConfig.configs.keySet().size() < ModConfig.features.length) ModConfig.resetToDefault();
		HashMap<String, ModConfig.Config> confs = (HashMap<String, ModConfig.Config>) ModConfig.configs;
//		mixinEnabled = new HashMap<>();
//		mixinEnabled.put("ChatHudMixin", confs.get("Chat Copy").on);
//		mixinEnabled.put("PartyFinderStatsMixin", confs.get("Dungeon Party Finder Stats").on);

		//  enable

		/*
			configs must parallel to featureClasses
		 */
		for (String key : confs.keySet()) {
			if("number".equals(confs.get(key).type)) continue;
			try {
				if(confs.get(key).on) ModConfig.featureClasses.get(key).getMethod("on").invoke(null); // wattasigm
			}
			catch (Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}
//		if(confs.get("Party Commands").on) PartyCommands.on();
//		if(confs.get("Pet HUD").on) PetInfoHUD.on();
//		if(confs.get("Pest HUD").on) PestCooldownHUD.on();
//		if(confs.get("Tree Progress").on) TreeProgressHUD.on();

	}
}