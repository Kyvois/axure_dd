package hr.ferit.matijasokol.coinmarket.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.ferit.matijasokol.coinmarket.db.CoinDatabase
import hr.ferit.matijasokol.coinmarket.networking.CoinsM