package com.arttseng.homeexam.airplane

import android.app.Application
import android.util.Log
import com.arttseng.homeexam.airplane.tools.Const
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit

class MyApplication: Application() {
    companion object {
        private var instance: Application? = null
        var tdx_token = ""
        var currencyApikey = ""
        private var okHttpClient: OkHttpClient? = null
        fun getOkHttpClient(): OkHttpClient {
            if(okHttpClient==null) {
                //if (BuildConfig.DEBUG) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.level = HttpLoggingInterceptor.Level.HEADERS
                okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(Interceptor { chain ->
                        val request: Request =
                            chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .addHeader("Authorization", "Bearer $tdx_token")
                                .build()
                        chain.proceed(request)
                    })
                    .retryOnConnectionFailure(true) //.addInterceptor(UserAgentInterc)
                    .connectTimeout(80, TimeUnit.SECONDS)
                    .readTimeout(80, TimeUnit.SECONDS)
                    .build()

                Log.e("", "art created okhttp")
            }
            return okHttpClient as OkHttpClient
        }
    }

    private fun getTDXToken() {
        val formBody = FormBody.Builder()
            .add("grant_type", "client_credentials" )
            .add("client_id", resources.getString(R.string.tdx_client_id))
            .add("client_secret", resources.getString(R.string.tdx_client_secret))
            .build()

        // creating request
        var request = Request.Builder().url(Const.TDX_Base + Const.TDX_Token)
            .post(formBody)
            .build()

        var client = OkHttpClient();
        client.newCall(request).enqueue(object: okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("", "okhttp fail:")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.body.let {
                    val jsonData: String = response.body!!.string()
                    //Log.e("", "okhttp ok:" + jsonData)
                    val Jobject = JSONObject(jsonData)
                    tdx_token = Jobject.getString("access_token")
                }

            }
        })
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        currencyApikey = resources.getString(R.string.freecurrency_apikey)
        //tdx_token = resources.getString(R.string.tdx_token)
        getTDXToken()
    }
}
