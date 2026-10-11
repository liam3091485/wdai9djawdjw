package com.larpclient.mixin;

import com.larpclient.LarpClient;
import com.larpclient.module.ModuleManager;
import com.larpclient.module.impl.SkinProtectModule;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_742.class})
public class AbstractClientPlayerMixin {
   @Inject(
      method = {"method_52814"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void larpclient$replaceSkin(CallbackInfoReturnable<class_8685> cir) {
      ModuleManager modules = LarpClient.modules();
      if (modules != null) {
         SkinProtectModule skinProtect = modules.skinProtect;
         if (skinProtect != null && skinProtect.isEnabled()) {
            class_8685 replacement = skinProtect.replacementSkin();
            if (replacement != null) {
                class_742 self = (class_742)(Object)this;
               if (skinProtect.shouldReplace(self.method_5667())) {
                  cir.setReturnValue(replacement);
               }
            }
         }
      }
   }
}
