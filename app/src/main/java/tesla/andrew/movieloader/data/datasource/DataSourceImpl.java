package tesla.andrew.movieloader.data.datasource;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Function;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import retrofit2.Response;
import tesla.andrew.movieloader.data.api.RestApi;
import tesla.andrew.movieloader.data.entity.DownloadProgress;
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
    public Flowable<DownloadProgress<File>> downloadFile(String fileName) {
        return restApi.downloadFile(fileName)
                .switchMap(responseBodyResponse -> Flowable.create(emitter -> {
                    ResponseBody body = responseBodyResponse.body();
                    final long contentLength = body.contentLength();

                    ForwardingSource forwardingSource = new ForwardingSource(body.source()) {
                        private long totalBytesRead = 0L;

                        @Override
                        public long read(Buffer sink, long byteCount) throws IOException {
                            long bytesRead = super.read(sink, byteCount);
                            totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                            boolean done = bytesRead == -1;
                            float progress = done ? 1f : (float) bytesRead / contentLength;
                            emitter.onNext(new DownloadProgress<>(progress));

                            return bytesRead;
                        }
                    };
                    emitter.setCancellable(body::close);
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
                }, BackpressureStrategy.LATEST));
    }
//    @Override
//    public Flowable<DownloadProgress<File>> downloadFile(final String fileName) {
//        return restApi.downloadFile(fileName)
//                .switchMap(new Function<Response, Flowable<DownloadProgress<File>>>() {
//                    @Override
//                    public Flowable<DownloadProgress<File>> apply(@NonNull final Response response) throws Exception {
//                        return Flowable.create(new FlowableOnSubscribe<DownloadProgress<File>>() {
//                            @Override
//                            public void subscribe(final FlowableEmitter<DownloadProgress<File>> emitter) {
//                            final ResponseBody body = response.raw().body();
//                            final long contentLength = body.contentLength();
//                            ForwardingSource forwardingSource = new ForwardingSource(body.source()) {
//                                private long totalBytesRead = 0L;
//
//                                @Override
//                                public long read(Buffer sink, long byteCount) throws IOException {
//                                    long bytesRead = super.read(sink, byteCount);
//                                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
//                                    boolean done = bytesRead == -1;
//                                    float progress = done ? 1f : (float) bytesRead / contentLength;
//                                    emitter.onNext(new DownloadProgress<File>(progress));
//                                    return bytesRead;
//                                }
//                            };
//                            emitter.setCancellable(new Cancellable() {
//                                @Override
//                                public void cancel() throws Exception {
//                                    body.close();
//                                }
//                            });
//                            try {
//                                File saveLocation = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), fileName);
//                                saveLocation.getParentFile().mkdirs();
//                                BufferedSink sink = Okio.buffer(Okio.sink(saveLocation));
//                                sink.writeAll(forwardingSource);
//                                sink.close();
//                                emitter.onNext(new DownloadProgress<>(saveLocation));
//                                emitter.onComplete();
//                            } catch (IOException e) {
//                                emitter.onError(e);
//                            }}
//                        }, BackpressureStrategy.LATEST);
//                    }
//                });
//    }


//    private final SimpleEventBus eventBus;
//
//    @Override
//    public Observable<File> downloadFile(final String fileName, final Subscriber<ProgressEvent> observeProgress) {
//
//        final Subscription progressSubscription = eventBus.filteredObservable(ProgressEvent.class)
//                .subscribe(new Subscriber<ProgressEvent>() {
//                    @Override
//                    public void onComplete() {
//                        observeProgress.onComplete();
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        observeProgress.onError(e);
//                    }
//
//                    @Override
//                    public void onSubscribe(Subscription s) {
//
//                    }
//
//                    @Override
//                    public void onNext(ProgressEvent progressEvent) {
//                        // We check if this event contains our identifier
//                        if(progressEvent.getDownloadIdentifier().equals(fileName)) {
//                            observeProgress.onNext(progressEvent); // notify listener
//                        }
//                    }
//                });
//
//        return restApi.downloadFile(fileName)
//                .flatMap(new Function<Response<ResponseBody>, Observable<File>>() {
//                    @Override
//                    public Observable<File> apply(@NonNull Response<ResponseBody> response) throws Exception {
//                        try {
//                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile(), fileName);
//                            BufferedSink sink = Okio.buffer(Okio.sink(file));
//                            sink.writeAll(response.body().source());
//                            sink.close();
//
//                            return Observable.just(file);
//                        } catch (IOException e) {
//                            return Observable.error(e);
//                        }
//                    }
//                });
//    }
}
