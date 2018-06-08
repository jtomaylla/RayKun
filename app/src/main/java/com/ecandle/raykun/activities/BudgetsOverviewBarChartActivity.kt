package com.ecandle.raykun.activities

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.custom.LabelXAxisFormatter
import com.ecandle.raykun.custom.PercentAxisValueFormatter
import com.ecandle.raykun.fragments.DemoBase
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.simplemobiletools.commons.extensions.toast
import org.json.JSONObject
import java.util.*

class BudgetsOverviewBarChartActivity : DemoBase() {

    private var mChart: BarChart? = null

    private var tvX: TextView? = null
    private var tvY: TextView? = null
    val INVOICE_STATUS_COLORS = intArrayOf(Color.rgb(119, 119, 119),
            Color.rgb(255, 111, 0),
            Color.rgb(3, 169, 244),
            Color.rgb(252, 45, 66),
            Color.rgb(0, 191, 54),
            Color.rgb(255, 111, 0))
    var jsonShowStatistics = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_horizontalbarchart)

        jsonShowStatistics = intent.getStringExtra("json_show_statistics")

        tvX = findViewById<View>(R.id.tvXMax) as TextView?
        tvY = findViewById<View>(R.id.tvYMax) as TextView?

        mChart = findViewById<View>(R.id.chart1) as BarChart
        //mChart!!.setOnChartValueSelectedListener(this)
        // mChart.setHighlightEnabled(false);

        mChart!!.setDrawBarShadow(false)

        mChart!!.setDrawValueAboveBar(true)

        mChart!!.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart!!.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        mChart!!.setPinchZoom(false)

        // draw shadows for each bar that show the maximum value
        // mChart!!.setDrawBarShadow(true);

        mChart!!.setDrawGridBackground(false)

        val custom = PercentAxisValueFormatter()

        val leftAxis = mChart!!.getAxisLeft()
        leftAxis.typeface = mTfLight
        leftAxis.setLabelCount(8, false)
        leftAxis.valueFormatter = custom
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val rightAxis = mChart!!.getAxisRight()
        rightAxis.setDrawGridLines(false)
        rightAxis.typeface = mTfLight
        rightAxis.setLabelCount(8, false)
        rightAxis.valueFormatter = custom
        rightAxis.spaceTop = 15f
        rightAxis.axisMinimum = 0f // this replaces setStartAtZero(true)

        val l = mChart!!.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.form = Legend.LegendForm.SQUARE
        l.formSize = 9f
        l.textSize = 11f
        l.xEntrySpace = 4f


//        val mv = XYMarkerView(this, xAxisFormatter)
//        mv.chartView = mChart!! // For bounds control
//        mChart!!.setMarker(mv) // Set the marker to the chart

        // setting data
        loadRemoteData(6, 50f)
        // number of bars,
        //setData(12, 50f)

        mChart!!.setFitBars(true)
        mChart!!.animateY(2500)

    }

    fun loadRemoteData(count: Int, range: Float) {
        var percent_draft =""
        var percent_not_sent =""
        var percent_sent =""
        var percent_declined =""
        var percent_accepted =""
        var percent_expired = ""

        // Load Anaytics Data
        val loginData = jsonShowStatistics
        if (loginData.isEmpty()){
            toast(getString(R.string.no_analytics_data), Toast.LENGTH_LONG)
            finish()
        } else {


            var json = JSONObject(loginData)

            var estimate_overview_stat = json.getJSONObject("estimate_overview_stat")

            var estimate_percent_draft_obj = estimate_overview_stat.getJSONObject("estimate_percent_draft")
            percent_draft = estimate_percent_draft_obj.getString("percent")

            var estimate_percent_not_sent_obj = estimate_overview_stat.getJSONObject("estimate_percent_not_sent")
            percent_not_sent = estimate_percent_not_sent_obj.getString("percent")

            var estimate_percent_sent_obj = estimate_overview_stat.getJSONObject("estimate_percent_sent")
            percent_sent = estimate_percent_sent_obj.getString("percent")

            var estimate_percent_declined_obj = estimate_overview_stat.getJSONObject("estimate_percent_declined")
            percent_declined = estimate_percent_declined_obj.getString("percent")

            var estimate_percent_accepted_obj = estimate_overview_stat.getJSONObject("estimate_percent_accepted")
            percent_accepted = estimate_percent_accepted_obj.getString("percent")

            var estimate_percent_expired_obj = estimate_overview_stat.getJSONObject("estimate_percent_expired")
            percent_expired = estimate_percent_expired_obj.getString("percent")


            var mLabels = arrayOf(resources.getString(R.string.draft),
                    resources.getString(R.string.not_sent),
                    resources.getString(R.string.not_sent),
                    resources.getString(R.string.declined),
                    resources.getString(R.string.accepted),
                    resources.getString(R.string.expired))
            val xAxisFormatter = LabelXAxisFormatter(mLabels)

            val xAxis = mChart!!.xAxis
            xAxis.position = XAxisPosition.BOTTOM
            xAxis.typeface = mTfLight
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 10f
            xAxis.setLabelCount(7)
            xAxis.setValueFormatter(xAxisFormatter)

            val barWidth = 4f
            val spaceForBar = 10f
            val yVals1 = ArrayList<BarEntry>()

            val myval1: Float = percent_draft.toFloat()
            val myval2: Float = percent_not_sent.toFloat()
            val myval3: Float = percent_sent.toFloat()
            val myval4: Float = percent_declined.toFloat()
            val myval5: Float = percent_accepted.toFloat()
            val myval6: Float = percent_expired.toFloat()

            yVals1.add(BarEntry(1 * spaceForBar, myval1,
                    resources.getDrawable(R.drawable.star)))
            yVals1.add(BarEntry(2 * spaceForBar, myval2,
                    resources.getDrawable(R.drawable.star)))
            yVals1.add(BarEntry(3 * spaceForBar, myval3,
                    resources.getDrawable(R.drawable.star)))
            yVals1.add(BarEntry(4 * spaceForBar, myval4,
                    resources.getDrawable(R.drawable.star)))
            yVals1.add(BarEntry(5 * spaceForBar, myval5,
                    resources.getDrawable(R.drawable.star)))
            yVals1.add(BarEntry(6 * spaceForBar, myval6,
                    resources.getDrawable(R.drawable.star)))


            val set1: BarDataSet

            if (mChart!!.data != null && mChart!!.data.dataSetCount > 0) {
                set1 = mChart!!.data.getDataSetByIndex(0) as BarDataSet
                set1.values = yVals1
                mChart!!.data.notifyDataChanged()
                mChart!!.notifyDataSetChanged()
            } else {
                set1 = BarDataSet(yVals1, getString(R.string.annual_budgets_overview))

                set1.setDrawIcons(false)
                set1.setColors(*INVOICE_STATUS_COLORS)

                val dataSets = ArrayList<IBarDataSet>()
                dataSets.add(set1)

                val data = BarData(dataSets)
                data.setValueTextSize(10f)
                data.setValueTypeface(mTfLight)
                data.barWidth = barWidth
                mChart!!.data = data


            }
        }

    }

}
