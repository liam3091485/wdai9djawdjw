package com.larpclient.mixin;

import com.larpclient.LarpClient;
import com.larpclient.module.ModuleManager;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_1309.class})
public class LivingEntityJumpMixin {
   @Inject(
      method = {"method_6043"},
      at = {@At("HEAD")}
   )
   private void larpclient$onJump(CallbackInfo ci) {
      class_1309 livingEntity = (class_1309)(Object)this;
      ModuleManager modules;
      class_746 player;
      if (livingEntity instanceof class_746
         && (player = (class_746)livingEntity) == class_310.method_1551().field_1724
         && (modules = LarpClient.modules()) != null
         && modules.jumpCircles != null
         && modules.jumpCircles.isEnabled()) {
         modules.jumpCircles.onPlayerJump(player);
      }
   }
}
