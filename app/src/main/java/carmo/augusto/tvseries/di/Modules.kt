package carmo.augusto.tvseries.di

import android.content.Context
import carmo.augusto.core.repositories.IEpisodeRepository
import carmo.augusto.core.repositories.ISeasonRepository
import carmo.augusto.core.repositories.ISeriesRepository
import carmo.augusto.core.viewmodels.*
import carmo.augusto.core.webservice.IWebService
import carmo.augusto.repository.EpisodeRepository
import carmo.augusto.repository.SeasonRepository
import carmo.augusto.repository.SeriesRepository
import carmo.augusto.viewmodels.*
import org.koin.android.viewmodel.ext.koin.viewModel
import carmo.augusto.webservice.Api
import carmo.augusto.webservice.WebService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModules = module {

    fun provideHomeViewModel(seriesRepository: ISeriesRepository): AbsHomeViewModel {
        return HomeViewModel(seriesRepository = seriesRepository)
    }

    fun provideSeriesViewModel(seriesRepository: ISeriesRepository): AbsSeriesViewModel {
        return SeriesViewModel(seriesRepository = seriesRepository)
    }

    fun provideSeasonEpisodesViewModel(
        seasonRepository: ISeasonRepository,
    ): AbsSeasonEpisodesViewModel {
        return SeasonEpisodesViewModel(seasonRepository = seasonRepository)
    }

    fun provideEpisodeViewModel(episodeRepository: IEpisodeRepository): AbsEpisodeViewModel {
        return EpisodeViewModel(episodeRepository = episodeRepository)
    }

    fun provideSearchSeriesModel(seriesRepository: ISeriesRepository): AbsSearchSeriesViewModel {
        return SearchSeriesViewModel(seriesRepository = seriesRepository)
    }

    viewModel {
        provideHomeViewModel(seriesRepository = get())
    }

    viewModel {
        provideSeriesViewModel(seriesRepository = get())
    }

    viewModel {
        provideSeasonEpisodesViewModel(seasonRepository = get())
    }

    viewModel {
        provideEpisodeViewModel(episodeRepository = get())
    }

    viewModel {
        provideSearchSeriesModel(seriesRepository = get())
    }
}

val repositories = module {

    fun provideSeriesRepository(webService: IWebService): ISeriesRepository {
        return SeriesRepository(webService = webService)
    }

    fun provideEpisodeRepository(webService: IWebService): IEpisodeRepository {
        return EpisodeRepository(webService = webService)
    }

    fun provideSeasonRepository(webService: IWebService): ISeasonRepository {
        return SeasonRepository(webService = webService)
    }

    single {
        provideSeriesRepository(webService = get())
    } bind ISeriesRepository::class

    single {
        provideEpisodeRepository(webService = get())
    } bind IEpisodeRepository::class

    single {
        provideSeasonRepository(webService = get())
    } bind ISeasonRepository::class
}

val webServiceModule = module {

    fun provideWebService(api: Api): IWebService {
        return WebService(api)
    }

    single {
        provideWebService(api = get())
    } bind IWebService::class
}

val retrofitModule = module {

    fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }

    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .create()
    }

    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.addHeader("Content-Type", "application/json")

                return@addNetworkInterceptor chain.proceed(requestBuilder.build())
            }
            .build()
    }

    fun provideRetrofit(context: Context, factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(context.getString(carmo.augusto.core.R.string.tvmaze_api_base_url))
            .addConverterFactory(GsonConverterFactory.create(factory))
            .client(client)
            .build()
    }

    single {
        provideGson()
    }
    single {
        provideHttpClient()
    }
    single {
        provideRetrofit(context = get(), factory = get(), client = get())
    }
    single {
        provideApi(retrofit = get())
    }
}