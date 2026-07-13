package com.thunderbear06.component

import com.thunderbear06.CCAndroids
import com.thunderbear06.ai.AndroidBrain
import dan200.computercraft.api.component.ComputerComponent

object ComputerComponents {
    @JvmField val ANDROID_COMPUTER: ComputerComponent<AndroidBrain> = ComputerComponent.create(CCAndroids.MOD_ID, "android")
}
