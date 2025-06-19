package jeu;

import jeu.terralib.APIUtils;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class JustEnoughUpdatesClient implements ClientModInitializer {
	public static String USERNAME, UUID;
	private static KeyBinding myKeyBinding;


	@Override
	public void onInitializeClient() {
		// Intialization of variables
		System.out.println("[JEU] client loaded ...");
		USERNAME = MinecraftClient.getInstance().getSession().getUsername();
		System.out.println("Username: " + USERNAME);
		initUUID();

		// init features
		PartyCommands.init();

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
}