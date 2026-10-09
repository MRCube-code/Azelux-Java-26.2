package com.azelux.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class AzeluxMenuScreen extends Screen {
    private final Screen parent;

    public AzeluxMenuScreen(Screen parent) {
        super(Component.literal("Azelux Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        AzeluxConfig c = AzeluxConfig.get();
        int panelW = Math.min(720, width - 24);
        int panelH = Math.min(365, height - 20);
        int left = width / 2 - panelW / 2;
        int top = height / 2 - panelH / 2;
        int gap = 8;
        int colW = (panelW - 40 - gap * 2) / 3;
        int rowH = 24;
        int x1 = left + 12;
        int x2 = x1 + colW + gap;
        int x3 = x2 + colW + gap;
        int y = top + 53;

        Entry[] entries = {
            new Entry("HUD", () -> c.hudEnabled, v -> c.hudEnabled = v),
            new Entry("FPS", () -> c.fps, v -> c.fps = v),
            new Entry("Coordinates", () -> c.coordinates, v -> c.coordinates = v),
            new Entry("Direction HUD", () -> c.direction, v -> c.direction = v),
            new Entry("Armor HUD", () -> c.armorHud, v -> c.armorHud = v),
            new Entry("Offhand HUD", () -> c.offhandHud, v -> c.offhandHud = v),
            new Entry("Durability Viewer", () -> c.durabilityViewer, v -> c.durabilityViewer = v),
            new Entry("Inventory HUD", () -> c.inventoryHud, v -> c.inventoryHud = v),
            new Entry("Moving Status", () -> c.movingStatus, v -> c.movingStatus = v),
            new Entry("Totem Counter", () -> c.totemCounter, v -> c.totemCounter = v),
            new Entry("Player List", () -> c.playerList, v -> c.playerList = v),
            new Entry("Bottom Chat", () -> c.bottomChat, v -> c.bottomChat = v),
            new Entry("Fullbright", () -> c.fullbright, v -> c.fullbright = v),
            new Entry("Chunk Map", () -> c.chunkMap, v -> c.chunkMap = v),
            new Entry("Custom Crosshair", () -> c.customCrosshair, v -> c.customCrosshair = v),
            new Entry("Chunk Borders", () -> c.chunkBorders, v -> c.chunkBorders = v),
            new Entry("Glint Colorizer", () -> c.glintColorizer, v -> c.glintColorizer = v),
            new Entry("Shiny Pots", () -> c.shinyPots, v -> c.shinyPots = v),
            new Entry("Left Hand", () -> c.leftHand, v -> c.leftHand = v),
            new Entry("Hitboxes", () -> c.hitbox, v -> c.hitbox = v),
            new Entry("Hit Color", () -> c.hitColor, v -> c.hitColor = v),
            new Entry("Tab List", () -> c.tabList, v -> c.tabList = v),
            new Entry("FrameX", () -> c.frameX, v -> c.frameX = v),
            new Entry("Mods List", () -> c.modsList, v -> c.modsList = v),
            new Entry("HUD Background", () -> c.hudBackground, v -> c.hudBackground = v),
            new Entry("Chat Background", () -> c.chatBackground, v -> c.chatBackground = v),
            new Entry("Player List BG", () -> c.playerListBackground, v -> c.playerListBackground = v)
        };

        for (int i = 0; i < entries.length; i++) {
            int col = i % 3;
            int row = i / 3;
            int x = col == 0 ? x1 : col == 1 ? x2 : x3;
            addToggle(x, y + row * rowH, colW, entries[i]);
        }

        int bottomY = top + panelH - 53;
        addRenderableWidget(Button.builder(Component.literal("Cosmetics"), b -> minecraft.setScreen(new AzeluxCosmeticsScreen(this)))
            .bounds(x1, bottomY, colW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("F8 Info"), b -> minecraft.setScreen(new AzeluxF8Screen(this)))
            .bounds(x2, bottomY, colW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Crosshair: " + c.crosshair), b -> {
            c.crosshair = c.crosshair % 8 + 1;
            b.setMessage(Component.literal("Crosshair: " + c.crosshair));
            c.save();
        }).bounds(x3, bottomY, colW, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Save + Done"), b -> onClose())
            .bounds(x1, bottomY + 25, panelW - 24, 20).build());
    }

    private void addToggle(int x, int y, int width, Entry e) {
        addRenderableWidget(Button.builder(label(e.name, e.getter.get()), b -> {
            boolean next = !e.getter.get();
            e.setter.set(next);
            b.setMessage(label(e.name, next));
            AzeluxConfig.get().save();
        }).bounds(x, y, width, 20).build());
    }

    private static Component label(String name, boolean on) {
        return Component.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    private static final class Entry {
        final String name;
        final BoolGetter getter;
        final BoolSetter setter;
        Entry(String name, BoolGetter getter, BoolSetter setter) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
        }
    }

    @FunctionalInterface private interface BoolGetter { boolean get(); }
    @FunctionalInterface private interface BoolSetter { void set(boolean value); }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        super.extractRenderState(g, mouseX, mouseY, delta);
        int panelW = Math.min(720, width - 24);
        int panelH = Math.min(365, height - 20);
        int left = width / 2 - panelW / 2;
        int top = height / 2 - panelH / 2;
        g.fill(0, 0, width, height, 0xC90A0A0D);
        g.fill(left, top, left + panelW, top + panelH, 0xE915151B);
        g.outline(left, top, panelW, panelH, 0xFF4A4A55);
        g.text(font, "Azelux Client", left + 14, top + 11, 0xFFFFFFFF, true);
        g.text(font, "Java Edition 26.2  •  7.1.0 port", left + 14, top + 27, 0xFFAAAAAA, false);
        g.text(font, "Modules", left + 14, top + 39, 0xFFAAAAAA, false);
        g.text(font, "Experimental renderer modules are marked in-game", left + panelW - 270, top + 27, 0xFF77777F, false);
    }

    @Override
    public void onClose() {
        AzeluxConfig.get().save();
        minecraft.setScreen(parent);
    }
}
