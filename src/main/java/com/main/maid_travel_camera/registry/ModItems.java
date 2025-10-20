package com.main.maid_travel_camera.registry;

import com.main.maid_travel_camera.MaidTravelCamera;
import com.main.maid_travel_camera.item.TravelCameraItem;
import com.main.maid_travel_camera.item.TravelPhotoItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MaidTravelCamera.MODID);

    public static final RegistryObject<Item> TRAVEL_CAMERA = ITEMS.register("travel_camera", TravelCameraItem::new);

    public static final RegistryObject<Item> TRAVEL_PHOTO = ITEMS.register("travel_photo", TravelPhotoItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
