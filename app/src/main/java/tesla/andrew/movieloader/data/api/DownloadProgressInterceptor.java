package tesla.andrew.movieloader.data.api;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by TESLA on 29.07.2017.
 */

public class DownloadProgressInterceptor implements Interceptor {
    public DownloadProgressInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body()))
                .build();
    }
}
