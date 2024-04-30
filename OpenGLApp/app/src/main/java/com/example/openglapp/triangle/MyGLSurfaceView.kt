package com.example.openglapp.triangle

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import kotlin.math.max
import kotlin.math.min

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

    private val renderer: MyGLRenderer
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private var previousX: Float = 0f
    private var previousY: Float = 0f

    init {
        setEGLContextClientVersion(2)

        renderer = MyGLRenderer()

        setRenderer(renderer)

        renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        initializeScaleGestureDetector()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x: Float = e.x
        val y: Float = e.y

        if (!scaleGestureDetector.isInProgress) {

            when (e.action) {
                MotionEvent.ACTION_MOVE -> {

                    var dx: Float = x - previousX
                    var dy: Float = y - previousY

                    if (y > height / 2) {
                        dx *= -1
                    }

                    if (x < width / 2) {
                        dy *= -1
                    }

                    renderer.angle += (dx + dy) * TOUCH_SCALE_FACTOR
                    requestRender()
                }
            }
        }

        previousX = x
        previousY = y
        return true
    }

    companion object {
        private const val TOUCH_SCALE_FACTOR: Float = 45.0f / 320f
    }

    private fun initializeScaleGestureDetector() {
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                renderer.scale *= detector.scaleFactor

                renderer.scale = max(0.1f, min(renderer.scale, 5.0f))

                requestRender()
                return true
            }
        })
    }
    }


