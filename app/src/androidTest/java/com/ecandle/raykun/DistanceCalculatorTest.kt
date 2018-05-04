package com.ecandle.raykun

import android.location.Location
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class DistanceCalculatorTest {

    @org.junit.Test
    @Throws(Exception::class)
    fun distanceValidator_CorrectDistance_ReturnsTrue() {
        assertThat(calculateDistanceInKilometer("-12.13588935","-77.014846647263","-12.15688935","-77.01494089127") > 0.0, `is`(true))
    }

    fun calculateDistanceInKilometer(myLocLat: String,myLocLon: String, clientLocLat: String,clientLocLon: String): Float {
//        var myLoc:Location?=null
//        var clientLoc:Location?=null

        var myLoc = Location("MyLocation")
        myLoc!!.latitude = myLocLat.toDouble()
        myLoc!!.longitude = myLocLon.toDouble()

        var clientLoc = Location("")
        clientLoc!!.latitude = clientLocLat.toDouble()
        clientLoc!!.longitude =  clientLocLon.toDouble()

        val distance = clientLoc!!.distanceTo(myLoc!!) / 1000
        val distanceText = String.format(Locale.getDefault(), "%.2f", distance) + " km Away"
        return distance
    }

}
