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
}
