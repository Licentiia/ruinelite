package net.runelite.client.plugins.ruinelite.bosses.yama.tools.spot;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;

public class YamaSpotanimDebuggerOverlay extends Overlay
{
    private final Client client;
    private final YamaSpotanimDebuggerPlugin plugin;

    private int latestGfxId = -1;

    @Inject
    public YamaSpotanimDebuggerOverlay(Client client, YamaSpotanimDebuggerPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;

        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    public void setLatestGfx(int id)
    {
        latestGfxId = id;
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if (plugin.getYama() == null || latestGfxId == -1)
            return null;

        LocalPoint lp = plugin.getYama().getLocalLocation();
        if (lp == null)
            return null;

        // Runelite API point (net.runelite.api.Point)
        net.runelite.api.Point rlPoint = Perspective.getCanvasTextLocation(
                client,
                g,
                lp,
                "GFX: " + latestGfxId,
                40
        );

        if (rlPoint == null)
            return null;

        // FIX: Use rlPoint.x and rlPoint.y (ints)
        g.setColor(Color.YELLOW);
        g.drawString("GFX: " + latestGfxId, rlPoint.getX(), rlPoint.getY());

        return null;
    }
}

