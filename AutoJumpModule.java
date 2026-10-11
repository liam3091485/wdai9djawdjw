package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import net.minecraft.class_310;

public final class AutoJumpModule extends Module {
   public final BooleanSetting requireMovement = this.addSetting(
      new BooleanSetting("Require Movement", "Only jump while one of the movement keys is held.", true)
   );
   private boolean pressedByModule;

   public AutoJumpModule() {
      super("Auto Jump", "Automatically holds jump while you are moving.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         boolean moving = mc.field_1690.field_1894.method_1434()
            || mc.field_1690.field_1881.method_1434()
            || mc.field_1690.field_1913.method_1434()
            || mc.field_1690.field_1849.method_1434();
         boolean shouldJump = !this.requireMovement.get() || moving;
         mc.field_1690.field_1903.method_23481(shouldJump);
         this.pressedByModule = shouldJump;
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
