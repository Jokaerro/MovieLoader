package tesla.andrew.movieloader.data.api;

import java.io.File;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import tesla.andrew.movieloader.data.entity.DownloadProgress;

/**
 * Created by TESLA on 28.07.2017.
 */

//http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4
public interface RestApi {
    @GET("video/mp4/720/{file}")
    Flowable<Response<ResponseBody>> downloadFile(@Path("file") String fileName);

    //Observable<retrofit2.Response<ResponseBody>> downloadFile(@Path("file") String fileName);
}
