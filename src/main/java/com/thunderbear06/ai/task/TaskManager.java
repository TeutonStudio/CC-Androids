package com.thunderbear06.ai.task;

public class TaskManager {
    private Task currentTask;

    public void setCurrentTask(Task task) {
        clearCurrentTask();
        currentTask = task;
        currentTask.firstTick();
    }

    public String getCurrentTaskName() {
        return currentTask == null ? "idle" : currentTask.getName();
    }

    public void clearCurrentTask() {
        if (currentTask == null) return;
        currentTask.lastTick();
        currentTask = null;
    }

    public void tick() {
        if (currentTask == null) return;
        if (currentTask.shouldTick()) currentTask.tick();
        else clearCurrentTask();
    }

    public boolean isIdle() {
        return currentTask == null;
    }
}
