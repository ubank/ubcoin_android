package com.ubcoin.utils

import android.content.Context
import android.support.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.ubcoin.R
import com.ubcoin.TheApplication
import kotlin.math.roundToInt

/**
 * Created by Yuriy Aizenberg
 */

object DistanceUtils {

    fun calculateDistance(lat: Double?, lon: Double?, context: Context): String {
        val currentLocation = TheApplication.instance.currentLocation
        if (lat == null || lon == null || currentLocation == null) {
            return noLocationDefined(context)
        }
        val distanceBetween = SphericalUtil.computeDistanceBetween(LatLng(lat, lon), currentLocation).roundToInt()
        return if (distanceBetween > 1000) {
            val meterDistance = distanceBetween.rem(1000)
            val meterDistanceString =
                    if (meterDistance == 0) {
                        ""
                    } else {
                        "," + meterDistance.toString()[0].toString()
                    }
//            val meterDistanceString = if (meterDistance == 0) " " else meterDistance.toString()
            /*if (meterDistanceString.endsWith("0") && meterDistanceString.length > 1) {
                meterDistanceString = meterDistanceString.substring(0, 2)
            }
            if (meterDistanceString.endsWith("0")) {
                meterDistanceString = meterDistanceString.substring(0, 1)
            }*/

            val kmDistance = ((distanceBetween - meterDistance) / 1000).toString()
            kmDistance + meterDistanceString + " " + getString(R.string.distance_km, context)
        } else {
            distanceBetween.toString() + " " + getString(R.string.distance_m, context)
        }
    }

    private fun noLocationDefined(context: Context) =
            """${getString(R.string.distance_unknown, context)} ${getString(R.string.distance_km, context)}"""

    fun calculateDistance(latLng: LatLng?, context: Context): String {
        if (latLng == null) return noLocationDefined(context)
        return calculateDistance(latLng.latitude, latLng.longitude, context)
    }

    private fun getString(@StringRes resId: Int, context: Context) = context.getString(resId)
}