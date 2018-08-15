package com.ubcoin.network

import io.reactivex.FlowableTransformer
import io.reactivex.MaybeTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Yuriy Aizenberg
 */
object RxUtils {

    fun <T> applyF(): FlowableTransformer<T, T> {
        return FlowableTransformer { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun <T> applyT(): ObservableTransformer<T, T> {
        return ObservableTransformer { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

    fun <T> applyMaybe(): MaybeTransformer<T, T> {
        return MaybeTransformer { it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) }
    }

}
