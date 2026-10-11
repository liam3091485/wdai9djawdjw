package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import net.minecraft.class_310;

public final class AutoSprintModule extends Module {
   public final BooleanSetting requireForward = this.addSetting(
      new BooleanSetting("Require Forward", "Only hold sprint while the forward key is pressed.", true)
   );
   private boolean pressedByModule;

   public AutoSprintModule() {
      super("Auto Sprint", "Automatically holds the vanilla sprint key while moving.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null && mc.field_1724 != null && mc.field_1755 == null) {
         boolean shouldSprint = !this.requireForward.get() || mc.field_1690.field_1894.method_1434();
         mc.field_1690.field_1867.method_23481(shouldSprint);
         this.pressedByModule = shouldSprint;
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
         mc.field_1690.field_1867.method_23481(false);
      }

      this.pressedByModule = false;
   }
}
