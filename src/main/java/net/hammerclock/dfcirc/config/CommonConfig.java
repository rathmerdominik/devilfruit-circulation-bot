// Source code is decompiled from a .class file using FernFlower decompiler.
package net.hammerclock.dfcirc.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraftforge.common.ForgeConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
	public static final Path CONFIG_PATH = Paths.get("config", "dfcirc-common.toml");
	public static final CommonConfig INSTANCE;
	public static final ForgeConfigSpec CONFIG;

	// General config values
	private ForgeConfigSpec.BooleanValue showUnavailableEmbed;
	private ForgeConfigSpec.BooleanValue showAvailableEmbed;
	private ForgeConfigSpec.BooleanValue showStatus;
	private ForgeConfigSpec.BooleanValue showPlayerNameAsStatus;

	// Emoji config values
	private ForgeConfigSpec.BooleanValue useEmojis;
	private ForgeConfigSpec.LongValue goldBoxEmojiId;
	private ForgeConfigSpec.LongValue ironBoxEmojiId;
	private ForgeConfigSpec.LongValue woodenBoxEmojiId;

	// Available Embed config values
	private ForgeConfigSpec.LongValue availableChannelId;
	private ForgeConfigSpec.ConfigValue<String> availableEmbedColor;
	private ForgeConfigSpec.ConfigValue<String> availableEmbedFooter;
	private ForgeConfigSpec.BooleanValue availableEmbedShowLastUpdated;
	private ForgeConfigSpec.BooleanValue availableEmbedSortByTier;
	private ForgeConfigSpec.BooleanValue availableEmbedSortByAlphabet;
	private ForgeConfigSpec.LongValue availableMessageId;

	// Unavailable Embed config values
	private ForgeConfigSpec.LongValue unavailableChannelId;
	private ForgeConfigSpec.ConfigValue<String> unavailableEmbedColor;
	private ForgeConfigSpec.ConfigValue<String> unavailableEmbedFooter;
	private ForgeConfigSpec.BooleanValue unavailableEmbedShowLastUpdated;
	private ForgeConfigSpec.BooleanValue unavailableEmbedSortByTier;
	private ForgeConfigSpec.BooleanValue unavailableEmbedSortByAlphabet;
	private ForgeConfigSpec.LongValue unavailableMessageId;

	static {
		Pair<CommonConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

		CONFIG = pair.getRight();
		INSTANCE = pair.getLeft();

		CommentedFileConfig file = CommentedFileConfig.builder(CONFIG_PATH).sync().autoreload()
			.writingMode(WritingMode.REPLACE).build();

		file.load();
		file.save();

		CONFIG.setConfig(file);
	}

	public CommonConfig(ForgeConfigSpec.Builder builder) {
		
		// General config

		builder.push("General");

		this.showAvailableEmbed = builder.comment("Show the Available Fruits Embed").define("Show Available Embed", true);

		this.showUnavailableEmbed = builder.comment("Show the Unavailable Fruits Embed").define("Show Unavailable Embed", true);
	
		this.showStatus = builder.comment(
			"Show the current Status of the Fruit as well if it has one")
			.define("Show Fruit Status", false);

		this.showPlayerNameAsStatus = builder.comment(
			"Show the player name if the fruit status is INVENTORY or IN_USE. Requires Show Fruit Status to be true")
			.define("Show Player Name as Status", false);

		builder.pop();

		// Emoji config

		builder.push("Emojis");

		this.useEmojis = builder.comment("Use emojis to show the rarity of a fruit").define("Use Emojis", false);

		this.goldBoxEmojiId = builder.comment("Discord Emoji ID to represent a Gold Box.")
			.defineInRange("Gold Box Emoji ID", 0L, 0L, Long.MAX_VALUE);

		this.ironBoxEmojiId = builder.comment("Discord Emoji ID to represent an Iron Box.")
			.defineInRange("Iron Box Emoji ID", 0L, 0L, Long.MAX_VALUE);

		this.woodenBoxEmojiId = builder.comment("Discord Emoji ID to represent a Wooden Box.")
			.defineInRange("Wooden Box Emoji ID", 0L, 0L, Long.MAX_VALUE);
		
		builder.pop();

		// Available Embed config
		
		builder.push("Available Embed Design");

		this.availableChannelId = builder.comment(
			"Channel ID to where the Available Fruits Embed Message will be sent to. Please make sure the bot has write access!")
			.defineInRange("Channel ID", 0L, 0L, Long.MAX_VALUE);

		this.availableEmbedColor = builder.comment("Color for the generated Embed in Hexadecimal").define("Color Hex", "0xFFD700");

		this.availableEmbedFooter = builder.comment("The footer of the Embed").define("Embed Footer",
			"Made by DerHammerclock | Last updated");

		this.availableEmbedShowLastUpdated = builder.comment("Show a date next to the footer when the embed has been updated")
			.define("Show Last Updated", true);

		this.availableEmbedSortByTier = builder.comment("Sort Devil Fruits by their Tier").define("Sort By Tier", true);

		this.availableEmbedSortByAlphabet = builder.comment("Sort Devil Fruits by Alphabet").define("Sort by Alphabet", false);

		this.availableMessageId = builder.comment("DO NOT TOUCH. WILL BE GENERATED").defineInRange("Available Message ID", 0L, 0L, Long.MAX_VALUE);
		
		builder.pop();

		// Unavailable Embed config

		builder.push("Unavailable Embed Design");

		this.unavailableChannelId = builder.comment(
			"Channel ID to where the Unavailable Fruits Embed Message will be sent to. Please make sure the bot has write access!")
			.defineInRange("Channel ID", 0L, 0L, Long.MAX_VALUE);

		this.unavailableEmbedColor = builder.comment("Color for the generated Embed in Hexadecimal").define("Color Hex", "0xFFD700");

		this.unavailableEmbedFooter = builder.comment("The footer of the Embed").define("Embed Footer",
			"Made by DerHammerclock | Last updated");

		this.unavailableEmbedShowLastUpdated = builder.comment("Show a date next to the footer when the embed has been updated")
			.define("Show Last Updated", true);

		this.unavailableEmbedSortByTier = builder.comment("Sort Devil Fruits by their Tier").define("Sort By Tier", true);

		this.unavailableEmbedSortByAlphabet = builder.comment("Sort Devil Fruits by Alphabet").define("Sort by Alphabet", false);
		
		this.unavailableMessageId = builder.comment("DO NOT TOUCH. WILL BE GENERATED").defineInRange("Unavailable Message ID", 0L, 0L,Long.MAX_VALUE);

		builder.pop();
	}

	// General settings


	public boolean showAvailableEmbed() {
		return showAvailableEmbed.get();
	}

	public boolean showUnavailableEmbed() {
		return showUnavailableEmbed.get();
	}

	public boolean showStatus() {
		return this.showStatus.get();
	}

	public boolean showPlayerNameAsStatus() {
		return showPlayerNameAsStatus.get();
	}

	public boolean getShowStatus() {
		return showStatus.get();
	}

	public boolean getShowPlayerNameAsStatus() {
		return showPlayerNameAsStatus.get();
	}

	// Emoji config

	public boolean useEmojis() {
		return this.useEmojis.get();
	}

	public long getGoldBoxEmojiId() {
		return this.goldBoxEmojiId.get();
	}

	public long getIronBoxEmojiId() {
		return this.ironBoxEmojiId.get();
	}

	public long getWoodenBoxEmojiId() {
		return this.woodenBoxEmojiId.get();
	}

	// Available Embed Config 

	public long getAvailableChannelId() {
		return availableChannelId.get();
	}

	public long getAvailableMessageId() {
		return this.availableMessageId.get();
	}

	public void setAvailableMessageId(long messageId) {
		this.availableMessageId.set(messageId);
		this.availableMessageId.save();
	}

	public String getAvailableEmbedColor() {
		return availableEmbedColor.get();
	}

	public String getAvailableEmbedFooter() {
		return availableEmbedFooter.get();
	}

	public boolean availableEmbedShowLastUpdated() {
		return availableEmbedShowLastUpdated.get();
	}

	public boolean availableEmbedSortByTier() {
		return availableEmbedSortByTier.get();
	}

	public boolean availableEmbedSortByAlphabet() {
		return availableEmbedSortByAlphabet.get();
	}

	// Unavailable Embed Config

	public long getUnavailableChannelId() {
		return unavailableChannelId.get();
	}

	public long getUnavailableMessageId() {
		return this.unavailableMessageId.get();
	}

	public void setUnavailableMessageId(long messageId) {
		this.unavailableMessageId.set(messageId);
		this.unavailableMessageId.save();
	}

	public String getUnavailableEmbedColor() {
		return unavailableEmbedColor.get();
	}

	public String getUnavailableEmbedFooter() {
		return unavailableEmbedFooter.get();
	}

	public boolean unavailableEmbedShowLastUpdated() {
		return unavailableEmbedShowLastUpdated.get();
	}

	public boolean unavailableEmbedSortByTier() {
		return unavailableEmbedSortByTier.get();
	}

	public boolean unavailableEmbedSortByAlphabet() {
		return unavailableEmbedSortByAlphabet.get();
	}
}
