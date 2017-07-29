package tesla.andrew.movieloader.presentation.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tesla.andrew.movieloader.data.api.RestApi;
import tesla.andrew.movieloader.data.api.RestApiCreator;
import tesla.andrew.movieloader.data.datasource.DataSourceImpl;
import tesla.andrew.movieloader.presentation.application.Config;

/**
 * Created by TESLA on 28.07.2017.
 */
@Module
public class DataModule {
    @Provides
    @Singleton
    DataSourceImpl provideDataSource() {
        return new DataSourceImpl();
    }
}
