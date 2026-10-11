package com.larpclient.hud.components;

import com.larpclient.hud.HudComponent;
import com.larpclient.module.impl.RegionMapModule;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class RegionMapHud extends HudComponent {
   private static final float PAD = 8.0F;
   private static final float CELL = 12.0F;
   private static final float GRID = 108.0F;
   private static final float HEADER_H = 15.0F;
   private static final float GAP_HEADER = 5.0F;
   private static final float GAP_LEGEND = 8.0F;
   private static final float LEGEND_ROW = 12.0F;
   private static final int LEGEND_COLS = 2;
   private final RegionMapModule module;
   private final ThemeManager themes;

   public RegionMapHud(RegionMapModule module, ThemeManager themes) {
      super("regionMap", 0.008F, 0.05F, module::isEnabled);
      this.module = module;
      this.themes = themes;
   }

   private float legendRows() {
      return (float)Math.ceil(this.module.regionTypeCount() / 2.0);
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 124.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      float h = 144.0F;
      if (this.module.legend.get()) {
         h += 8.0F + this.legendRows() * 12.0F;
      }

      return h;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         float radius = 10.0F;
         vg.glow(x, y, w, h, 13.0F, 8.0F, Colors.withAlpha(-16777216, 0.3F));
         vg.rectGradient(x, y, w, h, 0.0F, theme.background(), theme.backgroundTo(), true);
         int currentId = this.module.currentRegionId();
         int currentType = this.module.regionTypeAtWorld(player.method_23317(), player.method_23321());
         this.drawHeader(vg, theme, x, y, w, currentId);
         float gridX = x + 8.0F;
         float gridY = y + 8.0F + 15.0F + 5.0F;
         this.drawGrid(vg, theme, player, gridX, gridY);
         if (this.module.legend.get()) {
            this.drawLegend(vg, theme, gridX, gridY + 108.0F + 8.0F, currentType);
         }
      }
   }

   private void drawHeader(NVGRenderer vg, Theme theme, float x, float y, float w, int currentId) {
      float cy = y + 8.0F + 7.5F;
      vg.text("REGION MAP", x + 8.0F, cy, 9.0F, theme.textPrimary());
      Object reg = currentId >= 0 ? "#" + currentId : "N/A";
      float rw = vg.textWidth((String)reg, 8.5F);
      float bw = rw + 9.0F;
      float bh = 12.5F;
      float bx = x + w - 8.0F - bw;
      float by = cy - bh / 2.0F;
      boolean on = currentId >= 0;
      vg.rect(bx, by, bw, bh, 6.0F, Colors.withAlpha(theme.accent(), on ? 0.18F : 0.1F));
      vg.rectOutline(bx, by, bw, bh, 6.0F, 1.0F, Colors.withAlpha(theme.accent(), on ? 0.45F : 0.2F));
      vg.text((String)reg, bx + 4.5F, cy, 8.5F, on ? theme.accentBright() : theme.textMuted());
   }

   private void drawGrid(NVGRenderer vg, Theme theme, class_746 player, float gridX, float gridY) {
      int n = this.module.mapSize();
      int alpha = (int)(Math.clamp(this.module.opacity.getFloat() / 100.0F, 0.0F, 1.0F) * 255.0F);
      vg.rect(gridX, gridY, 108.0F, 108.0F, 4.0F, Colors.withAlpha(-16054000, 0.92F));
      vg.save();
      vg.scissor(gridX, gridY, 108.0F, 108.0F);

      for (int row = 0; row < n; row++) {
         for (int col = 0; col < n; col++) {
            int type = this.module.regionTypeAt(row * n + col);
            if (type >= 0) {
               int argb = Colors.withAlpha(0xFF000000 | this.module.regionTypeRgb(type), alpha);
               float cx = gridX + col * 12.0F;
               float cyc = gridY + row * 12.0F;
               vg.rect(cx + 1.0F, cyc + 1.0F, 10.0F, 10.0F, 1.5F, argb);
            }
         }
      }

      if (this.module.gridLines.get()) {
         int line = Colors.withAlpha(theme.accent(), 0.14F);

         for (int i = 0; i <= n; i++) {
            float p = i * 12.0F;
            vg.line(gridX + p, gridY, gridX + p, gridY + 108.0F, 1.0F, line);
            vg.line(gridX, gridY + p, gridX + 108.0F, gridY + p, 1.0F, line);
         }
      }

      int[] g;
      boolean onMap = (g = this.module.worldToGrid(player.method_23317(), player.method_23321()))[0] >= 0 && g[0] < n && g[1] >= 0 && g[1] < n;
      if (onMap) {
         float cx = gridX + g[0] * 12.0F;
         float cyc = gridY + g[1] * 12.0F;
         vg.glow(cx, cyc, 12.0F, 12.0F, 2.0F, 3.0F, Colors.withAlpha(theme.accent(), 0.35F));
         vg.rectOutline(cx + 0.5F, cyc + 0.5F, 11.0F, 11.0F, 2.0F, 1.2F, theme.accentBright());
      }

      if (this.module.cellNumbers.get()) {
         for (int row = 0; row < n; row++) {
            for (int colx = 0; colx < n; colx++) {
               int type = this.module.regionTypeAt(row * n + colx);
               if (type >= 0) {
                  String num = String.valueOf(this.module.regionIdAt(row * n + colx));
                  float fs = num.length() >= 3 ? 5.5F : 6.5F;
                  float tw = vg.textWidth(num, fs);
                  float tx = gridX + colx * 12.0F + (12.0F - tw) / 2.0F;
                  float ty = gridY + row * 12.0F + 6.0F;
                  vg.text(num, tx + 0.5F, ty + 0.5F, fs, Colors.withAlpha(-16777216, 0.55F));
                  vg.text(num, tx, ty, fs, -790280);
               }
            }
         }
      }

      if (onMap) {
         double[] cp = this.module.worldToCellPosition(player.method_23317(), player.method_23321());
         float ax = gridX + (float)((g[0] + cp[0]) * 12.0);
         float ay = gridY + (float)((g[1] + cp[1]) * 12.0);
         vg.circleGlow(ax, ay, 2.2F, 3.5F, Colors.withAlpha(theme.accent(), 0.75F));
         vg.save();
         vg.translate(ax, ay);
         vg.rotate((float)Math.toRadians(player.method_36454() + 180.0F));
         vg.triangle(0.0F, -4.9F, 3.3F, 2.9F, -3.3F, 2.9F, Colors.withAlpha(-16777216, 0.55F));
         vg.triangle(0.0F, -4.0F, 2.6F, 2.2F, -2.6F, 2.2F, theme.accentBright());
         vg.restore();
      }

      vg.restore();
      vg.rectOutline(gridX, gridY, 108.0F, 108.0F, 4.0F, 1.0F, Colors.withAlpha(theme.accent(), 0.5F));
   }

   private void drawLegend(NVGRenderer vg, Theme theme, float lx, float ly, int currentType) {
      float colW = 54.0F;

      for (int type = 0; type < this.module.regionTypeCount(); type++) {
         int col = type % 2;
         int row = type / 2;
         float ex = lx + col * colW;
         float ey = ly + row * 12.0F + 6.0F;
         vg.rect(ex, ey - 2.5F, 5.0F, 5.0F, 1.2F, 0xFF000000 | this.module.regionTypeRgb(type));
         boolean here = type == currentType;
         vg.text(this.module.regionTypeName(type), ex + 8.0F, ey, 7.5F, here ? theme.accentBright() : theme.textMuted());
      }
   }
}
