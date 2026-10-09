package com.azelux.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class AzeluxCosmeticsScreen extends Screen {
    private final Screen parent;

    private static final String[] CAPES = buildCapeList();
    private static final String[] HALOS = buildRange("halo", 0, 11);
    private static final String[] WINGS = buildRange("wings", 1, 25);

    public AzeluxCosmeticsScreen(Screen parent) {
        super(Component.literal("Azelux Cosmetics"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int left = width / 2 - 310;
        int top = Math.max(16, height / 2 - 150);
        int buttonW = 190;
        addRenderableWidget(Button.builder(Component.literal("Cape: " + AzeluxConfig.get().selectedCape), b -> cycleCape(b))
            .bounds(left + 20, top + 190, buttonW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Halo: " + AzeluxConfig.get().selectedHalo), b -> cycleHalo(b))
            .bounds(left + 220, top + 190, buttonW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Wings: " + AzeluxConfig.get().selectedWings), b -> cycleWings(b))
            .bounds(left + 420, top + 190, buttonW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
            .bounds(left + 420, top + 215, buttonW, 20).build());
    }

    private void cycleCape(Button b) {
        String next = next(CAPES, AzeluxConfig.get().selectedCape);
        AzeluxConfig.get().selectedCape = next;
        b.setMessage(Component.literal("Cape: " + next));
        AzeluxConfig.get().save();
    }

    private void cycleHalo(Button b) {
        String next = next(HALOS, AzeluxConfig.get().selectedHalo);
        AzeluxConfig.get().selectedHalo = next;
        b.setMessage(Component.literal("Halo: " + next));
        AzeluxConfig.get().save();
    }

    private void cycleWings(Button b) {
        String next = next(WINGS, AzeluxConfig.get().selectedWings);
        AzeluxConfig.get().selectedWings = next;
        b.setMessage(Component.literal("Wings: " + next));
        AzeluxConfig.get().save();
    }

    private static String next(String[] values, String current) {
        for (int i = 0; i < values.length; i++) if (values[i].equals(current)) return values[(i + 1) % values.length];
        return values[0];
    }

    private static String[] buildCapeList() {
        String[] result = new String[54];
        result[0] = "cape_azelux2";
        result[1] = "cape_azelux3";
        for (int i = 0; i <= 51; i++) result[i + 2] = "cape" + i;
        return result;
    }

    private static String[] buildRange(String prefix, int first, int last) {
        String[] result = new String[last - first + 1];
        for (int i = first; i <= last; i++) result[i - first] = prefix + i;
        return result;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        int left = width / 2 - 310;
        int top = Math.max(16, height / 2 - 150);
        g.fill(0, 0, width, height, 0xC90A0A0D);
        g.fill(left, top, left + 620, top + 245, 0xE915151B);
        g.outline(left, top, 620, 245, 0xFF4A4A55);
        g.text(font, "Azelux Cosmetics", left + 20, top + 12, 0xFFFFFFFF, true);
        g.text(font, "Local cosmetic selection — does not require a server-side plugin", left + 20, top + 30, 0xFFAAAAAA, false);
        renderPreview(g, left + 42, top + 58, "cosmetics/capes/", AzeluxConfig.get().selectedCape, 118);
        renderPreview(g, left + 242, top + 58, "cosmetics/halo/", AzeluxConfig.get().selectedHalo, 118);
        renderPreview(g, left + 442, top + 58, "cosmetics/wings/", AzeluxConfig.get().selectedWings, 118);
        g.text(font, "Cape", left + 88, top + 165, 0xFFFFFFFF, true);
        g.text(font, "Halo", left + 292, top + 165, 0xFFFFFFFF, true);
        g.text(font, "Wings", left + 490, top + 165, 0xFFFFFFFF, true);
        g.text(font, "3D avatar attachment is the remaining renderer-specific step", left + 20, top + 232, 0xFFFFD866, false);
    }

    private void renderPreview(GuiGraphicsExtractor g, int x, int y, String dir, String value, int size) {
        Identifier id = AzeluxClient.id(dir + value + ".png");
        g.fill(x - 6, y - 6, x + size + 6, y + size + 6, 0x880A0A0E);
        g.outline(x - 6, y - 6, size + 12, size + 12, 0xFF4A4A55);
        g.blit(RenderPipelines.GUI_TEXTURED, id, x, y, 0, 0, size, size, size, size);
    }

    @Override
    public void onClose() {
        AzeluxConfig.get().save();
        minecraft.setScreen(parent);
    }
}
