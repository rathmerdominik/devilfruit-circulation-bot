package net.hammerclock.dfcirc.events;

import java.util.HashMap;
import java.util.Map;


import org.apache.logging.log4j.Logger;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.internal.requests.RestActionImpl;
import net.hammerclock.dfcirc.DevilFruitCirculationMod;
import net.hammerclock.dfcirc.config.CommonConfig;
import net.hammerclock.dfcirc.generator.AvailableFruitsEmbedGenerator;
import net.hammerclock.dfcirc.generator.UnavailableFruitsEmbedGenerator;
import net.hammerclock.dfcirc.types.FruitData;
import net.hammerclock.dfcirc.types.TierBox;

import net.minecraft.world.World;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import xyz.pixelatedw.mineminenomi.api.OneFruitEntry;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.DroppedDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.EatDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.InventoryDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.api.events.onefruit.LostDevilFruitEvent;
import xyz.pixelatedw.mineminenomi.data.world.ExtendedWorldData;
import xyz.pixelatedw.mineminenomi.init.ModValues;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

public final class FruitEvents {
	private static final Logger LOGGER = DevilFruitCirculationMod.LOGGER;

	private World world;

	@SubscribeEvent
	void onServerStartedEvent(FMLServerStartedEvent event) {
		LOGGER.info("Devil Fruit Circulation bot Started!");
		LOGGER.debug("Server started and fruit data embed is being updated");
		this.world = event.getServer().getLevel(World.OVERWORLD);
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	void onFruitDroppedEvent(DroppedDevilFruitEvent event) {
		LOGGER.debug("Received Fruit dropped event");
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	void onFruitInInventoryEvent(InventoryDevilFruitEvent event) {
		LOGGER.debug("Received Fruit in inventory event");
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	void onFruitEatenEvent(EatDevilFruitEvent event) {
		LOGGER.debug("Received Fruit eaten event");
		getFruitDataAndSendEmbed();
	}

	@SubscribeEvent
	void onFruitLostEvent(LostDevilFruitEvent event) {
		LOGGER.debug("Received Fruit lost event");
		getFruitDataAndSendEmbed();
	}

	private void getFruitDataAndSendEmbed() {
		if(CommonConfig.INSTANCE.showAvailableEmbed()) {
			LOGGER.debug("Starting generation of available fruits embed");
			AvailableFruitsEmbedGenerator embedGenerator = new AvailableFruitsEmbedGenerator(this.world);
			LOGGER.debug("Building and sending available fruits embed ");
			embedGenerator.sendAvailableEmbed(getFruitData());
		}
		if(CommonConfig.INSTANCE.showUnavailableEmbed()) {
			LOGGER.debug("Starting generation of unavailable embed");
			UnavailableFruitsEmbedGenerator embedGenerator = new UnavailableFruitsEmbedGenerator(this.world);
			LOGGER.debug("Prepare unavailable embed");
			embedGenerator.sendUnavailableEmbed(getFruitData());

		}
	}


	private static Map<String, FruitData> getFruitData() {
		LOGGER.debug("Getting fruit data");
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

				LOGGER.debug("Inserting fruit with name {} and fruit key {} and status {} and tier {}", fruit.getDevilFruitName(), fruit.getFruitKey(), status, TierBox.values()[fruit.getTier() - 1]);
				FruitData fruitData = new FruitData(
						fruit.getDevilFruitName(),
						fruit.getFruitKey(),
						status,
						TierBox.values()[fruit.getTier() - 1]
					);

				fruitDataMap.put(fruit.getFruitKey(), fruitData);
			}
		}

		return fruitDataMap;
	}
}
