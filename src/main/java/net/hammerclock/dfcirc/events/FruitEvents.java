package net.hammerclock.dfcirc.events;

import java.awt.Color;

import java.time.OffsetDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

import net.hammerclock.dfcirc.DevilFruitCirculationMod;
import net.hammerclock.dfcirc.config.CommonConfig;
import net.hammerclock.dfcirc.types.FruitData;
import net.hammerclock.dfcirc.types.TierBox;

import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import xyz.pixelatedw.mineminenomi.api.OneFruitEntry;
import xyz.pixelatedw.mineminenomi.api.OneFruitEntry.Status;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.DroppedDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.EatDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.InventoryDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.LostDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.data.world.ExtendedWorldData;
import xyz.pixelatedw.mineminenomi.init.ModValues;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

public final class FruitEvents {
	private static final Logger LOGGER = DevilFruitCirculationMod.LOGGER;
	private static final int MAX_FRUITS_PER_LINE = 5;
	private static final String REPORT_ERROR_HERE = "This should not have happened! Please open a github issue here https://github.com/rathmerdominik/MineMineNoMiDevilFruitCirculationBot/issues with reproduction steps!";
	
	private FruitEvents() {
		throw new IllegalStateException("Utility class");
	}
	
	@SubscribeEvent
	public static void onServerStartedEvent(FMLServerStartedEvent event) {
		getFruitDataAndSendEmbed();
	} 

	@SubscribeEvent
	public static void onFruitDroppedEvent(DroppedDevilFruitEvent event){
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	public static void onFruitInInventoryEvent(InventoryDevilFruitEvent event){
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	public static void onFruitEatenEvent(EatDevilFruitEvent event){
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	public static void onFruitLostEvent(LostDevilFruitEvent event){
		getFruitDataAndSendEmbed();
	}

	private static void getFruitDataAndSendEmbed(){
		Map<String, FruitData> fruitData = getFruitData();
		EmbedBuilder eb = generateEmbed(fruitData);
		buildAndSendEmbed(eb);
	}

	private static Map<String, FruitData> getFruitData() {
		HashMap<String, FruitData> fruitDataMap = new HashMap<>();
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

				FruitData fruitData = new FruitData(
						fruit.getDevilFruitName(),
						fruit.getFruitKey(), status,
						TierBox.values()[fruit.getTier() - 1]
					);

				fruitDataMap.put(fruit.getFruitKey(), fruitData);
			}
		}

		return fruitDataMap;
	}

	/**
	 * Generates a formatted string based on pre-defined config values.
	 * 
	 * @param fruitEntry
	 * @return A formatted string
	 */
	@SuppressWarnings({"java:S1696", "java:S1774"})
	// java:S1696 Catching a NullPointer makes more sense instead of if/else checking for every emoji's existence
	// java:S1774 Ternary is way more readable in this instance then multiple nested if/else
	private static String formatWithDecoration(FruitData fruitEntry) {
		String formattedString = "";
		
		String goldBoxEmoji = "";
		String ironBoxEmoji = "";
		String woodenBoxEmoji = "";

		try {
			if(CommonConfig.INSTANCE.useEmojis()) {
				goldBoxEmoji = DiscordIntegration.INSTANCE.getJDA().getEmojiById(CommonConfig.INSTANCE.getGoldBoxEmojiId()).getAsMention();
				ironBoxEmoji = DiscordIntegration.INSTANCE.getJDA().getEmojiById(CommonConfig.INSTANCE.getIronBoxEmojiId()).getAsMention();
				woodenBoxEmoji = DiscordIntegration.INSTANCE.getJDA().getEmojiById(CommonConfig.INSTANCE.getWoodenBoxEmojiId()).getAsMention();
			}
		} catch (NullPointerException e) {
			LOGGER.debug(e);
			LOGGER.error("One of the Emojis has an invalid ID!");
		}

		String formatString = "%s**%s**";
		switch (fruitEntry.getDevilFruitTier()) {
			case GOLD:
				formattedString = String.format(formatString, goldBoxEmoji, fruitEntry.getDevilFruitName());
				break;
			case IRON:
				formattedString = String.format(formatString, ironBoxEmoji, fruitEntry.getDevilFruitName());
				break;
			case WOODEN:
				formattedString = String.format(formatString, woodenBoxEmoji, fruitEntry.getDevilFruitName());
				break;
			default:
				LOGGER.fatal(REPORT_ERROR_HERE);
				LOGGER.fatal("Provide this context: DevilFruitTier {}", fruitEntry.getDevilFruitTier());
		}		

		if (CommonConfig.INSTANCE.showStatus()) {
			formattedString = String.format("%s%n__Status:__ %s", formattedString,
				fruitEntry.getDevilFruitStatus().isPresent()
						? fruitEntry.getDevilFruitStatus().orElseThrow(IllegalArgumentException::new).name()
						: "Free");
		}

		return formattedString;
	}

	/**
	 * Automatically sort fruits either by tier or alphabetically based on pre-defined config values
	 * 
	 * @param fruitData
	 * @return A list of sorted FruitData
	 */
	private static List<FruitData> sortFruits(Map<String, FruitData> fruitData) {
		if (CommonConfig.INSTANCE.embedSortByTier()) {
			List<FruitData> sortedFruitData = sortByTier(fruitData); 

			if (CommonConfig.INSTANCE.embedSortByAlphabet()) {
				LOGGER.warn("You enabled Sort By Tier alongside Sort By Alphabet. Will not be respected!");
			} 

			return sortedFruitData;
		} else if(CommonConfig.INSTANCE.embedSortByAlphabet()) {
			ArrayList<FruitData> listFruitData = new ArrayList<>();
			Comparator<FruitData> nameComparator = Comparator.comparing(
				FruitData::getDevilFruitName,
				String.CASE_INSENSITIVE_ORDER
			);

			for (FruitData fruitDataEntry : fruitData.values()) {
				listFruitData.add(fruitDataEntry);
			}
			Collections.sort(listFruitData, nameComparator);

			return listFruitData;
		} else {
			LOGGER.fatal(REPORT_ERROR_HERE);
			LOGGER.fatal("Provide this context: SortFruits {}", fruitData.values());
			return new ArrayList<>();
		}
	}

	/**
	 * Sorts devil fruits by their rarity.
	 * 
	 * @param fruitData
	 * @return A list of sorted FruitData sorted by their rarity
	 */
	private static List<FruitData> sortByTier(Map<String, FruitData> fruitData) {

		ArrayList<FruitData> goldBoxFruitData = new ArrayList<>();
		ArrayList<FruitData> ironBoxFruitData = new ArrayList<>();
		ArrayList<FruitData> woodenBoxFruitData = new ArrayList<>();
		ArrayList<FruitData> sortedFruitData = new ArrayList<>();

		Comparator<FruitData> nameComparator = Comparator.comparing(
				FruitData::getDevilFruitName,
				String.CASE_INSENSITIVE_ORDER
			);

		for (FruitData tierFruitData : fruitData.values()) {
			switch (tierFruitData.getDevilFruitTier()) {
				case GOLD:
					goldBoxFruitData.add(tierFruitData);
					break;
				case IRON:
					ironBoxFruitData.add(tierFruitData);
					break;
				case WOODEN:
					woodenBoxFruitData.add(tierFruitData);
					break;
				default:
					LOGGER.fatal(REPORT_ERROR_HERE);
					LOGGER.fatal("Provide this context: FruitDataValues {} {} {}", tierFruitData.getDevilFruitName(), tierFruitData.getDevilFruitKey(), tierFruitData.getDevilFruitTier().name());
			}
		}

		Collections.sort(goldBoxFruitData, nameComparator);
		Collections.sort(ironBoxFruitData, nameComparator);
		Collections.sort(woodenBoxFruitData, nameComparator);

		sortedFruitData.addAll(goldBoxFruitData);
		sortedFruitData.addAll(ironBoxFruitData);
		sortedFruitData.addAll(woodenBoxFruitData);

		return sortedFruitData;
	}



	/**
	 * Retrieves the TextChannel where the Bot should send its messages.
	 * 
	 * @return TextChannel if found. Optional.empty() if no Channel could be found matching the id in the config.
	 */
	private static Optional<TextChannel> getCirculationTextChannel() {
		TextChannel channel = DiscordIntegration.INSTANCE.getJDA().getTextChannelById(CommonConfig.INSTANCE.getChannelId());
		if (channel == null) {
			return Optional.empty();
		}
		return Optional.of(channel);
	}

	/**
	 * Build a given embed and send it to the provided channel.
	 * This will either reuse the existing message id provided in the config Message ID or create a new one in case it is broken.
	 *  
	 * @param channel
	 * @param embedBuilder
	 */
	private static void buildAndSendEmbed(EmbedBuilder embedBuilder) {
		TextChannel channel = getCirculationTextChannel().orElseThrow(() -> new IllegalArgumentException("Circulation Text Channel ID is invalid. Please fix in config!"));
		
		Thread embedSend = new Thread() {
			@Override
			@SuppressWarnings("java:S1120")
			// java:S1120 I see no world where that would make sense. Basically indent everything by removing a tab in this function.
			public void run() {
				try {
					if (CommonConfig.INSTANCE.getMessageId() == 0L) {
						Message message = channel.sendMessageEmbeds(embedBuilder.build()).complete();
						CommonConfig.INSTANCE.setMessageId(message.getIdLong());
					} else {
						channel.editMessageEmbedsById(CommonConfig.INSTANCE.getMessageId(), embedBuilder.build()).complete();
					}
				} catch (ErrorResponseException e) {
					LOGGER.debug(e);
					LOGGER.warn("Message ID cannot be associated with a Message anymore. Sending new one!");

					Message message = channel.sendMessageEmbeds(embedBuilder.build()).complete();
					CommonConfig.INSTANCE.setMessageId(message.getIdLong());
				}
			}
		};

		embedSend.start();
	}

	/**
	 * Fills an embed based on available fruits. 
	 * Each fruit line is also getting formatted by set config values.
	 * 
	 * @param eb The already existing Embed to fill
	 * @param fruitData Fruit data to sort and fill the embed with
	 * @return An EmbedBuilder that has formatted sorted fruits based on available fruits 
	 */
	private static EmbedBuilder buildEmbedShowAvailable(EmbedBuilder eb, Map<String, FruitData> fruitData) {
		eb.addField("Available Devil Fruits", "", false);

		List<FruitData> sortedFruitData = sortFruits(fruitData);

		List<String> batchFruit = new ArrayList<>();

		for (int i = 0; i < sortedFruitData.size(); i++) {
			FruitData fruitEntry = sortedFruitData.get(i);

			if (!fruitEntry.getDevilFruitStatus().isPresent() || 
				fruitEntry.getDevilFruitStatus().orElseThrow(IllegalArgumentException::new) == Status.LOST) {

				if (batchFruit.size() == MAX_FRUITS_PER_LINE) {
					eb.addField("", String.join("\n", batchFruit), true);
					batchFruit.clear();
				}

				batchFruit.add(formatWithDecoration(fruitEntry));
			}
		}

		eb.addField("", String.join("\n", batchFruit), true);

		return eb;
	}

	/**
	 * Fills an embed based on unavailable fruits.
	 * Each fruit line is also getting formatted by set config values.
	 * 
	 * @param eb The already existing Embed to fill
	 * @param fruitData Fruit data to sort and fill the embed with
	 * @return An EmbedBuilder that has formatted sorted fruits based on unavailable fruits 
	 */
	private static EmbedBuilder buildEmbedShowUnavailable(EmbedBuilder eb, Map<String, FruitData> fruitData) {
		eb.addField("Unavailable Devil Fruits", "", false);

		List<FruitData> sortedFruitData = sortFruits(fruitData);

		List<String> batchFruit = new ArrayList<>();

		for (int i = 0; i < sortedFruitData.size(); i++) {
			FruitData fruitEntry = sortedFruitData.get(i);

			if (fruitEntry.getDevilFruitStatus().isPresent() && 
				fruitEntry.getDevilFruitStatus().orElseThrow(IllegalArgumentException::new) != Status.LOST) {
				
				if (batchFruit.size() == MAX_FRUITS_PER_LINE) {
					eb.addField("", String.join("\n", batchFruit), true);
					batchFruit.clear();
				}

				batchFruit.add(formatWithDecoration(fruitEntry));
			}
		}

		eb.addField("", String.join("\n", batchFruit), true);

		return eb;
	}

	/**
	 * Fills an embed based on unavailable and available fruits.
	 * Each fruit line is also getting formatted by set config values.
	 * 
	 * @param eb
	 * @param fruitData
	 * @return An EmbedBuilder that has formatted sorted fruits based on unavailable and available fruits 
	 */
	private static EmbedBuilder buildEmbedShowBoth(EmbedBuilder eb, Map<String, FruitData> fruitData) {
		EmbedBuilder available = buildEmbedShowAvailable(eb, fruitData);
		return buildEmbedShowUnavailable(available, fruitData);
	}

	/**
	 *  Generate a formatted embed based on the bot mode in the config and the given fruit data
	 * 
	 * @param fruitData
	 * @return The finished and formatted embed builder
	 */
	@SuppressWarnings("java:S4165")
	// java:S4165 The value is NOT the same as new things are added to the embed builder
	private static EmbedBuilder generateEmbed(Map<String, FruitData> fruitData) {
		EmbedBuilder eb = new EmbedBuilder();

		eb.setTitle(CommonConfig.INSTANCE.getEmbedTitle());
		eb.setFooter(CommonConfig.INSTANCE.getEmbedFooter());
		eb.setColor(Color.decode(CommonConfig.INSTANCE.getEmbedColor()));

		if (CommonConfig.INSTANCE.embedShowLastUpdated()) {
			eb.setTimestamp(OffsetDateTime.now());
		}

		switch (CommonConfig.INSTANCE.getBotMode()) {
			case ONLY_SHOW_AVAILABLE:
				eb = buildEmbedShowAvailable(eb, fruitData);
				break;
			case ONLY_SHOW_UNAVAILABLE:
				eb = buildEmbedShowUnavailable(eb, fruitData);
				break;
			case SHOW_AVAILABLE_AND_UNAVAILABLE:
				eb = buildEmbedShowBoth(eb, fruitData);
				break;
		}
		return eb;
	}
}
