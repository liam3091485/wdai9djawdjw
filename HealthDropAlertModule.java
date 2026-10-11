package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class HealthDropAlertModule extends Module {
   public final SliderSetting amount = this.addSetting(new SliderSetting("Drop", "Notify after losing at least this much health at once.", 4.0, 1.0, 10.0, 1.0));
   private float last = -1.0F;

   public HealthDropAlertModule() {
      super("Health Drop Alert", "Shows a toast after a large health drop.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null) {
         this.last = -1.0F;
      } else {
         float hp = mc.field_1724.method_6032();
         if (this.last >= 0.0F && this.last - hp >= this.amount.getFloat() && LarpClient.notifications() != null) {
            LarpClient.notifications().pushInfo("Health dropped to " + Math.round(hp) + " HP");
         }

         this.last = hp;
      }
   }

   @Override
   protected void onDisable() {
      this.last = -1.0F;
   }
}
