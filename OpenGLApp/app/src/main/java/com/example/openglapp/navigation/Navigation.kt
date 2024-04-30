package com.example.openglapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.openglapp.cube.CubeScreen
import com.example.openglapp.plane.PlaneScreen
import com.example.openglapp.triangle.TriangleScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "TriangleScreen") {
        composable("TriangleScreen") {
            TriangleScreen(
                onNavigationToTriangle = { navController.navigate("TriangleScreen") },
                onNavigationToCube = { navController.navigate("CubeScreen") },
                onNavigationToPlane = { navController.navigate("PlaneScreen") }
            )
        }
        composable("CubeScreen") {
            CubeScreen(
                onNavigationToTriangle = { navController.navigate("TriangleScreen") },
                onNavigationToCube = { navController.navigate("CubeScreen") },
                onNavigationToPlane = { navController.navigate("PlaneScreen") }
            )
        }
        composable("PlaneScreen") {
            PlaneScreen(
                onNavigationToTriangle = { navController.navigate("TriangleScreen") },
                onNavigationToCube = { navController.navigate("CubeScreen") },
                onNavigationToPlane = { navController.navigate("PlaneScreen") }
            )
        }
    }
}