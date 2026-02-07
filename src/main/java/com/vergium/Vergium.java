package com.vergium;

import com.vergium.config.ConfigManager;
import com.vergium.config.VergiumConfig;
import com.vergium.client.keybind.VergiumKeybinds;
import com.vergium.optimization.*;
import com.vergium.util.PerformanceTracker;
import com.vergium.util.SystemMonitor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Vergium.MODID)
public class Vergium {
    public static final String MODID = "vergium";
    public static final String MOD_NAME = "Vergium FPS Booster";
    public static final String VERSION = "1.0.0";
    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private static Vergium instance;

    private VergiumConfig config;
    private ConfigManager configManager;
    private PerformanceTracker performanceTracker;
    private SystemMonitor systemMonitor;

    // Optimizers
    private FpsOptimizer fpsOptimizer;
    private ChunkOptimizer chunkOptimizer;
    private EntityOptimizer entityOptimizer;
    private ParticleOptimizer particleOptimizer;
    private RenderOptimizer renderOptimizer;
    private TickOptimizer tickOptimizer;
    private MemoryOptimizer memoryOptimizer;
    private HeatOptimizer heatOptimizer;

    public Vergium() {
        instance = this;
        LOGGER.info("Vergium FPS Booster initializing...");

        configManager = new ConfigManager();
        config = configManager.load();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        }

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Vergium common setup complete");
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Vergium client setup starting...");

        performanceTracker = new PerformanceTracker();
        systemMonitor = new SystemMonitor();

        // Initialize optimizers
        fpsOptimizer = new FpsOptimizer(config);
        chunkOptimizer = new ChunkOptimizer(config);
        entityOptimizer = new EntityOptimizer(config);
        particleOptimizer = new ParticleOptimizer(config);
        renderOptimizer = new RenderOptimizer(config);
        tickOptimizer = new TickOptimizer(config);
        memoryOptimizer = new MemoryOptimizer(config);
        heatOptimizer = new HeatOptimizer(config, systemMonitor);

        // Register event handlers
        MinecraftForge.EVENT_BUS.register(performanceTracker);
        MinecraftForge.EVENT_BUS.register(fpsOptimizer);
        MinecraftForge.EVENT_BUS.register(chunkOptimizer);
        MinecraftForge.EVENT_BUS.register(entityOptimizer);
        MinecraftForge.EVENT_BUS.register(particleOptimizer);
        MinecraftForge.EVENT_BUS.register(renderOptimizer);
        MinecraftForge.EVENT_BUS.register(tickOptimizer);
        MinecraftForge.EVENT_BUS.register(memoryOptimizer);
        MinecraftForge.EVENT_BUS.register(heatOptimizer);

        VergiumKeybinds.register();
        MinecraftForge.EVENT_BUS.register(new VergiumKeybinds());

        LOGGER.info("Vergium client setup complete - All optimizations loaded");
    }

    public static Vergium getInstance() {
        return instance;
    }

    public VergiumConfig getConfig() {
        return config;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PerformanceTracker getPerformanceTracker() {
        return performanceTracker;
    }

    public SystemMonitor getSystemMonitor() {
        return systemMonitor;
    }

    public FpsOptimizer getFpsOptimizer() {
        return fpsOptimizer;
    }

    public ChunkOptimizer getChunkOptimizer() {
        return chunkOptimizer;
    }

    public EntityOptimizer getEntityOptimizer() {
        return entityOptimizer;
    }

    public ParticleOptimizer getParticleOptimizer() {
        return particleOptimizer;
    }

    public RenderOptimizer getRenderOptimizer() {
        return renderOptimizer;
    }

    public TickOptimizer getTickOptimizer() {
        return tickOptimizer;
    }

    public MemoryOptimizer getMemoryOptimizer() {
        return memoryOptimizer;
    }

    public HeatOptimizer getHeatOptimizer() {
        return heatOptimizer;
    }

    public void saveConfig() {
        configManager.save(config);
    }
}
