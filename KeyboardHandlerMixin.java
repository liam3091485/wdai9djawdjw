package com.larpclient.mixin;

import com.larpclient.LarpClient;
import com.larpclient.gui.ClickGuiScreen;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_364;
import net.minecraft.class_408;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_473;
import net.minecraft.class_7743;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_309.class})
public class KeyboardHandlerMixin {
   @Inject(
      method = {"method_1466(JILnet/minecraft/class_11908;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void larpclient$dispatchModuleKeybinds(long window, int action, class_11908 keyEvent, CallbackInfo ci) {
      class_310 minecraft = class_310.method_1551();
      if (LarpClient.modules() != null && action == 1) {
         int keyCode = keyEvent.comp_4795();
         if (keyCode != 344 || LarpClient.isPanicActive() || minecraft.field_1755 != null && !shiftOpensGui(minecraft.field_1755)) {
            if (minecraft.field_1755 == null && minecraft.field_1687 != null) {
               if (LarpClient.modules().onKeyPressed(keyCode)) {
                  ci.cancel();
               }
            }
         } else {
            minecraft.method_1507(new ClickGuiScreen(minecraft.field_1755));
            ci.cancel();
         }
      }
   }

   private static boolean shiftOpensGui(class_437 screen) {
      if (!(screen instanceof ClickGuiScreen)
         && !(screen instanceof class_408)
         && !(screen instanceof class_465)
         && !(screen instanceof class_7743)
         && !(screen instanceof class_473)) {
         class_364 class_3642 = screen.method_25399();
         class_342 editBox;
         return !(class_3642 instanceof class_342) || !(editBox = (class_342)class_3642).method_25370();
      } else {
         return false;
      }
   }
}
