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

    private final List<Runnable> widgetUpdaters = new ArrayList<>();

    public VergiumSettingsScreen(Screen parent) {
        super(Component.literal("Vergium Settings"));
        this.parent = parent;
        this.config = Vergium.getInstance().getConfig();
    }

    @Override
    protected void init() {
        super.init();
        rebuildWidgets();
    }

    private void rebuildWidgets() {
        clearWidgets();
        widgetUpdaters.clear();

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
                rebuildWidgets();
            }).bounds(tabX, 25, tabWidth, 20).build());
        }

        // Preset selector at top of content area
        startY += 5;

        // Build category-specific widgets
        int y = startY;
        int widgetWidth = 200;
        int leftX = centerX - widgetWidth - 5;
        int rightX = centerX + 5;

        switch (selectedCategory) {
            case PERFORMANCE -> y = buildPerformanceWidgets(y, leftX, rightX, widgetWidth);
            case RENDERING -> y = buildRenderingWidgets(y, leftX, rightX, widgetWidth);
            case HEAT -> y = buildHeatWidgets(y, leftX, rightX, widgetWidth);
            case ADVANCED -> y = buildAdvancedWidgets(y, leftX, rightX, widgetWidth);
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
            rebuildWidgets();
        }).bounds(centerX + 5, this.height - 30, 95, 20).build());
    }

    private int buildPerformanceWidgets(int y, int leftX, int rightX, int w) {
        // Preset selector
        String presetName = switch (config.getPreset()) {
            case LOW -> "Low (Max FPS)";
            case MEDIUM -> "Medium (Balanced)";
            case HIGH -> "High (Quality)";
            case CUSTOM -> "Custom";
        };

        addRenderableWidget(Button.builder(
            Component.literal("Preset: " + presetName),
            btn -> {
                VergiumConfig.Preset[] presets = VergiumConfig.Preset.values();
                int idx = (config.getPreset().ordinal() + 1) % presets.length;
                config.applyPreset(presets[idx]);
                rebuildWidgets();
            }
        ).bounds(leftX, y, w * 2 + 10, 20).build());
        y += 28;

        // FPS Boost toggle
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

        // Entity Render Distance slider
        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Entity Render Distance",
            8, 128, config.getEntityRenderDistance(),
            val -> { config.setEntityRenderDistance(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> val + " blocks"
        ));
        y += 28;

        // Max Particles slider
        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Max Particles",
            100, 10000, config.getMaxParticles(),
            val -> { config.setMaxParticles(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> String.valueOf(val)
        ));
        y += 28;

        // Chunk Update Limit slider
        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Chunk Update Limit",
            1, 20, config.getChunkUpdateLimit(),
            val -> { config.setChunkUpdateLimit(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> String.valueOf(val)
        ));
        y += 28;

        // Cull Distance slider
        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Cull Distance",
            16, 256, config.getCullDistance(),
            val -> { config.setCullDistance(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> val + " blocks"
        ));
        y += 28;

        // LOD Bias button (cycle)
        String lodText = switch ((int)(config.getLodBias() * 10)) {
            case 5 -> "Aggressive";
            case 10 -> "Normal";
            case 20 -> "Quality";
            default -> String.format("%.1f", config.getLodBias());
        };
        addRenderableWidget(Button.builder(
            Component.literal("LOD Bias: " + lodText),
            btn -> {
                float current = config.getLodBias();
                if (current <= 0.5f) config.setLodBias(1.0f);
                else if (current <= 1.0f) config.setLodBias(2.0f);
                else config.setLodBias(0.5f);
                config.setPreset(VergiumConfig.Preset.CUSTOM);
                rebuildWidgets();
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

        // Heat Management master toggle
        addRenderableWidget(new VergiumToggleButton(
            leftX, y, fullWidth, 20,
            "Heat Management",
            config.isHeatManagementEnabled(),
            val -> config.setHeatManagementEnabled(val)
        ));
        y += 28;

        // Target FPS slider
        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "Target FPS",
            30, 300, config.getTargetFps(),
            val -> { config.setTargetFps(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> String.valueOf(val)
        ));
        y += 28;

        // CPU Usage Limit slider
        addRenderableWidget(new VergiumSlider(
            leftX, y, fullWidth, 20,
            "CPU Usage Limit",
            30, 100, config.getCpuUsageLimit(),
            val -> { config.setCpuUsageLimit(val); config.setPreset(VergiumConfig.Preset.CUSTOM); },
            val -> val + "%"
        ));
        y += 28;

        // Thermal Throttle toggle
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

        // Show current system stats
        y += 10;

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

        // Title
        graphics.drawCenteredString(this.font,
            Component.literal("Vergium Settings").withStyle(ChatFormatting.BOLD),
            this.width / 2, 8, 0xFFFFFF);

        // Draw selected category indicator
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

        // Draw heat status info in heat category
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

        var systemMonitor = instance.getSystemMonitor();
        var heatOpt = instance.getHeatOptimizer();

        if (systemMonitor != null) {
            SystemMonitor.ThermalState state = systemMonitor.getThermalState();
            int stateColor = switch (state) {
                case COOL -> 0xFF44FF44;
                case WARM -> 0xFFFFFF44;
                case HOT -> 0xFFFF8844;
                case THROTTLING -> 0xFFFF4444;
            };

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

            if (heatOpt != null) {
                graphics.drawString(this.font,
                    Component.literal(String.format("Throttle: %.0f%%", heatOpt.getThrottleLevel() * 100)),
                    infoX + 120, infoY, 0xFFFFFF);
            }
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
