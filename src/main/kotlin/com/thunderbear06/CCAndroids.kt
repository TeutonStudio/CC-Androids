package com.thunderbear06

import com.mojang.logging.LogUtils
import com.thunderbear06.component.ComputerComponents
import com.thunderbear06.computer.EntityComputer
import com.thunderbear06.computer.api.AndroidAPI
import com.thunderbear06.config.CCAndroidsConfig
import com.thunderbear06.config.ConfigLoader
import com.thunderbear06.entity.android.AdvancedAndroidEntity
import com.thunderbear06.entity.android.AndroidEntity
import com.thunderbear06.entity.android.CommandAndroidEntity
import com.thunderbear06.entity.android.RogueDroidEntity
import com.thunderbear06.entity.android.frame.AndroidFrame
import com.thunderbear06.item.AndroidFrameItem
import com.thunderbear06.item.WrenchItem
import com.thunderbear06.menu.AndroidMenu
import com.thunderbear06.recipe.RecipeRegistry
import dan200.computercraft.api.ComputerCraftAPI
import dan200.computercraft.shared.network.container.ComputerContainerData
import dan200.computercraft.shared.network.container.ContainerData
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.entity.SpawnPlacementTypes
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.Item
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.event.ServerChatEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.slf4j.Logger
import java.util.function.Supplier

@Mod(CCAndroids.MOD_ID)
class CCAndroids(modEventBus: IEventBus) {
    init {
        CONFIG = ConfigLoader.loadConfig(MOD_ID, CCAndroidsConfig())
        ITEMS.register(modEventBus)
        ENTITY_TYPES.register(modEventBus)
        CREATIVE_TABS.register(modEventBus)
        SOUND_EVENTS.register(modEventBus)
        MENUS.register(modEventBus)
        RecipeRegistry.SERIALIZERS.register(modEventBus)
        ComputerCraftAPI.registerAPIFactory { computer ->
            val brain = computer.getComponent(ComputerComponents.ANDROID_COMPUTER)
            if (brain == null) null else AndroidAPI(brain)
        }
        LOGGER.info("Loaded CC: Androids for NeoForge")
    }

    companion object {
        const val MOD_ID: String = "cc_androids"

        @JvmField
        val LOGGER: Logger = LogUtils.getLogger()

        @JvmField
        var CONFIG: CCAndroidsConfig = CCAndroidsConfig()

        @JvmField
        val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MOD_ID)

        @JvmField
        val ENTITY_TYPES: DeferredRegister<EntityType<*>> = DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID)

        @JvmField
        val CREATIVE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID)

        @JvmField
        val SOUND_EVENTS: DeferredRegister<SoundEvent> = DeferredRegister.create(Registries.SOUND_EVENT, MOD_ID)

        @JvmField
        val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, MOD_ID)

        @JvmField
        val WRENCH: DeferredItem<Item> = ITEMS.register("wrench", Supplier { WrenchItem(Item.Properties().durability(100)) })

        @JvmField
        val COMPONENTS: DeferredItem<Item> = ITEMS.registerSimpleItem("components")

        @JvmField
        val ANDROID_CPU: DeferredItem<Item> = ITEMS.registerSimpleItem("android_cpu")

        @JvmField
        val REDSTONE_REACTOR: DeferredItem<Item> = ITEMS.registerSimpleItem("redstone_reactor")

        @JvmField
        val ANDROID_FRAME: DeferredItem<Item> = ITEMS.register("android_frame", Supplier { AndroidFrameItem(Item.Properties()) })

        @JvmField
        val ANDROID: DeferredHolder<EntityType<*>, EntityType<AndroidEntity>> =
            ENTITY_TYPES.register("android", Supplier {
                EntityType.Builder.of(::AndroidEntity, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("android")
            })

        @JvmField
        val ADVANCED_ANDROID: DeferredHolder<EntityType<*>, EntityType<AdvancedAndroidEntity>> =
            ENTITY_TYPES.register("advanced_android", Supplier {
                EntityType.Builder.of(::AdvancedAndroidEntity, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("advanced_android")
            })

        @JvmField
        val COMMAND_ANDROID: DeferredHolder<EntityType<*>, EntityType<CommandAndroidEntity>> =
            ENTITY_TYPES.register("command_android", Supplier {
                EntityType.Builder.of(::CommandAndroidEntity, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("command_android")
            })

        @JvmField
        val UNFINISHED_ANDROID: DeferredHolder<EntityType<*>, EntityType<AndroidFrame>> =
            ENTITY_TYPES.register("unfinished_android", Supplier {
                EntityType.Builder.of(::AndroidFrame, MobCategory.MISC)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("unfinished_android")
            })

        @JvmField
        val ROGUE_ANDROID: DeferredHolder<EntityType<*>, EntityType<RogueDroidEntity>> =
            ENTITY_TYPES.register("rogue_android", Supplier {
                EntityType.Builder.of(::RogueDroidEntity, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(8)
                    .build("rogue_android")
            })

        @JvmField
        val ANDROID_SPAWN: DeferredItem<SpawnEggItem> =
            ITEMS.register("android_spawn", Supplier { SpawnEggItem(ANDROID.get(), 0xb2b2b2, 0x8a8c8b, Item.Properties()) })

        @JvmField
        val ADVANCED_ANDROID_SPAWN: DeferredItem<SpawnEggItem> =
            ITEMS.register("android_advanced_spawn", Supplier { SpawnEggItem(ADVANCED_ANDROID.get(), 0xb2b2b2, 0xa5a333, Item.Properties()) })

        @JvmField
        val COMMAND_ANDROID_SPAWN: DeferredItem<SpawnEggItem> =
            ITEMS.register("android_command_spawn", Supplier { SpawnEggItem(COMMAND_ANDROID.get(), 0xfc9e46, 0x9b5c22, Item.Properties()) })

        @JvmField
        val ROGUE_ANDROID_SPAWN: DeferredItem<SpawnEggItem> =
            ITEMS.register("android_rogue_spawn", Supplier { SpawnEggItem(ROGUE_ANDROID.get(), 0xf41818, 0x9b2222, Item.Properties()) })

        @JvmField
        val ANDROID_AMBIENT: DeferredHolder<SoundEvent, SoundEvent> = sound("android_ambient")

        @JvmField
        val ANDROID_HURT: DeferredHolder<SoundEvent, SoundEvent> = sound("android_hurt")

        @JvmField
        val ANDROID_DEATH: DeferredHolder<SoundEvent, SoundEvent> = sound("android_death")

        @JvmField
        val ANDROID_MENU: DeferredHolder<MenuType<*>, MenuType<AndroidMenu>> =
            MENUS.register("android", Supplier {
                ContainerData.toType(ComputerContainerData.STREAM_CODEC, AndroidMenu::ofData)
            })

        @JvmField
        val ANDROIDS_TAB: DeferredHolder<CreativeModeTab, CreativeModeTab> =
            CREATIVE_TABS.register("androids_item_group", Supplier {
                CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cc_androids.android_item_group"))
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .icon { WRENCH.get().defaultInstance }
                    .displayItems { _, output ->
                        output.accept(WRENCH.get())
                        output.accept(COMPONENTS.get())
                        output.accept(ANDROID_CPU.get())
                        output.accept(REDSTONE_REACTOR.get())
                        output.accept(ANDROID_FRAME.get())
                        output.accept(ANDROID_SPAWN.get())
                        output.accept(ADVANCED_ANDROID_SPAWN.get())
                        output.accept(COMMAND_ANDROID_SPAWN.get())
                        output.accept(ROGUE_ANDROID_SPAWN.get())
                    }.build()
            })

        private fun sound(name: String): DeferredHolder<SoundEvent, SoundEvent> {
            val id = ResourceLocation.fromNamespaceAndPath(MOD_ID, name)
            return SOUND_EVENTS.register(name, Supplier { SoundEvent.createVariableRangeEvent(id) })
        }
    }

    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    object ModEvents {
        @SubscribeEvent
        @JvmStatic
        fun registerAttributes(event: EntityAttributeCreationEvent) {
            event.put(ANDROID.get(), AndroidEntity.createAndroidAttributes().build())
            event.put(ADVANCED_ANDROID.get(), AdvancedAndroidEntity.createAndroidAttributes().build())
            event.put(COMMAND_ANDROID.get(), CommandAndroidEntity.createAndroidAttributes().build())
            event.put(UNFINISHED_ANDROID.get(), AndroidFrame.createAttributes().build())
            event.put(ROGUE_ANDROID.get(), RogueDroidEntity.createAndroidAttributes().build())
        }

        @SubscribeEvent
        @JvmStatic
        fun registerSpawnPlacements(event: RegisterSpawnPlacementsEvent) {
            event.register(
                ROGUE_ANDROID.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                { type, level, reason, pos, random ->
                    CONFIG.RoguesSpawnNaturally &&
                        level.getMaxLocalRawBrightness(pos) < 4 &&
                        Monster.checkAnyLightMonsterSpawnRules(type, level, reason, pos, random)
                },
                RegisterSpawnPlacementsEvent.Operation.REPLACE,
            )
        }
    }

    @EventBusSubscriber(modid = MOD_ID)
    object GameEvents {
        private const val ANDROID_CHAT_RADIUS = 50.0

        @SubscribeEvent
        @JvmStatic
        fun onServerChat(event: ServerChatEvent) {
            val area: AABB = event.player.boundingBox.inflate(ANDROID_CHAT_RADIUS)
            for (android in event.player.level().getEntitiesOfClass(
                AndroidEntity::class.java,
                area,
                { android -> android.isAlive && android.distanceToSqr(event.player) <= ANDROID_CHAT_RADIUS * ANDROID_CHAT_RADIUS },
            )) {
                val computer: EntityComputer? = android.getComputer().serverComputer
                if (computer != null && computer.isOn) {
                    computer.queueEvent(
                        "chat_message",
                        arrayOf(event.rawText, event.username, event.player.stringUUID),
                    )
                }
            }
        }
    }
}
