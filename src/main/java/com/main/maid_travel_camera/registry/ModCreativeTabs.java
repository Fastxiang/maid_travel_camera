package com.main.maid_travel_camera.registry;

import com.main.maid_travel_camera.MaidTravelCamera;
import com.main.maid_travel_camera.item.TravelCameraItem;
import com.main.maid_travel_camera.item.TravelPhotoItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MaidTravelCamera.MODID);

    public static final RegistryObject<CreativeModeTab> MAID_TRAVEL_TAB = TAB_REGISTER.register("maid_travel_camera", () ->
            CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.TRAVEL_CAMERA.get()))
                    .title(Component.translatable("itemGroup.maid_travel_camera"))
                    .displayItems((params, output) -> {
                        output.accept(new ItemStack(ModItems.TRAVEL_CAMERA.get()));
                        output.accept(new ItemStack(ModItems.TRAVEL_PHOTO.get()));
                    })
                    .build()
    );

    public static void register(IEventBus bus) {
        TAB_REGISTER.register(bus);
    }
}
