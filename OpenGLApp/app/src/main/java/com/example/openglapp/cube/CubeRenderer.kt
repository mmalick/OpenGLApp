package com.example.openglapp.cube

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer : GLSurfaceView.Renderer {


    @Volatile
    var scale: Float = 1f
    @Volatile
    var angleX: Float = 0f
    @Volatile
    var angleY: Float = 0f

    private lateinit var mCube: Cube

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mCube = Cube()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val ratio: Float = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1f, 0f)

        val rotationMatrix = FloatArray(16)
        Matrix.setRotateM(rotationMatrix, 0, angleX, 1.0f, 0f, 0f)
        val tempMatrix = FloatArray(16)
        Matrix.setRotateM(tempMatrix, 0, angleY, 0f, 1.0f, 0f)
        Matrix.multiplyMM(rotationMatrix, 0, rotationMatrix, 0, tempMatrix, 0)

        val scaleMatrix = FloatArray(16)
        Matrix.setIdentityM(scaleMatrix, 0)
        Matrix.scaleM(scaleMatrix, 0, scale, scale, scale)

        Matrix.multiplyMM(tempMatrix, 0, rotationMatrix, 0, scaleMatrix, 0)
        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, tempMatrix, 0)

        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
        mCube.draw(mvpMatrix)
    }

}