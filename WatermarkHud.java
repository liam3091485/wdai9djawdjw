package com.larpclient.hud.components;

import com.larpclient.hud.HudComponent;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import java.util.function.BooleanSupplier;

public class WatermarkHud extends HudComponent {
   private static final float HEIGHT = 30.0F;
   private final ThemeManager themes;

   public WatermarkHud(ThemeManager themes, BooleanSupplier visible) {
      super("LARP Client", 0.006F, 0.01F, visible);
      this.themes = themes;
   }

   @Override
   public float measureWidth(NVGRenderer vg) {
      return 13.0F + vg.textWidth("LARP", 18.0F) + 7.0F + vg.textWidth("CLIENT", 13.0F) + 13.0F;
   }

   @Override
   public float measureHeight(NVGRenderer vg) {
      return 30.0F;
   }

   @Override
   public void render(NVGRenderer vg, float x, float y, float w, float h) {
      Theme theme = this.themes.current();
      float cy = y + h / 2.0F;
      float pulse = (float)(0.5 + 0.5 * Math.sin(System.nanoTime() / 9.0E8));
      int left = Colors.lerp(theme.accent(), theme.accentBright(), pulse * 0.45F);
      int right = Colors.lerp(theme.accentBright(), -13058568, 0.35F);
      vg.glow(x, y, w, h, h / 2.0F, 9.0F, Colors.withAlpha(theme.accent(), 0.12F + 0.08F * pulse));
      vg.rectGradient(x, y, w, h, h / 2.0F, Colors.withAlpha(-16051680, 0.94F), Colors.withAlpha(-15656402, 0.94F), true);
      vg.rectOutline(x, y, w, h, h / 2.0F, 1.0F, Colors.withAlpha(theme.accentBright(), 0.62F));
      float tx = x + 13.0F;
      vg.textGlow("LARP", tx, cy, 18.0F, Colors.withAlpha(theme.accent(), 0.42F));
      vg.textGradient("LARP", tx, cy, 18.0F, left, right);
      tx += vg.textWidth("LARP", 18.0F) + 7.0F;
      vg.circle(tx - 4.5F, cy, 1.4F, Colors.withAlpha(theme.accentBright(), 0.8F));
      vg.text("CLIENT", tx, cy, 13.0F, Colors.withAlpha(theme.textPrimary(), 0.92F));
   }
}
