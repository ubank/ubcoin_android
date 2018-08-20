package com.ubcoin.fragment.sell

import android.location.Address
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView
import com.ubcoin.R
import com.ubcoin.fragment.BaseFragment
import com.ubcoin.model.response.Location
import com.ubcoin.network.DataProvider
import com.ubcoin.network.SilentConsumer
import com.ubcoin.utils.SellCreateDataHolder

/**
 * Created by Yuriy Aizenberg
 */
class SelectLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var progressCenter: View
    private lateinit var edtSelectLocation: MaterialAutoCompleteTextView
    private lateinit var sellLocationAutocompleteAdapter: SellLocationAutocompleteAdapter
    private var location: Location? = null
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var marker: Marker? = null

    override fun onViewInflated(view: View, savedInstanceState: Bundle?) {
        super.onViewInflated(view, savedInstanceState)

        edtSelectLocation = view.findViewById(R.id.edtLocationAutoComplete)
        progressCenter = view.findViewById(R.id.progressCenter)
        mapView = view.findViewById(R.id.mapView)

        location = if (SellCreateDataHolder.location == null) null else Location(
                SellCreateDataHolder.location!!.text,
                SellCreateDataHolder.location?.latPoint,
                SellCreateDataHolder.location?.longPoint)

        edtSelectLocation.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val singleLocation = sellLocationAutocompleteAdapter.items[position]
            location = Location(singleLocation.text, singleLocation.latPoint, singleLocation.longPoint)
            setupLocation()
        }

        setupLocation()

        sellLocationAutocompleteAdapter = SellLocationAutocompleteAdapter(activity, R.layout.item_location_item)
        edtSelectLocation.setAdapter(sellLocationAutocompleteAdapter)
        mapView.getMapAsync(this)
        mapView.onCreate(savedInstanceState)

        view.findViewById<View>(R.id.llHeaderRight).setOnClickListener {
            SellCreateDataHolder.location = location
            activity?.onBackPressed()
        }
    }

    private fun setupLocation() {
        if (location == null) return
        location?.run {
            edtSelectLocation.setText(text)
            if (longPoint != null && latPoint != null && googleMap != null) {

                marker?.remove()

                val latLng = LatLng(
                        latPoint!!.toDouble(),
                        longPoint!!.toDouble())

                val markerOptions = MarkerOptions().position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                marker = googleMap!!.addMarker(markerOptions)
                googleMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))


            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun getLayoutResId() = R.layout.fragment_sell_select_location

    override fun isFooterShow() = false

    override fun getHeaderText() = R.string.select_location

    override fun getHeaderIcon() = R.drawable.ic_back

    override fun onIconClick() {
        super.onIconClick()
        activity?.onBackPressed()
    }

    private fun onLocationResolved(lat: Double, lon: Double): SilentConsumer<List<Address>> {
        return object : SilentConsumer<List<Address>> {
            override fun onConsume(t: List<Address>) {
                progressCenter.visibility = View.GONE
                if (!t.isEmpty()) {
                    val address = t[0]
                    val addressBuilder = StringBuilder()
                    (0..address.maxAddressLineIndex).forEach {
                        addressBuilder.append(address.getAddressLine(it))
                        if (it < address.maxAddressLineIndex) {
                            addressBuilder.append(",")
                        }
                    }
                    location = Location(addressBuilder.toString(), lat.toString(), lon.toString())
                    setupLocation()
                }
            }

        }
    }

    override fun handleException(t: Throwable) {
        progressCenter.visibility = View.GONE
        super.handleException(t)
    }

    override fun onMapReady(p0: GoogleMap?) {
        p0?.uiSettings?.isCompassEnabled = true
        p0?.uiSettings?.isMapToolbarEnabled = true
        p0?.uiSettings?.isMyLocationButtonEnabled = true
        googleMap = p0
        setupLocation()
        p0?.setOnMapClickListener {
            hideKeyboard()
            progressCenter.visibility = View.VISIBLE
            DataProvider.resolveLocation(activity!!, it.latitude, it.longitude, onLocationResolved(it.latitude, it.longitude),
                    object : SilentConsumer<Throwable> {
                        override fun onConsume(t: Throwable) {
                            handleException(t)
                        }
                    })
        }
    }

}