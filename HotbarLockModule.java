package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_310;

public final class HotbarLockModule extends Module {
   public final SliderSetting slot = this.addSetting(new SliderSetting("Slot", "Hotbar slot to keep selected.", 1.0, 1.0, 9.0, 1.0));

   public HotbarLockModule() {
      super("Hotbar Lock", "Keeps a selected hotbar slot active until disabled.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && mc.field_1755 == null) {
         InventoryHelper.selectHotbarSlot(this.slot.getInt() - 1);
      }
   }
}
