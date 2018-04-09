package com.ecandle.raykun.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.ecandle.raykun.R
import com.ecandle.raykun.dialogs.DeleteTaskDialog
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.dbHelper
import com.ecandle.raykun.extensions.seconds
import com.ecandle.raykun.helpers.*
import com.ecandle.raykun.helpers.Formatter
import com.ecandle.raykun.models.Task
import com.simplemobiletools.commons.dialogs.RadioGroupDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.models.RadioItem
import kotlinx.android.synthetic.main.activity_task.*
import org.joda.time.DateTime
import java.util.*

class TaskActivity : SimpleActivity() {
    private val LOG_TAG = TaskActivity::class.java.simpleName
    private var mDialogTheme = 0
    private var mPriority = 0
    private var mStatus = 0
    private var mTaskCalendarId = STORED_LOCALLY_ONLY
    private var wasActivityInitialized = false

    lateinit var mTaskStartDateTime: DateTime
    lateinit var mTaskEndDateTime: DateTime
    lateinit var mTask: Task

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_cross)

        val intent = intent ?: return
        mDialogTheme = getDialogTheme()

        val taskId = intent.getIntExtra(TASK_ID, 0)
        //val event = dbHelper.getEventWithId(eventId)
        val task = dbHelper.getTaskWithId(taskId)
        if (taskId != 0 && task == null) {
            finish()
            return
        }

        if (task != null) {
            mTask = task
            //mTaskOccurrenceTS = intent.getIntExtra(task_OCCURRENCE_TS, 0)
            setupEditTask()
            mPriority = mTask.priority.toInt()
            mStatus = mTask.status.toInt()
        } else {
            mTask = Task(0,"","","","","","")

            val startTS = intent.getIntExtra(NEW_TASK_START_TS, 0)
            if (startTS == 0) {
                return
            }

            setupNewTask(Formatter.getDateTimeFromTS(startTS))
            mPriority = 0
            mStatus = 0
        }


        updateStartTexts()
        updateEndTexts()

        updateName()
        updateDescription()
        updatePriorityText()
        
        updateStatusText()

        task_start_date.setOnClickListener { setupStartDate() }
        //task_start_time.setOnClickListener { setupStartTime() }
        task_due_date.setOnClickListener { setupEndDate() }
        //task_end_time.setOnClickListener { setupEndTime() }

        task_priority.setOnClickListener { showPriorityDialog() }
        task_status.setOnClickListener { showStatusDialog() }
        
        updateTextColors(task_scrollview)

        wasActivityInitialized = true
    }

    private fun setupEditTask() {
        //val realStart = mTask.startdate
        val realStart = DateTime(mTask.startdate)
        val realEnd = DateTime(mTask.duedate)
        //val duration = mTask.endTS - mTask.startTS
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar?.title = resources.getString(R.string.edit_task)
        mTaskStartDateTime = realStart
        mTaskEndDateTime = realEnd
        task_name.setText(mTask.name)
        task_priority.setText(mTask.priority)
        task_description.setText(mTask.description)
        task_description.movementMethod = LinkMovementMethod.getInstance()
        task_status.setText(mTask.status)

    }

    private fun setupNewTask(dateTime: DateTime) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        supportActionBar?.title = resources.getString(R.string.new_task)
        mTaskStartDateTime = dateTime
        mTaskEndDateTime = dateTime

    }


    private fun isLastDayOfTheMonth() = mTaskStartDateTime.dayOfMonth == mTaskStartDateTime.dayOfMonth().withMaximumValue().dayOfMonth

    private fun isLastWeekDayOfMonth() = mTaskStartDateTime.monthOfYear != mTaskStartDateTime.plusDays(7).monthOfYear

    private fun getRepeatXthDayString(includeBase: Boolean, repeatRule: Int): String {
        val dayOfWeek = mTaskStartDateTime.dayOfWeek
        val base = getBaseString(dayOfWeek)
        val order = getOrderString(repeatRule)
        val dayString = getDayString(dayOfWeek)
        return if (includeBase) {
            "$base $order $dayString"
        } else {
            val everyString = getString(if (isMaleGender(mTaskStartDateTime.dayOfWeek)) R.string.every_m else R.string.every_f)
            "$everyString $order $dayString"
        }
    }

    private fun getBaseString(day: Int): String {
        return getString(if (isMaleGender(day)) {
            R.string.repeat_every_m
        } else {
            R.string.repeat_every_f
        })
    }

    private fun isMaleGender(day: Int) = day == 1 || day == 2 || day == 4 || day == 5

    private fun getOrderString(repeatRule: Int): String {
        val dayOfMonth = mTaskStartDateTime.dayOfMonth
        var order = (dayOfMonth - 1) / 7 + 1
        if (order == 4 && isLastWeekDayOfMonth() && repeatRule == REPEAT_MONTH_ORDER_WEEKDAY_USE_LAST) {
            order = -1
        }

        val isMale = isMaleGender(mTaskStartDateTime.dayOfWeek)
        return getString(when (order) {
            1 -> if (isMale) R.string.first_m else R.string.first_f
            2 -> if (isMale) R.string.second_m else R.string.second_f
            3 -> if (isMale) R.string.third_m else R.string.third_f
            4 -> if (isMale) R.string.fourth_m else R.string.fourth_f
            else -> if (isMale) R.string.last_m else R.string.last_f
        })
    }

    private fun getDayString(day: Int): String {
        return getString(when (day) {
            1 -> R.string.monday_alt
            2 -> R.string.tuesday_alt
            3 -> R.string.wednesday_alt
            4 -> R.string.thursday_alt
            5 -> R.string.friday_alt
            6 -> R.string.saturday_alt
            else -> R.string.sunday_alt
        })
    }

    private fun getPriorityValue(priorityText: String): String =
        when (priorityText) {
            getString(R.string.no_priority) -> "0"
            getString(R.string.low_priority) -> PRIORITY_LOW.toString()
            getString(R.string.medium_priority) -> PRIORITY_MEDIUM.toString()
            getString(R.string.high_priority) -> PRIORITY_HIGH.toString()
            getString(R.string.urgent_priority) -> PRIORITY_URGENT.toString()
            else -> ""
        }

    private fun getStatusValue(statusText: String): String =
            when (statusText) {
                getString(R.string.pending_status) -> STATUS_PENDING.toString()
                getString(R.string.on_progress_status) -> STATUS_ON_PROGRESS.toString()
                getString(R.string.testing_status) -> STATUS_TESTING.toString()
                getString(R.string.completed_status) -> STATUS_COMPLETED.toString()
                else -> ""
            }

    private fun updateName() {
        task_name.setText(mTask.name)
    }

    private fun updateDescription() {
        task_description.setText(mTask.description)
    }
    
    private fun updatePriority() {
        task_priority.setText(mTask.priority)
    }

    private fun updatePriorityText() {
        task_priority.setText(getPriorityText(mPriority))
    }
    
    private fun updateStatus() {
        task_status.setText(mTask.status)
    }

    private fun updateStatusText() {
        task_status.setText(getStatusText(mStatus))
    }
    
/*    private fun toggleAllDay(isChecked: Boolean) {
        hideKeyboard()
        task_start_time.beGoneIf(isChecked)
        task_end_time.beGoneIf(isChecked)
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_task, menu)
        if (wasActivityInitialized) {
            menu.findItem(R.id.delete).isVisible = mTask.id != 0
            menu.findItem(R.id.exit).isVisible = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> saveTask()
            R.id.delete -> deleteTask()
            R.id.exit -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

//    private fun shareTask() {
//        shareEvents(arrayListOf(mTask.id))
//    }

    private fun deleteTask() {
        DeleteTaskDialog(this, arrayListOf(mTask.id)) {
            if (it) {
                dbHelper.deleteTasks(arrayOf(mTask.id.toString()))
            }
            finish()
        }
    }

    private fun saveTask() {
        val newId = mTask.id
        val newName = task_name.value
        if (newName.isEmpty()) {
            toast(R.string.title_empty)
            task_name.requestFocus()
            return
        }
        val newDescription = task_description.value
        val newPriority = getPriorityValue(task_priority.value)

        val newStartDate= task_start_date.value
        val newDueDate = task_due_date.value
        val newStatus = getStatusValue(task_status.value)

        mTask = Task(id = newId,
                name = newName,
                description = newDescription,
                priority = newPriority,
                startdate = newStartDate,
                duedate = newDueDate,
                status = newStatus
        )
        //mTask.id = 0
        Log.d(LOG_TAG,"mTask.id = $mTask.id")
        storeTask()
    }

    private fun storeTask() {
        if (mTask.id == 0) {
            dbHelper.insertTask(mTask)
            Log.d(LOG_TAG,"task added")
            finish()
        } else {
            dbHelper.updateTask(mTask)
            Log.d(LOG_TAG,"task updated")
            taskUpdated()
        }
    }

    private fun taskUpdated() {
        toast(R.string.task_updated)
        finish()
    }

    private fun updateStartTexts() {
        updateStartDateText()
        //updateStartTimeText()
    }

    private fun updateStartDateText() {
        //task_start_date.text = Formatter.getDate(applicationContext, mTaskStartDateTime)
        task_start_date.text = Formatter.getDayISOFromDateTime( mTaskStartDateTime)
        checkStartEndValidity()
    }

//    private fun updateStartTimeText() {
//        task_start_time.text = Formatter.getTime(this, mTaskStartDateTime)
//        checkStartEndValidity()
//    }

    private fun updateEndTexts() {
        updateEndDateText()
        //updateEndTimeText()
    }

    private fun updateEndDateText() {
        task_due_date.text = Formatter.getDayISOFromDateTime( mTaskEndDateTime)
        checkStartEndValidity()
    }

//    private fun updateEndTimeText() {
//        task_end_time.text = Formatter.getTime(this, mTaskEndDateTime)
//        checkStartEndValidity()
//    }

    private fun checkStartEndValidity() {
        val textColor = if (mTaskStartDateTime.isAfter(mTaskEndDateTime)) resources.getColor(R.color.red_text) else config.textColor
        task_start_date.setTextColor(textColor)
        task_due_date.setTextColor(textColor)
    }

    @SuppressLint("NewApi")
    private fun setupStartDate() {
        hideKeyboard()
        config.backgroundColor.getContrastColor()
        val datepicker = DatePickerDialog(this, mDialogTheme, startDateSetListener, mTaskStartDateTime.year, mTaskStartDateTime.monthOfYear - 1,
                mTaskStartDateTime.dayOfMonth)

        if (isLollipopPlus()) {
            datepicker.datePicker.firstDayOfWeek = if (config.isSundayFirst) Calendar.SUNDAY else Calendar.MONDAY
        }

        datepicker.show()
    }

//    private fun setupStartTime() {
//        hideKeyboard()
//        TimePickerDialog(this, mDialogTheme, startTimeSetListener, mTaskStartDateTime.hourOfDay, mTaskStartDateTime.minuteOfHour, config.use24hourFormat).show()
//    }

    @SuppressLint("NewApi")
    private fun setupEndDate() {
        hideKeyboard()
        val datepicker = DatePickerDialog(this, mDialogTheme, endDateSetListener, mTaskEndDateTime.year, mTaskEndDateTime.monthOfYear - 1,
                mTaskEndDateTime.dayOfMonth)

        if (isLollipopPlus()) {
            datepicker.datePicker.firstDayOfWeek = if (config.isSundayFirst) Calendar.SUNDAY else Calendar.MONDAY
        }

        datepicker.show()
    }

//    private fun setupEndTime() {
//        hideKeyboard()
//        TimePickerDialog(this, mDialogTheme, endTimeSetListener, mTaskEndDateTime.hourOfDay, mTaskEndDateTime.minuteOfHour, config.use24hourFormat).show()
//    }

    private val startDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateSet(year, monthOfYear, dayOfMonth, true)
    }

//    private val startTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
//        timeSet(hourOfDay, minute, true)
//    }

    private val endDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth -> dateSet(year, monthOfYear, dayOfMonth, false) }

//    private val endTimeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute -> timeSet(hourOfDay, minute, false) }


//    private fun updateRepetitionText() {
//        event_repetition.text = getRepetitionText(mPriority)
//    }


    private fun dateSet(year: Int, month: Int, day: Int, isStart: Boolean) {
        if (isStart) {
            val diff = mTaskEndDateTime.seconds() - mTaskStartDateTime.seconds()

            mTaskStartDateTime = mTaskStartDateTime.withDate(year, month + 1, day)
            updateStartDateText()
            //checkRepeatRule()

            mTaskEndDateTime = mTaskStartDateTime.plusSeconds(diff)
            updateEndTexts()
        } else {
            mTaskEndDateTime = mTaskEndDateTime.withDate(year, month + 1, day)
            updateEndDateText()
        }
    }

//    private fun timeSet(hours: Int, minutes: Int, isStart: Boolean) {
//        if (isStart) {
//            val diff = mTaskEndDateTime.seconds() - mTaskStartDateTime.seconds()
//
//            mTaskStartDateTime = mTaskStartDateTime.withHourOfDay(hours).withMinuteOfHour(minutes)
//            //updateStartTimeText()
//
//            mTaskEndDateTime = mTaskStartDateTime.plusSeconds(diff)
//            updateEndTexts()
//        } else {
//            mTaskEndDateTime = mTaskEndDateTime.withHourOfDay(hours).withMinuteOfHour(minutes)
//            //updateEndTimeText()
//        }
//    }

    private fun showPriorityDialog() {
        showTaskPriorityDialog(mPriority) {
            setPriority(it)
        }
    }

    private fun setPriority(priority: Int) {
        mPriority = priority
        updatePriorityText()
    }

    private fun showStatusDialog() {
        showTaskStatusDialog(mStatus) {
            setStatus(it)
        }
    }

    private fun setStatus(status: Int) {
        mStatus = status
        updateStatusText()
    }
    
    protected fun showTaskPriorityDialog(curPriority: Int, callback: (minutes: Int) -> Unit) {
        hideKeyboard()
        val priorities = TreeSet<Int>()
        priorities.apply {
            add(0)
            add(PRIORITY_LOW)
            add(PRIORITY_MEDIUM)
            add(PRIORITY_HIGH)
            add(PRIORITY_URGENT)
            add(curPriority)
        }

        val items = ArrayList<RadioItem>(priorities.size + 1)
        priorities.mapIndexedTo(items, { index, value ->
            RadioItem(index, getPriorityText(value), value)
        })

        var selectedIndex = 0
        priorities.forEachIndexed { index, value ->
            if (value == curPriority)
                selectedIndex = index
        }

        //items.add(RadioItem(-1, getString(R.string.custom)))

        RadioGroupDialog(this, items, selectedIndex) {
                callback(it as Int)
        }
    }

    protected fun showTaskStatusDialog(curStatus: Int, callback: (minutes: Int) -> Unit) {
        hideKeyboard()
        val statuses = TreeSet<Int>()
        statuses.apply {
            add(STATUS_PENDING)
            add(STATUS_ON_PROGRESS)
            add(STATUS_TESTING)
            add(STATUS_COMPLETED)
            add(curStatus)
        }

        val items = ArrayList<RadioItem>(statuses.size + 1)
        statuses.mapIndexedTo(items, { index, value ->
            RadioItem(index, getStatusText(value), value)
        })

        var selectedIndex = 0
        statuses.forEachIndexed { index, value ->
            if (value == curStatus)
                selectedIndex = index
        }

        RadioGroupDialog(this, items, selectedIndex) {
            callback(it as Int)
        }
    }
    
    fun getPriorityText(priority: Int) = when (priority) {
        0 -> getString(R.string.no_priority)
        PRIORITY_LOW -> getString(R.string.low_priority)
        PRIORITY_MEDIUM -> getString(R.string.medium_priority)
        PRIORITY_HIGH -> getString(R.string.high_priority)
        PRIORITY_URGENT -> getString(R.string.urgent_priority)
        else -> getString(R.string.none)
    }

    fun getStatusText(status: Int) = when (status) {
        STATUS_PENDING -> getString(R.string.pending_status)
        STATUS_ON_PROGRESS -> getString(R.string.on_progress_status)
        STATUS_TESTING -> getString(R.string.testing_status)
        STATUS_COMPLETED -> getString(R.string.completed_status)
        else -> getString(R.string.none)
    }
}
