package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class SlotReminderModule extends Module {
   public final SliderSetting slot = this.addSetting(new SliderSetting("Preferred Slot", "Hotbar slot to remind you to return to.", 1.0, 1.0, 9.0, 1.0));
   private boolean warned;

   public SlotReminderModule() {
      super("Slot Reminder", "Reminds you when you move away from a preferred hotbar slot.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         int selected = mc.field_1724.method_31548().method_67532();
         boolean away = selected != this.slot.getInt() - 1;
         if (away && !this.warned && LarpClient.notifications() != null) {
            LarpClient.notifications().pushInfo("Preferred slot: " + this.slot.getInt());
         }

         this.warned = away;
      }
   }

   @Override
   protected void onDisable() {
      this.warned = false;
   }
}
