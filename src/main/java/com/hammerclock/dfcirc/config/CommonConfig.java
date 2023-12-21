// Source code is decompiled from a .class file using FernFlower decompiler.
package com.hammerclock.dfcirc.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.hammerclock.dfcirc.types.BotMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CommonConfig {
   public static final Path CONFIG_PATH = Paths.get("config", "dfcirc-common.toml");
   public static final CommonConfig INSTANCE;
   public static final ForgeConfigSpec CONFIG;
   private ForgeConfigSpec.EnumValue<BotMode> botMode;
   private ForgeConfigSpec.ConfigValue<String> botToken;
   private ForgeConfigSpec.LongValue serverId;
   private ForgeConfigSpec.LongValue channelId;
   private ForgeConfigSpec.LongValue messageId;
   private ForgeConfigSpec.BooleanValue showStatus;
   private ForgeConfigSpec.BooleanValue useEmojis;
   private ForgeConfigSpec.LongValue goldBoxEmojiId;
   private ForgeConfigSpec.LongValue ironBoxEmojiId;
   private ForgeConfigSpec.LongValue woodenBoxEmojiId;
   private ForgeConfigSpec.ConfigValue<String> embedColor;
   private ForgeConfigSpec.ConfigValue<String> embedTitle;
   private ForgeConfigSpec.ConfigValue<String> embedFooter;
   private ForgeConfigSpec.BooleanValue embedShowLastUpdated;
   private ForgeConfigSpec.BooleanValue embedSortByTier;

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
      builder.push("General");
      this.botMode = builder.comment("In which mode the Bot is supposed to work.").defineEnum("Circulation Bot Mode",
            BotMode.ONLY_SHOW_AVAILABLE);

      this.botToken = builder.comment("Your discord bot token here.").define("Discord Bot Token", "");

      this.serverId = builder.comment("Server ID in which the Devil Fruit Circulation Bot should work in")
            .defineInRange("Server ID", 100000000000000000L, 0L, Long.MAX_VALUE);

      this.channelId = builder.comment(
            "Channel ID to where the Circulation Messages will be send. Please make sure the bot has write access!")
            .defineInRange("Channel ID", 1000000000000000000L, 0L, Long.MAX_VALUE);

      this.messageId = builder.comment("DO NOT TOUCH. WILL BE GENERATED").defineInRange("Message ID", 0L, 0L,
            Long.MAX_VALUE);

      this.showStatus = builder.comment(
            "Show the current Status of the Fruit as well if it has one\nAvailable Status: LOST, IN_USE, INVENTORY, DROPPED\nDefault: False")
            .define("Show Fruit Status", false);

      builder.push("Emojis");
      this.useEmojis = builder.comment("Use emojis to show the rarity of a fruit").define("Use Emojis", false);

      this.goldBoxEmojiId = builder.comment("Discord Emoji ID to represent a Gold Box.")
            .defineInRange("Gold Box Emoji ID", 1000000000000000000L, 0L, Long.MAX_VALUE);

      this.ironBoxEmojiId = builder.comment("Discord Emoji ID to represent an Iron Box.")
            .defineInRange("Iron Box Emoji ID", 1000000000000000000L, 0L, Long.MAX_VALUE);

      this.woodenBoxEmojiId = builder.comment("Discord Emoji ID to represent a Wooden Box.")
            .defineInRange("Wooden Box Emoji ID", 1000000000000000000L, 0L, Long.MAX_VALUE);
      builder.pop();

      builder.push("EmbedDesign");
      this.embedColor = builder.comment("Color for the generated Embed in Hexadecimal").define("Color Hex", "0xFFD700");

      this.embedTitle = builder.comment("The title of the Embed").define("Embed Title",
            "Current Devilfruit Circulation");

      this.embedFooter = builder.comment("The footer of the Embed").define("Embed Footer",
            "Made by DerHammerclock | Last updated");

      this.embedShowLastUpdated = builder.comment("Show a date next to the footer when the embed has been updated")
            .define("Show Last Updated", true);

      this.embedSortByTier = builder.comment("Sort Devil Fruits by their Tier.").define("Sort By Tier", true);

      builder.pop();

      builder.pop();
   }

   public BotMode getBotMode() {
      return this.botMode.get();
   }

   public String getBotToken() {
      return this.botToken.get();
   }

   public Long getChannelId() {
      return this.channelId.get();
   }

   public Boolean getUseEmojis() {
      return this.useEmojis.get();
   }

   public Long getGoldBoxEmojiId() {
      return this.goldBoxEmojiId.get();
   }

   public Long getIronBoxEmojiId() {
      return this.ironBoxEmojiId.get();
   }

   public Long getWoodenBoxEmojiId() {
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

   public Boolean getEmbedShowLastUpdated() {
      return this.embedShowLastUpdated.get();
   }

   public Boolean getShowStatus() {
      return this.showStatus.get();
   }

   public Long getGuildId() {
      return this.serverId.get();
   }

   public Boolean getEmbedSortByTier() {
      return this.embedSortByTier.get();
   }

   public Long getMessageId() {
      return this.messageId.get();
   }

   public void setMessageId(Long messageId) {
      this.messageId.set(messageId);
      this.messageId.save();
   }
}
