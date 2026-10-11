package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class CopyCoordsModule extends Module {
   public CopyCoordsModule() {
      super("Copy Coordinates", "Copies your current XYZ coordinates to the clipboard.", Category.WORLD);
   }

   @Override
   protected void onEnable() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         String coords = String.format("%.1f, %.1f, %.1f", mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());

         try {
            mc.field_1774.method_1455(coords);
         } catch (Exception var4) {
         }

         if (LarpClient.notifications() != null) {
            LarpClient.notifications().pushInfo("Copied coordinates: " + coords);
         }
      }

      this.setEnabled(false);
   }
}
