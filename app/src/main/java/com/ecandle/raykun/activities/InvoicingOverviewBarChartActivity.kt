package com.ecandle.raykun.activities

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.ecandle.raykun.R
import com.ecandle.raykun.custom.LabelXAxisFormatter
import com.ecandle.raykun.custom.PercentAxisValueFormatter
import com.ecandle.raykun.fragments.DemoBase
import com.ecandle.raykun.helpers.ConnectionDetector
import com.ecandle.raykun.helpers.M
import com.ecandle.raykun.helpers.USER_ID
import com.ecandle.raykun.tasks.pullServerDataTask
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.simplemobiletools.commons.extensions.toast
import org.json.JSONObject
import java.util.*

class InvoicingOverviewBarChartActivity : DemoBase() {

    private var mChart: BarChart? = null

    private var tvX: TextView? = null
    private var tvY: TextView? = null
    protected var mStatuses = arrayOf("Draft", "Not Sent", "Unpaid", "Uncomplete", "Overdue", "paid")
    val INVOICE_STATUS_COLORS = intArrayOf(Color.rgb(114, 123, 144),
            Color.rgb(114, 123, 144),
            Color.rgb(252, 45, 66),
            Color.rgb(255, 111, 0),
            Color.rgb(255, 111, 0),
            Color.rgb(0, 191, 54))
    private var mUserId: String? = null
    var connectionDetector = ConnectionDetector(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_horizontalbarchart)

        mUserId = intent.getStringExtra(USER_ID)

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
        loadRemoteData(6, 50f,mUserId!!)
        // number of bars,
        //setData(12, 50f)

        mChart!!.setFitBars(true)
        mChart!!.animateY(2500)



    }

    fun loadRemoteData(count: Int, range: Float,userid:String) {
        var percent_draft =""
        var percent_not_sent =""
        var percent_unpaid =""
        var percent_not_paid_completely =""
        var percent_overdue =""
        var percent_paid = ""
        var invoices_color_draft = ""
        var invoices_color_not_sent = ""
        var invoices_color_unpaid = ""
        var invoices_color_not_paid_completely = ""
        var invoices_color_overdue = ""
        var invoices_color_paid = ""

        connectionDetector = ConnectionDetector(this)
        // Load Anaytics Data
        if (connectionDetector!!.isConnectingToInternet) {
            // JT: Loading Progress Bar
            var dialog = M.setProgressDialog(this)
            dialog.show()
            Handler().postDelayed({dialog.dismiss()},3000)
            //JT
            val url = "http://ecandlemobile.com/RayKun/webservice/index.php/admin/home/showStatistics?id=$userid"

            val loadLoginData = pullServerDataTask()

            val loginData = loadLoginData.execute(url).get()
            //JT Check not null data
            if (loginData == null){
                toast(getString(R.string.no_analytics_data), Toast.LENGTH_LONG)
                finish()
            } else {
                Log.d("loginData:",loginData.toString())
            }
            //JT
            var json = JSONObject(loginData)

            var invoice_overview_stat = json.getJSONObject("invoice_overview_stat")

            var title = invoice_overview_stat.getString("labels")
            var total_invoices = invoice_overview_stat.getString("total_invoices")
            var invoices_percent_draft_obj = invoice_overview_stat.getJSONObject("invoices_percent_draft")
            var total_draft = invoices_percent_draft_obj.getInt("total_by_status")
            percent_draft = invoices_percent_draft_obj.getString("percent")
            var total = invoices_percent_draft_obj.getInt("total")

            var invoices_percent_not_sent_obj = invoice_overview_stat.getJSONObject("invoices_percent_not_sent")
            var total_not_sent = invoices_percent_not_sent_obj.getInt("total_by_status")
            percent_not_sent = invoices_percent_not_sent_obj.getString("percent")

            var invoices_percent_unpaid_obj = invoice_overview_stat.getJSONObject("invoices_percent_unpaid")
            var total_unpaid = invoices_percent_unpaid_obj.getInt("total_by_status")
            percent_unpaid = invoices_percent_unpaid_obj.getString("percent")

            var invoices_percent_not_paid_completely_obj = invoice_overview_stat.getJSONObject("invoices_percent_not_paid_completely")
            var total_not_paid_completely = invoices_percent_not_paid_completely_obj.getInt("total_by_status")
            percent_not_paid_completely = invoices_percent_not_paid_completely_obj.getString("percent")

            var invoices_percent_overdue_obj = invoice_overview_stat.getJSONObject("invoices_percent_overdue")
            var total_overdue = invoices_percent_overdue_obj.getInt("total_by_status")
            percent_overdue = invoices_percent_overdue_obj.getString("percent")

            var invoices_percent_paid_obj = invoice_overview_stat.getJSONObject("invoices_percent_paid")
            var total_paid = invoices_percent_paid_obj.getInt("total_by_status")
            percent_paid = invoices_percent_paid_obj.getString("percent")

//            "invoices_color_draft": "114, 123, 144",
//            "invoices_color_not_sent": "114, 123, 144",
//            "invoices_color_unpaid": "252, 45, 66",
//            "invoices_color_not_paid_completely": "255, 111, 0",
//            "invoices_color_overdue": "255, 111, 0",
//            "invoices_color_paid": "0, 191, 54"
            invoices_color_draft = invoice_overview_stat.getString("invoices_color_draft")
            invoices_color_not_sent = invoice_overview_stat.getString("invoices_color_not_sent")
            invoices_color_unpaid = invoice_overview_stat.getString("invoices_color_unpaid")
            invoices_color_not_paid_completely = invoice_overview_stat.getString("invoices_color_not_paid_completely")
            invoices_color_overdue = invoice_overview_stat.getString("invoices_color_overdue")
            invoices_color_paid = invoice_overview_stat.getString("invoices_color_paid")

            Log.d(Chart.LOG_TAG, "leads_stats : $invoices_color_paid")
//        var mLabels = arrayOf("Pending Invoices($total_invoices_awaiting_payment/$total_invoices)",
//                "Converted Leads($total_leads_converted/$total_leads)",
//                "Active Projects($total_projects/$total_projects_in_progress)",
//                "Pending Tasks($total_not_finished_tasks/$total_tasks)")

            var mLabels = arrayOf("Draft", "Not Sent", "Unpaid", "Uncomplete", "Overdue", "Paid")
            val xAxisFormatter = LabelXAxisFormatter(mLabels)

            val xAxis = mChart!!.xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.typeface = mTfLight
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 10f
            xAxis.setLabelCount(7)
            xAxis.setValueFormatter(xAxisFormatter)

            val barWidth = 4f
            val spaceForBar = 10f
            val yVals1 = ArrayList<BarEntry>()

            val myval1:Float = percent_draft.toFloat()
            val myval2:Float = percent_not_sent.toFloat()
            val myval3:Float = percent_unpaid.toFloat()
            val myval4:Float = percent_not_paid_completely.toFloat()
            val myval5:Float = percent_overdue.toFloat()
            val myval6:Float = percent_paid.toFloat()

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
                set1 = BarDataSet(yVals1, "Invoicing Overview")

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

        } else {
            //TODO Load data without connection
            finish()
            toast(getString(R.string.no_internet_connection), Toast.LENGTH_LONG)

        }


    }

}
