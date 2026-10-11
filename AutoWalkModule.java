package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ModeSetting;
import net.minecraft.class_310;

public final class AutoWalkModule extends Module {
   public final ModeSetting direction = this.addSetting(
      new ModeSetting("Direction", "Direction to hold while Auto Walk is enabled.", "Forward", "Forward", "Backward", "Left", "Right")
   );
   public final BooleanSetting sprint = this.addSetting(new BooleanSetting("Sprint", "Also hold the vanilla sprint key while walking forward.", true));
   public final BooleanSetting pauseInScreens = this.addSetting(
      new BooleanSetting("Pause In Screens", "Release movement while an inventory or menu is open.", true)
   );

   public AutoWalkModule() {
      super("Auto Walk", "Keeps a movement direction held until disabled.", Category.MOVEMENT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 == null || mc.field_1724 == null) {
         this.release(mc);
      } else if (this.pauseInScreens.get() && mc.field_1755 != null) {
         this.release(mc);
      } else {
         this.releaseMovement(mc);
         if (this.direction.is("Backward")) {
            mc.field_1690.field_1913.method_23481(true);
         } else if (this.direction.is("Left")) {
            mc.field_1690.field_1881.method_23481(true);
         } else if (this.direction.is("Right")) {
            mc.field_1690.field_1849.method_23481(true);
         } else {
            mc.field_1690.field_1894.method_23481(true);
            if (this.sprint.get()) {
               mc.field_1690.field_1867.method_23481(true);
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.release(class_310.method_1551());
   }

   private void releaseMovement(class_310 mc) {
      mc.field_1690.field_1894.method_23481(false);
      mc.field_1690.field_1881.method_23481(false);
      mc.field_1690.field_1913.method_23481(false);
      mc.field_1690.field_1849.method_23481(false);
      mc.field_1690.field_1867.method_23481(false);
   }

   private void release(class_310 mc) {
      if (mc.field_1690 != null) {
         this.releaseMovement(mc);
      }
   }
}
