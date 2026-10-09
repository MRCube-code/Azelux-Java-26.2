package com.azelux.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class AzeluxModules {
    private AzeluxModules() {}

    public static void render(GuiGraphicsExtractor g) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        if (p == null) return;
        AzeluxConfig c = AzeluxConfig.get();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        if (c.fps || c.coordinates || c.direction || c.serverAddress) renderInfo(g, mc, p, c);
        if (c.armorHud) renderArmor(g, p, c);
        if (c.offhandHud) renderOffhand(g, p, w, h, c);
        if (c.durabilityViewer) renderDurability(g, p, c);
        if (c.inventoryHud) renderInventory(g, p, c);
        if (c.movingStatus) renderMovingStatus(g, p, c);
        if (c.totemCounter) renderTotem(g, p, w, h, c);
        if (c.playerList || c.tabList) renderPlayerList(g, mc, w, c);
        if (c.bottomChat) renderChatBackdrop(g, mc, w, h, c);
        if (c.chunkMap) renderChunkMap(g, p, w, c);
        if (c.modsList) renderModsList(g, mc, h, c);
        if (c.frameX) renderFrameX(g, mc, w, h, c);
        if (c.customCrosshair) renderCrosshair(g, w, h, c);
        if (c.shinyPots || c.glintColorizer || c.hitbox || c.hitColor || c.chunkBorders) renderExperimentalBadges(g, mc, w, h, c);
    }

    private static void renderInfo(GuiGraphicsExtractor g, Minecraft mc, LocalPlayer p, AzeluxConfig c) {
        List<String> lines = new ArrayList<>();
        if (c.fps) lines.add("FPS  " + mc.getFps());
        if (c.frameX) lines.add(String.format(Locale.ROOT, "Frame  %.1f ms", mc.getFps() <= 0 ? 0.0 : 1000.0 / mc.getFps()));
        if (c.coordinates) lines.add(String.format(Locale.ROOT, "XYZ  %.1f  %.1f  %.1f", p.getX(), p.getY(), p.getZ()));
        if (c.direction) lines.add("Facing  " + direction(p.getYRot()));
        if (c.serverAddress) lines.add("Server  " + serverText(mc));
        if (lines.isEmpty()) return;

        int x = c.hudX;
        int y = c.hudY;
        int width = 16;
        for (String s : lines) width = Math.max(width, mc.font.width(s));
        if (c.hudBackground) {
            g.fill(x - 5, y - 5, x + width + 7, y + lines.size() * 11 + 3, 0x990A0A0E);
            g.outline(x - 5, y - 5, width + 12, lines.size() * 11 + 8, 0xFF4B4B57);
        }
        int yy = y;
        for (String s : lines) {
            g.text(mc.font, s, x, yy, 0xFFFFFFFF, true);
            yy += 11;
        }
    }

    private static void renderArmor(GuiGraphicsExtractor g, LocalPlayer p, AzeluxConfig c) {
        int x = c.hudX;
        int y = c.hudY + ((c.fps || c.coordinates || c.direction || c.serverAddress) ? 54 : 0);
        EquipmentSlot[] slots = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
        for (int i = 0; i < slots.length; i++) {
            ItemStack s = p.getItemBySlot(slots[i]);
            int xx = x + i * 20;
            if (!s.isEmpty()) {
                g.item(s, xx, y);
                if (s.isDamageableItem()) g.itemDecorations(Minecraft.getInstance().font, s, xx, y, String.valueOf(s.getMaxDamage() - s.getDamageValue()));
            }
        }
    }

    private static void renderOffhand(GuiGraphicsExtractor g, LocalPlayer p, int w, int h, AzeluxConfig c) {
        ItemStack s = p.getOffhandItem();
        if (s.isEmpty()) return;
        int x = c.offhandX < 0 ? 8 : c.offhandX;
        int y = c.offhandY < 0 ? h - 62 : c.offhandY;
        g.fill(x - 3, y - 3, x + 20, y + 20, 0x66000000);
        g.outline(x - 3, y - 3, 23, 23, 0xFF4B4B57);
        g.item(s, x, y);
        g.itemDecorations(Minecraft.getInstance().font, s, x, y);
    }

    private static void renderDurability(GuiGraphicsExtractor g, LocalPlayer p, AzeluxConfig c) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack s = p.getMainHandItem();
        if (s.isEmpty() || !s.isDamageableItem()) return;
        int current = s.getMaxDamage() - s.getDamageValue();
        int max = s.getMaxDamage();
        int pct = Math.round(current * 100f / max);
        int x = c.durabilityX;
        int y = c.durabilityY;
        String name = s.getHoverName().getString();
        String line = name + "  " + current + "/" + max + " (" + pct + "%)";
        int width = mc.font.width(line) + 28;
        if (c.durabilityBackground) {
            g.fill(x - 4, y - 4, x + width, y + 14, 0x880A0A0E);
            g.outline(x - 4, y - 4, width + 4, 18, 0xFF4B4B57);
        }
        g.item(s, x, y - 1);
        g.text(mc.font, line, x + 20, y + 1, durabilityColor(pct), true);
    }

    private static int durabilityColor(int pct) {
        if (pct <= 15) return 0xFFFF5555;
        if (pct <= 35) return 0xFFFFAA33;
        return 0xFF55FF88;
    }

    private static void renderInventory(GuiGraphicsExtractor g, LocalPlayer p, AzeluxConfig c) {
        Minecraft mc = Minecraft.getInstance();
        int x = c.inventoryHudX;
        int y = c.inventoryHudY;
        int cols = 9;
        int rows = 4;
        g.fill(x - 4, y - 4, x + cols * 20 + 4, y + rows * 20 + 4, 0x880A0A0E);
        g.outline(x - 4, y - 4, cols * 20 + 8, rows * 20 + 8, 0xFF4B4B57);
        for (int i = 0; i < 36; i++) {
            ItemStack s = p.getInventory().getItem(i);
            if (s.isEmpty()) continue;
            // Main inventory is displayed first, then the hotbar in the bottom row.
            int row;
            int col = i % 9;
            if (i < 9) row = 3;
            else row = (i - 9) / 9;
            int xx = x + col * 20;
            int yy = y + row * 20;
            g.item(s, xx, yy);
            g.itemDecorations(mc.font, s, xx, yy);
            if (c.shinyPots && isPotion(s)) g.outline(xx - 1, yy - 1, 18, 18, 0xFFFFD84D);
            if (c.glintColorizer && hasGlint(s)) g.outline(xx - 1, yy - 1, 18, 18, 0xFFD66CFF);
        }
    }

    private static boolean isPotion(ItemStack s) {
        return s.is(Items.POTION) || s.is(Items.SPLASH_POTION) || s.is(Items.LINGERING_POTION);
    }

    private static boolean hasGlint(ItemStack s) {
        return s.hasFoil();
    }

    private static void renderMovingStatus(GuiGraphicsExtractor g, LocalPlayer p, AzeluxConfig c) {
        Minecraft mc = Minecraft.getInstance();
        String state;
        String texture;
        if (!p.onGround()) { state = "JUMPING"; texture = "jumping"; }
        else if (p.isCrouching()) { state = "SNEAKING"; texture = "sneaking"; }
        else if (p.isSprinting()) { state = "SPRINTING"; texture = "sprinting"; }
        else {
            Vec3 vel = p.getDeltaMovement();
            if (vel.horizontalDistanceSqr() > 0.0025) { state = "WALKING"; texture = "walking"; }
            else { state = "STANDING"; texture = "standing"; }
        }
        int x = c.movingX;
        int y = c.movingY;
        g.blit(RenderPipelines.GUI_TEXTURED, AzeluxClient.id("modules/moving_status/" + texture + ".png"), x, y, 0, 0, 45, 10, 45, 10);
        g.text(mc.font, state, x + 49, y + 1, 0xFFFFFFFF, true);
    }

    private static void renderTotem(GuiGraphicsExtractor g, LocalPlayer p, int w, int h, AzeluxConfig c) {
        int count = 0;
        for (ItemStack s : p.getInventory().items) if (s.is(Items.TOTEM_OF_UNDYING)) count += s.getCount();
        if (p.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) count += p.getOffhandItem().getCount();
        int x = c.totemX < 0 ? w - 84 : c.totemX;
        int y = c.totemY < 0 ? h - 54 : c.totemY;
        ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
        g.fill(x - 4, y - 4, x + 42, y + 22, 0x880A0A0E);
        g.outline(x - 4, y - 4, 46, 26, 0xFF4B4B57);
        g.item(totem, x, y - 1);
        g.text(Minecraft.getInstance().font, "x" + count, x + 20, y + 3, 0xFFFFFFFF, true);
    }

    private static void renderPlayerList(GuiGraphicsExtractor g, Minecraft mc, int w, AzeluxConfig c) {
        if (mc.level == null) return;
        List<String> players = new ArrayList<>();
        mc.level.players().forEach(player -> players.add(player.getGameProfile().name()));
        if (players.isEmpty()) return;
        players.sort(String.CASE_INSENSITIVE_ORDER);
        int max = Math.min(12, players.size());
        int x = c.playerListX < 0 ? w - 180 : c.playerListX;
        int y = c.playerListY;
        int width = 165;
        if (c.playerListBackground) g.fill(x, y, x + width, y + 18 + max * 11, 0xB00A0A0E);
        g.outline(x, y, width, 18 + max * 11, 0xFF4B4B57);
        g.text(mc.font, "PLAYERS  " + players.size(), x + 7, y + 6, 0xFFFFFFFF, true);
        for (int i = 0; i < max; i++) g.text(mc.font, players.get(i), x + 7, y + 20 + i * 11, 0xFFE8E8EE, false);
        if (players.size() > max) g.text(mc.font, "+" + (players.size() - max) + " more", x + 7, y + 20 + max * 11, 0xFFAAAAAA, false);
    }

    private static void renderChatBackdrop(GuiGraphicsExtractor g, Minecraft mc, int w, int h, AzeluxConfig c) {
        if (!c.chatBackground) return;
        int width = Math.min(420, (int) (w * 0.42));
        int top = h - 165;
        g.fill(4, top, width, h - 28, 0x3A000000);
        g.outline(4, top, width - 4, h - top - 28, 0x552F2F36);
    }

    private static void renderChunkMap(GuiGraphicsExtractor g, LocalPlayer p, int w, AzeluxConfig c) {
        int size = 132;
        int x = c.chunkMapX < 0 ? w - size - 8 : c.chunkMapX;
        int y = c.chunkMapY;
        int cell = size / 7;
        int center = 3;
        int chunkX = p.blockPosition().getX() >> 4;
        int chunkZ = p.blockPosition().getZ() >> 4;
        if (c.chunkMapBackground) g.fill(x - 4, y - 4, x + size + 4, y + size + 30, 0xA00A0A0E);
        g.outline(x - 4, y - 4, size + 8, size + 34, 0xFF4B4B57);
        for (int dz = -3; dz <= 3; dz++) for (int dx = -3; dx <= 3; dx++) {
            int xx = x + (dx + center) * cell;
            int yy = y + (dz + center) * cell;
            boolean selected = dx == 0 && dz == 0;
            g.fill(xx + 1, yy + 1, xx + cell - 1, yy + cell - 1, selected ? 0x664C7EC9 : 0x553A3A45);
            if (c.chunkBorders) {
                g.blit(RenderPipelines.GUI_TEXTURED, AzeluxClient.id("modules/chunkmap/chunk_border.png"), xx, yy, 0, 0, cell, cell, 16, 16);
            }
        }
        int px = x + center * cell + (int) ((p.getX() & 15) / 16f * cell);
        int py = y + center * cell + (int) ((p.getZ() & 15) / 16f * cell);
        g.blit(RenderPipelines.GUI_TEXTURED, AzeluxClient.id("modules/chunkmap/player_indicator.png"), px - 3, py - 3, 0, 0, 7, 7, 7, 7);
        String s = "Chunk " + chunkX + ", " + chunkZ;
        g.text(Minecraft.getInstance().font, s, x, y + size + 7, 0xFFFFFFFF, true);
    }

    private static void renderModsList(GuiGraphicsExtractor g, Minecraft mc, int h, AzeluxConfig c) {
        String[] mods = {
            "Azelux Client 7.1.0", "FPS", "Coordinates", "Direction HUD", "Armor HUD", "Offhand HUD",
            "Durability Viewer", "Inventory HUD", "Moving Status", "Totem Counter", "Player List", "Bottom Chat",
            "Fullbright", "Chunk Map", "Custom Crosshair", "Chunk Borders", "Glint Colorizer", "Shiny Pots",
            "Left Hand", "Hitboxes", "Hit Color", "Tab List", "FrameX", "Cosmetics"
        };
        int x = 8, y = h - Math.min(245, 20 + mods.length * 10);
        int width = 180;
        if (c.hudBackground) g.fill(x - 5, y - 5, x + width, y + mods.length * 10 + 4, 0x990A0A0E);
        g.outline(x - 5, y - 5, width + 5, mods.length * 10 + 9, 0xFF4B4B57);
        for (int i = 0; i < mods.length; i++) g.text(mc.font, mods[i], x, y + i * 10, i == 0 ? 0xFFFFFFFF : 0xFFE0E0E8, true);
    }

    private static void renderFrameX(GuiGraphicsExtractor g, Minecraft mc, int w, int h, AzeluxConfig c) {
        int x = c.frameXX < 0 ? w - 100 : c.frameXX;
        int y = c.frameXY;
        String label = String.format(Locale.ROOT, "FrameX %.1f ms", mc.getFps() <= 0 ? 0.0 : 1000.0 / mc.getFps());
        int width = mc.font.width(label) + 10;
        g.fill(x - 4, y - 4, x + width, y + 14, 0x880A0A0E);
        g.outline(x - 4, y - 4, width + 4, 18, 0xFF4B4B57);
        g.text(mc.font, label, x, y + 1, 0xFFFFFFFF, true);
    }

    private static void renderCrosshair(GuiGraphicsExtractor g, int w, int h, AzeluxConfig c) {
        Identifier id = AzeluxClient.id("modules/crosshair/crosshair_" + Math.max(1, Math.min(8, c.crosshair)) + ".png");
        g.blit(RenderPipelines.GUI_TEXTURED, id, w / 2 - 8, h / 2 - 8, 0, 0, 16, 16, 16, 16);
    }

    private static void renderExperimentalBadges(GuiGraphicsExtractor g, Minecraft mc, int w, int h, AzeluxConfig c) {
        StringBuilder s = new StringBuilder();
        if (c.glintColorizer) s.append("GLINT ");
        if (c.shinyPots) s.append("POTS ");
        if (c.hitbox) s.append("HITBOX ");
        if (c.hitColor) s.append("HITCOLOR ");
        if (c.chunkBorders) s.append("BORDERS ");
        if (s.isEmpty()) return;
        String label = "Azelux: " + s.toString().trim();
        int x = 8, y = h - 14;
        g.text(mc.font, label + " [visual beta]", x, y, 0xFFFFD866, true);
    }

    public static String direction(float yaw) {
        float normalized = ((yaw % 360) + 360) % 360;
        if (normalized >= 337.5 || normalized < 22.5) return "S";
        if (normalized < 67.5) return "SW";
        if (normalized < 112.5) return "W";
        if (normalized < 157.5) return "NW";
        if (normalized < 202.5) return "N";
        if (normalized < 247.5) return "NE";
        if (normalized < 292.5) return "E";
        return "SE";
    }

    private static String serverText(Minecraft mc) {
        if (mc.getCurrentServer() == null) return "Singleplayer";
        String ip = mc.getCurrentServer().ip;
        return ip == null || ip.isBlank() ? mc.getCurrentServer().name : ip;
    }
}
