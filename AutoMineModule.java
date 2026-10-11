package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class AutoMineModule extends Module {
   private boolean pressedByModule;

   public AutoMineModule() {
      super("Auto Mine", "Holds the normal attack key for continuous mining.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         mc.field_1690.field_1886.method_23481(true);
         this.pressedByModule = true;
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
         mc.field_1690.field_1886.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
