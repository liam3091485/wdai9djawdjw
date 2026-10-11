package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class LowHealthBackpedalModule extends Module {
   public final SliderSetting health = this.addSetting(new SliderSetting("Health", "Start moving backward at or below this health.", 6.0, 2.0, 20.0, 1.0));
   private boolean pressedByModule;

   public LowHealthBackpedalModule() {
      super("Low Health Backpedal", "Holds backward when your health drops below a chosen level.", Category.COMBAT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         boolean active = mc.field_1724.method_6032() <= this.health.getFloat();
         mc.field_1690.field_1913.method_23481(active);
         this.pressedByModule = active;
      } else {
         this.release(mc);
      }
   }

   @Override
   protected void onDisable() {
      this.release(class_310.method_1551());
   }

   private void release(class_310 mc) {
      if (this.pressedByModule && mc.field_1690 != null) {
         mc.field_1690.field_1913.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
