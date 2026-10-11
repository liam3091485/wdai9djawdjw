package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class LowHealthAlertModule extends Module {
   public final SliderSetting threshold = this.addSetting(
      new SliderSetting("Health", "Warn when your health falls to or below this value", 8.0, 2.0, 20.0, 1.0)
   );
   private boolean warned;

   public LowHealthAlertModule() {
      super("Low Health Alert", "Shows a notification when your health gets low.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null) {
         this.warned = false;
      } else {
         float health = mc.field_1724.method_6032();
         boolean low = health <= this.threshold.getFloat();
         if (low && !this.warned) {
            LarpClient.notifications().pushInfo("Low Health: " + Math.round(health) + " HP");
         }

         this.warned = low;
      }
   }

   @Override
   protected void onDisable() {
      this.warned = false;
   }
}
