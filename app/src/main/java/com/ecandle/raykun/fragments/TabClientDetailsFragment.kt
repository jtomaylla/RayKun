package com.ecandle.raykun.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ecandle.raykun.R
import com.ecandle.raykun.models.Client
import kotlinx.android.synthetic.main.fragment_tab_client_details.*


/**
 * A simple [Fragment] subclass.
 */
class TabClientDetailsFragment() : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_client_details, container, false)
    }

    override fun onResume() {
        super.onResume()

        client_company!!.text = arguments!!.getString("company")
        client_vat!!.text = arguments!!.getString("vat")
        client_phonenumber!!.text = arguments!!.getString("phonenumber")
        client_website!!.text = arguments!!.getString("website")
//        client_default_currency!!.text = arguments!!.getString("default_currency")
//        client_default_language!!.text = arguments!!.getString("default_language")
        client_latitude!!.text = arguments!!.getString("latitude")
        client_longitude!!.text = arguments!!.getString("longitude")

        client_address!!.text = arguments!!.getString("address")
        client_city!!.text = arguments!!.getString("city")
        client_state!!.text = arguments!!.getString("state")
        client_country!!.text = arguments!!.getString("country_name")
        client_zip!!.text = arguments!!.getString("zip")
    }

    companion object {

        fun newInstance(client: Client): TabClientDetailsFragment {
            val arguments = Bundle()
            arguments.putString("company", client.company)
            arguments.putString("vat", client.vat)
            arguments.putString("phonenumber", client.phonenumber)
            arguments.putString("country", client.country)
            arguments.putString("city", client.city)
            arguments.putString("zip", client.zip)
            arguments.putString("state", client.state)
            arguments.putString("address", client.address)
            arguments.putString("website", client.website)

            arguments.putString("datecreate", client.datecreated)
            arguments.putString("active", client.active)
            arguments.putString("leadid", client.leadid)
            arguments.putString("billing_street", client.billing_street)
            arguments.putString("billing_city", client.billing_city)
            arguments.putString("billing_state", client.billing_state)
            arguments.putString("billing_zip", client.billing_zip)
            arguments.putString("billing_country", client.billing_country)
            arguments.putString("shipping_street", client.shipping_street)
            arguments.putString("shipping_city", client.shipping_city)
            arguments.putString("shipping_state", client.shipping_state)
            arguments.putString("shipping_zip", client.shipping_zip)
            arguments.putString("shipping_country", client.shipping_country)
            arguments.putString("longitude", client.longitude)
            arguments.putString("latitude", client.latitude)
            arguments.putString("default_language", client.default_language)
            arguments.putString("default_currency", client.default_currency)
            arguments.putString("show_primary_contact", client.show_primary_contact)
            arguments.putString("addedfrom", client.addedfrom)
            arguments.putString("contact_name",client.contact_name )
            arguments.putString("contact_email",client.contact_email)
            arguments.putString("country_name",client.country_name)
            arguments.putString("billing_country_name",client.billing_country_name)
            arguments.putString("shipping_country_name",client.shipping_country_name)

            val fragment = TabClientDetailsFragment()
            fragment.arguments = arguments
            return fragment
        }
    }
}
