package com.ubcoin.network

import com.ubcoin.fragment.deals.BaseDealsChildFragment
import com.ubcoin.model.response.User
import com.ubcoin.model.response.base.MarketListResponse
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.SignIn
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 * Created by Yuriy Aizenberg
 */
object DataProvider {

    private var networkModule: NetworkModule = NetworkModule

    fun login(email: String, password: String, onSuccess: Consumer<ProfileCompleteResponse>, onError: Consumer<Throwable>) {
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

    fun getMarketList(limit: Int, page: Int, onSuccess: Consumer<MarketListResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().marketList(limit, page)
                .debounce(100, TimeUnit.MILLISECONDS)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getFavoriteList(limit: Int, page: Int, onSuccess: Consumer<MarketListResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().marketList(limit, page)
                .debounce(100, TimeUnit.MILLISECONDS)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getDeals(limit: Int, page: Int, type: BaseDealsChildFragment.Type, onSuccess: Consumer<MarketListResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().marketList(limit, page)
                .doOnNext {
                    if (type == BaseDealsChildFragment.Type.BUY) {
                        (it.data as ArrayList).clear()
                    }
                }
                .debounce(100, TimeUnit.MILLISECONDS)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }


}