package tesla.andrew.movieloader.presentation.screen;

/**
 * Created by TESLA on 28.07.2017.
 */

public interface MainView {
    void makeMessage(String message);
    void updateProgressState(int state);
    void hideDownloadDialog();
}
