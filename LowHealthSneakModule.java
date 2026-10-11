package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class LowHealthSneakModule extends Module {
   public final SliderSetting threshold = this.addSetting(new SliderSetting("Health", "Start sneaking at or below this health.", 6.0, 2.0, 20.0, 1.0));
   private boolean pressedByModule;

   public LowHealthSneakModule() {
      super("Low Health Sneak", "Automatically holds sneak when your health is low.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         boolean active = mc.field_1724.method_6032() <= this.threshold.getFloat();
         mc.field_1690.field_1832.method_23481(active);
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
         mc.field_1690.field_1832.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
