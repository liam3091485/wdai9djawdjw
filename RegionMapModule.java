package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class RegionMapModule extends Module {
   public static final int MAP_SIZE = 9;
   private static final double REGION_SIZE = 50000.0;
   private static final double MAP_OFFSET = 225000.0;
   private static final String[] TYPE_NAMES = new String[]{
      "EU-C", "EU-W", "NA-E", "NA-W", "Asia", "Oce"
   };
   private static final int[] TYPE_COLORS = new int[]{
      rgb(159, 206, 99), rgb(0, 166, 99), rgb(79, 173, 234), rgb(47, 110, 186), rgb(245, 194, 66), rgb(252, 136, 3)
   };
   private static final int[][] LAYOUT = new int[][]{
      {82, 5},
      {100, 3},
      {101, 3},
      {102, 3},
      {103, 2},
      {104, 2},
      {105, 2},
      {106, 2},
      {91, 2},
      {83, 5},
      {44, 3},
      {75, 3},
      {42, 3},
      {41, 2},
      {40, 2},
      {39, 2},
      {38, 2},
      {92, 2},
      {84, 5},
      {45, 3},
      {14, 3},
      {13, 3},
      {12, 2},
      {11, 2},
      {10, 2},
      {37, 2},
      {93, 2},
      {85, 5},
      {46, 5},
      {74, 5},
      {3, 3},
      {2, 2},
      {1, 2},
      {25, 2},
      {36, 2},
      {94, 2},
      {86, 4},
      {47, 4},
      {72, 4},
      {71, 4},
      {5, 2},
      {4, 2},
      {24, 2},
      {35, 2},
      {95, 2},
      {87, 4},
      {51, 1},
      {17, 1},
      {9, 0},
      {8, 0},
      {7, 0},
      {23, 0},
      {34, 0},
      {96, 2},
      {88, 4},
      {54, 1},
      {18, 1},
      {61, 0},
      {62, 0},
      {21, 0},
      {22, 0},
      {33, 0},
      {97, 0},
      {89, 0},
      {26, 1},
      {27, 0},
      {28, 0},
      {29, 0},
      {30, 0},
      {59, 0},
      {32, 0},
      {98, 0},
      {90, 0},
      {107, 1},
      {108, 1},
      {109, 1},
      {110, 1},
      {111, 1},
      {112, 1},
      {113, 1},
      {99, 0}
   };
   private static final int[] REGION_ID = new int[81];
   private static final int[] REGION_TYPE = new int[81];
   public final SliderSetting opacity = this.addSetting(
      new SliderSetting("Opacity", "Region cell fill opacity.", 90.0, 10.0, 100.0, 5.0, "%")
   );
   public final BooleanSetting gridLines = this.addSetting(
      new BooleanSetting("Grid Lines", "Draw the accent lines between region cells.", true)
   );
   public final BooleanSetting cellNumbers = this.addSetting(
      new BooleanSetting("Cell Numbers", "Show each region's ID number in its cell.", true)
   );
   public final BooleanSetting legend = this.addSetting(
      new BooleanSetting("Legend", "Show the region-type colour legend below the grid.", true)
   );

   public RegionMapModule() {
      super("RegionMap", "DonutSMP server region map on the HUD.", Category.WORLD);
   }

   private static int rgb(int r, int g, int b) {
      return r << 16 | g << 8 | b;
   }

   public boolean hasData() {
      return true;
   }

   public int mapSize() {
      return 9;
   }

   public int regionTypeCount() {
      return TYPE_NAMES.length;
   }

   public String regionTypeName(int type) {
      return type >= 0 && type < TYPE_NAMES.length ? TYPE_NAMES[type] : "";
   }

   public int regionTypeRgb(int type) {
      return type >= 0 && type < TYPE_COLORS.length ? TYPE_COLORS[type] : 16777215;
   }

   public int regionTypeAt(int index) {
      return index >= 0 && index < REGION_TYPE.length ? REGION_TYPE[index] : -1;
   }

   public int regionIdAt(int index) {
      return index >= 0 && index < REGION_ID.length ? REGION_ID[index] : -1;
   }

   public int[] worldToGrid(double worldX, double worldZ) {
      int gx = (int)Math.floor((worldX + 225000.0) / 50000.0);
      int gz = (int)Math.floor((worldZ + 225000.0) / 50000.0);
      return new int[]{gx, gz};
   }

   public double[] worldToCellPosition(double worldX, double worldZ) {
      double cx = (worldX + 225000.0) % 50000.0 / 50000.0;
      double cz = (worldZ + 225000.0) % 50000.0 / 50000.0;
      if (cx < 0.0) {
         cx++;
      }

      if (cz < 0.0) {
         cz++;
      }

      return new double[]{Math.clamp(cx, 0.0, 1.0), Math.clamp(cz, 0.0, 1.0)};
   }

   public int regionIdAtWorld(double worldX, double worldZ) {
      int[] g = this.worldToGrid(worldX, worldZ);
      return g[0] >= 0 && g[0] < 9 && g[1] >= 0 && g[1] < 9 ? this.regionIdAt(g[1] * 9 + g[0]) : -1;
   }

   public int regionTypeAtWorld(double worldX, double worldZ) {
      int[] g = this.worldToGrid(worldX, worldZ);
      return g[0] >= 0 && g[0] < 9 && g[1] >= 0 && g[1] < 9 ? this.regionTypeAt(g[1] * 9 + g[0]) : -1;
   }

   public int currentRegionId() {
      class_746 player = class_310.method_1551().field_1724;
      return player == null ? -1 : this.regionIdAtWorld(player.method_23317(), player.method_23321());
   }

   static {
      for (int i = 0; i < LAYOUT.length && i < REGION_ID.length; i++) {
         if (LAYOUT[i].length >= 2) {
            REGION_ID[i] = LAYOUT[i][0];
            REGION_TYPE[i] = Math.min(LAYOUT[i][1], TYPE_NAMES.length - 1);
         } else {
            REGION_TYPE[i] = -1;
         }
      }
   }
}
