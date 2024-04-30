package com.example.openglapp.cube

import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

private const val COORDS_PER_VERTEX = 3

private val cubeCoords = floatArrayOf(
    -1.0f,  1.0f, -1.0f,
    1.0f,  1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    -1.0f, -1.0f,  1.0f,
    1.0f, -1.0f,  1.0f,
    -1.0f,  1.0f, -1.0f,
    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f,  1.0f,
    -1.0f,  1.0f,  1.0f,
    1.0f,  1.0f, -1.0f,
    1.0f, -1.0f, -1.0f,
    1.0f, -1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    -1.0f, -1.0f, -1.0f,
    -1.0f, -1.0f,  1.0f,
    1.0f, -1.0f,  1.0f,
    1.0f, -1.0f, -1.0f,
    -1.0f,  1.0f, -1.0f,
    -1.0f,  1.0f,  1.0f,
    1.0f,  1.0f,  1.0f,
    1.0f,  1.0f, -1.0f
)

private val cubeIndices = shortArrayOf(
    0, 2, 3, 0, 1, 3,
    4, 6, 7, 4, 5, 7,
    8, 9, 10, 11, 8, 10,
    12, 13, 14, 15, 12, 14,
    16, 17, 18, 19, 16, 18,
    20, 21, 22, 23, 20, 22
)

private val colorCoords = floatArrayOf(
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 1.0f, 0.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    0.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 1.0f, 0.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.0f, 1.0f, 1.0f,
    1.0f, 0.5f, 0.0f, 1.0f,
    1.0f, 0.5f, 0.0f, 1.0f,
    1.0f, 0.5f, 0.0f, 1.0f,
    1.0f, 0.5f, 0.0f, 1.0f
)

class Cube {
    private var mProgram: Int = 0
    private var positionHandle: Int = 0
    private var colorHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val vertexStride: Int = COORDS_PER_VERTEX * 4

    private val vertexShaderCode =
        "uniform mat4 uMVPMatrix;" +
                "attribute vec4 vPosition;" +
                "attribute vec4 vColor;" +
                "varying vec4 fragColor;" +
                "void main() {" +
                "  gl_Position = uMVPMatrix * vPosition;" +
                "  fragColor = vColor;" +
                "}"

    private val fragmentShaderCode =
        "precision mediump float;" +
                "varying vec4 fragColor;" +
                "void main() {" +
                "  gl_FragColor = fragColor;" +
                "}"

    init {
        val bbVertices = ByteBuffer.allocateDirect(cubeCoords.size * 4)
        bbVertices.order(ByteOrder.nativeOrder())
        vertexBuffer = bbVertices.asFloatBuffer()
        vertexBuffer.put(cubeCoords)
        vertexBuffer.position(0)

        val bbColors = ByteBuffer.allocateDirect(colorCoords.size * 4)
        bbColors.order(ByteOrder.nativeOrder())
        colorBuffer = bbColors.asFloatBuffer()
        colorBuffer.put(colorCoords)
        colorBuffer.position(0)

        val dlb = ByteBuffer.allocateDirect(cubeIndices.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        indexBuffer = dlb.asShortBuffer()
        indexBuffer.put(cubeIndices)
        indexBuffer.position(0)

        val vertexShader: Int = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader: Int = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer
            )
        }

        colorHandle = GLES20.glGetAttribLocation(mProgram, "vColor").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it, 4,
                GLES20.GL_FLOAT, false,
                0, colorBuffer
            )
        }

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, cubeIndices.size,
            GLES20.GL_UNSIGNED_SHORT, indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(colorHandle)

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}
