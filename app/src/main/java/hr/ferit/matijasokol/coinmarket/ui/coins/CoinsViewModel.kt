package hr.ferit.matijasokol.coinmarket.ui.coins

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.app.CoinMarketApplication
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.models.Resource
import hr.ferit.matijasokol.coinmarket.other.SingleLiveEvent
import hr.ferit.matijasokol.coinmarket.other.hasInternetConnection
import hr.ferit.matijasokol.coinmarket.repository.CoinMarketRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.IOException

class CoinsViewModel @ViewModelInject constructor(
    app: Application,
    private val repository: CoinMarketRepository
) : AndroidViewModel(app