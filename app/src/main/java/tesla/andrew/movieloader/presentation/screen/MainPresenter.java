package tesla.andrew.movieloader.presentation.screen;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import retrofit2.Response;
import tesla.andrew.movieloader.data.api.DownloadProgressListener;
import tesla.andrew.movieloader.data.api.RestApi;
import tesla.andrew.movieloader.data.api.RestApiCreator;
import tesla.andrew.movieloader.data.datasource.DataSourceImpl;
import tesla.andrew.movieloader.data.entity.Download;
import tesla.andrew.movieloader.data.entity.DownloadProgress;
import tesla.andrew.movieloader.presentation.application.App;
import tesla.andrew.movieloader.presentation.application.Config;
import tesla.andrew.movieloader.presentation.screen.base.BasePresenter;

/**
 * Created by TESLA on 28.07.2017.
 */

public class MainPresenter extends BasePresenter<MainView> {

    @Inject
    public DataSourceImpl mDataSource;

    public MainPresenter(){
        App.getAppComponent().injectMainPresenter(this);
    }

    public void startDownloadFile() {
        final  String fileName = "big_buck_bunny_720p_5mb.mp4";

        final File outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_MOVIES), fileName);

        RestApiCreator.create(Config.API_BASE_URL)
                .create(RestApi.class)
                .downloadFile2(fileName)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(@NonNull ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(@NonNull InputStream inputStream) throws Exception {
                        try {
                            OutputStream out = null;
                            out = new FileOutputStream(outputFile);
                            byte[] buf = new byte[1024];
                            int len;
                            while((len=inputStream.read(buf))>0){
                                out.write(buf,0,len);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<InputStream>() {
                    @Override
                    public void accept(@NonNull InputStream inputStream) throws Exception {
//                        updateDownloadState(100);
                    }
                });
    }

    public void updateDownloadState(int state) {
        if(state < 100)
            mView.updateProgressState(state);
        else {
            mView.hideDownloadDialog();
            mView.makeMessage("Download complete");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
