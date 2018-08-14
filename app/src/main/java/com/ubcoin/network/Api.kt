package com.ubcoin.network

import com.ubcoin.model.response.TgLink
import com.ubcoin.model.response.User
import com.ubcoin.model.response.base.MarketListResponse
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.*
import io.reactivex.Observable
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
    fun sendForgotEmail(@Body sendForgotEmail: SendForgotEmail) : Observable<Response<Unit>>

    @POST("/api/verification/check")
    fun changeForgotPassword (@Body changeForgotPassword: ChangeForgotPassword) : Observable<ProfileCompleteResponse>

    @POST("/api/verification/check")
    fun confirmEmailRegistration(@Body confirmEmailRegistration: ConfirmEmailRegistration) : Observable<ProfileCompleteResponse>

    @POST("/api/auth/logout")
    fun logout(): Observable<Response<Unit>>

    @GET("/api/users/me")
    fun profile(): Observable<User>

    @GET("/api/items")
    fun marketList(@Query("size") limit: Int, @Query("page") page: Int): Observable<MarketListResponse>

    @GET("/api/favorites")
    fun favorites(@Query("size") limit: Int, @Query("page") page: Int) : Observable<MarketListResponse>

    @POST("/api/favorites/{itemId}")
    fun favorite(@Path("itemId") itemId: String)  : Observable<Response<Unit>>

    @DELETE("/api/favorites/{itemId}")
    fun unfavorite(@Path("itemId") itemId: String)  : Observable<Response<Unit>>

    @GET("/api/users/tg")
    fun getTgLink(@Query("itemId") itemId: String) : Observable<TgLink>
}
