package com.ecandle.raykun

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import com.ecandle.raykun.models.Contact
import com.simplemobiletools.commons.extensions.getIntValue
import com.simplemobiletools.commons.extensions.getStringValue


/**
 * Created by juantomaylla on 24/04/18.
 */
class DatabaseHelper public constructor(val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    public val CONTACTS_TABLE_NAME = "contacts"
    private val COL_CONTACT_ID = "id"
    private val COL_CONTACT_USER_ID = "userid"
    private val COL_CONTACT_IS_PRIMARY = "is_primary"
    private val COL_CONTACT_FIRST_NAME = "firstname"
    private val COL_CONTACT_LAST_NAME = "lastname"
    private val COL_CONTACT_EMAIL = "email"
    private val COL_CONTACT_PHONE_NUMBER = "phonenumber"
    private val COL_CONTACT_TITLE = "title"

    private val database: SQLiteDatabase = writableDatabase

    companion object {
        val DB_NAME = "demo.db"
    }

    override fun onCreate(db: SQLiteDatabase) {
        //sqLiteDatabase.execSQL(ExpenseTypeTable.CREATE_TABLE_QUERY)
        createContactsTable(db)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    fun addContact(mContact: Contact) : Int{
        //val database = this.writableDatabase
        //val values = ContentValues()
        //values.put(ExpenseTable.TYPE, type.getType())

        //database.insert(ExpenseTypeTable.TABLE_NAME, null, values)

        return insertContact(mContact,database)
        
    }

    fun insertContact(contact: Contact, db: SQLiteDatabase): Int {
        val values = fillContactValues(contact)
        val insertedId = db.insert(CONTACTS_TABLE_NAME, null, values).toInt()
        return insertedId
    }
    

    private fun fillContactValues(contact: Contact): ContentValues {
        return ContentValues().apply {
            put(COL_CONTACT_ID, contact.id.toString())
            put(COL_CONTACT_USER_ID, contact.userid)
            put(COL_CONTACT_IS_PRIMARY, contact.is_primary)
            put(COL_CONTACT_FIRST_NAME, contact.firstname)
            put(COL_CONTACT_LAST_NAME, contact.lastname)
            put(COL_CONTACT_EMAIL, contact.email)
            put(COL_CONTACT_PHONE_NUMBER, contact.phonenumber)
            put(COL_CONTACT_TITLE, contact.title)
        }
    }
    
    
    fun createContactsTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $CONTACTS_TABLE_NAME " +
                "($COL_CONTACT_ID INTEGER PRIMARY KEY, $COL_CONTACT_USER_ID TEXT, $COL_CONTACT_IS_PRIMARY TEXT, $COL_CONTACT_FIRST_NAME TEXT, $COL_CONTACT_LAST_NAME TEXT, " +
                "$COL_CONTACT_EMAIL TEXT, $COL_CONTACT_PHONE_NUMBER TEXT, $COL_CONTACT_TITLE TEXT)")
    }

    fun getContactWithId(id: Int): Contact? {
        val selection = "$CONTACTS_TABLE_NAME.$COL_CONTACT_ID = ?"
        val selectionArgs = arrayOf(id.toString())
        val cursor = getContactsCursor(selection, selectionArgs)
        val items = fillContacts(cursor)
        return if (items.isNotEmpty()) {
            items[0]
        } else {
            null
        }
    }


    private fun getContactsCursor(selection: String = "", selectionArgs: Array<String>? = null): Cursor? {
        val builder = SQLiteQueryBuilder()
        builder.tables = "$CONTACTS_TABLE_NAME"
        val projection = allContactColumns
        return builder.query(database, projection, selection, selectionArgs, "$CONTACTS_TABLE_NAME.$COL_CONTACT_ID", null, COL_CONTACT_LAST_NAME)
    }

    private fun fillContacts(cursor: Cursor?): List<Contact> {

        val contacts = ArrayList<Contact>()
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    val contactid = cursor.getIntValue(COL_CONTACT_ID)
                    val userid = cursor.getStringValue(COL_CONTACT_USER_ID)
                    val is_primary = cursor.getStringValue(COL_CONTACT_IS_PRIMARY)
                    val firstname = cursor.getStringValue(COL_CONTACT_FIRST_NAME)
                    val lastname = cursor.getStringValue(COL_CONTACT_LAST_NAME)
                    val email = cursor.getStringValue(COL_CONTACT_EMAIL)
                    val phonenumber = cursor.getStringValue(COL_CONTACT_PHONE_NUMBER)
                    val title = cursor.getStringValue(COL_CONTACT_TITLE)
                    val contact = Contact(contactid, userid, is_primary, firstname, lastname,email,phonenumber,title)
                    contacts.add(contact)
                } while (cursor.moveToNext())
            }
        }
        return contacts
    }

    private val allContactColumns: Array<String>
        get() = arrayOf("$CONTACTS_TABLE_NAME.$COL_CONTACT_ID",  COL_CONTACT_USER_ID, COL_CONTACT_IS_PRIMARY,COL_CONTACT_FIRST_NAME, COL_CONTACT_LAST_NAME, COL_CONTACT_EMAIL,
                COL_CONTACT_PHONE_NUMBER,COL_CONTACT_TITLE)

    fun getContacts(callback: (contacts: ArrayList<Contact>) -> Unit) {
        Thread {
            callback(fetchContacts())
        }.start()
    }

    fun fetchContacts(): ArrayList<Contact> {
        val contacts = ArrayList<Contact>(4)
        val cols = arrayOf(COL_CONTACT_ID, COL_CONTACT_USER_ID, COL_CONTACT_IS_PRIMARY, COL_CONTACT_FIRST_NAME, COL_CONTACT_LAST_NAME, COL_CONTACT_EMAIL, COL_CONTACT_PHONE_NUMBER,COL_CONTACT_TITLE)
        var cursor: Cursor? = null
        try {
            cursor = database.query(CONTACTS_TABLE_NAME, cols, null, null, null, null, "$COL_CONTACT_ID ASC")
            if (cursor?.moveToFirst() == true) {
                do {
                    val contactid = cursor.getIntValue(COL_CONTACT_ID)
                    val userid = cursor.getStringValue(COL_CONTACT_USER_ID)
                    val is_primary = cursor.getStringValue(COL_CONTACT_IS_PRIMARY)
                    val firstname = cursor.getStringValue(COL_CONTACT_FIRST_NAME)
                    val lastname = cursor.getStringValue(COL_CONTACT_LAST_NAME)
                    val email = cursor.getStringValue(COL_CONTACT_EMAIL)
                    val phonenumber = cursor.getStringValue(COL_CONTACT_PHONE_NUMBER)
                    val title = cursor.getStringValue(COL_CONTACT_TITLE)
                    val contact = Contact(contactid, userid, is_primary, firstname, lastname, email, phonenumber, title)
                    contacts.add(contact)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor?.close()
        }
        return contacts
    }

}