package com.ecandle.raykun.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.models.Lead
import kotlinx.android.synthetic.main.fragment_tab_general_info.*


/**
 * A simple [Fragment] subclass.
 */
class TabGeneralInfoFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_general_info, container, false)
    }


    override fun onResume() {
        super.onResume()
        

        lead_status_name!!.text = arguments!!.getString("status_name")
        lead_source_name!!.text = arguments!!.getString("source_name")
        lead_assigned!!.text = arguments!!.getString("assigned")
        lead_dateadded!!.text = arguments!!.getString("dateadded")
        lead_last_contact!!.text = arguments!!.getString("lastcontact")
        //lead_is_public!!.text = arguments!!.getString("is_public")

        lead_is_public.isChecked = arguments!!.getString("is_public") == "1"
    }

    companion object {

        fun newInstance(lead: Lead): TabGeneralInfoFragment {
            val arguments = Bundle()

            arguments.putString("status_name", lead.status_name)
            arguments.putString("source_name", lead.source_name)
            arguments.putString("assigned", lead.assigned)
            arguments.putString("dateadded", lead.dateadded)
            arguments.putString("is_public", lead.is_public)
            arguments.putString("lastcontact", lead.lastcontact)
            val fragment = TabGeneralInfoFragment()
            fragment.arguments = arguments
            return fragment
        }
    }


}
