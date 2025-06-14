package jeu;

import jeu.terralib.CommandUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class JustEnoughUpdatesClient implements ClientModInitializer {
	public static String USERNAME;
	private static KeyBinding myKeyBinding;
	@Override
	public void onInitializeClient() {
		System.out.println("client loaded ... ...");
		USERNAME = MinecraftClient.getInstance().getSession().getUsername();
		System.out.println("Username: " + USERNAME);
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		PartyCommands.init();


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
}