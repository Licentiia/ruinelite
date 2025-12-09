package net.runelite.client.plugins.ruinelite.bosses.yama.tools;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.GraphicChanged;

import net.runelite.api.events.NpcDespawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
		name = PluginDescriptor.Zebe + " Yama SpotAnim Debug"
)
public class YamaSpotAnimPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private YamaSpotAnimOverlay overlay;

	@Getter
	private NPC yamaNpc;

	@Getter
	private int yamaSpotAnim = -1;

	@Getter
	private int playerSpotAnim = -1;

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		yamaNpc = null;
		yamaSpotAnim = -1;
		playerSpotAnim = -1;
		log.info("Yama SpotAnim Debugger started");
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		yamaNpc = null;
		log.info("Yama SpotAnim Debugger stopped");
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{
		Actor actor = event.getActor();

		int gfx = actor.getGraphic();

		if (gfx == -1)
			return;

		// Detect YOUR spotanim
		if (actor == client.getLocalPlayer())
		{
			playerSpotAnim = gfx;
			return;
		}

		// Detect YAMA spotanim
		if (actor instanceof NPC)
		{
			NPC npc = (NPC) actor;

			if (npc.getName() != null && npc.getName().toLowerCase().contains("yama"))
			{
				yamaNpc = npc;
				yamaSpotAnim = gfx;
			}
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (yamaNpc == event.getNpc())
		{
			yamaNpc = null;
			yamaSpotAnim = -1;
		}
	}

}
