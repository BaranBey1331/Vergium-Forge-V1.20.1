package com.vergium.client.gui;

import com.vergium.Vergium;
import com.vergium.config.VergiumConfig;
import com.vergium.util.SystemMonitor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class VergiumSettingsScreen extends Screen {

    private final Screen parent;
    private final VergiumConfig config;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int contentHeight = 0;

    private enum Category {
        PERFORMANCE("Performance", 0xFF44AA44),
        RENDERING("Rendering", 0xFF4488DD),
        HEAT("Heat Management", 0xFFDD6644),
        ADVANCED("Advanced", 0xFFAA44AA);

        final String name;
        final int color;

        Category(String name, int color) {
            this.name = name;
            this.color = color;
        }
    }

    private Category selectedCategory = Category.PERFORMANCE;

    public VergiumSettingsScreen(Screen parent) {
        super(Component.literal("Vergium Settings"));
        this.parent = parent;
        this.config = Vergium.getInstance().getConfig();
    }

    @Override
    protected void init() {
        super.init();
        buildSettingsWidgets();
    }

    private void buildSettingsWidgets() {
        clearWidgets();

        int centerX = this.width / 2;
        int startY = 50;

        // Category tabs at the top
        int tabWidth = 100;
        int tabSpacing = 5;
        int totalTabWidth = Category.values().length * (tabWidth + tabSpacing) - tabSpacing;
        int tabStartX = centerX - totalTabWidth / 2;

        for (int i = 0; i < Category.values().length; i++) {
            Category cat = Category.values()[i];
            int tabX = tabStartX + i * (tabWidth + tabSpacing);

            addRenderableWidget(Button.builder(Component.literal(cat.name), btn -> {
                selectedCategory = cat;
                scrollOffset = 0;
                buildSettingsWidgets();
            }).bounds(tabX, 25, tabWidth, 20).build());
        }

        startY += 5;

        int widgetWidth = 200;
        int leftX = centerX - widgetWidth - 5;
        int rightX = centerX + 5;

        int y;
        switch (selectedCategory) {
            case PERFORMANCE:
                y = buildPerformanceWidgets(startY, leftX, rightX, widgetWidth);
                break;
            case RENDERING:
                y = buildRenderingWidgets(startY, leftX, rightX, widgetWidth);
                break;
            case HEAT:
                y = buildHeatWidgets(startY, leftX, rightX, widgetWidth);
                break;
            case ADVANCED:
                y = buildAdvancedWidgets(startY, leftX, rightX, widgetWidth);
                break;
            default:
                y = startY;
                break;
        }

        contentHeight = y - startY;
        maxScroll = Math.max(0, contentHeight - (this.height - startY - 40));

        // Done button
        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> {
            Vergium.getInstance().saveConfig();
            this.minecraft.setScreen(parent);
        }).bounds(centerX - 100, this.height - 30, 95, 20).build());

        // Reset button
        addRenderableWidget(Button.builder(Component.literal("Reset"), btn -> {
            config.applyPreset(VergiumConfig.Preset.MEDIUM);
            buildSettingsWidgets();
        }).bounds(centerX + 5, this.height - 30, 95, 20).build());
    }

    private int buildPerformanceWidgets(int y, int leftX, int rightX, int w) {
        String presetName;
        switch (config.getPreset()) {
            case LOW: presetName = "Low (Max FPS)"; break;
            case MEDIUM: presetName = "Medium (Balanced)"; break;
            case HIGH: presetName = "High (Quality)"; break;
            case CUSTOM: presetName = "Custom"; break;
            default: presetName = "Unknown"; break;
        }

        addRenderableWidget(Button.builder(
            Component.literal("Preset: " + presetName),
            btn -> {
                VergiumConfig.Preset[] presets = VergiumConfig.Preset.values();
                int idx = (config.getPreset().ordinal() + 1) % presets.length;
                config.applyPreset(presets[idx]);
                buildSettingsWidgets();
            }
        ).bounds(leftX, y, w * 2 + 10, 20).build());
        y += 28;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "FPS Boost",
            config.isFpsBoostEnabled(),
            val -> config.setFpsBoostEnabled(val)
        ));

        addRenderableWidget(new VergiumToggleButton(
            rightX, y, w, 20,
            "Chunk Optimization",
            config.isChunkOptimization(),
            val -> { config.setChunkOptimization(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 25;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Entity Optimization",
            config.isEntityOptimization(),
            val -> { config.setEntityOptimization(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));

        addRenderableWidget(new VergiumToggleButton(
            rightX, y, w, 20,
            "Particle Optimization",
            config.isParticleOptimization(),
            val -> { config.setParticleOptimization(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 25;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Render Optimization",
            config.isRenderOptimization(),
            val -> { config.setRenderOptimization(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));

        addRenderableWidget(new VergiumToggleButton(
            rightX, y, w, 20,
            "Tick Optimization",
            config.isTickOptimization(),
            val -> { config.setTickOptimization(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 25;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Memory Optimization",
            config.isMemoryOptimization(),
            val -> { config.setMemoryOptimization(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 30;

        return y;
    }

    private int buildRenderingWidgets(int y, int leftX, int rightX, int w) {
        int fullWidth = w * 2 + 10;

        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Entity Render Distance",
            8, 128, config.getEntityRenderDistance(),
            val -> { config.setEntityRenderDistance(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> val + " blocks"
        ));
        y += 28;

        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Max Particles",
            100, 10000, config.getMaxParticles(),
            val -> { config.setMaxParticles(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> String.valueOf(val)
        ));
        y += 28;

        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Chunk Update Limit",
            1, 20, config.getChunkUpdateLimit(),
            val -> { config.setChunkUpdateLimit(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> String.valueOf(val)
        ));
        y += 28;

        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Cull Distance",
            16, 256, config.getCullDistance(),
            val -> { config.setCullDistance(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> val + " blocks"
        ));
        y += 28;

        String lodText;
        int lodVal = (int)(config.getLodBias() * 10);
        if (lodVal <= 5) lodText = "Aggressive";
        else if (lodVal <= 10) lodText = "Normal";
        else lodText = "Quality";

        addRenderableWidget(Button.builder(
            Component.literal("LOD Bias: " + lodText),
            btn -> {
                float current = config.getLodBias();
                if (current <= 0.5f) config.setLodBias(1.0f);
                else if (current <= 1.0f) config.setLodBias(2.0f);
                else config.setLodBias(0.5f);
                config.setPreset(VergiumConfig.Preset.CUSTOM);
                buildSettingsWidgets();
            }
        ).bounds(leftX, y, fullWidth, 20).build());
        y += 28;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Aggressive Culling",
            config.isAggressiveCulling(),
            val -> { config.setAggressiveCulling(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 30;

        return y;
    }

    private int buildHeatWidgets(int y, int leftX, int rightX, int w) {
        int fullWidth = w * 2 + 10;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, fullWidth, 20,
            "Heat Management",
            config.isHeatManagementEnabled(),
            val -> config.setHeatManagementEnabled(val)
        ));
        y += 28;

        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Target FPS",
            30, 300, config.getTargetFps(),
            val -> { config.setTargetFps(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> String.valueOf(val)
        ));
        y += 28;

        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "CPU Usage Limit",
            30, 100, config.getCpuUsageLimit(),
            val -> { config.setCpuUsageLimit(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> val + "%"
        ));
        y += 28;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Thermal Throttle",
            config.isThermalThrottle(),
            val -> { config.setThermalThrottle(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));

        addRenderableWidget(new VergiumToggleButton(
            rightX, y, w, 20,
            "Frame Pacing",
            config.isFramePacing(),
            val -> { config.setFramePacing(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 25;

        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Lazy Chunk Loading",
            config.isLazyChunkLoading(),
            val -> { config.setLazyChunkLoading(val); config.setPreset(VergiumConfig.Preset.CUSTOM); }
        ));
        y += 30;

        return y;
    }

    private int buildAdvancedWidgets(int y, int leftX, int rightX, int w) {
        addRenderableWidget(new VergiumToggleButton(
            leftX, y, w, 20,
            "Show FPS Overlay",
            config.isShowFpsOverlay(),
            val -> config.setShowFpsOverlay(val)
        ));

        addRenderableWidget(new VergiumToggleButton(
            rightX, y, w, 20,
            "Performance Metrics",
            config.isShowPerformanceMetrics(),
            val -> config.setShowPerformanceMetrics(val)
        ));
        y += 30;

        return y;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        graphics.drawCenteredString(this.font,
            Component.literal("Vergium Settings").withStyle(ChatFormatting.BOLD),
            this.width / 2, 8, 0xFFFFFF);

        int tabWidth = 100;
        int tabSpacing = 5;
        int totalTabWidth = Category.values().length * (tabWidth + tabSpacing) - tabSpacing;
        int tabStartX = this.width / 2 - totalTabWidth / 2;
        for (int i = 0; i < Category.values().length; i++) {
            Category cat = Category.values()[i];
            if (cat == selectedCategory) {
                int tabX = tabStartX + i * (tabWidth + tabSpacing);
                graphics.fill(tabX, 45, tabX + tabWidth, 47, cat.color);
            }
        }

        if (selectedCategory == Category.HEAT) {
            drawHeatInfo(graphics);
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void drawHeatInfo(GuiGraphics graphics) {
        Vergium instance = Vergium.getInstance();
        if (instance == null) return;

        int infoY = this.height - 80;
        int infoX = this.width / 2 - 150;

        SystemMonitor systemMonitor = instance.getSystemMonitor();
        if (systemMonitor == null) return;

        SystemMonitor.ThermalState state = systemMonitor.getThermalState();
        int stateColor;
        switch (state) {
            case COOL: stateColor = 0xFF44FF44; break;
            case WARM: stateColor = 0xFFFFFF44; break;
            case HOT: stateColor = 0xFFFF8844; break;
            case THROTTLING: stateColor = 0xFFFF4444; break;
            default: stateColor = 0xFFFFFFFF; break;
        }

        graphics.drawString(this.font,
            Component.literal("System Status:"),
            infoX, infoY, 0xAAAAAA);
        infoY += 12;

        graphics.drawString(this.font,
            Component.literal(String.format("CPU: %.1f%%", systemMonitor.getCpuUsage())),
            infoX, infoY, 0xFFFFFF);

        graphics.drawString(this.font,
            Component.literal(String.format("Memory: %dMB / %dMB",
                systemMonitor.getUsedMemoryMB(), systemMonitor.getMaxMemoryMB())),
            infoX + 120, infoY, 0xFFFFFF);
        infoY += 12;

        graphics.drawString(this.font,
            Component.literal("Thermal: " + state.name()),
            infoX, infoY, stateColor);

        var heatOpt = instance.getHeatOptimizer();
        if (heatOpt != null) {
            graphics.drawString(this.font,
                Component.literal(String.format("Throttle: %.0f%%", heatOpt.getThrottleLevel() * 100)),
                infoX + 120, infoY, 0xFFFFFF);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta * 10));
        return true;
    }

    @Override
    public void onClose() {
        Vergium.getInstance().saveConfig();
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    }
