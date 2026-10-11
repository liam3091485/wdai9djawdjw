package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_1799;
import net.minecraft.class_310;

public final class ItemSaverModule extends Module {
   public final SliderSetting percent = this.addSetting(
      new SliderSetting("Durability %", "Switch away when the held item reaches this remaining durability.", 5.0, 1.0, 25.0, 1.0)
   );
   private long lastAction;

   public ItemSaverModule() {
      super("Item Saver", "Protects your held tool by switching slots before it breaks.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && mc.field_1755 == null) {
         class_1799 held = mc.field_1724.method_6047();
         if (!held.method_7960() && held.method_7936() > 0) {
            float remaining = 1.0F - (float)held.method_7919() / held.method_7936();
            if (!(remaining * 100.0F > this.percent.getFloat())) {
               if (System.currentTimeMillis() - this.lastAction >= 1200L) {
                  int current = mc.field_1724.method_31548().method_67532();

                  for (int offset = 1; offset <= 9; offset++) {
                     int candidate = (current + offset) % 9;
                     class_1799 stack = mc.field_1724.method_31548().method_5438(candidate);
                     if (!stack.method_7960()) {
                        InventoryHelper.selectHotbarSlot(candidate);
                        this.lastAction = System.currentTimeMillis();
                        if (LarpClient.notifications() != null) {
                           LarpClient.notifications().pushInfo("Item Saver switched away from low durability item");
                        }

                        return;
                     }
                  }
               }
            }
         }
      }
   }
}
