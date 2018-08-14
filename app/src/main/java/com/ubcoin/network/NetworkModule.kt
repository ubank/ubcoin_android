package com.ubcoin.network

import android.util.Log
import com.google.gson.Gson
import com.ubcoin.ThePreferences
import com.ubcoin.model.Error
import com.ubcoin.utils.NetworkConnectivityAwareManager
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.net.HttpURLConnection
import kotlin.system.exitProcess


/**
 * Created by Yuriy Aizenberg
 */
private const val AUTH_HEADER = "X-Authentication"

object NetworkModule {

    private var thePreferences = ThePreferences()

    fun api(): Api {
        return retrofit().create(Api::class.java)
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
                .validateEagerly(true)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(NullOrEmptyConvertFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://my.ubcoin.io/")
                .client(client())
                .build()
    }

    fun client(): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor())
                .addInterceptor(logInterceptor())
                .addInterceptor(networkHandleInterceptor())
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
                    if (response?.code()?: 401 == HttpURLConnection.HTTP_OK) {
                        thePreferences.setCookie(response.header("set-cookie")?.split(";")?.get(0))
                        thePreferences.setWVCookie(response.header("set-cookie"))
                        thePreferences.setToken(response.header(AUTH_HEADER))
                    }
                } else {
                    response = it.proceed(it.request().newBuilder()
                            .addHeader("Cookie", thePreferences.getCookie() ?: "")
                            .addHeader(AUTH_HEADER, thePreferences.getToken() ?: "")
                            .build())
                }
                val code = response.code()
                if (code == HttpURLConnection.HTTP_OK) {
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

    private fun parseResponseForError(response: Response): Error? {
        return try {
            Gson().fromJson(response.body()!!.string(), Error::class.java)
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

    private fun networkHandleInterceptor() : Interceptor {
        return Interceptor { chain ->
            if (!NetworkConnectivityAwareManager.isNetworkAvailable()) {
                throw NetworkConnectivityException()
            }
            chain.proceed(chain.request())
        }
    }


}