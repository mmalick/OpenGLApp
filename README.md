# OpenGL 3D Shapes Viewer

## Overview
This Android application demonstrates 3D rendering using OpenGL ES. It features interactive demos that display a simple triangle, a colorful cube, and a detailed plane model loaded from OBJ/MTL files. Navigation between these demos is managed using Jetpack Compose Navigation for a seamless user experience.

## Features
- **Triangle Rendering:** Draws a basic 2D triangle using custom vertex and fragment shaders.
- **Cube Rendering:** Renders a 3D cube with distinct color faces and depth testing.
- **Plane Rendering:** Loads and displays a 3D plane model with texture mapping from OBJ and MTL files.
- **Interactive Navigation:** Switch easily between different shape demos with Jetpack Compose.

## Technologies & Libraries
- **Android & Kotlin:** Core development platform and language.
- **OpenGL ES 2.0:** For 2D/3D graphics rendering.
- **Jetpack Compose Navigation:** Facilitates in-app navigation between demos.
- **OBJ/MTL Parsing:** Custom implementation for loading complex 3D models.
- **Matrix Transformations:** Uses Android’s Matrix utilities for 3D transformations and projections.
