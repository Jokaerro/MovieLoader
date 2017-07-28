package tesla.andrew.movieloader.presentation.di;

import javax.inject.Singleton;

import dagger.Component;
import tesla.andrew.movieloader.data.datasource.DataSourceImpl;
import tesla.andrew.movieloader.presentation.screen.MainActivity;
import tesla.andrew.movieloader.presentation.screen.MainPresenter;

/**
 * Created by TESLA on 28.07.2017.
 */
@Singleton
@Component(modules = {PresenterModule.class, DataModule.class, AppModule.class})
public interface AppComponent {
    void injectMainActivity(MainActivity activity);
    void injectMainPresenter(MainPresenter mainPresenter);
    void injectDatSource(DataSourceImpl dataSource);
}
