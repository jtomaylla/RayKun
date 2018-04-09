package com.ecandle.raykun.models

data class Item(var itemid: Int,
                var description: String,
                var long_description: String,
                var rate: String,
                var taxrate: String,
                var taxrate_2: String,
                var group_id: String,
                var unit: String): ListItem()
