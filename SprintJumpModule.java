package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class SprintJumpModule extends Module {
   private boolean pressedByModule;

   public SprintJumpModule() {
      super("Sprint Jump", "Jumps while you are moving forward and sprinting.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         boolean active = mc.field_1690.field_1894.method_1434() && mc.field_1690.field_1867.method_1434();
         mc.field_1690.field_1903.method_23481(active);
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
         mc.field_1690.field_1903.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
