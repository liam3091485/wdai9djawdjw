package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class HealthRecoveryAlertModule extends Module {
   public final SliderSetting threshold = this.addSetting(new SliderSetting("Health", "Notify after health recovers to this value.", 16.0, 2.0, 20.0, 1.0));
   private boolean wasBelow;

   public HealthRecoveryAlertModule() {
      super("Health Recovery", "Notifies you when your health recovers above a chosen value.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         float hp = mc.field_1724.method_6032();
         if (hp < this.threshold.getFloat()) {
            this.wasBelow = true;
         } else if (this.wasBelow) {
            this.wasBelow = false;
            if (LarpClient.notifications() != null) {
               LarpClient.notifications().pushInfo("Health recovered: " + Math.round(hp) + " HP");
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.wasBelow = false;
   }
}
