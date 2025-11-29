package net.runelite.client.plugins.ruinelite.bosses.yama;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;

public class YamaPrayerOverlay extends Overlay
{
	private final Client client;
	private final YamaPrayerPlugin plugin;
	private final YamaPrayerConfig config;
	private final SpriteManager spriteManager;

	private BufferedImage iconMage;
	private BufferedImage iconRange;

	@Inject
	public YamaPrayerOverlay(
			Client client,
			YamaPrayerPlugin plugin,
			YamaPrayerConfig config,
			SpriteManager spriteManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.spriteManager = spriteManager;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!config.enabled())
			return null;

		NPC yama = plugin.getYama();
		if (yama == null)
			return null;

		YamaPrayerType attack = plugin.getCurrentAttack();
		if (attack == YamaPrayerType.UNKNOWN)
			return null;

		ensureIcons();

		String text = attack.label;
		int offset = config.verticalOffset();

		Point loc = yama.getCanvasTextLocation(
				g, text, yama.getLogicalHeight() + 40 + offset);

		if (loc == null)
			return null;

		g.setFont(FontManager.getRunescapeBoldFont().deriveFont((float) config.fontSize()));

		if (config.showIcon())
		{
			BufferedImage icon = attack == YamaPrayerType.MAGIC ? iconMage : iconRange;
			if (icon != null)
			{
				g.drawImage(icon,
						loc.getX() - icon.getWidth() / 2,
						loc.getY() - icon.getHeight() - 2,
						null);
			}
		}

		if (config.showLabel())
		{
			OverlayUtil.renderTextLocation(g, loc, text, attack.color);
		}

		return null;
	}

	private void ensureIcons()
	{
		if (iconMage == null)
		{
			iconMage = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
		}
		if (iconRange == null)
		{
			iconRange = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
		}
	}
}
