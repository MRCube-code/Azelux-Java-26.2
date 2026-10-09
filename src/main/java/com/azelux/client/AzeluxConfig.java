package com.azelux.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/** Persistent Azelux settings. Fields are deliberately public so the GUI stays lightweight. */
public final class AzeluxConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("azelux.json");

    // Core modules from the original Bedrock client.
    public boolean hudEnabled = true;
    public boolean fps = true;
    public boolean coordinates = true;
    public boolean direction = true;
    public boolean armorHud = true;
    public boolean offhandHud = true;
    public boolean serverAddress = true;
    public boolean customCrosshair = true;
    public boolean durabilityViewer = true;
    public boolean inventoryHud = false;
    public boolean movingStatus = true;
    public boolean totemCounter = true;
    public boolean playerList = false;
    public boolean bottomChat = true;
    public boolean fullbright = false;
    public boolean chunkMap = false;
    public boolean f8Screen = true;
    public boolean modsList = false;

    // Additional modules exposed by the original modules page.
    public boolean chunkBorders = false;
    public boolean glintColorizer = false;
    public boolean shinyPots = false;
    public boolean leftHand = false;
    public boolean hitbox = false;
    public boolean hitColor = false;
    public boolean tabList = false;
    public boolean frameX = false;

    public boolean hudBackground = true;
    public boolean chatBackground = true;
    public boolean playerListBackground = true;
    public boolean chunkMapBackground = true;
    public boolean durabilityBackground = true;
    public int crosshair = 1;

    public int hudX = 8;
    public int hudY = 8;
    public int inventoryHudX = 8;
    public int inventoryHudY = 70;
    public int durabilityX = 8;
    public int durabilityY = 145;
    public int movingX = 8;
    public int movingY = 160;
    public int directionX = 8;
    public int directionY = 8;
    public int offhandX = -1;
    public int offhandY = -1;
    public int serverX = 8;
    public int serverY = -1;
    public int totemX = -1;
    public int totemY = -1;
    public int playerListX = -1;
    public int playerListY = 8;
    public int chunkMapX = -1;
    public int chunkMapY = 30;
    public int frameXX = -1;
    public int frameXY = 178;

    // Local-only cosmetics. These never alter multiplayer packets or server state.
    public String selectedCape = "cape_azelux2";
    public String selectedHalo = "halo0";
    public String selectedWings = "wings1";

    private static AzeluxConfig INSTANCE;

    private AzeluxConfig() {}

    public static AzeluxConfig get() {
        if (INSTANCE == null) INSTANCE = loadInternal();
        return INSTANCE;
    }

    public static void load() {
        INSTANCE = loadInternal();
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException ignored) {
            // A failed save should not crash the client.
        }
    }

    private static AzeluxConfig loadInternal() {
        if (!Files.exists(FILE)) return new AzeluxConfig();
        try {
            AzeluxConfig c = GSON.fromJson(Files.readString(FILE), AzeluxConfig.class);
            return c == null ? new AzeluxConfig() : c;
        } catch (Exception ignored) {
            return new AzeluxConfig();
        }
    }
}
