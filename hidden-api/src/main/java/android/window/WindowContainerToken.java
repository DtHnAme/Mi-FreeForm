package android.window;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

public final class WindowContainerToken implements Parcelable {

    private WindowContainerToken(Parcel in) {
    }

    public IBinder asBinder() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }

    public static final Creator<WindowContainerToken> CREATOR =
            new Creator<WindowContainerToken>() {
                @Override
                public WindowContainerToken createFromParcel(Parcel in) {
                    return new WindowContainerToken(in);
                }

                @Override
                public WindowContainerToken[] newArray(int size) {
                    return new WindowContainerToken[size];
                }
            };

    @Override
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public int hashCode() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public String toString() {
        throw new RuntimeException("Stub!");
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        throw new RuntimeException("Stub!");
    }
}
