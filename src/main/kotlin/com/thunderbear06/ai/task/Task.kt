package com.thunderbear06.ai.task

import com.thunderbear06.entity.android.AndroidEntity

abstract class Task(@JvmField protected val android: AndroidEntity) {
    abstract val name: String
    abstract fun shouldTick(): Boolean
    abstract fun firstTick()
    abstract fun tick()
    abstract fun lastTick()
}
