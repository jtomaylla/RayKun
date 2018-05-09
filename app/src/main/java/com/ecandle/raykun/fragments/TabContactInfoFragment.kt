package com.ecandle.raykun.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.models.Lead
import kotlinx.android.synthetic.main.fragment_tab_contact_info.*


/**
 * A simple [Fragment] subclass.
 */
class TabContactInfoFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_contact_info, container, false)
    }

    override fun onResume() {
        super.onResume()
        val textcolor:Int = this.resources.getColor(R.color.default_text_color)
        lead_name!!.text = arguments!!.getString("name")
        lead_title!!.text = arguments!!.getString("title")
        lead_email!!.text = arguments!!.getString("email")
        lead_website!!.text = arguments!!.getString("website")
        lead_phonenumber!!.text = arguments!!.getString("phonenumber")
        lead_company!!.text = arguments!!.getString("company")
        lead_address!!.text = arguments!!.getString("address")
        lead_city!!.text = arguments!!.getString("city")
        lead_state!!.text = arguments!!.getString("state")
        lead_country!!.text = arguments!!.getString("country")
        lead_zip!!.text = arguments!!.getString("zip")
        lead_description!!.text = arguments!!.getString("description")

        lead_name.setTextColor(textcolor)
        lead_title.setTextColor(textcolor)
        lead_email.setTextColor(textcolor)
        lead_website.setTextColor(textcolor)
        lead_phonenumber.setTextColor(textcolor)
        lead_company.setTextColor(textcolor)
        lead_address.setTextColor(textcolor)
        lead_city.setTextColor(textcolor)
        lead_state.setTextColor(textcolor)
        lead_country.setTextColor(textcolor)
        lead_zip.setTextColor(textcolor)
        lead_description.setTextColor(textcolor)
    }

    companion object {

        fun newInstance(lead: Lead): TabContactInfoFragment {
            val arguments = Bundle()
            arguments.putString("name", lead.name)
            arguments.putString("title", lead.title)
            arguments.putString("email", lead.email)
            arguments.putString("website", lead.website)
            arguments.putString("phonenumber", lead.phonenumber)
            arguments.putString("company", lead.company)
            arguments.putString("address", lead.address)
            arguments.putString("city", lead.city)
            arguments.putString("state", lead.state)           
            arguments.putString("country", lead.country)
            arguments.putString("zip", lead.zip)
            arguments.putString("description", lead.description)
            val fragment = TabContactInfoFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}
