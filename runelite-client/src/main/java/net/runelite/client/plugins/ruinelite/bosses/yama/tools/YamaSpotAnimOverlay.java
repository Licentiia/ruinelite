package net.runelite.client.plugins.ruinelite.bosses.yama.tools;

import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.*;

import java.awt.*;

public class YamaSpotAnimOverlay extends Overlay
{
	private final YamaSpotAnimPlugin plugin;

	@Inject
	private Client client;

	@Inject
	public YamaSpotAnimOverlay(YamaSpotAnimPlugin plugin)
	{
		this.plugin = plugin;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		// DRAW YAMA GFX
		NPC yama = plugin.getYamaNpc();

		if (yama != null && !yama.isDead())
		{
			int gfx = plugin.getYamaSpotAnim();

			if (gfx != -1)
			{
				String text = "Yama GFX: " + gfx;

				Point loc = yama.getCanvasTextLocation(
						g, text, yama.getLogicalHeight() + 40);

				if (loc != null)
				{
					OverlayUtil.renderTextLocation(g, loc, text, Color.RED);
				}
			}
		}

		// DRAW PLAYER GFX
		Player player = client.getLocalPlayer();

		if (player != null)
		{
			int gfx = plugin.getPlayerSpotAnim();

			if (gfx != -1)
			{
				String text = "My GFX: " + gfx;

				Point loc = player.getCanvasTextLocation(
						g, text, player.getLogicalHeight() + 50);

				if (loc != null)
				{
					OverlayUtil.renderTextLocation(g, loc, text, Color.CYAN);
				}
			}
		}

		return null;
	}
}
