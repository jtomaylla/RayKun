package com.ecandle.raykun.tasks

import android.os.AsyncTask
import com.ecandle.raykun.helpers.Operations
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
/**
 * Created by juantomaylla on 15/04/18.
 */
// CALL HTTP
class pullServerDataTask : AsyncTask<String, String, String>() {


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

        }catch (ex:Exception){}


        return ""

    }

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


