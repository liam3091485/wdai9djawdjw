package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.KeybindSetting;
import com.larpclient.settings.ModeSetting;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class CoordSnapperModule extends Module {
   public final ModeSetting format = this.addSetting(
      new ModeSetting(
         "Format",
         "Clipboard format.",
         "X Y Z",
         "X Y Z",
         "JSON",
         "Command"
      )
   );
   public final ModeSetting target = this.addSetting(
      new ModeSetting(
         "Target",
         "Which coordinates to copy.",
         "Looked-at Block",
         "Looked-at Block",
         "Player"
      )
   );
   public final KeybindSetting copyKey = this.addSetting(
      new KeybindSetting("Copy Key", "Press to copy the coordinates.", -1)
   );
   public final BooleanSetting notify = this.addSetting(
      new BooleanSetting("Notify", "Show a confirmation when copied.", true)
   );
   private String lastCopied = "";

   public CoordSnapperModule() {
      super("CoordSnapper", "Copies looked-at coordinates with one key.", Category.MOVEMENT);
   }

   public String lastCopied() {
      return this.lastCopied;
   }

   @Override
   public boolean onKeyPress(int keyCode) {
      if (!this.copyKey.matches(keyCode)) {
         return false;
      } else {
         this.snap();
         return true;
      }
   }

   private void snap() {
      throw new IllegalStateException("Decompilation failed");
   }

   private class_2338 resolvePos(class_310 mc) {
      if (this.target.is("Player")) {
         return mc.field_1724.method_24515();
      } else {
         class_239 hit = mc.field_1765;
         return hit instanceof class_3965 block && hit.method_17783() == class_240.field_1332 ? block.method_17777() : null;
      }
   }
}
