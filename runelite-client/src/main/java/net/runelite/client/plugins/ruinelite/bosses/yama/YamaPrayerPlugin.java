package net.runelite.client.plugins.ruinelite.bosses.yama;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sound.sampled.*;

import java.io.File;

import net.runelite.api.*;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.NpcDespawned;

import net.runelite.client.callback.ClientThread;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
		name = PluginDescriptor.Zebe + " Yama Pray",
		description = "Shows prayer to use based on Yama spotanimations."
)
public class YamaPrayerPlugin extends Plugin
{
	@Inject private Client client;
	@Inject private ClientThread clientThread;
	@Inject private OverlayManager overlayManager;

	@Inject private YamaPrayerConfig config;
	@Inject private ConfigManager configManager;

	@Inject private YamaPrayerOverlay overlay;
	@Inject private YamaPrayerQueueOverlay queueOverlay;

	@Getter private NPC yama;
	@Getter private YamaPrayerType currentAttack = YamaPrayerType.UNKNOWN;

	private YamaPrayerType lastAttack = YamaPrayerType.UNKNOWN;

	@Provides
	YamaPrayerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(YamaPrayerConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);

		if (config.showQueuePanel())
			overlayManager.add(queueOverlay);

		yama = null;
		currentAttack = YamaPrayerType.UNKNOWN;
		lastAttack = YamaPrayerType.UNKNOWN;
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(queueOverlay);
	}

	// NPC Tracking
	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();

		if (npc.getName() != null && npc.getName().toLowerCase().contains("yama"))
		{
			yama = npc;
			currentAttack = YamaPrayerType.UNKNOWN;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		NPC npc = event.getNpc();

		if (npc.getName() != null && npc.getName().toLowerCase().contains("yama"))
		{
			yama = null;
			currentAttack = YamaPrayerType.UNKNOWN;
		}
	}

	// Detect Yama attack animation
	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{
		if (!(event.getActor() instanceof NPC))
			return;

		NPC npc = (NPC) event.getActor();
		if (npc.getName() == null || !npc.getName().toLowerCase().contains("yama"))
			return;

		yama = npc;

		int gfx = npc.getGraphic();
		YamaPrayerType newType = currentAttack;

		if (gfx == 3246) newType = YamaPrayerType.MAGIC;
		else if (gfx == 3243) newType = YamaPrayerType.RANGED;
		else return;

		if (newType != currentAttack)
		{
			currentAttack = newType;
			playConfiguredSound();
		}
	}

	// Config handling for checkbox
	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals(YamaPrayerConfig.GROUP))
			return;

		if (event.getKey().equals("testSoundNow") && config.testSoundNow())
		{
			playConfiguredSound();

			// Reset checkbox
			configManager.setConfiguration(YamaPrayerConfig.GROUP, "testSoundNow", false);
		}
	}

	// Sound Logic
	private void playConfiguredSound()
	{
		if (config.enableCustomSound() && !config.customSoundPath().isEmpty())
		{
			playCustomSound(config.customSoundPath(), config.customSoundVolume());
			return;
		}

		if (config.enableBuiltInSound())
		{
			playBuiltInSound(config.builtInSoundId());
		}
	}

	// Built-in OSRS sound engine
	private void playBuiltInSound(String idString)
	{
		int id;

		try
		{
			id = Integer.parseInt(idString.trim());
		}
		catch (Exception ex)
		{
			return;
		}

		clientThread.invoke(() ->
				client.playSoundEffect(id, 0) // volume ignored by OSRS
		);
	}

	// Custom audio file
	private void playCustomSound(String path, int volumePercent)
	{
		new Thread(() ->
		{
			try
			{
				File f = new File(path);
				if (!f.exists()) return;

				AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
				Clip clip = AudioSystem.getClip();
				clip.open(audioIn);

				// Apply volume
				FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float range = gain.getMaximum() - gain.getMinimum();
				float gainValue = (range * (volumePercent / 100f)) + gain.getMinimum();
				gain.setValue(gainValue);

				clip.start();
			}
			catch (Exception e)
			{
				log.warn("Failed to play custom sound: {}", path);
			}
		}).start();
	}
}
