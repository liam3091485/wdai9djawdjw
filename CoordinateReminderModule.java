package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class CoordinateReminderModule extends Module {
   public final SliderSetting minutes = this.addSetting(new SliderSetting("Minutes", "How often to show your current coordinates.", 10.0, 1.0, 60.0, 1.0));
   private long next;

   public CoordinateReminderModule() {
      super("Coordinate Reminder", "Periodically shows your current coordinates as a toast.", Category.WORLD);
   }

   @Override
   protected void onEnable() {
      this.next = System.currentTimeMillis() + (long)(this.minutes.getFloat() * 60000.0F);
   }

   @Override
   public void onTick() {
      if (System.currentTimeMillis() >= this.next) {
         class_310 mc = class_310.method_1551();
         if (mc.field_1724 != null && LarpClient.notifications() != null) {
            String s = String.format("XYZ %.0f / %.0f / %.0f", mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
            LarpClient.notifications().pushInfo(s);
         }

         this.next = System.currentTimeMillis() + (long)(this.minutes.getFloat() * 60000.0F);
      }
   }

   @Override
   protected void onDisable() {
      this.next = 0L;
   }
}
