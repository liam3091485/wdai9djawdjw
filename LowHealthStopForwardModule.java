package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public final class LowHealthStopForwardModule extends Module {
   public final SliderSetting health = this.addSetting(new SliderSetting("Health", "Release forward at or below this health.", 4.0, 2.0, 20.0, 1.0));

   public LowHealthStopForwardModule() {
      super("Low Health Stop Forward", "Releases forward movement when critically low on health.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1724.method_6032() <= this.health.getFloat()) {
         mc.field_1690.field_1894.method_23481(false);
      }
   }
}
