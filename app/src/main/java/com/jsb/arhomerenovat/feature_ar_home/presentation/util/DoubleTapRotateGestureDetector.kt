package com.jsb.arhomerenovat.feature_ar_home.presentation.util

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2

class DoubleTapRotateGestureDetector(
    context: Context,
    private val listener: OnRotateListener
) {

    interface OnRotateListener {
        fun onRotate(rotationDegrees: Float): Boolean
        fun onDoubleTap() // For enabling rotation mode
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            listener.onDoubleTap()
            return true
        }
    })

    private var rotationEnabled = false
    private var initialAngle = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)

        if (rotationEnabled) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    initialAngle = calculateAngle(event)
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentAngle = calculateAngle(event)
                    val rotationDegrees = currentAngle - initialAngle
                    listener.onRotate(rotationDegrees)
                    initialAngle = currentAngle
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    rotationEnabled = false // Disable rotation after releasing the touch
                }
            }
        }
        return true
    }

    fun enableRotation() {
        rotationEnabled = true
    }

    private fun calculateAngle(event: MotionEvent): Float {
        return if (event.pointerCount == 2) {
            val deltaX = event.getX(1) - event.getX(0)
            val deltaY = event.getY(1) - event.getY(0)
            Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
        } else {
            event.x
        }
    }
}
