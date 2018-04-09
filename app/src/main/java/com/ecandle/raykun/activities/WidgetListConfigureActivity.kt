package com.ecandle.raykun.activities

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import com.ecandle.raykun.activities.SimpleActivity
import com.ecandle.raykun.R
import com.ecandle.raykun.adapters.EventListAdapter
import com.ecandle.raykun.extensions.config
import com.ecandle.raykun.extensions.seconds
import com.ecandle.raykun.helpers.Formatter
import com.ecandle.raykun.helpers.MyWidgetListProvider
import com.ecandle.raykun.models.ListEvent
import com.ecandle.raykun.models.ListItem
import com.ecandle.raykun.models.ListSection
import com.simplemobiletools.commons.dialogs.ColorPickerDialog
import com.simplemobiletools.commons.extensions.adjustAlpha
import kotlinx.android.synthetic.main.widget_config_list.*
import org.joda.time.DateTime
import java.util.*

class WidgetListConfigureActivity : SimpleActivity() {
    lateinit var mRes: Resources
    private var mPackageName = ""

    private var mBgAlpha = 0f
    private var mWidgetId = 0
    private var mBgColorWithoutTransparency = 0
    private var mBgColor = 0
    private var mTextColorWithoutTransparency = 0
    private var mTextColor = 0

    private var mEventsAdapter: EventListAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.widget_config_list)
        mPackageName = packageName
        initVariables()

        val extras = intent.extras
        if (extras != null)
            mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
            finish()

        mEventsAdapter = EventListAdapter(this, getListItems(), false, null, config_events_list) {}
        mEventsAdapter!!.updateTextColor(mTextColor)
        config_events_list.adapter = mEventsAdapter

        config_save.setOnClickListener { saveConfig() }
        config_bg_color.setOnClickListener { pickBackgroundColor() }
        config_text_color.setOnClickListener { pickTextColor() }

        val primaryColor = config.primaryColor
        config_bg_seekbar.setColors(mTextColor, primaryColor, primaryColor)
    }

    override fun onResume() {
        super.onResume()
        window.decorView.setBackgroundColor(0)
    }

    private fun initVariables() {
        mRes = resources

        mTextColorWithoutTransparency = config.widgetTextColor
        updateColors()

        mBgColor = config.widgetBgColor
        if (mBgColor == 1) {
            mBgColor = Color.BLACK
            mBgAlpha = .2f
        } else {
            mBgAlpha = Color.alpha(mBgColor) / 255.toFloat()
        }

        mBgColorWithoutTransparency = Color.rgb(Color.red(mBgColor), Color.green(mBgColor), Color.blue(mBgColor))
        config_bg_seekbar.setOnSeekBarChangeListener(bgSeekbarChangeListener)
        config_bg_seekbar.progress = (mBgAlpha * 100).toInt()
        updateBgColor()
    }

    private fun saveConfig() {
        storeWidgetColors()
        requestWidgetUpdate()

        Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun storeWidgetColors() {
        config.apply {
            widgetBgColor = mBgColor
            widgetTextColor = mTextColorWithoutTransparency
        }
    }

    private fun pickBackgroundColor() {
        ColorPickerDialog(this, mBgColorWithoutTransparency) {
            mBgColorWithoutTransparency = it
            updateBgColor()
        }
    }

    private fun pickTextColor() {
        ColorPickerDialog(this, mTextColor) {
            mTextColorWithoutTransparency = it
            updateColors()
        }
    }

    private fun requestWidgetUpdate() {
        Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, MyWidgetListProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mWidgetId))
            sendBroadcast(this)
        }
    }

    private fun updateColors() {
        mTextColor = mTextColorWithoutTransparency
        mEventsAdapter?.updateTextColor(mTextColor)
        config_text_color.setBackgroundColor(mTextColor)
        config_save.setTextColor(mTextColor)
    }

    private fun updateBgColor() {
        mBgColor = mBgColorWithoutTransparency.adjustAlpha(mBgAlpha)
        config_events_list.setBackgroundColor(mBgColor)
        config_bg_color.setBackgroundColor(mBgColor)
        config_save.setBackgroundColor(mBgColor)
    }

    private fun getListItems(): ArrayList<ListItem> {
        val listItems = ArrayList<ListItem>(10)
        var dateTime = DateTime.now().withTime(0, 0, 0, 0).plusDays(1)
        var code = Formatter.getDayCodeFromTS(dateTime.seconds())
        var day = Formatter.getDayTitle(this, code)
        listItems.add(ListSection(day))

        var time = dateTime.withHourOfDay(7)
        listItems.add(ListEvent(1, time.seconds(), time.plusMinutes(30).seconds(), getString(R.string.sample_title_1), getString(R.string.sample_description_1), false, config.primaryColor))
        time = dateTime.withHourOfDay(8)
        listItems.add(ListEvent(2, time.seconds(), time.plusHours(1).seconds(), getString(R.string.sample_title_2), getString(R.string.sample_description_2), false, config.primaryColor))

        dateTime = dateTime.plusDays(1)
        code = Formatter.getDayCodeFromTS(dateTime.seconds())
        day = Formatter.getDayTitle(this, code)
        listItems.add(ListSection(day))

        time = dateTime.withHourOfDay(8)
        listItems.add(ListEvent(3, time.seconds(), time.plusHours(1).seconds(), getString(R.string.sample_title_3), "", false, config.primaryColor))
        time = dateTime.withHourOfDay(13)
        listItems.add(ListEvent(4, time.seconds(), time.plusHours(1).seconds(), getString(R.string.sample_title_4), getString(R.string.sample_description_4), false, config.primaryColor))
        time = dateTime.withHourOfDay(18)
        listItems.add(ListEvent(5, time.seconds(), time.plusMinutes(10).seconds(), getString(R.string.sample_title_5), "", false, config.primaryColor))

        return listItems
    }

    private val bgSeekbarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            mBgAlpha = progress.toFloat() / 100.toFloat()
            updateBgColor()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {

        }
    }
}
