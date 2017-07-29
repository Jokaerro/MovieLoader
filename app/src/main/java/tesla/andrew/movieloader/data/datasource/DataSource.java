package tesla.andrew.movieloader.data.datasource;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import tesla.andrew.movieloader.data.entity.DownloadProgress;

/**
 * Created by TESLA on 28.07.2017.
 */

public interface DataSource {
    Flowable<DownloadProgress<File>> downloadFile(String fileName);
}
