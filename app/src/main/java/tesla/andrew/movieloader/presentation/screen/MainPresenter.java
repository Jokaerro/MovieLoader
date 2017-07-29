package tesla.andrew.movieloader.presentation.screen;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
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
import tesla.andrew.movieloader.data.datasource.DataSourceImpl;
import tesla.andrew.movieloader.data.entity.DownloadProgress;
import tesla.andrew.movieloader.presentation.application.App;
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
        String filename = "big_buck_bunny_720p_5mb.mp4";
        String url = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_5mb.mp4";
        File saveLocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsoluteFile(), filename);
        mDataSource.downloadFile2(url, saveLocation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(fileDownloadProgress -> {
                    float progress = fileDownloadProgress.getProgress();
                    Log.e("", ">>>" + (int)progress * 100);
                    updateDownloadState((int)progress * 100);
                })
                .filter(DownloadProgress::isDone)
                .map(DownloadProgress::getData)
                .subscribe(file -> {
                    // file downloaded
                    updateDownloadState(100);
                }, throwable -> {
                    // error
                    mView.makeMessage("Something wrong, please try later");
                });
//        final String fileName = "big_buck_bunny_720p_5mb.mp4";
//        mDataSource.downloadFile(fileName)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnNext(fileDownloadProgress -> {
//                    float progress = fileDownloadProgress.getProgress();
//                    Log.e("", ">>>" + (int)(progress * 100));
//                    updateDownloadState((int)(progress * 100));
//                })
//                .filter(DownloadProgress::isDone)
//                .map(DownloadProgress::getData)
//                .subscribe(file -> {
//                    updateDownloadState(100);
//                }, throwable -> {
//                    mView.makeMessage("Something wrong, please try later");
//                });
    }

    public void updateDownloadState(int state) {
        if(state < 100)
            mView.updateProgressState(state);
        else {
            mView.makeMessage("Download complete");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
