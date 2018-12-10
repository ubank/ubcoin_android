package com.ubcoin.network

import com.ubcoin.model.ConversionResponse
import com.ubcoin.model.CryptoCurrency
import com.ubcoin.model.response.*
import com.ubcoin.model.response.base.IdResponse
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Yuriy Aizenberg
 */
interface Api {

    @POST("/api/auth")
    fun login(@Body signIn: SignIn): Observable<ProfileCompleteResponse>

    @POST("/api/users/registration")
    fun registration(@Body signIn: SignUp): Observable<Response<Unit>>

    @POST("/api/verification")
    fun sendForgotEmail(@Body sendForgotEmail: SendForgotEmail): Observable<Response<Unit>>

    @POST("/api/verification/check")
    fun changeForgotPassword(@Body changeForgotPassword: ChangeForgotPassword): Observable<ProfileCompleteResponse>

    @POST("/api/verification/check")
    fun confirmEmailRegistration(@Body confirmEmailRegistration: ConfirmEmailRegistration): Observable<ProfileCompleteResponse>

    @EliminatedBody
    @POST("/api/auth/logout")
    fun logout(): Observable<Response<Unit>>

    @GET("/api/users/me")
    fun profile(): Observable<User>

    @EliminatedBody
    @PUT("/api/users")
    fun updateProfileEmailAndName(@Body updateUserData: UpdateUserData): Observable<Response<Unit>>

    @GET("/api/items")
    fun sellerMarketItemsList(
            @Query("sellerId") sellerId: String?,
            @Query("size") limit: Int,
            @Query("page") page: Int): Observable<MarketListResponse>

    @GET("/api/items")
    fun marketList(
            @Query("size") limit: Int,
            @Query("page") page: Int,
            @Query("category") categories: List<String>?,
            @Query("maxPrice") maxPrice: Double?,
            @Query("maxDistance") maxDistance: Int?,
            @Query("sortByDate") sortByDate: String?,
            @Query("sortByPrice") sortByPrice: String?,
            @Query("sortByDistance") sortByDistance: String?,
            @Query("condition") condition: String?): Observable<MarketListResponse>

    @GET("/api/items")
    fun marketList(
            @Query("size") limit: Int,
            @Query("page") page: Int,
            @Query("latPoint") latPoint: Double,
            @Query("longPoint") longPoint: Double,
            @Query("category") categories: List<String>?,
            @Query("maxPrice") maxPrice: Double?,
            @Query("maxDistance") maxDistance: Int?,
            @Query("sortByDate") sortByDate: String?,
            @Query("sortByPrice") sortByPrice: String?,
            @Query("sortByDistance") sortByDistance: String?,
            @Query("condition") condition: String?): Observable<MarketListResponse>

    @GET("/api/items/{itemId}")
    fun marketItem(@Path("itemId") itemId: String): Observable<MarketItem>

    @GET("/api/favorites")
    fun favorites(@Query("size") limit: Int, @Query("page") page: Int): Observable<MarketListResponse>

    @POST("/api/favorites/{itemId}")
    fun favorite(@Path("itemId") itemId: String): Observable<Response<Unit>>

    @DELETE("/api/favorites/{itemId}")
    fun unfavorite(@Path("itemId") itemId: String): Observable<Response<Unit>>

    @GET("/api/users/tg")
    fun getTgLink(@Query("itemId") itemId: String): Observable<TgLink>

    @GET("/api/users/tg")
    fun getTgLink(): Observable<TgLink>

    @Multipart
    @POST("/api/images")
    fun uploadImage(@Part file: MultipartBody.Part): Observable<TgLink>

    @GET("/api/purchases/buyer")
    fun getBuyersItems(@Query("size") limit: Int, @Query("page") page: Int): Observable<DealsListResponse>

    @GET("/api/purchases/seller")
    fun getSellersItems(@Query("size") limit: Int, @Query("page") page: Int): Observable<MarketListResponse>

    @GET("/api/items/categories")
    fun getCategories(): Observable<List<Category>>

    @POST("/api/geo/find")
    fun findLocation(@Body addressBody: AddressBody): Observable<List<SingleLocation>>

    @POST("/api/items")
    fun createProduct(@Body createProductRequest: CreateProductRequest): Observable<IdResponse>

    @PUT("/api/items")
    fun updateProduct(@Body updateProductRequest: UpdateProductRequest): Observable<MarketItem>

    @GET("/api/wallet/balance")
    fun balance(): Observable<MyBalance>

    @GET("/api/wallet/transactions")
    fun transactions(@Query("currencyType") currencyType: CryptoCurrency, @Query("size") limit: Int, @Query("page") page: Int): Observable<TransactionListResponse>

    @GET("/api/wallet/transactions")
    fun transaction(@Query("id") id: String): Observable<TransactionListResponse>

    @GET("/api/wallet/topup")
    fun topUp(): Observable<TopUp>

    @POST("/api/wallet/convert")
    fun getConversion(@Body conversionBody: ConversionRequest): Observable<ConversionResponse>

    @GET("/api/wallet/commission")
    fun getCommission(@Query("amount") amount: Double): Observable<Commission>

    @POST("/api/wallet/withdraw")
    fun withdraw(@Body withdraw: Withdraw): Observable<WithdrawResponse>

    @POST("/api/items/discuss")
    fun discussFromBuyer(@Body request: BuyerPurchaseLinkRequest): Observable<TgLink>

    @POST("/api/items/discuss")
    fun discussFromSeller(@Body request: SellerPurchaseLinkRequest): Observable<TgLink>

    @GET("/api/wallet/markets")
    fun exchangeMarkets() : Observable<List<ExchangeMarket>>

    @POST("/api/items/activate")
    fun activate(@Body activateDeactivateRequest: ActivateDeactivateRequest) : Observable<MarketItem>

    @POST("/api/items/deactivate")
    fun deactivate(@Body activateDeactivateRequest: ActivateDeactivateRequest) : Observable<MarketItem>
}
