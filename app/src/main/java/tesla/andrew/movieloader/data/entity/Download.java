package tesla.andrew.movieloader.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TESLA on 29.07.2017.
 */

public class Download{
    private int progress;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public Download() {
    }
}
