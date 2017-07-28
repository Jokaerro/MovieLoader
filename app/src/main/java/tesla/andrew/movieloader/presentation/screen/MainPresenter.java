package tesla.andrew.movieloader.presentation.screen;

import android.os.Environment;

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
        final  String fileName = "big_buck_bunny_720p_5mb.mp4";
        mDataSource.downloadFile(fileName)
                .switchMap(new Function<Response<ResponseBody>, Flowable<DownloadProgress<File>>>() {
                    @Override
                    public Flowable<DownloadProgress<File>> apply(@NonNull final Response<ResponseBody> response) throws Exception {
                        return Flowable.create(new FlowableOnSubscribe<DownloadProgress<File>>() {
                            @Override
                            public void subscribe(final FlowableEmitter<DownloadProgress<File>> emitter) throws Exception {

                                final ResponseBody body = response.body();
                                final long contentLength = body.contentLength();
                                ForwardingSource forwardingSource = new ForwardingSource(body.source()) {
                                    private long totalBytesRead = 0L;

                                    @Override
                                    public long read(Buffer sink, long byteCount) throws IOException {
                                        long bytesRead = super.read(sink, byteCount);
                                        // read() returns the number of bytes read, or -1 if this source is exhausted.
                                        totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                                        boolean done = bytesRead == -1;
                                        float progress = done ? 1f : (float) totalBytesRead / contentLength;
                                        emitter.onNext(new DownloadProgress<File>(progress));
                                        return bytesRead;
                                    }
                                };
                                emitter.setCancellable(new Cancellable() {
                                    @Override
                                    public void cancel() throws Exception {
                                        body.close();
                                    }
                                });
                                try {
                                    File saveLocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsoluteFile(), fileName);
                                    saveLocation.getParentFile().mkdirs();
                                    BufferedSink sink = Okio.buffer(Okio.sink(saveLocation));
                                    sink.writeAll(forwardingSource);
                                    sink.close();
                                    emitter.onNext(new DownloadProgress<>(saveLocation));
                                    emitter.onComplete();
                                } catch (IOException e) {
                                    emitter.onError(e);
                                }
                            }
                        }, BackpressureStrategy.LATEST);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<DownloadProgress<File>>() {
                    @Override
                    public void accept(@NonNull DownloadProgress<File> fileDownloadProgress) throws Exception {
                        updateDownloadState((int) (fileDownloadProgress.getProgress() * 100));
                    }
                })
                .filter(new Predicate<DownloadProgress<File>>() {
                    @Override
                    public boolean test(@NonNull DownloadProgress<File> fileDownloadProgress) throws Exception {
                        return fileDownloadProgress.isDone();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<DownloadProgress<File>>() {
                    @Override
                    public void accept(@NonNull DownloadProgress<File> fileDownloadProgress) throws Exception {
                        updateDownloadState((int) (fileDownloadProgress.getProgress() * 100));
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
