package com.ecandle.raykun

import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.runner.AndroidJUnit4
import com.ecandle.raykun.models.Contact

import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class DatabaseHelperTest {

    private var database:DatabaseHelper? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        getTargetContext().deleteDatabase(DatabaseHelper.DB_NAME)
        database =DatabaseHelper(getTargetContext())
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        database!!.close()
    }

    @org.junit.Test
    @Throws(Exception::class)
    fun shouldAddContact() {
        //database!!.addExpenseType(ExpenseType("Food"))
        val insertedId = database!!.addContact(Contact(1,
                "newUserid",
                "newIs_primary",
                "newFirstname",
                "newLastname",
                "newEmail",
                "newPhonenumber",
                "newTitle")
        )
        val testContact = database!!.getContactWithId(1)

        //val contacts = database.getContacts()

        assertThat(insertedId, `is`(1))
        assertTrue(testContact!!.is_primary == "newIs_primary")
    }
}