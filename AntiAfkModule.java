package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class AntiAfkModule extends Module {
   public final SliderSetting intervalSeconds = this.addSetting(
      new SliderSetting("Interval", "Seconds between small anti-AFK actions.", 45.0, 15.0, 300.0, 5.0)
   );
   public final BooleanSetting notify = this.addSetting(new BooleanSetting("Notify", "Show a small notification when an anti-AFK action runs.", false));
   private long nextAction;
   private int releaseTicks;

   public AntiAfkModule() {
      super("Anti AFK", "Periodically taps jump to help prevent idle timeouts.", Category.MISC);
   }

   @Override
   protected void onEnable() {
      this.schedule();
   }

   @Override
   protected void onDisable() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null) {
         mc.field_1690.field_1903.method_23481(false);
      }

      this.releaseTicks = 0;
      this.nextAction = 0L;
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         if (this.releaseTicks > 0) {
            this.releaseTicks--;
            if (this.releaseTicks == 0) {
               mc.field_1690.field_1903.method_23481(false);
            }
         } else if (System.currentTimeMillis() >= this.nextAction) {
            mc.field_1690.field_1903.method_23481(true);
            this.releaseTicks = 2;
            this.schedule();
            if (this.notify.get() && LarpClient.notifications() != null) {
               LarpClient.notifications().pushInfo("Anti AFK action sent");
            }
         }
      }
   }

   private void schedule() {
      this.nextAction = System.currentTimeMillis() + (long)(this.intervalSeconds.getFloat() * 1000.0F);
   }
}
