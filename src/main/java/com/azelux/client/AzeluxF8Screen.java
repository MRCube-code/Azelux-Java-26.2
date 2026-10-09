package com.azelux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public final class AzeluxF8Screen extends Screen {
    private final Screen parent;
    public AzeluxF8Screen(Screen parent) { super(Component.literal("Azelux F8 Menu")); this.parent = parent; }

    @Override protected void init() {
        int left = width / 2 - 170;
        int top = Math.max(20, height / 2 - 120);
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose()).bounds(left + 10, top + 210, 340, 20).build());
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        Minecraft mc = Minecraft.getInstance();
        var p = mc.player;
        int left = width / 2 - 170, top = Math.max(20, height / 2 - 120);
        g.fill(0, 0, width, height, 0xC90A0A0D);
        g.fill(left, top, left + 340, top + 240, 0xE915151B);
        g.outline(left, top, 340, 240, 0xFF4A4A55);
        g.text(font, "Azelux Client 7.1.0", left + 14, top + 12, 0xFFFFFFFF, true);
        g.text(font, "Minecraft Java Edition 26.2", left + 14, top + 29, 0xFFAAAAAA, false);
        if (p == null) return;
        int y = top + 54;
        g.text(font, "Player: " + p.getGameProfile().name(), left + 14, y, 0xFFFFFFFF, false); y += 14;
        g.text(font, String.format(java.util.Locale.ROOT, "Position: %.2f, %.2f, %.2f", p.getX(), p.getY(), p.getZ()), left + 14, y, 0xFFFFFFFF, false); y += 14;
        long ticks = p.level().getGameTime();
        g.text(font, "World time: " + ticks + " ticks / day " + (ticks / 24000L), left + 14, y, 0xFFFFFFFF, false); y += 14;
        String mode = p.isCreative() ? "creative" : p.isSpectator() ? "spectator" : p.isAdventure() ? "adventure" : "survival";
        g.text(font, "Gamemode: " + mode, left + 14, y, 0xFFFFFFFF, false); y += 14;
        g.text(font, "Dimension: " + p.level().dimension().identifier(), left + 14, y, 0xFFFFFFFF, false); y += 14;
        g.text(font, "FPS: " + mc.getFps(), left + 14, y, 0xFFFFFFFF, false); y += 14;
        g.text(font, "Java: 25+ required", left + 14, y, 0xFFAAAAAA, false);
    }

    @Override public void onClose() { minecraft.gui.setScreen(parent); }
}
