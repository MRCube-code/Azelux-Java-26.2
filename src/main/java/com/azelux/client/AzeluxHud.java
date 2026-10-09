package com.azelux.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class AzeluxHud {
    private AzeluxHud() {}
    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        AzeluxConfig c = AzeluxConfig.get();
        if (!c.hudEnabled) return;
        AzeluxModules.render(graphics);
    }
}
