package tesla.andrew.movieloader.data.datasource;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Response;
import tesla.andrew.movieloader.data.api.RestApi;
import tesla.andrew.movieloader.presentation.application.App;

/**
 * Created by TESLA on 28.07.2017.
 */

public class DataSourceImpl implements DataSource {
    @Inject
    public RestApi restApi;

    public DataSourceImpl() {
        App.getAppComponent().injectDatSource(this);
    }

    @Override
    public Observable<Response<ResponseBody>> downloadFile(String fileName) {
        restApi.downloadFile(fileName)
                .flatMap(new Function<Response<ResponseBody>, Observable<File>>() {
                    @Override
                    public Observable<File> apply(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {
                        return null;
                    }
                });
        return null;
    }
}
