package com.ecandle.raykun.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.ecandle.raykun.BuildConfig
import com.ecandle.raykun.R
import com.ecandle.raykun.helpers.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val LOG_TAG = MainActivity::class.java.simpleName
    private var mDrawerLayout: DrawerLayout? = null
    var op="*"
    private var mUserId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the toolbar.
        //val toolbar = findViewById(R.id.toolbar) as Toolbar
        //setSupportActionBar(toolbar);
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        ab.setDisplayHomeAsUpEnabled(true)

        mDrawerLayout = findViewById(R.id.drawer_layout) as DrawerLayout
        mDrawerLayout!!.setStatusBarBackground(R.color.color_primary_dark)

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        if (navigationView != null) {
            setupDrawerContent(navigationView)
        }
        val savedSettings = SavedSettings(applicationContext)
//TODO chequear si user id esta logeado OJO verificar si funciona!!
//
        mUserId = savedSettings.getLoggedUserId()
        if (mUserId == "0")
        {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            //finish()
        }
    }
    fun opc_event(view: View){

        val buSelect= view as LinearLayout
        when(buSelect.id) {

            calendar.id -> {
                startCalendar()
            }
            tasks.id -> {
                startTasks()
            }
            products.id -> {
                startProducts()
            }
            contacts.id -> {
                startContacts()
            }

//            sales.id -> {
//
//                op="sales"
//            }

            clients.id -> {
                startClients()
            }
//            reports.id -> {
//                startReports()
//                op="reports"
//            }
            dashboard.id -> {
                startDashboard()
            }
            geotrack.id -> {
                startGeotrack()
            }
            leads.id -> {
                startLeads()
            }
//            expenses.id -> {
//
//                op="expenses"
//            }
            geolocation.id -> {
                startGeoRouting()
            }


        }
        //Toast.makeText(this,"ID:"+ op, Toast.LENGTH_LONG).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Open the navigation drawer when the home icon is selected from the toolbar.
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener(
                NavigationView.OnNavigationItemSelectedListener { menuItem ->
                    when (menuItem.itemId) {

                        R.id.exit_menu -> {
                            //Toast.makeText(this@MainActivity, "You have selected Exit App", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.calendar_menu -> {
                            //Toast.makeText(this@MainActivity, "You have selected Calendar Menu", Toast.LENGTH_SHORT).show()
                            //val intent = Intent(this@MainActivity, CalendarGActivity::class.java)
                            startCalendar()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.tasks_menu -> {
                            //Toast.makeText(this@MainActivity, "You have selected  Tasks", Toast.LENGTH_SHORT).show()
                            startTasks()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.products_menu -> {
                            //Toast.makeText(this@MainActivity, "You have selected products ", Toast.LENGTH_SHORT).show()
                            startProducts()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.leads_menu -> {
                            startLeads()
                            return@OnNavigationItemSelectedListener true
                        }
//                        R.id.sales_menu -> {
//                            Toast.makeText(this@MainActivity, "You have selected surveys", Toast.LENGTH_SHORT).show()
//                            //val intent = Intent(this@MainActivity, CalendarGActivity::class.java)
//                            //startActivity(intent)
//                            return@OnNavigationItemSelectedListener true
//                        }
                        R.id.clients_menu -> {
                            //Toast.makeText(this@MainActivity, "You have selected  clients", Toast.LENGTH_SHORT).show()
                            startClients()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.reports_menu -> {
                            Toast.makeText(this@MainActivity, "You have selected  reports", Toast.LENGTH_SHORT).show()
                            //val intent = Intent(this@MainActivity, CalendarGActivity::class.java)
                            //startActivity(intent)
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.dashboard_menu -> {
                            startDashboard()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.geotrack_menu -> {
                            //Toast.makeText(this@MainActivity, "You have selected geotrack", Toast.LENGTH_SHORT).show()
                            startGeotrack()
                            return@OnNavigationItemSelectedListener true
                        }
//                        R.id.dialing_menu -> {
//                            Toast.makeText(this@MainActivity, "You have selected  dialing", Toast.LENGTH_SHORT).show()
//                            //val intent = Intent(this@MainActivity, CalendarGActivity::class.java)
//                            //startActivity(intent)
//                            return@OnNavigationItemSelectedListener true
//                        }
//                        R.id.expenses_menu -> {
//                            Toast.makeText(this@MainActivity, "You have selected expenses", Toast.LENGTH_SHORT).show()
//                            //val intent = Intent(this@MainActivity, CalendarGActivity::class.java)
//                            //startActivity(intent)
//                            return@OnNavigationItemSelectedListener true
//                        }
                        R.id.geolocation_menu -> {
                            startGeoRouting()
                            return@OnNavigationItemSelectedListener true
                        }
                        R.id.about_menu -> {
                            startAbout()
                            return@OnNavigationItemSelectedListener true
                        }
                        else -> {
                        }
                    }
                    // Close the navigation drawer when an item is selected.
                    menuItem.isChecked = true
                    mDrawerLayout!!.closeDrawers()
                    true
                })
    }

    fun startCalendar(){
        op="calendar"

        when {
            intent.extras?.containsKey(DAY_CODE) == true -> Intent(this, CalendarActivity::class.java).apply {
                putExtra(DAY_CODE, intent.getStringExtra(DAY_CODE))
                startActivity(this)
            }
            intent.extras?.containsKey(EVENT_ID) == true -> Intent(this, CalendarActivity::class.java).apply {
                putExtra(EVENT_ID, intent.getIntExtra(EVENT_ID, 0))
                putExtra(EVENT_OCCURRENCE_TS, intent.getIntExtra(EVENT_OCCURRENCE_TS, 0))
                startActivity(this)
            }
            else -> Intent(this, CalendarActivity::class.java).apply {
                putExtra(USER_ID, mUserId)
                startActivity(this)
            }
        }

    }
    fun startTasks(){
        Intent(this, TaskListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
        op=getString(R.string.tasks)
    }
    fun startProducts(){
        Intent(this, ProductListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
        op="products"
    }
    fun startContacts(){
        Intent(this, ContactListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
        op="contacts"
    }
    fun startClients(){
        Intent(this, ClientListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
        op="clients"
    }
    fun startGeotrack(){
        Intent(this, GeoTrackListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            putExtra(MAP_MODE, "1")
            startActivity(this)
        }
        op="geotrack"
    }
    fun startDashboard(){
        //Toast.makeText(this@MainActivity, "You don't have access to dashboard", Toast.LENGTH_SHORT).show()
        Intent(this, AnalyticsActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
        op="dashboard"
    }
    fun startGeoRouting(){
        Intent(this, GeoTrackListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            putExtra(MAP_MODE, "2")
            startActivity(this)
        }
        op="GeoRouting"
    }
    fun startLeads(){

        Intent(this, LeadListActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
        op="leads"
    }

    fun startAbout(){
        //Toast.makeText(this@MainActivity, "You don't have access to reports", Toast.LENGTH_SHORT).show()

        startAboutActivity(R.string.app_name, LICENSE_KOTLIN or LICENSE_JODA or LICENSE_STETHO or LICENSE_MULTISELECT or LICENSE_GSON or
                LICENSE_LEAK_CANARY, BuildConfig.VERSION_NAME)
        op="About"
    }
    fun startAboutActivity(appNameId: Int, licenseMask: Int, versionName: String) {
        Intent(this, AboutActivity::class.java).apply {
            putExtra(APP_NAME, getString(appNameId))
            putExtra(APP_LICENSES, licenseMask)
            putExtra(APP_VERSION_NAME, versionName)
            startActivity(this)
        }
    }
}
