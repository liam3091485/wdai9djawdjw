package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;
import com.larpclient.util.BlockHelper;
import com.larpclient.util.InventoryHelper;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import org.lwjgl.glfw.GLFW;

public class AnchorMacroModule extends Module {
   public final SliderSetting switchDelay = this.addSetting(new SliderSetting("Switch Delay", "", 0.0, 0.0, 20.0, 1.0));
   public final SliderSetting glowstoneDelay = this.addSetting(
      new SliderSetting("Glowstone Delay", "", 0.0, 0.0, 20.0, 1.0)
   );
   public final SliderSetting explodeDelay = this.addSetting(
      new SliderSetting("Explode Delay", "", 0.0, 0.0, 20.0, 1.0)
   );
   public final SliderSetting totemSlot = this.addSetting(
      new SliderSetting("Totem Slot", "", 1.0, 1.0, 9.0, 1.0)
   );
   public final BooleanSetting autoSwitchBack = this.addSetting(new BooleanSetting("Auto Switch Back", "", true));
   public final SliderSetting switchBackDelay = this.addSetting(
      new SliderSetting("Switch Back Delay", "", 2.0, 0.0, 20.0, 1.0)
   );
   private int step;
   private int tickCounter;
   private class_2338 targetPos;
   private int previousSlot = -1;

   public AnchorMacroModule() {
      super("Anchor Macro", "Automatically blows up respawn anchors", Category.COMBAT);
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   @Override
   protected void onDisable() {
      this.resetState();
   }

   @Override
   public void onTick() {
      class_310 client = class_310.method_1551();
      if (client.field_1724 != null && client.field_1761 != null && client.field_1687 != null) {
         if (this.step > 0) {
            this.processStep(client);
         } else {
            long handle = client.method_22683().method_4490();
            boolean rmb = GLFW.glfwGetMouseButton(handle, 1) == 1;
            class_3965 hit;
            class_2338 pos;
            if (rmb
               && client.field_1765 != null
               && client.field_1765.method_17783() == class_240.field_1332
               && BlockHelper.isBlockAt(pos = (hit = (class_3965)client.field_1765).method_17777(), class_2246.field_23152)) {
               this.targetPos = pos;
               this.previousSlot = client.field_1724.method_31548().method_67532();
               if (BlockHelper.isAnchorUncharged(pos)) {
                  this.step = 1;
                  this.tickCounter = 0;
               } else if (BlockHelper.isAnchorCharged(pos)) {
                  this.step = 3;
                  this.tickCounter = 0;
               }
            }
         }
      }
   }

   private void processStep(class_310 client) {
      this.tickCounter++;
      class_239 class_2392 = client.field_1765;
      class_3965 bhr;
      class_3965 hit = class_2392 instanceof class_3965 ? (bhr = (class_3965)class_2392) : null;
      switch (this.step) {
         case 1:
            if (this.tickCounter >= this.switchDelay.getInt()) {
               InventoryHelper.swapToItem(class_1802.field_8801);
               this.step = 2;
               this.tickCounter = 0;
            }
            break;
         case 2:
            if (this.tickCounter >= this.glowstoneDelay.getInt()) {
               if (hit != null && this.targetPos.equals(hit.method_17777())) {
                  BlockHelper.interactBlock(hit, true);
               }

               this.step = 3;
               this.tickCounter = 0;
            }
            break;
         case 3:
            if (this.tickCounter >= this.explodeDelay.getInt()) {
               InventoryHelper.swap(this.totemSlot.getInt() - 1);
               this.step = 4;
               this.tickCounter = 0;
            }
            break;
         case 4:
            if (this.tickCounter >= 1) {
               if (hit != null && this.targetPos.equals(hit.method_17777())) {
                  BlockHelper.interactBlock(hit, true);
               }

               if (this.autoSwitchBack.get() && this.previousSlot >= 0) {
                  this.step = 5;
                  this.tickCounter = 0;
               } else {
                  this.resetState();
               }
            }
            break;
         case 5:
            if (this.tickCounter >= this.switchBackDelay.getInt()) {
               InventoryHelper.swap(this.previousSlot);
               this.resetState();
            }
            break;
         default:
            this.resetState();
      }
   }

   private void resetState() {
      this.step = 0;
      this.tickCounter = 0;
      this.targetPos = null;
      this.previousSlot = -1;
   }
}
