package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class LowHealthStopSprintModule extends Module {
   public final SliderSetting health = this.addSetting(new SliderSetting("Health", "Release sprint at or below this health.", 6.0, 2.0, 20.0, 1.0));

   public LowHealthStopSprintModule() {
      super("Low Health Stop Sprint", "Stops holding sprint while your health is low.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1724.method_6032() <= this.health.getFloat()) {
         mc.field_1690.field_1867.method_23481(false);
      }
   }
}
