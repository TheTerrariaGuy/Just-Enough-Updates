package jeu;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;

public class JustEnoughUpdatesClient implements ClientModInitializer {
	public static String USERNAME;
	@Override
	public void onInitializeClient() {
		System.out.println("client loaded ... ...");
		USERNAME = MinecraftClient.getInstance().getSession().getUsername();
		System.out.println("Username: " + USERNAME);
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		PartyCommands.init();

	}
}