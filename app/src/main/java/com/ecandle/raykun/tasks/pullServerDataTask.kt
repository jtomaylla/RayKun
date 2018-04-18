package com.ecandle.raykun.tasks

import android.os.AsyncTask
import com.ecandle.raykun.helpers.Operations
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

// CALL HTTP
class loadLoginDataTask: AsyncTask<String, String, String>() {


    override fun onPreExecute() {
        //Before task started

    }
    override fun doInBackground(vararg p0: String?): String {
        val url= URL(p0[0])

        val urlConnect=url.openConnection() as HttpURLConnection

        try {

            urlConnect.connectTimeout= CONNECTION_TIMEOUT

        } catch (eIO: IOException) {
            // TODO Auto-generated catch block
            eIO.printStackTrace()
            //return e1.toString();
        }

        try {
            val response_code = urlConnect.responseCode

            if (response_code == HttpURLConnection.HTTP_OK) {
                val op= Operations()
                var result = op.ConvertStreamToString(urlConnect.inputStream)
                return result

            }

            //Cannot access to ui
            //publishProgress(inString)

//            val json = JSONArray(result)
//
//            return json
//
//            val logged =json.getBoolean("logged")
//
//            if (logged){
//                //logged = true
//
//                val user_id= json.getJSONObject("user").getString("staffid")
//
//                val saveSettings = SavedSettings(applicationContext)
//                saveSettings.saveSettings(user_id)
//
//                App.prefs!!.logged_user_id = user_id
//                Log.i(LOG_TAG, "user_id:"+ App.prefs!!.logged_user_id)
//                Log.i(LOG_TAG, "user_id:"+saveSettings.getLoggedUserId())
//                //finish()
//            }else{
//                //Toast.makeText(applicationContext,json.getString("logged"), Toast.LENGTH_LONG).show()
//                //logged = false
//            }


        }catch (ex:Exception){}


        return ""

    }

//    override fun onProgressUpdate(vararg values: String?) {
//        try{
//            var json= JSONObject(values[0])
//
//            logged =json.getBoolean("logged")
//
//            if (logged){
//                //logged = true
//
//                val user_id= json.getJSONObject("user").getString("staffid")
//
//                val saveSettings = SavedSettings(applicationContext)
//                saveSettings.saveSettings(user_id)
//
//                App.prefs!!.logged_user_id = user_id
//                Log.i(LOG_TAG, "user_id:"+ App.prefs!!.logged_user_id)
//                Log.i(LOG_TAG, "user_id:"+saveSettings.getLoggedUserId())
//                //finish()
//            }else{
//                //Toast.makeText(applicationContext,json.getString("logged"), Toast.LENGTH_LONG).show()
//                //logged = false
//            }
//
//        }catch (ex:Exception){}
//
//    }

    override fun onPostExecute(result: String?) {

        //after task done
        //pdLoading.dismiss()
    }

    companion object {
        // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
        val CONNECTION_TIMEOUT = 10000
        val READ_TIMEOUT = 15000
    }

}

/**
 * Created by juantomaylla on 15/04/18.
 */
