package com.thunderbear06.computer.peripherals;

import com.thunderbear06.entity.android.BaseAndroidEntity;

/**
 * Pocket-upgrade bridging from the old port is intentionally deferred.
 * Android computers still run and expose the android API; hand-held pocket peripherals can be restored here later.
 */
public record DummyPocket(BaseAndroidEntity android) {
}
