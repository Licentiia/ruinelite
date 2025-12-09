package net.runelite.client.plugins.ruinelite.utils.npcdebug;

import com.google.inject.Inject;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.NpcDespawned;

import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.config.ConfigManager;

import java.util.HashSet;
import java.util.Set;

@Slf4j
@PluginDescriptor(
		name = "NPC Animation Debugger"
)
public class NPCAnimationDebugPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NPCAnimationDebugOverlay overlay;

	@Inject
	private NPCAnimationDebugConfig config;

	@Getter
	private final Set<NPC> trackedNPCs = new HashSet<>();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		log.info("NPC Animation Debugger started");
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		trackedNPCs.clear();
		log.info("NPC Animation Debugger stopped");
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		trackedNPCs.add(event.getNpc());
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		trackedNPCs.remove(event.getNpc());
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		System.out.println("AnimationChanged event fired");

		if (!(event.getActor() instanceof NPC))
			return;

		NPC npc = (NPC) event.getActor();

		// Apply filter (string parsing)
		if (!npcPassesFilter(npc.getId()))
			return;

		int animId = npc.getAnimation();

		log.info("NPC {} (ID {}) → Animation {}", npc.getName(), npc.getId(), animId);

		if (config.showChat())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",
					"NPC " + npc.getId() + " animation: " + animId, null);
		}
	}

	private boolean npcPassesFilter(int npcId)
	{
		String filter = config.npcFilter().trim();

		// Empty string = allow all NPCs
		if (filter.isEmpty())
			return true;

		String[] ids = filter.split(",");

		for (String id : ids)
		{
			try
			{
				if (npcId == Integer.parseInt(id.trim()))
					return true;
			}
			catch (NumberFormatException ignored)
			{
			}
		}

		return false;
	}

	@Provides
	NPCAnimationDebugConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NPCAnimationDebugConfig.class);
	}
}
