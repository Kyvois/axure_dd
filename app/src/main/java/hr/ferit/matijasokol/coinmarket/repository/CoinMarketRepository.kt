package hr.ferit.matijasokol.coinmarket.repository

import hr.ferit.matijasokol.coinmarket.db.CoinDao
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.networking.CoinsMarketApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoinMarketRepository @Inject constructor(
    private val coinDao: CoinDao,
    private val coinApi: CoinsMarketApi
) {

    suspend fun getCoins() = coinApi.getCoins()

    suspend 