package com.ecandle.raykun.helpers

import com.ecandle.raykun.R

val APP_NAME = "app_name"
val APP_LICENSES = "app_licenses"
val APP_VERSION_NAME = "app_version_name"


// licenses
val LICENSE_KOTLIN = 1
val LICENSE_SUBSAMPLING = 2
val LICENSE_GLIDE = 4
val LICENSE_CROPPER = 8
val LICENSE_MULTISELECT = 16
val LICENSE_RTL = 32
val LICENSE_JODA = 64
val LICENSE_STETHO = 128
val LICENSE_OTTO = 256
val LICENSE_PHOTOVIEW = 512
val LICENSE_PICASSO = 1024
val LICENSE_PATTERN = 2048
val LICENSE_REPRINT = 4096
val LICENSE_GIF_DRAWABLE = 8192
val LICENSE_AUTOFITTEXTVIEW = 16384
val LICENSE_ROBOLECTRIC = 32768
val LICENSE_ESPRESSO = 65536
val LICENSE_GSON = 131072
val LICENSE_LEAK_CANARY = 262144
val LICENSE_SIMPLE_CALENDAR = 262244


val LOW_ALPHA = .3f
val MEDIUM_ALPHA = .6f
val STORED_LOCALLY_ONLY = 0

val DAY_CODE = "day_code"
val YEAR_LABEL = "year"
val EVENT_ID = "event_id"
val EVENT_OCCURRENCE_TS = "event_occurrence_ts"
val NEW_EVENT_START_TS = "new_event_start_ts"
val WEEK_START_TIMESTAMP = "week_start_timestamp"
val NEW_EVENT_SET_HOUR_DURATION = "new_event_set_hour_duration"
val CALDAV = "Caldav"

val USER_ID = "user_id"
val ITEM_ID = "item_id"
val CONTACT_ID = "id"
val LEAD_ID = "id"

val USER_EMAIL = "test@ecandlemobile.com"
val USER_KEY = "DYCSMKlrWBcf"

val CLIENT_LATITUDE ="latitude"
val CLIENT_LONGITUDE ="longitude"
val CLIENT_COMPANY = "company"
val CLIENT_ADDRESS = "address"
val SELECTED_CLIENTS = "selected_clients"

val TASK_ID = "task_id"
val TASK_START_DATE = "task_start_date"
val NEW_TASK_START_TS = "new_task_start_ts"

val MONTHLY_VIEW = 1
val YEARLY_VIEW = 2
val EVENTS_LIST_VIEW = 3
val WEEKLY_VIEW = 4

val REMINDER_OFF = -1

val MAP_MODE = "map_mode"

val DAY = 86400
val WEEK = 604800
val MONTH = 2592001    // exact value not taken into account, Joda is used for adding months and years
val YEAR = 31536000

val PRIORITY_LOW = 1
val PRIORITY_MEDIUM = 2
val PRIORITY_HIGH = 3
val PRIORITY_URGENT = 4

val STATUS_PENDING = 1
val STATUS_ON_PROGRESS = 2
val STATUS_TESTING = 3
val STATUS_COMPLETED = 4

val DAY_MINUTES = 24 * 60
val DAY_SECONDS = 24 * 60 * 60
val WEEK_SECONDS = 7 * DAY_SECONDS

// Shared Preferences
val USE_24_HOUR_FORMAT = "use_24_hour_format"
val SUNDAY_FIRST = "sunday_first"
val WEEK_NUMBERS = "week_numbers"
val START_WEEKLY_AT = "start_weekly_at"
val END_WEEKLY_AT = "end_weekly_at"
val VIBRATE = "vibrate"
val REMINDER_SOUND = "reminder_sound"
val VIEW = "view"
val REMINDER_MINUTES = "reminder_minutes"
val DISPLAY_EVENT_TYPES = "display_event_types"
val FONT_SIZE = "font_size"
val CALDAV_SYNC = "caldav_sync"
val CALDAV_SYNCED_CALENDAR_IDS = "caldav_synced_calendar_ids"
val LAST_USED_CALDAV_CALENDAR = "last_used_caldav_calendar"
val SNOOZE_DELAY = "snooze_delay"
val DISPLAY_PAST_EVENTS = "display_past_events"
val REPLACE_DESCRIPTION = "replace_description"
val GOOGLE_SYNC = "google_sync" // deprecated

val letterIDs = intArrayOf(R.string.sunday_letter, R.string.monday_letter, R.string.tuesday_letter, R.string.wednesday_letter,
        R.string.thursday_letter, R.string.friday_letter, R.string.saturday_letter)

// repeat_rule for weekly repetition
val MONDAY = 1
val TUESDAY = 2
val WEDNESDAY = 4
val THURSDAY = 8
val FRIDAY = 16
val SATURDAY = 32
val SUNDAY = 64
val EVERY_DAY = 127

// repeat_rule for monthly repetition
val REPEAT_MONTH_SAME_DAY = 1                   // ie 25th every month
val REPEAT_MONTH_ORDER_WEEKDAY_USE_LAST = 2     // ie every xth sunday. 4th if a month has 4 sundays, 5th if 5
val REPEAT_MONTH_LAST_DAY = 3                   // ie every last day of the month
val REPEAT_MONTH_ORDER_WEEKDAY = 4              // ie every 4th sunday, even if a month has 4 sundays only (will stay 4th even at months with 5)

// special event flags
val FLAG_ALL_DAY = 1

// constants related to ICS file exporting / importing
val BEGIN_CALENDAR = "BEGIN:VCALENDAR"
val END_CALENDAR = "END:VCALENDAR"
val CALENDAR_PRODID = "PRODID:-//Simple Mobile Tools//NONSGML Event Calendar//EN"
val CALENDAR_VERSION = "VERSION:2.0"
val BEGIN_EVENT = "BEGIN:VEVENT"
val END_EVENT = "END:VEVENT"
val BEGIN_ALARM = "BEGIN:VALARM"
val END_ALARM = "END:VALARM"
val DTSTART = "DTSTART"
val DTEND = "DTEND"
val LAST_MODIFIED = "LAST-MODIFIED"
val DURATION = "DURATION:"
val SUMMARY = "SUMMARY"
val DESCRIPTION = "DESCRIPTION:"
val UID = "UID:"
val ACTION = "ACTION:"
val TRIGGER = "TRIGGER:"
val RRULE = "RRULE:"
val CATEGORIES = "CATEGORIES:"
val STATUS = "STATUS:"
val EXDATE = "EXDATE"
val BYDAY = "BYDAY"
val BYMONTHDAY = "BYMONTHDAY"
val LOCATION = "LOCATION:"

// this tag isn't a standard ICS tag, but there's no official way of adding a category color in an ics file
val CATEGORY_COLOR = "CATEGORY_COLOR:"

val DISPLAY = "DISPLAY"
val FREQ = "FREQ"
val UNTIL = "UNTIL"
val COUNT = "COUNT"
val INTERVAL = "INTERVAL"
val CONFIRMED = "CONFIRMED"
val VALUE = "VALUE"
val DATE = "DATE"

val DAILY = "DAILY"
val WEEKLY = "WEEKLY"
val MONTHLY = "MONTHLY"
val YEARLY = "YEARLY"

val MO = "MO"
val TU = "TU"
val WE = "WE"
val TH = "TH"
val FR = "FR"
val SA = "SA"
val SU = "SU"

// font sizes
val FONT_SIZE_SMALL = 0
val FONT_SIZE_MEDIUM = 1
val FONT_SIZE_LARGE = 2

val SOURCE_SIMPLE_CALENDAR = "simple-calendar"
val SOURCE_IMPORTED_ICS = "imported-ics"
val SOURCE_CONTACT_BIRTHDAY = "contact-birthday"
val SOURCE_CONTACT_ANNIVERSARY = "contact-anniversary"

// deprecated
val SOURCE_GOOGLE_CALENDAR = 1

val CURRENT_LATITUDE = "-12.3456"
val CURRENT_LONGITUDE = "37.7785"
