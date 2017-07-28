package tesla.andrew.movieloader.presentation.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tesla.andrew.movieloader.presentation.screen.MainPresenter;

/**
 * Created by TESLA on 28.07.2017.
 */
@Module
public class PresenterModule {
    @Provides
    @Singleton
    MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }
}
