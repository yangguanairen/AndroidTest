package com.sena.module_two

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


/**
 * FileName: ModuleTwoAdapter
 * Author: JiaoCan
 * Date: 2022/11/16 14:50
 */

class ModuleTwoAdapter(
    private val context: Context,
    private val dataList: List<Map.Entry<String, Class<out AppCompatActivity>>>
): RecyclerView.Adapter<ModuleTwoAdapter.ViewHolder>() {

    private val dp_10 by lazy {
        val scale = context.resources.displayMetrics.scaledDensity
        (scale * 10 + 0.5f).toInt()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val linearLayout = LinearLayout(context)
        linearLayout.setPadding(dp_10, dp_10, dp_10, dp_10)
        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val textView = TextView(context)
        textView.textSize = 18f
        textView.setTextColor(Color.BLACK)
        textView.setBackgroundColor(Color.parseColor(colors.random()))
        textView.setPadding(dp_10, dp_10, dp_10, dp_10)
        textView.gravity = Gravity.CENTER
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        linearLayout.addView(textView)
        return ViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.textView.text = data.key
        holder.textView.setOnClickListener {
            val intent = Intent(context, data.value)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val textView: TextView

        init {
            val linearLayout = itemView as LinearLayout
            textView = linearLayout.getChildAt(0) as TextView
        }

    }

    private companion object {
        val colors = arrayListOf(
            "#ffef9a9a",
            "#FFF48FB1",
            "#FF9C27B0",
            "#FF7E57C2",
            "#FF3F51B5",
            "#FF42A5F5",
            "#FF4FC3F7",
            "#FF26C6DA",
            "#FF26A69A",
            "#FF4CAF50",
            "#FF8BC34A",
            "#FFCDDC39",
            "#FFFF9CAA",
            "#FF78909C"
        )
    }

}

