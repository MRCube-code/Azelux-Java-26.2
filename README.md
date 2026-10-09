# Azelux Client — Java Edition 26.2

A Fabric client-side reimplementation of the supplied Azelux Client 7.1.0 Bedrock pack for **Minecraft Java Edition 26.2**.

## Ported client features

- Azelux module menu
- Persistent JSON configuration
- FPS and coordinates HUD
- Direction HUD
- Server address HUD
- Armor HUD
- Offhand HUD
- Durability Viewer
- Inventory HUD
- Moving Status with original Azelux status textures
- Totem Counter
- Player List / Tab-style overlay
- Bottom Chat backdrop
- Fullbright toggle
- Chunk Map with player indicator and original chunk-border artwork
- Custom Crosshair using the original eight crosshair textures
- Mods List
- FrameX frame-time display
- Left Hand option wired to the Java main-hand setting
- Cosmetic selector using the original Azelux cape/halo/wings artwork
- F8 information screen
- Title-screen Azelux entry button
- Keyboard shortcuts: Right Shift, F6, F7, F8, F9, F10

## Original source

The complete Bedrock source is retained under `bedrock-reference/` for porting/reference purposes, while the original artwork used by the Java client lives under `src/main/resources/assets/azelux/`.

## Renderer-specific work

A few original Bedrock modules are not one-to-one with Java's renderer and are intentionally marked as **visual beta** in the client rather than pretending they are fully equivalent. These include direct world hitbox tinting, hit-color overrides, true glint recoloring, full 3D cosmetic attachment to `AvatarRenderer`, and a true world-space chunk-border renderer.

Those pieces require Java 26.2 renderer integration/testing, not just Bedrock JSON conversion.

## Build

See `TERMUX_BUILD.md` for Android/Termux commands.
