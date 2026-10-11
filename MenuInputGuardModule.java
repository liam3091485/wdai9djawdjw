package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class MenuInputGuardModule extends Module {
   public MenuInputGuardModule() {
      super("Menu Input Guard", "Releases movement keys when a menu or inventory opens.", Category.CLIENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1755 != null) {
         mc.field_1690.field_1894.method_23481(false);
         mc.field_1690.field_1913.method_23481(false);
         mc.field_1690.field_1881.method_23481(false);
         mc.field_1690.field_1849.method_23481(false);
         mc.field_1690.field_1867.method_23481(false);
         mc.field_1690.field_1903.method_23481(false);
         mc.field_1690.field_1832.method_23481(false);
      }
   }
}
