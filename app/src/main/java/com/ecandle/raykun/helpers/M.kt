package com.ecandle.raykun.helpers

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.ecandle.raykun.R


class M {

    companion object {
        fun progressDialog(context: Context): Dialog {

            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT))

            return dialog
        }
        fun setProgressDialog(context: Context): AlertDialog {

            val llPadding = 30
            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.HORIZONTAL
            ll.setPadding(llPadding, llPadding, llPadding, llPadding)
            ll.gravity = Gravity.CENTER
            var llParam = LinearLayout.LayoutParams(110, 110)
            llParam.gravity = Gravity.CENTER

            val progressBar = ProgressBar(context)
            progressBar.isIndeterminate = true
            progressBar.setPadding(0, 0, llPadding, 0)
            progressBar.layoutParams = llParam

            llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            llParam.gravity = Gravity.CENTER
            val tvText = TextView(context)
            tvText.text = context.getString(R.string.loading_text)
            //tvText.setTextColor(Color.parseColor("#000000"))
            tvText.setTextColor(Color.WHITE)
            tvText.textSize = 20f
            tvText.layoutParams = llParam

            ll.addView(progressBar)
            ll.addView(tvText)

            val builder = AlertDialog.Builder(context)
            builder.setCancelable(false)
            builder.setView(ll)

            val dialog = builder.create()
            dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            val window = dialog.getWindow()
            if (window != null) {
                val layoutParams = WindowManager.LayoutParams()
                layoutParams.copyFrom(dialog.getWindow().getAttributes())
                layoutParams.width = 756
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                dialog.getWindow().setAttributes(layoutParams)
            }

            return dialog
        }

    }
}

