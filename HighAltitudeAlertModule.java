package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class HighAltitudeAlertModule extends Module {
   public final SliderSetting y = this.addSetting(new SliderSetting("Y Level", "Notify when you rise above this Y level.", 200.0, 64.0, 320.0, 8.0));
   private boolean warned;

   public HighAltitudeAlertModule() {
      super("High Altitude Alert", "Notifies you when you reach a high Y level.", Category.WORLD);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         boolean high = mc.field_1724.method_23318() >= this.y.getFloat();
         if (high && !this.warned && LarpClient.notifications() != null) {
            LarpClient.notifications().pushInfo("High altitude: Y " + Math.round(mc.field_1724.method_23318()));
         }

         this.warned = high;
      }
   }

   @Override
   protected void onDisable() {
      this.warned = false;
   }
}
