package com.larpclient.gui;

import com.larpclient.gui.panel.ModuleEntry;
import com.larpclient.gui.widget.StringWidget;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.module.ModuleManager;
import com.larpclient.render.nanovg.NVGImages;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.class_2960;

public final class SidebarModuleView {
   public static final float WIDTH = 1080.0F;
   public static final float HEIGHT = 590.0F;
   private static final float RAIL_W = 172.0F;
   private static final float SIDE_W = 190.0F;
   private static final float TOP_H = 72.0F;
   private static final float PAD = 18.0F;
   private static final float CARD_GAP = 12.0F;
   private final ThemeManager themes;
   private final Map<Category, List<ModuleEntry>> entries = new EnumMap<>(Category.class);
   private final Runnable panicAction;
   private final ClickGuiState state;
   private final ModuleManager modules;
   private static final class_2960 LC_LOGO = class_2960.method_60655("larpclient", "textures/gui/lc_logo.png");
   private static final class_2960 LARP_BACKGROUND = class_2960.method_60655("larpclient", "textures/gui/larp_background.png");
   private Category selected = Category.COMBAT;
   private boolean favoritesSelected;
   private float scroll;
   private float maxScroll;
   private final StringBuilder search = new StringBuilder();
   private boolean searchFocused;
   private ModuleEntry pressedEntry;
   private float searchX;
   private float searchY;
   private float searchW;
   private float searchH;
   private float quickPanicX;
   private float quickPanicY;
   private float quickPanicW;
   private float quickPanicH;
   private float quickHudX;
   private float quickHudY;
   private float quickHudW;
   private float quickHudH;
   private float quickClearX;
   private float quickClearY;
   private float quickClearW;
   private float quickClearH;
   private final List<ModuleEntry> favoriteRows = new ArrayList<>();
   private final List<float[]> favoriteBounds = new ArrayList<>();

   public SidebarModuleView(ModuleManager var1, ThemeManager var2, ClickGuiState var3, Runnable var4) {
      this.themes = var2;
      this.panicAction = var4;
      this.state = var3;
      this.modules = var1;

      for (Category var8 : Category.values()) {
         ArrayList<ModuleEntry> var9 = new ArrayList<>();

         for (Module var11 : var1.inCategory(var8)) {
            String var12 = var11.getName();
            if (!var12.equalsIgnoreCase("License") && !var12.equalsIgnoreCase("ConfigShare") && !var12.equalsIgnoreCase("Config Share")) {
               var9.add(new ModuleEntry(var11, var2, var3));
            }
         }

         this.entries.put(var8, var9);
      }
   }

   private List<ModuleEntry> visibleEntries() {
      String var1 = this.search.toString().toLowerCase(Locale.ROOT).trim();
      if (!var1.isEmpty()) {
         ArrayList<ModuleEntry> var9 = new ArrayList<>();

         for (Category var13 : Category.values()) {
            var9.addAll(this.entries.getOrDefault(var13, List.of()));
         }

         return var9.stream()
            .filter(
               var1x -> {
                  Module var2x = var1x.getModule();
                  if (var2x.getName().toLowerCase(Locale.ROOT).contains(var1)) {
                     return true;
                  } else {
                     return var2x.getDescription() != null && var2x.getDescription().toLowerCase(Locale.ROOT).contains(var1)
                        ? true
                        : var2x.getSettings().stream().anyMatch(var1xx -> var1xx.getName() != null && var1xx.getName().toLowerCase(Locale.ROOT).contains(var1));
                  }
               }
            )
            .toList();
      } else if (!this.favoritesSelected) {
         return this.entries.getOrDefault(this.selected, List.of());
      } else {
         ArrayList<ModuleEntry> var2 = new ArrayList<>();

         for (Category var6 : Category.values()) {
            for (ModuleEntry var8 : this.entries.getOrDefault(var6, List.of())) {
               if (var8.isFavorite()) {
                  var2.add(var8);
               }
            }
         }

         return var2;
      }
   }

   public float boxX(float var1) {
      return (var1 - 1080.0F) / 2.0F;
   }

   public float boxY(float var1) {
      return (var1 - 590.0F) / 2.0F;
   }

   public void render(NVGRenderer var1, float var2, float var3, float var4, float var5) {
      Theme var6 = this.themes.current();
      float var7 = this.boxX(var4);
      float var8 = this.boxY(var5);
      var1.glow(var7, var8, 1080.0F, 590.0F, 14.0F, 22.0F, Colors.withAlpha(-16777216, 0.58F));
      var1.rect(var7, var8, 1080.0F, 590.0F, 14.0F, -83424494);
      var1.rectOutline(var7, var8, 1080.0F, 590.0F, 14.0F, 1.0F, Colors.withAlpha(var6.accentBright(), 0.24F));
      var1.rectVaryingGradient(var7, var8, 172.0F, 590.0F, 14.0F, 0.0F, 0.0F, 14.0F, -16181981, -16315373);
      var1.rect(var7 + 172.0F - 1.0F, var8 + 14.0F, 1.0F, 562.0F, 0.0F, Colors.withAlpha(var6.accent(), 0.16F));
      int var9 = NVGImages.fromResource(LC_LOGO);
      if (var9 > 0) {
         var1.imagePattern(var9, var7 + 20.0F, var8 + 14.0F, 48.0F, 48.0F, var7 + 20.0F, var8 + 14.0F, 48.0F, 48.0F, 1.0F);
      } else {
         var1.text("LC", var7 + 20.0F, var8 + 38.0F, 18.0F, var6.accentBright());
      }

      var1.text("v2.2.0", var7 + 20.0F, var8 + 78.0F, 9.5F, var6.textMuted());
      var1.text("CATEGORIES", var7 + 20.0F, var8 + 112.0F, 9.5F, var6.textDisabled());
      float var10 = var8 + 128.0F;

      for (Category var14 : Category.values()) {
         boolean var15 = !this.favoritesSelected && var14 == this.selected;
         boolean var16 = hit(var2, var3, var7 + 14.0F, var10, 144.0F, 36.0F);
         if (var15 || var16) {
            var1.rect(var7 + 14.0F, var10, 144.0F, 36.0F, 8.0F, var15 ? Colors.withAlpha(var6.accent(), 0.19F) : Colors.withAlpha(-1, 0.035F));
         }

         if (var15) {
            var1.rect(var7 + 14.0F, var10 + 8.0F, 3.0F, 20.0F, 1.5F, var6.accentBright());
         }

         var1.text(var14.getDisplayName(), var7 + 28.0F, var10 + 18.0F, 12.0F, var15 ? var6.textPrimary() : (var16 ? var6.textMuted() : var6.textDisabled()));
         var10 += 42.0F;
      }

      boolean var45 = hit(var2, var3, var7 + 14.0F, var10, 144.0F, 36.0F);
      if (this.favoritesSelected || var45) {
         var1.rect(var7 + 14.0F, var10, 144.0F, 36.0F, 8.0F, this.favoritesSelected ? Colors.withAlpha(var6.accent(), 0.19F) : Colors.withAlpha(-1, 0.035F));
      }

      if (this.favoritesSelected) {
         var1.rect(var7 + 14.0F, var10 + 8.0F, 3.0F, 20.0F, 1.5F, var6.accentBright());
      }

      var1.text("FAVORITES", var7 + 28.0F, var10 + 18.0F, 12.0F, this.favoritesSelected ? var6.textPrimary() : (var45 ? var6.textMuted() : var6.textDisabled()));
      float var46 = var7 + 172.0F;
      float var47 = 718.0F;
      var1.rectVaryingGradient(var46, var8, var47, 72.0F, 0.0F, 14.0F, 0.0F, 0.0F, -16117476, -16249579);
      var1.rect(var46, var8 + 72.0F - 1.0F, var47, 1.0F, 0.0F, Colors.withAlpha(-1, 0.055F));
      var1.text(this.favoritesSelected ? "Favorites" : this.selected.getDisplayName(), var46 + 22.0F, var8 + 29.0F, 20.0F, var6.textPrimary());
      var1.text(this.favoritesSelected ? "Your pinned modules" : "Modules & settings", var46 + 22.0F, var8 + 50.0F, 10.5F, var6.textDisabled());
      this.searchW = 238.0F;
      this.searchH = 34.0F;
      this.searchX = var46 + var47 - 22.0F - this.searchW;
      this.searchY = var8 + 19.0F;
      boolean var48 = hit(var2, var3, this.searchX, this.searchY, this.searchW, this.searchH);
      var1.rect(this.searchX, this.searchY, this.searchW, this.searchH, 8.0F, -15985374);
      var1.rectOutline(
         this.searchX,
         this.searchY,
         this.searchW,
         this.searchH,
         8.0F,
         1.0F,
         Colors.withAlpha(var6.accentBright(), this.searchFocused ? 0.72F : (var48 ? 0.34F : 0.16F))
      );
      String var49 = this.search.length() == 0 ? "Search modules or settings..." : this.search.toString();
      var1.text(var49, this.searchX + 12.0F, this.searchY + 17.0F, 11.0F, this.search.length() == 0 ? var6.textDisabled() : var6.textPrimary());
      if (this.searchFocused && System.nanoTime() / 400000000L % 2L == 0L) {
         float var50 = var1.textWidth(this.search.toString(), 11.0F);
         var1.rect(this.searchX + 12.0F + var50 + 1.0F, this.searchY + 10.0F, 1.0F, 14.0F, 0.0F, var6.accentBright());
      }

      List<ModuleEntry> var51 = this.visibleEntries();
      int var17 = 0;
      if (this.favoritesSelected) {
         for (ModuleEntry var19 : var51) {
            if (var19.getModule().isEnabled()) {
               var17++;
            }
         }
      } else {
         for (ModuleEntry var54 : this.entries.getOrDefault(this.selected, List.of())) {
            if (var54.getModule().isEnabled()) {
               var17++;
            }
         }
      }

      var1.text(var51.size() + " MODULES", var46 + 22.0F, var8 + 91.0F, 9.5F, var6.textDisabled());
      var1.text(var17 + " ENABLED", var46 + 112.0F, var8 + 91.0F, 9.5F, var17 > 0 ? Colors.withAlpha(var6.accentBright(), 0.78F) : var6.textDisabled());
      float var53 = var46 + 18.0F;
      float var55 = var8 + 108.0F;
      float var20 = var47 - 36.0F;
      float var21 = 464.0F;
      float var22 = (var20 - 12.0F) / 2.0F;
      int var23 = NVGImages.fromResource(LARP_BACKGROUND);
      if (var23 > 0) {
         var1.save();
         var1.scissor(var53, var55, var20, var21);
         var1.alpha(0.13F);
         float var24 = 620.0F;
         float var25 = 348.75F;
         float var26 = var53 + (var20 - var24) / 2.0F;
         float var27 = var55 + (var21 - var25) / 2.0F;
         var1.imagePattern(var23, var26, var27, var24, var25, var26, var27, var24, var25, 1.0F);
         var1.restore();
      }

      float var56 = 0.0F;
      float var57 = 0.0F;

      for (ModuleEntry var60 : var51) {
         float var28 = var60.height(var1) + 12.0F;
         if (var56 <= var57) {
            var56 += var28;
         } else {
            var57 += var28;
         }
      }

      float var59 = Math.max(var56, var57);
      this.maxScroll = Math.max(0.0F, var59 - var21);
      this.scroll = Math.clamp(this.scroll, 0.0F, this.maxScroll);
      var1.save();
      var1.scissor(var53, var55, var20, var21);
      float var61 = var55 - this.scroll;
      float var62 = var55 - this.scroll;

      for (ModuleEntry var30 : var51) {
         float var31 = var30.height(var1);
         boolean var32 = var61 <= var62;
         float var33 = var32 ? var53 : var53 + var22 + 12.0F;
         float var34 = var32 ? var61 : var62;
         var30.setBounds(var33, var34, var22);
         if (var34 + var31 >= var55 - 36.0F && var34 <= var55 + var21 + 36.0F) {
            var30.render(var1, var2, var3, 1.0F);
         }

         if (var32) {
            var61 += var31 + 12.0F;
         } else {
            var62 += var31 + 12.0F;
         }
      }

      var1.restore();
      if (var51.isEmpty()) {
         float var63 = var55 + 54.0F;
         var1.rect(var53, var63, var20, 84.0F, 10.0F, Colors.withAlpha(-1, 0.025F));
         var1.text("No modules found", var53 + 22.0F, var63 + 31.0F, 14.0F, var6.textMuted());
         var1.text("Try a different category or search term.", var53 + 22.0F, var63 + 55.0F, 10.5F, var6.textDisabled());
      }

      if (this.maxScroll > 0.0F) {
         float var64 = Math.max(46.0F, var21 * (var21 / (var21 + this.maxScroll)));
         float var66 = var55 + (var21 - var64) * (this.scroll / this.maxScroll);
         var1.rect(var46 + var47 - 7.0F, var55, 2.0F, var21, 1.0F, Colors.withAlpha(-1, 0.05F));
         var1.rect(var46 + var47 - 7.0F, var66, 2.0F, var64, 1.0F, Colors.withAlpha(var6.accentBright(), 0.7F));
      }

      float var65 = var46 + var47;
      float var67 = 12.0F;
      float var68 = var65 + var67;
      float var69 = 190.0F - var67 * 2.0F;
      var1.rect(var65, var8 + 14.0F, 1.0F, 562.0F, 0.0F, Colors.withAlpha(var6.accent(), 0.14F));
      float var70 = var8 + 20.0F;
      var1.text("QUICK ACTIONS", var68, var70 + 11.0F, 10.0F, var6.textDisabled());
      this.quickPanicX = var68;
      this.quickPanicY = var70 + 24.0F;
      this.quickPanicW = var69;
      this.quickPanicH = 31.0F;
      this.quickHudX = var68;
      this.quickHudY = this.quickPanicY + 37.0F;
      this.quickHudW = var69;
      this.quickHudH = 31.0F;
      this.quickClearX = var68;
      this.quickClearY = this.quickHudY + 37.0F;
      this.quickClearW = var69;
      this.quickClearH = 31.0F;
      this.drawSideButton(var1, var6, var2, var3, this.quickPanicX, this.quickPanicY, this.quickPanicW, this.quickPanicH, "PANIC (F8)");
      this.drawSideButton(var1, var6, var2, var3, this.quickHudX, this.quickHudY, this.quickHudW, this.quickHudH, "HUD SETTINGS");
      this.drawSideButton(var1, var6, var2, var3, this.quickClearX, this.quickClearY, this.quickClearW, this.quickClearH, "CLEAR SEARCH");
      float var71 = this.quickClearY + this.quickClearH + 25.0F;
      var1.text("FAVORITES", var68, var71, 10.0F, var6.textDisabled());
      var1.text(Integer.toString(this.state.favoriteCount()), var68 + var69 - 12.0F, var71, 10.0F, var6.accentBright());
      this.favoriteRows.clear();
      this.favoriteBounds.clear();
      float var35 = var71 + 14.0F;

      label211:
      for (Category var39 : Category.values()) {
         for (ModuleEntry var41 : this.entries.getOrDefault(var39, List.of())) {
            if (var41.isFavorite()) {
               if (this.favoriteRows.size() >= 4) {
                  break label211;
               }

               float var42 = 29.0F;
               boolean var43 = hit(var2, var3, var68, var35, var69, var42);
               var1.rect(var68, var35, var69, var42, 6.0F, Colors.withAlpha(-1, var43 ? 0.045F : 0.022F));
               String var44 = var41.getModule().getName();
               if (var44.length() > 18) {
                  var44 = var44.substring(0, 15) + "...";
               }

               var1.text(var44, var68 + 9.0F, var35 + 15.0F, 10.5F, var43 ? var6.textPrimary() : var6.textMuted());
               this.favoriteRows.add(var41);
               this.favoriteBounds.add(new float[]{var68, var35, var69, var42});
               var35 += var42 + 5.0F;
            }
         }
      }

      if (this.favoriteRows.isEmpty()) {
         var1.text("Pin modules to show them here", var68, var35 + 12.0F, 9.5F, var6.textDisabled());
         var35 += 28.0F;
      }

      float var72 = Math.max(var8 + 395.0F, var35 + 23.0F);
      var1.text("CLIENT STATS", var68, var72, 10.0F, var6.textDisabled());
      int var73 = 0;
      int var74 = 0;

      for (Category var80 : Category.values()) {
         for (ModuleEntry var82 : this.entries.getOrDefault(var80, List.of())) {
            var73++;
            if (var82.getModule().isEnabled()) {
               var74++;
            }
         }
      }

      float var76 = var72 + 17.0F;
      this.drawStat(var1, var6, var68, var76, var69, "MODULES", Integer.toString(var73));
      this.drawStat(var1, var6, var68, var76 + 37.0F, var69, "ENABLED", Integer.toString(var74));
      this.drawStat(var1, var6, var68, var76 + 74.0F, var69, "FAVORITES", Integer.toString(this.state.favoriteCount()));
      float var78 = var8 + 590.0F - 45.0F;
      var1.rect(var68, var78, var69, 29.0F, 7.0F, Colors.withAlpha(var6.accent(), 0.09F));
      var1.rectOutline(var68, var78, var69, 29.0F, 7.0F, 1.0F, Colors.withAlpha(var6.accentBright(), 0.18F));
      var1.circle(var68 + 13.0F, var78 + 14.5F, 3.0F, var6.accentBright());
      var1.text("Welcome back!", var68 + 24.0F, var78 + 14.5F, 10.5F, var6.textMuted());
   }

   private void drawSideButton(NVGRenderer var1, Theme var2, float var3, float var4, float var5, float var6, float var7, float var8, String var9) {
      boolean var10 = hit(var3, var4, var5, var6, var7, var8);
      var1.rect(var5, var6, var7, var8, 7.0F, Colors.withAlpha(var2.accent(), var10 ? 0.16F : 0.07F));
      var1.rectOutline(var5, var6, var7, var8, 7.0F, 1.0F, Colors.withAlpha(var2.accentBright(), var10 ? 0.35F : 0.14F));
      var1.text(var9, var5 + 12.0F, var6 + var8 / 2.0F, 10.2F, var10 ? var2.textPrimary() : var2.textMuted());
   }

   private void drawStat(NVGRenderer var1, Theme var2, float var3, float var4, float var5, String var6, String var7) {
      var1.rect(var3, var4, var5, 31.0F, 7.0F, Colors.withAlpha(-1, 0.022F));
      var1.text(var6, var3 + 10.0F, var4 + 15.5F, 9.4F, var2.textDisabled());
      float var8 = var1.textWidth(var7, 11.0F);
      var1.text(var7, var3 + var5 - 10.0F - var8, var4 + 15.5F, 11.0F, var2.accentBright());
   }

   public boolean mouseClicked(float var1, float var2, int var3, float var4, float var5) {
      float var6 = this.boxX(var4);
      float var7 = this.boxY(var5);
      if (hit(var1, var2, this.searchX, this.searchY, this.searchW, this.searchH)) {
         this.searchFocused = true;
         UiSounds.select();
         return true;
      } else {
         this.searchFocused = false;
         if (var3 == 0 && hit(var1, var2, this.quickPanicX, this.quickPanicY, this.quickPanicW, this.quickPanicH)) {
            this.panicAction.run();
            UiSounds.select();
            return true;
         } else if (var3 == 0 && hit(var1, var2, this.quickHudX, this.quickHudY, this.quickHudW, this.quickHudH)) {
            this.favoritesSelected = false;
            this.selected = Category.CLIENT;
            this.search.setLength(0);
            this.search.append("HUD");
            this.scroll = 0.0F;
            UiSounds.select();
            return true;
         } else if (var3 == 0 && hit(var1, var2, this.quickClearX, this.quickClearY, this.quickClearW, this.quickClearH)) {
            this.search.setLength(0);
            this.scroll = 0.0F;
            UiSounds.select();
            return true;
         } else {
            if (var3 == 0) {
               for (int var8 = 0; var8 < this.favoriteBounds.size() && var8 < this.favoriteRows.size(); var8++) {
                  float[] var9 = this.favoriteBounds.get(var8);
                  if (hit(var1, var2, var9[0], var9[1], var9[2], var9[3])) {
                     ModuleEntry var10 = this.favoriteRows.get(var8);
                     this.favoritesSelected = false;
                     this.selected = var10.getModule().getCategory();
                     this.search.setLength(0);
                     this.search.append(var10.getModule().getName());
                     this.scroll = 0.0F;
                     UiSounds.select();
                     return true;
                  }
               }
            }

            float var13 = var7 + 128.0F;

            for (Category var12 : Category.values()) {
               if (hit(var1, var2, var6 + 14.0F, var13, 144.0F, 36.0F)) {
                  if (this.favoritesSelected || this.selected != var12) {
                     this.favoritesSelected = false;
                     this.selected = var12;
                     this.scroll = 0.0F;
                     UiSounds.select();
                  }

                  return true;
               }

               var13 += 42.0F;
            }

            if (hit(var1, var2, var6 + 14.0F, var13, 144.0F, 36.0F)) {
               this.favoritesSelected = true;
               this.search.setLength(0);
               this.scroll = 0.0F;
               UiSounds.select();
               return true;
            } else {
               for (ModuleEntry var17 : this.visibleEntries()) {
                  if (var17.mouseClicked(var1, var2, var3)) {
                     this.pressedEntry = var17;
                     return true;
                  }
               }

               return true;
            }
         }
      }
   }

   public void mouseDragged(float var1, float var2) {
      if (this.pressedEntry != null) {
         this.pressedEntry.mouseDragged(var1, var2);
      }
   }

   public void mouseReleased() {
      if (this.pressedEntry != null) {
         this.pressedEntry.mouseReleased();
         this.pressedEntry = null;
      }
   }

   public void scroll(double var1) {
      this.scroll = Math.clamp(this.scroll - (float)var1 * 42.0F, 0.0F, this.maxScroll);
   }

   public boolean keyPressed(int var1) {
      StringWidget var2 = StringWidget.active();
      if (var2 != null && var2.keyPressed(var1)) {
         return true;
      } else if (this.searchFocused) {
         if (var1 == 259 && this.search.length() > 0) {
            this.search.deleteCharAt(this.search.length() - 1);
            this.scroll = 0.0F;
            return true;
         } else if (var1 != 256 && var1 != 257 && var1 != 335) {
            return true;
         } else {
            this.searchFocused = false;
            return true;
         }
      } else {
         for (ModuleEntry var4 : this.visibleEntries()) {
            if (var4.isListening() && var4.keyPressed(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean charTyped(int var1) {
      StringWidget var2 = StringWidget.active();
      if (var2 != null && var2.charTyped(var1)) {
         return true;
      } else if (this.searchFocused) {
         char var5 = (char)var1;
         if (var5 >= ' ' && var5 < 127 && this.search.length() < 48) {
            this.search.append(var5);
            this.scroll = 0.0F;
         }

         return true;
      } else {
         for (ModuleEntry var4 : this.visibleEntries()) {
            if (var4.isListening() && var4.charTyped(var1)) {
               return true;
            }
         }

         return false;
      }
   }

   private static boolean hit(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 <= var2 + var4 && var1 >= var3 && var1 <= var3 + var5;
   }
}
