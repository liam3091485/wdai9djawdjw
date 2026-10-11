package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import net.minecraft.class_310;

public final class DeathCoordsModule extends Module {
   private boolean dead;
   private String lastDeath = "";

   public DeathCoordsModule() {
      super("Death Coordinates", "Remembers and copies the coordinates where you died.", Category.WORLD);
   }

   public String lastDeath() {
      return this.lastDeath;
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 == null) {
         this.dead = false;
      } else {
         boolean nowDead = mc.field_1724.method_6032() <= 0.0F;
         if (nowDead && !this.dead) {
            this.lastDeath = String.format("%.1f, %.1f, %.1f", mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());

            try {
               mc.field_1774.method_1455(this.lastDeath);
            } catch (Exception var4) {
            }

            if (LarpClient.notifications() != null) {
               LarpClient.notifications().pushInfo("Death coordinates copied: " + this.lastDeath);
            }
         }

         this.dead = nowDead;
      }
   }
}
