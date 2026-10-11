package com.larpclient.hud;

import com.larpclient.render.OverlayRenderer;
import com.larpclient.render.nanovg.NVGRenderer;
import net.minecraft.class_310;
import net.minecraft.class_408;

public final class HudDragController {
   private static boolean snappingEnabled = true;
   private static float gridSize = 5.0F;
   private static float snapDistance = 6.0F;
   private static HudComponent dragging;
   private static HudManager activeHud;
   private static float grabDx;
   private static float grabDy;
   private static float guideX = Float.NaN;
   private static float guideY = Float.NaN;

   private HudDragController() {
   }

   public static boolean isEditing() {
      return class_310.method_1551().field_1755 instanceof class_408;
   }

   public static boolean isDragging() {
      return dragging != null;
   }

   public static boolean snappingEnabled() {
      return snappingEnabled;
   }

   public static float gridSize() {
      return gridSize;
   }

   public static float snapDistance() {
      return snapDistance;
   }

   public static boolean tryHandleEditorControls() {
      float mx = OverlayRenderer.uiMouseX();
      float my = OverlayRenderer.uiMouseY();
      float uiWidth = OverlayRenderer.uiWidth();
      float y = 40.0F;
      float h = 24.0F;
      float gap = 6.0F;
      float snapW = 92.0F;
      float smallW = 28.0F;
      float valueW = 54.0F;
      float totalW = snapW + gap + smallW + valueW + smallW + gap + smallW + valueW + smallW;
      float x = (uiWidth - totalW) / 2.0F;
      if (hit(mx, my, x, y, snapW, h)) {
         snappingEnabled = !snappingEnabled;
         guideX = Float.NaN;
         guideY = Float.NaN;
         return true;
      } else {
         x += snapW + gap;
         if (hit(mx, my, x, y, smallW, h)) {
            snapDistance = Math.max(1.0F, snapDistance - 1.0F);
            return true;
         } else {
            x += smallW + valueW;
            if (hit(mx, my, x, y, smallW, h)) {
               snapDistance = Math.min(20.0F, snapDistance + 1.0F);
               return true;
            } else {
               x += smallW + gap;
               if (hit(mx, my, x, y, smallW, h)) {
                  gridSize = Math.max(1.0F, gridSize - 1.0F);
                  return true;
               } else {
                  x += smallW + valueW;
                  if (hit(mx, my, x, y, smallW, h)) {
                     gridSize = Math.min(20.0F, gridSize + 1.0F);
                     return true;
                  } else {
                     return false;
                  }
               }
            }
         }
      }
   }

   private static boolean hit(float mx, float my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   public static boolean tryStartDrag(HudManager hud) {
      if (tryHandleEditorControls()) {
         return true;
      } else {
         NVGRenderer vg = NVGRenderer.get();
         float mx = OverlayRenderer.uiMouseX();
         float my = OverlayRenderer.uiMouseY();

         for (HudManager.Placement p : hud.layout(vg, OverlayRenderer.uiWidth(), OverlayRenderer.uiHeight(), true)) {
            if (p.contains(mx, my)) {
               float scale = p.component().getScale();
               if (p.component().onEditClick((mx - p.x()) / scale, (my - p.y()) / scale)) {
                  return true;
               }

               dragging = p.component();
               activeHud = hud;
               grabDx = mx - p.x();
               grabDy = my - p.y();
               guideX = Float.NaN;
               guideY = Float.NaN;
               return true;
            }
         }

         return false;
      }
   }

   public static boolean tryResize(HudManager hud, double scrollY) {
      NVGRenderer vg = NVGRenderer.get();
      float mx = OverlayRenderer.uiMouseX();
      float my = OverlayRenderer.uiMouseY();

      for (HudManager.Placement p : hud.layout(vg, OverlayRenderer.uiWidth(), OverlayRenderer.uiHeight(), true)) {
         if (p.contains(mx, my)) {
            HudComponent component = p.component();
            component.setScale(component.getScale() + (float)scrollY * 0.06F);
            return true;
         }
      }

      return false;
   }

   public static void updateDrag(NVGRenderer vg) {
      if (dragging != null) {
         float uiWidth = OverlayRenderer.uiWidth();
         float uiHeight = OverlayRenderer.uiHeight();
         float scale = dragging.getScale();
         float w = dragging.measureWidth(vg) * scale;
         float h = dragging.measureHeight(vg) * scale;
         float x = OverlayRenderer.uiMouseX() - grabDx;
         float y = OverlayRenderer.uiMouseY() - grabDy;
         x = Math.clamp(x, 0.0F, Math.max(0.0F, uiWidth - w));
         y = Math.clamp(y, 0.0F, Math.max(0.0F, uiHeight - h));
         guideX = Float.NaN;
         guideY = Float.NaN;
         if (snappingEnabled) {
            float gridX = Math.round(x / gridSize) * gridSize;
            float gridY = Math.round(y / gridSize) * gridSize;
            float gridCapture = Math.max(1.0F, Math.min(snapDistance, gridSize * 0.45F));
            if (Math.abs(gridX - x) <= gridCapture) {
               x = gridX;
            }

            if (Math.abs(gridY - y) <= gridCapture) {
               y = gridY;
            }

            float[] screenX = new float[]{0.0F, (uiWidth - w) / 2.0F, uiWidth - w};
            float[] screenGuideX = new float[]{0.0F, uiWidth / 2.0F, uiWidth};

            for (int i = 0; i < screenX.length; i++) {
               if (Math.abs(x - screenX[i]) <= snapDistance) {
                  x = screenX[i];
                  guideX = screenGuideX[i];
                  break;
               }
            }

            float[] screenY = new float[]{0.0F, (uiHeight - h) / 2.0F, uiHeight - h};
            float[] screenGuideY = new float[]{0.0F, uiHeight / 2.0F, uiHeight};

            for (int ix = 0; ix < screenY.length; ix++) {
               if (Math.abs(y - screenY[ix]) <= snapDistance) {
                  y = screenY[ix];
                  guideY = screenGuideY[ix];
                  break;
               }
            }

            if (activeHud != null) {
               for (HudManager.Placement p : activeHud.layout(vg, uiWidth, uiHeight, true)) {
                  if (p.component() != dragging) {
                     float[] mineX = new float[]{x, x + w / 2.0F, x + w};
                     float[] otherX = new float[]{p.x(), p.x() + p.w() / 2.0F, p.x() + p.w()};
                     boolean snappedX = false;

                     for (int mi = 0; mi < mineX.length && !snappedX; mi++) {
                        for (float target : otherX) {
                           if (Math.abs(mineX[mi] - target) <= snapDistance) {
                              float offset = mi == 0 ? 0.0F : (mi == 1 ? w / 2.0F : w);
                              x = target - offset;
                              guideX = target;
                              snappedX = true;
                              break;
                           }
                        }
                     }

                     float[] mineY = new float[]{y, y + h / 2.0F, y + h};
                     float[] otherY = new float[]{p.y(), p.y() + p.h() / 2.0F, p.y() + p.h()};
                     boolean snappedY = false;

                     for (int mi = 0; mi < mineY.length && !snappedY; mi++) {
                        for (float targetx : otherY) {
                           if (Math.abs(mineY[mi] - targetx) <= snapDistance) {
                              float offset = mi == 0 ? 0.0F : (mi == 1 ? h / 2.0F : h);
                              y = targetx - offset;
                              guideY = targetx;
                              snappedY = true;
                              break;
                           }
                        }
                     }

                     if (snappedX && snappedY) {
                        break;
                     }
                  }
               }
            }
         }

         x = Math.clamp(x, 0.0F, Math.max(0.0F, uiWidth - w));
         y = Math.clamp(y, 0.0F, Math.max(0.0F, uiHeight - h));
         float freeW = Math.max(1.0F, uiWidth - w);
         float freeH = Math.max(1.0F, uiHeight - h);
         dragging.setPosition(x / freeW, y / freeH);
      }
   }

   public static float guideX() {
      return guideX;
   }

   public static float guideY() {
      return guideY;
   }

   public static void stopDrag() {
      dragging = null;
      activeHud = null;
      guideX = Float.NaN;
      guideY = Float.NaN;
   }

   public static HudComponent draggedComponent() {
      return dragging;
   }
}
