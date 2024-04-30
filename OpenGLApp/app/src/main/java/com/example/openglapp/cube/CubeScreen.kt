package com.example.openglapp.cube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.openglapp.triangle.BottomNavigationBar

class CubeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CubeScreen(
                onNavigationToTriangle = { finish() },
                onNavigationToCube = { finish() },
                onNavigationToPlane = { finish() }
            )
        }
    }
}

@Composable
fun CubeScreen(
    onNavigationToTriangle: () -> Unit,
    onNavigationToCube: () -> Unit,
    onNavigationToPlane: () -> Unit
) {
    val selectedItem by remember { mutableStateOf(1) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem, onNavigationToTriangle, onNavigationToCube, onNavigationToPlane)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AndroidView(
                factory = { context ->
                    CubeGLSurfaceView(context)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
