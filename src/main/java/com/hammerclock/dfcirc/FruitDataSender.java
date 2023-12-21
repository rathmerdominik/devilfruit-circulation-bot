package com.hammerclock.dfcirc;

import com.hammerclock.dfcirc.config.CommonConfig;
import com.hammerclock.dfcirc.types.BotMode;
import com.hammerclock.dfcirc.types.FruitData;
import com.hammerclock.dfcirc.types.TierBox;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.pixelatedw.mineminenomi.api.OneFruitEntry;
import xyz.pixelatedw.mineminenomi.api.OneFruitEntry.Status;
import xyz.pixelatedw.mineminenomi.data.world.ExtendedWorldData;
import xyz.pixelatedw.mineminenomi.init.ModValues;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

public class FruitDataSender implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private AtomicBoolean running = new AtomicBoolean(false);
   private AtomicBoolean stopped = new AtomicBoolean(true);
   private Thread worker;
   EnumSet<GatewayIntent> intents;

   public FruitDataSender() {
      this.intents = EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.MESSAGE_CONTENT);
   }

   public void start() {
      this.worker = new Thread(this);
      this.worker.start();
   }

   public void stop() {
      this.running.set(false);
   }

   public void interrupt() {
      this.running.set(false);
      this.worker.interrupt();
   }

   boolean isRunning() {
      return this.running.get();
   }

   boolean isStopped() {
      return this.stopped.get();
   }

   public boolean areFruitDataSame(HashMap<String, FruitData> oldFruitData, HashMap<String, FruitData> newFruitData) {
      if (!oldFruitData.isEmpty() && !newFruitData.isEmpty()) {

         for (Map.Entry<String, FruitData> newFruit : newFruitData.entrySet()) {
            String newFruitKey = (String) newFruit.getKey();

            String oldFruitStatus;
            try {
               oldFruitStatus = oldFruitData.get(newFruitKey).devilFruitStatus.get().name().toString();
            } catch (NoSuchElementException e) {
               oldFruitStatus = "";
            }

            String newFruitStatus;
            try {
               newFruitStatus = newFruit.getValue().devilFruitStatus.get().name().toString();
            } catch (NoSuchElementException e) {
               newFruitStatus = "";
            }

            if (oldFruitStatus != newFruitStatus) {
               return false;
            }
         }
         return true;
      } else {
         return false;
      }
   }

   public HashMap<String, FruitData> getFruitData() {
      HashMap<String, FruitData> fruitDataMap = new HashMap<String, FruitData>();
      ExtendedWorldData extendedWorldData = ExtendedWorldData.get();

      if (extendedWorldData != null) {
         for (AkumaNoMiItem fruit : ModValues.devilfruits) {
            OneFruitEntry entry = extendedWorldData.getOneFruitEntry(fruit.getFruitKey());
            OneFruitEntry.Status status;

            if (entry == null) {
               status = null;
            } else {
               status = entry.getStatus();
            }

            FruitData fruitData = new FruitData(fruit.getDevilFruitName(), fruit.getFruitKey(), status,
                  TierBox.values()[fruit.getTier() - 1]);
            fruitDataMap.put(fruit.getFruitKey(), fruitData);
         }
      }

      return fruitDataMap;
   }

   public String formatWithDecoration(JDA jda, FruitData fruitEntry) {
      String formattedString = "";

      if (CommonConfig.INSTANCE.getUseEmojis()) {
         try {
            String goldBoxEmoji = jda.getEmojiById(CommonConfig.INSTANCE.getGoldBoxEmojiId()).getAsMention();
            String ironBoxEmoji = jda.getEmojiById(CommonConfig.INSTANCE.getIronBoxEmojiId()).getAsMention();
            String woodenBoxEmoji = jda.getEmojiById(CommonConfig.INSTANCE.getWoodenBoxEmojiId()).getAsMention();

            switch (fruitEntry.devilFruitTier) {
               case GOLD:
                  formattedString = String.format("%s%s", goldBoxEmoji, fruitEntry.devilFruitName);
                  break;
               case IRON:
                  formattedString = String.format("%s%s", ironBoxEmoji, fruitEntry.devilFruitName);
                  break;
               case WOODEN:
                  formattedString = String.format("%s%s", woodenBoxEmoji, fruitEntry.devilFruitName);
                  break;
               default:
                  LOGGER.error(
                        "THIS SHOULD NOT HAVE HAPPENED. Something is wrong with the Emoji function. Please report to DerHammerclock!");
            }
         } catch (NullPointerException e) {
            LOGGER.error("One of the Emojis has an invalid ID! " + e.getMessage());
         }
      }

      if (CommonConfig.INSTANCE.getShowStatus()) {
         formattedString = String.format("%s\n__Status:__ %s", formattedString,
               fruitEntry.devilFruitStatus.isPresent() ? fruitEntry.devilFruitStatus.get().name() : "Free");
      }

      return formattedString;
   }

   public ArrayList<FruitData> sortFruits(HashMap<String, FruitData> fruitData) {
      ArrayList<FruitData> goldBoxFruitData = new ArrayList<FruitData>();
      ArrayList<FruitData> ironBoxFruitData = new ArrayList<FruitData>();
      ArrayList<FruitData> woodenBoxFruitData = new ArrayList<FruitData>();

      for (FruitData tierFruitData : fruitData.values()) {
         switch (tierFruitData.devilFruitTier) {
            case GOLD:
               goldBoxFruitData.add(tierFruitData);
               break;
            case IRON:
               ironBoxFruitData.add(tierFruitData);
               break;
            case WOODEN:
               woodenBoxFruitData.add(tierFruitData);
         }
      }

      // yeah yeah. Semantics...
      goldBoxFruitData.addAll(ironBoxFruitData);
      goldBoxFruitData.addAll(woodenBoxFruitData);

      return goldBoxFruitData;

   }

   public EmbedBuilder buildEmbedShowAvailable(JDA jda, EmbedBuilder eb, HashMap<String, FruitData> fruitData) {
      eb.addField("Available Devil Fruits", "", false);

      ArrayList<FruitData> sortedFruitData = this.sortFruits(fruitData);

      List<String> batchFruit = new ArrayList<String>();

      for (int i = 0; i < sortedFruitData.size(); i++) {
         FruitData fruitEntry = sortedFruitData.get(i);

         if (!fruitEntry.devilFruitStatus.isPresent() || fruitEntry.devilFruitStatus.get() == Status.LOST) {
            if (batchFruit.size() == 5 || i == sortedFruitData.size() - 1) {
               eb.addField("", String.join("\n", batchFruit), true);
               batchFruit.clear();
            }

            batchFruit.add(this.formatWithDecoration(jda, fruitEntry));
         }
      }

      return eb;
   }

   public EmbedBuilder buildEmbedShowUnavailable(JDA jda, EmbedBuilder eb, HashMap<String, FruitData> fruitData) {
      eb.addField("Unavailable Devil Fruits", "", false);

      ArrayList<FruitData> sortedFruitData = this.sortFruits(fruitData);

      List<String> batchFruit = new ArrayList<String>();

      for (int i = 0; i < sortedFruitData.size(); i++) {
         FruitData fruitEntry = sortedFruitData.get(i);
         if (fruitEntry.devilFruitStatus.isPresent() && fruitEntry.devilFruitStatus.get() != Status.LOST) {
            if (batchFruit.size() == 5 || i == sortedFruitData.size() - 1) {
               eb.addField("", String.join("\n", batchFruit), true);
               batchFruit.clear();
            }

            batchFruit.add(this.formatWithDecoration(jda, fruitEntry));
         }
      }

      return eb;
   }

   public EmbedBuilder buildEmbedShowBoth(JDA jda, EmbedBuilder eb, HashMap<String, FruitData> fruitData) {
      EmbedBuilder available = this.buildEmbedShowAvailable(jda, eb, fruitData);
      EmbedBuilder unavailable = this.buildEmbedShowUnavailable(jda, available, fruitData);

      return unavailable;
   }

   public EmbedBuilder buildEmbed(JDA jda, BotMode botMode, HashMap<String, FruitData> fruitData) {
      EmbedBuilder eb = new EmbedBuilder();

      eb.setTitle(CommonConfig.INSTANCE.getEmbedTitle());
      eb.setFooter(CommonConfig.INSTANCE.getEmbedFooter());
      eb.setColor(Color.decode(CommonConfig.INSTANCE.getEmbedColor()));

      if (CommonConfig.INSTANCE.getEmbedShowLastUpdated()) {
         eb.setTimestamp(OffsetDateTime.now());
      }

      switch (botMode) {
         case ONLY_SHOW_AVAILABLE:
            eb = this.buildEmbedShowAvailable(jda, eb, fruitData);
            break;
         case ONLY_SHOW_UNAVAILABLE:
            eb = this.buildEmbedShowUnavailable(jda, eb, fruitData);
            break;
         case SHOW_AVAILABLE_AND_UNAVAILABLE:
            eb = this.buildEmbedShowBoth(jda, eb, fruitData);
      }

      return eb;
   }

   public void run() {
      this.running.set(true);
      this.stopped.set(false);
      HashMap<String, FruitData> oldFruitData = new HashMap<String, FruitData>();

      while (this.running.get()) {
         HashMap<String, FruitData> fruitData = this.getFruitData();
         if (!this.areFruitDataSame(oldFruitData, fruitData)) {
            try {
               JDA jda = JDABuilder
                     .createDefault(CommonConfig.INSTANCE.getBotToken(), this.intents)
                     .setActivity(Activity.watching("Devil Fruit Circulation"))
                     .setStatus(OnlineStatus.ONLINE)
                     .build();
               jda.awaitReady();

               Guild guild = jda.getGuildById(CommonConfig.INSTANCE.getGuildId());
               if (guild == null) {
                  LOGGER.error("NO SERVER COULD BE FOUND WITH THE GIVEN ID! PLEASE FIX IN CONFIG!");
                  break;
               }

               TextChannel channel = guild.getTextChannelById(CommonConfig.INSTANCE.getChannelId());
               if (channel == null) {
                  LOGGER.error("NO CHANNEL COULD BE FOUND WITH THE GIVEN ID! PLEASE FIX IN CONFIG!");
                  break;
               }

               EmbedBuilder embed = this.buildEmbed(jda, CommonConfig.INSTANCE.getBotMode(), fruitData);

               Message message;
               try {
                  if (CommonConfig.INSTANCE.getMessageId() == 0L) {
                     message = channel.sendMessageEmbeds(embed.build(), new MessageEmbed[0]).complete();
                     CommonConfig.INSTANCE.setMessageId(message.getIdLong());
                  } else {
                     message = channel.editMessageEmbedsById(CommonConfig.INSTANCE.getMessageId(),
                           new MessageEmbed[] { embed.build() }).complete();
                  }
               } catch (ErrorResponseException e) {
                  LOGGER.warn("Message ID cannot be associated with a Message anymore. Sending new one!");
                  message = channel.sendMessageEmbeds(embed.build(), new MessageEmbed[0]).complete();
                  CommonConfig.INSTANCE.setMessageId(message.getIdLong());
               }

               oldFruitData = fruitData;

            } catch (InvalidTokenException e) {
               LOGGER.error("INCORRECT BOT TOKEN SUPPLIED! PLEASE FIX IN CONFIG!");
               break;
            } catch (InterruptedException e) {
               LOGGER.fatal(String.format("Something stopped JDA from starting: %s", e.getMessage()));
               break;
            } catch (InsufficientPermissionException e) {
               LOGGER.error(String.format("THE BOT IS MISSING NECESSARY PERMISSIONS: %s", e.getMessage()));
               break;
            } catch (IllegalArgumentException e) {
               LOGGER.error(String.format("Something happened while trying to access discord functionality: %s",
                     e.getMessage()));
               break;
            }
         }
      }
      this.stopped.set(true);
   }
}
