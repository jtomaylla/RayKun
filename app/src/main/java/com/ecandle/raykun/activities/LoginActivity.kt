package com.ecandle.raykun.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.ecandle.raykun.R
import com.ecandle.raykun.helpers.App.Companion.prefs
import com.ecandle.raykun.helpers.Operations
import com.ecandle.raykun.helpers.SavedSettings
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


class LoginActivity : AppCompatActivity() {
    private val LOG_TAG = LoginActivity::class.java.simpleName
    var logged = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }


    fun buLoginEvent(view:View){
        val pdLoading = ProgressDialog(this)

        //this method will be running on UI thread
        pdLoading.setMessage("\tLoading...")
        pdLoading.setCancelable(false)
        pdLoading.show()

//        val builder = AlertDialog.Builder(this)
//        val dialogView = layoutInflater.inflate(R.layout.progress_dialog,null)
//        val message = dialogView.findViewById<TextView>(R.id.message)
//        message.text="Downloading..."
//        builder.setView(dialogView)
//        builder.setCancelable(false)
//        builder.show()
//
//        val dialog = builder.create()
//        dialog.dismiss()
//        dialog.show()
//
//        Handler().postDelayed({dialog.dismiss()},5000)
        // user login
        //val url="http://10.0.2.2/Twitter/Login.php?email=" + etEmail.text.toString() +"&password="+ etPassword.text.toString()
        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/authentication/login?email=" + etEmail.text.toString() +"&password="+ etPassword.text.toString()
        MyAsyncTask().execute(url)

        pdLoading.dismiss()

        if(logged) {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            finish()
        }
    }

    fun buRegisterUserEvent(view:View){
        val intent=Intent(this, DemoLogin::class.java)
        startActivity(intent)
    }


    // CALL HTTP
    inner class MyAsyncTask: AsyncTask<String, String, String>() {


        override fun onPreExecute() {
            //Before task started

        }
        override fun doInBackground(vararg p0: String?): String {
            try {

                val url= URL(p0[0])

                val urlConnect=url.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000
                val op= Operations()
                var inString= op.ConvertStreamToString(urlConnect.inputStream)
                //Cannot access to ui
                publishProgress(inString)
            }catch (ex:Exception){}


            return " "

        }

        override fun onProgressUpdate(vararg values: String?) {
            try{
                var json= JSONObject(values[0])

                logged =json.getBoolean("logged")

                if (logged){
                    //logged = true

                    val user_id= json.getJSONObject("user").getString("staffid")

                    val saveSettings = SavedSettings(applicationContext)
                    saveSettings.saveSettings(user_id)

                    prefs!!.logged_user_id = user_id
                    Log.i(LOG_TAG, "user_id:"+prefs!!.logged_user_id)
                    Log.i(LOG_TAG, "user_id:"+saveSettings.getLoggedUserId())
                    //finish()
                }else{
                    //Toast.makeText(applicationContext,json.getString("logged"), Toast.LENGTH_LONG).show()
                    //logged = false
                }

            }catch (ex:Exception){}

        }

        override fun onPostExecute(result: String?) {

            //after task done
            //pdLoading.dismiss()
        }


    }


}
