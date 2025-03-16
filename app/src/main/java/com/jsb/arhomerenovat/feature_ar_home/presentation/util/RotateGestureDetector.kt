package com.jsb.arhomerenovat.feature_ar_home.presentation.util

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import kotlin.math.atan2

class RotateGestureDetector(
    context: Context,
    private val listener: OnRotateListener
) {
    interface OnRotateListener {
        fun onRotate(rotationDegrees: Float): Boolean
    }

    private var initialAngle = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (event.pointerCount == 2) {
                    initialAngle = calculateAngle(event)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2) {
                    val currentAngle = calculateAngle(event)
                    val rotationDegrees = currentAngle - initialAngle
                    listener.onRotate(rotationDegrees)
                    initialAngle = currentAngle
                }
            }
        }
        return true
    }

    private fun calculateAngle(event: MotionEvent): Float {
        val deltaX = event.getX(1) - event.getX(0)
        val deltaY = event.getY(1) - event.getY(0)
        return Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
    }
}
