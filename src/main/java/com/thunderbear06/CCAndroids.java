package com.thunderbear06;

import com.mojang.logging.LogUtils;
import com.thunderbear06.computer.api.AndroidAPI;
import com.thunderbear06.config.CCAndroidsConfig;
import com.thunderbear06.config.ConfigLoader;
import com.thunderbear06.entity.android.AdvancedAndroidEntity;
import com.thunderbear06.entity.android.AndroidEntity;
import com.thunderbear06.entity.android.CommandAndroidEntity;
import com.thunderbear06.entity.android.RogueDroidEntity;
import com.thunderbear06.entity.android.frame.AndroidFrame;
import com.thunderbear06.item.AndroidFrameItem;
import com.thunderbear06.item.WrenchItem;
import com.thunderbear06.menu.AndroidMenu;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.network.container.ContainerData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(CCAndroids.MOD_ID)
public final class CCAndroids {
    public static final String MOD_ID = "cc_androids";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static CCAndroidsConfig CONFIG;

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final DeferredItem<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties().durability(100)));
    public static final DeferredItem<Item> COMPONENTS = ITEMS.registerSimpleItem("components");
    public static final DeferredItem<Item> ANDROID_CPU = ITEMS.registerSimpleItem("android_cpu");
    public static final DeferredItem<Item> REDSTONE_REACTOR = ITEMS.registerSimpleItem("redstone_reactor");
    public static final DeferredItem<Item> ANDROID_FRAME = ITEMS.register("android_frame", () -> new AndroidFrameItem(new Item.Properties()));

    public static final DeferredHolder<EntityType<?>, EntityType<AndroidEntity>> ANDROID = ENTITY_TYPES.register("android", () -> EntityType.Builder.of(AndroidEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(8).build("android"));
    public static final DeferredHolder<EntityType<?>, EntityType<AdvancedAndroidEntity>> ADVANCED_ANDROID = ENTITY_TYPES.register("advanced_android", () -> EntityType.Builder.of(AdvancedAndroidEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(8).build("advanced_android"));
    public static final DeferredHolder<EntityType<?>, EntityType<CommandAndroidEntity>> COMMAND_ANDROID = ENTITY_TYPES.register("command_android", () -> EntityType.Builder.of(CommandAndroidEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(8).build("command_android"));
    public static final DeferredHolder<EntityType<?>, EntityType<AndroidFrame>> UNFINISHED_ANDROID = ENTITY_TYPES.register("unfinished_android", () -> EntityType.Builder.of(AndroidFrame::new, MobCategory.MISC).sized(0.6F, 1.95F).clientTrackingRange(8).build("unfinished_android"));
    public static final DeferredHolder<EntityType<?>, EntityType<RogueDroidEntity>> ROGUE_ANDROID = ENTITY_TYPES.register("rogue_android", () -> EntityType.Builder.of(RogueDroidEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("rogue_android"));

    public static final DeferredItem<SpawnEggItem> ANDROID_SPAWN = ITEMS.register("android_spawn", () -> new SpawnEggItem(ANDROID.get(), 0xb2b2b2, 0x8a8c8b, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> ADVANCED_ANDROID_SPAWN = ITEMS.register("android_advanced_spawn", () -> new SpawnEggItem(ADVANCED_ANDROID.get(), 0xb2b2b2, 0xa5a333, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> COMMAND_ANDROID_SPAWN = ITEMS.register("android_command_spawn", () -> new SpawnEggItem(COMMAND_ANDROID.get(), 0xfc9e46, 0x9b5c22, new Item.Properties()));
    public static final DeferredItem<SpawnEggItem> ROGUE_ANDROID_SPAWN = ITEMS.register("android_rogue_spawn", () -> new SpawnEggItem(ROGUE_ANDROID.get(), 0xf41818, 0x9b2222, new Item.Properties()));

    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_AMBIENT = sound("android_ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_HURT = sound("android_hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> ANDROID_DEATH = sound("android_death");

    public static final DeferredHolder<MenuType<?>, MenuType<AndroidMenu>> ANDROID_MENU = MENUS.register("android",
            () -> ContainerData.toType(ComputerContainerData.STREAM_CODEC, AndroidMenu::ofData));

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
            }).build());

    public CCAndroids(IEventBus modEventBus) {
        CONFIG = ConfigLoader.loadConfig(MOD_ID, new CCAndroidsConfig());
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        MENUS.register(modEventBus);
        ComputerCraftAPI.registerAPIFactory(computer -> {
            var brain = computer.getComponent(com.thunderbear06.component.ComputerComponents.ANDROID_COMPUTER);
            return brain == null ? null : new AndroidAPI(brain);
        });
        LOGGER.info("Loaded CC: Androids for NeoForge");
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
            event.put(ANDROID.get(), AndroidEntity.createAndroidAttributes().build());
            event.put(ADVANCED_ANDROID.get(), AdvancedAndroidEntity.createAndroidAttributes().build());
            event.put(COMMAND_ANDROID.get(), CommandAndroidEntity.createAndroidAttributes().build());
            event.put(UNFINISHED_ANDROID.get(), AndroidFrame.createAttributes().build());
            event.put(ROGUE_ANDROID.get(), RogueDroidEntity.createAndroidAttributes().build());
        }
    }
}
