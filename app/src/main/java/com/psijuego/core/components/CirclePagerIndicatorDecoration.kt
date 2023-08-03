package com.psijuego.core.components

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.psijuego.R
import kotlin.math.max

class CirclePagerIndicatorDecoration(context: Context) : ItemDecoration() {
    private val colorActive: Int = ContextCompat.getColor(context, R.color.active_indicator_color)
    private val colorInactive: Int =
        ContextCompat.getColor(context, R.color.inactive_indicator_color)
    private val density: Float = Resources.getSystem().displayMetrics.density

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private val indicatorHeight: Int = (density * 16).toInt()

    /**
     * Indicator stroke width.
     */
    private val indicatorStrokeWidth: Float = density * 4

    /**
     * Indicator width.
     */
    private val indicatorItemLength: Float = density * 4

    /**
     * Padding between indicators.
     */
    private val indicatorItemPadding: Float = density * 8

    /**
     * Some more natural animation interpolation
     */
    private val interpolator = AccelerateDecelerateInterpolator()

    private val paint: Paint = Paint()

    init {
        paint.apply {
            strokeWidth = indicatorStrokeWidth
            style = Paint.Style.STROKE
            isAntiAlias = true
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val itemCount: Int = parent.adapter?.itemCount ?: return

        // Center horizontally, calculate width and subtract half from center
        val totalLength: Float = indicatorItemLength * itemCount
        val paddingBetweenItems: Float = max(0, itemCount - 1) * indicatorItemPadding
        val indicatorTotalWidth: Float = totalLength + paddingBetweenItems
        val indicatorStartX: Float = (parent.width - indicatorTotalWidth) / 2F

        // Center vertically in the allotted space
        val indicatorPosY: Float = parent.height - indicatorHeight / 2F

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)

        // Find active page (which should be highlighted)
        val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return
        val activePosition: Int = layoutManager.findFirstVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION) {
            return
        }

        // Find offset of active page (if the user is scrolling)
        val activeChild: View? = layoutManager.findViewByPosition(activePosition)
        val left: Int = activeChild?.left ?: 0
        val width: Int = activeChild?.width ?: 0

        // On swipe, the active item will be positioned from [-width, 0]
        // Interpolate offset for smooth animation
        val progress: Float = interpolator.getInterpolation(left.toFloat() * -1 / width.toFloat())

        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress)
    }

    private fun drawInactiveIndicators(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        paint.color = colorInactive

        // Width of item indicator including padding
        val itemWidth: Float = indicatorItemLength + indicatorItemPadding

        var start = indicatorStartX
        for (i in 0 until itemCount) {
            c.drawCircle(start, indicatorPosY, indicatorItemLength / 2F, paint)
            start += itemWidth
        }
    }

    private fun drawHighlights(
        c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
        highlightPosition: Int, progress: Float
    ) {
        paint.color = colorActive

        // Width of item indicator including padding
        val itemWidth: Float = indicatorItemLength + indicatorItemPadding

        if (progress == 0F) {
            // No swipe, draw a normal indicator
            val highlightStart: Float = indicatorStartX + itemWidth * highlightPosition
            c.drawCircle(highlightStart, indicatorPosY, indicatorItemLength / 2F, paint)
        } else {
            val highlightStart: Float = indicatorStartX + itemWidth * highlightPosition
            // Calculate partial highlight
            val partialLength: Float =
                indicatorItemLength * progress + indicatorItemPadding * progress
            c.drawCircle(
                highlightStart + partialLength,
                indicatorPosY,
                indicatorItemLength / 2F,
                paint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = indicatorHeight
    }
}

