package com.larpclient.theme;

import com.larpclient.util.Colors;

public class RainbowTheme extends Theme {
   public RainbowTheme() {
      super("Spectrum", -12877066, false);
   }

   @Override
   public int accent() {
      double seconds = System.nanoTime() % 1000000000000L / 1.0E9;
      return Colors.hsvToRgb((float)(seconds * 36.0 % 360.0), 0.72F, 1.0F);
   }
}
