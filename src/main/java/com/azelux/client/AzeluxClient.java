package com.azelux.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;

public final class AzeluxClient implements ClientModInitializer {
    public static final String MOD_ID = "azelux";
    public static final Identifier HUD_ID = id("hud");
    private static KeyMapping openMenu, toggleHud, toggleFullbright, togglePlayers, openF8, openCosmetics;
    private static double previousGamma = 1.0;
    private static HumanoidArm previousMainArm = HumanoidArm.RIGHT;

    @Override
    public void onInitializeClient() {
        AzeluxConfig.load();
        KeyMapping.Category category = KeyMapping.Category.register(id("client"));
        openMenu = register("key.azelux.open_menu", InputConstants.KEY_RIGHT_SHIFT, category);
        toggleHud = register("key.azelux.toggle_hud", InputConstants.KEY_F7, category);
        toggleFullbright = register("key.azelux.fullbright", InputConstants.KEY_F6, category);
        togglePlayers = register("key.azelux.player_list", InputConstants.KEY_F9, category);
        openF8 = register("key.azelux.f8", InputConstants.KEY_F8, category);
        openCosmetics = register("key.azelux.cosmetics", InputConstants.KEY_F10, category);

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, HUD_ID, AzeluxHud::render);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            AzeluxConfig c = AzeluxConfig.get();

            while (openMenu.consumeClick()) {
                client.setScreen(new AzeluxMenuScreen(client.screen));
            }
            while (toggleHud.consumeClick()) {
                c.hudEnabled = !c.hudEnabled;
                c.save();
            }
            while (togglePlayers.consumeClick()) {
                c.playerList = !c.playerList;
                c.tabList = c.playerList;
                c.save();
            }
            while (toggleFullbright.consumeClick()) {
                c.fullbright = !c.fullbright;
                applyFullbright(client, c.fullbright);
                c.save();
            }
            while (openF8.consumeClick()) client.setScreen(new AzeluxF8Screen(client.screen));
            while (openCosmetics.consumeClick()) client.setScreen(new AzeluxCosmeticsScreen(client.screen));

            // The Java option is the cleanest translation of Bedrock's Left Hand module.
            if (client.player != null) {
                HumanoidArm desired = c.leftHand ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
                if (client.options.mainHand().get() != desired) client.options.mainHand().set(desired);
            }
            if (c.fullbright) applyFullbright(client, true);
        });

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                screen.addRenderableWidget(
                    Button.builder(Component.literal("Azelux"), b -> client.setScreen(new AzeluxMenuScreen(screen)))
                        .bounds(scaledWidth / 2 - 100, scaledHeight / 4 + 96, 200, 20)
                        .build()
                );
            }
        });
    }

    private static void applyFullbright(Minecraft client, boolean enabled) {
        if (client.options == null) return;
        if (enabled) {
            previousGamma = client.options.gamma().get();
            // Gamma is intentionally pushed past the normal slider range; the renderer accepts
            // the option value and this keeps the feature entirely client-side.
            client.options.gamma().set(15.0);
        } else {
            client.options.gamma().set(previousGamma);
        }
    }

    private static KeyMapping register(String translationKey, int key, KeyMapping.Category category) {
        return KeyMappingHelper.registerKeyMapping(
            new KeyMapping(translationKey, InputConstants.Type.KEYSYM, key, category)
        );
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
