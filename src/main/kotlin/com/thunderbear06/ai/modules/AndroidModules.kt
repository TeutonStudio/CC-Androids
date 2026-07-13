package com.thunderbear06.ai.modules

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.entity.android.BaseAndroidEntity

class AndroidModules(android: BaseAndroidEntity, brain: AndroidBrain) {
    @JvmField val navigationModule: NavigationModule = NavigationModule(android, brain)
    @JvmField val miningModule: MiningModule = MiningModule(android, brain)
    @JvmField val sensorModule: SensorModule = SensorModule(android, brain, android.entitySearchRadius, android.blockSearchRadius)
    @JvmField val interactionModule: InteractionModule = InteractionModule(android, brain)
}
