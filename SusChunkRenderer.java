package com.larpclient.render;

import com.larpclient.module.Modules;
import com.larpclient.suschunk.SusChunkScanner;
import com.larpclient.util.Colors;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1923;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4597.class_4598;

public final class SusChunkRenderer {
   private static final float FADE_IN_SECONDS = 0.3F;
   private static final float FADE_OUT_SECONDS = 0.45F;
   private static final float MOVE_RATE = 7.0F;
   private static final Map<Long, SusChunkRenderer.ChunkFade> chunkFades = new HashMap<>();
   private static final Map<Long, SusChunkRenderer.ZoneFade> zoneFades = new HashMap<>();
   private static long lastFrameNanos;

   private SusChunkRenderer() {
   }

   public static void reset() {
      chunkFades.clear();
      zoneFades.clear();
   }

   public static String debugState() {
      StringBuilder sb = new StringBuilder();

      for (Entry<Long, SusChunkRenderer.ChunkFade> entry : chunkFades.entrySet()) {
         SusChunkRenderer.ChunkFade fade = entry.getValue();
         sb.append(new class_1923(entry.getKey()))
            .append("{a=")
            .append(fade.alpha)
            .append(",t=")
            .append(fade.tier)
            .append(",f=")
            .append(fade.flagged)
            .append("} ");
      }

      for (Entry<Long, SusChunkRenderer.ZoneFade> entry : zoneFades.entrySet()) {
         SusChunkRenderer.ZoneFade zone = entry.getValue();
         sb.append("zone")
            .append(new class_1923(entry.getKey()))
            .append("{a=")
            .append(zone.alpha)
            .append(",s=")
            .append(zone.size)
            .append(",f=")
            .append(zone.flagged)
            .append("} ");
      }

      return sb.toString();
   }

   public static void render(class_4598 bufferSource, class_4587 poseStack, class_243 camera, Modules.SusChunkFinderModule module) {
      SusChunkScanner scanner = module.scanner;
      double y = module.renderY.get();
      int accent = -16734209;
      float fillAlpha = module.fillOpacity.getFloat() / 255.0F;
      float outlineAlpha = module.outlineOpacity.getFloat() / 255.0F;
      boolean outline = module.outline.get();
      boolean smart = module.smartMode.get();
      int threshold = scanner.threshold();
      updateTargets(scanner, smart, threshold);
      float dt = frameDelta();
      float move = 1.0F - (float)Math.exp(-dt * 7.0F);
      Iterator<Entry<Long, SusChunkRenderer.ChunkFade>> chunks = chunkFades.entrySet().iterator();

      while (chunks.hasNext()) {
         Entry<Long, SusChunkRenderer.ChunkFade> entry = chunks.next();
         SusChunkRenderer.ChunkFade fade = entry.getValue();
         fade.alpha = fade.alpha + (fade.flagged ? dt / 0.3F : -dt / 0.45F);
         fade.alpha = Math.clamp(fade.alpha, 0.0F, 1.0F);
         if (!fade.flagged && fade.alpha <= 0.0F) {
            chunks.remove();
         } else {
            double x0 = class_1923.method_8325(entry.getKey()) * 16.0;
            double z0 = class_1923.method_8332(entry.getKey()) * 16.0;
            drawQuad(
               bufferSource,
               poseStack,
               camera,
               x0,
               z0,
               x0 + 16.0,
               z0 + 16.0,
               y,
               blueForTier(fade.tier),
               fillAlpha * (0.7F + 0.3F * fade.tier) * fade.alpha,
               outline ? outlineAlpha * fade.alpha : 0.0F
            );
         }
      }

      Iterator<Entry<Long, SusChunkRenderer.ZoneFade>> zones = zoneFades.entrySet().iterator();

      while (zones.hasNext()) {
         SusChunkRenderer.ZoneFade zone = zones.next().getValue();
         zone.alpha = zone.alpha + (zone.flagged ? dt / 0.3F : -dt / 0.45F);
         zone.alpha = Math.clamp(zone.alpha, 0.0F, 1.0F);
         if (!zone.flagged && zone.alpha <= 0.0F) {
            zones.remove();
         } else {
            zone.centerX = zone.centerX + (zone.targetX - zone.centerX) * move;
            zone.centerZ = zone.centerZ + (zone.targetZ - zone.centerZ) * move;
            zone.size = zone.size + (zone.targetSize - zone.size) * move;
            double half = zone.size / 2.0;
            drawQuad(
               bufferSource,
               poseStack,
               camera,
               zone.centerX - half,
               zone.centerZ - half,
               zone.centerX + half,
               zone.centerZ + half,
               y,
               blueForTier(zone.tier),
               fillAlpha * (0.7F + 0.3F * zone.tier) * zone.alpha,
               outline ? outlineAlpha * zone.alpha : 0.0F
            );
            if (module.centroidMarker.get()) {
               FlatOverlay.marker(bufferSource, poseStack, camera, zone.centerX, zone.centerZ, y + 0.05, 2.4, Colors.withAlpha(-13058568, 0.98F * zone.alpha));
            }
         }
      }

      FlatOverlay.flush(bufferSource);
   }

   private static void drawQuad(
      class_4598 bufferSource,
      class_4587 poseStack,
      class_243 camera,
      double x0,
      double z0,
      double x1,
      double z1,
      double y,
      int accent,
      float fill,
      float border
   ) {
      FlatOverlay.fillQuad(bufferSource, poseStack, camera, x0, z0, x1, z1, y, Colors.withAlpha(accent, fill));
      if (border > 0.004F) {
         int color = Colors.withAlpha(accent, border);
         FlatOverlay.edge(bufferSource, poseStack, camera, x0, z0, x1, z0, y, color, 2.5F);
         FlatOverlay.edge(bufferSource, poseStack, camera, x0, z1, x1, z1, y, color, 2.5F);
         FlatOverlay.edge(bufferSource, poseStack, camera, x0, z0, x0, z1, y, color, 2.5F);
         FlatOverlay.edge(bufferSource, poseStack, camera, x1, z0, x1, z1, y, color, 2.5F);
      }
   }

   private static void updateTargets(SusChunkScanner scanner, boolean smart, int threshold) {
      for (SusChunkRenderer.ChunkFade chunkFade : chunkFades.values()) {
         chunkFade.flagged = false;
      }

      for (SusChunkRenderer.ZoneFade zoneFade : zoneFades.values()) {
         zoneFade.flagged = false;
      }

      if (smart) {
         for (SusChunkScanner.Zone zone : scanner.zones()) {
            if (zone.members().size() == 1) {
               long key = zone.members().iterator().next();
               SusChunkRenderer.ChunkFade chunkFade = chunkFades.computeIfAbsent(key, k -> new SusChunkRenderer.ChunkFade());
               chunkFade.flagged = true;
               chunkFade.tier = confidence(zone.maxScore(), threshold);
            } else {
               long key = Long.MAX_VALUE;

               for (long member : zone.members()) {
                  key = Math.min(key, member);
               }

               SusChunkRenderer.ZoneFade fade = zoneFades.get(key);
               if (fade == null) {
                  fade = new SusChunkRenderer.ZoneFade(zone.centroidX(), zone.centroidZ());
                  zoneFades.put(key, fade);
               }

               fade.flagged = true;
               fade.targetX = zone.centroidX();
               fade.targetZ = zone.centroidZ();
               fade.targetSize = Math.min(48.0F, 16.0F + (zone.members().size() - 1) * 8.0F);
               fade.tier = confidence(zone.maxScore(), threshold);
            }
         }
      } else {
         for (SusChunkScanner.Flag flag : scanner.flags()) {
            SusChunkRenderer.ChunkFade fade = chunkFades.computeIfAbsent(flag.chunkKey(), k -> new SusChunkRenderer.ChunkFade());
            fade.flagged = true;
            fade.tier = 1.0F;
         }
      }
   }

   private static int blueForTier(float tier) {
      float t = Math.clamp(tier, 0.0F, 1.0F);
      int low = -16750849;
      int high = -10561537;
      int r = (int)((low >> 16 & 0xFF) + ((high >> 16 & 0xFF) - (low >> 16 & 0xFF)) * t);
      int g = (int)((low >> 8 & 0xFF) + ((high >> 8 & 0xFF) - (low >> 8 & 0xFF)) * t);
      int b = (int)((low & 0xFF) + ((high & 0xFF) - (low & 0xFF)) * t);
      return 0xFF000000 | r << 16 | g << 8 | b;
   }

   private static float confidence(double score, int threshold) {
      if (threshold <= 0) {
         return 1.0F;
      } else {
         float over = (float)((score - threshold) / (threshold * 2.0));
         return 0.55F + 0.45F * Math.clamp(over, 0.0F, 1.0F);
      }
   }

   private static float frameDelta() {
      long now = System.nanoTime();
      float dt = lastFrameNanos == 0L ? 0.016F : (float)(now - lastFrameNanos) / 1.0E9F;
      lastFrameNanos = now;
      return Math.min(dt, 0.1F);
   }

   private static final class ChunkFade {
      float alpha;
      float tier = 1.0F;
      boolean flagged;
   }

   private static final class ZoneFade {
      double centerX;
      double centerZ;
      double targetX;
      double targetZ;
      float size = 16.0F;
      float targetSize = 16.0F;
      float alpha;
      float tier = 1.0F;
      boolean flagged;

      ZoneFade(double x, double z) {
         this.centerX = this.targetX = x;
         this.centerZ = this.targetZ = z;
      }
   }
}
