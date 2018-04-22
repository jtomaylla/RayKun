package com.ecandle.raykun.helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.text.TextUtils
import android.util.SparseIntArray
import com.ecandle.raykun.R
import com.ecandle.raykun.extensions.*
import com.ecandle.raykun.models.*
import com.simplemobiletools.commons.extensions.getIntValue
import com.simplemobiletools.commons.extensions.getLongValue
import com.simplemobiletools.commons.extensions.getStringValue
import org.joda.time.DateTime
import java.util.*
import kotlin.collections.ArrayList

class DBHelper private constructor(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private val MAIN_TABLE_NAME = "events"
    private val COL_ID = "id"
    private val COL_START_TS = "start_ts"
    private val COL_END_TS = "end_ts"
    private val COL_TITLE = "title"
    private val COL_DESCRIPTION = "description"
    private val COL_REMINDER_MINUTES = "reminder_minutes"
    private val COL_REMINDER_MINUTES_2 = "reminder_minutes_2"
    private val COL_REMINDER_MINUTES_3 = "reminder_minutes_3"
    private val COL_IMPORT_ID = "import_id"
    private val COL_FLAGS = "flags"
    private val COL_EVENT_TYPE = "event_type"
    private val COL_OFFSET = "offset"
    private val COL_IS_DST_INCLUDED = "is_dst_included"
    private val COL_LAST_UPDATED = "last_updated"
    private val COL_EVENT_SOURCE = "event_source"
    private val COL_LOCATION = "location"
    private val COL_SOURCE = "source"   // deprecated

    private val META_TABLE_NAME = "events_meta"
    private val COL_EVENT_ID = "event_id"
    private val COL_REPEAT_START = "repeat_start"
    private val COL_REPEAT_INTERVAL = "repeat_interval"
    private val COL_REPEAT_RULE = "repeat_rule"
    private val COL_REPEAT_LIMIT = "repeat_limit"

    private val TYPES_TABLE_NAME = "event_types"
    private val COL_TYPE_ID = "event_type_id"
    private val COL_TYPE_TITLE = "event_type_title"
    private val COL_TYPE_COLOR = "event_type_color"
    private val COL_TYPE_CALDAV_CALENDAR_ID = "event_caldav_calendar_id"
    private val COL_TYPE_CALDAV_DISPLAY_NAME = "event_caldav_display_name"
    private val COL_TYPE_CALDAV_EMAIL = "event_caldav_email"

    private val EXCEPTIONS_TABLE_NAME = "event_repeat_exceptions"
    private val COL_EXCEPTION_ID = "event_exception_id"
    private val COL_OCCURRENCE_TIMESTAMP = "event_occurrence_timestamp"
    private val COL_OCCURRENCE_DAYCODE = "event_occurrence_daycode"
    private val COL_PARENT_EVENT_ID = "event_parent_id"
    private val COL_CHILD_EVENT_ID = "event_child_id"

    private val TASKS_TABLE_NAME = "tasks"
    private val COL_TASK_ID = "id"
    private val COL_TASK_NAME = "name"
    private val COL_TASK_DESCRIPTION = "description"
    private val COL_TASK_PRIORITY = "priority"
    private val COL_TASK_START_DATE = "startdate"
    private val COL_TASK_DUE_DATE = "duedate"
    private val COL_TASK_STATUS = "status"

    public val ITEMS_TABLE_NAME = "items"
    private val COL_ITEM_ID = "itemid"
    private val COL_ITEM_DESCRIPTION = "description"
    private val COL_ITEM_LONG_DESCRIPTION = "long_description"
    private val COL_ITEM_RATE = "rate"
    private val COL_ITEM_TAX_RATE = "taxrate"
    private val COL_ITEM_TAX_RATE_2 = "taxrate_2"
    private val COL_ITEM_GROUP_ID = "group_id"
    private val COL_ITEM_UNIT = "unit"

    public val CLIENTS_TABLE_NAME = "clients"
    private val COL_CLIENT_ID = "userid"
    private val COL_CLIENT_COMPANY = "company"
    private val COL_CLIENT_VAT = "vat"
    private val COL_CLIENT_PHONENUMBER = "phonenumber"
    private val COL_CLIENT_COUNTRY = "country"
    private val COL_CLIENT_CITY = "city"
    private val COL_CLIENT_ZIP = "zip"
    private val COL_CLIENT_STATE = "state"
    private val COL_CLIENT_ADDRESS = "address"
    private val COL_CLIENT_WEBSITE= "website"
    private val COL_CLIENT_DATECREATED = "datecreated"
    private val COL_CLIENT_ACTIVE = "active"
    private val COL_CLIENT_LEADID = "leadid"
    private val COL_CLIENT_BILLING_STREET = "billing_street"
    private val COL_CLIENT_BILLING_CITY = "billing_city"
    private val COL_CLIENT_BILLING_STATE = "billing_state"
    private val COL_CLIENT_BILLING_ZIP = "billing_zip"
    private val COL_CLIENT_BILLING_COUNTRY = "billing_country"
    private val COL_CLIENT_SHIPPING_STREET = "shipping_street"
    private val COL_CLIENT_SHIPPING_CITY = "shipping_city"
    private val COL_CLIENT_SHIPPING_STATE = "shipping_state"
    private val COL_CLIENT_SHIPPING_ZIP = "shipping_zip"
    private val COL_CLIENT_SHIPPING_COUNTRY = "shipping_country"
    private val COL_CLIENT_LONGITUDE = "longitude"
    private val COL_CLIENT_LATITUDE = "latitude"
    private val COL_CLIENT_DEFAULT_LANGUAGE = "default_language"
    private val COL_CLIENT_DEFAULT_CURRENCY = "default_currency"
    private val COL_CLIENT_SHOW_PRIMARY_CONTACT = "show_primary_contact"
    private val COL_CLIENT_ADDEDFROM = "addedfrom"
    private val COL_CLIENT_CONTACT_NAME = "contact_name"
    private val COL_CLIENT_CONTACT_EMAIL = "contact_email"
    private val COL_CLIENT_COUNTRY_NAME = "country_name"


    private val mDb: SQLiteDatabase = writableDatabase

    companion object {
        private val DB_VERSION = 19
        val DB_NAME = "events.db"
        val REGULAR_EVENT_TYPE_ID = 1
        var dbInstance: DBHelper? = null

        fun newInstance(context: Context): DBHelper {
            if (dbInstance == null)
                dbInstance = DBHelper(context)

            return dbInstance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $MAIN_TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_START_TS INTEGER, $COL_END_TS INTEGER, " +
                "$COL_TITLE TEXT, $COL_DESCRIPTION TEXT, $COL_REMINDER_MINUTES INTEGER, $COL_REMINDER_MINUTES_2 INTEGER, $COL_REMINDER_MINUTES_3 INTEGER, " +
                "$COL_IMPORT_ID TEXT, $COL_FLAGS INTEGER, $COL_EVENT_TYPE INTEGER NOT NULL DEFAULT $REGULAR_EVENT_TYPE_ID, " +
                "$COL_PARENT_EVENT_ID INTEGER, $COL_OFFSET TEXT, $COL_IS_DST_INCLUDED INTEGER, $COL_LAST_UPDATED INTEGER, $COL_EVENT_SOURCE TEXT, " +
                "$COL_LOCATION TEXT)")

        createMetaTable(db)
        createTypesTable(db)
        createExceptionsTable(db)
        createTasksTable(db)
        createItemsTable(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_REMINDER_MINUTES INTEGER DEFAULT -1")
        }

        if (oldVersion < 3) {
            createMetaTable(db)
        }

        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_IMPORT_ID TEXT DEFAULT ''")
        }

        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_FLAGS INTEGER NOT NULL DEFAULT 0")
            db.execSQL("ALTER TABLE $META_TABLE_NAME ADD COLUMN $COL_REPEAT_LIMIT INTEGER NOT NULL DEFAULT 0")
        }

        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_REMINDER_MINUTES_2 INTEGER NOT NULL DEFAULT -1")
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_REMINDER_MINUTES_3 INTEGER NOT NULL DEFAULT -1")
        }

        if (oldVersion < 7) {
            createTypesTable(db)
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_EVENT_TYPE INTEGER NOT NULL DEFAULT $REGULAR_EVENT_TYPE_ID")
        }

        if (oldVersion < 8) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_PARENT_EVENT_ID INTEGER NOT NULL DEFAULT 0")
            createExceptionsTable(db)
        }

        if (oldVersion < 9) {
            try {
                db.execSQL("ALTER TABLE $EXCEPTIONS_TABLE_NAME ADD COLUMN $COL_OCCURRENCE_DAYCODE INTEGER NOT NULL DEFAULT 0")
            } catch (ignored: SQLiteException) {
            }
            convertExceptionTimestampToDaycode(db)
        }

        if (oldVersion < 11) {
            db.execSQL("ALTER TABLE $META_TABLE_NAME ADD COLUMN $COL_REPEAT_RULE INTEGER NOT NULL DEFAULT 0")
            setupRepeatRules(db)
        }

        if (oldVersion < 12) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_OFFSET TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_IS_DST_INCLUDED INTEGER NOT NULL DEFAULT 0")
        }

        if (oldVersion < 13) {
            try {
                createExceptionsTable(db)
            } catch (e: Exception) {
                try {
                    db.execSQL("ALTER TABLE $EXCEPTIONS_TABLE_NAME ADD COLUMN $COL_CHILD_EVENT_ID INTEGER NOT NULL DEFAULT 0")
                } catch (e: Exception) {

                }
            }
        }

        if (oldVersion < 14) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_LAST_UPDATED INTEGER NOT NULL DEFAULT 0")
        }

        if (oldVersion < 15) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_EVENT_SOURCE TEXT DEFAULT ''")
        }

        if (oldVersion < 16) {
            db.execSQL("ALTER TABLE $TYPES_TABLE_NAME ADD COLUMN $COL_TYPE_CALDAV_CALENDAR_ID INTEGER NOT NULL DEFAULT 0")
        }

        if (oldVersion < 17) {
            db.execSQL("ALTER TABLE $TYPES_TABLE_NAME ADD COLUMN $COL_TYPE_CALDAV_DISPLAY_NAME TEXT DEFAULT ''")
            db.execSQL("ALTER TABLE $TYPES_TABLE_NAME ADD COLUMN $COL_TYPE_CALDAV_EMAIL TEXT DEFAULT ''")
        }

        if (oldVersion < 18) {
            updateOldMonthlyEvents(db)
        }

        if (oldVersion < 19) {
            db.execSQL("ALTER TABLE $MAIN_TABLE_NAME ADD COLUMN $COL_LOCATION TEXT DEFAULT ''")
        }
    }

    fun isTableExists(tableName: String): Boolean {
//        if (openDb) {
//            if (mDb == null || !mDb.isOpen()) {
//                mDb = readableDatabase
//            }
//
//            if (!mDb.isReadOnly()) {
//                mDb.close()
//                mDb = readableDatabase
//            }
//        }

        val cursor = mDb.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '$tableName'", null)
        if (cursor != null) {
            if (cursor!!.getCount() > 0) {
                cursor!!.close()
                return true
            }
            cursor!!.close()
        }
        return false
    }

    private fun createMetaTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $META_TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_EVENT_ID INTEGER UNIQUE, $COL_REPEAT_START INTEGER, " +
                "$COL_REPEAT_INTERVAL INTEGER, $COL_REPEAT_LIMIT INTEGER, $COL_REPEAT_RULE INTEGER)")
    }

    private fun createTypesTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TYPES_TABLE_NAME ($COL_TYPE_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TYPE_TITLE TEXT, $COL_TYPE_COLOR INTEGER, " +
                "$COL_TYPE_CALDAV_CALENDAR_ID INTEGER, $COL_TYPE_CALDAV_DISPLAY_NAME TEXT, $COL_TYPE_CALDAV_EMAIL TEXT)")
        addRegularEventType(db)
    }

    private fun createExceptionsTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $EXCEPTIONS_TABLE_NAME ($COL_EXCEPTION_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_PARENT_EVENT_ID INTEGER, " +
                "$COL_OCCURRENCE_TIMESTAMP INTEGER, $COL_OCCURRENCE_DAYCODE INTEGER, $COL_CHILD_EVENT_ID INTEGER)")
    }

    public fun createTasksTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TASKS_TABLE_NAME ($COL_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_TASK_NAME TEXT, $COL_TASK_DESCRIPTION TEXT, " +
                "$COL_TASK_PRIORITY TEXT, $COL_TASK_START_DATE TEXT, $COL_TASK_DUE_DATE TEXT, $COL_TASK_STATUS TEXT)")
    }

    public fun createItemsTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $ITEMS_TABLE_NAME " +
                "($COL_ITEM_ID INTEGER PRIMARY KEY, $COL_ITEM_DESCRIPTION TEXT, $COL_ITEM_LONG_DESCRIPTION TEXT, $COL_ITEM_RATE TEXT, " +
                "$COL_ITEM_TAX_RATE TEXT, $COL_ITEM_TAX_RATE_2 TEXT, $COL_ITEM_GROUP_ID TEXT, $COL_ITEM_UNIT TEXT)")
    }

    public fun createItemsTable() {
        mDb.execSQL("CREATE TABLE $ITEMS_TABLE_NAME " +
                "($COL_ITEM_ID INTEGER PRIMARY KEY, $COL_ITEM_DESCRIPTION TEXT, $COL_ITEM_LONG_DESCRIPTION TEXT, $COL_ITEM_RATE TEXT, " +
                "$COL_ITEM_TAX_RATE TEXT, $COL_ITEM_TAX_RATE_2 TEXT, $COL_ITEM_GROUP_ID TEXT, $COL_ITEM_UNIT TEXT)")
    }

    public fun createClientsTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $CLIENTS_TABLE_NAME " +
                "($COL_CLIENT_ID INTEGER PRIMARY KEY, " +
                "$COL_CLIENT_COMPANY TEXT, $COL_CLIENT_VAT TEXT, $COL_CLIENT_PHONENUMBER TEXT, $COL_CLIENT_COUNTRY TEXT, $COL_CLIENT_CITY TEXT, " +
                "$COL_CLIENT_ZIP TEXT, $COL_CLIENT_STATE TEXT, $COL_CLIENT_ADDRESS TEXT, $COL_CLIENT_WEBSITE TEXT, $COL_CLIENT_DATECREATED TEXT, " +
                "$COL_CLIENT_ACTIVE TEXT, $COL_CLIENT_LEADID TEXT, $COL_CLIENT_BILLING_STREET TEXT, $COL_CLIENT_BILLING_CITY TEXT, $COL_CLIENT_BILLING_STATE TEXT, " +
                "$COL_CLIENT_BILLING_ZIP TEXT, $COL_CLIENT_BILLING_COUNTRY TEXT, $COL_CLIENT_SHIPPING_STREET TEXT, $COL_CLIENT_SHIPPING_CITY TEXT, $COL_CLIENT_SHIPPING_STATE TEXT, " +
                "$COL_CLIENT_SHIPPING_ZIP TEXT, $COL_CLIENT_SHIPPING_COUNTRY TEXT, $COL_CLIENT_LATITUDE TEXT, $COL_CLIENT_LONGITUDE TEXT, $COL_CLIENT_DEFAULT_LANGUAGE TEXT, " +
                "$COL_CLIENT_DEFAULT_CURRENCY TEXT, $COL_CLIENT_SHOW_PRIMARY_CONTACT TEXT, $COL_CLIENT_ADDEDFROM TEXT,$COL_CLIENT_CONTACT_NAME TEXT, $COL_CLIENT_CONTACT_EMAIL TEXT,$COL_CLIENT_COUNTRY_NAME TEXT)")
    }

    public fun createClientsTable() {
        mDb.execSQL("CREATE TABLE $CLIENTS_TABLE_NAME " +
                "($COL_CLIENT_ID INTEGER PRIMARY KEY, " +
                "$COL_CLIENT_COMPANY TEXT, $COL_CLIENT_VAT TEXT, $COL_CLIENT_PHONENUMBER TEXT, $COL_CLIENT_COUNTRY TEXT, $COL_CLIENT_CITY TEXT, " +
                "$COL_CLIENT_ZIP TEXT, $COL_CLIENT_STATE TEXT, $COL_CLIENT_ADDRESS TEXT, $COL_CLIENT_WEBSITE TEXT, $COL_CLIENT_DATECREATED TEXT, " +
                "$COL_CLIENT_ACTIVE TEXT, $COL_CLIENT_LEADID TEXT, $COL_CLIENT_BILLING_STREET TEXT, $COL_CLIENT_BILLING_CITY TEXT, $COL_CLIENT_BILLING_STATE TEXT, " +
                "$COL_CLIENT_BILLING_ZIP TEXT, $COL_CLIENT_BILLING_COUNTRY TEXT, $COL_CLIENT_SHIPPING_STREET TEXT, $COL_CLIENT_SHIPPING_CITY TEXT, $COL_CLIENT_SHIPPING_STATE TEXT, " +
                "$COL_CLIENT_SHIPPING_ZIP TEXT, $COL_CLIENT_SHIPPING_COUNTRY TEXT, $COL_CLIENT_LATITUDE TEXT, $COL_CLIENT_LONGITUDE TEXT, $COL_CLIENT_DEFAULT_LANGUAGE TEXT, " +
                "$COL_CLIENT_DEFAULT_CURRENCY TEXT, $COL_CLIENT_SHOW_PRIMARY_CONTACT TEXT, $COL_CLIENT_ADDEDFROM TEXT,$COL_CLIENT_CONTACT_NAME TEXT, $COL_CLIENT_CONTACT_EMAIL TEXT,$COL_CLIENT_COUNTRY_NAME TEXT)")
    }

    private fun addRegularEventType(db: SQLiteDatabase) {
        val regularEvent = context.resources.getString(R.string.regular_event)
        val eventType = EventType(REGULAR_EVENT_TYPE_ID, regularEvent, context.config.primaryColor)
        addEventType(eventType, db)
    }

    fun insert(event: Event, addToCalDAV: Boolean, callback: (id: Int) -> Unit) {
        if (event.startTS > event.endTS || event.title.trim().isEmpty()) {
            callback(0)
            return
        }

        val eventValues = fillEventValues(event)
        val id = mDb.insert(MAIN_TABLE_NAME, null, eventValues)
        event.id = id.toInt()

        if (event.repeatInterval != 0 && event.parentId == 0) {
            val metaValues = fillMetaValues(event)
            mDb.insert(META_TABLE_NAME, null, metaValues)
        }

        context.updateWidgets()
        context.scheduleNextEventReminder(event, this)

        if (addToCalDAV && event.source != SOURCE_SIMPLE_CALENDAR && context.config.caldavSync) {
            CalDAVHandler(context).insertCalDAVEvent(event)
        }

        callback(event.id)
    }

    fun update(event: Event, updateAtCalDAV: Boolean, callback: () -> Unit) {
        val selectionArgs = arrayOf(event.id.toString())
        val values = fillEventValues(event)
        val selection = "$COL_ID = ?"
        mDb.update(MAIN_TABLE_NAME, values, selection, selectionArgs)

        if (event.repeatInterval == 0) {
            val metaSelection = "$COL_EVENT_ID = ?"
            mDb.delete(META_TABLE_NAME, metaSelection, selectionArgs)
        } else {
            val metaValues = fillMetaValues(event)
            mDb.insertWithOnConflict(META_TABLE_NAME, null, metaValues, SQLiteDatabase.CONFLICT_REPLACE)
        }

        context.updateWidgets()
        context.scheduleNextEventReminder(event, this)
        if (updateAtCalDAV && event.source != SOURCE_SIMPLE_CALENDAR && context.config.caldavSync) {
            CalDAVHandler(context).updateCalDAVEvent(event)
        }
        callback()
    }

    private fun fillEventValues(event: Event): ContentValues {
        return ContentValues().apply {
            put(COL_START_TS, event.startTS)
            put(COL_END_TS, event.endTS)
            put(COL_TITLE, event.title)
            put(COL_DESCRIPTION, event.description)
            put(COL_REMINDER_MINUTES, event.reminder1Minutes)
            put(COL_REMINDER_MINUTES_2, event.reminder2Minutes)
            put(COL_REMINDER_MINUTES_3, event.reminder3Minutes)
            put(COL_IMPORT_ID, event.importId)
            put(COL_FLAGS, event.flags)
            put(COL_EVENT_TYPE, event.eventType)
            put(COL_PARENT_EVENT_ID, event.parentId)
            put(COL_OFFSET, event.offset)
            put(COL_IS_DST_INCLUDED, if (event.isDstIncluded) 1 else 0)
            put(COL_LAST_UPDATED, event.lastUpdated)
            put(COL_EVENT_SOURCE, event.source)
            put(COL_LOCATION, event.location)
        }
    }

    private fun fillMetaValues(event: Event): ContentValues {
        return ContentValues().apply {
            put(COL_EVENT_ID, event.id)
            put(COL_REPEAT_START, event.startTS)
            put(COL_REPEAT_INTERVAL, event.repeatInterval)
            put(COL_REPEAT_LIMIT, event.repeatLimit)
            put(COL_REPEAT_RULE, event.repeatRule)
        }
    }

    private fun addEventType(eventType: EventType, db: SQLiteDatabase) {
        insertEventType(eventType, db)
    }

    fun insertEventType(eventType: EventType, db: SQLiteDatabase = mDb): Int {
        val values = fillEventTypeValues(eventType)
        val insertedId = db.insert(TYPES_TABLE_NAME, null, values).toInt()
        context.config.addDisplayEventType(insertedId.toString())
        return insertedId
    }

    fun updateEventType(eventType: EventType): Int {
        return if (eventType.caldavCalendarId != 0) {
            if (CalDAVHandler(context).updateCalDAVCalendar(eventType)) {
                updateLocalEventType(eventType)
            } else {
                -1
            }
        } else {
            updateLocalEventType(eventType)
        }
    }

    fun updateLocalEventType(eventType: EventType): Int {
        val selectionArgs = arrayOf(eventType.id.toString())
        val values = fillEventTypeValues(eventType)
        val selection = "$COL_TYPE_ID = ?"
        return mDb.update(TYPES_TABLE_NAME, values, selection, selectionArgs)
    }

    private fun fillEventTypeValues(eventType: EventType): ContentValues {
        return ContentValues().apply {
            put(COL_TYPE_TITLE, eventType.title)
            put(COL_TYPE_COLOR, eventType.color)
            put(COL_TYPE_CALDAV_CALENDAR_ID, eventType.caldavCalendarId)
            put(COL_TYPE_CALDAV_DISPLAY_NAME, eventType.caldavDisplayName)
            put(COL_TYPE_CALDAV_EMAIL, eventType.caldavEmail)
        }
    }

    fun insertTask(task: Task, db: SQLiteDatabase = mDb): Int {
        val values = fillTaskValues(task)
        val insertedId = db.insert(TASKS_TABLE_NAME, null, values).toInt()
        return insertedId
    }

    fun updateTask(task: Task): Int {
        val selectionArgs = arrayOf(task.id.toString())
        val values = fillTaskValues(task)
        val selection = "$COL_TASK_ID = ?"
        return mDb.update(TASKS_TABLE_NAME, values, selection, selectionArgs)
    }

    private fun fillTaskValues(task: Task): ContentValues {
        return ContentValues().apply {
            put(COL_TASK_NAME, task.name)
            put(COL_TASK_DESCRIPTION, task.description)
            put(COL_TASK_PRIORITY, task.priority)
            put(COL_TASK_START_DATE, task.startdate)
            put(COL_TASK_DUE_DATE, task.duedate)
            put(COL_TASK_STATUS, task.status)
        }
    }

    fun insertItem(item: Item, db: SQLiteDatabase = mDb): Int {
        val values = fillItemValues(item)
        val insertedId = db.insert(ITEMS_TABLE_NAME, null, values).toInt()
        return insertedId
    }

    fun updateItem(item: Item): Int {
        val selectionArgs = arrayOf(item.itemid.toString())
        val values = fillItemValues(item)
        val selection = "$COL_ITEM_ID = ?"
        return mDb.update(ITEMS_TABLE_NAME, values, selection, selectionArgs)
    }

    private fun fillItemValues(item: Item): ContentValues {
        return ContentValues().apply {
            put(COL_ITEM_ID, item.itemid)
            put(COL_ITEM_DESCRIPTION, item.description)
            put(COL_ITEM_LONG_DESCRIPTION, item.long_description)
            put(COL_ITEM_RATE, item.rate)
            put(COL_ITEM_TAX_RATE, item.taxrate)
            put(COL_ITEM_TAX_RATE_2, item.taxrate_2)
            put(COL_ITEM_GROUP_ID, item.group_id)
            put(COL_ITEM_UNIT, item.unit)
        }
    }

    fun insertClient(client: Client, db: SQLiteDatabase = mDb): Int {
        val values = fillClientValues(client)
        val insertedId = db.insert(CLIENTS_TABLE_NAME, null, values).toInt()
        return insertedId
    }

    fun updateClient(client: Client): Int {
        val selectionArgs = arrayOf(client.userid.toString())
        val values = fillClientValues(client)
        val selection = "$COL_CLIENT_ID = ?"
        return mDb.update(CLIENTS_TABLE_NAME, values, selection, selectionArgs)
    }

    private fun fillClientValues(client: Client): ContentValues {
        return ContentValues().apply {
            put(COL_CLIENT_ID, client.userid)
            put(COL_CLIENT_COMPANY, client.company)
            put(COL_CLIENT_VAT, client.vat)
            put(COL_CLIENT_PHONENUMBER, client.phonenumber)
            put(COL_CLIENT_COUNTRY, client.country)
            put(COL_CLIENT_CITY, client.city)
            put(COL_CLIENT_ZIP, client.zip)
            put(COL_CLIENT_STATE, client.state)
            put(COL_CLIENT_ADDRESS, client.address)
            put(COL_CLIENT_WEBSITE, client.website)
            put(COL_CLIENT_DATECREATED, client.datecreated)
            put(COL_CLIENT_ACTIVE, client.active)
            put(COL_CLIENT_LEADID, client.leadid)
            put(COL_CLIENT_BILLING_STREET, client.billing_street)
            put(COL_CLIENT_BILLING_CITY, client.billing_city)
            put(COL_CLIENT_BILLING_STATE, client.billing_state)
            put(COL_CLIENT_BILLING_ZIP, client.billing_zip)
            put(COL_CLIENT_BILLING_COUNTRY, client.billing_country)
            put(COL_CLIENT_SHIPPING_STREET, client.shipping_street)
            put(COL_CLIENT_SHIPPING_CITY, client.shipping_city)
            put(COL_CLIENT_SHIPPING_STATE, client.shipping_state)
            put(COL_CLIENT_SHIPPING_ZIP, client.shipping_zip)
            put(COL_CLIENT_SHIPPING_COUNTRY, client.shipping_country)
            put(COL_CLIENT_LONGITUDE, client.longitude)
            put(COL_CLIENT_LATITUDE, client.latitude)
            put(COL_CLIENT_DEFAULT_LANGUAGE, client.default_language)
            put(COL_CLIENT_DEFAULT_CURRENCY, client.default_currency)
            put(COL_CLIENT_SHOW_PRIMARY_CONTACT, client.show_primary_contact)
            put(COL_CLIENT_ADDEDFROM, client.addedfrom)
            put(COL_CLIENT_CONTACT_NAME,client.contact_name )
            put(COL_CLIENT_CONTACT_EMAIL,client.contact_email)
            put(COL_CLIENT_COUNTRY_NAME,client.country_name)
        }
    }
    
    private fun fillExceptionValues(parentEventId: Int, occurrenceTS: Int, callback: (values: ContentValues) -> Unit) {
        val childEvent = getEventWithId(parentEventId)
        if (childEvent == null) {
            callback(ContentValues())
            return
        }

        childEvent.apply {
            id = 0
            parentId = parentEventId
            startTS = 0
            endTS = 0
        }

        insert(childEvent, false) {
            callback(ContentValues().apply {
                put(COL_PARENT_EVENT_ID, parentEventId)
                put(COL_OCCURRENCE_DAYCODE, Formatter.getDayCodeFromTS(occurrenceTS))
                put(COL_CHILD_EVENT_ID, it)
            })
        }
    }

    fun getEventTypeIdWithTitle(title: String): Int {
        val cols = arrayOf(COL_TYPE_ID)
        val selection = "$COL_TYPE_TITLE = ? COLLATE NOCASE"
        val selectionArgs = arrayOf(title)
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(TYPES_TABLE_NAME, cols, selection, selectionArgs, null, null, null)
            if (cursor?.moveToFirst() == true) {
                return cursor.getIntValue(COL_TYPE_ID)
            }
        } finally {
            cursor?.close()
        }
        return -1
    }

    fun getEventTypeWithCalDAVCalendarId(calendarId: Int): EventType? {
        val cols = arrayOf(COL_TYPE_ID)
        val selection = "$COL_TYPE_CALDAV_CALENDAR_ID = ?"
        val selectionArgs = arrayOf(calendarId.toString())
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(TYPES_TABLE_NAME, cols, selection, selectionArgs, null, null, null)
            if (cursor?.moveToFirst() == true) {
                return getEventType(cursor.getIntValue(COL_TYPE_ID))
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getEventType(id: Int): EventType? {
        val cols = arrayOf(COL_TYPE_TITLE, COL_TYPE_COLOR, COL_TYPE_CALDAV_CALENDAR_ID, COL_TYPE_CALDAV_DISPLAY_NAME, COL_TYPE_CALDAV_EMAIL)
        val selection = "$COL_TYPE_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(TYPES_TABLE_NAME, cols, selection, selectionArgs, null, null, null)
            if (cursor?.moveToFirst() == true) {
                val title = cursor.getStringValue(COL_TYPE_TITLE)
                val color = cursor.getIntValue(COL_TYPE_COLOR)
                val calendarId = cursor.getIntValue(COL_TYPE_CALDAV_CALENDAR_ID)
                val displayName = cursor.getStringValue(COL_TYPE_CALDAV_DISPLAY_NAME)
                val email = cursor.getStringValue(COL_TYPE_CALDAV_EMAIL)
                return EventType(id, title, color, calendarId, displayName, email)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun getBirthdays(): List<Event> {
        val selection = "$MAIN_TABLE_NAME.$COL_EVENT_SOURCE = ?"
        val selectionArgs = arrayOf(SOURCE_CONTACT_BIRTHDAY)
        val cursor = getEventsCursor(selection, selectionArgs)
        return fillEvents(cursor)
    }

    fun getAnniversaries(): List<Event> {
        val selection = "$MAIN_TABLE_NAME.$COL_EVENT_SOURCE = ?"
        val selectionArgs = arrayOf(SOURCE_CONTACT_ANNIVERSARY)
        val cursor = getEventsCursor(selection, selectionArgs)
        return fillEvents(cursor)
    }

    fun deleteEvents(ids: Array<String>, deleteFromCalDAV: Boolean) {
        val args = TextUtils.join(", ", ids)
        val selection = "$MAIN_TABLE_NAME.$COL_ID IN ($args)"
        val cursor = getEventsCursor(selection)
        val events = fillEvents(cursor).filter { it.importId.isNotEmpty() }

        mDb.delete(MAIN_TABLE_NAME, selection, null)

        val metaSelection = "$COL_EVENT_ID IN ($args)"
        mDb.delete(META_TABLE_NAME, metaSelection, null)

        val exceptionSelection = "$COL_PARENT_EVENT_ID IN ($args)"
        mDb.delete(EXCEPTIONS_TABLE_NAME, exceptionSelection, null)

        context.updateWidgets()

        ids.forEach {
            context.cancelNotification(it.toInt())
        }

        if (deleteFromCalDAV && context.config.caldavSync) {
            events.forEach {
                CalDAVHandler(context).deleteCalDAVEvent(it)
            }
        }

        deleteChildEvents(args, deleteFromCalDAV)
    }

    fun deleteTasks(ids: Array<String>) {
        val args = TextUtils.join(", ", ids)
        val selection = "$TASKS_TABLE_NAME.$COL_TASK_ID IN ($args)"

        mDb.delete(TASKS_TABLE_NAME, selection, null)

    }

    fun deleteItems(ids: Array<String>) {
        val args = TextUtils.join(", ", ids)
        val selection = "$ITEMS_TABLE_NAME.$COL_ITEM_ID IN ($args)"

        mDb.delete(ITEMS_TABLE_NAME, selection, null)

    }

    fun deleteClients(ids: Array<String>) {
        val args = TextUtils.join(", ", ids)
        val selection = "$CLIENTS_TABLE_NAME.$COL_CLIENT_ID IN ($args)"

        mDb.delete(CLIENTS_TABLE_NAME, selection, null)

    }
    fun initEventTable() {
        mDb.execSQL("DELETE FROM $MAIN_TABLE_NAME")
    }

    fun initTasksTable() {
        mDb.execSQL("DELETE FROM $TASKS_TABLE_NAME")
    }

    fun initItemsTable() {
        mDb.execSQL("DELETE FROM $ITEMS_TABLE_NAME")
    }

    fun initClientsTable() {
        mDb.execSQL("DELETE FROM $CLIENTS_TABLE_NAME")
    }

    private fun deleteChildEvents(ids: String, deleteFromCalDAV: Boolean) {
        val projection = arrayOf(COL_ID)
        val selection = "$COL_PARENT_EVENT_ID IN ($ids)"
        val childIds = ArrayList<String>()

        var cursor: Cursor? = null
        try {
            cursor = mDb.query(MAIN_TABLE_NAME, projection, selection, null, null, null, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    childIds.add(cursor.getStringValue(COL_ID))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }

        if (childIds.isNotEmpty())
            deleteEvents(childIds.toTypedArray(), deleteFromCalDAV)
    }

    fun getCalDAVCalendarEvents(calendarId: Long): List<Event> {
        val selection = "$MAIN_TABLE_NAME.$COL_EVENT_SOURCE = (?)"
        val selectionArgs = arrayOf("$CALDAV-$calendarId")
        val cursor = getEventsCursor(selection, selectionArgs)
        return fillEvents(cursor).filter { it.importId.isNotEmpty() }
    }

    private fun updateOldMonthlyEvents(db: SQLiteDatabase) {
        val OLD_MONTH = 2592000
        val projection = arrayOf(COL_ID, COL_REPEAT_INTERVAL)
        val selection = "$COL_REPEAT_INTERVAL != 0 AND $COL_REPEAT_INTERVAL % $OLD_MONTH == 0"
        var cursor: Cursor? = null
        try {
            cursor = db.query(META_TABLE_NAME, projection, selection, null, null, null, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    val id = cursor.getIntValue(COL_ID)
                    val repeatInterval = cursor.getIntValue(COL_REPEAT_INTERVAL)
                    val multiplies = repeatInterval / OLD_MONTH

                    val values = ContentValues().apply {
                        put(COL_REPEAT_INTERVAL, multiplies * MONTH)
                    }

                    val updateSelection = "$COL_ID = $id"
                    db.update(META_TABLE_NAME, values, updateSelection, null)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
    }

    fun addEventRepeatException(parentEventId: Int, occurrenceTS: Int) {
        fillExceptionValues(parentEventId, occurrenceTS) {
            mDb.insert(EXCEPTIONS_TABLE_NAME, null, it)
        }
    }

    fun deleteEventTypes(eventTypes: ArrayList<EventType>, deleteEvents: Boolean, callback: (deletedCnt: Int) -> Unit) {
        var deleteIds = eventTypes.filter { it.caldavCalendarId == 0 }.map { it.id }
        deleteIds = deleteIds.filter { it != DBHelper.REGULAR_EVENT_TYPE_ID } as ArrayList<Int>

        val deletedSet = HashSet<String>()
        deleteIds.map { deletedSet.add(it.toString()) }
        context.config.removeDisplayEventTypes(deletedSet)
        if (deleteIds.isEmpty())
            return

        for (eventTypeId in deleteIds) {
            if (deleteEvents) {
                deleteEventsWithType(eventTypeId)
            } else {
                resetEventsWithType(eventTypeId)
            }
        }

        val args = TextUtils.join(", ", deleteIds)
        val selection = "$COL_TYPE_ID IN ($args)"
        callback(mDb.delete(TYPES_TABLE_NAME, selection, null))
    }

    fun deleteEventTypesWithCalendarId(calendarIds: String) {
        val selection = "$COL_TYPE_CALDAV_CALENDAR_ID IN ($calendarIds)"
        mDb.delete(TYPES_TABLE_NAME, selection, null)
    }

    private fun deleteEventsWithType(eventTypeId: Int) {
        val selection = "$MAIN_TABLE_NAME.$COL_EVENT_TYPE = ?"
        val selectionArgs = arrayOf(eventTypeId.toString())
        val cursor = getEventsCursor(selection, selectionArgs)
        val events = fillEvents(cursor)
        val eventIDs = Array(events.size, { i -> (events[i].id.toString()) })
        deleteEvents(eventIDs, true)
    }

    private fun resetEventsWithType(eventTypeId: Int) {
        val values = ContentValues()
        values.put(COL_EVENT_TYPE, REGULAR_EVENT_TYPE_ID)

        val selection = "$COL_EVENT_TYPE = ?"
        val selectionArgs = arrayOf(eventTypeId.toString())
        mDb.update(MAIN_TABLE_NAME, values, selection, selectionArgs)
    }

    fun updateEventImportIdAndSource(eventId: Int, importId: String, source: String) {
        val values = ContentValues()
        values.put(COL_IMPORT_ID, importId)
        values.put(COL_EVENT_SOURCE, source)

        val selection = "$MAIN_TABLE_NAME.$COL_ID = ?"
        val selectionArgs = arrayOf(eventId.toString())
        mDb.update(MAIN_TABLE_NAME, values, selection, selectionArgs)
    }

    fun getEventsWithImportIds() = getEvents("").filter { it.importId.trim().isNotEmpty() } as ArrayList<Event>

    fun getEventWithId(id: Int): Event? {
        val selection = "$MAIN_TABLE_NAME.$COL_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = getEventsCursor(selection, selectionArgs)
        val events = fillEvents(cursor)
        return if (events.isNotEmpty()) {
            events[0]
        } else {
            null
        }
    }

    fun getEvents(fromTS: Int, toTS: Int, eventId: Int = -1, callback: (events: MutableList<Event>) -> Unit) {
        Thread {
            getEventsInBackground(fromTS, toTS, eventId, callback)
        }.start()
    }

    fun getEventsInBackground(fromTS: Int, toTS: Int, eventId: Int = -1, callback: (events: MutableList<Event>) -> Unit) {
        val events = ArrayList<Event>()

        var selection = "$COL_START_TS <= ? AND $COL_END_TS >= ? AND $COL_REPEAT_INTERVAL IS NULL AND $COL_START_TS != 0"
        if (eventId != -1)
            selection += " AND $MAIN_TABLE_NAME.$COL_ID = $eventId"
        val selectionArgs = arrayOf(toTS.toString(), fromTS.toString())
        val cursor = getEventsCursor(selection, selectionArgs)
        events.addAll(fillEvents(cursor))

        events.addAll(getRepeatableEventsFor(fromTS, toTS, eventId))

        events.addAll(getAllDayEvents(fromTS, toTS, eventId))

        val filtered = events.distinct().filterNot { it.ignoreEventOccurrences.contains(Formatter.getDayCodeFromTS(it.startTS).toInt()) } as MutableList<Event>
        callback(filtered)
    }

    private fun getRepeatableEventsFor(fromTS: Int, toTS: Int, eventId: Int = -1): List<Event> {
        val newEvents = ArrayList<Event>()

        // get repeatable events
        var selection = "$COL_REPEAT_INTERVAL != 0 AND $COL_START_TS <= $toTS AND $COL_START_TS != 0"
        if (eventId != -1)
            selection += " AND $MAIN_TABLE_NAME.$COL_ID = $eventId"
        val events = getEvents(selection)
        val startTimes = SparseIntArray(events.size)
        events.forEach {
            startTimes.put(it.id, it.startTS)
            if (it.repeatLimit >= 0) {
                newEvents.addAll(getEventsRepeatingTillDateOrForever(fromTS, toTS, startTimes, it))
            } else {
                newEvents.addAll(getEventsRepeatingXTimes(fromTS, toTS, startTimes, it))
            }
        }

        return newEvents
    }

    private fun getEventsRepeatingTillDateOrForever(fromTS: Int, toTS: Int, startTimes: SparseIntArray, event: Event): ArrayList<Event> {
        val original = event.copy()
        val events = ArrayList<Event>()
        while (event.startTS <= toTS && (event.repeatLimit == 0 || event.repeatLimit >= event.startTS)) {
            if (event.endTS >= fromTS) {
                if (event.repeatInterval.isXWeeklyRepetition()) {
                    if (event.startTS.isTsOnProperDay(event)) {
                        if (isOnProperWeek(event, startTimes)) {
                            events.add(event.copy())
                        }
                    }
                } else {
                    events.add(event.copy())
                }
            }

            if (event.getIsAllDay()) {
                if (event.repeatInterval.isXWeeklyRepetition()) {
                    if (event.endTS >= toTS && event.startTS.isTsOnProperDay(event)) {
                        if (isOnProperWeek(event, startTimes)) {
                            events.add(event.copy())
                        }
                    }
                } else {
                    val dayCode = Formatter.getDayCodeFromTS(fromTS)
                    val endDayCode = Formatter.getDayCodeFromTS(event.endTS)
                    if (dayCode == endDayCode) {
                        events.add(event.copy())
                    }
                }
            }
            event.addIntervalTime(original)
        }
        return events
    }

    private fun getEventsRepeatingXTimes(fromTS: Int, toTS: Int, startTimes: SparseIntArray, event: Event): ArrayList<Event> {
        val original = event.copy()
        val events = ArrayList<Event>()
        while (event.repeatLimit < 0 && event.startTS <= toTS) {
            if (event.repeatInterval.isXWeeklyRepetition()) {
                if (event.startTS.isTsOnProperDay(event)) {
                    if (isOnProperWeek(event, startTimes)) {
                        if (event.endTS >= fromTS) {
                            events.add(event.copy())
                        }
                        event.repeatLimit++
                    }
                }
            } else {
                if (event.endTS >= fromTS) {
                    events.add(event.copy())
                } else if (event.getIsAllDay()) {
                    val dayCode = Formatter.getDayCodeFromTS(fromTS)
                    val endDayCode = Formatter.getDayCodeFromTS(event.endTS)
                    if (dayCode == endDayCode) {
                        events.add(event.copy())
                    }
                }
                event.repeatLimit++
            }
            event.addIntervalTime(original)
        }
        return events
    }

    private fun getAllDayEvents(fromTS: Int, toTS: Int, eventId: Int = -1): List<Event> {
        val events = ArrayList<Event>()
        var selection = "($COL_FLAGS & $FLAG_ALL_DAY) != 0"
        if (eventId != -1)
            selection += " AND $MAIN_TABLE_NAME.$COL_ID = $eventId"

        val dayCode = Formatter.getDayCodeFromTS(fromTS)
        val cursor = getEventsCursor(selection)
        events.addAll(fillEvents(cursor).filter { dayCode == Formatter.getDayCodeFromTS(it.startTS) })
        return events
    }

    // check if its the proper week, for events repeating by x weeks
    private fun isOnProperWeek(event: Event, startTimes: SparseIntArray): Boolean {
        val initialWeekOfYear = Formatter.getDateTimeFromTS(startTimes[event.id]).weekOfWeekyear
        val currentWeekOfYear = Formatter.getDateTimeFromTS(event.startTS).weekOfWeekyear
        return (currentWeekOfYear - initialWeekOfYear) % (event.repeatInterval / WEEK) == 0
    }

    fun getRunningEvents(): List<Event> {
        val events = ArrayList<Event>()
        val ts = (System.currentTimeMillis() / 1000).toInt()

        val selection = "$COL_START_TS <= ? AND $COL_END_TS >= ? AND $COL_REPEAT_INTERVAL IS 0 AND $COL_START_TS != 0"
        val selectionArgs = arrayOf(ts.toString(), ts.toString())
        val cursor = getEventsCursor(selection, selectionArgs)
        events.addAll(fillEvents(cursor))

        events.addAll(getRepeatableEventsFor(ts, ts))
        return events
    }

    private fun getEvents(selection: String): List<Event> {
        val events = ArrayList<Event>()
        var cursor: Cursor? = null
        try {
            cursor = getEventsCursor(selection)
            if (cursor != null) {
                val currEvents = fillEvents(cursor)
                events.addAll(currEvents)
            }
        } finally {
            cursor?.close()
        }

        return events
    }

    fun getEventsWithIds(ids: List<Int>): ArrayList<Event> {
        val args = TextUtils.join(", ", ids)
        val selection = "$MAIN_TABLE_NAME.$COL_ID IN ($args)"
        return getEvents(selection) as ArrayList<Event>
    }

    // get deprecated Google Sync events
    fun getGoogleSyncEvents(): ArrayList<Event> {
        val selection = "$MAIN_TABLE_NAME.$COL_SOURCE = $SOURCE_GOOGLE_CALENDAR"
        return getEvents(selection) as ArrayList<Event>
    }

    fun getEventsAtReboot(): List<Event> {
        val selection = "$COL_REMINDER_MINUTES != -1 AND ($COL_START_TS > ? OR $COL_REPEAT_INTERVAL != 0) AND $COL_START_TS != 0"
        val selectionArgs = arrayOf(DateTime.now().seconds().toString())
        val cursor = getEventsCursor(selection, selectionArgs)
        return fillEvents(cursor)
    }

    fun getEventsToExport(includePast: Boolean): ArrayList<Event> {
        val currTime = (System.currentTimeMillis() / 1000).toString()
        var events = ArrayList<Event>()

        // non repeating events
        var cursor = if (includePast) {
            getEventsCursor()
        } else {
            val selection = "$COL_END_TS > ?"
            val selectionArgs = arrayOf(currTime)
            getEventsCursor(selection, selectionArgs)
        }
        events.addAll(fillEvents(cursor))

        // repeating events
        if (!includePast) {
            val selection = "$COL_REPEAT_INTERVAL != 0 AND ($COL_REPEAT_LIMIT == 0 OR $COL_REPEAT_LIMIT > ?)"
            val selectionArgs = arrayOf(currTime)
            cursor = getEventsCursor(selection, selectionArgs)
            events.addAll(fillEvents(cursor))
        }

        events = events.distinctBy { it.id } as ArrayList<Event>
        return events
    }

    fun getEventsFromCalDAVCalendar(calendarId: Int): List<Event> {
        val selection = "$MAIN_TABLE_NAME.$COL_EVENT_SOURCE = ?"
        val selectionArgs = arrayOf("$CALDAV-$calendarId")
        val cursor = getEventsCursor(selection, selectionArgs)
        return fillEvents(cursor)
    }

    private fun getEventsCursor(selection: String = "", selectionArgs: Array<String>? = null): Cursor? {
        val builder = SQLiteQueryBuilder()
        builder.tables = "$MAIN_TABLE_NAME LEFT OUTER JOIN $META_TABLE_NAME ON $COL_EVENT_ID = $MAIN_TABLE_NAME.$COL_ID"
        val projection = allColumns
        return builder.query(mDb, projection, selection, selectionArgs, "$MAIN_TABLE_NAME.$COL_ID", null, COL_START_TS)
    }

    fun getTaskWithId(id: Int): Task? {
        val selection = "$TASKS_TABLE_NAME.$COL_TASK_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = getTasksCursor(selection, selectionArgs)
        val items = fillTasks(cursor)
        return if (items.isNotEmpty()) {
            items[0]
        } else {
            null
        }
    }
    fun getTasksWithIds(ids: List<Int>): ArrayList<Task> {
        val args = TextUtils.join(", ", ids)
        val selection = "$TASKS_TABLE_NAME.$COL_TASK_ID IN ($args)"
        return getTasks(selection) as ArrayList<Task>
    }

    private fun getTasksCursor(selection: String = "", selectionArgs: Array<String>? = null): Cursor? {
        val builder = SQLiteQueryBuilder()
        builder.tables = "$TASKS_TABLE_NAME"
        val projection = allTaskColumns
        return builder.query(mDb, projection, selection, selectionArgs, "$TASKS_TABLE_NAME.$COL_ID", null, COL_TASK_START_DATE)
    }

    fun getItemWithId(id: Int): Item? {
        val selection = "$ITEMS_TABLE_NAME.$COL_ITEM_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = getItemsCursor(selection, selectionArgs)
        val items = fillItems(cursor)
        return if (items.isNotEmpty()) {
            items[0]
        } else {
            null
        }
    }
    
    fun getItemsWithIds(ids: List<Int>): ArrayList<Item> {
        val args = TextUtils.join(", ", ids)
        val selection = "$ITEMS_TABLE_NAME.$COL_ITEM_ID IN ($args)"
        return getItems(selection) as ArrayList<Item>
    }

    private fun getItemsCursor(selection: String = "", selectionArgs: Array<String>? = null): Cursor? {
        val builder = SQLiteQueryBuilder()
        builder.tables = "$ITEMS_TABLE_NAME"
        val projection = allItemColumns
        return builder.query(mDb, projection, selection, selectionArgs, "$ITEMS_TABLE_NAME.$COL_ITEM_ID", null, COL_ITEM_DESCRIPTION)
    }
    fun getClientWithId(id: Int): Client? {
        val selection = "$CLIENTS_TABLE_NAME.$COL_CLIENT_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = getClientsCursor(selection, selectionArgs)
        val clients = fillClients(cursor)
        return if (clients.isNotEmpty()) {
            clients[0]
        } else {
            null
        }
    }
    
    fun getClientsWithIds(ids: List<Int>): ArrayList<Client> {
        val args = TextUtils.join(", ", ids)
        val selection = "$CLIENTS_TABLE_NAME.$COL_CLIENT_ID IN ($args)"
        return getClients(selection) as ArrayList<Client>
    }

    private fun getClientsCursor(selection: String = "", selectionArgs: Array<String>? = null): Cursor? {
        val builder = SQLiteQueryBuilder()
        builder.tables = "$CLIENTS_TABLE_NAME"
        val projection = allClientColumns
        return builder.query(mDb, projection, selection, selectionArgs, "$CLIENTS_TABLE_NAME.$COL_CLIENT_ID", null, COL_CLIENT_COMPANY)
    }
    
    private val allColumns: Array<String>
        get() = arrayOf("$MAIN_TABLE_NAME.$COL_ID", COL_START_TS, COL_END_TS, COL_TITLE, COL_DESCRIPTION, COL_REMINDER_MINUTES, COL_REMINDER_MINUTES_2,
                COL_REMINDER_MINUTES_3, COL_REPEAT_INTERVAL, COL_REPEAT_RULE, COL_IMPORT_ID, COL_FLAGS, COL_REPEAT_LIMIT, COL_EVENT_TYPE, COL_OFFSET,
                COL_IS_DST_INCLUDED, COL_LAST_UPDATED, COL_EVENT_SOURCE, COL_LOCATION)

    private val allTaskColumns: Array<String>
        get() = arrayOf("$TASKS_TABLE_NAME.$COL_TASK_ID",  COL_TASK_NAME, COL_TASK_DESCRIPTION,COL_TASK_PRIORITY, COL_TASK_START_DATE, COL_TASK_DUE_DATE,
                COL_TASK_STATUS)

    private val allItemColumns: Array<String>
        get() = arrayOf("$ITEMS_TABLE_NAME.$COL_ITEM_ID",  COL_ITEM_DESCRIPTION, COL_ITEM_LONG_DESCRIPTION,COL_ITEM_RATE, COL_ITEM_TAX_RATE, COL_ITEM_TAX_RATE_2,
                COL_ITEM_GROUP_ID,COL_ITEM_UNIT)

    private val allClientColumns: Array<String>
        get() = arrayOf("$CLIENTS_TABLE_NAME.$COL_CLIENT_ID",
        COL_CLIENT_COMPANY , COL_CLIENT_VAT , COL_CLIENT_PHONENUMBER , COL_CLIENT_COUNTRY , COL_CLIENT_CITY , 
        COL_CLIENT_ZIP , COL_CLIENT_STATE , COL_CLIENT_ADDRESS , COL_CLIENT_WEBSITE , COL_CLIENT_DATECREATED , 
        COL_CLIENT_ACTIVE , COL_CLIENT_LEADID , COL_CLIENT_BILLING_STREET , COL_CLIENT_BILLING_CITY , COL_CLIENT_BILLING_STATE ,
        COL_CLIENT_BILLING_ZIP , COL_CLIENT_BILLING_COUNTRY , COL_CLIENT_SHIPPING_STREET , COL_CLIENT_SHIPPING_CITY , COL_CLIENT_SHIPPING_STATE ,
        COL_CLIENT_SHIPPING_ZIP , COL_CLIENT_SHIPPING_COUNTRY , COL_CLIENT_LATITUDE , COL_CLIENT_LONGITUDE , COL_CLIENT_DEFAULT_LANGUAGE ,
        COL_CLIENT_DEFAULT_CURRENCY , COL_CLIENT_SHOW_PRIMARY_CONTACT , COL_CLIENT_ADDEDFROM,COL_CLIENT_CONTACT_NAME,COL_CLIENT_CONTACT_EMAIL,COL_CLIENT_COUNTRY_NAME)
    
    private fun fillEvents(cursor: Cursor?): List<Event> {
        val eventTypeColors = SparseIntArray()
        fetchEventTypes().forEach {
            eventTypeColors.put(it.id, it.color)
        }

        val events = ArrayList<Event>()
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getIntValue(COL_ID)
                    val startTS = cursor.getIntValue(COL_START_TS)
                    val endTS = cursor.getIntValue(COL_END_TS)
                    val reminder1Minutes = cursor.getIntValue(COL_REMINDER_MINUTES)
                    val reminder2Minutes = cursor.getIntValue(COL_REMINDER_MINUTES_2)
                    val reminder3Minutes = cursor.getIntValue(COL_REMINDER_MINUTES_3)
                    val repeatInterval = cursor.getIntValue(COL_REPEAT_INTERVAL)
                    var repeatRule = cursor.getIntValue(COL_REPEAT_RULE)
                    val title = cursor.getStringValue(COL_TITLE)
                    val description = cursor.getStringValue(COL_DESCRIPTION)
                    val importId = cursor.getStringValue(COL_IMPORT_ID) ?: ""
                    val flags = cursor.getIntValue(COL_FLAGS)
                    val repeatLimit = cursor.getIntValue(COL_REPEAT_LIMIT)
                    val eventType = cursor.getIntValue(COL_EVENT_TYPE)
                    val offset = cursor.getStringValue(COL_OFFSET)
                    val isDstIncluded = cursor.getIntValue(COL_IS_DST_INCLUDED) == 1
                    val lastUpdated = cursor.getLongValue(COL_LAST_UPDATED)
                    val source = cursor.getStringValue(COL_EVENT_SOURCE)
                    val location = cursor.getStringValue(COL_LOCATION)
                    val color = eventTypeColors[eventType]

                    val ignoreEventOccurrences = if (repeatInterval != 0) {
                        getIgnoredOccurrences(id)
                    } else {
                        ArrayList()
                    }

                    if (repeatInterval > 0 && repeatInterval % MONTH == 0 && repeatRule == 0) {
                        repeatRule = REPEAT_MONTH_SAME_DAY
                    }

                    val event = Event(id, startTS, endTS, title, description, reminder1Minutes, reminder2Minutes, reminder3Minutes,
                            repeatInterval, importId, flags, repeatLimit, repeatRule, eventType, ignoreEventOccurrences, offset, isDstIncluded,
                            0, lastUpdated, source, color, location)
                    events.add(event)
                } while (cursor.moveToNext())
            }
        }
        return events
    }


    private fun fillTasks(cursor: Cursor?): List<Task> {

        val items = ArrayList<Task>()
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getIntValue(COL_TASK_ID)
                    val startdate = cursor.getStringValue(COL_TASK_START_DATE)
                    val duedate = cursor.getStringValue(COL_TASK_DUE_DATE)
                    val name = cursor.getStringValue(COL_TASK_NAME)
                    val description = cursor.getStringValue(COL_TASK_DESCRIPTION)
                    val priority = cursor.getStringValue(COL_TASK_PRIORITY) ?: ""
                    val status = cursor.getStringValue(COL_TASK_STATUS)

                    val task = Task(id, name, description, priority, startdate, duedate,status)
                    items.add(task)
                } while (cursor.moveToNext())
            }
        }
        return items
    }

    private fun fillItems(cursor: Cursor?): List<Item> {

        val items = ArrayList<Item>()
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val itemid = cursor.getIntValue(COL_ITEM_ID)
                    val description = cursor.getStringValue(COL_ITEM_DESCRIPTION)
                    val longdescription = cursor.getStringValue(COL_ITEM_LONG_DESCRIPTION)
                    val rate = cursor.getStringValue(COL_ITEM_RATE)
                    val taxrate = cursor.getStringValue(COL_ITEM_TAX_RATE)
                    val taxrate_2 = cursor.getStringValue(COL_ITEM_TAX_RATE_2)
                    val group_id = cursor.getStringValue(COL_ITEM_GROUP_ID)
                    val unit = cursor.getStringValue(COL_ITEM_UNIT)
                    val item = Item(itemid, description, longdescription, rate, taxrate,taxrate_2,group_id,unit)
                    items.add(item)
                } while (cursor.moveToNext())
            }
        }
        return items
    }

    private fun fillClients(cursor: Cursor?): List<Client> {

        val clients = ArrayList<Client>()
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val userid = cursor.getIntValue(COL_CLIENT_ID)
                    val company = cursor.getStringValue(COL_CLIENT_COMPANY)
                    val vat = cursor.getStringValue(COL_CLIENT_VAT)
                    val phonenumber = cursor.getStringValue(COL_CLIENT_PHONENUMBER)
                    val country = cursor.getStringValue(COL_CLIENT_COUNTRY)
                    val city = cursor.getStringValue(COL_CLIENT_CITY)
                    val zip = cursor.getStringValue(COL_CLIENT_ZIP)
                    val state = cursor.getStringValue(COL_CLIENT_STATE)
                    val address = cursor.getStringValue(COL_CLIENT_ADDRESS)
                    val website = cursor.getStringValue(COL_CLIENT_WEBSITE)
                    val datecreated = cursor.getStringValue(COL_CLIENT_DATECREATED)
                    val active = cursor.getStringValue(COL_CLIENT_ACTIVE)
                    val leadid = cursor.getStringValue(COL_CLIENT_LEADID)
                    val billing_street = cursor.getStringValue(COL_CLIENT_BILLING_CITY)
                    val billing_city = cursor.getStringValue(COL_CLIENT_BILLING_CITY)
                    val billing_state = cursor.getStringValue(COL_CLIENT_BILLING_STATE)
                    val billing_zip = cursor.getStringValue(COL_CLIENT_BILLING_ZIP)
                    val billing_country = cursor.getStringValue(COL_CLIENT_BILLING_COUNTRY)
                    val shipping_street = cursor.getStringValue(COL_CLIENT_SHIPPING_STREET)
                    val shipping_city = cursor.getStringValue(COL_CLIENT_SHIPPING_CITY)
                    val shipping_state = cursor.getStringValue(COL_CLIENT_SHIPPING_STATE)
                    val shipping_zip = cursor.getStringValue(COL_CLIENT_SHIPPING_ZIP)
                    val shipping_country = cursor.getStringValue(COL_CLIENT_SHIPPING_COUNTRY)
                    val longitude = cursor.getStringValue(COL_CLIENT_LONGITUDE)
                    val latitude = cursor.getStringValue(COL_CLIENT_LATITUDE)
                    val default_language = cursor.getStringValue(COL_CLIENT_DEFAULT_LANGUAGE)
                    val default_currency = cursor.getStringValue(COL_CLIENT_DEFAULT_CURRENCY)
                    val show_primary_contact = cursor.getStringValue(COL_CLIENT_SHOW_PRIMARY_CONTACT)
                    val addedfrom = cursor.getStringValue(COL_CLIENT_ADDEDFROM)
                    val contact_name= cursor.getStringValue(COL_CLIENT_CONTACT_NAME)
                    val contact_email= cursor.getStringValue(COL_CLIENT_CONTACT_EMAIL)
                    val country_name= cursor.getStringValue(COL_CLIENT_COUNTRY_NAME)

                    val client = Client(userid ,
                                    company,
                                    vat,
                                    phonenumber ,
                                    country,
                                    city,
                                    zip,
                                    state ,
                                    address,
                                    website,
                                    datecreated,
                                    active,
                                    leadid,
                                    billing_street,
                                    billing_city,
                                    billing_state,
                                    billing_zip,
                                    billing_country,
                                    shipping_street,
                                    shipping_city,
                                    shipping_state,
                                    shipping_zip,
                                    shipping_country,
                                    longitude ,
                                    latitude,
                                    default_language,
                                    default_currency,
                                    show_primary_contact,
                                    addedfrom,
                                    contact_name,
                                    contact_email,
                                    country_name
                                    )
                    clients.add(client)
                } while (cursor.moveToNext())
            }
        }
        return clients
    }
    
    fun getEventTypes(callback: (types: ArrayList<EventType>) -> Unit) {
        Thread {
            callback(fetchEventTypes())
        }.start()
    }

    fun fetchEventTypes(): ArrayList<EventType> {
        val eventTypes = ArrayList<EventType>(4)
        val cols = arrayOf(COL_TYPE_ID, COL_TYPE_TITLE, COL_TYPE_COLOR, COL_TYPE_CALDAV_CALENDAR_ID, COL_TYPE_CALDAV_DISPLAY_NAME, COL_TYPE_CALDAV_EMAIL)
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(TYPES_TABLE_NAME, cols, null, null, null, null, "$COL_TYPE_TITLE ASC")
            if (cursor?.moveToFirst() == true) {
                do {
                    val id = cursor.getIntValue(COL_TYPE_ID)
                    val title = cursor.getStringValue(COL_TYPE_TITLE)
                    val color = cursor.getIntValue(COL_TYPE_COLOR)
                    val calendarId = cursor.getIntValue(COL_TYPE_CALDAV_CALENDAR_ID)
                    val displayName = cursor.getStringValue(COL_TYPE_CALDAV_DISPLAY_NAME)
                    val email = cursor.getStringValue(COL_TYPE_CALDAV_EMAIL)
                    val eventType = EventType(id, title, color, calendarId, displayName, email)
                    eventTypes.add(eventType)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return eventTypes
    }

    fun getTasks(callback: (items: ArrayList<Task>) -> Unit) {
        Thread {
            callback(fetchTasks())
        }.start()
    }

    private fun getTasks(selection: String): List<Task> {
        val tasks = ArrayList<Task>()
        var cursor: Cursor? = null
        try {
            cursor = getTasksCursor(selection)
            if (cursor != null) {
                val currTasks = fillTasks(cursor)
                tasks.addAll(currTasks)
            }
        } finally {
            cursor?.close()
        }

        return tasks
    }


    fun fetchTasks(): ArrayList<Task> {
        val tasks = ArrayList<Task>(4)
        val cols = arrayOf(COL_TASK_ID, COL_TASK_NAME, COL_TASK_DESCRIPTION, COL_TASK_PRIORITY, COL_TASK_START_DATE, COL_TASK_DUE_DATE, COL_TASK_STATUS)
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(TASKS_TABLE_NAME, cols, null, null, null, null, "$COL_TASK_NAME ASC")
            if (cursor?.moveToFirst() == true) {
                do {
                    val id = cursor.getIntValue(COL_TASK_ID)
                    val name = cursor.getStringValue(COL_TASK_NAME)
                    val description = cursor.getStringValue(COL_TASK_DESCRIPTION)
                    val priority = cursor.getStringValue(COL_TASK_PRIORITY)
                    val startdate = cursor.getStringValue(COL_TASK_START_DATE)
                    val duedate = cursor.getStringValue(COL_TASK_DUE_DATE)
                    val status = cursor.getStringValue(COL_TASK_STATUS)

                    val task = Task(id, name, description , priority ,startdate ,duedate, status)
                    tasks.add(task)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return tasks
    }

    fun getItems(callback: (items: ArrayList<Item>) -> Unit) {
        Thread {
            callback(fetchItems())
        }.start()
    }


    private fun getItems(selection: String): List<Item> {
        val items = ArrayList<Item>()
        var cursor: Cursor? = null
        try {
            cursor = getItemsCursor(selection)
            if (cursor != null) {
                val currItems = fillItems(cursor)
                items.addAll(currItems)
            }
        } finally {
            cursor?.close()
        }

        return items
    }


    fun fetchItems(): ArrayList<Item> {
        val items = ArrayList<Item>(4)
        val cols = arrayOf(COL_ITEM_ID, COL_ITEM_DESCRIPTION, COL_ITEM_LONG_DESCRIPTION, COL_ITEM_RATE, COL_ITEM_TAX_RATE, COL_ITEM_TAX_RATE_2, COL_ITEM_GROUP_ID,COL_ITEM_UNIT)
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(ITEMS_TABLE_NAME, cols, null, null, null, null, "$COL_ITEM_DESCRIPTION ASC")
            if (cursor?.moveToFirst() == true) {
                do {
                    val itemid = cursor.getIntValue(COL_ITEM_ID)
                    val description = cursor.getStringValue(COL_ITEM_DESCRIPTION)
                    val long_description = cursor.getStringValue(COL_ITEM_LONG_DESCRIPTION)
                    val rate = cursor.getStringValue(COL_ITEM_RATE)
                    val taxrate = cursor.getStringValue(COL_ITEM_TAX_RATE)
                    val taxrate_2 = cursor.getStringValue(COL_ITEM_TAX_RATE_2)
                    val group_id = cursor.getStringValue(COL_ITEM_RATE)
                    val unit = cursor.getStringValue(COL_ITEM_UNIT)
                    val task = Item(itemid, description, long_description , rate ,taxrate ,taxrate_2, group_id,unit)
                    items.add(task)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return items
    }

    fun getClients(callback: (clients: ArrayList<Client>) -> Unit) {
        Thread {
            callback(fetchClients())
        }.start()
    }


    private fun getClients(selection: String): List<Client> {
        val clients = ArrayList<Client>()
        var cursor: Cursor? = null
        try {
            cursor = getClientsCursor(selection)
            if (cursor != null) {
                val currClients = fillClients(cursor)
                clients.addAll(currClients)
            }
        } finally {
            cursor?.close()
        }

        return clients
    }


    fun fetchClients(): ArrayList<Client> {
        val clients = ArrayList<Client>(4)
        val cols = arrayOf(COL_CLIENT_ID,COL_CLIENT_COMPANY , COL_CLIENT_VAT , COL_CLIENT_PHONENUMBER , COL_CLIENT_COUNTRY , COL_CLIENT_CITY ,
        COL_CLIENT_ZIP , COL_CLIENT_STATE , COL_CLIENT_ADDRESS , COL_CLIENT_WEBSITE , COL_CLIENT_DATECREATED ,
        COL_CLIENT_ACTIVE , COL_CLIENT_LEADID , COL_CLIENT_BILLING_STREET , COL_CLIENT_BILLING_CITY , COL_CLIENT_BILLING_STATE ,
        COL_CLIENT_BILLING_ZIP , COL_CLIENT_BILLING_COUNTRY , COL_CLIENT_SHIPPING_STREET , COL_CLIENT_SHIPPING_CITY , COL_CLIENT_SHIPPING_STATE ,
        COL_CLIENT_SHIPPING_ZIP , COL_CLIENT_SHIPPING_COUNTRY , COL_CLIENT_LATITUDE , COL_CLIENT_LONGITUDE , COL_CLIENT_DEFAULT_LANGUAGE ,
        COL_CLIENT_DEFAULT_CURRENCY , COL_CLIENT_SHOW_PRIMARY_CONTACT , COL_CLIENT_ADDEDFROM,COL_CLIENT_CONTACT_NAME,COL_CLIENT_CONTACT_EMAIL,
                COL_CLIENT_COUNTRY_NAME)
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(CLIENTS_TABLE_NAME, cols, null, null, null, null, "$COL_CLIENT_COMPANY ASC")
            if (cursor?.moveToFirst() == true) {
                do {
                    val userid = cursor.getIntValue(COL_CLIENT_ID)
                    val company = cursor.getStringValue(COL_CLIENT_COMPANY)
                    val vat = cursor.getStringValue(COL_CLIENT_VAT)
                    val phonenumber = cursor.getStringValue(COL_CLIENT_PHONENUMBER)
                    val country = cursor.getStringValue(COL_CLIENT_COUNTRY)
                    val city = cursor.getStringValue(COL_CLIENT_CITY)
                    val zip = cursor.getStringValue(COL_CLIENT_ZIP)
                    val state = cursor.getStringValue(COL_CLIENT_STATE)
                    val address = cursor.getStringValue(COL_CLIENT_ADDRESS)
                    val website = cursor.getStringValue(COL_CLIENT_WEBSITE)
                    val datecreated = cursor.getStringValue(COL_CLIENT_DATECREATED)
                    val active = cursor.getStringValue(COL_CLIENT_ACTIVE)
                    val leadid = cursor.getStringValue(COL_CLIENT_LEADID)
                    val billing_street = cursor.getStringValue(COL_CLIENT_BILLING_CITY)
                    val billing_city = cursor.getStringValue(COL_CLIENT_BILLING_CITY)
                    val billing_state = cursor.getStringValue(COL_CLIENT_BILLING_STATE)
                    val billing_zip = cursor.getStringValue(COL_CLIENT_BILLING_ZIP)
                    val billing_country = cursor.getStringValue(COL_CLIENT_BILLING_COUNTRY)
                    val shipping_street = cursor.getStringValue(COL_CLIENT_SHIPPING_STREET)
                    val shipping_city = cursor.getStringValue(COL_CLIENT_SHIPPING_CITY)
                    val shipping_state = cursor.getStringValue(COL_CLIENT_SHIPPING_STATE)
                    val shipping_zip = cursor.getStringValue(COL_CLIENT_SHIPPING_ZIP)
                    val shipping_country = cursor.getStringValue(COL_CLIENT_SHIPPING_COUNTRY)
                    val longitude = cursor.getStringValue(COL_CLIENT_LONGITUDE)
                    val latitude = cursor.getStringValue(COL_CLIENT_LATITUDE)
                    val default_language = cursor.getStringValue(COL_CLIENT_DEFAULT_LANGUAGE)
                    val default_currency = cursor.getStringValue(COL_CLIENT_DEFAULT_CURRENCY)
                    val show_primary_contact = cursor.getStringValue(COL_CLIENT_SHOW_PRIMARY_CONTACT)
                    val addedfrom = cursor.getStringValue(COL_CLIENT_ADDEDFROM)
                    val contact_name= cursor.getStringValue(COL_CLIENT_CONTACT_NAME)
                    val contact_email= cursor.getStringValue(COL_CLIENT_CONTACT_EMAIL)
                    val country_name= cursor.getStringValue(COL_CLIENT_COUNTRY_NAME)

                    val client = Client(userid ,
                            company,
                            vat,
                            phonenumber ,
                            country,
                            city,
                            zip,
                            state ,
                            address,
                            website,
                            datecreated,
                            active,
                            leadid,
                            billing_street,
                            billing_city,
                            billing_state,
                            billing_zip,
                            billing_country,
                            shipping_street,
                            shipping_city,
                            shipping_state,
                            shipping_zip,
                            shipping_country,
                            longitude ,
                            latitude,
                            default_language,
                            default_currency,
                            show_primary_contact,
                            addedfrom,
                            contact_name,
                            contact_email,
                            country_name
                    )
                    clients.add(client)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return clients
    }


    fun doEventTypesContainEvent(types: ArrayList<EventType>): Boolean {
        val args = TextUtils.join(", ", types.map { it.id })
        val columns = arrayOf(COL_ID)
        val selection = "$COL_EVENT_TYPE IN ($args)"
        var cursor: Cursor? = null
        try {
            cursor = mDb.query(MAIN_TABLE_NAME, columns, selection, null, null, null, null)
            return cursor?.moveToFirst() == true
        } finally {
            cursor?.close()
        }
    }

    private fun getIgnoredOccurrences(eventId: Int): ArrayList<Int> {
        val projection = arrayOf(COL_OCCURRENCE_DAYCODE)
        val selection = "$COL_PARENT_EVENT_ID = ?"
        val selectionArgs = arrayOf(eventId.toString())
        val daycodes = ArrayList<Int>()

        var cursor: Cursor? = null
        try {
            cursor = mDb.query(EXCEPTIONS_TABLE_NAME, projection, selection, selectionArgs, null, null, COL_OCCURRENCE_DAYCODE)
            if (cursor?.moveToFirst() == true) {
                do {
                    daycodes.add(cursor.getIntValue(COL_OCCURRENCE_DAYCODE))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return daycodes
    }

    private fun convertExceptionTimestampToDaycode(db: SQLiteDatabase) {
        val projection = arrayOf(COL_EXCEPTION_ID, COL_OCCURRENCE_TIMESTAMP)
        var cursor: Cursor? = null
        try {
            cursor = db.query(EXCEPTIONS_TABLE_NAME, projection, null, null, null, null, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    val id = cursor.getIntValue(COL_EXCEPTION_ID)
                    val ts = cursor.getIntValue(COL_OCCURRENCE_TIMESTAMP)
                    val values = ContentValues()
                    values.put(COL_OCCURRENCE_DAYCODE, Formatter.getDayCodeFromTS(ts))

                    val selection = "$COL_EXCEPTION_ID = ?"
                    val selectionArgs = arrayOf(id.toString())
                    db.update(EXCEPTIONS_TABLE_NAME, values, selection, selectionArgs)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
    }

    private fun setupRepeatRules(db: SQLiteDatabase) {
        val projection = arrayOf(COL_EVENT_ID, COL_REPEAT_INTERVAL, COL_REPEAT_START)
        val selection = "$COL_REPEAT_INTERVAL != 0"
        var cursor: Cursor? = null
        try {
            cursor = db.query(META_TABLE_NAME, projection, selection, null, null, null, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    val interval = cursor.getIntValue(COL_REPEAT_INTERVAL)
                    if (interval != MONTH && interval % WEEK != 0)
                        continue

                    val eventId = cursor.getIntValue(COL_EVENT_ID)
                    val start = cursor.getIntValue(COL_REPEAT_START)
                    var rule = Math.pow(2.0, (Formatter.getDateTimeFromTS(start).dayOfWeek - 1).toDouble()).toInt()
                    if (interval == MONTH) {
                        rule = REPEAT_MONTH_SAME_DAY
                    }

                    val values = ContentValues()
                    values.put(COL_REPEAT_RULE, rule)
                    val curSelection = "$COL_EVENT_ID = ?"
                    val curSelectionArgs = arrayOf(eventId.toString())
                    db.update(META_TABLE_NAME, values, curSelection, curSelectionArgs)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
    }
}
