package com.ubcoin.network

import com.ubcoin.fragment.deals.BaseDealsChildFragment
import com.ubcoin.model.response.*
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
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


    fun registrations(email: String, password: String, name: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
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

    fun updateProfileEmailAndName(email: String, userName: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().updateProfileEmailAndName(UpdateUserData(userName, email))
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

    fun profile(onSuccess: Consumer<User>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().profile()
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

    fun getTgLink(itemId: String, onSuccess: Consumer<TgLink>, onError: Consumer<Throwable>): Disposable {
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
                .debounce(200, TimeUnit.MILLISECONDS)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun unfavorite(itemId: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().unfavorite(itemId)
                .debounce(200, TimeUnit.MILLISECONDS)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun uploadFiles(filePath: ArrayList<String>, onSuccess: Consumer<TgLinks>, onError: Consumer<Throwable>) {
        val tgLinks = TgLinks()
        Observable.fromIterable(filePath)
                .map { t ->
                    val file = File(t)
                    val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
                    val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
                    tgLinks.tgLinks.add((networkModule.api().uploadImage(body).blockingFirst()))
                    tgLinks
                }
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getBuyersItems(limit: Int, page: Int, onSuccess: Consumer<DealsListResponse>, onError: Consumer<Throwable>) {
        networkModule.api()
                .getBuyersItems(limit, page)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }


    fun getSellerItems(limit: Int, page: Int, onSuccess: Consumer<DealsListResponse>, onError: Consumer<Throwable>) {
        networkModule.api()
                .getSellersItems(limit, page)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getMarketItemById(itemId: String, onSuccess: Consumer<MarketItem>, onError: Consumer<Throwable>) {
        networkModule.api()
                .marketItem(itemId)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }
}