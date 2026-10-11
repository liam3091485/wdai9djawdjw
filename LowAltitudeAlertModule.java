package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class LowAltitudeAlertModule extends Module {
   public final SliderSetting y = this.addSetting(new SliderSetting("Y Level", "Notify when you fall below this Y level.", 0.0, -64.0, 64.0, 4.0));
   private boolean warned;

   public LowAltitudeAlertModule() {
      super("Low Altitude Alert", "Notifies you when you reach a low Y level.", Category.WORLD);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         boolean low = mc.field_1724.method_23318() <= this.y.getFloat();
         if (low && !this.warned && LarpClient.notifications() != null) {
            LarpClient.notifications().pushInfo("Low altitude: Y " + Math.round(mc.field_1724.method_23318()));
         }

         this.warned = low;
      }
   }

   @Override
   protected void onDisable() {
      this.warned = false;
   }
}
