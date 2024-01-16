package net.hammerclock.dfcirc;

import net.hammerclock.dfcirc.config.CommonConfig;
import net.hammerclock.dfcirc.events.FruitEvents;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.CheckResult;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;

@Mod(DevilFruitCirculationMod.PROJECT_ID)
public final class DevilFruitCirculationMod {
	public static final String PROJECT_ID = "dfcirc";

	public static final Logger LOGGER = LogManager.getLogger();

	public DevilFruitCirculationMod() {
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
			() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		ModLoadingContext context = ModLoadingContext.get();

		context.registerConfig(Type.COMMON, CommonConfig.CONFIG, "dfcirc-common.toml");

		MinecraftForge.EVENT_BUS.register(DevilFruitCirculationMod.class);
	}

	@SubscribeEvent
	static void onServerStarted(FMLServerStartingEvent event) {
		CheckResult result = VersionChecker.getResult(ModList.get().getModContainerById(PROJECT_ID).orElseThrow(IllegalArgumentException::new).getModInfo());
		if(result.status == Status.OUTDATED) {
			LOGGER.warn("YOUR MOD IS OUTDATED. The latest version is {}. Please get the latest version here: {}", result.target, result.url);
		}
		if (!xyz.pixelatedw.mineminenomi.config.CommonConfig.INSTANCE.hasOneFruitPerWorldExtendedLogic()
			|| !xyz.pixelatedw.mineminenomi.config.CommonConfig.INSTANCE.hasOneFruitPerWorldSimpleLogic()) {
			LOGGER.error("Mine Mine no Mi has One Fruit per World config not set to either SIMPLE or EXTENDED!");
			LOGGER.error("Refusing to start the Bot!");
			return;
		}
		if (DiscordIntegration.INSTANCE == null) {
			LOGGER.error("Discord Integration is not properly set up!");
			return;
		}
		MinecraftForge.EVENT_BUS.register(new FruitEvents());
	}
}
