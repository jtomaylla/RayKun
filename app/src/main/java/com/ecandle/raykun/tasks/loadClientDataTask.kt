package com.ecandle.raykun.tasks

//import com.ecandle.raykun.tasks.AsyncTaskIncidente.loadEventDataTask.Companion.READ_TIMEOUT
import android.content.Context
import android.util.Log
import com.ecandle.raykun.helpers.Operations
import com.ecandle.raykun.models.Client
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by juantomaylla on 19/02/18.
 */
class loadClientDataTask(internal var mycontext: Context) : android.os.AsyncTask<String, String, List<Client>>() {



    override fun onPreExecute() {
        super.onPreExecute()

        //this method will be running on UI thread
//            pdLoading.setMessage("\tLoading...")
//            pdLoading.setCancelable(false)
//            pdLoading.show()

    }


    override fun doInBackground(vararg params: String): List<Client>? {
        val url= URL(params[0])
        val conn=url.openConnection() as HttpURLConnection

        try {

            // Setup HttpURLConnection class to send and receive data from php and mysql
            //conn = url!!.openConnection() as HttpURLConnection
            //conn.readTimeout = READ_TIMEOUT
            conn.connectTimeout = CONNECTION_TIMEOUT
            //conn.requestMethod = "GET"

            // setDoOutput to true as we recieve data from json file
            //conn.doOutput = true

        } catch (eIO: IOException) {
            // TODO Auto-generated catch block
            eIO.printStackTrace()
            //return e1.toString();
        }

        try {

            val response_code = conn.responseCode
            val data = java.util.ArrayList<Client>()
            // Check if successful connection made
            if (response_code == HttpURLConnection.HTTP_OK) {
                // Read data sent from server
                val op= Operations()
                var result = op.ConvertStreamToString(conn.inputStream)
                //pdLoading.dismiss();
                // Process data
                val jArray = JSONArray(result)

                // Extract data from json and store into ArrayList as class objects
                for (i in 0 until jArray.length()) {
                    val jsondata: JSONObject = jArray.getJSONObject(i)
                    val clientData = Client(
                            jsondata.getInt("userid"),
                            jsondata.getString("company"),
                            jsondata.getString("vat"),
                            jsondata.getString("phonenumber"),
                            jsondata.getString("country"),
                            jsondata.getString("city"),
                            jsondata.getString("zip"),
                            jsondata.getString("state"),
                            jsondata.getString("address"),
                            jsondata.getString("website"),
                            jsondata.getString("datecreated"),
                            jsondata.getString("active"),
                            jsondata.getString("leadid"),
                            jsondata.getString("billing_street"),
                            jsondata.getString("billing_city"),
                            jsondata.getString("billing_state"),
                            jsondata.getString("billing_zip"),
                            jsondata.getString("billing_country"),
                            jsondata.getString("shipping_street"),
                            jsondata.getString("shipping_city"),
                            jsondata.getString("shipping_state"),
                            jsondata.getString("shipping_zip"),
                            jsondata.getString("shipping_country"),
                            jsondata.getString("longitude"),
                            jsondata.getString("latitude"),
                            jsondata.getString("default_language"),
                            jsondata.getString("default_currency"),
                            jsondata.getString("show_primary_contact"),
                            jsondata.getString("addedfrom"),
                            jsondata.getString("contact_name"),
                            jsondata.getString("contact_email"),
                            jsondata.getString("country_name")
                    )
                    data.add(clientData)
                }

                return data

            } else {

                return null
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            //return e.toString();
        } catch (e1: IOException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
            //return e1.toString();}

        } finally {
            conn.disconnect()
        }


        return null
    }

    override fun onPostExecute(result: List<Client>?) {

        //after task done
        //pdLoading.dismiss()
        val mydata = result
        //mydata = data!!
        Log.d("loadClientDataTask",result.toString())
    }

    companion object {
        // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
        val CONNECTION_TIMEOUT = 7000
        val READ_TIMEOUT = 15000
    }
}
