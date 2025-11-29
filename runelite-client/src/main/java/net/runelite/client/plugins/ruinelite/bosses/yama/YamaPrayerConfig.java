package net.runelite.client.plugins.ruinelite.bosses.yama;

import net.runelite.client.config.*;

@ConfigGroup(YamaPrayerConfig.GROUP)
public interface YamaPrayerConfig extends Config
{
	String GROUP = "YamaPrayerHelper";

	@ConfigItem(
			keyName = "enabled",
			name = "Enable plugin",
			description = "Master enable/disable"
	)
	default boolean enabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showIcon",
			name = "Show prayer icon",
			description = "Display protect icon above Yama"
	)
	default boolean showIcon()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showLabel",
			name = "Show text label",
			description = "Show Mage / Range label"
	)
	default boolean showLabel()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showQueuePanel",
			name = "Show queue panel",
			description = "Display the prayer queue panel on the side"
	)
	default boolean showQueuePanel()
	{
		return true;
	}

	@ConfigItem(
			keyName = "fontSize",
			name = "Font size",
			description = "Text size for label above Yama"
	)
	@Range(min = 10, max = 32)
	default int fontSize()
	{
		return 16;
	}

	@ConfigItem(
			keyName = "verticalOffset",
			name = "Vertical offset",
			description = "Adjust vertical position of text/icon"
	)
	@Range(min = -50, max = 50)
	default int verticalOffset()
	{
		return 0;
	}

	// CUSTOM SOUND SYSTEM
	@ConfigItem(
			keyName = "enableCustomSound",
			name = "Enable custom sound",
			description = "Use external WAV/MP3 files instead of OSRS sounds"
	)
	default boolean enableCustomSound()
	{
		return false;
	}

	@ConfigItem(
			keyName = "customSoundPath",
			name = "Custom sound file",
			description = "Full path to sound file (example: C:/sounds/yama.wav)"
	)
	default String customSoundPath()
	{
		return "";
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
			keyName = "customSoundVolume",
			name = "Custom sound volume",
			description = "Volume for custom sound playback (0–100%)"
	)
	default int customSoundVolume()
	{
		return 70;
	}

	// BUILT-IN OSRS SOUND (fallback)
	@ConfigItem(
			keyName = "enableBuiltInSound",
			name = "Enable built-in OSRS sound",
			description = "Use OSRS sound effect IDs instead of custom audio files"
	)
	default boolean enableBuiltInSound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "builtInSoundId",
			name = "OSRS Sound ID",
			description = "Sound effect ID (example: 3800)"
	)
	default String builtInSoundId()
	{
		return "3800";
	}

	// TEST SOUND CHECKBOX
	@ConfigItem(
			keyName = "testSoundNow",
			name = "Test sound now",
			description = "Tick to immediately play configured sound"
	)
	default boolean testSoundNow()
	{
		return false;
	}
}
