package com.sena.module_one.lock_pattern

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.sena.module_one.utils.contains
import com.sena.module_one.utils.dpToPx


/**
 * FileName: LockPatternView
 * Author: JiaoCan
 * Date: 2022/11/14 15:15
 *
 * https://juejin.cn/post/7143137578080796686#heading-6
 *
 * 执行顺序
 * setAdapter->onMeasure->onLayout->onDraw
 */

class LockPatternView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
): View(context, attrs, defStyle) {

    private var adapter: LockPatternBaseAdapter? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeJoin = Paint.Join.BEVEL
    }

    private val bigRadius by lazy { width / (NUMBER * 2) * 0.7f }
    private val smallRadius by lazy { bigRadius * 0.2f }
    private val strokeWidth = context.dpToPx(4).toFloat()

    private var isDown = false
    private val lockPaints = arrayListOf<ArrayList<LockPatternBean>>()
    private var passwordList = arrayListOf(1, 2, 5, 8)  // 正确路径
    private val recordList = arrayListOf<LockPatternBean>()  // 输入路径
    private val path = Path()  // 连接线
    private val line = Pair(PointF(), PointF()) // 引导线

    private var mCurType = Type.ORIGIN

    companion object {
        private var NUMBER = 3
        private var ORIGIN_COLOR = Color.parseColor("#D8D9D8")
        private var DOWN_COLOR = Color.parseColor("#3AD94E")
        private var UP_COLOR = Color.parseColor("#57D900")
        private var ERROR_COLOR = Color.parseColor("#D9251E")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val diameter = width / NUMBER
        val ratio = diameter / 2f
        var index = 1

        for (i in 0 until NUMBER) {
            val list = arrayListOf<LockPatternBean>()
            for (j in 0 until NUMBER) {
                // 计算圆心坐标
                list.add(LockPatternBean(diameter * j + ratio, diameter * i  + ratio, index++))
            }
            lockPaints.add(list)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 宽高一致
        val width = resolveSize(measuredWidth, widthMeasureSpec)
        val height = resolveSize(width, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        Log.d("TAG", "onLayout: ")
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val pointF = isContains(event.x, event.y)
                pointF?.let {
                    isDown = true
                    mCurType = Type.DOWN

                    it.type = Type.DOWN

                    // 记录路径
                    recordList.add(it)
                    path.moveTo(it.x, it.y)
                    // 记录引导线起点
                    line.first.x = it.x
                    line.first.y = it.y
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isDown) {
                    return super.onTouchEvent(event)
                }
                val pointF = isContains(event.x, event.y)
                pointF?.let {
                    it.type = Type.DOWN
                    if (!recordList.contains(it)) {
                        recordList.add(it)
                        path.lineTo(it.x, it.y)
                        // 重设引导线起点
                        line.first.x = it.x
                        line.first.y = it.y
                    }
                }
                // 记录引导线终点
                line.second.x = event.x
                line.second.y = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDown = false

                line.first.x = 0f
                line.first.y = 0f
                line.second.x = 0f
                line.second.y = 0f

                val isSuccess = if (recordList.size == passwordList.size) {
                    val list = recordList.zip(passwordList).filter {
                        it.first.index == it.second
                    }.toList()
                    list.size == recordList.size
                } else {
                    false
                }

                if (!isSuccess) {
                    recordList.forEach {
                        it.type = Type.ERROR
                    }
                    mCurType = Type.ERROR
                    Toast.makeText(context, "输入失败!!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "输入成功!!", Toast.LENGTH_SHORT).show()
                }

                postDelayed({
                    clear()
                }, 1000L)
            }
        }
        invalidate()
        return true
    }

    private fun clear() {
        path.reset()
        mCurType = Type.ORIGIN

        recordList.forEach {
            it.type = Type.ORIGIN
        }
        recordList.clear()
        isDown = false
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // 绘制圆
        paint.style = Paint.Style.FILL
        lockPaints.forEach {
            it.forEach { point ->
                paint.color = getTypeColor(point.type)
                paint.alpha = point.bigCircleAlpha
                canvas?.drawCircle(point.x, point.y, bigRadius, paint)
                paint.alpha = point.smallCircleAlpha
                canvas?.drawCircle(point.x, point.y, smallRadius, paint)
            }
        }
        // 绘制连接线
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeWidth
        paint.color = getTypeColor(mCurType)
        canvas?.drawPath(path, paint)
        // 绘制引导线
        if (line.first.x != 0f && line.second.x != 0f) {
            canvas?.drawLine(line.first.x, line.first.y, line.second.x, line.second.y, paint)
        }
    }

    private fun isContains(x: Float, y: Float) = let {
        lockPaints.forEach {
            it.forEach { point ->
                if (PointF(x, y).contains(PointF(point.x, point.y), bigRadius)) {
                    return@let point
                }
            }
        }
        return@let null
    }

    private fun getTypeColor(type: Type): Int {
        return when (type) {
            Type.ORIGIN -> ORIGIN_COLOR
            Type.DOWN -> DOWN_COLOR
            Type.UP -> UP_COLOR
            Type.ERROR -> ERROR_COLOR
        }
    }

    fun setAdapter(adapter: LockPatternBaseAdapter) {
        adapter.also {
            this.adapter = it
            NUMBER = it.getNumber()
            ORIGIN_COLOR = it.getOriginColor()
            DOWN_COLOR = it.getDownColor()
            UP_COLOR = it.getUpColor()
            ERROR_COLOR = it.getErrorColor()
        }

    }

}


