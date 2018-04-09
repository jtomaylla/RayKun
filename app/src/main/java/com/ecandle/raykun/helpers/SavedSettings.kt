package com.ecandle.raykun.helpers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.ecandle.raykun.activities.LoginActivity

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

    fun saveSettings(userID:String){
        val editor=sharedRef!!.edit()
        editor.putString("userID",userID)
        editor.commit()
        loadSettings()
    }

    fun loadSettings(){
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

    companion object {
        var userID=""
    }
}