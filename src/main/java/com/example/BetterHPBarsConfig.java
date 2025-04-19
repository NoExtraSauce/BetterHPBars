package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.*;

@ConfigGroup("betterhpbars")
public interface BetterHPBarsConfig extends Config
{
	@Range(
			min = -400,
			max = 200
	)
	@ConfigItem(
			keyName = "verticalOffset",
			name = "Vertical Offset",
			description = "Move the HP display up/down"
	)
	default int verticalOffset()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "nameColor",
			name = "Enemy Name Color",
			description = "Change the color of the opponent's name"
	)
	default Color nameColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
			keyName = "hpColor",
			name = "HP Text Color",
			description = "Change the color of HP count text"
	)
	default Color hpColor()
	{
		return Color.WHITE;
	}

	@ConfigItem(
			keyName = "boldText",
			name = "Bold Text",
			description = "Render font in bold"
	)
	default boolean boldText()
	{
		return false;
	}

	@Range(
			min = 8,
			max = 24
	)
	@ConfigItem(
			keyName = "hpFontSize",
			name = "HP Font Size",
			description = "Font size of Opponent's HP count"
	)
	default int hpFontSize()
	{
		return 11;
	}

	@Range(
			min = 8,
			max = 24
	)
	@ConfigItem(
			keyName = "nameFontSize",
			name = "Name Font Size",
			description = "Font size of the opponent's name"
	)
	default int nameFontSize()
	{
		return 11;
	}


}
