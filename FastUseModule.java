package com.larpclient.module.impl;

import com.larpclient.mixin.MinecraftAccessor;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.ModeSetting;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_310;

public class FastUseModule extends Module {
   public final ModeSetting items = this.addSetting(
      new ModeSetting(
         "Items",
         "What to speed up",
         "All",
         "All",
         "Pearls",
         "XP Bottles"
      )
   );

   public FastUseModule() {
      super("FastUse", "Removes item use cooldowns", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         if (this.appliesTo(mc)) {
            ((MinecraftAccessor)mc).larpclient$setRightClickDelay(0);
         }
      }
   }

   private boolean appliesTo(class_310 mc) {
      if (this.items.is("All")) {
         return true;
      } else {
         class_1792 target = this.items.is("Pearls") ? class_1802.field_8634 : class_1802.field_8287;
         return mc.field_1724.method_6047().method_31574(target) || mc.field_1724.method_6079().method_31574(target);
      }
   }
}
