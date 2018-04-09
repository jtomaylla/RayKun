package com.ecandle.raykun.models

data class Task(var id: Int,
                var name: String,
                var description: String,
                var priority: String,
                var startdate: String,
                var duedate: String,
                var status: String): ListItem()
