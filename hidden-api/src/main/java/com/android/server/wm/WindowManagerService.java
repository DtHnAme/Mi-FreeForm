package com.android.server.wm;

import android.view.IRotationWatcher;

public class WindowManagerService {
    public int watchRotation(IRotationWatcher watcher, int displayId) {
        throw new RuntimeException("Stub!");
    }

    public void removeRotationWatcher(IRotationWatcher watcher) {
        throw new RuntimeException("Stub!");
    }

    //Only support A12+
    public void setDisplayImePolicy(int displayId, int imePolicy) {
        throw new RuntimeException("Stub!");
    }

    public void freezeDisplayRotation(int displayId, int rotation) {
        throw new RuntimeException("Stub!");
    }

}
