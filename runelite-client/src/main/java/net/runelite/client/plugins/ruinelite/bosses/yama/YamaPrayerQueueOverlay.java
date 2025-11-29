package net.runelite.client.plugins.ruinelite.bosses.yama;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.SpriteID;

import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class YamaPrayerQueueOverlay extends OverlayPanel
{
	private final Client client;
	private final YamaPrayerPlugin plugin;
	private final YamaPrayerConfig config;
	private final SpriteManager spriteManager;

	private BufferedImage iconMage;
	private BufferedImage iconRange;

	@Inject
	public YamaPrayerQueueOverlay(
			Client client,
			YamaPrayerPlugin plugin,
			YamaPrayerConfig config,
			SpriteManager spriteManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.spriteManager = spriteManager;

		setPosition(OverlayPosition.TOP_LEFT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);

		panelComponent.setBackgroundColor(new Color(0, 0, 0, 160));
		panelComponent.setPreferredSize(new Dimension(150, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.enabled() || !config.showQueuePanel())
			return null;

		panelComponent.getChildren().clear();
		ensureIcons();

		panelComponent.getChildren().add(
				TitleComponent.builder()
						.text("YAMA PRAYER")
						.color(Color.WHITE)
						.build()
		);

		NPC yama = plugin.getYama();
		YamaPrayerType type = plugin.getCurrentAttack();

		if (yama == null || type == YamaPrayerType.UNKNOWN)
		{
			panelComponent.getChildren().add(
					LineComponent.builder()
							.left("Waiting...")
							.leftColor(Color.GRAY)
							.build()
			);
			return super.render(graphics);
		}

		BufferedImage icon = type == YamaPrayerType.MAGIC ? iconMage : iconRange;
		if (icon != null)
		{
			panelComponent.getChildren().add(new ImageComponent(icon));
		}

		panelComponent.getChildren().add(
				LineComponent.builder()
						.left(type.label.toUpperCase())
						.leftColor(type.color)
						.build()
		);

		return super.render(graphics);
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
