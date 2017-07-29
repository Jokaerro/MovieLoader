package tesla.andrew.movieloader.data.api;

/**
 * Created by TESLA on 29.07.2017.
 */

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
