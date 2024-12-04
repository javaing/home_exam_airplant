package com.arttseng.homeexam.airplane.viewmodel


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arttseng.homeexam.airplane.MyApplication
import com.arttseng.homeexam.airplane.datamodel.Airport
import com.arttseng.homeexam.airplane.datamodel.ArrivalItem
import com.arttseng.homeexam.airplane.datamodel.Currencies
import com.arttseng.homeexam.airplane.datamodel.Departure
import com.arttseng.homeexam.airplane.tools.Const
import com.arttseng.homeexam.airplane.tools.RetrofitFactory
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {
    private val apiClient = RetrofitFactory.WebAccess.API_Airpane;
    private val apiCurrency = RetrofitFactory.WebAccess.APICurrency

    val airportData = MutableLiveData<List<Airport>>();
    val departureData = MutableLiveData<List<Departure>>()
    val arrivalPlaneData = MutableLiveData<List<ArrivalItem>>()
    val isNeedReload = MutableLiveData(false)
    val currenciesData = MutableLiveData<Currencies?>();


    init {
        airportData.value = emptyList()
        getAirport()
    }

    fun getCurrencies(base: String) {
        viewModelScope.launch {
            try {
                val response = apiCurrency.getLastestCurrency(MyApplication.currencyApikey, Const.currencyList, base_currency = base)
                val note = if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
                currenciesData.value = note
                Log.e("","art currenciesData size:" + currenciesData.value)
            } catch (e: Exception) {
                Log.e("", "art currenciesData err:" + e.message)
            }
        }
    }

    fun setReload(isNeed: Boolean) {
        isNeedReload.value = isNeed;
    }

    fun getAirport() {
        if(MyApplication.tdx_token.isEmpty()) return
        viewModelScope.launch {
            try {
                val response = apiClient.getAirport("JSON")
                val note = if (response.isSuccessful) {
                    response.body()
                } else {
                    emptyList()
                    //genTestData()
                }
                airportData.value = note ?: emptyList()
                Log.e("","art airportData size:" + airportData.value?.size)
            } catch (e: Exception) {
                Log.e("", "art airportData err:" + e.message)
            }
        }
    }

    fun getDeparture() {
        if(MyApplication.tdx_token.isEmpty()) return
        if(airportData.value!!.isEmpty()) getAirport()

        viewModelScope.launch {
            val response = apiClient.getDeparture("TPE","JSON", Const.RecordSize, false)
            val note = if (response.isSuccessful) {
                response.body()
            } else {
                emptyList()
            }
            departureData.value = note ?: emptyList()
        }
    }

    fun getArrival() {
        if(MyApplication.tdx_token.isEmpty()) return
        if(airportData.value!!.isEmpty()) getAirport()

        viewModelScope.launch {
            val response = apiClient.getArrival("TPE","JSON", Const.RecordSize, false)
            val note = if (response.isSuccessful) {
                response.body()
            } else {
                //genDummyArrival()
                emptyList()
            }
            arrivalPlaneData.value = note ?: emptyList()
        }
    }

}