package com.thunderbear06.component;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import dan200.computercraft.api.component.ComputerComponent;

public final class ComputerComponents {
    public static final ComputerComponent<AndroidBrain> ANDROID_COMPUTER = ComputerComponent.create(CCAndroids.MOD_ID, "android");

    private ComputerComponents() {
    }
}
