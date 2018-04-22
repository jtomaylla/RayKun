package com.ecandle.raykun.helpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.ecandle.raykun.activities.LoginActivity
import org.json.JSONObject

/**
 * Created by JuanTomaylla on 8/6/17.
 */
class SavedSettings {
    var context:Context?=null
    var sharedRef:SharedPreferences?=null
    constructor(context:Context){
        this.context=context
        sharedRef=context.getSharedPreferences("myRef",Context.MODE_PRIVATE)
    }


    fun saveSettings(key:String,value:String){
        val editor=sharedRef!!.edit()
        editor.putString(key,value)
        editor.commit()
    }

    fun getSettings(key:String): String? {

        return sharedRef!!.getString(key,"")

    }


    fun saveLoginSettings(userID:String){
        val editor=sharedRef!!.edit()
        editor.putString("userID",userID)
        editor.commit()
        loadLoginSettings()
    }

    fun loadLoginSettings(){
        userID = sharedRef!!.getString("userID","0")

        if (userID =="0"){
            val intent=Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
        }
    }

    fun getLoggedUserId(): String? {

        return sharedRef!!.getString("userID","0")

    }

    fun getJsonInvoiceData(): String? {

        return sharedRef!!.getString("jsonInvoiceData","")

    }
    fun getJsonInvoiceDataItem(name:String): String {

        var json= JSONObject(sharedRef!!.getString("jsonInvoiceData",""))


        return json.getJSONArray(name).toString()

    }

    companion object {
        var userID=""
    }
}