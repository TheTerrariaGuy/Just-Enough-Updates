package jeu;

import jeu.oopShits.Feature;
import jeu.screens.ModConfig;
import jeu.terralib.APIUtils;
import jeu.terralib.HologramUtils;
import jeu.terralib.HudManager;
import jeu.terralib.TabList;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.HashMap;

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
//		Configurator.setLevel("Minecraft", Level.ERROR);
//		System.out.println("[JEU] Disabled goofy yggdrasil (and others :p) warnings, ur welcome");

		HudManager.init();
		ModCommands.init();
		HologramUtils.init();
		TabList.init();

//		if(ModConfig.configs.isEmpty() || ModConfig.configs.keySet().size() < ModConfig.features.length) ModConfig.resetToDefault();

		HashMap<String, Class<? extends Feature>> feats = ModConfig.featureClasses;
		for (String key : feats.keySet()) {
			try {
				Class<? extends Feature> c = feats.get(key);
				Feature instance = (Feature) c.getMethod("getInstance").invoke(null);
				instance.initCall();
			}
			catch (Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}

	}
	private void initUUID(){
		APIUtils.getUUID(USERNAME).thenAccept(uuid -> {
			UUID = uuid;
			System.out.println("UUID: " + UUID);
			deving();
		}).exceptionally(e -> {
			System.err.println("Failed to get UUID: " + e.getMessage());
			return null;
		});
	}

	private void deving(){
		if(UUID.equals("731139daa37b409f92e2e339268918ed")) // 731139daa37b409f92e2e339268918ed
			DevShits.init();
	}

	public static void refreshFeatures(){
		// disable all first
		HashMap<String, Class<? extends Feature>> feats = ModConfig.featureClasses;
		for (String key : feats.keySet()) {
			try {
				Class<? extends Feature> c = feats.get(key);
				Feature instance = (Feature) c.getMethod("getInstance").invoke(null);
				instance.off();
			}
			catch (Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}

		loadFeatures();
	}
	public static void loadFeatures(){
		ModConfig.load();
		if(ModConfig.configs.isEmpty() || ModConfig.configs.keySet().size() < ModConfig.features.length) ModConfig.resetToDefault();
		//  enable
		HashMap<String, Class<? extends Feature>> feats = ModConfig.featureClasses;
		for (String key : feats.keySet()) {
			if(!ModConfig.configs.get(key).on) continue;
			try {
				Class<? extends Feature> c = feats.get(key);
				Feature instance = (Feature) c.getMethod("getInstance").invoke(null);
				instance.on();
			}
			catch (Exception e){
				System.out.println("error: " + e.getMessage());
			}
		}

	}
}