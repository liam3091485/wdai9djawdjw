package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.util.InventoryHelper;

public final class EmptySlotModule extends Module {
   public EmptySlotModule() {
      super("Empty Slot", "Selects the first empty hotbar slot.", Category.PLAYER);
   }

   @Override
   protected void onEnable() {
      int slot = InventoryHelper.findEmptyHotbarSlot();
      if (slot >= 0) {
         InventoryHelper.selectHotbarSlot(slot);
      } else if (LarpClient.notifications() != null) {
         LarpClient.notifications().pushInfo("No empty hotbar slot");
      }

      this.setEnabled(false);
   }
}
