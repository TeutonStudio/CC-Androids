package com.thunderbear06;

import com.mojang.logging.LogUtils;
import com.thunderbear06.entity.AndroidEntity;
import com.thunderbear06.item.WrenchItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(CCAndroids.MOD_ID)
public final class CCAndroids {
    public static final String MOD_ID = "cc_androids";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);

    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties().durability(100)));
    public static final DeferredItem<Item> COMPONENTS = ITEMS.registerSimpleItem("components");
    public static final DeferredItem<Item> ANDROID_CPU = ITEMS.registerSimpleItem("android_cpu");
    public static final DeferredItem<Item> REDSTONE_REACTOR = ITEMS.registerSimpleItem("redstone_reactor");
    public static final DeferredItem<Item> ANDROID_FRAME = ITEMS.registerSimpleItem("android_frame");

    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> ANDROID = registerAndroid("android", MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> ADVANCED_ANDROID = registerAndroid("advanced_android", MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> COMMAND_ANDROID = registerAndroid("command_android", MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> UNFINISHED_ANDROID = registerAndroid("unfinished_android", MobCategory.MISC);
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> ROGUE_ANDROID = registerAndroid("rogue_android", MobCategory.MONSTER);

    public static final DeferredItem<SpawnEggItem> ANDROID_SPAWN = spawnEgg("android_spawn", ANDROID, 0xb2b2b2, 0x8a8c8b);
    public static final DeferredItem<SpawnEggItem> ADVANCED_ANDROID_SPAWN = spawnEgg("android_advanced_spawn", ADVANCED_ANDROID, 0xb2b2b2, 0xa5a333);
    public static final DeferredItem<SpawnEggItem> COMMAND_ANDROID_SPAWN = spawnEgg("android_command_spawn", COMMAND_ANDROID, 0xfc9e46, 0x9b5c22);
    public static final DeferredItem<SpawnEggItem> ROGUE_ANDROID_SPAWN = spawnEgg("android_rogue_spawn", ROGUE_ANDROID, 0xf41818, 0x9b2222);

    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_AMBIENT = sound("android_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_HURT = sound("android_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_DEATH = sound("android_death");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ANDROIDS_TAB = CREATIVE_TABS.register("androids_item_group", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cc_androids.android_item_group"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> WRENCH.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(WRENCH.get());
                output.accept(COMPONENTS.get());
                output.accept(ANDROID_CPU.get());
                output.accept(REDSTONE_REACTOR.get());
                output.accept(ANDROID_FRAME.get());
                output.accept(ANDROID_SPAWN.get());
                output.accept(ADVANCED_ANDROID_SPAWN.get());
                output.accept(COMMAND_ANDROID_SPAWN.get());
                output.accept(ROGUE_ANDROID_SPAWN.get());
            })
            .build());

    public CCAndroids(IEventBus modEventBus) {
        NeoForgeMod.enableMilkFluid();
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        LOGGER.info("Loaded CC: Androids for NeoForge");
    }

    private static DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> registerAndroid(String name, MobCategory category) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(AndroidEntity::new, category)
                .sized(0.6F, 1.95F)
                .clientTrackingRange(8)
                .build(name));
    }

    private static DeferredItem<SpawnEggItem> spawnEgg(String name, DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> type, int background, int highlight) {
        return ITEMS.register(name, () -> new SpawnEggItem(type.get(), background, highlight, new Item.Properties()));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> sound(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    public static final class ModEvents {
        private ModEvents() {
        }

        @SubscribeEvent
        public static void registerAttributes(EntityAttributeCreationEvent event) {
            event.put(ANDROID.get(), AndroidEntity.createAttributes().build());
            event.put(ADVANCED_ANDROID.get(), AndroidEntity.createAttributes().add(Attributes.MAX_HEALTH, 30.0D).build());
            event.put(COMMAND_ANDROID.get(), AndroidEntity.createAttributes().add(Attributes.MAX_HEALTH, 24.0D).build());
            event.put(UNFINISHED_ANDROID.get(), Mob.createMobAttributes().build());
            event.put(ROGUE_ANDROID.get(), AndroidEntity.createAttributes().add(Attributes.ATTACK_DAMAGE, 4.0D).build());
        }
    }
}
