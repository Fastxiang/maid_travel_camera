package com.main.maid_travel_camera.item;

import com.github.tartaricacid.touhoulittlemaid.item.ItemPhoto;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAndItemTransformEvent;
import com.github.tartaricacid.touhoulittlemaid.util.PlaceHelper;
import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.Util;
import net.minecraftforge.common.MinecraftForge;
import com.main.maid_travel_camera.event.MaidTravelSuccessEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TravelPhotoItem extends ItemPhoto {

    public TravelPhotoItem() {
        super();
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction facing = context.getClickedFace();
        Level worldIn = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack photo = context.getItemInHand();
        Vec3 clickLocation = context.getClickLocation();

        if (player == null) return super.useOn(context);

        if (facing != Direction.UP) return InteractionResult.FAIL;

        if (!hasMaidData(photo)) {
            if (worldIn.isClientSide) {
                player.sendSystemMessage(Component.literal("旅行照片没有女仆数据!"));
            }
            return InteractionResult.FAIL;
        }
        
        CompoundTag travelTag = getTravelData(photo);
        if (travelTag == null) return InteractionResult.FAIL;
        long now = worldIn.getGameTime();  
        long start = travelTag.getLong("StartTick");
        long duration = travelTag.getLong("DurationTicks");
        String travelLocation = travelTag.contains("TravelLocation") ? travelTag.getString("TravelLocation") : "unknown";

        if (now - start < duration) {
            if (worldIn.isClientSide) {
                player.sendSystemMessage(Component.literal("女仆还在旅行中，请耐心等待~"));
            }
            return InteractionResult.FAIL;
         }
        
        CompoundTag maidData = getMaidData(photo);

        Optional<Entity> entityOptional = Util.ifElse(EntityType.by(maidData).map(type -> type.create(worldIn)), entity -> {
            if (entity instanceof EntityMaid maid) {
                var event = new MaidAndItemTransformEvent.ToMaid(maid, photo, maidData);
                MinecraftForge.EVENT_BUS.post(event);
            }
            entity.load(maidData);
        }, () -> TouhouLittleMaid.LOGGER.warn("Skipping Entity with id {}", maidData.getString("id")));

        if (entityOptional.isPresent() && entityOptional.get() instanceof EntityMaid maid) {
            maid.setPos(clickLocation.x, clickLocation.y, clickLocation.z);
            if (!worldIn.isClientSide) {
                worldIn.addFreshEntity(maid);
                ServerLevel serverWorld = (ServerLevel) worldIn;
                var successEvent = new MaidTravelSuccessEvent(
        worldIn,
        (ServerPlayer) player,
        maid,
        photo.copy(),
        start,
        duration,
        travelLocation
                );
                if (!MinecraftForge.EVENT_BUS.post(successEvent)) {
                travelLocation = successEvent.getTravelLocation();
                ResourceLocation lootTableId = ResourceLocation.fromNamespaceAndPath(
                "maid_travel_camera", "travel_success/" + travelLocation.toLowerCase()
                );
                LootTable lootTable = serverWorld.getServer().getLootData().getLootTable(lootTableId);

                LootParams.Builder builder = new LootParams.Builder(serverWorld)
           .withParameter(LootContextParams.ORIGIN, maid.position())
           .withParameter(LootContextParams.THIS_ENTITY, maid)
           .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
           
           LootParams lootParams = builder.create(LootContextParamSets.GIFT);
           
           lootTable.getRandomItems(lootParams, 0, player::spawnAtLocation);
            }
            
            }
            maid.spawnExplosionParticle();
            photo.shrink(1);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
    
    @Nullable
    private CompoundTag getTravelData(ItemStack photo) {
        CompoundTag tag = photo.getTag();
        if (tag != null && tag.contains("MaidTravelInfo")) {
            return tag.getCompound("MaidTravelInfo");
        }
        return null;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    CompoundTag travelTag = getTravelData(stack);
    if (travelTag != null && worldIn != null) {
        long currentTick = worldIn.getGameTime();
        long startTick = travelTag.getLong("StartTick");
        long durationTicks = travelTag.getLong("DurationTicks");
        String location = travelTag.contains("TravelLocation") ? travelTag.getString("TravelLocation") : "unknown";

        long elapsedTicks = currentTick - startTick;
        long elapsedSeconds = Math.max(0, elapsedTicks / 20);
        long totalSeconds = durationTicks / 20;

        boolean isCompleted = elapsedTicks >= durationTicks;

        Component elapsedDisplay;
        if (elapsedSeconds >= 60) {
            elapsedDisplay = Component.translatable("time.minutes_seconds", elapsedSeconds / 60, elapsedSeconds % 60);
        } else {
            elapsedDisplay = Component.translatable("time.seconds", elapsedSeconds);
        }

        Component totalDisplay;
        if (totalSeconds >= 60) {
            totalDisplay = Component.translatable("time.minutes_seconds", totalSeconds / 60, totalSeconds % 60);
        } else {
            totalDisplay = Component.translatable("time.seconds", totalSeconds);
        }

        if (isCompleted) {
            tooltip.add(Component.translatable("tooltip.maid_travel_camera.photo.completed")
                    .withStyle(ChatFormatting.GREEN));
        } else {
            tooltip.add(Component.translatable("tooltip.maid_travel_camera.photo.traveling",
                    elapsedDisplay, totalDisplay).withStyle(ChatFormatting.AQUA));
        }

        tooltip.add(Component.translatable("tooltip.maid_travel_camera.photo.location",
                Component.translatable("travel_location." + location))
                .withStyle(ChatFormatting.GOLD));
    } else {
        tooltip.add(Component.translatable("tooltip.maid_travel_camera.photo.missing_data")
                .withStyle(ChatFormatting.RED));
    }

    if (!hasMaidData(stack)) {
        tooltip.add(Component.translatable("tooltips.touhou_little_maid.photo.no_data.desc")
                .withStyle(ChatFormatting.DARK_RED));
    }
    }
}
