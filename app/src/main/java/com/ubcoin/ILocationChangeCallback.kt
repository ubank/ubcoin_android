package com.ubcoin

import com.google.android.gms.maps.model.LatLng

/**
 * Created by Yuriy Aizenberg
 */
interface ILocationChangeCallback {

    fun onLatLngChanged(latLng: LatLng)

}