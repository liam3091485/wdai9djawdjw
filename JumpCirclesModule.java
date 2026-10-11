package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ColorSetting;
import com.larpclient.settings.SliderSetting;
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.class_2390;
import net.minecraft.class_5819;
import net.minecraft.class_746;

public class JumpCirclesModule extends Module {
   public final SliderSetting size = this.addSetting(
      new SliderSetting("Size", "Decal scale (1.0 = one block wide)", 1.0, 0.5, 3.0, 0.1, "x")
   );
   public final ColorSetting color = this.addSetting(
      new ColorSetting("Color", "Decal color (alpha is animated)", -16734209)
   );
   public final SliderSetting lifetime = this.addSetting(
      new SliderSetting("Lifetime", "How long a LARP stays visible", 1.5, 0.5, 4.0, 0.1, "s")
   );
   public final BooleanSetting rainbow = this.addSetting(
      new BooleanSetting("Rainbow", "Cycle the color through the rainbow", false)
   );
   public final BooleanSetting shockwave = this.addSetting(
      new BooleanSetting("Shockwave", "Expanding ring on spawn", true)
   );
   public final BooleanSetting particles = this.addSetting(
      new BooleanSetting("Particles", "Drifting dust motes on jump", true)
   );
   public final SliderSetting maxCircles = this.addSetting(
      new SliderSetting("Max", "Max simultaneous decals (oldest drops first)", 10.0, 1.0, 30.0, 1.0)
   );
   private final Deque<JumpCirclesModule.JumpCircle> circles = new ArrayDeque<>();
   private int spawnCounter;

   public JumpCirclesModule() {
      super("LARP Jump", "Stamps a glowing blue LARP logo on the ground when you jump", Category.CLIENT);
   }

   public Deque<JumpCirclesModule.JumpCircle> circles() {
      return this.circles;
   }

   @Override
   protected void onDisable() {
      this.clear();
   }

   public void clear() {
      this.circles.clear();
   }

   public int baseRgb() {
      return this.color.get() & 16777215;
   }

   public void onPlayerJump(class_746 player) {
      if (this.isEnabled()) {
         double x = player.method_23317();
         double y = player.method_23318();
         double z = player.method_23321();
         float yLift = 0.01F + this.spawnCounter % 8 * 0.001F;
         this.spawnCounter++;
         this.circles.addLast(new JumpCirclesModule.JumpCircle(x, y, z, player.method_36454(), yLift, System.nanoTime()));
         int max = Math.max(1, this.maxCircles.getInt());

         while (this.circles.size() > max) {
            this.circles.removeFirst();
         }

         if (this.particles.get()) {
            this.spawnParticles(player);
         }
      }
   }

   private void spawnParticles(class_746 player) {
      class_5819 random = player.method_59922();
      int rgb = this.baseRgb();

      for (int i = 0; i < 10; i++) {
         double angle = random.method_43058() * Math.PI * 2.0;
         double dist = 0.15 + random.method_43058() * 0.45;
         player.method_73183()
            .method_8406(
               new class_2390(rgb, 0.9F),
               player.method_23317() + Math.cos(angle) * dist,
               player.method_23318() + 0.05,
               player.method_23321() + Math.sin(angle) * dist,
               0.0,
               0.6 + random.method_43058() * 0.4,
               0.0
            );
      }
   }

   public static final class JumpCircle {
      public final double x;
      public final double y;
      public final double z;
      public final float yawDegrees;
      public final float yLift;
      public final long spawnNanos;

      JumpCircle(double x, double y, double z, float yawDegrees, float yLift, long spawnNanos) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.yawDegrees = yawDegrees;
         this.yLift = yLift;
         this.spawnNanos = spawnNanos;
      }

      public float ageSeconds(long nowNanos) {
         return (float)(nowNanos - this.spawnNanos) / 1.0E9F;
      }
   }
}
