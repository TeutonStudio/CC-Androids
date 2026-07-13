package com.thunderbear06.config

class CCAndroidsConfig : ConfigLoader.ConfigState {
    @JvmField var DebugLogging: Boolean = false
    @JvmField var CompsForConstruction: Byte = 8
    @JvmField var IngotsForConstruction: Byte = 10
    @JvmField var CompsDroppedOnDeathPercentage: Float = 0.5f
    @JvmField var IngotsDroppedOnDeathPercentage: Float = 0.5f
    @JvmField var AndroidMaxHealth: Float = 20.0f
    @JvmField var AndroidDamage: Float = 1.0f
    @JvmField var AndroidSpeed: Float = 0.6f
    @JvmField var AndroidArmor: Float = 0.0f
    @JvmField var AdvAndroidMaxHealth: Float = 25.0f
    @JvmField var AdvAndroidDamage: Float = 3.0f
    @JvmField var AdvAndroidSpeed: Float = 0.9f
    @JvmField var AdvAndroidArmor: Float = 3.0f
    @JvmField var ComAndroidMaxHealth: Float = 25.0f
    @JvmField var ComAndroidDamage: Float = 3.0f
    @JvmField var ComAndroidSpeed: Float = 0.9f
    @JvmField var ComAndroidArmor: Float = 3.0f
    @JvmField var RogueMaxHealth: Float = 25.0f
    @JvmField var RogueDamage: Float = 5.0f
    @JvmField var RogueSpeed: Float = 0.6f
    @JvmField var RogueArmor: Float = 5.0f
    @JvmField var RoguesSpawnNaturally: Boolean = true
    @JvmField var RoguesSpawnWithTools: Boolean = true
}
