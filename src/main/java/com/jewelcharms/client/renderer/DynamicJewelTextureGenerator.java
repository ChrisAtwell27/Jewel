package com.jewelcharms.client.renderer;

import com.jewelcharms.JewelCharms;
import com.jewelcharms.util.JewelData;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dynamically generates jewel textures based on material colors.
 * Creates unique textures for each jewel combination.
 */
public class DynamicJewelTextureGenerator {
    private static final Map<String, ResourceLocation> TEXTURE_CACHE = new HashMap<>();
    private static final int TEXTURE_SIZE = 16;

    /**
     * Get or create a texture for a jewel with the given colors.
     * @param colors List of material colors
     * @param isPolished true for polished (shiny), false for rough
     * @return ResourceLocation of the generated texture
     */
    public static ResourceLocation getOrCreateTexture(List<Integer> colors, boolean isPolished) {
        // Create cache key from colors and type
        String key = generateCacheKey(colors, isPolished);

        // Return cached texture if it exists
        if (TEXTURE_CACHE.containsKey(key)) {
            return TEXTURE_CACHE.get(key);
        }

        // Generate new texture
        ResourceLocation textureLocation = generateTexture(colors, isPolished, key);
        TEXTURE_CACHE.put(key, textureLocation);
        return textureLocation;
    }

    private static String generateCacheKey(List<Integer> colors, boolean isPolished) {
        StringBuilder sb = new StringBuilder();
        sb.append(isPolished ? "polished_" : "rough_");
        for (int color : colors) {
            sb.append(Integer.toHexString(color)).append("_");
        }
        return sb.toString();
    }

    private static ResourceLocation generateTexture(List<Integer> colors, boolean isPolished, String key) {
        try {
            // Create native image
            NativeImage image = new NativeImage(TEXTURE_SIZE, TEXTURE_SIZE, false);

            if (isPolished) {
                generatePolishedTexture(image, colors);
            } else {
                generateRoughTexture(image, colors);
            }

            // Create dynamic texture and register it
            DynamicTexture dynamicTexture = new DynamicTexture(image);
            ResourceLocation location = new ResourceLocation(JewelCharms.MOD_ID, "dynamic/jewel_" + key);
            Minecraft.getInstance().getTextureManager().register(location, dynamicTexture);

            return location;
        } catch (Exception e) {
            JewelCharms.LOGGER.error("Failed to generate jewel texture", e);
            return new ResourceLocation("minecraft", "textures/item/diamond.png");
        }
    }

    /**
     * Generate a polished (shiny, smooth) jewel texture
     */
    private static void generatePolishedTexture(NativeImage image, List<Integer> colors) {
        // Don't just average - use actual material colors for variety
        int primaryColor = colors.get(0);
        int secondaryColor = colors.size() > 1 ? colors.get(1) : primaryColor;
        int tertiaryColor = colors.size() > 2 ? colors.get(2) : secondaryColor;

        int avgR = ((primaryColor >> 16) & 0xFF);
        int avgG = ((primaryColor >> 8) & 0xFF);
        int avgB = (primaryColor & 0xFF);

        int secR = ((secondaryColor >> 16) & 0xFF);
        int secG = ((secondaryColor >> 8) & 0xFF);
        int secB = (secondaryColor & 0xFF);

        // Create gem shape with facets and material-based patterns
        for (int y = 0; y < TEXTURE_SIZE; y++) {
            for (int x = 0; x < TEXTURE_SIZE; x++) {
                float dx = (x - 7.5f) / 7.5f;
                float dy = (y - 7.5f) / 7.5f;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                // Outside gem boundary - transparent
                if (dist > 0.95f || y < 2 || y > 13 || x < 3 || x > 12) {
                    image.setPixelRGBA(x, y, packColor(0, 0, 0, 0));
                    continue;
                }

                // Determine which color zone based on position (creates layered effect)
                int r, g, b;
                float brightness;

                // Top highlight zone (white shine)
                if (y < 5 && x >= 6 && x <= 9) {
                    brightness = 1.5f - dist * 0.4f;
                    r = Math.min(255, (int)(avgR * brightness + 80));
                    g = Math.min(255, (int)(avgG * brightness + 80));
                    b = Math.min(255, (int)(avgB * brightness + 80));
                }
                // Left facet (primary color)
                else if (x < 8) {
                    brightness = 0.9f - dist * 0.2f;
                    r = (int)(avgR * brightness);
                    g = (int)(avgG * brightness);
                    b = (int)(avgB * brightness);
                }
                // Right facet (secondary color blend)
                else {
                    brightness = 0.85f - dist * 0.25f;
                    r = (int)((avgR * 0.7f + secR * 0.3f) * brightness);
                    g = (int)((avgG * 0.7f + secG * 0.3f) * brightness);
                    b = (int)((avgB * 0.7f + secB * 0.3f) * brightness);
                }

                // Bottom shadow
                if (y > 10) {
                    brightness = 0.5f - (y - 10) * 0.1f;
                    r = (int)(r * brightness);
                    g = (int)(g * brightness);
                    b = (int)(b * brightness);
                }

                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                image.setPixelRGBA(x, y, packColor(255, b, g, r));
            }
        }

        // Add facet lines for crystalline look
        addFacets(image, avgR, avgG, avgB, false);
    }

    /**
     * Generate a rough (unpolished, grainy) jewel texture
     */
    private static void generateRoughTexture(NativeImage image, List<Integer> colors) {
        // Use actual material colors but darker
        int primaryColor = colors.get(0);
        int secondaryColor = colors.size() > 1 ? colors.get(1) : primaryColor;

        int avgR = ((primaryColor >> 16) & 0xFF) * 6 / 10; // 60% brightness
        int avgG = ((primaryColor >> 8) & 0xFF) * 6 / 10;
        int avgB = (primaryColor & 0xFF) * 6 / 10;

        int secR = ((secondaryColor >> 16) & 0xFF) * 6 / 10;
        int secG = ((secondaryColor >> 8) & 0xFF) * 6 / 10;
        int secB = (secondaryColor & 0xFF) * 6 / 10;

        // Rough pattern - chunky, irregular with visible material layers
        for (int y = 0; y < TEXTURE_SIZE; y++) {
            for (int x = 0; x < TEXTURE_SIZE; x++) {
                float dx = (x - 7.5f) / 7.5f;
                float dy = (y - 7.5f) / 7.5f;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                // Outside gem boundary
                if (dist > 0.95f || y < 2 || y > 13 || x < 3 || x > 12) {
                    image.setPixelRGBA(x, y, packColor(0, 0, 0, 0));
                    continue;
                }

                // Create visible material layers/bands (rough, unpolished look)
                int r, g, b;
                float brightness = 0.6f - dist * 0.15f;

                // Pseudo-random noise for rough texture
                int seed = x * 37 + y * 73;
                int noise = (seed % 50) - 25;

                // Create layered effect - different materials visible in different zones
                if ((y + x) % 5 < 2) {
                    // Primary material zones
                    r = (int)(avgR * brightness) + noise / 2;
                    g = (int)(avgG * brightness) + noise / 2;
                    b = (int)(avgB * brightness) + noise / 2;
                } else {
                    // Secondary material visible in cracks/layers
                    r = (int)((avgR * 0.6f + secR * 0.4f) * brightness) + noise / 2;
                    g = (int)((avgG * 0.6f + secG * 0.4f) * brightness) + noise / 2;
                    b = (int)((avgB * 0.6f + secB * 0.4f) * brightness) + noise / 2;
                }

                // Add visible cracks/fissures
                if (seed % 11 == 0 || seed % 13 == 0) {
                    r = r * 2 / 3;
                    g = g * 2 / 3;
                    b = b * 2 / 3;
                }

                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                image.setPixelRGBA(x, y, packColor(255, b, g, r));
            }
        }

        // Add rough facet lines
        addFacets(image, avgR, avgG, avgB, true);
    }

    /**
     * Add crystalline facet lines
     */
    private static void addFacets(NativeImage image, int baseR, int baseG, int baseB, boolean rough) {
        int facetColor = packColor(255, baseB / 3, baseG / 3, baseR / 3);

        // Vertical facets (within y boundary: 2-13)
        for (int y = 2; y <= 13; y++) {
            image.setPixelRGBA(6, y, facetColor);
            image.setPixelRGBA(9, y, facetColor);
        }

        // Horizontal facets (within x boundary: 3-12)
        for (int x = 3; x <= 12; x++) {
            image.setPixelRGBA(x, 6, facetColor);
            image.setPixelRGBA(x, 9, facetColor);
        }

        // Diagonal facets (less prominent for rough)
        if (!rough) {
            for (int i = 4; i <= 11; i++) {
                // Only draw diagonal within boundaries
                if (i >= 3 && i <= 12) {
                    image.setPixelRGBA(i, i, facetColor);
                    int mirrorX = 15 - i;
                    if (mirrorX >= 3 && mirrorX <= 12) {
                        image.setPixelRGBA(mirrorX, i, facetColor);
                    }
                }
            }
        }
    }

    /**
     * Calculate average color from list of colors
     */
    private static int calculateAverageColor(List<Integer> colors) {
        if (colors.isEmpty()) {
            return 0xFFFFFF; // White default
        }

        int totalR = 0, totalG = 0, totalB = 0;
        for (int color : colors) {
            totalR += (color >> 16) & 0xFF;
            totalG += (color >> 8) & 0xFF;
            totalB += color & 0xFF;
        }

        int count = colors.size();
        int avgR = totalR / count;
        int avgG = totalG / count;
        int avgB = totalB / count;

        return (avgR << 16) | (avgG << 8) | avgB;
    }

    /**
     * Pack ARGB color into int format for NativeImage
     */
    private static int packColor(int alpha, int blue, int green, int red) {
        return (alpha << 24) | (blue << 16) | (green << 8) | red;
    }

    /**
     * Clear the texture cache (useful for resource reloading)
     */
    public static void clearCache() {
        TEXTURE_CACHE.clear();
    }
}
