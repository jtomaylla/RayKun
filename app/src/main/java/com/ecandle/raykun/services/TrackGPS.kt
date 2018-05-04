package com.ecandle.raykun.services

import android.app.AlertDialog
import android.app.Service
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast

class TrackGPS(private val mContext: Context) : Service(), LocationListener {


    internal var checkGPS = false


    internal var checkNetwork = false

    internal var canGetLocation = false

    internal var loc: Location? = null
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    protected var locationManager: LocationManager? = null

    // getting GPS status
    // getting network status
    // First get location from Network Provider
    // if GPS Enabled get lat/long using GPS Services
    val location: Location?
        get() {

            try {
                locationManager = mContext
                        .getSystemService(Context.LOCATION_SERVICE) as LocationManager
                checkGPS = locationManager!!
                        .isProviderEnabled(LocationManager.GPS_PROVIDER)
                checkNetwork = locationManager!!
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!checkGPS && !checkNetwork) {
                    Toast.makeText(mContext, "No Service Provider Available", Toast.LENGTH_SHORT).show()
                } else {
                    this.canGetLocation = true
                    if (checkNetwork) {
                        Toast.makeText(mContext, "Network", Toast.LENGTH_SHORT).show()

                        try {
                            locationManager!!.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                            Log.d("Network", "Network")
                            if (locationManager != null) {
                                loc = locationManager!!
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                            }

                            if (loc != null) {
                                latitude = loc!!.getLatitude()
                                longitude = loc!!.getLongitude()
                            }
                        } catch (e: SecurityException) {

                        }

                    }
                }
                if (checkGPS) {
                    Toast.makeText(mContext, "GPS", Toast.LENGTH_SHORT).show()
                    if (loc == null) {
                        try {
                            locationManager!!.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES.toFloat(), this)
                            Log.d("GPS Enabled", "GPS Enabled")
                            if (locationManager != null) {
                                loc = locationManager!!
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (loc != null) {
                                    latitude = loc!!.getLatitude()
                                    longitude = loc!!.getLongitude()
                                }
                            }
                        } catch (e: SecurityException) {

                        }

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return loc
        }

    init {
        location
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(mContext)


        alertDialog.setTitle("GPS Not Enabled")

        alertDialog.setMessage("Do you wants to turn On GPS")


        alertDialog.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                mContext.startActivity(intent)
            }
        })


        alertDialog.setNegativeButton("No", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.cancel()
            }
        })


        alertDialog.show()
    }


    fun stopUsingGPS() {
        if (locationManager != null) {

            locationManager!!.removeUpdates(this@TrackGPS)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {

    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

    }

    override fun onProviderEnabled(s: String) {

    }

    override fun onProviderDisabled(s: String) {

    }

    companion object {


        private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Long = 10


        private val MIN_TIME_BW_UPDATES = (1000 * 60 * 1).toLong()
    }
}