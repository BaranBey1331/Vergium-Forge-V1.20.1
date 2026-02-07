package com.vergium.util;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;

public class PerformanceTracker {

    private final LinkedList<Long> frameTimes = new LinkedList<>();
    private static final int SAMPLE_SIZE = 120;

    private int fps;
    private double averageFrameTime;
    private double minFrameTime;
    private double maxFrameTime;
    private long lastFrameTime;
    private int renderedChunks;
    private int renderedEntities;

    private long tickStartTime;
    private double lastTickTime;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            tickStartTime = System.nanoTime();
        } else {
            lastTickTime = (System.nanoTime() - tickStartTime) / 1_000_000.0;
        }
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            long now = System.nanoTime();
            if (lastFrameTime != 0) {
                long delta = now - lastFrameTime;
                frameTimes.addLast(delta);
                if (frameTimes.size() > SAMPLE_SIZE) {
                    frameTimes.removeFirst();
                }
                updateMetrics();
            }
            lastFrameTime = now;
        }
    }

    private void updateMetrics() {
        if (frameTimes.isEmpty()) return;

        long sum = 0;
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;

        for (long t : frameTimes) {
            sum += t;
            if (t < min) min = t;
            if (t > max) max = t;
        }

        averageFrameTime = (sum / (double) frameTimes.size()) / 1_000_000.0;
        minFrameTime = min / 1_000_000.0;
        maxFrameTime = max / 1_000_000.0;
        fps = (int) (1000.0 / averageFrameTime);
    }

    public int getFps() { return fps; }
    public double getAverageFrameTime() { return averageFrameTime; }
    public double getMinFrameTime() { return minFrameTime; }
    public double getMaxFrameTime() { return maxFrameTime; }
    public double getLastTickTime() { return lastTickTime; }

    public int getRenderedChunks() { return renderedChunks; }
    public void setRenderedChunks(int v) { this.renderedChunks = v; }

    public int getRenderedEntities() { return renderedEntities; }
    public void setRenderedEntities(int v) { this.renderedEntities = v; }

    public double getFrameTimeStability() {
        if (frameTimes.size() < 2) return 1.0;
        double mean = averageFrameTime;
        double sumSq = 0;
        for (long t : frameTimes) {
            double ms = t / 1_000_000.0;
            sumSq += (ms - mean) * (ms - mean);
        }
        double stddev = Math.sqrt(sumSq / frameTimes.size());
        return Math.max(0, 1.0 - (stddev / mean));
    }
}
