package carmo.augusto.tvseries

import android.app.Application
import carmo.augusto.tvseries.di.repositories
import carmo.augusto.tvseries.di.retrofitModule
import carmo.augusto.tvseries.di.viewModelModules
import carmo.augusto.tvseries.di.webServiceModule
import org.koin.android.ext.android.startKoin

class TvSeriesApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(
            androidContext = this,
            modules = listOf(
                retrofitModule,
                webServiceModule,
                repositories,
                viewModelModules
            )
        )
    }
}