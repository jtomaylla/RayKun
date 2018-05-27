package com.ecandle.raykun.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.helpers.*
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

        login()
    }

    fun buDemoLoginEvent(view:View){
        etEmail.setText(USER_EMAIL)
        etPassword.setText(USER_KEY)
        login()
    }

    fun login()
    {
        val savedSettings = SavedSettings(applicationContext)
        val email = etEmail.getText().toString().trim()
        if (isValidEmail(email)) {
            if (!etPassword.text.isEmpty()){
                if (connectionDetector!!.isConnectingToInternet) {
                    // JT: Loading Progress Bar
                    var dialog = M.setProgressDialog(this)
                    dialog.show()
                    Handler().postDelayed({dialog.dismiss()},3000)
                    //JT
                    val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/authentication/login?email=" + etEmail.text.toString() +"&password="+ etPassword.text.toString()

                    val loadLoginData = pullServerDataTask()

                    val loginData =  loadLoginData.execute(url).get()

                    var json= JSONObject(loginData)

                    logged =json.getBoolean("logged")

                    //dialog.dismiss()

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
                        toast(resources.getString(R.string.enter_valid_email_password), Toast.LENGTH_LONG)
                        return
                    }

                }else {
                    toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
                    //val user_id =  savedSettings.getLoggedUserId()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }

            } else {
                etPassword.setError(resources.getString(R.string.enter_valid_password))
            }
        } else {
            etEmail.setError(resources.getString(R.string.enter_valid_email))
        }

    }
    fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}
