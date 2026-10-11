package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.render.MotionBlurRenderer;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;

public class MotionBlurModule extends Module {
   public final SliderSetting strength = this.addSetting(
      new SliderSetting("Strength", "Blur strength", 30.0, 5.0, 100.0, 5.0, "%")
         .withLabel(
            v -> {
               int pct = (int)Math.round(v);
               String tier = pct <= 20
                  ? "Subtle"
                  : (
                     pct <= 45
                        ? "Balanced"
                        : (pct <= 70 ? "Smooth" : (pct <= 90 ? "Heavy" : "Cinematic"))
                  );
               return pct + "% · " + tier;
            }
         )
   );
   public final BooleanSetting pinkTrails = this.addSetting(
      new BooleanSetting("Pink Trails", "Tint the trails with your theme accent (LARP look)", true)
   );
   public final SliderSetting tint = this.addSetting(
      new SliderSetting("Tint", "How strongly trails take the theme color", 30.0, 0.0, 100.0, 5.0, "%")
   );
   public final BooleanSetting fpsCompensated = this.addSetting(
      new BooleanSetting("FPS Compensated", "Keep the blur consistent across framerates", true)
   );

   public MotionBlurModule() {
      super("MotionBlur", "Cinematic motion blur", Category.VISUALS);
      this.tint.visibleWhen(this.pinkTrails::get);
   }

   @Override
   protected void onDisable() {
      MotionBlurRenderer.reset();
   }

   public double retention() {
      return Math.clamp(this.strength.getFloat() / 100.0, 0.05, 0.95);
   }

   public float tintAmount() {
      return this.pinkTrails.get() ? (float)Math.clamp(this.tint.getFloat() / 100.0, 0.0, 1.0) : 0.0F;
   }

   public int accentColor() {
      return LarpClient.themes().current().accent();
   }
}
