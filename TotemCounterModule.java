package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;

public final class TotemCounterModule extends Module {
   public final SliderSetting warnAt = this.addSetting(
      new SliderSetting("Warn At", "Notify when your total totem count reaches this amount", 2.0, 0.0, 10.0, 1.0)
   );
   private int lastCount = -1;

   public TotemCounterModule() {
      super("Totem Counter", "Tracks your totems and warns when your supply gets low.", Category.CLIENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null) {
         this.lastCount = -1;
      } else {
         int count = 0;

         for (int i = 0; i < 36; i++) {
            class_1799 stack = mc.field_1724.method_31548().method_5438(i);
            if (!stack.method_7960() && stack.method_31574(class_1802.field_8288)) {
               count += stack.method_7947();
            }
         }

         class_1799 offhand = mc.field_1724.method_6079();
         if (!offhand.method_7960() && offhand.method_31574(class_1802.field_8288)) {
            count += offhand.method_7947();
         }

         int threshold = this.warnAt.getInt();
         if (this.lastCount > threshold && count <= threshold) {
            LarpClient.notifications().pushInfo("Totems Left: " + count);
         }

         this.lastCount = count;
      }
   }

   @Override
   protected void onDisable() {
      this.lastCount = -1;
   }
}
