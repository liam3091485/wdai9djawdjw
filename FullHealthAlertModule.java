package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class FullHealthAlertModule extends Module {
   private boolean below = true;

   public FullHealthAlertModule() {
      super("Full Health Alert", "Notifies you when you return to full health.", Category.PLAYER);
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         float hp = mc.field_1724.method_6032();
         if (hp < 20.0F) {
            this.below = true;
         } else if (this.below) {
            this.below = false;
            if (LarpClient.notifications() != null) {
               LarpClient.notifications().pushInfo("Back to full health");
            }
         }
      }
   }

   @Override
   protected void onDisable() {
      this.below = true;
   }
}
