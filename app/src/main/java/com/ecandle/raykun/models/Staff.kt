package com.ecandle.raykun.models

data class Staff(var staff_id: Int,
                 var customer_id: String,
                 var date_assigned: String,
                 var name: String,
                 var email: String,
                 var phonenumber: String
): ListItem()
