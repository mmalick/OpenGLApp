package com.example.openglapp.triangle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

class OpenGlES20Activity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TriangleScreen(
                onNavigationToTriangle = { finish() },
                onNavigationToCube = { finish() },
                onNavigationToPlane = { finish() }
            )
        }
    }
}

@Composable
fun TriangleScreen(onNavigationToTriangle: () -> Unit, onNavigationToCube: () -> Unit, onNavigationToPlane: () -> Unit) {
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(selectedItem, onNavigationToTriangle, onNavigationToCube, onNavigationToPlane)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AndroidView(
                factory = { context ->
                    MyGLSurfaceView(context).apply {

                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onNavigationToTriangle: () -> Unit,
    onNavigationToCube: () -> Unit,
    onNavigationToPlane: () -> Unit
) {
    NavigationBar(
        containerColor = Color.Transparent
    ) {
        val items = listOf("Triangle", "Cube", "Plane")
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index
            NavigationBarItem(
                icon = {
                    val iconColor = if (isSelected) Color.Yellow else Color.White

                    Icon(
                        imageVector = when (item) {
                            "Triangle" -> Icons.Filled.Star
                            "Cube" -> Icons.Filled.AccountBox
                            "Plane" -> Icons.Filled.ExitToApp
                            else -> Icons.Filled.Warning
                        },
                        contentDescription = item,
                        tint = iconColor,

                    )
                },
                label = {
                    Text(
                        text = item,
                        color = if (isSelected) Color.Yellow else Color.White
                    )
                },
                selected = isSelected,
                onClick = {
                    when (item) {
                        "Triangle" -> {
                            onNavigationToTriangle()
                        }
                        "Cube" -> {
                            onNavigationToCube()
                        }
                        "Plane" -> {
                            onNavigationToPlane()
                        }
                    }
                }

            )
        }
    }
}
