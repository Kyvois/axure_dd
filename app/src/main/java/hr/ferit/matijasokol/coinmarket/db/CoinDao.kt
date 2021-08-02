package hr.ferit.matijasokol.coinmarket.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.ferit.matijasokol.coinmarket.models.Coin

@Dao
interface CoinDao {

    @Inse