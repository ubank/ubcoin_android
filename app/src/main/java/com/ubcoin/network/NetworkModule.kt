package com.ubcoin.network

import android.util.Log
import com.google.gson.Gson
import com.ubcoin.isDebug
import com.ubcoin.preferences.ThePreferences
import com.ubcoin.model.ErrorWrapper
import com.ubcoin.utils.NetworkConnectivityAwareManager
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.HttpURLConnection
import java.util.*


/**
 * Created by Yuriy Aizenberg
 */
private const val AUTH_HEADER = "X-Authentication"

object NetworkModule {

    private var thePreferences = ThePreferences()

    fun api(): Api {
        return retrofit().create(Api::class.java)
    }

    private fun getUrl() : String {
        return if (isDebug()) {
            "https://qa.ubcoin.io/api/"
        } else {
            "https://my.ubcoin.io/"
        }
    }



    public fun getChatUrl() : String {
        return if (isDebug()) {
            "https://qa.ubcoin.io"
        } else {
            "https://my.ubcoin.io"
        }
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .validateEagerly(true)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(NullOrEmptyConvertFactory())
                .addConverterFactory(gsonConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(getUrl())
                .client(client())
                .build()
    }

    fun gsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    fun client(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor())
                .addInterceptor(logInterceptor())
                .addInterceptor(networkHandleInterceptor())
                .connectionSpecs(listOf(createConnectionSpec(), ConnectionSpec.CLEARTEXT))
                .build()
    }

    fun clientWithoutInterceptors(): OkHttpClient {
        return OkHttpClient.Builder()
                .connectionSpecs(listOf(createConnectionSpec(), ConnectionSpec.CLEARTEXT))
                .build()
    }

    private fun createConnectionSpec(): ConnectionSpec {
        return ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_0, TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA
                ).build()
    }

    private fun tokenInterceptor(): Interceptor {
        return Interceptor {
            val response: Response?
            try {
                if (it.request().url().toString().endsWith("/api/auth")) {
                    response = it.proceed(it.request())
                    if (response?.code() ?: 401 == HttpURLConnection.HTTP_OK) {
                        thePreferences.setToken(response.header(AUTH_HEADER))
                    }
                } else {

                    var d = Locale.getDefault().toString()

                    response = it.proceed(it.request().newBuilder()
                            .addHeader(AUTH_HEADER, thePreferences.getToken() ?: "")
                            .addHeader("Accept-Language", Locale.getDefault().toString().substring(0,2))
                            .build())
                }
                val code = response.code()
                if (code == HttpURLConnection.HTTP_OK) {
                    checkResponseContentLength(response)
                    response
                } else {
                    throw HttpRequestException(null, parseResponseForError(response), code)
                }
            } catch (e: NetworkConnectivityException) {
                throw e
            } catch (e: Exception) {
                throw HttpRequestException(e, null)
            }
        }
    }

    private fun checkResponseContentLength(response: Response?) {
        val get = response?.headers()?.get("Content-Length")
        if (get == null || get.isBlank()) {
            response?.headers()?.newBuilder()?.add("Content-Length", "0")
        }
    }

    private fun parseResponseForError(response: Response): ErrorWrapper? {
        return try {
            Gson().fromJson(response.body()!!.string(), ErrorWrapper::class.java)
        } catch (e: Exception) {
            Log.e(javaClass.name, e.message, e)
            null
        }
    }

    private fun logInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    private fun networkHandleInterceptor(): Interceptor {
        return Interceptor { chain ->
            if (!NetworkConnectivityAwareManager.isNetworkAvailable()) {
                throw NetworkConnectivityException()
            }
            chain.proceed(chain.request())
        }
    }


}