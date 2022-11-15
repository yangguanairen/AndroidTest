package com.sena.wk_a

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.NinePatch
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.sena.wk_a.R


/**
 * FileName: TestAdapter
 * Author: JiaoCan
 * Date: 2022/9/26 17:23
 */

class TestAdapter(
    private val context: Context,
    private val dataList: List<String> = arrayListOf()
) : RecyclerView.Adapter<TestAdapter.TestViewHolder>() {

    private val leftDrawable = Drawable.createFromStream(context.assets.open("left_bubble.png"), null)
    private val rightDrawable = Drawable.createFromStream(context.assets.open("right_bubble.png"), null)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val view = if (viewType % 2 == 0) {
            LayoutInflater.from(context).inflate(R.layout.item_message_left, parent, false)
        } else {
            LayoutInflater.from(context).inflate(R.layout.item_message_right, parent, false)
        }
        return TestViewHolder(view)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.textView.text = dataList[position]
//        holder.textView.background = if (position % 2 == 0) leftDrawable else rightDrawable


        val fileName = if (position % 2 == 0) {
            "left_bubble.png"
        } else {
            "right_bubble.png"
        }
        try {
            val bitmap = BitmapFactory.decodeStream(context.assets.open(fileName))
            val chunk = bitmap.ninePatchChunk
            if (NinePatch.isNinePatchChunk(chunk)) {
                val patchy =
                    NinePatchDrawable(holder.textView.resources, bitmap, chunk, Rect(), null)
                holder.textView.background = patchy
            } else {
                holder.textView.background =
                    Drawable.createFromStream(context.assets.open(fileName), null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // 加载默认气泡
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textView: TextView

        init {
            textView = itemView.findViewById(R.id.message)
        }

    }

}



