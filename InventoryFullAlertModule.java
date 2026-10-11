package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class InventoryFullAlertModule extends Module {
   private boolean warned;

   public InventoryFullAlertModule() {
      super("Inventory Full Alert", "Warns when your main inventory has no empty slots.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null) {
         this.warned = false;
      } else {
         boolean full = true;

         for (int i = 0; i < 36; i++) {
            if (mc.field_1724.method_31548().method_5438(i).method_7960()) {
               full = false;
               break;
            }
         }

         if (full && !this.warned) {
            LarpClient.notifications().pushInfo("Inventory Full");
         }

         this.warned = full;
      }
   }

   @Override
   protected void onDisable() {
      this.warned = false;
   }
}
