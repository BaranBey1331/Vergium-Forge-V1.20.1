package com.vergium.config;

public class VergiumConfig {

    public enum Preset {
        LOW, MEDIUM, HIGH, CUSTOM
    }

    private boolean fpsBoostEnabled = true;
    private boolean showFpsOverlay = true;
    private boolean showPerformanceMetrics = false;
    private Preset preset = Preset.MEDIUM;

    private boolean chunkOptimization = true;
    private boolean entityOptimization = true;
    private boolean particleOptimization = true;
    private boolean renderOptimization = true;
    private boolean tickOptimization = true;
    private boolean memoryOptimization = true;

    private int entityRenderDistance = 48;
    private int maxParticles = 2000;
    private int chunkUpdateLimit = 5;
    private float lodBias = 1.0f;
    private int cullDistance = 64;

    private boolean heatManagementEnabled = true;
    private int targetFps = 60;
    private int cpuUsageLimit = 80;
    private boolean thermalThrottle = true;
    private boolean framePacing = true;
    private boolean lazyChunkLoading = true;
    private boolean aggressiveCulling = false;

    public boolean isFpsBoostEnabled() { return fpsBoostEnabled; }
    public void setFpsBoostEnabled(boolean v) { this.fpsBoostEnabled = v; }

    public boolean isShowFpsOverlay() { return showFpsOverlay; }
    public void setShowFpsOverlay(boolean v) { this.showFpsOverlay = v; }

    public boolean isShowPerformanceMetrics() { return showPerformanceMetrics; }
    public void setShowPerformanceMetrics(boolean v) { this.showPerformanceMetrics = v; }

    public Preset getPreset() { return preset; }
    public void setPreset(Preset v) { this.preset = v; }

    public boolean isChunkOptimization() { return chunkOptimization; }
    public void setChunkOptimization(boolean v) { this.chunkOptimization = v; }

    public boolean isEntityOptimization() { return entityOptimization; }
    public void setEntityOptimization(boolean v) { this.entityOptimization = v; }

    public boolean isParticleOptimization() { return particleOptimization; }
    public void setParticleOptimization(boolean v) { this.particleOptimization = v; }

    public boolean isRenderOptimization() { return renderOptimization; }
    public void setRenderOptimization(boolean v) { this.renderOptimization = v; }

    public boolean isTickOptimization() { return tickOptimization; }
    public void setTickOptimization(boolean v) { this.tickOptimization = v; }

    public boolean isMemoryOptimization() { return memoryOptimization; }
    public void setMemoryOptimization(boolean v) { this.memoryOptimization = v; }

    public int getEntityRenderDistance() { return entityRenderDistance; }
    public void setEntityRenderDistance(int v) { this.entityRenderDistance = v; }

    public int getMaxParticles() { return maxParticles; }
    public void setMaxParticles(int v) { this.maxParticles = v; }

    public int getChunkUpdateLimit() { return chunkUpdateLimit; }
    public void setChunkUpdateLimit(int v) { this.chunkUpdateLimit = v; }

    public float getLodBias() { return lodBias; }
    public void setLodBias(float v) { this.lodBias = v; }

    public int getCullDistance() { return cullDistance; }
    public void setCullDistance(int v) { this.cullDistance = v; }

    public boolean isHeatManagementEnabled() { return heatManagementEnabled; }
    public void setHeatManagementEnabled(boolean v) { this.heatManagementEnabled = v; }

    public int getTargetFps() { return targetFps; }
    public void setTargetFps(int v) { this.targetFps = v; }

    public int getCpuUsageLimit() { return cpuUsageLimit; }
    public void setCpuUsageLimit(int v) { this.cpuUsageLimit = v; }

    public boolean isThermalThrottle() { return thermalThrottle; }
    public void setThermalThrottle(boolean v) { this.thermalThrottle = v; }

    public boolean isFramePacing() { return framePacing; }
    public void setFramePacing(boolean v) { this.framePacing = v; }

    public boolean isLazyChunkLoading() { return lazyChunkLoading; }
    public void setLazyChunkLoading(boolean v) { this.lazyChunkLoading = v; }

    public boolean isAggressiveCulling() { return aggressiveCulling; }
    public void setAggressiveCulling(boolean v) { this.aggressiveCulling = v; }

    public void applyPreset(Preset preset) {
        this.preset = preset;
        switch (preset) {
            case LOW:
                entityRenderDistance = 24;
                maxParticles = 500;
                chunkUpdateLimit = 2;
                lodBias = 0.5f;
                cullDistance = 32;
                aggressiveCulling = true;
                lazyChunkLoading = true;
                framePacing = true;
                thermalThrottle = true;
                targetFps = 60;
                cpuUsageLimit = 60;
                break;
            case MEDIUM:
                entityRenderDistance = 48;
                maxParticles = 2000;
                chunkUpdateLimit = 5;
                lodBias = 1.0f;
                cullDistance = 64;
                aggressiveCulling = false;
                lazyChunkLoading = true;
                framePacing = true;
                thermalThrottle = true;
                targetFps = 60;
                cpuUsageLimit = 80;
                break;
            case HIGH:
                entityRenderDistance = 96;
                maxParticles = 5000;
                chunkUpdateLimit = 10;
                lodBias = 2.0f;
                cullDistance = 128;
                aggressiveCulling = false;
                lazyChunkLoading = false;
                framePacing = false;
                thermalThrottle = false;
                targetFps = 144;
                cpuUsageLimit = 95;
                break;
            case CUSTOM:
                break;
        }
    }
}
