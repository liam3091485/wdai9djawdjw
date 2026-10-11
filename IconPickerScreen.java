package com.larpclient.gui;

import com.larpclient.LarpClient;
import com.larpclient.gui.picker.PickerGrid;
import com.larpclient.gui.widget.ColorWidget;
import com.larpclient.render.NvgDrawable;
import com.larpclient.render.OverlayRenderer;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.ColorSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.joml.Matrix3x2fStack;

public class IconPickerScreen extends class_437 implements NvgDrawable {
   private static final float HEADER_H = 42.0F;
   private static final float SEARCH_H = 34.0F;
   private static final float PAD = 12.0F;
   private static final float CELL = 34.0F;
   private static final float SEL_CHIP_W = 116.0F;
   private static final float PANEL_RADIUS = 14.0F;
   private final class_437 parent;
   private final PickerGrid model;
   private final ThemeManager themes;
   private final StringBuilder search = new StringBuilder();
   private boolean searchFocused;
   private boolean selectedOnly;
   private List<PickerGrid.Cell> filtered = List.of();
   private String lastQuery = null;
   private boolean lastSelectedOnly;
   private boolean filterDirty = true;
   private float scroll;
   private float maxScroll;
   private ColorSetting colorSetting;
   private String colorTitle;
   private ColorWidget colorWidget;
   private final float[] popupRect = new float[4];
   private final float[] closeRect = new float[4];
   private final float[] searchRect = new float[4];
   private final float[] selChipRect = new float[4];

   public IconPickerScreen(class_437 parent, PickerGrid model, ThemeManager themes) {
      super(class_2561.method_43470(model.title()));
      this.parent = parent;
      this.model = model;
      this.themes = themes;
   }

   private void refreshFilter() {
      String q = this.search.toString().trim().toLowerCase(Locale.ROOT);
      if (this.filterDirty || !q.equals(this.lastQuery) || this.selectedOnly != this.lastSelectedOnly) {
         this.lastQuery = q;
         this.lastSelectedOnly = this.selectedOnly;
         this.filterDirty = false;
         ArrayList<PickerGrid.Cell> out = new ArrayList<>();

         for (PickerGrid.Cell cell : this.model.cells()) {
            if ((!this.selectedOnly || cell.selected()) && (q.isEmpty() || cell.matches(q))) {
               out.add(cell);
            }
         }

         this.filtered = out;
         this.scroll = 0.0F;
      }
   }

   private IconPickerScreen.Layout layout() {
      float uiW = OverlayRenderer.uiWidth();
      float uiH = OverlayRenderer.uiHeight();
      float panelW = Math.min(uiW * 0.82F, 940.0F);
      float panelH = Math.min(uiH * 0.84F, 660.0F);
      float px = (uiW - panelW) / 2.0F;
      float py = (uiH - panelH) / 2.0F;
      float gridX = px + 12.0F;
      float gridY = py + 42.0F + 34.0F;
      float gridW = panelW - 24.0F;
      float gridH = panelH - 42.0F - 34.0F - 12.0F;
      int cols = Math.max(1, (int)(gridW / 34.0F));
      float cell = gridW / cols;
      return new IconPickerScreen.Layout(px, py, panelW, panelH, gridX, gridY, gridW, gridH, cols, cell, cell - 11.0F);
   }

   private float contentHeight(IconPickerScreen.Layout l) {
      int rows = (this.filtered.size() + l.cols() - 1) / l.cols();
      return rows * l.cell();
   }

   public void method_25394(class_332 gg, int mouseX, int mouseY, float partialTick) {
      this.refreshFilter();
      IconPickerScreen.Layout l = this.layout();
      this.maxScroll = Math.max(0.0F, this.contentHeight(l) - l.gridH());
      this.scroll = Math.clamp(this.scroll, 0.0F, this.maxScroll);
      float uiScale = OverlayRenderer.uiScale();
      double guiScale = this.field_22787.method_22683().method_4495();
      float k = (float)(uiScale / guiScale);
      gg.method_25294(0, 0, this.field_22789, this.field_22790, -670694391);
      fillRoundedRectV(gg, l.px() * k, l.py() * k, l.panelW() * k, l.panelH() * k, 14.0F * k, -132771297, -133560304);
      gg.method_44379(gi(l.gridX() * k), gi(l.gridY() * k), gi((l.gridX() + l.gridW()) * k), gi((l.gridY() + l.gridH()) * k));
      int first = Math.max(0, (int)(this.scroll / l.cell()) * l.cols());
      int perView = l.cols() * ((int)(l.gridH() / l.cell()) + 3);
      int last = Math.min(this.filtered.size(), first + perView);
      Matrix3x2fStack pose = gg.method_51448();

      for (int i = first; i < last; i++) {
         PickerGrid.Cell cell = this.filtered.get(i);
         int col = i % l.cols();
         int row = i / l.cols();
         float cx = l.gridX() + col * l.cell() + (l.cell() - l.icon()) / 2.0F;
         float cy = l.gridY() - this.scroll + row * l.cell() + (l.cell() - l.icon()) / 2.0F;
         float sc = l.icon() * k / 16.0F;
         pose.pushMatrix();
         pose.translate(cx * k, cy * k);
         pose.scale(sc, sc);
         gg.method_51427(cell.icon(), 0, 0);
         pose.popMatrix();
      }

      gg.method_44380();
   }

   private static int gi(float v) {
      return Math.round(v);
   }

   private static void fillRoundedRectV(class_332 gg, float x, float y, float w, float h, float radius, int top, int bottom) {
      int x0 = Math.round(x);
      int y0 = Math.round(y);
      int x1 = Math.round(x + w);
      int y1 = Math.round(y + h);
      int height = y1 - y0;
      int width = x1 - x0;
      if (height > 0 && width > 0) {
         int r = Math.min(Math.round(radius), Math.min(width, height) / 2);

         for (int row = 0; row < height; row++) {
            int inset = 0;
            if (r > 0) {
               int dy = row < r ? row : (row >= height - r ? height - 1 - row : -1);
               if (dy >= 0) {
                  double off = r - dy - 0.5;
                  inset = (int)Math.round(r - Math.sqrt(Math.max(0.0, (double)r * r - off * off)));
               }
            }

            int color = Colors.lerp(top, bottom, height <= 1 ? 0.0F : (float)row / (height - 1));
            gg.method_25294(x0 + inset, y0 + row, x1 - inset, y0 + row + 1, color);
         }
      }
   }

   @Override
   public void renderNvg(NVGRenderer vg, float mouseX, float mouseY, float uiWidth, float uiHeight) {
      if (vg.hasFont()) {
         Theme theme = this.themes.current();
         IconPickerScreen.Layout l = this.layout();
         boolean blink = System.nanoTime() / 400000000L % 2L == 0L;
         vg.glow(l.px(), l.py(), l.panelW(), l.panelH(), 14.0F, 17.0F, Colors.withAlpha(-16777216, 0.45F));
         vg.glow(l.px(), l.py(), l.panelW(), l.panelH(), 14.0F, 8.0F, Colors.withAlpha(theme.accent(), 0.28F));
         vg.rectOutline(l.px(), l.py(), l.panelW(), l.panelH(), 14.0F, 1.4F, Colors.withAlpha(theme.accentBright(), 0.75F));
         vg.rectOutline(l.px() + 2.2F, l.py() + 2.2F, l.panelW() - 4.4F, l.panelH() - 4.4F, 11.8F, 1.0F, Colors.withAlpha(theme.accentBright(), 0.13F));
         vg.textGradient(this.model.title(), l.px() + 12.0F, l.py() + 16.0F, 16.0F, theme.accentBright(), theme.accent());
         String hint = "Left-click: toggle   ·   Right-click: color   ·   " + this.model.activeCount() + " active";
         vg.text(hint, l.px() + 12.0F, l.py() + 31.0F, 11.0F, theme.textDisabled());
         float closeCx = l.px() + l.panelW() - 12.0F - 3.0F;
         float closeCy = l.py() + 18.0F;
         boolean closeHover = Math.abs(mouseX - closeCx) < 10.0F && Math.abs(mouseY - closeCy) < 10.0F;
         vg.cross(closeCx - 6.0F, closeCy - 6.0F, 12.0F, 1.8F, closeHover ? theme.accentBright() : theme.textMuted());
         this.closeRect[0] = closeCx - 10.0F;
         this.closeRect[1] = closeCy - 10.0F;
         this.closeRect[2] = closeCx + 10.0F;
         this.closeRect[3] = closeCy + 10.0F;
         float sx = l.px() + 12.0F;
         float sy = l.py() + 42.0F + 4.0F;
         float sh = 22.0F;
         float sw = l.panelW() - 24.0F - 116.0F - 8.0F;
         this.searchRect[0] = sx;
         this.searchRect[1] = sy;
         this.searchRect[2] = sx + sw;
         this.searchRect[3] = sy + sh;
         vg.rect(sx, sy, sw, sh, sh / 2.0F, this.searchFocused ? Colors.withAlpha(theme.accent(), 0.16F) : Colors.withAlpha(-16777216, 0.4F));
         vg.rectOutline(
            sx, sy, sw, sh, sh / 2.0F, 1.1F, Colors.withAlpha(this.searchFocused ? theme.accentBright() : theme.accent(), this.searchFocused ? 0.9F : 0.35F)
         );
         float ix = sx + 12.0F;
         float iy = sy + sh / 2.0F;
         vg.circleOutline(ix, iy - 1.0F, 4.0F, 1.4F, theme.textMuted());
         vg.line(ix + 3.0F, iy + 2.0F, ix + 6.0F, iy + 5.0F, 1.4F, theme.textMuted());
         if (this.search.length() == 0 && !this.searchFocused) {
            vg.text("Search…  (" + this.filtered.size() + " shown)", sx + 24.0F, iy, 12.5F, theme.textDisabled());
         } else {
            float w = vg.text(this.search.toString(), sx + 24.0F, iy, 12.5F, theme.textPrimary());
            if (this.searchFocused && blink) {
               vg.rect(sx + 24.0F + w + 1.5F, iy - 6.0F, 1.4F, 12.0F, 0.7F, theme.accentBright());
            }
         }

         float chipX;
         this.selChipRect[0] = chipX = sx + sw + 8.0F;
         this.selChipRect[1] = sy;
         this.selChipRect[2] = chipX + 116.0F;
         this.selChipRect[3] = sy + sh;
         boolean chipHover = inRect(mouseX, mouseY, this.selChipRect);
         vg.rect(chipX, sy, 116.0F, sh, sh / 2.0F, this.selectedOnly ? Colors.withAlpha(theme.accent(), 0.22F) : Colors.withAlpha(-16777216, 0.4F));
         vg.rectOutline(
            chipX,
            sy,
            116.0F,
            sh,
            sh / 2.0F,
            1.1F,
            Colors.withAlpha(!this.selectedOnly && !chipHover ? theme.accent() : theme.accentBright(), this.selectedOnly ? 0.9F : 0.4F)
         );
         float dotX = chipX + 13.0F;
         float dotY = sy + sh / 2.0F;
         if (this.selectedOnly) {
            vg.circle(dotX, dotY, 4.0F, theme.statusEnabled());
            vg.circleGlow(dotX, dotY, 4.0F, 4.0F, Colors.withAlpha(theme.statusEnabled(), 0.5F));
         } else {
            vg.circleOutline(dotX, dotY, 4.0F, 1.4F, theme.textMuted());
         }

         vg.text("Selected only", chipX + 24.0F, dotY, 11.5F, this.selectedOnly ? theme.textPrimary() : theme.textMuted());
         vg.save();
         vg.scissor(l.gridX(), l.gridY(), l.gridW(), l.gridH());
         int first = Math.max(0, (int)(this.scroll / l.cell()) * l.cols());
         int perView = l.cols() * ((int)(l.gridH() / l.cell()) + 3);
         int last = Math.min(this.filtered.size(), first + perView);
         int hovered = this.cellAt(mouseX, mouseY, l);

         for (int i = first; i < last; i++) {
            PickerGrid.Cell cell = this.filtered.get(i);
            int col = i % l.cols();
            int row = i / l.cols();
            float cellX = l.gridX() + col * l.cell();
            float cellY = l.gridY() - this.scroll + row * l.cell();
            boolean tracked = cell.tracked();
            boolean active = cell.enabled();
            if (i == hovered) {
               vg.rect(cellX + 1.0F, cellY + 1.0F, l.cell() - 2.0F, l.cell() - 2.0F, 6.0F, Colors.withAlpha(theme.accent(), 0.12F));
            }

            if (tracked) {
               int base = cell.color();
               int ring = active ? base : Colors.withAlpha(base, 0.35F);
               vg.rectOutline(cellX + 1.5F, cellY + 1.5F, l.cell() - 3.0F, l.cell() - 3.0F, 6.0F, active ? 1.8F : 1.0F, ring | (active ? -16777216 : 0));
               vg.rect(cellX + 4.0F, cellY + l.cell() - 5.0F, l.cell() - 8.0F, 2.5F, 1.0F, base | 0xFF000000);
               if (active) {
                  vg.circle(cellX + l.cell() - 6.0F, cellY + 6.0F, 2.6F, theme.statusEnabled());
               }
            }
         }

         vg.restore();
         if (this.filtered.isEmpty() && this.selectedOnly) {
            vg.text(
               "Nothing selected yet \u2014 turn off \"Selected only\" to browse.",
               l.gridX() + 4.0F,
               l.gridY() + 16.0F,
               12.5F,
               theme.textDisabled()
            );
         }

         if (this.maxScroll > 0.0F) {
            float trackX = l.px() + l.panelW() - 6.0F;
            float thumbH = Math.max(24.0F, l.gridH() * (l.gridH() / this.contentHeight(l)));
            float thumbY = l.gridY() + (l.gridH() - thumbH) * (this.scroll / this.maxScroll);
            vg.rect(trackX, thumbY, 3.0F, thumbH, 1.5F, Colors.withAlpha(theme.accent(), 0.55F));
         }

         if (this.colorSetting != null && this.colorWidget != null) {
            this.renderColorPopup(vg, theme);
         } else {
            this.popupRect[3] = 0.0F;
            this.popupRect[2] = 0.0F;
            this.popupRect[1] = 0.0F;
            this.popupRect[0] = 0.0F;
         }
      }
   }

   private void renderColorPopup(NVGRenderer vg, Theme theme) {
      this.colorWidget.setExpanded(true);
      float cardW = 220.0F;
      float titleH = 26.0F;
      float uiW = OverlayRenderer.uiWidth();
      float uiH = OverlayRenderer.uiHeight();
      float wx = (uiW - cardW) / 2.0F + 12.0F;
      float bodyH = this.colorWidget.height(vg);
      float cardH = titleH + bodyH + 12.0F;
      float cardX = (uiW - cardW) / 2.0F;
      float cardY = (uiH - cardH) / 2.0F;
      this.popupRect[0] = cardX;
      this.popupRect[1] = cardY;
      this.popupRect[2] = cardX + cardW;
      this.popupRect[3] = cardY + cardH;
      vg.glow(cardX, cardY, cardW, cardH, 12.0F, 14.0F, Colors.withAlpha(theme.accent(), 0.3F));
      vg.rectGradient(cardX, cardY, cardW, cardH, 12.0F, theme.background(), theme.backgroundTo(), true);
      vg.rectOutline(cardX, cardY, cardW, cardH, 12.0F, 1.4F, Colors.withAlpha(theme.accentBright(), 0.7F));
      vg.textGradient(this.colorTitle, cardX + 12.0F, cardY + 14.0F, 12.5F, theme.accentBright(), theme.accent());
      this.colorWidget.setBounds(wx, cardY + titleH, cardW - 24.0F);
      this.colorWidget.render(vg, OverlayRenderer.uiMouseX(), OverlayRenderer.uiMouseY());
   }

   private int cellAt(float mx, float my, IconPickerScreen.Layout l) {
      if (!(mx < l.gridX()) && !(mx > l.gridX() + l.gridW()) && !(my < l.gridY()) && !(my > l.gridY() + l.gridH())) {
         int col = (int)((mx - l.gridX()) / l.cell());
         int row = (int)((my - (l.gridY() - this.scroll)) / l.cell());
         if (col >= 0 && col < l.cols() && row >= 0) {
            int index = row * l.cols() + col;
            return index >= 0 && index < this.filtered.size() ? index : -1;
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   private float ux(double guiX) {
      return OverlayRenderer.guiToUi(guiX);
   }

   public boolean method_25402(class_11909 event, boolean doubled) {
      float mx = this.ux(event.comp_4798());
      float my = this.ux(event.comp_4799());
      IconPickerScreen.Layout l = this.layout();
      if (this.colorSetting != null) {
         if (inRect(mx, my, this.popupRect)) {
            this.colorWidget.mouseClicked(mx, my, event.method_74245());
         } else {
            this.closeColor();
            UiSounds.select();
         }

         return true;
      } else if (inRect(mx, my, this.closeRect)) {
         this.method_25419();
         return true;
      } else if (inRect(mx, my, this.selChipRect)) {
         this.selectedOnly = !this.selectedOnly;
         this.filterDirty = true;
         this.searchFocused = false;
         UiSounds.toggle(this.selectedOnly);
         return true;
      } else if (inRect(mx, my, this.searchRect)) {
         this.searchFocused = true;
         UiSounds.select();
         return true;
      } else {
         this.searchFocused = false;
         if (!(mx < l.px()) && !(mx > l.px() + l.panelW()) && !(my < l.py()) && !(my > l.py() + l.panelH())) {
            int index = this.cellAt(mx, my, l);
            if (index >= 0) {
               PickerGrid.Cell cell = this.filtered.get(index);
               if (event.method_74245() == 1) {
                  this.openColor(cell.colorTarget(), cell.label());
               } else if (event.method_74245() == 0) {
                  boolean wasSelected = cell.selected();
                  cell.toggle();
                  UiSounds.toggle(cell.enabled());
                  if (this.selectedOnly && wasSelected && !cell.selected()) {
                     this.filterDirty = true;
                  }
               }

               return true;
            } else {
               return true;
            }
         } else {
            this.method_25419();
            return true;
         }
      }
   }

   private void openColor(ColorSetting target, String title) {
      if (target != null) {
         this.colorSetting = target;
         this.colorTitle = title;
         this.colorWidget = new ColorWidget(this.themes, target);
         this.colorWidget.setExpanded(true);
         this.searchFocused = false;
         UiSounds.select();
      }
   }

   private void closeColor() {
      this.colorSetting = null;
      this.colorTitle = null;
      this.colorWidget = null;
   }

   public boolean method_25403(class_11909 event, double dx, double dy) {
      if (this.colorSetting != null && this.colorWidget != null) {
         this.colorWidget.mouseDragged(this.ux(event.comp_4798()), this.ux(event.comp_4799()));
      }

      return true;
   }

   public boolean method_25406(class_11909 event) {
      if (this.colorWidget != null) {
         this.colorWidget.mouseReleased();
      }

      return true;
   }

   public boolean method_25401(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (this.colorSetting == null) {
         this.scroll = Math.clamp(this.scroll - (float)(scrollY * this.layout().cell()), 0.0F, this.maxScroll);
      }

      return true;
   }

   public boolean method_25404(class_11908 event) {
      int key = event.comp_4795();
      if (this.colorSetting != null) {
         if (this.colorWidget != null && this.colorWidget.isListening()) {
            this.colorWidget.keyPressed(key);
            return true;
         } else if (key == 256) {
            this.closeColor();
            return true;
         } else {
            return true;
         }
      } else if (this.searchFocused) {
         switch (key) {
            case 256:
            case 257:
            case 335:
               this.searchFocused = false;
               break;
            case 259:
               if (this.search.length() > 0) {
                  this.search.deleteCharAt(this.search.length() - 1);
                  this.filterDirty = true;
               }
         }

         return true;
      } else if (key == 256) {
         this.method_25419();
         return true;
      } else {
         return super.method_25404(event);
      }
   }

   public boolean method_25400(class_11905 event) {
      if (this.colorSetting != null) {
         return true;
      } else if (!this.searchFocused) {
         return true;
      } else if (this.search.length() >= 48) {
         return true;
      } else {
         char c = (char)event.comp_4793();
         if (c == ' ' || c == '_' || c == ':' || c == '/' || c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
            this.search.append(Character.toLowerCase(c));
            this.filterDirty = true;
         }

         return true;
      }
   }

   public void method_25419() {
      LarpClient.config().save();
      this.field_22787.method_1507(this.parent);
   }

   public boolean method_25421() {
      return false;
   }

   public void debugSetSearch(String query) {
      this.search.setLength(0);
      this.search.append(query);
      this.searchFocused = true;
      this.filterDirty = true;
      this.refreshFilter();
   }

   public void debugSetSelectedOnly(boolean value) {
      this.selectedOnly = value;
      this.filterDirty = true;
      this.refreshFilter();
   }

   public void debugOpenColor(int filteredIndex) {
      if (filteredIndex >= 0 && filteredIndex < this.filtered.size()) {
         PickerGrid.Cell cell = this.filtered.get(filteredIndex);
         this.openColor(cell.colorTarget(), cell.label());
      }
   }

   public int debugFilteredCount() {
      return this.filtered.size();
   }

   private static boolean inRect(float mx, float my, float[] r) {
      return mx >= r[0] && mx <= r[2] && my >= r[1] && my <= r[3];
   }

   private record Layout(float px, float py, float panelW, float panelH, float gridX, float gridY, float gridW, float gridH, int cols, float cell, float icon) {
   }
}
