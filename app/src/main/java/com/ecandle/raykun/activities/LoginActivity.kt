package com.ecandle.raykun.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.SavedSettings
import com.ecandle.raykun.tasks.pullServerDataTask
import com.simplemobiletools.commons.extensions.toast
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject




class LoginActivity : AppCompatActivity() {
    private val LOG_TAG = LoginActivity::class.java.simpleName
    var logged = false
    var connectionDetector: ConnectionDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        connectionDetector = ConnectionDetector(this)
    }


    fun buLoginEvent(view:View){
        val savedSettings = SavedSettings(applicationContext)
//        val pdLoading = ProgressDialog(this)

        //this method will be running on UI thread
//        pdLoading.setMessage("\tLoading...")
//        pdLoading.setCancelable(false)
//        pdLoading.show()

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

        if (connectionDetector!!.isConnectingToInternet) {


            val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/authentication/login?email=" + etEmail.text.toString() +"&password="+ etPassword.text.toString()

            val loadLoginData = pullServerDataTask()

            val loginData =  loadLoginData.execute(url).get()


            var json= JSONObject(loginData)


            logged =json.getBoolean("logged")


    //        pdLoading.dismiss()

            if(logged) {

                val user_id= json.getJSONObject("user").getString("staffid")


                savedSettings.saveLoginSettings(user_id)
                savedSettings.saveSettings("jsonUserAccessData",json.getJSONObject("user").toString())

                //Saving Invoice Params on Shared Preferences Json Format
                val url1="http://ecandlemobile.com/RayKun/webservice/index.php/admin/invoice_items/index"
                val loadInvoiceData = pullServerDataTask()
                val invoiceData =  loadInvoiceData.execute(url1).get()
                var json1= JSONObject(invoiceData)
                savedSettings.saveSettings("jsonInvoiceData",json1.toString())

                // Saving Clients Params (Project, proposal, estimate and invoice  statuses, Client Groups,
                // etc on Shared Preferences Json Format

                val url2="http://ecandlemobile.com/RayKun/webservice/index.php/admin/clients/showIndex"
                val loadClientData = pullServerDataTask()
                val clientData =  loadClientData.execute(url2).get()
                var json2= JSONObject(clientData)
                savedSettings.saveSettings("jsonClientData",json2.toString())



                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                //finish()
            }else{
                //finish()
                return
            }


        }else {
            toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
            //val user_id =  savedSettings.getLoggedUserId()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)

        }
    }

    fun buRegisterUserEvent(view:View){
        val intent=Intent(this, DemoLogin::class.java)
        startActivity(intent)
    }

}
