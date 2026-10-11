package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class HoldUseModule extends Module {
   private boolean pressedByModule;

   public HoldUseModule() {
      super("Hold Use", "Keeps the vanilla use-item key held until disabled.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         mc.field_1690.field_1904.method_23481(true);
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
         mc.field_1690.field_1904.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
