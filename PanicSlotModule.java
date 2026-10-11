package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_310;

public final class PanicSlotModule extends Module {
   public final SliderSetting slot = this.addSetting(new SliderSetting("Slot", "Emergency hotbar slot to hold while enabled.", 1.0, 1.0, 9.0, 1.0));

   public PanicSlotModule() {
      super("Panic Slot", "Keeps a chosen emergency hotbar slot selected.", Category.COMBAT);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && mc.field_1755 == null) {
         InventoryHelper.selectHotbarSlot(Math.max(0, Math.min(8, this.slot.getInt() - 1)));
      }
   }
}
