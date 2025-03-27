package com.jsb.arhomerenovat.feature_ar_home.presentation.plane_renderer

import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class PlaneRenderer {
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private var program: Int = 0

    companion object {
        private const val TAG = "PlaneRenderer"
    }

    init {
        // Define the quad vertices (X, Y, Z)
        val vertices = floatArrayOf(
            -0.5f, 0f, -0.5f,  // Bottom-left
            0.5f, 0f, -0.5f,   // Bottom-right
            0.5f, 0f, 0.5f,    // Top-right
            -0.5f, 0f, 0.5f    // Top-left
        )

        val indices = shortArrayOf(0, 1, 2, 0, 2, 3)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().apply {
                put(vertices)
                position(0)
            }

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer().apply {
                put(indices)
                position(0)
            }

        Log.d(TAG, "Vertex buffer initialized with ${vertices.size} elements")
        Log.d(TAG, "Index buffer initialized with ${indices.size} elements")
    }

    fun initOpenGL() {
        val vertexShaderCode = """
            attribute vec4 vPosition;
            uniform mat4 uMVPMatrix;
            void main() {
                gl_Position = uMVPMatrix * vPosition;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
            void main() {
                gl_FragColor = vec4(0.0, 1.0, 0.0, 0.5); // Green with transparency
            }
        """.trimIndent()

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        if (vertexShader == 0 || fragmentShader == 0) {
            Log.e(TAG, "Shader compilation failed!")
            return
        }

        program = GLES20.glCreateProgram().apply {
            GLES20.glAttachShader(this, vertexShader)
            GLES20.glAttachShader(this, fragmentShader)
            GLES20.glLinkProgram(this)
        }

        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] == GLES20.GL_FALSE) {
            Log.e(TAG, "Program linking failed!")
            Log.e(TAG, GLES20.glGetProgramInfoLog(program))
        } else {
            Log.d(TAG, "OpenGL program linked successfully")
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val matrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glUniformMatrix4fv(matrixHandle, 1, false, mvpMatrix, 0)

        // Log matrix values to ensure they're correct
        Log.d(TAG, "MVP Matrix: ${mvpMatrix.joinToString()}")

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        // Check for OpenGL errors
        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e(TAG, "OpenGL Error after drawing: $error")
        } else {
            Log.d(TAG, "Mesh drawn successfully")
        }

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type).apply {
            GLES20.glShaderSource(this, shaderCode)
            GLES20.glCompileShader(this)
        }

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == GLES20.GL_FALSE) {
            Log.e(TAG, "Shader compilation failed: ${GLES20.glGetShaderInfoLog(shader)}")
            GLES20.glDeleteShader(shader)
            return 0
        }
        return shader
    }
}
