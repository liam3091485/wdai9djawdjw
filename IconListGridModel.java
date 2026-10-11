package com.larpclient.gui.picker;

import com.larpclient.settings.ColorSetting;
import com.larpclient.settings.IconListSetting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1799;

public final class IconListGridModel implements PickerGrid {
   private final IconListSetting setting;
   private List<PickerGrid.Cell> cells;

   public IconListGridModel(IconListSetting setting) {
      this.setting = setting;
   }

   @Override
   public String title() {
      return this.setting.getName();
   }

   @Override
   public long activeCount() {
      return this.setting.enabledCount();
   }

   @Override
   public List<PickerGrid.Cell> cells() {
      if (this.cells == null) {
         ArrayList<PickerGrid.Cell> out = new ArrayList<>(this.setting.entries().size());

         for (IconListSetting.Entry entry : this.setting.entries()) {
            out.add(new IconListGridModel.EntryCell(entry));
         }

         this.cells = out;
      }

      return this.cells;
   }

   private static final class EntryCell implements PickerGrid.Cell {
      private final IconListSetting.Entry entry;
      private final class_1799 icon;

      EntryCell(IconListSetting.Entry entry) {
         this.entry = entry;
         this.icon = new class_1799(entry.icon());
      }

      @Override
      public class_1799 icon() {
         return this.icon;
      }

      @Override
      public String label() {
         return this.entry.label();
      }

      @Override
      public boolean matches(String lowerQuery) {
         return this.entry.matches(lowerQuery);
      }

      @Override
      public boolean tracked() {
         return true;
      }

      @Override
      public boolean enabled() {
         return this.entry.enabled.get();
      }

      @Override
      public boolean selected() {
         return this.entry.enabled.get();
      }

      @Override
      public int color() {
         return this.entry.color.get();
      }

      @Override
      public void toggle() {
         this.entry.enabled.toggle();
      }

      @Override
      public ColorSetting colorTarget() {
         return this.entry.color;
      }
   }
}
