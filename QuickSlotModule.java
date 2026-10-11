package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.util.InventoryHelper;

public final class QuickSlotModule extends Module {
   private final int slot;

   public QuickSlotModule(int slot) {
      super("Quick Slot " + slot, "Instantly selects hotbar slot " + slot + ".", Category.PLAYER);
      this.slot = Math.max(1, Math.min(9, slot));
   }

   @Override
   protected void onEnable() {
      InventoryHelper.selectHotbarSlot(this.slot - 1);
      this.setEnabled(false);
   }
}
