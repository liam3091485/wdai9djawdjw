package com.larpclient.render;

import com.larpclient.LarpClient;
import com.larpclient.hud.HudDragController;
import com.larpclient.hud.HudManager;
import com.larpclient.module.ModuleManager;
import com.larpclient.module.impl.CustomCrosshairModule;
import com.larpclient.module.impl.MotionBlurModule;
import com.larpclient.notification.NotificationManager;
import com.larpclient.render.nanovg.GlStateSnapshot;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.theme.Theme;
import com.larpclient.util.Colors;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_10868;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_408;
import org.lwjgl.opengl.GL33C;

public final class OverlayRenderer {
   private static HudManager hudManager;
   private static NotificationManager notifications;
   private static int fbo = -1;
   private static boolean crashed;

   private OverlayRenderer() {
   }

   public static void init(HudManager hud, NotificationManager toasts) {
      hudManager = hud;
      notifications = toasts;
   }

   public static float uiScale() {
      class_310 mc = class_310.method_1551();
      return Math.max(1.0F, mc.method_22683().method_4506() / 1080.0F);
   }

   public static void render() {
      if (!crashed && hudManager != null) {
         class_310 mc = class_310.method_1551();
         class_276 target = mc.method_1522();
         GpuTexture gpuTexture;
         if (target != null && (gpuTexture = target.method_30277()) instanceof class_10868) {
            class_10868 colorTexture = (class_10868)gpuTexture;
            boolean guiScreen = mc.field_1755 instanceof NvgDrawable;
            boolean chatEditing = mc.field_1755 instanceof class_408;
            ModuleManager moduleManager = LarpClient.modules();
            boolean overlayFeature = hudManager.hasVisibleComponents();
            if (moduleManager != null) {
               overlayFeature = overlayFeature
                  || moduleManager.customCrosshair != null && moduleManager.customCrosshair.isEnabled()
                  || moduleManager.nameTags != null && moduleManager.nameTags.isEnabled()
                  || moduleManager.spawnerNametags != null && moduleManager.spawnerNametags.isEnabled();
            }

            boolean drawHud = !mc.field_1690.field_1842 && mc.field_1687 != null && !guiScreen && (overlayFeature || chatEditing);
            boolean drawToasts = notifications != null && notifications.hasActiveToasts();
            MotionBlurModule mbModule = moduleManager != null ? moduleManager.motionBlur : null;
            boolean motionBlur = mbModule != null && mbModule.isEnabled() && mc.field_1687 != null && !guiScreen;
            if (guiScreen || drawHud || drawToasts || motionBlur) {
               try {
                  GlStateSnapshot state = GlStateSnapshot.capture();

                  try {
                     if (!bindOverlayFbo(colorTexture, target.field_1482, target.field_1481)) {
                        return;
                     }

                     NVGRenderer vg = NVGRenderer.get();
                     if (motionBlur) {
                        MotionBlurRenderer.render(vg, target.field_1482, target.field_1481, mbModule);
                     }

                     vg.setFontMode(LarpClient.modules().clickGui.font.get());
                     if (vg.hasFont()) {
                        float scale = uiScale();
                        float uiWidth = target.field_1482 / scale;
                        float uiHeight = target.field_1481 / scale;
                        vg.beginFrame(target.field_1482, target.field_1481, 1.0F);
                        vg.save();
                        vg.scale(scale);
                        if (drawHud) {
                           if (chatEditing && HudDragController.isDragging()) {
                              HudDragController.updateDrag(vg);
                           }

                           CustomCrosshairModule crosshair;
                           if ((crosshair = LarpClient.modules().customCrosshair).isEnabled() && mc.field_1755 == null) {
                              crosshair.render(vg, uiWidth / 2.0F, uiHeight / 2.0F);
                           }

                           if (mc.field_1755 == null) {
                              WorldNametagRenderer.render(vg);
                           }

                           hudManager.render(vg, uiWidth, uiHeight);
                           if (chatEditing) {
                              renderChatEditOverlay(vg, uiWidth, uiHeight);
                           }
                        }

                        if (drawToasts) {
                           notifications.renderToasts(vg, uiWidth, uiHeight);
                        }

                        if (guiScreen) {
                           ((NvgDrawable)mc.field_1755).renderNvg(vg, uiMouseX(), uiMouseY(), uiWidth, uiHeight);
                        }

                        vg.restore();
                        vg.endFrame();
                        return;
                     }
                  } finally {
                     state.restore();
                  }
               } catch (Throwable var23) {
                  crashed = true;
                  LarpClient.LOGGER.error("LARP Client overlay renderer crashed; disabling overlay", var23);
               }
            }
         }
      }
   }

   private static void renderChatEditOverlay(NVGRenderer vg, float uiWidth, float uiHeight) {
      Theme theme = LarpClient.themes().current();
      float mx = uiMouseX();
      float my = uiMouseY();
      String hint = "LARP HUD Editor  •  Drag to move  •  Scroll to scale";
      vg.text(hint, (uiWidth - vg.textWidth(hint, 14.0F)) / 2.0F, 20.0F, 14.0F, Colors.withAlpha(theme.textMuted(), 0.9F));
      float cy = 40.0F;
      float ch = 24.0F;
      float gap = 6.0F;
      float snapW = 92.0F;
      float smallW = 28.0F;
      float valueW = 54.0F;
      float totalW = snapW + gap + smallW + valueW + smallW + gap + smallW + valueW + smallW;
      float cx = (uiWidth - totalW) / 2.0F;
      int panel = Colors.withAlpha(-16314342, 0.94F);
      int controlOutline = Colors.withAlpha(theme.accent(), 0.55F);
      int controlActive = theme.accentBright();
      vg.rect(cx, cy, snapW, ch, 3.0F, HudDragController.snappingEnabled() ? Colors.withAlpha(theme.accent(), 0.22F) : panel);
      vg.rectOutline(cx, cy, snapW, ch, 3.0F, 1.0F, HudDragController.snappingEnabled() ? controlActive : controlOutline);
      String snapText = HudDragController.snappingEnabled() ? "SNAP: ON" : "SNAP: OFF";
      vg.text(
         snapText,
         cx + (snapW - vg.textWidth(snapText, 11.5F)) / 2.0F,
         cy + ch / 2.0F,
         11.5F,
         HudDragController.snappingEnabled() ? controlActive : theme.textMuted()
      );
      cx += snapW + gap;
      vg.rect(cx, cy, smallW, ch, 3.0F, panel);
      vg.rectOutline(cx, cy, smallW, ch, 3.0F, 1.0F, controlOutline);
      vg.text("-", cx + 10.0F, cy + ch / 2.0F, 13.0F, theme.textPrimary());
      cx += smallW;
      vg.rect(cx, cy, valueW, ch, 3.0F, panel);
      vg.rectOutline(cx, cy, valueW, ch, 3.0F, 1.0F, controlOutline);
      String distText = "DIST " + Math.round(HudDragController.snapDistance());
      vg.text(distText, cx + (valueW - vg.textWidth(distText, 10.5F)) / 2.0F, cy + ch / 2.0F, 10.5F, theme.textMuted());
      cx += valueW;
      vg.rect(cx, cy, smallW, ch, 3.0F, panel);
      vg.rectOutline(cx, cy, smallW, ch, 3.0F, 1.0F, controlOutline);
      vg.text("+", cx + 8.0F, cy + ch / 2.0F, 13.0F, theme.textPrimary());
      cx += smallW + gap;
      vg.rect(cx, cy, smallW, ch, 3.0F, panel);
      vg.rectOutline(cx, cy, smallW, ch, 3.0F, 1.0F, controlOutline);
      vg.text("-", cx + 10.0F, cy + ch / 2.0F, 13.0F, theme.textPrimary());
      cx += smallW;
      vg.rect(cx, cy, valueW, ch, 3.0F, panel);
      vg.rectOutline(cx, cy, valueW, ch, 3.0F, 1.0F, controlOutline);
      String gridText = "GRID " + Math.round(HudDragController.gridSize());
      vg.text(gridText, cx + (valueW - vg.textWidth(gridText, 10.5F)) / 2.0F, cy + ch / 2.0F, 10.5F, theme.textMuted());
      cx += valueW;
      vg.rect(cx, cy, smallW, ch, 3.0F, panel);
      vg.rectOutline(cx, cy, smallW, ch, 3.0F, 1.0F, controlOutline);
      vg.text("+", cx + 8.0F, cy + ch / 2.0F, 13.0F, theme.textPrimary());
      if (HudDragController.isDragging()) {
         float gx = HudDragController.guideX();
         float gy = HudDragController.guideY();
         int guide = Colors.withAlpha(theme.accentBright(), 0.55F);
         if (!Float.isNaN(gx)) {
            vg.line(gx, 34.0F, gx, uiHeight, 1.0F, guide);
         }

         if (!Float.isNaN(gy)) {
            vg.line(0.0F, gy, uiWidth, gy, 1.0F, guide);
         }
      }

      for (HudManager.Placement p : hudManager.layout(vg, uiWidth, uiHeight, true)) {
         boolean hidden = !p.component().visible();
         boolean active = p.contains(mx, my) || p.component() == HudDragController.draggedComponent();
         if (hidden) {
            vg.save();
            vg.alpha(0.35F);
            hudManager.renderPlacement(vg, p);
            vg.restore();
         }

         int outline = active ? theme.accentBright() : Colors.withAlpha(theme.accent(), 0.5F);
         vg.rectOutline(p.x() - 3.0F, p.y() - 3.0F, p.w() + 6.0F, p.h() + 6.0F, 6.0F, active ? 1.6F : 1.0F, outline);
         vg.rect(p.x() + p.w() - 2.0F, p.y() + p.h() - 2.0F, 6.0F, 6.0F, 2.0F, outline);
         if (active) {
            String pct = Math.round(p.component().getScale() * 100.0F) + "%";
            vg.text(pct, p.x() + p.w() + 6.0F, p.y() + p.h() / 2.0F, 11.0F, theme.accentBright());
         }
      }
   }

   private static boolean bindOverlayFbo(class_10868 colorTexture, int width, int height) {
      if (fbo == -1) {
         fbo = GL33C.glGenFramebuffers();
      }

      GL33C.glBindFramebuffer(36160, fbo);
      GL33C.glFramebufferTexture2D(36160, 36064, 3553, colorTexture.method_68427(), 0);
      if (GL33C.glCheckFramebufferStatus(36160) != 36053) {
         return false;
      } else {
         GL33C.glViewport(0, 0, width, height);
         GL33C.glDisable(3089);
         return true;
      }
   }

   public static float uiMouseX() {
      class_310 mc = class_310.method_1551();
      return (float)(mc.field_1729.method_1603() * mc.method_22683().method_4489() / Math.max(1, mc.method_22683().method_4480())) / uiScale();
   }

   public static float uiMouseY() {
      class_310 mc = class_310.method_1551();
      return (float)(mc.field_1729.method_1604() * mc.method_22683().method_4506() / Math.max(1, mc.method_22683().method_4507())) / uiScale();
   }

   public static float guiToUi(double guiCoord) {
      class_310 mc = class_310.method_1551();
      float pixels = (float)(guiCoord * mc.method_22683().method_4495());
      return pixels / uiScale();
   }

   public static float uiWidth() {
      class_310 mc = class_310.method_1551();
      return mc.method_22683().method_4489() / uiScale();
   }

   public static float uiHeight() {
      class_310 mc = class_310.method_1551();
      return mc.method_22683().method_4506() / uiScale();
   }
}
