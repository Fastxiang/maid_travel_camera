package com.main.maid_travel_camera;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import com.main.maid_travel_camera.registry.ModItems;
import com.main.maid_travel_camera.registry.ModCreativeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;

@Mod(MaidTravelCamera.MODID)
public class MaidTravelCamera {
    public static final String MODID = "maid_travel_camera";
    private static final Logger LOGGER = LogUtils.getLogger();

    public MaidTravelCamera(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();
        ModItems.register(bus);
        ModCreativeTabs.register(bus);
        
        MinecraftForge.EVENT_BUS.register(this);
        
        context.registerConfig(ModConfig.Type.COMMON, MaidTravelCameraConfig.SPEC);
    }
    
}
