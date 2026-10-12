package com.autortp;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class AutoRtpClient implements ClientModInitializer {
	private static boolean running = false;
	private static String target = "";
	private static int ticksSinceRtp = 0;
	private static int delayTicks = 100; // 5 seconds between /rtp attempts
	private static int attempts = 0;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(AutoRtpClient::tick);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, buildContext) -> {
			dispatcher.register(ClientCommands.literal("autortp")
				.then(ClientCommands.literal("start")
					.then(ClientCommands.argument("biome", StringArgumentType.word())
						.executes(context -> {
							String biome = StringArgumentType.getString(context, "biome");
							start(biome);
							return 1;
						})))
				.then(ClientCommands.literal("stop")
					.executes(context -> {
						if (running) {
							running = false;
							message("AutoRTP stopped after " + attempts + " attempt(s).");
						} else {
							message("AutoRTP is not running.");
						}
						return 1;
					}))
				.then(ClientCommands.literal("delay")
					.then(ClientCommands.argument("seconds", IntegerArgumentType.integer(2, 60))
						.executes(context -> {
							int seconds = IntegerArgumentType.getInteger(context, "seconds");
							delayTicks = seconds * 20;
							message("Delay between /rtp attempts set to " + seconds + "s.");
							return 1;
						})))
				.then(ClientCommands.literal("status")
					.executes(context -> {
						if (running) {
							message("Running. Looking for: " + target + " | attempts: " + attempts
								+ " | delay: " + (delayTicks / 20) + "s");
						} else {
							message("Not running. Use /autortp start <biome>");
						}
						return 1;
					})));
		});
	}

	private static void start(String biome) {
		String cleaned = biome.toLowerCase().replace("minecraft:", "");
		target = cleaned;
		attempts = 0;
		ticksSinceRtp = delayTicks; // send the first /rtp right away
		running = true;
		message("AutoRTP started. Looking for biome: " + cleaned + ". Stop with /autortp stop");
	}

	private static void tick(Minecraft client) {
		if (!running) {
			return;
		}
		if (client.player == null || client.level == null) {
			running = false;
			return;
		}

		// Registered name looks like "minecraft:desert" (or "modname:biome").
		String current = client.level.getBiome(client.player.blockPosition()).getRegisteredName();
		String path = current.contains(":") ? current.substring(current.indexOf(':') + 1) : current;

		if (path.equals(target)) {
			running = false;
			message("Found " + current + " after " + attempts + " attempt(s). AutoRTP stopped.");
			return;
		}

		ticksSinceRtp++;
		if (ticksSinceRtp >= delayTicks) {
			ticksSinceRtp = 0;
			attempts++;
			client.player.connection.sendCommand("rtp");
		}
	}

	private static void message(String text) {
		Minecraft client = Minecraft.getInstance();
		if (client.player != null) {
			client.player.displayClientMessage(Component.literal("[AutoRTP] " + text), false);
		}
	}
}
