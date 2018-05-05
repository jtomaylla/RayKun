package com.ecandle.raykun.helpers

import android.location.Location
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
/**
 * Created by hussienecandle on 8/6/17.
 */
class Operations{

    fun ConvertStreamToString(inputStream: InputStream):String{

        val bufferReader= BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String=""

        try {
            do{
                line=bufferReader.readLine()
                if(line!=null){
                    AllString+=line
                }
            }while (line!=null)
            inputStream.close()
        }catch (ex:Exception){}
        return AllString
    }

    fun calculateDistanceInKilometer(fromLocLat: String, fromLocLon: String, toLocLat: String, toLocLon: String): String {
        //var context: Context? = Context
        var myLoc = Location("MyLocation")
        myLoc!!.latitude = fromLocLat.toDouble()
        myLoc!!.longitude = fromLocLon.toDouble()

        var clientLoc = Location("ClientLocation")
        clientLoc!!.latitude = toLocLat.toDouble()
        clientLoc!!.longitude =  toLocLon.toDouble()

        val distance = clientLoc!!.distanceTo(myLoc!!) / 1000
        //val textDistance = context!!.getResources().getString(R.string.distance_away)
        //val distanceText = String.format(Locale.getDefault(), "%.2f", distance) + textDistance //" km Away"
        val distanceText = String.format(Locale.getDefault(), "%.2f", distance) + " km Away"
        return distanceText
    }


}