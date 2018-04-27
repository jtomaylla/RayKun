package com.ecandle.raykun.models

data class Client(var userid: Int,
                  var company: String,
                  var vat: String,
                  var phonenumber : String,
                  var country: String,
                  var city: String,
                  var zip: String,
                  var state : String,
                  var address: String,
                  var website: String,
                  var datecreated: String,
                  var active: String,
                  var leadid: String,
                  var billing_street: String,
                  var billing_city: String,
                  var billing_state: String,
                  var billing_zip: String,
                  var billing_country: String,
                  var shipping_street: String,
                  var shipping_city: String,
                  var shipping_state: String,
                  var shipping_zip: String,
                  var shipping_country: String,
                  var longitude : String,
                  var latitude: String,
                  var default_language: String,
                  var default_currency: String,
                  var show_primary_contact: String,
                  var addedfrom: String,
                  var contact_name: String,
                  var contact_email: String,
                  var country_name: String,
                  var billing_country_name: String,
                  var shipping_country_name: String
): ListItem()
