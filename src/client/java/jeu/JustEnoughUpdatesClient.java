package jeu;

import jeu.terralib.APIUtils;
import jeu.terralib.CommandUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
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

		PartyCommands.init();

		//TODO: API key handling, make backend for ts


		// testing, remove later
		myKeyBinding = new KeyBinding(
				"key.jeu.my_command", // translation key
				GLFW.GLFW_KEY_G,      // default key (G)
				"category.jeu"        // category
		);
		net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(myKeyBinding);

		// Listen for key press
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (myKeyBinding.wasPressed()) {
				// Run your command here
				System.out.println("G key pressed! Running command...");
				CommandUtils.send("/pc [Just Enough Updates] test message");
				// Example: PartyCommands.runMyCommand();
			}
		});

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