package com.ecandle.raykun.activities

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.helpers.CLIENT_LATITUDE
import com.ecandle.raykun.helpers.CLIENT_LONGITUDE
import com.ecandle.raykun.helpers.ITEM_ID
import com.ecandle.raykun.helpers.SavedSettings
import com.ecandle.raykun.models.GeoTrack

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
/**
 * Created by juantomaylla on 4/5/18.
 */
class GeoTrackMapsActivity : FragmentActivity(), OnMapReadyCallback  {

    //WORK WITH USER LOCATION


    private var mMap: GoogleMap? = null
    var clientLongitude = ""
    var clientLatitude =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_track_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val intent = intent ?: return
        val itemId = intent.getIntExtra(ITEM_ID, 0)
        clientLongitude = intent.getStringExtra(CLIENT_LONGITUDE)
        clientLatitude = intent.getStringExtra(CLIENT_LATITUDE)

        checkPermmison()
        LoadGeoTrack()
    }

    var ACCESSLOCATION=123
    fun checkPermmison(){

        if(Build.VERSION.SDK_INT>=23){

            if(ActivityCompat.
                            checkSelfPermission(this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),ACCESSLOCATION)
                return
            }
        }

        GetUserLocation()
    }

    fun GetUserLocation(){
        Toast.makeText(this,"User location access on",Toast.LENGTH_LONG).show()
        //TODO: Will implement later

        var myLocation= MylocationListener()

        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3,3f,myLocation)

        var mythread=myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when(requestCode){

            ACCESSLOCATION->{

                if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    GetUserLocation()
                }else{
                    Toast.makeText(this,"We cannot access to your location",Toast.LENGTH_LONG).show()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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


    }

    var location:Location?=null

    //Get user location

    inner class MylocationListener:LocationListener{


        constructor(){
            location= Location("Start")
            location!!.longitude=0.0
            location!!.longitude=0.0
        }
        override fun onLocationChanged(p0: Location?) {
            location=p0
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(p0: String?) {
            // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(p0: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }


    var oldLocation:Location?=null
    inner class myThread:Thread{

        constructor():super(){
            oldLocation= Location("Start")
            oldLocation!!.longitude=0.0
            oldLocation!!.longitude=0.0
        }

        override fun run(){

            while (true){

                try {

                    if(oldLocation!!.distanceTo(location)==0f){
                        continue
                    }

                    oldLocation=location


                    runOnUiThread {


                        mMap!!.clear()

                        // show me
                        val sydney = LatLng(location!!.latitude, location!!.longitude)
                        mMap!!.addMarker(MarkerOptions()
                                .position(sydney)
                                .title("Me")
                                .snippet(" here is my location lat:"+location!!.latitude+"lng:"+location!!.longitude)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_google_maps_marker)))
                        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14f))

                        // show GeoTracks

                        for(i in 0..listGeoTracks.size-1){

                            var newGeoTrack=listGeoTracks[i]

                            if(newGeoTrack.IsCatch==false){

                                val GeoTrackLoc = LatLng(newGeoTrack.location!!.latitude, newGeoTrack.location!!.longitude)
                                mMap!!.addMarker(MarkerOptions()
                                        .position(GeoTrackLoc)
                                        .title(newGeoTrack.name!!)
                                        .snippet(newGeoTrack.des!! +", power:"+ newGeoTrack!!.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newGeoTrack.image!!)))


                                if (location!!.distanceTo(newGeoTrack.location)<2){
                                    newGeoTrack.IsCatch=true
                                    listGeoTracks[i]=newGeoTrack
                                    playerPower+=newGeoTrack.power!!
                                    Toast.makeText(applicationContext,
                                            "You catch new GeoTrack your new pwoer is " + playerPower,
                                            Toast.LENGTH_LONG).show()

                                }

                            }
                        }





                    }

                    Thread.sleep(1000)

                }catch (ex:Exception){}


            }

        }

    }


    var playerPower=0.0
    var listGeoTracks=ArrayList<GeoTrack>()

    fun  LoadGeoTrack(){

        var savedSettings = SavedSettings(applicationContext)


        var myCurrentLat = savedSettings.getSettings("myCurrentLat").toString()
        var myCurrentLon = savedSettings.getSettings("myCurrentLon").toString()
//        listGeoTracks.add(GeoTrack(R.drawable.ic_smartbif_24dp,
//                "Charmander", "Charmander living in japan", 55.0, -12.13588935, -77.014846647263))
        listGeoTracks.add(GeoTrack(R.drawable.ic_google_maps_marker_verde,
                "Bulbasaur", "Client Location", 90.5, clientLatitude.toDouble(), clientLongitude.toDouble()))
        listGeoTracks.add(GeoTrack(R.drawable.ic_google_maps_marker,
                "Squirtle", "My Current Location", 33.5, myCurrentLat.toDouble(), myCurrentLon.toDouble()))

    }

}

