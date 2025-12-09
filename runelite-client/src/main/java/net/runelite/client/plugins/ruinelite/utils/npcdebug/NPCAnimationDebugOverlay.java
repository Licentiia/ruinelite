package net.runelite.client.plugins.ruinelite.utils.npcdebug;

import javax.inject.Inject;

import net.runelite.api.NPC;
import net.runelite.api.Point;

import net.runelite.client.ui.overlay.*;

import java.awt.*;

public class NPCAnimationDebugOverlay extends Overlay
{
	private final NPCAnimationDebugPlugin plugin;
	private final NPCAnimationDebugConfig config;

	@Inject
	public NPCAnimationDebugOverlay(NPCAnimationDebugPlugin plugin, NPCAnimationDebugConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!config.showOverlay())
			return null;

		for (NPC npc : plugin.getTrackedNPCs())
		{
			if (!npcFilterPass(npc.getId()))
				continue;

			String text = String.valueOf(npc.getAnimation());

			Point point = npc.getCanvasTextLocation(g, text, npc.getLogicalHeight() + 40);

			if (point == null)
				continue;

			OverlayUtil.renderTextLocation(g, point, text, config.textColor());
		}

		return null;
	}

	private boolean npcFilterPass(int npcId)
	{
		String filter = config.npcFilter().trim();
		if (filter.isEmpty())
			return true;

		for (String idStr : filter.split(","))
		{
			try
			{
				if (npcId == Integer.parseInt(idStr.trim()))
					return true;
			}
			catch (Exception ignored)
			{
			}
		}

		return false;
	}
}
