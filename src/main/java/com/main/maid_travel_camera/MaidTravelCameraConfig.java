package com.main.maid_travel_camera;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = MaidTravelCamera.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MaidTravelCameraConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 配置列表
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TRAVEL_LOCATIONS;

    static final ForgeConfigSpec SPEC;

    static {
        TRAVEL_LOCATIONS = BUILDER
                .comment(
                "女仆进行旅行时，默认会根据权重随机选择旅行地点，旅行地点绑定时间。（时间单位：tick）",
                "格式示例：forest=1200,weight=5",
                "格式对应着：旅行地点=时间，weight为随机权重",
                "旅行地点可以随意自己写任何英文，旅行地点的奖励在data/maid_travel_camera/loot_tables/travel_success/旅行地点名字.json",
                "When the maid goes on a trip, a location will be randomly selected according to weight, and each location has a corresponding travel time (unit: tick).",
                "Example format: forest=1200,weight=5",
                "Format explanation: location=ticks, weight is the random weight",
                "You can write any English name for locations, rewards are in data/maid_travel_camera/loot_tables/travel_success/location_name.json"
                )
                .defineListAllowEmpty(
                        List.of("travel_locations"),
                        List.of(
                                "forest=1200,weight=5",
                                "desert=2400,weight=2",
                                "village=2000,weight=1",
                                "nether=6000,weight=3"
                        ),
                        obj -> obj instanceof String s && s.contains("=")
                );

        SPEC = BUILDER.build();
    }

    public static final Map<String, LocationData> LOCATION_DATA_MAP = new HashMap<>();
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        reload();
    }

    public static void reload() {
        LOCATION_DATA_MAP.clear();
        for (String entry : TRAVEL_LOCATIONS.get()) {
            try {
                String[] parts = entry.split(",");
                String[] nameAndTime = parts[0].split("=");
                if (nameAndTime.length < 2) continue;

                String name = nameAndTime[0].trim().toLowerCase();
                long ticks = Long.parseLong(nameAndTime[1].trim());
                int weight = 1;
                
                for (String part : parts) {
                    if (part.trim().startsWith("weight=")) {
                        weight = Integer.parseInt(part.trim().substring(7));
                    }
                }

                LOCATION_DATA_MAP.put(name, new LocationData(name, ticks, weight));
            } catch (Exception e) {
                System.err.println("[MaidTravelCameraConfig] 无法解析旅行配置: " + entry);
            }
        }
    }

    /**
     * 根据权重随机选择一个旅行地点。
     */
    public static LocationData getRandomLocation() {
        if (LOCATION_DATA_MAP.isEmpty()) return new LocationData("unknown", 1200, 1);

        int totalWeight = LOCATION_DATA_MAP.values().stream().mapToInt(LocationData::weight).sum();
        int roll = RANDOM.nextInt(totalWeight);
        int current = 0;

        for (LocationData data : LOCATION_DATA_MAP.values()) {
            current += data.weight();
            if (roll < current) {
                return data;
            }
        }
        return LOCATION_DATA_MAP.values().iterator().next();
    }

    public record LocationData(String name, long duration, int weight) {}
}
