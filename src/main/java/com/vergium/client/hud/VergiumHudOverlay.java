package com.vergium.client.hud;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import com.vergium.util.PerformanceTracker;
import com.vergium.util.SystemMonitor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Vergium.MODID, value = Dist.CLIENT)
public class VergiumHudOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;

        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        VergiumConfig config = instance.getConfig();
        if (!config.isShowFpsOverlay()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        GuiGraphics graphics = event.getGuiGraphics();
        PerformanceTracker tracker = instance.getPerformanceTracker();
        SystemMonitor monitor = instance.getSystemMonitor();

        if (tracker == null || monitor == null) return;

        int x = 5;
        int y = 5;
        int lineHeight = 11;

        // Background
        int lines = config.isShowPerformanceMetrics() ? 7 : 2;
        int bgWidth = 140;
        int bgHeight = lines * lineHeight + 6;
        graphics.fill(x - 2, y - 2, x + bgWidth, y + bgHeight, 0x88000000);

        // FPS with color coding
        int fps = tracker.getFps();
        int fpsColor;
        if (fps >= 60) fpsColor = 0xFF44FF44;
        else if (fps >= 30) fpsColor = 0xFFFFFF44;
        else fpsColor = 0xFFFF4444;

        graphics.drawString(mc.font,
            String.format("§lVergium§r FPS: %d", fps),
            x, y, fpsColor, false);
        y += lineHeight;

        // Boost status
        String boostStatus = config.isFpsBoostEnabled() ? "§aActive" : "§cDisabled";
        graphics.drawString(mc.font, "Boost: " + boostStatus, x, y, 0xFFFFFF, false);
        y += lineHeight;

        if (config.isShowPerformanceMetrics()) {
            // CPU
            double cpu = monitor.getCpuUsage();
            int cpuColor = cpu > 80 ? 0xFFFF4444 : cpu > 50 ? 0xFFFFFF44 : 0xFF44FF44;
            graphics.drawString(mc.font,
                String.format("CPU: %.1f%%", cpu),
                x, y, cpuColor, false);
            y += lineHeight;

            // Memory
            graphics.drawString(mc.font,
                String.format("MEM: %dMB / %dMB",
                    monitor.getUsedMemoryMB(), monitor.getMaxMemoryMB()),
                x, y, 0xFFFFFF, false);
            y += lineHeight;

            // Frame time
            graphics.drawString(mc.font,
                String.format("Frame: %.1fms", tracker.getAverageFrameTime()),
                x, y, 0xFFFFFF, false);
            y += lineHeight;

            // Thermal state
            SystemMonitor.ThermalState state = monitor.getThermalState();
            int stateColor = switch (state) {
                case COOL -> 0xFF44FF44;
                case WARM -> 0xFFFFFF44;
                case HOT -> 0xFFFF8844;
                case THROTTLING -> 0xFFFF4444;
            };
            graphics.drawString(mc.font,
                "Thermal: " + state.name(), x, y, stateColor, false);
            y += lineHeight;

            // Throttle level
            var heatOpt = instance.getHeatOptimizer();
            if (heatOpt != null) {
                graphics.drawString(mc.font,
                    String.format("Throttle: %.0f%%", heatOpt.getThrottleLevel() * 100),
                    x, y, 0xFFFFFF, false);
            }
        }
    }
}
