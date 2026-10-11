package com.larpclient.theme;

import com.larpclient.util.Colors;

public class Theme {
   private final String name;
   private int accent;
   private final boolean custom;

   public Theme(String name, int accent, boolean custom) {
      this.name = name;
      this.accent = accent;
      this.custom = custom;
   }

   public String getName() {
      return this.name;
   }

   public int accent() {
      return this.accent;
   }

   public void setAccent(int accent) {
      this.accent = accent;
   }

   public boolean isCustom() {
      return this.custom;
   }

   public int accentBright() {
      return Colors.lighten(this.accent(), 0.35F);
   }

   public int accentHover() {
      return Colors.withAlpha(this.accent(), 0.35F);
   }

   public int background() {
      return Colors.withAlpha(-16117728, 0.94F);
   }

   public int backgroundTo() {
      return Colors.withAlpha(-15919573, 0.94F);
   }

   public int headerTop() {
      return Colors.withAlpha(-15655884, 0.97F);
   }

   public int headerBottom() {
      return Colors.withAlpha(-16051418, 0.97F);
   }

   public int moduleActiveFill() {
      return Colors.withAlpha(Colors.lerp(-15593450, this.accent(), 0.22F), 0.95F);
   }

   public int textPrimary() {
      return -722945;
   }

   public int textMuted() {
      return -6641214;
   }

   public int textDisabled() {
      return -10193781;
   }

   public int statusEnabled() {
      return -13058568;
   }

   public int statusDisabled() {
      return -10193781;
   }
}
