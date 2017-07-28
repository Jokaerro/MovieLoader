package tesla.andrew.movieloader.data.datasource;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by TESLA on 28.07.2017.
 */

public interface DataSource {
    Observable<Response<ResponseBody>> downloadFile(String fileName);
}
