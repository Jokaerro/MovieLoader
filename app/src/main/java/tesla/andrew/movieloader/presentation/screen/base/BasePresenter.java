package tesla.andrew.movieloader.presentation.screen.base;

/**
 * Created by TESLA on 28.07.2017.
 */

public class BasePresenter<View> {
    protected View mView;

    public void setView(View view) {
        mView = view;
    }

    public void destroy() {
        mView = null;
    }
}
