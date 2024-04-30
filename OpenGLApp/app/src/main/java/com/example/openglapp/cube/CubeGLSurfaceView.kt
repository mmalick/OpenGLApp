package com.example.openglapp.cube

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector


class CubeGLSurfaceView(context: Context) : GLSurfaceView(context) {


    private val renderer: CubeRenderer
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(2)

        renderer = CubeRenderer()
        setRenderer(renderer)
        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        initializeScaleGestureDetector()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x: Float = event.x
        val y: Float = event.y
        Log.d("GLSurfaceView", "touch event: x=$x, y=$y, action=${event.action}")

        scaleGestureDetector.onTouchEvent(event)

        if (!scaleGestureDetector.isInProgress) {

            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    Log.d("GLSurfaceView", "action")
                    var dx: Float = x - previousX
                    var dy: Float = y - previousY

                    renderer.angleY += dx * TOUCH_SCALE_FACTOR
                    renderer.angleX -= dy * TOUCH_SCALE_FACTOR
                    requestRender()
                }
            }
        }

        previousX = x
        previousY = y
        return true
    }

    companion object {
        private const val TOUCH_SCALE_FACTOR: Float = 0.2f
    }

    private fun initializeScaleGestureDetector() {
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor

                renderer.scale *= scaleFactor

                requestRender()

                return true
            }
        })
    }
}
