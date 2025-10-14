# Feature Updates

## Overview
This document outlines the new features and enhancements made to the MapView component across the codebase.

## Modified Files

### Android
- `android/src/main/java/com/rnmapbox/rnmbx/components/mapview/NativeMapViewModule.kt`
- `android/src/main/old-arch/com/rnmapbox/rnmbx/NativeMapViewModuleSpec.java`
- `android/src/main/java/com/rnmapbox/rnmbx/components/mapview/RNMBXMapView.kt`

### iOS
- `ios/RNMBX/RNMBXMapViewModule.mm`
- `ios/RNMBX/RNMBXMapViewManager.swift`

### React Native
- `src/components/MapView.tsx`


## New Features

### 1. `queryRenderedLayersInRect`
Query rendered layers within a specified rectangular area on the map. This feature allows you to identify which map layers are currently rendered within a given bounding box.

**Use Cases:**
- Detecting visible features in a specific area
- Interactive map region analysis
- Layer visibility detection

### 2. `getStyles`
Retrieve the current styles applied to the map. This method provides access to the map's style configuration.

**Use Cases:**
- Inspecting current map styling
- Style state management
- Dynamic style switching

### 3. `setLayerProperties`
Batch update multiple properties of a map layer at once. This method allows for efficient bulk property updates.

**Use Cases:**
- Optimized layer configuration
- Theme switching
- Bulk property updates

### 4. `setLayerProperty`
Set a single property on a specific map layer. This provides granular control over individual layer properties.

**Use Cases:**
- Fine-grained layer customization
- Property-level updates
- Dynamic layer configuration

## Implementation Details

These features have been implemented across both iOS and Android platforms to ensure consistent behavior and API surface across all supported platforms.

## Notes

All changes maintain backward compatibility with existing MapView implementations.
