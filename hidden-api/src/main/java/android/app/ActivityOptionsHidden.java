package android.app;

import android.os.Bundle;

import dev.rikka.tools.refine.RefineAs;

@RefineAs(ActivityOptions.class)
public class ActivityOptionsHidden {
    public int getCallerDisplayId() {
        throw new RuntimeException("Stub!");
    }

    public ActivityOptions setCallerDisplayId(int callerDisplayId) {
        throw new RuntimeException("Stub!");
    }

    public Bundle toBundle() {
        throw new RuntimeException("Stub!");
    }

}
