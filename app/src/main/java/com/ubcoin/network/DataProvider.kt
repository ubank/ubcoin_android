package com.ubcoin.network

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.ubcoin.model.CommissionAndConversionResponse
import com.ubcoin.model.ConversionResponse
import com.ubcoin.model.response.*
import com.ubcoin.model.response.base.IdResponse
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.*
import com.ubcoin.utils.ProfileHolder
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Yuriy Aizenberg
 */
@SuppressLint("CheckResult")
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

    fun sendRegistrationEmail(email: String, onSuccess: Consumer<Response<Unit>>, onError: Consumer<Throwable>) {
        networkModule.api().sendForgotEmail(SendForgotEmail("LOGIN", email))
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
                .doOnNext {
                    val myBalance = networkModule.api().balance().blockingSingle()
                    ProfileHolder.balance = myBalance
                }
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getMarketList(limit: Int, page: Int, latPoint: Double?, longPoint: Double?, onSuccess: Consumer<MarketListResponse>, onError: Consumer<Throwable>): Disposable {
        val marketList =
                if (latPoint != null && longPoint != null) {
                    networkModule.api().marketList(limit, page, latPoint, longPoint)
                } else {
                    networkModule.api().marketList(limit, page)
                }
        return marketList
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

    fun discuss(iPurchaseLinkRequest: IPurchaseLinkRequest, onSuccess: Consumer<TgLink>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .discuss(iPurchaseLinkRequest)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getTgLink(onSuccess: Consumer<TgLink>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .getTgLink()
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
        Observable.just(filePath)
                .doOnNext { it ->
                    it.forEach {
                        val file = File(it)
                        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
                        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
                        tgLinks.tgLinks.add((networkModule.api().uploadImage(body).blockingFirst()))
                    }
                }.map { tgLinks }
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getBuyersItems(limit: Int, page: Int, onSuccess: Consumer<DealsListResponse>, onError: Consumer<Throwable>) {
        networkModule.api()
                .getBuyersItems(limit, page)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }


    fun getSellerItems(limit: Int, page: Int, onSuccess: Consumer<MarketListResponse>, onError: Consumer<Throwable>) {
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

    fun getCategories(onSuccess: Consumer<List<Category>>, onError: Consumer<Throwable>) {
        networkModule.api().getCategories()
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun findLocation(address: String, onSuccess: Consumer<List<SingleLocation>>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .findLocation(AddressBody(address))
                .compose(RxUtils.applyTSingle())
                .subscribe(onSuccess, onError)
    }

    fun findLocationSync(address: String): List<SingleLocation> {
        return networkModule.api().findLocation(AddressBody(address)).blockingFirst()
    }

    fun resolveLocation(context: Context, lat: Double, lon: Double, onSuccess: Consumer<List<Address>>, onError: Consumer<Throwable>): Disposable {
        return Observable.just(Geocoder(context, Locale.getDefault()).getFromLocation(lat, lon, 1))
                .compose(RxUtils.applyTSingle())
                .subscribe(onSuccess, onError)
    }

    fun createProduct(createProductRequest: CreateProductRequest, onSuccess: Consumer<IdResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().createProduct(createProductRequest)
                .compose(RxUtils.applyTSingle())
                .subscribe(onSuccess, onError)
    }

    fun updateProduct(updateProductRequest: UpdateProductRequest, onSuccess: Consumer<MarketItem>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().updateProduct(updateProductRequest)
                .compose(RxUtils.applyTSingle())
                .subscribe(onSuccess, onError)
    }

    fun balance(onSuccess: Consumer<MyBalance>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().balance()
                .compose(RxUtils.applyTSingle())
                .subscribe(onSuccess, onError)
    }

    fun transactions(limit: Int, page: Int, onSuccess: Consumer<TransactionListResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .transactions(limit, page)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun transaction(id: String, onSuccess: Consumer<TransactionListResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .transaction(id)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun topUp(onSuccess: Consumer<TopUp>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .topUp()
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    private fun getConversion(conversionRequest: ConversionRequest, onSuccess: Consumer<ConversionResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .getConversion(conversionRequest)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getConversionFromUBC(amount: String, onSuccess: Consumer<ConversionResponse>, onError: Consumer<Throwable>): Disposable {
        return getConversion(ConversionRequest("UBC", "USD", amount), onSuccess, onError)
    }

    fun getConversionFromUSD(amount: String, onSuccess: Consumer<ConversionResponse>, onError: Consumer<Throwable>): Disposable {
        return getConversion(ConversionRequest("USD", "UBC", amount), onSuccess, onError)
    }


    fun getCommission(amount: Double, onSuccess: Consumer<Commission>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .getCommission(amount)
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getCommissionBeforeAndConversionTOUSDAfter(amount: Double, onSuccess: Consumer<CommissionAndConversionResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api()
                .getCommission(amount)
                .flatMap(
                        { t ->
                            networkModule.api().getConversion(ConversionRequest("UBC", "USD", (amount - t.commission).toString()))
                        }, { t1, t2 ->
                    CommissionAndConversionResponse(t1, t2)
                }
                ).compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun withdraw(amount: Double, address: String, onSuccess: Consumer<WithdrawResponse>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().withdraw(Withdraw(address, amount))
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun getExchangeMarkets(onSuccess: Consumer<List<ExchangeMarket>>, onError: Consumer<Throwable>): Disposable {
        return networkModule.api().exchangeMarkets()
                .compose(RxUtils.applyT())
                .subscribe(onSuccess, onError)
    }

    fun activate(itemId: String, onSuccess: Consumer<MarketItem>, onError: Consumer<Throwable>): Disposable {
        return toggleStatusInternal(itemId, activate = true)
                .subscribe(onSuccess, onError)
    }

    fun deactivate(itemId: String, onSuccess: Consumer<MarketItem>, onError: Consumer<Throwable>): Disposable {
        return toggleStatusInternal(itemId, activate = false)
                .subscribe(onSuccess, onError)
    }

    private fun toggleStatusInternal(itemId: String, activate: Boolean): Observable<MarketItem> {
        val request = ActivateDeactivateRequest(itemId)
        val api = networkModule.api()
        val observable: Observable<MarketItem>
        observable = if (activate) {
            api.activate(request)
        } else {
            api.deactivate(request)
        }
        return observable.compose(RxUtils.applyT())
    }


}