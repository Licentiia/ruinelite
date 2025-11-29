package net.runelite.client.plugins.ruinelite.bosses.yama;

import java.awt.*;

public enum YamaPrayerType
{
	MAGIC("Mage", new Color(0x3498DB)),     // blue
	RANGED("Range", new Color(0x27AE60)),   // green
	UNKNOWN("?", Color.WHITE);

	public final String label;
	public final Color color;

	YamaPrayerType(String label, Color color)
	{
		this.label = label;
		this.color = color;
	}
}
