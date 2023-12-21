// Source code is decompiled from a .class file using FernFlower decompiler.
package net.hammerclock.dfcirc.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.hammerclock.dfcirc.types.BotMode;

import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraftforge.common.ForgeConfigSpec;

import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
	public static final Path CONFIG_PATH = Paths.get("config", "dfcirc-common.toml");
	public static final CommonConfig INSTANCE;
	public static final ForgeConfigSpec CONFIG;

	// General config values
	private ForgeConfigSpec.EnumValue<BotMode> botMode;
	private ForgeConfigSpec.LongValue channelId;
	private ForgeConfigSpec.LongValue messageId;
	private ForgeConfigSpec.BooleanValue showStatus;
	private ForgeConfigSpec.BooleanValue showPlayerNameAsStatus;

	// Emoji config values
	private ForgeConfigSpec.BooleanValue useEmojis;
	private ForgeConfigSpec.LongValue goldBoxEmojiId;
	private ForgeConfigSpec.LongValue ironBoxEmojiId;
	private ForgeConfigSpec.LongValue woodenBoxEmojiId;

	// Embed config values
	private ForgeConfigSpec.ConfigValue<String> embedColor;
	private ForgeConfigSpec.ConfigValue<String> embedTitle;
	private ForgeConfigSpec.ConfigValue<String> embedFooter;
	private ForgeConfigSpec.BooleanValue embedShowLastUpdated;
	private ForgeConfigSpec.BooleanValue embedSortByTier;
	private ForgeConfigSpec.BooleanValue embedSortByAlphabet;

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
		this.botMode = builder.comment("In which mode the Bot is supposed to work.").defineEnum("Circulation Bot Mode",
			BotMode.ONLY_SHOW_AVAILABLE);

		this.channelId = builder.comment(
			"Channel ID to where the Circulation Messages will be send. Please make sure the bot has write access!")
			.defineInRange("Channel ID", 0L, 0L, Long.MAX_VALUE);

		this.messageId = builder.comment("DO NOT TOUCH. WILL BE GENERATED").defineInRange("Message ID", 0L, 0L,
			Long.MAX_VALUE);

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

		// Embed config
		
		builder.push("EmbedDesign");
		this.embedColor = builder.comment("Color for the generated Embed in Hexadecimal").define("Color Hex", "0xFFD700");

		this.embedTitle = builder.comment("The title of the Embed").define("Embed Title",
			"Current Devilfruit Circulation");

		this.embedFooter = builder.comment("The footer of the Embed").define("Embed Footer",
			"Made by DerHammerclock | Last updated");

		this.embedShowLastUpdated = builder.comment("Show a date next to the footer when the embed has been updated")
			.define("Show Last Updated", true);

		this.embedSortByTier = builder.comment("Sort Devil Fruits by their Tier").define("Sort By Tier", true);

		this.embedSortByAlphabet = builder.comment("Sort Devil Fruits by Alphabet").define("Sort by Alphabet", false);
	
		builder.pop();
	}

	public BotMode getBotMode() {
		return this.botMode.get();
	}

	public long getChannelId() {
		return this.channelId.get();
	}

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

	public String getEmbedColor() {
		return this.embedColor.get();
	}

	public String getEmbedTitle() {
		return this.embedTitle.get();
	}

	public String getEmbedFooter() {
		return this.embedFooter.get();
	}

	public boolean embedShowLastUpdated() {
		return this.embedShowLastUpdated.get();
	}

	public boolean showStatus() {
		return this.showStatus.get();
	}

	public boolean showPlayerNameAsStatus() {
		return showPlayerNameAsStatus.get();
	}

	public boolean embedSortByTier() {
		return this.embedSortByTier.get();
	}

	public boolean embedSortByAlphabet() {
		return this.embedSortByAlphabet.get();
	}

	public long getMessageId() {
		return this.messageId.get();
	}

	public void setMessageId(long messageId) {
		this.messageId.set(messageId);
		this.messageId.save();
	}
}
