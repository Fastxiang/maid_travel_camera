package com.main.maid_travel_camera.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

/**
 * 女仆旅行成功事件
 * 当玩家成功取回旅行女仆照片并生成女仆时触发。
 * 可取消：取消后将不会触发默认的数据驱动奖励逻辑。
 */
@Cancelable
public class MaidTravelSuccessEvent extends Event {
    private final Level level;
    private final ServerPlayer player;
    private final EntityMaid maid;
    private final ItemStack photo;
    private String travelLocation;
    private final long travelStart;
    private final long travelDuration;

    public MaidTravelSuccessEvent(Level level, ServerPlayer player, EntityMaid maid,
                                  ItemStack photo, long travelStart, long travelDuration, String travelLocation) {
        this.level = level;
        this.player = player;
        this.maid = maid;
        this.photo = photo;
        this.travelStart = travelStart;
        this.travelDuration = travelDuration;
        this.travelLocation = travelLocation;
    }

    public Level getLevel() {
        return level;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public ItemStack getPhoto() {
        return photo;
    }

    public long getTravelStart() {
        return travelStart;
    }

    public long getTravelDuration() {
        return travelDuration;
    }
    
    public String getTravelLocation() {
        return travelLocation;
    }

    public void setTravelLocation(String travelLocation) {
        this.travelLocation = travelLocation;
    }
}
