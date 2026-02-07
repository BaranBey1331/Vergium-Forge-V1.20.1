package com.vergium.client.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import com.vergium.Vergium;
import com.vergium.client.gui.VergiumSettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Vergium.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VergiumKeybinds {

    private static final String CATEGORY = "vergium.key.category";

    public static KeyMapping OPEN_SETTINGS;
    public static KeyMapping TOGGLE_OVERLAY;
    public static KeyMapping TOGGLE_BOOST;

    public static void register() {
        OPEN_SETTINGS = new KeyMapping(
            "vergium.key.open_settings",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
        );

        TOGGLE_OVERLAY = new KeyMapping(
            "vergium.key.toggle_overlay",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F9,
            CATEGORY
        );

        TOGGLE_BOOST = new KeyMapping(
            "vergium.key.toggle_boost",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F10,
            CATEGORY
        );
    }

    @Mod.EventBusSubscriber(modid = Vergium.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SETTINGS);
            event.register(TOGGLE_OVERLAY);
            event.register(TOGGLE_BOOST);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (OPEN_SETTINGS.consumeClick()) {
            mc.setScreen(new VergiumSettingsScreen(mc.screen));
        }

        while (TOGGLE_OVERLAY.consumeClick()) {
            var config = Vergium.getInstance().getConfig();
            config.setShowFpsOverlay(!config.isShowFpsOverlay());
            Vergium.getInstance().saveConfig();
        }

        while (TOGGLE_BOOST.consumeClick()) {
            var config = Vergium.getInstance().getConfig();
            config.setFpsBoostEnabled(!config.isFpsBoostEnabled());
            Vergium.getInstance().saveConfig();
        }
    }
}
