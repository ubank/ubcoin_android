package com.ubcoin.network

import com.ubcoin.model.response.User
import com.ubcoin.model.response.base.MarketListResponse
import com.ubcoin.model.response.profile.ProfileCompleteResponse
import com.ubcoin.network.request.SignIn
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Yuriy Aizenberg
 */
interface Api {

    @POST("/api/auth")
    fun login(@Body signIn: SignIn): Observable<ProfileCompleteResponse>

    @POST("/api/auth/logout")
    fun logout(): Observable<Response<Unit>>

    @GET("/api/users/me")
    fun profile(): Observable<User>

    @GET("/api/items")
    fun marketList(@Query("size") limit: Int, @Query("page") page: Int): Observable<MarketListResponse>
}