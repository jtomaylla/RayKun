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

    fun calculateDistanceInKilometer(myLocLat: String,myLocLon: String, clientLocLat: String,clientLocLon: String): String {

        var myLoc = Location("MyLocation")
        myLoc!!.latitude = myLocLat.toDouble()
        myLoc!!.longitude = myLocLon.toDouble()

        var clientLoc = Location("")
        clientLoc!!.latitude = clientLocLat.toDouble()
        clientLoc!!.longitude =  clientLocLon.toDouble()

        val distance = clientLoc!!.distanceTo(myLoc!!) / 1000
        val distanceText = String.format(Locale.getDefault(), "%.2f", distance) + " km Away"
        return distanceText
    }


}