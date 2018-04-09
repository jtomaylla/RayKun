package com.ecandle.raykun.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.ecandle.raykun.R
import com.ecandle.raykun.dialogs.DeleteProductDialog
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.helpers.ITEM_ID
import com.ecandle.raykun.models.Item
import com.simplemobiletools.commons.extensions.getDialogTheme
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.extensions.updateTextColors
import com.simplemobiletools.commons.extensions.value
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : SimpleActivity() {
    private val LOG_TAG = ProductActivity::class.java.simpleName
    private var mDialogTheme = 0

    private var wasActivityInitialized = false
    
    
    lateinit var mItem: Item
    private var mItemDescription = ""
    private var mItemLongDescription = ""
    private var mRate  = ""
    private var mTaxRate = ""
    private var mTaxRate2 =  ""
    private var mGroupId = ""
    private var mUnit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        val intent = intent ?: return
        mDialogTheme = getDialogTheme()

        val itemId = intent.getIntExtra(ITEM_ID, 0)
        val item = dbHelper.getItemWithId(itemId)
        if (itemId != 0 && item == null) {
            finish()
            return
        }

        if (item != null) {
            mItem = item
            setupEditItem()
        } else {
            mItem = Item(0,"","","","","","","")

//            if (startTS == 0) {
//                return
//            }

            setupNewItem()
        }

        updateDescription()
        updateLongDescription()
        updateRate()
        updateTaxRate()
        updateTaxRate2()
        updateGroupId()
        updateUnit()

//        task_start_date.setOnClickListener { setupStartDate() }
//        //task_start_time.setOnClickListener { setupStartTime() }
//        task_due_date.setOnClickListener { setupEndDate() }
//        //task_end_time.setOnClickListener { setupEndTime() }
//
//        task_priority.setOnClickListener { showPriorityDialog() }
//        task_status.setOnClickListener { showStatusDialog() }
        
        updateTextColors(product_scrollview)

        wasActivityInitialized = true
    }

    private fun setupEditItem() {
        //val realStart = mItem.startdate
//        val realStart = DateTime(mItem.startdate)
//        val realEnd = DateTime(mItem.duedate)
        //val duration = mItem.endTS - mItem.startTS
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar?.title = resources.getString(R.string.edit_item)
        product_description.setText(mItem.description)
        product_long_description.setText(mItem.long_description)
        product_rate.setText(mItem.rate)
        product_taxrate.setText(mItem.taxrate)
        product_taxrate_2.setText(mItem.taxrate_2)
        product_description.movementMethod = LinkMovementMethod.getInstance()
        product_unit.setText(mItem.unit)

    }

    private fun setupNewItem() {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        supportActionBar?.title = resources.getString(R.string.new_item)
    }


    //private fun isLastDayOfTheMonth() = mItemStartDateTime.dayOfMonth == mItemStartDateTime.dayOfMonth().withMaximumValue().dayOfMonth

    //private fun isLastWeekDayOfMonth() = mItemStartDateTime.monthOfYear != mItemStartDateTime.plusDays(7).monthOfYear

//    private fun getRepeatXthDayString(includeBase: Boolean, repeatRule: Int): String {
//        val dayOfWeek = mItemStartDateTime.dayOfWeek
//        val base = getBaseString(dayOfWeek)
//        val order = getOrderString(repeatRule)
//        val dayString = getDayString(dayOfWeek)
//        return if (includeBase) {
//            "$base $order $dayString"
//        } else {
//            val everyString = getString(if (isMaleGender(mItemStartDateTime.dayOfWeek)) R.string.every_m else R.string.every_f)
//            "$everyString $order $dayString"
//        }
//    }

//    private fun getBaseString(day: Int): String {
//        return getString(if (isMaleGender(day)) {
//            R.string.repeat_every_m
//        } else {
//            R.string.repeat_every_f
//        })
//    }

//    private fun isMaleGender(day: Int) = day == 1 || day == 2 || day == 4 || day == 5

//    private fun getOrderString(repeatRule: Int): String {
//        val dayOfMonth = mItemStartDateTime.dayOfMonth
//        var order = (dayOfMonth - 1) / 7 + 1
//        if (order == 4 && isLastWeekDayOfMonth() && repeatRule == REPEAT_MONTH_ORDER_WEEKDAY_USE_LAST) {
//            order = -1
//        }
//
//        val isMale = isMaleGender(mItemStartDateTime.dayOfWeek)
//        return getString(when (order) {
//            1 -> if (isMale) R.string.first_m else R.string.first_f
//            2 -> if (isMale) R.string.second_m else R.string.second_f
//            3 -> if (isMale) R.string.third_m else R.string.third_f
//            4 -> if (isMale) R.string.fourth_m else R.string.fourth_f
//            else -> if (isMale) R.string.last_m else R.string.last_f
//        })
//    }
//
//    private fun getDayString(day: Int): String {
//        return getString(when (day) {
//            1 -> R.string.monday_alt
//            2 -> R.string.tuesday_alt
//            3 -> R.string.wednesday_alt
//            4 -> R.string.thursday_alt
//            5 -> R.string.friday_alt
//            6 -> R.string.saturday_alt
//            else -> R.string.sunday_alt
//        })
//    }
//
//    private fun getPriorityValue(priorityText: String): String =
//        when (priorityText) {
//            getString(R.string.no_priority) -> "0"
//            getString(R.string.low_priority) -> PRIORITY_LOW.toString()
//            getString(R.string.medium_priority) -> PRIORITY_MEDIUM.toString()
//            getString(R.string.high_priority) -> PRIORITY_HIGH.toString()
//            getString(R.string.urgent_priority) -> PRIORITY_URGENT.toString()
//            else -> ""
//        }
//
//    private fun getStatusValue(statusText: String): String =
//            when (statusText) {
//                getString(R.string.pending_status) -> STATUS_PENDING.toString()
//                getString(R.string.on_progress_status) -> STATUS_ON_PROGRESS.toString()
//                getString(R.string.testing_status) -> STATUS_TESTING.toString()
//                getString(R.string.completed_status) -> STATUS_COMPLETED.toString()
//                else -> ""
//            }
    private fun updateDescription() {
        product_long_description.setText(mItem.description)
    }

    private fun updateLongDescription() {
        product_description.setText(mItem.long_description)
    }

    private fun updateRate() {
        product_rate.setText(mItem.rate)
    }

    private fun updateTaxRate() {
        product_taxrate.setText(mItem.taxrate)
    }

    private fun updateTaxRate2() {
        product_taxrate_2.setText(mItem.taxrate_2)
    }

    private fun updateGroupId() {
        product_group_id.setText(mItem.group_id)
    }

    private fun updateUnit() {
        product_unit.setText(mItem.unit)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        if (wasActivityInitialized) {
            menu.findItem(R.id.delete).isVisible = mItem.itemid != 0
            menu.findItem(R.id.exit).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> saveItem()
            R.id.delete -> deleteItem()
            R.id.exit -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

//    private fun shareItem() {
//        shareEvents(arrayListOf(mItem.id))
//    }

    private fun deleteItem() {
        DeleteProductDialog(this, arrayListOf(mItem.itemid)) {
            if (it) {
                dbHelper.deleteItems(arrayOf(mItem.itemid.toString()))
            }
            finish()
        }
    }

    private fun saveItem() {
        val newId = mItem.itemid
        val newDescription  = product_description.value
        if (newDescription.isEmpty()) {
            toast(R.string.title_empty)
            product_description.requestFocus()
            return
        }
        val newLongDescription = product_description.value
        val newRate = product_rate.value
        val newTaxRate= product_taxrate.value
        val newTaxRate2 = product_taxrate_2.value
        val newGroupId = product_group_id.value
        val newUnit = product_unit.value

        mItem = Item(itemid = newId,
                description = newDescription,
                long_description =  newLongDescription,
                rate = newRate,
                taxrate = newTaxRate,
                taxrate_2 = newTaxRate2,
                group_id = newGroupId,
                unit = newUnit
        )
        //mItem.id = 0
        Log.d(LOG_TAG,"mItem.itemid = $mItem.itemid")
        storeItem()
    }

    private fun storeItem() {
        if (mItem.itemid == 0) {
            dbHelper.insertItem(mItem)
            Log.d(LOG_TAG,"item added")
            finish()
        } else {
            dbHelper.updateItem(mItem)
            Log.d(LOG_TAG,"item updated")
           itemUpdated()
        }
    }

    private fun itemUpdated() {
        toast(R.string.item_updated)
        finish()
    }

//    private fun updateStartTexts() {
//        updateStartDateText()
//        //updateStartTimeText()
//    }
//
//    private fun updateStartDateText() {
//        //task_start_date.text = Formatter.getDate(applicationContext, mItemStartDateTime)
//        task_start_date.text = Formatter.getDayISOFromDateTime( mItemStartDateTime)
//        checkStartEndValidity()
//    }

//    private fun updateStartTimeText() {
//        task_start_time.text = Formatter.getTime(this, mItemStartDateTime)
//        checkStartEndValidity()
//    }

//    private fun updateEndTexts() {
//        updateEndDateText()
//        //updateEndTimeText()
//    }
//
//    private fun updateEndDateText() {
//        task_due_date.text = Formatter.getDayISOFromDateTime( mItemEndDateTime)
//        checkStartEndValidity()
//    }

//    private fun updateEndTimeText() {
//        task_end_time.text = Formatter.getTime(this, mItemEndDateTime)
//        checkStartEndValidity()
//    }

//    private fun checkStartEndValidity() {
//        val textColor = if (mItemStartDateTime.isAfter(mItemEndDateTime)) resources.getColor(R.color.red_text) else config.textColor
//        task_start_date.setTextColor(textColor)
//        task_due_date.setTextColor(textColor)
//    }

//    @SuppressLint("NewApi")
//    private fun setupStartDate() {
//        hideKeyboard()
//        config.backgroundColor.getContrastColor()
//        val datepicker = DatePickerDialog(this, mDialogTheme, startDateSetListener, mItemStartDateTime.year, mItemStartDateTime.monthOfYear - 1,
//                mItemStartDateTime.dayOfMonth)
//
//        if (isLollipopPlus()) {
//            datepicker.datePicker.firstDayOfWeek = if (config.isSundayFirst) Calendar.SUNDAY else Calendar.MONDAY
//        }
//
//        datepicker.show()
//    }

//    private fun setupStartTime() {
//        hideKeyboard()
//        TimePickerDialog(this, mDialogTheme, startTimeSetListener, mItemStartDateTime.hourOfDay, mItemStartDateTime.minuteOfHour, config.use24hourFormat).show()
//    }

//    @SuppressLint("NewApi")
//    private fun setupEndDate() {
//        hideKeyboard()
//        val datepicker = DatePickerDialog(this, mDialogTheme, endDateSetListener, mItemEndDateTime.year, mItemEndDateTime.monthOfYear - 1,
//                mItemEndDateTime.dayOfMonth)
//
//        if (isLollipopPlus()) {
//            datepicker.datePicker.firstDayOfWeek = if (config.isSundayFirst) Calendar.SUNDAY else Calendar.MONDAY
//        }
//
//        datepicker.show()
//    }

//    private fun setupEndTime() {
//        hideKeyboard()
//        TimePickerDialog(this, mDialogTheme, endTimeSetListener, mItemEndDateTime.hourOfDay, mItemEndDateTime.minuteOfHour, config.use24hourFormat).show()
//    }

//    private val startDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//        dateSet(year, monthOfYear, dayOfMonth, true)
//    }

//    private val startTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//        timeSet(hourOfDay, minute, true)
//    }

//    private val endDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> dateSet(year, monthOfYear, dayOfMonth, false) }

//    private val endTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> timeSet(hourOfDay, minute, false) }


//    private fun updateRepetitionText() {
//        event_repetition.text = getRepetitionText(mPriority)
//    }


//    private fun dateSet(year: Int, month: Int, day: Int, isStart: Boolean) {
//        if (isStart) {
//            val diff = mItemEndDateTime.seconds() - mItemStartDateTime.seconds()
//
//            mItemStartDateTime = mItemStartDateTime.withDate(year, month + 1, day)
//            updateStartDateText()
//            //checkRepeatRule()
//
//            mItemEndDateTime = mItemStartDateTime.plusSeconds(diff)
//            updateEndTexts()
//        } else {
//            mItemEndDateTime = mItemEndDateTime.withDate(year, month + 1, day)
//            updateEndDateText()
//        }
//    }

//    private fun timeSet(hours: Int, minutes: Int, isStart: Boolean) {
//        if (isStart) {
//            val diff = mItemEndDateTime.seconds() - mItemStartDateTime.seconds()
//
//            mItemStartDateTime = mItemStartDateTime.withHourOfDay(hours).withMinuteOfHour(minutes)
//            //updateStartTimeText()
//
//            mItemEndDateTime = mItemStartDateTime.plusSeconds(diff)
//            updateEndTexts()
//        } else {
//            mItemEndDateTime = mItemEndDateTime.withHourOfDay(hours).withMinuteOfHour(minutes)
//            //updateEndTimeText()
//        }
//    }

//    private fun showPriorityDialog() {
//        showItemPriorityDialog(mPriority) {
//            setPriority(it)
//        }
//    }
//
//    private fun setPriority(priority: Int) {
//        mPriority = priority
//        updatePriorityText()
//    }
//
//    private fun showStatusDialog() {
//        showItemStatusDialog(mStatus) {
//            setStatus(it)
//        }
//    }
//
//    private fun setStatus(status: Int) {
//        mStatus = status
//        updateStatusText()
//    }
//
//    protected fun showItemPriorityDialog(curPriority: Int, callback: (minutes: Int) -> Unit) {
//        hideKeyboard()
//        val priorities = TreeSet<Int>()
//        priorities.apply {
//            add(0)
//            add(PRIORITY_LOW)
//            add(PRIORITY_MEDIUM)
//            add(PRIORITY_HIGH)
//            add(PRIORITY_URGENT)
//            add(curPriority)
//        }
//
//        val items = ArrayList<RadioItem>(priorities.size + 1)
//        priorities.mapIndexedTo(items, { index, value ->
//            RadioItem(index, getPriorityText(value), value)
//        })
//
//        var selectedIndex = 0
//        priorities.forEachIndexed { index, value ->
//            if (value == curPriority)
//                selectedIndex = index
//        }
//
//        //items.add(RadioItem(-1, getString(R.string.custom)))
//
//        RadioGroupDialog(this, items, selectedIndex) {
//                callback(it as Int)
//        }
//    }
//
//    protected fun showItemStatusDialog(curStatus: Int, callback: (minutes: Int) -> Unit) {
//        hideKeyboard()
//        val statuses = TreeSet<Int>()
//        statuses.apply {
//            add(STATUS_PENDING)
//            add(STATUS_ON_PROGRESS)
//            add(STATUS_TESTING)
//            add(STATUS_COMPLETED)
//            add(curStatus)
//        }
//
//        val items = ArrayList<RadioItem>(statuses.size + 1)
//        statuses.mapIndexedTo(items, { index, value ->
//            RadioItem(index, getStatusText(value), value)
//        })
//
//        var selectedIndex = 0
//        statuses.forEachIndexed { index, value ->
//            if (value == curStatus)
//                selectedIndex = index
//        }
//
//        RadioGroupDialog(this, items, selectedIndex) {
//            callback(it as Int)
//        }
//    }
//
//    fun getPriorityText(priority: Int) = when (priority) {
//        0 -> getString(R.string.no_priority)
//        PRIORITY_LOW -> getString(R.string.low_priority)
//        PRIORITY_MEDIUM -> getString(R.string.medium_priority)
//        PRIORITY_HIGH -> getString(R.string.high_priority)
//        PRIORITY_URGENT -> getString(R.string.urgent_priority)
//        else -> getString(R.string.none)
//    }
//
//    fun getStatusText(status: Int) = when (status) {
//        STATUS_PENDING -> getString(R.string.pending_status)
//        STATUS_ON_PROGRESS -> getString(R.string.on_progress_status)
//        STATUS_TESTING -> getString(R.string.testing_status)
//        STATUS_COMPLETED -> getString(R.string.completed_status)
//        else -> getString(R.string.none)
//    }
}
