package net.runelite.client.plugins.ruinelite.utils.projectiledetector;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Perspective;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ProjectileDetectorOverlay extends Overlay
{
	private final Client client;
	private final ProjectileDetectorPlugin plugin;
	private final ProjectileDetectorConfig config;
	private final SpriteManager spriteManager;

	private BufferedImage iconMage;
	private BufferedImage iconRange;
	private BufferedImage iconMelee;

	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!config.enabled())
		{
			return null;
		}

		Font base = FontManager.getRunescapeBoldFont();
		if (config.enableFontSizeOverride())
		{
			base = base.deriveFont((float) config.fontSize());
		}
		g.setFont(base);
		FontMetrics fm = g.getFontMetrics();

		ensureIconsLoaded();

		for (Projectile proj : client.getProjectiles())
		{
			if (proj == null)
			{
				continue;
			}

			if (config.onlyIfTargetingLocal() && proj.getInteracting() != client.getLocalPlayer())
			{
				continue;
			}

			ProjectileRule rule = plugin.ruleFor(proj);
			if (rule == null)
			{
				continue;
			}

			int projX = (int) proj.getX();
			int projY = (int) proj.getY();
			LocalPoint lp = new LocalPoint(projX, projY);

			int z = (int) proj.getZ();
			Point canvas = Perspective.localToCanvas(client, lp, client.getPlane(), z + config.verticalOffset());
			if (canvas == null)
			{
				continue;
			}

			int x = canvas.getX();
			int y = canvas.getY();

			if (config.showIcon())
			{
				BufferedImage icon =
						rule.type == PrayerType.MAGIC ? iconMage :
								rule.type == PrayerType.RANGED ? iconRange :
										rule.type == PrayerType.MELEE ? iconMelee :
												null;


				if (icon != null)
				{
					int iw = icon.getWidth();
					int ih = icon.getHeight();
					g.drawImage(icon, x - iw / 2, y - ih - 2, null);
					y -= ih + 4;
				}
			}

			if (config.showLabel() && rule.label != null && !rule.label.isEmpty())
			{
				drawFlatBoxLabel(g, fm, rule.label, x, y, rule.type.color);
			}
		}

		return null;
	}

	private void ensureIconsLoaded()
	{
		if (iconMage == null)
		{
			iconMage = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
		}
		if (iconRange == null)
		{
			iconRange = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
		}
		if (iconMelee == null)
		{
			iconMelee = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0);
		}
	}

	private void drawFlatBoxLabel(Graphics2D g, FontMetrics fm, String text, int centerX, int baselineY, Color textColor)
	{
		int padding = 4;
		int textW = fm.stringWidth(text);
		int textH = fm.getHeight();

		int boxW = textW + padding * 2;
		int boxH = textH;

		int x = centerX - (boxW / 2);
		int y = baselineY - boxH;

		g.setColor(new Color(0, 0, 0, 160));
		g.fillRect(x, y, boxW, boxH);

		g.setColor(Color.WHITE);
		g.drawRect(x, y, boxW, boxH);

		g.setColor(textColor);
		g.drawString(text, x + padding, y + fm.getAscent());
	}
}