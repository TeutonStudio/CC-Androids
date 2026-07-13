package com.thunderbear06.ai.task

class TaskManager {
    private var currentTask: Task? = null

    fun setCurrentTask(task: Task) {
        clearCurrentTask()
        currentTask = task
        task.firstTick()
    }

    fun getCurrentTaskName(): String = currentTask?.name ?: "idle"

    fun clearCurrentTask() {
        val task = currentTask ?: return
        task.lastTick()
        currentTask = null
    }

    fun tick() {
        val task = currentTask ?: return
        if (task.shouldTick()) task.tick() else clearCurrentTask()
    }

    fun isIdle(): Boolean = currentTask == null
}
