package com.vergium.util;

import com.vergium.Vergium;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class SystemMonitor {

    private final OperatingSystemMXBean osBean;
    private final MemoryMXBean memoryBean;

    private double cpuUsage;
    private long usedMemoryMB;
    private long maxMemoryMB;
    private long allocatedMemoryMB;

    // Estimated thermal state
    public enum ThermalState {
        COOL, WARM, HOT, THROTTLING
    }

    private ThermalState thermalState = ThermalState.COOL;
    private double estimatedLoad = 0;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL_MS = 1000;

    // Track sustained high CPU for thermal estimation
    private final double[] cpuHistory = new double[30]; // 30 seconds of history
    private int cpuHistoryIndex = 0;

    public SystemMonitor() {
        osBean = ManagementFactory.getOperatingSystemMXBean();
        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastUpdateTime < UPDATE_INTERVAL_MS) return;
        lastUpdateTime = now;

        // CPU usage
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            cpuUsage = sunBean.getProcessCpuLoad() * 100.0;
            if (cpuUsage < 0) cpuUsage = 0;
        } else {
            cpuUsage = osBean.getSystemLoadAverage() / osBean.getAvailableProcessors() * 100.0;
            if (cpuUsage < 0) cpuUsage = estimatedLoad;
        }

        // Memory
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        usedMemoryMB = heapUsage.getUsed() / (1024 * 1024);
        maxMemoryMB = heapUsage.getMax() / (1024 * 1024);
        allocatedMemoryMB = heapUsage.getCommitted() / (1024 * 1024);

        // CPU history for thermal estimation
        cpuHistory[cpuHistoryIndex % cpuHistory.length] = cpuUsage;
        cpuHistoryIndex++;

        // Estimate thermal state based on sustained CPU usage
        updateThermalState();
    }

    private void updateThermalState() {
        int samples = Math.min(cpuHistoryIndex, cpuHistory.length);
        if (samples < 3) {
            thermalState = ThermalState.COOL;
            return;
        }

        double avgCpu = 0;
        for (int i = 0; i < samples; i++) {
            avgCpu += cpuHistory[i];
        }
        avgCpu /= samples;

        // Also check recent trend (last 5 seconds)
        int recentSamples = Math.min(5, samples);
        double recentAvg = 0;
        for (int i = 0; i < recentSamples; i++) {
            int idx = (cpuHistoryIndex - 1 - i + cpuHistory.length) % cpuHistory.length;
            recentAvg += cpuHistory[idx];
        }
        recentAvg /= recentSamples;

        estimatedLoad = recentAvg;

        // Memory pressure factor
        double memoryPressure = (double) usedMemoryMB / maxMemoryMB;

        // Combined thermal score
        double thermalScore = avgCpu * 0.4 + recentAvg * 0.4 + memoryPressure * 100 * 0.2;

        if (thermalScore > 90) {
            thermalState = ThermalState.THROTTLING;
        } else if (thermalScore > 70) {
            thermalState = ThermalState.HOT;
        } else if (thermalScore > 50) {
            thermalState = ThermalState.WARM;
        } else {
            thermalState = ThermalState.COOL;
        }
    }

    public double getCpuUsage() { return cpuUsage; }
    public long getUsedMemoryMB() { return usedMemoryMB; }
    public long getMaxMemoryMB() { return maxMemoryMB; }
    public long getAllocatedMemoryMB() { return allocatedMemoryMB; }
    public ThermalState getThermalState() { return thermalState; }
    public double getEstimatedLoad() { return estimatedLoad; }

    public double getMemoryUsagePercent() {
        if (maxMemoryMB == 0) return 0;
        return (double) usedMemoryMB / maxMemoryMB * 100.0;
    }
}
