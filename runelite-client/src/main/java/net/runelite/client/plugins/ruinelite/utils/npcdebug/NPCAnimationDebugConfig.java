package net.runelite.client.plugins.ruinelite.utils.npcdebug;

import net.runelite.client.config.*;

@ConfigGroup("npcanimationdebug")
public interface NPCAnimationDebugConfig extends Config
{
	@ConfigItem(
			keyName = "showOverlay",
			name = "Show Overlay",
			description = "Display animation IDs above NPCs"
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
			keyName = "npcFilter",
			name = "NPC ID Filter",
			description = "NPC IDs to track. Example: 7668 or 7668,9463. Leave empty for all NPCs."
	)
	default String npcFilter()
	{
		return "";
	}

	@ConfigItem(
			keyName = "showChat",
			name = "Show Chat Messages",
			description = "Print animation IDs in chat when they change"
	)
	default boolean showChat()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "textColor",
			name = "Overlay Text Color",
			description = "Color of the animation ID text"
	)
	default java.awt.Color textColor()
	{
		return java.awt.Color.YELLOW;
	}
}
