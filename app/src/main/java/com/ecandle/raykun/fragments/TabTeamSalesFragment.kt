package com.ecandle.raykun.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.adapters.StaffListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.models.Client
import com.ecandle.raykun.models.Staff
import com.ecandle.raykun.tasks.loadStaffDataTask
import com.simplemobiletools.commons.extensions.beGoneIf
import com.simplemobiletools.commons.extensions.beVisibleIf
import com.simplemobiletools.commons.extensions.toast
import kotlinx.android.synthetic.main.fragment_tab_staff_list.view.*


/**
 * A simple [Fragment] subclass.
 */

class TabTeamSalesFragment: Fragment()  {

    private val LOG_TAG = TabTeamSalesFragment ::class.java.simpleName
    private var mStaffs: List<Staff> = ArrayList()
    lateinit var mView: View
    private var mStaffListAdapter: StaffListAdapter? = null
    lateinit var mStaff: Staff
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //setHasOptionsMenu(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_tab_staff_list, container, false)
        val placeholderText = String.format(getString(R.string.two_string_placeholder), "${getString(R.string.no_upcoming_records)}\n", getString(R.string.add_some_records))
        mView.staff_empty_list_placeholder.text = placeholderText
        return mView
    }

    override fun onResume() {
        super.onResume()
        var connectionDetector = ConnectionDetector(context!!.applicationContext)
        if (connectionDetector!!.isConnectingToInternet) {
            ///context!!.dbHelper.initStaffsTable()
            loadStaffs()
        }else{
            context!!.toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)
        }
        checkStaffs()
    }

    fun checkStaffs() {
        context!!.dbHelper.getStaffs() {
            receivedStaffs(it)
        }
    }

    private fun receivedStaffs(items: List<Staff>) {
        if (context == null || activity == null)
            return

        mStaffs = items
        val listStaffs = ArrayList<Staff>(mStaffs.size)
        val replaceDescription = context!!.config.replaceDescription
        val sorted = mStaffs.sortedWith(compareBy({ it.staff_id}, { it.name }, { it.date_assigned }, { if (replaceDescription) it.name else it.name }))
        val sublist = sorted.subList(0, Math.min(sorted.size, 100))
        sublist.forEach {
            listStaffs.add(Staff(it.staff_id,it.customer_id , it.date_assigned,it.name, it.email, it.phonenumber))
        }

        mStaffListAdapter = StaffListAdapter(context!!, listStaffs)

        activity?.runOnUiThread {
            mView.staff_list.apply {
                this@apply.adapter = mStaffListAdapter
            }
            checkPlaceholderVisibility()
        }
    }

    private fun checkPlaceholderVisibility() {
        mView.staff_empty_list_placeholder.beVisibleIf(mStaffs.isEmpty())
        mView.staff_list.beGoneIf(mStaffs.isEmpty())
        if (activity != null)
            mView.staff_empty_list_placeholder.setTextColor(activity!!.config.textColor)
    }

    fun loadStaffs(){
        val mUserId =arguments!!.getString("userid")

        if (!context!!.dbHelper.isTableExists(context!!.dbHelper.CONTACTS_TABLE_NAME)) {
            context!!.dbHelper.dropTable(context!!.dbHelper.CONTACTS_TABLE_NAME)
            context!!.dbHelper.createStaffsTable()
        }else{
            context!!.dbHelper.initStaffsTable()
        }
        //http://ecandlemobile.com/RayKun/webservice/index.php/admin/clients/showCustomerAdmins?id=1
        val url="http://ecandlemobile.com/RayKun/webservice/index.php/admin/clients/showCustomerAdmins?id="+mUserId
        val loadStaffData = loadStaffDataTask(context!!.applicationContext)

        val staffsData =  loadStaffData.execute(url).get()

        Log.d("loadStaffData",staffsData.toString())

        for (staff in staffsData){
            saveStaff(staff)
        }
    }
    private fun saveStaff(staff: Staff) {

        context!!.dbHelper.insertStaff(staff)
        Log.d(LOG_TAG,"staff added")

    }

    companion object {

        fun newInstance(client: Client): TabTeamSalesFragment {
            val arguments = Bundle()

            arguments.putString("userid", client.userid.toString())

            val fragment = TabTeamSalesFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

}