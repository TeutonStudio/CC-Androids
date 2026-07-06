package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public abstract class AbstractAndroidModule {
    protected final BaseAndroidEntity android;
    protected final AndroidBrain brain;

    protected AbstractAndroidModule(BaseAndroidEntity android, AndroidBrain brain) {
        this.android = android;
        this.brain = brain;
    }
}
