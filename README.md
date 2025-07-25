### AR Home Renovation App

*An Android application demonstrating ARCore integration with both SceneView and native ARCore, plus depth visualization via MiDaS*

---

## Overview

AR Home Renovation is an experimental Android app that allows users to place, move, rotate, and visualize 3D furniture models in their real-world environment. The project combines:

- **SceneView-based AR experience** – a custom implementation I built from scratch.
- **Native ARCore integration** – adapted from Google’s official sample, converted from XML to Jetpack Compose, and structured with Clean Architecture.
- **Depth visualization** – uses the MiDaS depth-estimation model (TensorFlow Lite) to overlay a depth heatmap on detected surfaces.

My goal was to research how ARCore works under the hood, compare high‑level (SceneView) vs. low‑level (native ARCore) APIs, and explore real‑time depth information for more immersive AR experiences.

---

## Key Features

- **Place & interact with 3D models**
    
    – Move, rotate, enable/disable depth-based occlusion.
    
- **SceneView module (`feature_ar_home`)**
    
    – Fully Jetpack‑Compose UI from scratch.
    
    – Clean‑architecture separation: data → domain → presentation.
    
- **Native ARCore module (`feature_arcore_native`)**
    
    – Imported Google’s ARCore sample, refactored to Compose.
    
    – Kept original rendering pipeline (SampleRender, Framebuffer, etc.).
    
- **Depth Estimation (`feature_midas_depth_estimation`)**
    
    – MiDaS TFLite model for per‑pixel depth maps.
    
    – Real‑time point‑cloud generation and depth heatmap overlay.
    

---

## Architecture & Tech Stack

- **Languages & Frameworks**:
    
    Kotlin · Jetpack Compose · AndroidX · Hilt (DI) · MVVM · Clean Architecture
    
- **AR & Rendering**:
    
    ARCore 1.47.0 · SceneView 0.10.0 · SampleRender (native)
    
- **Machine Learning**:
    
    MiDaS depth model (`.tflite`) via ML Model Binding
    
- **Dependency Management**:
    
    Gradle Kotlin DSL · Version catalog (`libs.versions.toml`)
    
- **Other Libraries**:
    
    CameraX · Material3 · Room · TensorFlow Lite Metadata
    

---

## Project Structure

```
app/
├── assets/
│   └── models/… (dozens of .glb furniture assets)
├── java/com/jsb/arhomerenovat/
│   ├── feature_ar_home/            ← SceneView‑based AR
│   ├── feature_arcore_native/      ← Native ARCore (Google sample refactored)
│   └── feature_midas_depth_estimation/ ← MiDaS depth integration
└── ml/
    └── depth_model.tflite          ← MiDaS TFLite model

```

Each feature module follows Clean Architecture (data → domain → presentation) and is registered in `ARHomeRenovatApp.kt`.

---

## Getting Started

1. **Clone the repo**
    
    ```bash
    git clone https://github.com/YourUsername/ARHomeRenovationApp.git
    cd ARHomeRenovationApp
    
    ```
    
2. **Configure API keys**
    - Create a `local.properties` at the project root:
        
        ```
        AR_API_KEY=YOUR_ARCORE_API_KEY
        GEO_API_KEY=YOUR_GOOGLE_GEOSPATIAL_API_KEY
        
        ```
        
    - These are injected at build time via Gradle’s `manifestPlaceholders`.
3. **Build & run**
    - In Android Studio:
        
        **Build** --> **Make Project**, then **Run** on a compatible AR‑capable device.
        

---

## Screenshots

> Coming soon…
> 

---

## License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](./LICENSE) file for details.

---

## References

- **ARCore Geospatial Codelab**
    
    https://developers.google.com/ar/develop/java/geospatial/codelab#3
    
- **SceneView Android**
    
    https://github.com/SceneView/sceneview-android
    
- **ARCore Depth Quickstart**
    
    https://developers.google.com/ar/develop/java/depth/quickstart
    
- **MiDaS Depth Estimation**
    
    https://medium.com/beyondminds/depth-estimation-cad24b0099f
    
- **ARKit Occlusion Example**
    
    https://github.com/bjarnel/arkit-occlusion/tree/master/Occlusion
    
- **3D Asset Library (Poly.Pizza)**
    
    https://poly.pizza/
    

---

## Acknowledgements

- **Google ARCore samples** (Apache 2.0)
- **MiDaS model** by Intel ISL (MIT License)

---

I’d love to connect and discuss how this app demonstrates my passion for AR and Android architecture. Feel free to reach out if you’d like to learn more!
