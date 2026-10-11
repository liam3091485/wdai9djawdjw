package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class IdleSneakModule extends Module {
   public final SliderSetting delay = this.addSetting(new SliderSetting("Delay", "Seconds without movement before sneak is held.", 2.0, 0.5, 10.0, 0.5));
   private long idleSince = -1L;
   private boolean pressedByModule;

   public IdleSneakModule() {
      super("Idle Sneak", "Automatically sneaks after you stop moving for a short time.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         boolean moving = mc.field_1690.field_1894.method_1434()
            || mc.field_1690.field_1913.method_1434()
            || mc.field_1690.field_1881.method_1434()
            || mc.field_1690.field_1849.method_1434();
         if (moving) {
            this.idleSince = System.currentTimeMillis();
            this.release(mc);
         } else {
            if (this.idleSince < 0L) {
               this.idleSince = System.currentTimeMillis();
            }

            boolean shouldSneak = System.currentTimeMillis() - this.idleSince >= (long)(this.delay.getFloat() * 1000.0F);
            mc.field_1690.field_1832.method_23481(shouldSneak);
            this.pressedByModule = shouldSneak;
         }
      } else {
         this.release(mc);
         this.idleSince = -1L;
      }
   }

   @Override
   protected void onDisable() {
      this.release(class_310.method_1551());
      this.idleSince = -1L;
   }

   private void release(class_310 mc) {
      if (this.pressedByModule && mc.field_1690 != null) {
         mc.field_1690.field_1832.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
