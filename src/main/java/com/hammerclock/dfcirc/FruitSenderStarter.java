package com.hammerclock.dfcirc;

import com.hammerclock.dfcirc.config.CommonConfig;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.FMLNetworkConstants;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("dfcirc")
public class FruitSenderStarter {
   private static final Logger LOGGER = LogManager.getLogger();
   private FruitDataSender sender;

   public FruitSenderStarter() {
      ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
            () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (a, b) -> true));

      ModLoadingContext context = ModLoadingContext.get();
      context.registerConfig(Type.COMMON, CommonConfig.CONFIG, "dfcirc-common.toml");
      FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
      MinecraftForge.EVENT_BUS.register(this);
   }

   @SubscribeEvent
   public void onServerStartedEvent(FMLServerStartedEvent event) {
      if (xyz.pixelatedw.mineminenomi.config.CommonConfig.INSTANCE.hasOneFruitPerWorldExtendedLogic()
            || xyz.pixelatedw.mineminenomi.config.CommonConfig.INSTANCE.hasOneFruitPerWorldSimpleLogic()) {
         if (CommonConfig.INSTANCE.getBotToken() != "") {
            this.sender = new FruitDataSender();
            this.sender.start();
         } else {
            LOGGER.warn(
                  "PLEASE SET A BOT TOKEN AND OTHER IMPORTANT INFORMATION IN dfcirc-common.toml\n Refusing to start the Bot!");
         }
      } else {
         LOGGER.error(
               "Mine Mine no Mi has One Fruit per World config not set to either SIMPLE or EXTENDED!\nRefusing to start the Bot!");
      }

   }

   @SubscribeEvent
   public void onServerStoppedEvent(FMLServerStoppingEvent event) {
      if (this.sender != null) {
         LOGGER.info("Shutting down Devil Fruit Circulation bot");
         this.sender.interrupt();

      }
   }

   private void setup(FMLCommonSetupEvent event) {
      LOGGER.info("Devil Fruit Circulation Bot successfully started!");
   }
}
