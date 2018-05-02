package com.ecandle.raykun.activities
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.util.Log
import android.view.Gravity
import android.view.View
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.fragments.LeadListFragment
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.models.Lead
import com.ecandle.raykun.tasks.loadLeadDataTask
import com.simplemobiletools.commons.extensions.beVisible
import kotlinx.android.synthetic.main.activity_lead_list.*

class LeadListActivity : SimpleActivity() {
    private val LOG_TAG = LeadListActivity::class.java.simpleName
    private var mUserId: String? = null
    private var fabAnchorId: Int = View.NO_ID
    private val fabParams: CoordinatorLayout.LayoutParams get() = (lead_fab.layoutParams as CoordinatorLayout.LayoutParams)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lead_list)

        var connectionDetector = ConnectionDetector(this)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        supportActionBar?.title = resources.getString(R.string.leads)

        lead_fab.setOnClickListener { launchNewLeadIntent() }

        hideFab()

        mUserId = intent.getStringExtra(USER_ID)
        val intent = intent ?: return

        if (connectionDetector!!.isConnectingToInternet) {
            loadLeads()
        }

        fillLeadsList()

    }

    fun showFab() {
        fabParams.anchorId = fabAnchorId
        fabParams.gravity = Gravity.NO_GRAVITY
        lead_fab.show()
    }

    fun hideFab() {
        fabParams.anchorId = View.NO_ID
        fabParams.gravity = Gravity.END.or(Gravity.BOTTOM)
        lead_fab.hide()
    }
    fun launchNewLeadIntent() {
        Intent(applicationContext, LeadActivity::class.java).apply {
            putExtra(USER_ID, mUserId)
            startActivity(this)
        }
    }

    private fun fillLeadsList() {
        lead_list_holder.beVisible()
        supportFragmentManager.beginTransaction().replace(R.id.lead_list_holder, LeadListFragment(), "").commit()
    }

    fun loadLeads(){
        if (dbHelper.isTableExists(dbHelper.LEADS_TABLE_NAME)) {
            dbHelper.initLeadsTable()
        }else{
            dbHelper.createLeadsTable()
        }
        //http://ecandlemobile.com/RayKun/webservice/index.php/admin/leads/showLeads
        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/leads/showLeads"
        val loadLeadData = loadLeadDataTask(this)

        val leadsData =  loadLeadData.execute(url).get()

        Log.d("loadLeadData",leadsData.toString())

        for (lead in leadsData){
            saveLead(lead)
        }
    }
    private fun saveLead(lead: Lead) {

        dbHelper.insertLead(lead)
        Log.d(LOG_TAG,"lead added")

    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_product_list, menu)
//        menu.findItem(R.id.exit).isVisible = true
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.exit -> finish()
//            else -> return super.onOptionsItemSelected(item)
//        }
//        return true
//    }
}
