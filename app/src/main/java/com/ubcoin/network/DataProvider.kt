package com.ubcoin.network

import com.ubcoin.model.response.User
import com.ubcoin.model.response.base.MarketListResponse
import com.ubcoin.network.request.SignIn
import com.ubcoin.utils.CollectionExtensions
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import retrofit2.Response

/**
 * Created by Yuriy Aizenberg
 */
object DataProvider {

    private var networkModule : NetworkModule = NetworkModule

    fun login(email: String, password: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api()
                .login(SignIn(email, password))
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun logout(onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().logout()
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun profile(onSuccess: Consumer<User>, onError: Consumer<Throwable>) {
        networkModule.api().profile()
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getMarketList(limit: Int, page: Int, onSuccess: Consumer<MarketListResponse>, onError: Consumer<Throwable>) : Disposable {
        return networkModule.api().marketList(limit, page)
                .doOnNext {
                    if (!CollectionExtensions.nullOrEmpty(it.data)) {
                        for (i in 0..5) {
                            (it.data as ArrayList).addAll(ArrayList(it.data))
                        }
                    }
                }
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }


}