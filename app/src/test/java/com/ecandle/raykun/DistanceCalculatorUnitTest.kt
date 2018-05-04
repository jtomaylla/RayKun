package com.ecandle.raykun

import android.location.Location
import org.hamcrest.core.Is.`is`
import org.junit.Assert.assertThat
import org.junit.Test
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DistanceCalculatorUnitTest {

    @Test
    fun distanceValidator_CorrectDistance_ReturnsTrue() {
        assertThat(!calculateDistanceInKilometer("-12.13588935","-77.014846647263","-12.15688935","-77.01494089127").isBlank(), `is`(true))
    }

    var myLoc:Location?=null
    var clientLoc:Location?=null
    fun calculateDistanceInKilometer(myLocLat: String,myLocLon: String, clientLocLat: String,clientLocLon: String): String {
        myLoc = Location("MyLocation")
        myLoc!!.latitude = myLocLat.toDouble()
        myLoc!!.longitude = myLocLon.toDouble()

        clientLoc = Location("")
        clientLoc!!.latitude = clientLocLat.toDouble()
        clientLoc!!.longitude =  clientLocLon.toDouble()

        val distance = clientLoc!!.distanceTo(myLoc!!) / 1000
        return String.format(Locale.getDefault(), "%.2f", distance) + " km Away"

    }

//    listPockemons.add(Pockemon(R.drawable.charmander,
//    "Charmander", "Charmander living in japan", 55.0, -12.13588935, -77.014846647263))
//    listPockemons.add(Pockemon(R.drawable.bulbasaur,
//    "Bulbasaur", "Bulbasaur living in usa", 90.5, -12.15688935, -77.01494089127))

}
