package tesla.andrew.movieloader.data.datasource;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import android.os.Environment;
import android.os.RecoverySystem;

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

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import tesla.andrew.movieloader.data.entity.DownloadProgress;
import tesla.andrew.movieloader.presentation.application.App;

/**
 * Created by TESLA on 28.07.2017.
 */

public class DataSourceImpl implements DataSource {
    public DataSourceImpl() {
        App.getAppComponent().injectDatSource(this);
    }

    @Override
    public Flowable<DownloadProgress<File>> downloadFile(String fileName) {
        return null;
    }

    public Flowable<DownloadProgress<File>> downloadFile2(String fileName, @NonNull final File saveLocation){
        return Flowable.create(emitter -> {
            Request request = new Request.Builder()
                    .url(fileName)
                    .build();

            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            final Call call = client.newCall(request);
            emitter.setCancellable(() -> call.cancel());

            okhttp3.Response response = call.execute();
            ResponseBody body = response.body();
            final long contentLength = body.contentLength();

            ForwardingSource forwardingSource = new ForwardingSource(body.source()) {
                private long totalBytesRead = 0L;

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    boolean done = bytesRead == -1;
                    float progress = done ? 1f : (float) totalBytesRead / contentLength;
                    emitter.onNext(new DownloadProgress<>(progress));
                    return bytesRead;
                }
            };

            try {
                saveLocation.getParentFile().mkdirs();
                BufferedSink sink = Okio.buffer(Okio.sink(saveLocation));
                sink.writeAll(forwardingSource);
                sink.close();
                emitter.onNext(new DownloadProgress<>(saveLocation));
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }

        }, BackpressureStrategy.LATEST);
    }
}