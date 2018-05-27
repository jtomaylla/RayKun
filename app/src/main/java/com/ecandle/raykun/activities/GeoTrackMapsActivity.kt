package com.ecandle.raykun.activities

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.ecandle.raykun.R
import com.ecandle.raykun.helpers.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import java.util.*
/**
 * Created by juantomaylla on 5/5/18.
 */
class GeoTrackMapsActivity : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private val TAG = "so47492459"
    var clientLongitude = ""
    var clientLatitude = ""
    var myCurrentLat = ""
    var myCurrentLon = ""
    var clientCompany = ""
    var clientAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val intent = intent ?: return
        val itemId = intent.getIntExtra(ITEM_ID, 0)
        clientLongitude = intent.getStringExtra(CLIENT_LONGITUDE)
        clientLatitude = intent.getStringExtra(CLIENT_LATITUDE)
        clientCompany = intent.getStringExtra(CLIENT_COMPANY)
        clientAddress = intent.getStringExtra(CLIENT_ADDRESS)
        // JT: Check empty Lat and Long
        if (clientLatitude.isEmpty() || clientLongitude.isEmpty()){
            clientLongitude = CURRENT_LONGITUDE
            clientLatitude = CURRENT_LATITUDE
        }
        // JT
        var savedSettings = SavedSettings(applicationContext)
        myCurrentLat = savedSettings.getSettings("myCurrentLat").toString()
        myCurrentLon = savedSettings.getSettings("myCurrentLon").toString()

        checkPermmison()
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        //val barcelona = LatLng(41.385064, 2.173403)
        val client = LatLng(clientLatitude.toDouble(), clientLongitude.toDouble())
        mMap!!.addMarker(MarkerOptions()
                .position(client).title("Marker in Client")
                .title(clientCompany)
                .snippet(clientAddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_maps_marker_verde))
        )

        //val madrid = LatLng(40.416775, -3.70379)
        val myLocation = LatLng(myCurrentLat.toDouble(), myCurrentLon.toDouble())
        mMap!!.addMarker(MarkerOptions().position(myLocation).title("Marker in My Location"))

        //val zaragoza = LatLng(41.648823, -0.889085)

        //Define list to get all latlng for the route

        var path = ArrayList<LatLng>()
        //Execute Directions API request
        val context = GeoApiContext.Builder()
                .apiKey("AIzaSyBrPt88vvoPDDn_imh-RzCXl5Ha2F2LYig")
                .build()
        //val req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379")
        val req = DirectionsApi.getDirections(context, myCurrentLat+ "," + myCurrentLon, clientLatitude + "," + clientLongitude)

        try {
            val res = req.await()

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.size > 0) {
                val route = res.routes[0]

                if (route.legs != null) {
                    for (i in 0 until route.legs.size) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in 0 until leg.steps.size) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.size > 0) {
                                    for (k in 0 until step.steps.size) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            val coords1 = points1!!.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        val coords = points!!.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.localizedMessage)
        }

        //Draw the polyline
        if (path.size > 0) {
            val opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5f)
            mMap!!.addPolyline(opts)
        }

        mMap!!.uiSettings.isZoomControlsEnabled = true

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 10f))
    }

    var ACCESSLOCATION=123
    fun checkPermmison(){

        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat.
                            checkSelfPermission(this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }
        //GetUserLocation()
    }
}
