package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class CustomFovModule extends Module {
   private static final double EASE_SPEED = 12.0;
   private static final double SPRINT_SPEED = 0.2825;
   public final SliderSetting fov = this.addSetting(
      new SliderSetting("FOV", "Target field of view", 95.0, 30.0, 140.0, 1.0, "\u00b0")
         .withLabel(v -> (int)v + "° · " + tag((int)v))
   );
   public final BooleanSetting smooth = this.addSetting(
      new BooleanSetting("Smooth", "Ease FOV changes in and out", true)
   );
   public final BooleanSetting noSprintZoom = this.addSetting(
      new BooleanSetting("No Sprint Zoom", "Cancel the vanilla sprint / speed FOV punch", true)
   );
   public final BooleanSetting speedFov = this.addSetting(
      new BooleanSetting("Speed FOV", "Widen the view with your movement speed", false)
   );
   public final SliderSetting speedStrength = this.addSetting(
      new SliderSetting(
         "Speed Strength", "Extra degrees at full sprint", 12.0, 0.0, 30.0, 1.0, "\u00b0"
      )
   );
   private double current = -1.0;
   private long lastNanos = 0L;

   public CustomFovModule() {
      super("CustomFOV", "Overrides the field of view", Category.MISC);
      this.speedStrength.visibleWhen(this.speedFov::get);
   }

   public float fovMultiplier(float vanillaSprintMultiplier) {
      class_310 mc = class_310.method_1551();
      int optionsFov = (Integer)mc.field_1690.method_41808().method_41753();
      if (optionsFov <= 0) {
         return vanillaSprintMultiplier;
      } else {
         double target = this.isEnabled() ? this.fov.get() + this.speedBonus(mc) : optionsFov;
         long now = System.nanoTime();
         double dt = this.lastNanos == 0L ? 0.0 : (now - this.lastNanos) / 1.0E9;
         this.lastNanos = now;
         if (this.current < 0.0) {
            this.current = optionsFov;
         }

         if (!this.smooth.get()) {
            this.current = target;
         } else {
            double t = 1.0 - Math.exp(-12.0 * Math.max(0.0, dt));
            this.current = this.current + (target - this.current) * t;
            if (Math.abs(this.current - target) < 0.05) {
               this.current = target;
            }
         }

         if (!this.isEnabled() && this.current == optionsFov) {
            return vanillaSprintMultiplier;
         } else {
            float sprint = this.isEnabled() && this.noSprintZoom.get() ? 1.0F : vanillaSprintMultiplier;
            return (float)(this.current / optionsFov) * sprint;
         }
      }
   }

   public double currentFov() {
      return this.current;
   }

   private double speedBonus(class_310 mc) {
      if (!this.speedFov.get()) {
         return 0.0;
      } else {
         class_746 p = mc.field_1724;
         if (p == null) {
            return 0.0;
         } else {
            class_243 v = p.method_18798();
            double horizontal = Math.sqrt(v.field_1352 * v.field_1352 + v.field_1350 * v.field_1350);
            double t = Math.min(1.0, horizontal / 0.2825);
            return this.speedStrength.getFloat() * t;
         }
      }
   }

   private static String tag(int v) {
      if (v <= 45) {
         return "Tunnel";
      } else if (v <= 65) {
         return "Focused";
      } else if (v <= 80) {
         return "Normal";
      } else if (v <= 100) {
         return "Wide";
      } else {
         return v <= 118 ? "Ultra" : "Fisheye";
      }
   }
}
