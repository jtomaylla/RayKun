package com.ecandle.raykun.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ecandle.raykun.R
import com.ecandle.raykun.models.Client
import kotlinx.android.synthetic.main.fragment_tab_invoice_info.*


/**
 * A simple [Fragment] subclass.
 */
class TabInvoiceInfoFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_invoice_info, container, false)
    }


    override fun onResume() {
        super.onResume()
        

        client_billing_street!!.text = arguments!!.getString("billing_street")
        client_billing_city!!.text = arguments!!.getString("billing_city")
        client_billing_state!!.text = arguments!!.getString("billing_state")
        client_billing_country!!.text = arguments!!.getString("billing_country_name")
        client_billing_zip!!.text = arguments!!.getString("billing_zip")

        client_shipping_street!!.text = arguments!!.getString("shipping_street")
        client_shipping_city!!.text = arguments!!.getString("shipping_city")
        client_shipping_state!!.text = arguments!!.getString("shipping_state")
        client_shipping_country!!.text = arguments!!.getString("shipping_country_name")
        client_shipping_zip!!.text = arguments!!.getString("shipping_zip")
        
    }

    companion object {

        fun newInstance(client: Client): TabInvoiceInfoFragment {
            val arguments = Bundle()

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
            arguments.putString("billing_country_name",client.billing_country_name)
            arguments.putString("shipping_country_name",client.shipping_country_name)
            val fragment = TabInvoiceInfoFragment()
            fragment.arguments = arguments
            return fragment
        }
    }


}
