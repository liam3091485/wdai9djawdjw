package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_1799;
import net.minecraft.class_310;

public final class DurabilityAlertModule extends Module {
   public final SliderSetting percent = this.addSetting(
      new SliderSetting("Durability %", "Warn when a damageable item falls below this durability", 15.0, 5.0, 50.0, 5.0)
   );
   private long lastWarning;

   public DurabilityAlertModule() {
      super("Durability Alert", "Warns when an item in your inventory is close to breaking.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         long now = System.currentTimeMillis();
         if (now - this.lastWarning >= 5000L) {
            for (int i = 0; i < 36; i++) {
               class_1799 stack = mc.field_1724.method_31548().method_5438(i);
               if (!stack.method_7960() && stack.method_7936() > 0) {
                  float remaining = 1.0F - (float)stack.method_7919() / stack.method_7936();
                  if (remaining * 100.0F <= this.percent.getFloat()) {
                     LarpClient.notifications().pushInfo("Durability Low: " + stack.method_7909().toString());
                     this.lastWarning = now;
                     return;
                  }
               }
            }
         }
      }
   }
}
