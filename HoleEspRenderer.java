package com.larpclient.render;

import com.larpclient.module.impl.DebugHoleEspModule;
import com.larpclient.util.Colors;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_4597.class_4598;
import org.joml.Vector3fc;

public final class HoleEspRenderer {
   private static final int SCAN_INTERVAL_TICKS = 10;
   private static final int RADIUS = 16;
   private static final int VERTICAL = 6;
   private static final Set<class_2248> UNBREAKABLE = Set.of(
      class_2246.field_10540, class_2246.field_22423, class_2246.field_9987, class_2246.field_23152, class_2246.field_38420
   );
   private static volatile List<HoleEspRenderer.Hole> cache = List.of();
   private static int tickCounter;

   private HoleEspRenderer() {
   }

   public static void clear() {
      cache = List.of();
      tickCounter = 0;
   }

   public static void scan(DebugHoleEspModule module) {
      if (tickCounter++ % 10 == 0) {
         class_310 mc = class_310.method_1551();
         class_638 level = mc.field_1687;
         class_746 player = mc.field_1724;
         if (level != null && player != null) {
            int need = module.depth.is("Any") ? 1 : Integer.parseInt(module.depth.get());
            class_2338 origin = player.method_24515();
            class_2339 p = new class_2339();
            ArrayList<HoleEspRenderer.Hole> found = new ArrayList<>();

            for (int dx = -16; dx <= 16; dx++) {
               for (int dz = -16; dz <= 16; dz++) {
                  for (int dy = -6; dy <= 6; dy++) {
                     int x = origin.method_10263() + dx;
                     int z;
                     int y;
                     if (isHole(level, p, x, y = origin.method_10264() + dy, z = origin.method_10260() + dz, need)) {
                        found.add(new HoleEspRenderer.Hole(x, y, z, wallsSafe(level, p, x, y, z)));
                     }
                  }
               }
            }

            cache = found;
         }
      }
   }

   public static void render(class_4598 bufferSource, class_4587 poseStack, class_243 cam, DebugHoleEspModule module) {
      List<HoleEspRenderer.Hole> snapshot = cache;
      if (!snapshot.isEmpty()) {
         int safeColor = module.safe.get();
         int unsafeColor = module.unsafe.get();
         boolean tracers = module.tracers.get();
         Vector3fc forward = tracers ? class_310.method_1551().field_1773.method_19418().method_19335() : null;

         for (HoleEspRenderer.Hole hole : snapshot) {
            int color = hole.safe() ? safeColor : unsafeColor;
            EspBoxRenderer.outline(bufferSource, poseStack, cam, hole.x(), hole.y(), hole.z(), hole.x() + 1, hole.y() + 1, hole.z() + 1, color, 2.0F);
            if (tracers) {
               EspBoxRenderer.tracer(
                  bufferSource, poseStack, cam, forward, hole.x() + 0.5, hole.y() + 0.5, hole.z() + 0.5, Colors.withAlpha(color, 0.72F), 1.2F
               );
            }
         }

         EspBoxRenderer.flush(bufferSource);
      }
   }

   private static boolean isHole(class_638 level, class_2339 p, int x, int y, int z, int need) {
      if (!level.method_8320(p.method_10103(x, y - 1, z)).method_51366()) {
         return false;
      } else {
         for (int i = 0; i < need; i++) {
            if (!level.method_8320(p.method_10103(x, y + i, z)).method_26215()) {
               return false;
            }
         }

         return level.method_8320(p.method_10103(x + 1, y, z)).method_51366()
            && level.method_8320(p.method_10103(x - 1, y, z)).method_51366()
            && level.method_8320(p.method_10103(x, y, z + 1)).method_51366()
            && level.method_8320(p.method_10103(x, y, z - 1)).method_51366();
      }
   }

   private static boolean wallsSafe(class_638 level, class_2339 p, int x, int y, int z) {
      return isUnbreakable(level.method_8320(p.method_10103(x, y - 1, z)))
         && isUnbreakable(level.method_8320(p.method_10103(x + 1, y, z)))
         && isUnbreakable(level.method_8320(p.method_10103(x - 1, y, z)))
         && isUnbreakable(level.method_8320(p.method_10103(x, y, z + 1)))
         && isUnbreakable(level.method_8320(p.method_10103(x, y, z - 1)));
   }

   private static boolean isUnbreakable(class_2680 state) {
      return UNBREAKABLE.contains(state.method_26204());
   }

   private record Hole(int x, int y, int z, boolean safe) {
   }
}
