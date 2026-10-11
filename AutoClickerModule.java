package com.larpclient.module.impl;

import com.larpclient.mixin.MinecraftAccessor;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ModeSetting;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;

public class AutoClickerModule extends Module {
   public final BooleanSetting inScreens = this.addSetting(
      new BooleanSetting("While In Screens", "Whether to click while a screen is open.", true)
   );
   public final ModeSetting leftMode = this.addSetting(
      new ModeSetting(
         "Left Click Mode",
         "The method of clicking for left clicks.",
         "Press",
         "Disabled",
         "Hold",
         "Press"
      )
   );
   public final SliderSetting leftDelay = this.addSetting(
      new SliderSetting(
         "Left Click Delay",
         "Delay between left clicks in ticks.",
         2.0,
         0.0,
         60.0,
         1.0,
         " ticks"
      )
   );
   public final ModeSetting rightMode = this.addSetting(
      new ModeSetting(
         "Right Click Mode",
         "The method of clicking for right clicks.",
         "Press",
         "Disabled",
         "Hold",
         "Press"
      )
   );
   public final SliderSetting rightDelay = this.addSetting(
      new SliderSetting(
         "Right Click Delay",
         "Delay between right clicks in ticks.",
         2.0,
         0.0,
         60.0,
         1.0,
         " ticks"
      )
   );
   private int leftTimer;
   private int rightTimer;

   public AutoClickerModule() {
      super("AutoClicker", "Automatically clicks.", Category.COMBAT);
      this.leftDelay.visibleWhen(() -> this.leftMode.is("Press"));
      this.rightDelay.visibleWhen(() -> this.rightMode.is("Press"));
   }

   @Override
   protected void onEnable() {
      this.leftTimer = 0;
      this.rightTimer = 0;
      this.release();
   }

   @Override
   protected void onDisable() {
      this.release();
   }

   private void release() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1690 != null) {
         mc.field_1690.field_1886.method_23481(false);
         mc.field_1690.field_1904.method_23481(false);
      }
   }

   @Override
   public void onTick() {
      throw new IllegalStateException("Decompilation failed");
   }

   private void leftClick(class_310 mc) {
      if (mc.field_1771 == 10000) {
         mc.field_1771 = 0;
      }

      mc.field_1690.field_1886.method_23481(true);
      ((MinecraftAccessor)mc).larpclient$startAttack();
      mc.field_1690.field_1886.method_23481(false);
   }

   private void rightClick(class_310 mc) {
      ((MinecraftAccessor)mc).larpclient$startUseItem();
   }
}
