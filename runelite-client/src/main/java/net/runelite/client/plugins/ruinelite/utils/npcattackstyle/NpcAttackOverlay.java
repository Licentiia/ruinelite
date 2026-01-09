package net.runelite.client.plugins.ruinelite.utils.npcattackstyle;

import java.awt.*;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NpcAttackOverlay extends Overlay
{
    private final Client client;
    private final NpcAttackPlugin plugin;
    private final NpcAttackConfig config;

    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        Font base = FontManager.getRunescapeBoldFont();
        if (config.enableFontSizeOverride())
        {
            base = base.deriveFont((float) config.fontSize());
        }
        g.setFont(base);
        FontMetrics fm = g.getFontMetrics();

        for (NPC npc : client.getNpcs())
        {
            if (npc == null || npc.isDead())
                continue;

            final String label = plugin.labelFor(npc);
            if (label == null || label.isEmpty())
                continue;

            LocalPoint lp = npc.getLocalLocation();
            if (lp == null)
                continue;

            int z = npc.getLogicalHeight();
            Point loc = Perspective.localToCanvas(client, lp, client.getPlane(), z + config.verticalOffset());
            if (loc == null)
                continue;

            drawFlatBoxLabel(g, fm, label, loc, plugin.colorForLabel(label));
        }

        return null;
    }

    private void drawFlatBoxLabel(Graphics2D g, FontMetrics fm, String text, Point loc, Color textColor)
    {
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int padding = 4;
        int boxWidth = textWidth + padding * 2;
        int boxHeight = textHeight;

        int x = loc.getX() - (boxWidth / 2);
        int y = loc.getY() - boxHeight;
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(x, y, boxWidth, boxHeight);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, boxWidth, boxHeight);

        g.setColor(textColor);
        g.drawString(text, x + padding, y + fm.getAscent());
    }
}
