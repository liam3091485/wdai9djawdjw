package com.larpclient.render.nanovg;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.nanovg.NSVGImage;
import org.lwjgl.nanovg.NanoSVG;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public final class NVGIcons {
   private static final int RASTER_SIZE = 64;
   private static final Map<String, Integer> ICONS = new HashMap<>();
   private static boolean loaded;

   private NVGIcons() {
   }

   public static int get(Category category) {
      ensureLoaded();
      return ICONS.getOrDefault(category.getIconId(), -1);
   }

   private static void ensureLoaded() {
      if (!loaded) {
         loaded = true;

         for (Category category : Category.values()) {
            int handle = loadSvg("assets/larpclient/icons/" + category.getIconId() + ".svg");
            if (handle > 0) {
               ICONS.put(category.getIconId(), handle);
            }
         }
      }
   }

   private static int loadSvg(String resourcePath) {
      long ctx = NVGRenderer.get().ctx();

      try {
         int var23;
         try (InputStream in = NVGIcons.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
               LarpClient.LOGGER.warn("Icon resource missing: {}", resourcePath);
               return -1;
            }

            String svg = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            String var22 = svg.replaceAll("#[0-9a-fA-F]{6}", "#ffffff")
               .replaceAll("#[0-9a-fA-F]{3}\\b", "#fff")
               .replace("currentColor", "#ffffff")
               .replace("\"black\"", "\"white\"");
            ByteBuffer svgData = MemoryUtil.memUTF8(var22, true);
            ByteBuffer units = MemoryUtil.memASCII("px");
            NSVGImage image = null;
            long rasterizer = 0L;
            ByteBuffer pixels = null;

            int n2;
            label136: {
               label102: {
                  try {
                     image = NanoSVG.nsvgParse(svgData, units, 96.0F);
                     if (image != null) {
                        break label102;
                     }

                     n2 = -1;
                     if (pixels == null) {
                        break label136;
                     }
                  } catch (Throwable var15) {
                     if (pixels != null) {
                        MemoryUtil.memFree(pixels);
                     }

                     if (rasterizer != 0L) {
                        NanoSVG.nsvgDeleteRasterizer(rasterizer);
                     }

                     if (image != null) {
                        NanoSVG.nsvgDelete(image);
                     }

                     MemoryUtil.memFree(svgData);
                     MemoryUtil.memFree(units);
                     throw var15;
                  }

                  MemoryUtil.memFree(pixels);
                  break label136;
               }

               rasterizer = NanoSVG.nsvgCreateRasterizer();
               float scale = 64.0F / Math.max(image.width(), image.height());
               pixels = MemoryUtil.memAlloc(16384);
               NanoSVG.nsvgRasterize(rasterizer, image, 0.0F, 0.0F, scale, pixels, 64, 64, 256);
               int n = NanoVG.nvgCreateImageRGBA(ctx, 64, 64, 0, pixels);
               if (pixels != null) {
                  MemoryUtil.memFree(pixels);
               }

               if (rasterizer != 0L) {
                  NanoSVG.nsvgDeleteRasterizer(rasterizer);
               }

               if (image != null) {
                  NanoSVG.nsvgDelete(image);
               }

               MemoryUtil.memFree(svgData);
               MemoryUtil.memFree(units);
               return n;
            }

            if (rasterizer != 0L) {
               NanoSVG.nsvgDeleteRasterizer(rasterizer);
            }

            if (image != null) {
               NanoSVG.nsvgDelete(image);
            }

            MemoryUtil.memFree(svgData);
            MemoryUtil.memFree(units);
            var23 = n2;
         }

         return var23;
      } catch (Exception var17) {
         LarpClient.LOGGER.error("Failed to rasterize icon {}", resourcePath, var17);
         return -1;
      }
   }
}
