package com.thunderbear06.ai.modules;

import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.entity.android.BaseAndroidEntity;

public class AndroidModules {
    public final NavigationModule navigationModule;
    public final MiningModule miningModule;
    public final SensorModule sensorModule;
    public final InteractionModule interactionModule;

    public AndroidModules(BaseAndroidEntity android, AndroidBrain brain) {
        navigationModule = new NavigationModule(android, brain);
        miningModule = new MiningModule(android, brain);
        sensorModule = new SensorModule(android, brain, android.getEntitySearchRadius(), android.getBlockSearchRadius());
        interactionModule = new InteractionModule(android, brain);
    }
}
