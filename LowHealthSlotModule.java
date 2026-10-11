package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_310;

public final class LowHealthSlotModule extends Module {
   public final SliderSetting health = this.addSetting(new SliderSetting("Health", "Switch when health is at or below this value.", 8.0, 2.0, 20.0, 1.0));
   public final SliderSetting slot = this.addSetting(new SliderSetting("Slot", "Hotbar slot to select at low health.", 1.0, 1.0, 9.0, 1.0));
   public final BooleanSetting restore = this.addSetting(new BooleanSetting("Restore Slot", "Return to the previous slot after health recovers.", true));
   private int previousSlot = -1;
   private boolean triggered;

   public LowHealthSlotModule() {
      super("Low Health Slot", "Switches to a chosen hotbar slot when your health gets low.", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.previousSlot = -1;
      this.triggered = false;
   }

   @Override
   protected void onDisable() {
      this.restorePrevious();
      this.triggered = false;
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && mc.field_1755 == null) {
         boolean low = mc.field_1724.method_6032() <= this.health.getFloat();
         if (low && !this.triggered) {
            this.previousSlot = mc.field_1724.method_31548().method_67532();
            InventoryHelper.selectHotbarSlot(this.slot.getInt() - 1);
            this.triggered = true;
         } else if (!low && this.triggered) {
            if (this.restore.get()) {
               this.restorePrevious();
            }

            this.triggered = false;
            this.previousSlot = -1;
         }
      }
   }

   private void restorePrevious() {
      if (this.previousSlot >= 0 && this.previousSlot <= 8) {
         InventoryHelper.selectHotbarSlot(this.previousSlot);
      }
   }
}
