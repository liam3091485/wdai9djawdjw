package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_310;

public final class FirstFilledSlotModule extends Module {
   public FirstFilledSlotModule() {
      super("First Filled Slot", "Selects the first non-empty hotbar slot.", Category.PLAYER);
   }

   @Override
   protected void onEnable() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         for (int i = 0; i < 9; i++) {
            if (!mc.field_1724.method_31548().method_5438(i).method_7960()) {
               InventoryHelper.selectHotbarSlot(i);
               break;
            }
         }
      }

      this.setEnabled(false);
   }
}
