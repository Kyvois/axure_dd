package hr.ferit.matijasokol.coinmarket.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.matijasokol.coinmarket.db.CoinDatabase
import hr.ferit.matijasokol.coinmarket.networking.CoinsMarketApi
import hr.ferit.matijasokol.coinmarket.other.Constants
import hr.ferit.matijasokol.coinmarket.other.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCoinDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context.applicationContext,
        CoinDatabase::class.java,
        Constants.DB_NAME
    ).build()

    @Singleton
    @Provides
    fun provideCoinDao(coinDatabase: CoinDatabase) = coinDatabase.getCoinDao