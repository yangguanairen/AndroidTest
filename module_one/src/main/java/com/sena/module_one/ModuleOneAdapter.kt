package com.sena.module_one

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
 * FileName: ModuleOneAdapter
 * Author: JiaoCan
 * Date: 2022/11/14 15:36
 */

class ModuleOneAdapter(
    private val context: Context,
    private val dataList: List<Map.Entry<String, Class<out AppCompatActivity>>>
) : RecyclerView.Adapter<ModuleOneAdapter.ViewHolder>() {

    private val padding by lazy { dpToPx(10) }
    private val textSize by lazy { 18f }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val linearLayout = LinearLayout(context)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(padding, padding, padding, padding)
        linearLayout.layoutParams = layoutParams

        val textView = TextView(context)
        textView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        textView.setPadding(padding, padding, padding, padding)
        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.BLACK)
        textView.textSize = textSize
        textView.setBackgroundColor(Color.parseColor(colors.random()))

        linearLayout.addView(textView)
        return ViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.textView.text = data.key
        holder.textView.setOnClickListener {
            val intent = Intent(context, data.value)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView

        init {
            val childView = (itemView as LinearLayout).getChildAt(0)
            textView = childView as TextView
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

    private fun dpToPx(dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}

