package com.larpclient.render;

import com.larpclient.module.impl.StorageEspModule;
import java.util.List;
import net.minecraft.class_2248;
import net.minecraft.class_2260;
import net.minecraft.class_2281;
import net.minecraft.class_2315;
import net.minecraft.class_2336;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2363;
import net.minecraft.class_2377;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2496;
import net.minecraft.class_2531;
import net.minecraft.class_2680;
import net.minecraft.class_2745;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_3708;
import net.minecraft.class_4587;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_4597.class_4598;
import org.joml.Vector3fc;

public final class StorageEspRenderer {
   private static final int MAX_CHUNK_RADIUS = 16;
   private static final int MAX_RESULTS = 6000;
   private static final float BOX_INFLATE = 0.002F;
   private static final double CHEST_INSET = 0.0625;
   private static final int INTERACTED_RGB = 6579300;
   private static final float LINE_WIDTH = 1.0F;
   private static final float TRACER_WIDTH = 1.15F;
   private static final int TRACER_ALPHA = 180;
   private static final IncrementalScan<StorageEspRenderer.Hit> SCAN = new IncrementalScan<>(48, 80000, 20);

   private StorageEspRenderer() {
   }

   public static void clear() {
      SCAN.clear();
   }

   public static int cachedCount() {
      return SCAN.get().size();
   }

   public static long cachedShulkerCount() {
      return SCAN.get().stream().filter(h -> h.type() == StorageEspModule.StorageType.SHULKER).count();
   }

   public static void scan(StorageEspModule module) {
      double range = module.range.get();
      double rangeSq = range * range;
      int wanted = (int)Math.ceil(range / 16.0) + 1;
      int chunkRadius = Math.min(Math.min(16, wanted), (Integer)class_310.method_1551().field_1690.method_42503().method_41753());
      SCAN.tick(chunkRadius, (chunk, out) -> scanChunk(chunk, rangeSq, out));
   }

   private static int scanChunk(class_2818 chunk, double rangeSq, List<StorageEspRenderer.Hit> out) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return 0;
      } else {
         class_2826[] sections = chunk.method_12006();
         int minSectionY = chunk.method_32891();
         int baseX = chunk.method_12004().method_8326();
         int baseZ = chunk.method_12004().method_8328();
         int blocks = 0;
         if (out.size() >= 6000) {
            return 0;
         } else {
            for (int s = 0; s < sections.length; s++) {
               class_2826 section = sections[s];
               if (!section.method_38292() && section.method_19523(StorageEspRenderer::isStorage)) {
                  int baseY = minSectionY + s << 4;
                  blocks += 4096;

                  for (int y = 0; y < 16; y++) {
                     for (int z = 0; z < 16; z++) {
                        for (int x = 0; x < 16; x++) {
                           StorageEspModule.StorageType type = classify(section.method_12254(x, y, z).method_26204());
                           int wz;
                           int wy;
                           int wx;
                           if (type != null && !(player.method_5649((wx = baseX + x) + 0.5, (wy = baseY + y) + 0.5, (wz = baseZ + z) + 0.5) > rangeSq)) {
                              out.add(new StorageEspRenderer.Hit(wx, wy, wz, type));
                              if (out.size() >= 6000) {
                                 return blocks;
                              }
                           }
                        }
                     }
                  }
               }
            }

            return blocks;
         }
      }
   }

   public static void render(class_4598 bufferSource, class_4587 poseStack, class_243 cam, StorageEspModule module) {
      List<StorageEspRenderer.Hit> snapshot = SCAN.get();
      if (!snapshot.isEmpty()) {
         class_310 mc = class_310.method_1551();
         class_638 level = mc.field_1687;
         if (level != null) {
            boolean fill = module.mode.is("Full");
            int alphaBits = Math.max(0, Math.min(255, module.highlightAlpha.getInt())) << 24;
            boolean tracers = module.tracers.get();
            boolean hideOpened = module.hideOpened();
            Vector3fc forward = mc.field_1773.method_19418().method_19335();

            for (StorageEspRenderer.Hit hit : snapshot) {
               StorageEspModule.StorageType type = hit.type();
               boolean interacted;
               if (module.isTypeEnabled(type) && (!(interacted = module.isInteracted(hit.x(), hit.y(), hit.z())) || !hideOpened)) {
                  int rgb = interacted ? 6579300 : module.colorFor(type) & 16777215;
                  int color = alphaBits | rgb;
                  double x1 = hit.x();
                  double y1 = hit.y();
                  double z1 = hit.z();
                  double x2 = x1 + 1.0;
                  double y2 = y1 + 1.0;
                  double z2 = z1 + 1.0;
                  if (type == StorageEspModule.StorageType.CHEST || type == StorageEspModule.StorageType.TRAPPED || type == StorageEspModule.StorageType.ENDER) {
                     x1 += 0.0625;
                     z1 += 0.0625;
                     x2 -= 0.0625;
                     y2 -= 0.125;
                     z2 -= 0.0625;
                     class_2680 st;
                     if ((type == StorageEspModule.StorageType.CHEST || type == StorageEspModule.StorageType.TRAPPED)
                        && (st = level.method_8320(new class_2338(hit.x(), hit.y(), hit.z()))).method_26204() instanceof class_2281
                        && st.method_11654(class_2281.field_10770) != class_2745.field_12569) {
                        class_2350 facing = (class_2350)st.method_11654(class_2281.field_10768);
                        class_2745 ct = (class_2745)st.method_11654(class_2281.field_10770);
                        class_2350 nb = ct == class_2745.field_12574 ? facing.method_10170() : facing.method_10160();
                        if (nb == class_2350.field_11039) {
                           x1 = hit.x();
                        } else if (nb == class_2350.field_11034) {
                           x2 = hit.x() + 1;
                        } else if (nb == class_2350.field_11043) {
                           z1 = hit.z();
                        } else if (nb == class_2350.field_11035) {
                           z2 = hit.z() + 1;
                        }
                     }
                  }

                  if (fill) {
                     EspBoxRenderer.fill(bufferSource, poseStack, cam, x1 - 0.002F, y1 - 0.002F, z1 - 0.002F, x2 + 0.002F, y2 + 0.002F, z2 + 0.002F, color);
                  } else {
                     EspBoxRenderer.outline(bufferSource, poseStack, cam, x1, y1, z1, x2, y2, z2, color, 1.0F);
                  }

                  if (tracers) {
                     int tracerColor = -1275068416 | rgb;
                     EspBoxRenderer.tracer(bufferSource, poseStack, cam, forward, (x1 + x2) / 2.0, (y1 + y2) / 2.0, (z1 + z2) / 2.0, tracerColor, 1.15F);
                  }
               }
            }

            EspBoxRenderer.flush(bufferSource);
         }
      }
   }

   private static boolean isStorage(class_2680 state) {
      return classify(state.method_26204()) != null;
   }

   private static StorageEspModule.StorageType classify(class_2248 b) {
      if (b instanceof class_2531) {
         return StorageEspModule.StorageType.TRAPPED;
      } else if (b instanceof class_2281) {
         return StorageEspModule.StorageType.CHEST;
      } else if (b instanceof class_2336) {
         return StorageEspModule.StorageType.ENDER;
      } else if (b instanceof class_2480) {
         return StorageEspModule.StorageType.SHULKER;
      } else if (b instanceof class_3708) {
         return StorageEspModule.StorageType.BARREL;
      } else if (b instanceof class_2496) {
         return StorageEspModule.StorageType.SPAWNER;
      } else if (b instanceof class_2377) {
         return StorageEspModule.StorageType.HOPPER;
      } else if (b instanceof class_2363) {
         return StorageEspModule.StorageType.FURNACE;
      } else if (b instanceof class_2260) {
         return StorageEspModule.StorageType.FURNACE;
      } else {
         return b instanceof class_2315 ? StorageEspModule.StorageType.HOPPER : null;
      }
   }

   private record Hit(int x, int y, int z, StorageEspModule.StorageType type) {
   }
}
