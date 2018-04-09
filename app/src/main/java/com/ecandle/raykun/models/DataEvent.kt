package com.ecandle.raykun.models

/**
 * Created by juantomaylla on 19/02/18.
 */
data class DataEvent(
        var id: Int,
        var name: String,
        var description: String,
        var priority: String,
        var startdate: String,
        var duedate: String,
        var status: String)