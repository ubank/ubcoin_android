package com.ubcoin.network

import com.ubcoin.fragment.deals.BaseDealsChildFragment
import com.ubcoin.model.response.TgLink
import com.ubcoin.model.response.User
import com.ubcoin.model.response.base.MarketListResponse
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.*
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


    fun regisration(email: String, password: String, name: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().registration(SignUp(email, password, name))
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun confirmRegistrationEmail(email: String, code: String, onSuccess: Consumer<ProfileCompleteResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().confirmEmailRegistration(ConfirmEmailRegistration("REGISTRATION", email, code))
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun sendForgotEmail(email: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().sendForgotEmail(SendForgotEmail("PASSWORD", email))
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun forgotChangePassword(email: String, code: String, value: String, onSuccess: Consumer<ProfileCompleteResponse>, onError: Consumer<Throwable>) {
        networkModule.api().changeForgotPassword(ChangeForgotPassword(email, value, "PASSWORD", code))
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun registrationCheckEmail(email: String, code: String, value: String, onSuccess: Consumer<ProfileCompleteResponse>, onError: Consumer<Throwable>) {
        networkModule.api().changeForgotPassword(ChangeForgotPassword(email, value, "PASSWORD", code))
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
        return networkModule.api().favorites(limit, page)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getTgLink(itemId: String, onSuccess: Consumer<TgLink>, onError: Consumer<Throwable>) : Disposable {
        return networkModule.api()
                .getTgLink(itemId)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    //TODO
    fun getDeals(limit: Int, page: Int, type: BaseDealsChildFragment.Type, onSuccess: SilentConsumer<MarketListResponse>, onError: Consumer<Throwable>): Disposable {
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

    fun favorite(itemId: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().favorite(itemId)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun unfavorite(itemId: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().unfavorite(itemId)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

}