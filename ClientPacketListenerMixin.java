package com.larpclient.mixin;

import com.larpclient.LarpClient;
import com.larpclient.module.ModuleManager;
import com.larpclient.module.impl.BlockEntityEspModule;
import com.larpclient.module.impl.SpawnerProtectModule;
import com.larpclient.util.TpsTracker;
import net.minecraft.class_2620;
import net.minecraft.class_2622;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2672;
import net.minecraft.class_2761;
import net.minecraft.class_310;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_634.class})
public class ClientPacketListenerMixin {
   @Inject(
      method = {"method_11079"},
      at = {@At("HEAD")}
   )
   private void larpclient$trackTps(class_2761 packet, CallbackInfo ci) {
      TpsTracker.onTimePacket();
   }

   @Inject(
      method = {"method_45730"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void larpclient$fakeStatsCommand(String command, CallbackInfo ci) {
      ModuleManager modules = LarpClient.modules();
      if (modules != null && modules.fakeStats != null) {
         try {
            if (modules.fakeStats.tryInterceptBalance(command)) {
               ci.cancel();
            }
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11128"},
      at = {@At("TAIL")}
   )
   private void larpclient$blockEntityChunk(class_2672 packet, CallbackInfo ci) {
      BlockEntityEspModule module = module();
      if (module != null && module.isEnabled() && module.chunkPacketsEnabled()) {
         try {
            packet.method_38598().method_38587(packet.method_11523(), packet.method_11524()).accept((pos, type, tag) -> module.record(pos, type));
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11094"},
      at = {@At("TAIL")}
   )
   private void larpclient$blockEntityUpdate(class_2622 packet, CallbackInfo ci) {
      BlockEntityEspModule module = module();
      if (module != null && module.isEnabled() && module.beUpdatePacketsEnabled()) {
         try {
            module.record(packet.method_11293(), packet.method_11291());
         } catch (Exception var5) {
         }
      }
   }

   private static BlockEntityEspModule module() {
      ModuleManager modules = LarpClient.modules();
      return modules != null ? modules.blockEntityEsp : null;
   }

   private static SpawnerProtectModule spawnerProtect() {
      ModuleManager modules = LarpClient.modules();
      return modules != null ? modules.spawnerProtect : null;
   }

   @Inject(
      method = {"method_11116"},
      at = {@At("HEAD")}
   )
   private void larpclient$spawnerProtectDestruction(class_2620 packet, CallbackInfo ci) {
      SpawnerProtectModule sp = spawnerProtect();
      if (sp != null && sp.isEnabled() && class_310.method_1551().method_18854()) {
         try {
            sp.onBlockDestructionPacket(packet.method_11280(), packet.method_11277());
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11136"},
      at = {@At("HEAD")}
   )
   private void larpclient$spawnerProtectBlockUpdate(class_2626 packet, CallbackInfo ci) {
      SpawnerProtectModule sp = spawnerProtect();
      if (sp != null && sp.isEnabled() && sp.detectBlockUpdatesEnabled() && class_310.method_1551().method_18854()) {
         try {
            sp.onServerBlockUpdate(packet.method_11309(), packet.method_11308(), false);
         } catch (Exception var5) {
         }
      }
   }

   @Inject(
      method = {"method_11100"},
      at = {@At("HEAD")}
   )
   private void larpclient$spawnerProtectSectionUpdate(class_2637 packet, CallbackInfo ci) {
      SpawnerProtectModule sp = spawnerProtect();
      if (sp != null && sp.isEnabled() && sp.detectBlockUpdatesEnabled() && class_310.method_1551().method_18854()) {
         try {
            packet.method_30621((pos, state) -> sp.onServerBlockUpdate(pos, state, true));
         } catch (Exception var5) {
         }
      }
   }
}
