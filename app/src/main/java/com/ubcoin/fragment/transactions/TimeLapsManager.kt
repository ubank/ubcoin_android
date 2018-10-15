package com.ubcoin.fragment.transactions

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.flowables.ConnectableFlowable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Predicate
import java.util.concurrent.TimeUnit

/**
 * Created by Yuriy Aizenberg
 */

class TimeLapsManager {

    private var flowable: ConnectableFlowable<Long>? = null
    private var currentQRHash: String? = null
    private var stopFlag = false
    private var interval: Long = 0L


    fun subscribe(currentQrHash: String, interval: Long, consumer: Consumer<Long>): Disposable {
        this.interval = interval
        this.currentQRHash = currentQrHash
        flowable = Flowable.interval(1.toLong(), TimeUnit.SECONDS)
                .map { t -> interval - t * 1000.toLong() } //Revert to time diff

                .takeUntil(Predicate<Long> { t ->
                    if (!stopFlag && t > 0L) return@Predicate false
                    true
                })
                .onBackpressureDrop()
                .observeOn(AndroidSchedulers.mainThread())
                .publish()
        flowable!!.connect()
        return flowable!!.subscribe(consumer)
    }

    fun stop() {
        stopFlag = true
    }
}