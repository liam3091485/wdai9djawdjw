package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.KeybindSetting;
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

public class DoubleAnchorModule extends Module {
   public final KeybindSetting activateKey = this.addSetting(
      new KeybindSetting("Activate Key", "Hold to run the combo", 72)
   );
   public final SliderSetting timing = this.addSetting(
      new SliderSetting(
         "Timing", "Delay between the two blows", 120.0, 40.0, 400.0, 10.0, "ms"
      )
   );
   public final SliderSetting detonateSlot = this.addSetting(
      new SliderSetting(
         "Detonate Slot", "Hotbar slot to hold while detonating (anything but glowstone)", 1.0, 1.0, 9.0, 1.0
      )
   );
   public final BooleanSetting rePlace = this.addSetting(
      new BooleanSetting("Re-Place", "Place a fresh anchor for the second blow if the first is consumed", true)
   );
   public final BooleanSetting switchBack = this.addSetting(
      new BooleanSetting("Switch Back", "Return to the previous hotbar slot when done", true)
   );
   private int step;
   private int tickCounter;
   private class_2338 targetPos;
   private int previousSlot = -1;

   public DoubleAnchorModule() {
      super("Double Anchor", "Two rapid anchor charge\u2192detonate cycles", Category.COMBAT);
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
      if (client.field_1724 != null && client.field_1761 != null && client.field_1687 != null && client.field_1755 == null) {
         if (this.step > 0) {
            this.processStep(client);
         } else if (this.keyDown(client)) {
            class_239 class_2392 = client.field_1765;
            class_3965 hit;
            if (class_2392 instanceof class_3965 && (hit = (class_3965)class_2392).method_17783() == class_240.field_1332) {
               class_2338 pos = hit.method_17777();
               if (BlockHelper.isBlockAt(pos, class_2246.field_23152)) {
                  this.targetPos = pos.method_10062();
                  this.previousSlot = client.field_1724.method_31548().method_67532();
                  this.step = 1;
                  this.tickCounter = 0;
               }
            }
         }
      } else {
         if (this.step != 0) {
            this.resetState();
         }
      }
   }

   private void processStep(class_310 client) {
      this.tickCounter++;
      class_239 class_2392 = client.field_1765;
      class_3965 bhr;
      class_3965 live = class_2392 instanceof class_3965 ? (bhr = (class_3965)class_2392) : null;
      switch (this.step) {
         case 1:
            if (!this.lookingAtTarget(live)) {
               this.resetState();
               return;
            }

            if (BlockHelper.isAnchorUncharged(this.targetPos)) {
               InventoryHelper.swapToItem(class_1802.field_8801);
               BlockHelper.interactBlock(live, true);
            }

            this.advance(2);
            break;
         case 2:
            if (!this.lookingAtTarget(live)) {
               this.resetState();
               return;
            }

            InventoryHelper.swap(this.detonateSlot.getInt() - 1);
            BlockHelper.interactBlock(live, true);
            this.advance(3);
            break;
         case 3:
            if (this.tickCounter >= this.gapTicks()) {
               this.advance(4);
            }
            break;
         case 4:
            if (BlockHelper.isBlockAt(this.targetPos, class_2246.field_23152)) {
               if (this.lookingAtTarget(live) && BlockHelper.isAnchorUncharged(this.targetPos)) {
                  InventoryHelper.swapToItem(class_1802.field_8801);
                  BlockHelper.interactBlock(live, true);
               }

               this.advance(5);
            } else if (this.rePlace.get() && live != null && InventoryHelper.getHotbarSlot(class_1802.field_23141) >= 0) {
               InventoryHelper.swapToItem(class_1802.field_23141);
               BlockHelper.interactBlock(live, true);
               this.targetPos = live.method_17777().method_10093(live.method_17780()).method_10062();
               this.advance(6);
            } else {
               this.finish(client);
            }
            break;
         case 5:
            if (this.lookingAtTarget(live) && BlockHelper.isBlockAt(this.targetPos, class_2246.field_23152)) {
               InventoryHelper.swap(this.detonateSlot.getInt() - 1);
               BlockHelper.interactBlock(live, true);
            }

            this.finish(client);
            break;
         case 6:
            if (BlockHelper.isBlockAt(this.targetPos, class_2246.field_23152)) {
               InventoryHelper.swapToItem(class_1802.field_8801);
               if (live != null) {
                  BlockHelper.interactBlock(live, true);
               }

               this.advance(5);
            } else {
               this.finish(client);
            }
            break;
         default:
            this.finish(client);
      }
   }

   private boolean lookingAtTarget(class_3965 live) {
      return live != null && live.method_17783() == class_240.field_1332 && live.method_17777().equals(this.targetPos);
   }

   private void advance(int next) {
      this.step = next;
      this.tickCounter = 0;
   }

   private int gapTicks() {
      return Math.max(1, this.timing.getInt() / 50);
   }

   private void finish(class_310 client) {
      if (this.switchBack.get() && this.previousSlot >= 0) {
         InventoryHelper.swap(this.previousSlot);
      }

      this.resetState();
   }

   private boolean keyDown(class_310 client) {
      int key = this.activateKey.get();
      if (key == -1) {
         return false;
      } else {
         long handle = client.method_22683().method_4490();
         return key <= 7 ? GLFW.glfwGetMouseButton(handle, key) == 1 : GLFW.glfwGetKey(handle, key) == 1;
      }
   }

   private void resetState() {
      this.step = 0;
      this.tickCounter = 0;
      this.targetPos = null;
      this.previousSlot = -1;
   }
}
