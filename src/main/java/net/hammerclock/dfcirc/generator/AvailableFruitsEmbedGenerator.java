package net.hammerclock.dfcirc.generator;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;
import net.hammerclock.dfcirc.DevilFruitCirculationMod;
import net.hammerclock.dfcirc.config.CommonConfig;
import net.hammerclock.dfcirc.types.FruitData;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import org.apache.logging.log4j.Logger;
import xyz.pixelatedw.mineminenomi.api.OneFruitEntry;
import xyz.pixelatedw.mineminenomi.api.OneFruitEntry.Status;
import xyz.pixelatedw.mineminenomi.data.world.OFPWWorldData;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.*;

public class AvailableFruitsEmbedGenerator {
    private static final Logger LOGGER = DevilFruitCirculationMod.LOGGER;

    private static final int MAX_FRUITS_PER_LINE = 5;
    private static final String REPORT_ERROR_HERE = "This should not have happened! Please open a github issue here https://github.com/rathmerdominik/devilfruit-circulation-bot/issues with reproduction steps!";

    private final World world;
    private String goldBoxEmoji = "";
    private String ironBoxEmoji = "";
    private String woodenBoxEmoji = "";

    public AvailableFruitsEmbedGenerator(World world) {
        this.world = world;
        try {
            if (CommonConfig.INSTANCE.useEmojis()) {
                LOGGER.debug("Setting gold box emoji");
                this.goldBoxEmoji = DiscordIntegration.INSTANCE.getJDA().getEmojiById(CommonConfig.INSTANCE.getGoldBoxEmojiId()).getAsMention();
                LOGGER.debug("Setting iron gold box emoji");
                this.ironBoxEmoji = DiscordIntegration.INSTANCE.getJDA().getEmojiById(CommonConfig.INSTANCE.getIronBoxEmojiId()).getAsMention();
                LOGGER.debug("Setting wooden box emoji");
                this.woodenBoxEmoji = DiscordIntegration.INSTANCE.getJDA().getEmojiById(CommonConfig.INSTANCE.getWoodenBoxEmojiId()).getAsMention();
            }
        } catch (NullPointerException e) {
            LOGGER.error("One of the Emojis have an invalid ID!");
        }
    }

    /**
     * Sorts devil fruits by their rarity.
     *
     * @return A list of sorted FruitData sorted by their rarity
     */
    private static List<FruitData> sortFruitsByTier(Map<String, FruitData> fruitData) {
        LOGGER.debug("Starting sorting of fruits by tier");
        ArrayList<FruitData> goldBoxFruitData = new ArrayList<>();
        ArrayList<FruitData> ironBoxFruitData = new ArrayList<>();
        ArrayList<FruitData> woodenBoxFruitData = new ArrayList<>();
        ArrayList<FruitData> sortedFruitData = new ArrayList<>();

        Comparator<FruitData> nameComparator = Comparator.comparing(
                FruitData::getDevilFruitName,
                String.CASE_INSENSITIVE_ORDER
        );

        for (FruitData tierFruitData : fruitData.values()) {
            LOGGER.debug("Going through tier data of {}", tierFruitData.getDevilFruitTier());
            switch (tierFruitData.getDevilFruitTier()) {
                case GOLD:
                    LOGGER.debug("Tier is gold box");
                    goldBoxFruitData.add(tierFruitData);
                    break;
                case IRON:
                    LOGGER.debug("Tier is iron box");
                    ironBoxFruitData.add(tierFruitData);
                    break;
                case WOODEN:
                    LOGGER.debug("Tier is wooden box");
                    woodenBoxFruitData.add(tierFruitData);
                    break;
                default:
                    LOGGER.error(REPORT_ERROR_HERE);
                    LOGGER.error("Provide this context: FruitDataValues {} {} {}", tierFruitData.getDevilFruitName(), tierFruitData.getDevilFruitKey(), tierFruitData.getDevilFruitTier().name());
            }
        }

        LOGGER.debug("Sorting fruit data for each respective tier");
        goldBoxFruitData.sort(nameComparator);
        ironBoxFruitData.sort(nameComparator);
        woodenBoxFruitData.sort(nameComparator);

        LOGGER.debug("Combining all fruit data into one ArrayList");
        sortedFruitData.addAll(goldBoxFruitData);
        sortedFruitData.addAll(ironBoxFruitData);
        sortedFruitData.addAll(woodenBoxFruitData);

        LOGGER.debug("Returning sorted fruit data");
        return sortedFruitData;
    }

    /**
     * Get fruit owner name first from online player and if not found from the user cache
     *
     * @return Name of the owner of the fruit entry
     */
    private static String getOwnerName(OneFruitEntry entry, IWorld world) {
        LOGGER.debug("Starting search for fruit owner");
        String playerName = "";
        if (entry.getOwner().isPresent()) {
            LOGGER.debug("Fruit owner with uuid {} found", entry.getOwner().get());
            if (world.getPlayerByUUID(entry.getOwner().get()) != null) {
                LOGGER.debug("Found online players with matching UUID");
                playerName = world.getPlayerByUUID(entry.getOwner().get()).getDisplayName().getString();
            } else if (UsernameCache.getLastKnownUsername(entry.getOwner().get()) != null) {
                LOGGER.debug("Found offline player with matching name");
                playerName = UsernameCache.getLastKnownUsername(entry.getOwner().get());
            }
        }

        LOGGER.debug("Found owner with name {}", playerName);
        return playerName;
    }

    /**
     * Automatically sort fruits either by tier or alphabetically based on pre-defined config values
     *
     * @return A list of sorted FruitData
     */
    private List<FruitData> sortFruits(Map<String, FruitData> fruitData) {
        LOGGER.debug("Starting sorting of fruits");
        if (CommonConfig.INSTANCE.availableEmbedSortByTier()) {
            LOGGER.debug("Sort by tier is requested");
            List<FruitData> sortedFruitData = sortFruitsByTier(fruitData);

            if (CommonConfig.INSTANCE.availableEmbedSortByAlphabet()) {
                LOGGER.warn("You enabled Sort By Tier alongside Sort By Alphabet. Will not be respected!");
            }

            return sortedFruitData;
        } else if (CommonConfig.INSTANCE.availableEmbedSortByAlphabet()) {
            LOGGER.debug("Sorting by alphabet is requested");
            ArrayList<FruitData> listFruitData = new ArrayList<>();

            Comparator<FruitData> nameComparator = Comparator.comparing(
                    FruitData::getDevilFruitName,
                    String.CASE_INSENSITIVE_ORDER
            );

            for (FruitData fruitDataEntry : fruitData.values()) {
                LOGGER.debug("Adding fruit {} to list to be sorted", fruitDataEntry.devilFruitKey);
                listFruitData.add(fruitDataEntry);
            }
            LOGGER.debug("Sorting fruits alphabetically");
            listFruitData.sort(nameComparator);

            return listFruitData;
        } else {
            LOGGER.error("You have disabled sorting by tier and by alphabet!");
            return new ArrayList<>();
        }
    }

    /**
     * Generates a formatted string based on pre-defined config values.
     *
     * @return A formatted string
     */
    private String formatWithDecoration(FruitData fruitEmbedEntry) {
        LOGGER.debug("Starting formatting of an entry");
        String formattedString = "";
        String formatString = "%s**%s**";

        switch (fruitEmbedEntry.getDevilFruitTier()) {
            case GOLD:
                LOGGER.debug("Add formatting for gold tier");
                formattedString = String.format(formatString, this.goldBoxEmoji, fruitEmbedEntry.getDevilFruitName());
                break;
            case IRON:
                LOGGER.debug("Add formatting for iron tier");
                formattedString = String.format(formatString, this.ironBoxEmoji, fruitEmbedEntry.getDevilFruitName());
                break;
            case WOODEN:
                LOGGER.debug("Add formatting for wooden tier");
                formattedString = String.format(formatString, this.woodenBoxEmoji, fruitEmbedEntry.getDevilFruitName());
                break;
            default:
                LOGGER.error(REPORT_ERROR_HERE);
                LOGGER.error("Provide this context: DevilFruitTier {}", fruitEmbedEntry.getDevilFruitTier());
        }

        if (CommonConfig.INSTANCE.showStatus()) {
            LOGGER.debug("Status addition requested. Adding formatting for df fruit status");
            OFPWWorldData worldData = OFPWWorldData.get();
            if (worldData == null) {
                throw new IllegalStateException(REPORT_ERROR_HERE);
            }

            OneFruitEntry entry = worldData.getOneFruitEntry(new ResourceLocation(fruitEmbedEntry.devilFruitKey));

            if (entry != null && !getOwnerName(entry, this.world).isEmpty() && CommonConfig.INSTANCE.showPlayerNameAsStatus() && fruitEmbedEntry.getDevilFruitStatus().isPresent()) {
                LOGGER.debug("Adding player name alongside status as requested");
                formattedString = String.format("%s%n```%s``` by%n||%s||", formattedString, fruitEmbedEntry.getDevilFruitStatus().get().name(), getOwnerName(entry, this.world));
            } else {
                LOGGER.debug("Adding status");
                formattedString = String.format("%s%n```%s```", formattedString,
                        fruitEmbedEntry.getDevilFruitStatus().isPresent()
                                ? fruitEmbedEntry.getDevilFruitStatus().get().name()
                                : "FREE");
            }
        }

        LOGGER.debug("Returning formatted string: {}", formattedString);
        return formattedString;
    }

    /**
     * Retrieves the TextChannel where the Bot should send its messages.
     *
     * @return TextChannel if found. Optional.empty() if no Channel could be found matching the id in the config.
     */
    private Optional<TextChannel> getCirculationTextChannel() {
        TextChannel channel = DiscordIntegration.INSTANCE.getJDA().getTextChannelById(CommonConfig.INSTANCE.getAvailableChannelId());
        if (channel == null) {
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    /**
     * Fills an embed based on available fruits.
     * Each fruit line is also getting formatted by set config values.
     *
     * @param fruitData Fruit data to sort and fill the embed with
     */
    public void sendAvailableEmbed(Map<String, FruitData> fruitData) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("**### Available ###**");
        eb.setFooter(CommonConfig.INSTANCE.getAvailableEmbedFooter());
        eb.setColor(Color.decode(CommonConfig.INSTANCE.getAvailableEmbedColor()));

        if (CommonConfig.INSTANCE.availableEmbedShowLastUpdated()) {
            eb.setTimestamp(OffsetDateTime.now());
        }

        List<FruitData> sortedFruitData = this.sortFruits(fruitData);

        List<String> batchFruit = new ArrayList<>();

        for (int i = 0; i < sortedFruitData.size(); i++) {
            FruitData fruitEntry = sortedFruitData.get(i);

            if (!fruitEntry.getDevilFruitStatus().isPresent() || fruitEntry.getDevilFruitStatus().get() == Status.LOST) {

                if (batchFruit.size() == MAX_FRUITS_PER_LINE) {
                    eb.addField("", String.join("\n", batchFruit), true);
                    batchFruit.clear();
                }

                batchFruit.add(this.formatWithDecoration(fruitEntry));
            }
        }

        eb.addField("", String.join("\n", batchFruit), true);

        this.buildAndSendEmbed(eb);
    }

    /**
     * Build a given embed and send it to the provided channel.
     * This will either reuse the existing message id provided in the config Message ID or create a new one in case it is broken.
     */
    public void buildAndSendEmbed(EmbedBuilder embedBuilder) {
        TextChannel channel = getCirculationTextChannel().orElseThrow(() -> new IllegalArgumentException("Circulation Text Channel ID is invalid. Please fix in config!"));

        if (CommonConfig.INSTANCE.getAvailableMessageId() == 0L) {
            channel.sendMessageEmbeds(embedBuilder.build()).queue(result -> CommonConfig.INSTANCE.setAvailableMessageId(result.getIdLong()));
        } else {
            channel.editMessageEmbedsById(
                            CommonConfig.INSTANCE.getAvailableMessageId(), embedBuilder.build())
                    .queue(null, new ErrorHandler()
                            .handle(
                                    ErrorResponse.UNKNOWN_MESSAGE,
                                    e -> channel.sendMessageEmbeds(embedBuilder.build()).queue(result -> CommonConfig.INSTANCE.setAvailableMessageId(result.getIdLong()))));
        }
    }
}
