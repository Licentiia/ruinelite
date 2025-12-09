package net.runelite.client.plugins.ruinelite.bosses.yama.tools.spot;

import com.google.inject.Inject;
import com.google.inject.Provides;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.NpcDespawned;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import lombok.Getter;

@PluginDescriptor(
        name = PluginDescriptor.Zebe + " Yama Spotanim",
        description = "Displays spotanim IDs on Yama and prints them to chat"
)
public class YamaSpotanimDebuggerPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private YamaSpotanimDebuggerOverlay overlay;

    @Getter private NPC yama;

    @Provides
    YamaSpotanimDebuggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(YamaSpotanimDebuggerConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        yama = null;
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        NPC npc = event.getNpc();

        if (npc.getName() != null && npc.getName().toLowerCase().contains("yama"))
        {
            yama = npc;
            client.addChatMessage(
                    net.runelite.api.ChatMessageType.GAMEMESSAGE,
                    "",
                    "[YAMA DEBUG] Yama detected (ID: " + npc.getId() + ")",
                    null
            );
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        if (yama == null)
            return;

        if (event.getNpc() == yama)
        {
            client.addChatMessage(
                    net.runelite.api.ChatMessageType.GAMEMESSAGE,
                    "",
                    "[YAMA DEBUG] Yama despawned.",
                    null
            );

            yama = null;
        }
    }

    @Subscribe
    public void onGraphicChanged(GraphicChanged event)
    {
        if (!(event.getActor() instanceof NPC))
            return;

        NPC npc = (NPC) event.getActor();

        if (npc != yama)
            return;

        int gfx = npc.getGraphic();

        if (gfx <= 0)
            return;

        // Print to chat
        client.addChatMessage(
                net.runelite.api.ChatMessageType.GAMEMESSAGE,
                "",
                "[YAMA DEBUG] Spotanim detected on Yama: " + gfx,
                null
        );

        // Overlay will draw the gfx ID visually
        overlay.setLatestGfx(gfx);
    }
}
