package com.example.openglapp.plane

import android.content.Context
import android.opengl.GLES20
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Plane(context: Context, objFileName: String, mtlFileName: String) {
    private var mProgram: Int = 0
    private var positionHandle: Int = 0
    private var normalHandle: Int = 0
    private var textureHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private lateinit var vertexBuffer: FloatBuffer
    private lateinit var normalBuffer: FloatBuffer
    private lateinit var textureBuffer: FloatBuffer
    private lateinit var indexBuffer: ShortBuffer
    private var vertexCount: Int = 0

    class ModelData {
        lateinit var vertices: FloatArray
        lateinit var normals: FloatArray
        lateinit var textureCoordinates: FloatArray
        lateinit var indices: ShortArray
    }

    init {
        val modelData = loadObjModel(context, objFileName, mtlFileName)
        vertexCount = modelData.vertices.size / COORDS_PER_VERTEX
        initializeBuffers(modelData)

        val vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    "attribute vec3 vNormal;" +
                    "attribute vec2 texCoord;" +
                    "varying vec3 fragNormal;" +
                    "varying vec2 fragTexCoord;" +
                    "void main() {" +
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    "  fragNormal = vNormal;" +
                    "  fragTexCoord = texCoord;" +
                    "}"

        val fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec3 fragNormal;" +
                    "varying vec2 fragTexCoord;" +
                    "uniform sampler2D textureSampler;" +
                    "void main() {" +
                    "  vec3 normal = normalize(fragNormal);" +
                    "  vec3 lightDir = normalize(vec3(1, 1, 1));" +
                    "  float intensity = dot(normal, lightDir);" +
                    "  gl_FragColor = texture2D(textureSampler, fragTexCoord) * intensity;" +
                    "}"

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun initializeBuffers(modelData: ModelData) {
        val bbVertices = ByteBuffer.allocateDirect(modelData.vertices.size * 4)
        bbVertices.order(ByteOrder.nativeOrder())
        vertexBuffer = bbVertices.asFloatBuffer()
        vertexBuffer.put(modelData.vertices)
        vertexBuffer.position(0)

        val bbNormals = ByteBuffer.allocateDirect(modelData.normals.size * 4)
        bbNormals.order(ByteOrder.nativeOrder())
        normalBuffer = bbNormals.asFloatBuffer()
        normalBuffer.put(modelData.normals)
        normalBuffer.position(0)

        val bbTextureCoords = ByteBuffer.allocateDirect(modelData.textureCoordinates.size * 4)
        bbTextureCoords.order(ByteOrder.nativeOrder())
        textureBuffer = bbTextureCoords.asFloatBuffer()
        textureBuffer.put(modelData.textureCoordinates)
        textureBuffer.position(0)

        val dlb = ByteBuffer.allocateDirect(modelData.indices.size * 2)
        dlb.order(ByteOrder.nativeOrder())
        indexBuffer = dlb.asShortBuffer()
        indexBuffer.put(modelData.indices)
        indexBuffer.position(0)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(mProgram)

        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer
            )
        }

        normalHandle = GLES20.glGetAttribLocation(mProgram, "vNormal").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it, COORDS_PER_NORMAL,
                GLES20.GL_FLOAT, false,
                NORMAL_STRIDE, normalBuffer
            )
        }

        textureHandle = GLES20.glGetAttribLocation(mProgram, "texCoord").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(
                it, COORDS_PER_TEXTURE,
                GLES20.GL_FLOAT, false,
                TEXTURE_STRIDE, textureBuffer
            )
        }

        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES, vertexCount,
            GLES20.GL_UNSIGNED_SHORT, indexBuffer
        )

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(normalHandle)
        GLES20.glDisableVertexAttribArray(textureHandle)

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)
    }

    private fun loadObjModel(context: Context, objFileName: String, mtlFileName: String): ModelData {
        val modelData = ModelData()

        val objInputStream = context.assets.open(objFileName)
        val objReader = BufferedReader(InputStreamReader(objInputStream))

        val vertices = mutableListOf<Float>()
        val normals = mutableListOf<Float>()
        val textureCoords = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        try {
            var line: String?
            while (objReader.readLine().also { line = it } != null) {
                val parts = line!!.split(" ")
                when (parts[0]) {
                    "v" -> { // Vertex
                        vertices.add(parts[1].toFloat())
                        vertices.add(parts[2].toFloat())
                        vertices.add(parts[3].toFloat())
                    }
                    "vn" -> { // Normal
                        normals.add(parts[1].toFloat())
                        normals.add(parts[2].toFloat())
                        normals.add(parts[3].toFloat())
                    }
                    "vt" -> { // Texture coordinate
                        textureCoords.add(parts[1].toFloat())
                        textureCoords.add(parts[2].toFloat())
                    }
                    "f" -> { // Face
                        for (i in 1..3) {
                            val faceParts = parts[i].split("/")
                            val vertexIndex = faceParts[0].toInt() - 1
                            val textureIndex = faceParts[1].toInt() - 1
                            val normalIndex = faceParts[2].toInt() - 1
                            vertices.add(vertices[vertexIndex * 3])
                            vertices.add(vertices[vertexIndex * 3 + 1])
                            vertices.add(vertices[vertexIndex * 3 + 2])
                            textureCoords.add(textureCoords[textureIndex * 2])
                            textureCoords.add(textureCoords[textureIndex * 2 + 1])
                            normals.add(normals[normalIndex * 3])
                            normals.add(normals[normalIndex * 3 + 1])
                            normals.add(normals[normalIndex * 3 + 2])
                            indices.add(indices.size.toShort())
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            objReader.close()
        }

        modelData.vertices = vertices.toFloatArray()
        modelData.normals = normals.toFloatArray()
        modelData.textureCoordinates = textureCoords.toFloatArray()
        modelData.indices = indices.toShortArray()

        return modelData
    }

    companion object {
        private const val COORDS_PER_VERTEX = 3
        private const val COORDS_PER_NORMAL = 3
        private const val COORDS_PER_TEXTURE = 2
        private const val VERTEX_STRIDE = COORDS_PER_VERTEX * 4
        private const val NORMAL_STRIDE = COORDS_PER_NORMAL * 4
        private const val TEXTURE_STRIDE = COORDS_PER_TEXTURE * 4

        private fun loadShader(type: Int, shaderCode: String): Int {
            return GLES20.glCreateShader(type).also { shader ->
                GLES20.glShaderSource(shader, shaderCode)
                GLES20.glCompileShader(shader)
            }
        }
    }
}
