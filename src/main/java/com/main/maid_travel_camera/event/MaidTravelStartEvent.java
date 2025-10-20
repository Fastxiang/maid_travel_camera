package com.main.maid_travel_camera.event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

/**
 * 女仆旅行开始事件（拍照事件）
 * 当玩家使用旅行相机拍下女仆、并准备生成旅行照片时触发。
 * 可修改旅行时长、附加信息等。
 */
public class MaidTravelStartEvent extends Event {
    private final Level level;
    private final ServerPlayer player;
    private final EntityMaid maid;
    private final ItemStack camera;
    private String travelLocation;
    private long travelDuration; // 单位：tick（20tick = 1秒）

    public MaidTravelStartEvent(Level level, ServerPlayer player, EntityMaid maid,
                                ItemStack camera, long travelDuration, String travelLocation) {
        this.level = level;
        this.player = player;
        this.maid = maid;
        this.camera = camera;
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

    public ItemStack getCamera() {
        return camera;
    }

    public long getTravelDuration() {
        return travelDuration;
    }
    
    public void setTravelDuration(long travelDuration) {
        this.travelDuration = travelDuration;
    }
    
    public String getTravelLocation() {
        return travelLocation;
    }
    
    public void setTravelLocation(String travelLocation) {
        this.travelLocation = travelLocation;
    }
}