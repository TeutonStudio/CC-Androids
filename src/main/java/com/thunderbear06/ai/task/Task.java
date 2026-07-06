package com.thunderbear06.ai.task;

import com.thunderbear06.entity.android.AndroidEntity;

public abstract class Task {
    protected final AndroidEntity android;

    protected Task(AndroidEntity android) {
        this.android = android;
    }

    public abstract String getName();
    public abstract boolean shouldTick();
    public abstract void firstTick();
    public abstract void tick();
    public abstract void lastTick();
}
