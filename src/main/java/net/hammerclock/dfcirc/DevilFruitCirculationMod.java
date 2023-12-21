package net.hammerclock.dfcirc;

import net.hammerclock.dfcirc.config.CommonConfig;
import net.hammerclock.dfcirc.events.FruitEvents;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.erdbeerbaerlp.dcintegration.common.DiscordIntegration;

@Mod(DevilFruitCirculationMod.MOD_ID)
public final class DevilFruitCirculationMod {
	public static final String MOD_ID = "dfcirc";

	@SuppressWarnings("java:S1312")
	// java:S1312 While this rule makes complete sense for any other proper Java application it does not make sense in Minecraft's twisted world
	public static final Logger LOGGER = LogManager.getLogger();

	@SuppressWarnings("java:S1118")
	// java:S1118 Yet another Minecraft thing. This method is called over reflection by Forge...
	public DevilFruitCirculationMod() {
		// Make mod only needed on the server side
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
			() -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(Type.COMMON, CommonConfig.CONFIG, "dfcirc-common.toml");
		MinecraftForge.EVENT_BUS.addListener(DevilFruitCirculationMod::onServerStarting);
	}

	private static void onServerStarting(FMLServerStartingEvent event) {
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

		LOGGER.info("Successfully started Devil Fruit Circulation bot!");
	}
}
