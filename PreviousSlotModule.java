package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_310;

public final class PreviousSlotModule extends Module {
   public PreviousSlotModule() {
      super("Previous Slot", "Moves to the previous hotbar slot.", Category.PLAYER);
   }

   @Override
   protected void onEnable() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         int current = mc.field_1724.method_31548().method_67532();
         InventoryHelper.selectHotbarSlot((current + 8) % 9);
      }

      this.setEnabled(false);
   }
}
