package tesla.andrew.movieloader.presentation.screen;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Response;
import tesla.andrew.movieloader.data.datasource.DataSourceImpl;
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
        mDataSource.downloadFile("big_buck_bunny_720p_5mb.mp4")
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(@NonNull Response<ResponseBody> responseBodyResponse) throws Exception {

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
