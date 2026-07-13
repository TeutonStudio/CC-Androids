package com.thunderbear06.ai.modules

import com.thunderbear06.ai.AndroidBrain
import com.thunderbear06.entity.android.BaseAndroidEntity

abstract class AbstractAndroidModule(
    @JvmField
    protected val android: BaseAndroidEntity,
    @JvmField
    protected val brain: AndroidBrain,
)
