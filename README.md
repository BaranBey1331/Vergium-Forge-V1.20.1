# Vergium - FPS Booster Mod

**Vergium** is a comprehensive FPS booster and heat optimization mod for Minecraft Forge 1.20.1.

## Features

### FPS Optimizations
- **Chunk Optimization** - Limits chunk updates per frame, distance-based prioritization
- **Entity Optimization** - LOD-based entity rendering, distance culling, frame skipping
- **Particle Optimization** - Particle count limits, distance culling
- **Render Optimization** - Block entity culling, lighting update optimization
- **Tick Optimization** - Adaptive tick processing under load
- **Memory Optimization** - Memory pressure detection, GC management

### Heat Management
- **CPU Usage Monitoring** - Tracks CPU usage in real-time
- **Thermal State Estimation** - Detects sustained high load (Cool → Warm → Hot → Throttling)
- **Dynamic Throttling** - Automatically reduces workload when system heats up
- **Frame Pacing** - Smooth frame delivery to prevent GPU spikes
- **CPU Usage Limit** - Configurable maximum CPU utilization

### Settings UI
- Minecraft-style settings screen (press V in-game)
- Category tabs: Performance, Rendering, Heat Management, Advanced
- Toggle buttons and sliders
- Presets: Low (Max FPS), Medium (Balanced), High (Quality), Custom
- Real-time system status display

### HUD Overlay
- FPS counter with color coding
- CPU/Memory usage
- Thermal state indicator
- Throttle level display
- Toggle with F9

## Keybinds
- **V** - Open Vergium Settings
- **F9** - Toggle Performance Overlay
- **F10** - Toggle FPS Boost

## Building

```bash
./gradlew build
