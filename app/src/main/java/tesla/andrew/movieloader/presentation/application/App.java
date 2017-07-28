package tesla.andrew.movieloader.presentation.application;

import android.app.Application;

import tesla.andrew.movieloader.presentation.di.AppComponent;
import tesla.andrew.movieloader.presentation.di.AppModule;
import tesla.andrew.movieloader.presentation.di.DaggerAppComponent;

/**
 * Created by TESLA on 28.07.2017.
 */

public class App extends Application {

    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        sAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
