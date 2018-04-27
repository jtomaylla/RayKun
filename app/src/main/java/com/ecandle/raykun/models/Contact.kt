package com.ecandle.raykun.models

data class Contact(var id: Int,
                   var userid: String,
                   var is_primary: String,
                   var firstname: String,
                   var lastname: String,
                   var email: String,
                   var phonenumber: String,
                   var title: String
): ListItem()

