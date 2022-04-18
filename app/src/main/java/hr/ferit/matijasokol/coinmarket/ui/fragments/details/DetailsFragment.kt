
package hr.ferit.matijasokol.coinmarket.ui.fragments.details

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.coinmarket.R
import hr.ferit.matijasokol.coinmarket.models.Coin
import hr.ferit.matijasokol.coinmarket.models.CoinInfoResponse
import hr.ferit.matijasokol.coinmarket.models.Resource
import hr.ferit.matijasokol.coinmarket.other.*
import hr.ferit.matijasokol.coinmarket.other.Constants.CHART_ANIM_DURATION
import hr.ferit.matijasokol.coinmarket.other.Constants.WEEK_LENGTH
import kotlinx.android.synthetic.main.fragment_details.*
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args: DetailsFragmentArgs by navArgs()
    private lateinit var coin: Coin

    private val viewModel: DetailsViewModel by viewModels()

    private val dailyValues by lazy { mutableListOf<Entry>() }
    private val weekValues by lazy { mutableListOf<Entry>() }
    private val monthValues by lazy { mutableListOf<Entry>() }
    private val yearValues by lazy { mutableListOf<Entry>() }

    private val textViewsChoosers by lazy { listOf(textViewDay, textViewWeek, textViewMonth, textViewYear) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSharedElementTransitionOnEnter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coin = args.coin

        imageViewIconDetails.apply {
            transitionName = coin.imageUrl
            loadImage(coin.imageUrl)
        }

        setListeners()
        setTextViewsClickability(false)
        observeChanges()

        viewModel.getCoinDetails(coin.id)
    }

    private fun setListeners() {
        textViewDay.setOnClickListener {
            if (isDataValid(dailyValues)) {
                setTextViewsColor(textViewDay)
                setLineChart(dailyValues)
            } else {
                detailsRootLayout.showSnackbar(getString(R.string.no_values_category))
            }
        }
        textViewWeek.setOnClickListener {
            if (isDataValid(weekValues)) {
                setTextViewsColor(textViewWeek)
                setLineChart(weekValues)
            } else {
                detailsRootLayout.showSnackbar(getString(R.string.no_values_category))
            }
        }
        textViewMonth.setOnClickListener {
            if (isDataValid(monthValues)) {
                setTextViewsColor(textViewMonth)
                setLineChart(monthValues)
            } else {
                detailsRootLayout.showSnackbar(getString(R.string.no_values_category))
            }
        }
        textViewYear.setOnClickListener {
            if (isDataValid(yearValues)) {
                setTextViewsColor(textViewYear)
                setLineChart(yearValues)
            } else {
                detailsRootLayout.showSnackbar(getString(R.string.no_values_category))
            }
        }
    }

    private fun observeChanges() {
        viewModel.lastDayCoinDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        setDailyData(it)
                    }
                    lottieChart.gone()
                    lineChart.visible()
                    setTextViewsClickability(true)
                    setTextViewsColor(textViewDay)
                }
                is Resource.Error -> {
                    lottieChart.gone()
                    response.message?.let {
                        detailsRootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    lottieChart.visible()
                }
            }
        })

        viewModel.coinInfo.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        setTextViews(it)
                    }
                    lottieText.gone()
                }
                is Resource.Error -> {
                    lottieText.gone()
                    response.message?.let {
                        detailsRootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    lottieText.visible()
                }
            }
        })

        viewModel.yearCoinDetails.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data?.let {
                        if (it.size >= 365) {
                            setWeekData(it)
                            setYearData(it)
                            setMonthData(it)
                        }
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        detailsRootLayout.showSnackbar(getString(R.string.error))
                    }
                }
                is Resource.Loading -> {
                    lottieChart.visible()
                }
            }
        })
    }

    private fun setTextViews(response: CoinInfoResponse) {
        val highPriceText = "${getString(R.string.high_24)}: ${coin.maxPrice}€"
        val lowPriceText = "${getString(R.string.low_24)}: ${coin.minPrice}€"
        val hashAlgText = "${getString(R.string.hash_alg)}: ${response.hashAlgorithm ?: getString(R.string.unknown)}"

        val highPriceSpannable = SpannableString(highPriceText).apply {
            setSpan(ForegroundColorSpan(Color.GREEN), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val lowPriceSpannable = SpannableString(lowPriceText).apply {
            setSpan(ForegroundColorSpan(Color.RED), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        val hashAlgSpannable = SpannableString(hashAlgText).apply {
            setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.colorPrimary)), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD), 0, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        textViewDesc.text = response.description.english.trim().fromHtmlToText()
        if (textViewDesc.text.isBlank()) {
            textViewDesc.text = getString(R.string.no_data)