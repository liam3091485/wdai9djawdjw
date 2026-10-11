package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;

public final class BreakReminderModule extends Module {
   public final SliderSetting minutes = this.addSetting(new SliderSetting("Minutes", "Minutes between reminders", 60.0, 15.0, 180.0, 5.0));
   private long nextReminder;

   public BreakReminderModule() {
      super("Break Reminder", "Reminds you to take regular breaks during long sessions.", Category.PLAYER);
   }

   @Override
   protected void onEnable() {
      this.schedule();
   }

   @Override
   protected void onDisable() {
      this.nextReminder = 0L;
   }

   @Override
   public void onTick() {
      if (this.nextReminder != 0L && System.currentTimeMillis() >= this.nextReminder) {
         if (LarpClient.notifications() != null) {
            LarpClient.notifications().pushInfo("Break reminder • stretch, hydrate and rest your eyes");
         }

         this.schedule();
      }
   }

   private void schedule() {
      this.nextReminder = System.currentTimeMillis() + (long)(this.minutes.getFloat() * 60000.0F);
   }
}
