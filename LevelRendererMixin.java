package com.larpclient.mixin;

import com.larpclient.LarpClient;
import com.larpclient.module.ModuleManager;
import com.larpclient.render.AccessoryRenderer;
import com.larpclient.render.BlockEntityEspRenderer;
import com.larpclient.render.BlockEspRenderer;
import com.larpclient.render.BlockOutlineRenderer;
import com.larpclient.render.ChunkFinderRenderer;
import com.larpclient.render.EntityEspRenderer;
import com.larpclient.render.HitParticleRenderer;
import com.larpclient.render.HoleEspRenderer;
import com.larpclient.render.JumpCircleRenderer;
import com.larpclient.render.MiscBlockEspRenderer;
import com.larpclient.render.StorageEspRenderer;
import com.larpclient.render.SusChunkRenderer;
import net.minecraft.class_11658;
import net.minecraft.class_12074;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import net.minecraft.class_4597.class_4598;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_761.class})
public class LevelRendererMixin {
   @Inject(
      method = {"method_62210(Lnet/minecraft/class_4597$class_4598;Lnet/minecraft/class_4587;ZLnet/minecraft/class_11658;)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void larpclient$customBlockOutline(
      class_4598 bufferSource, class_4587 poseStack, boolean translucentPass, class_11658 levelRenderState, CallbackInfo ci
   ) {
      ModuleManager modules = LarpClient.modules();
      if (modules != null) {
         if (translucentPass && modules.susChunkFinder.isEnabled()) {
            SusChunkRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.susChunkFinder);
         }

         if (translucentPass && modules.chunkFinder != null && modules.chunkFinder.isEnabled()) {
            ChunkFinderRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.chunkFinder);
         }

         if (translucentPass && modules.storageEsp != null && modules.storageEsp.isEnabled()) {
            StorageEspRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.storageEsp);
         }

         if (translucentPass && modules.blockEsp != null && modules.blockEsp.isEnabled()) {
            BlockEspRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.blockEsp);
         }

         if (translucentPass && modules.blockEntityEsp != null && modules.blockEntityEsp.isEnabled()) {
            BlockEntityEspRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.blockEntityEsp);
         }

         if (translucentPass && modules.spawnerNametags != null && modules.spawnerNametags.isEnabled()) {
            MiscBlockEspRenderer.renderSpawners(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.spawnerNametags);
         }

         if (translucentPass && modules.debugHoleEsp != null && modules.debugHoleEsp.isEnabled()) {
            HoleEspRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.debugHoleEsp);
         }

         if (translucentPass && modules.playerEsp != null && modules.playerEsp.isEnabled()) {
            EntityEspRenderer.renderPlayers(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.playerEsp);
         }

         if (translucentPass && modules.mobEsp != null && modules.mobEsp.isEnabled()) {
            EntityEspRenderer.renderMobs(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.mobEsp);
         }

         if (translucentPass && modules.jumpCircles != null && modules.jumpCircles.isEnabled()) {
            JumpCircleRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.jumpCircles);
         }

         if (translucentPass && modules.hitParticles != null && modules.hitParticles.isEnabled()) {
            HitParticleRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.hitParticles);
         }

         if (translucentPass && modules.customAccessories != null && modules.customAccessories.isEnabled()) {
            AccessoryRenderer.render(bufferSource, poseStack, levelRenderState.field_63082.field_63078, modules.customAccessories);
         }

         if (modules.freecam != null && modules.freecam.isActive()) {
            ci.cancel();
         } else if (modules.blockOutline.isEnabled()) {
            ci.cancel();
            class_12074 state = levelRenderState.field_63083;
            if (state != null && state.comp_4933() == translucentPass) {
               BlockOutlineRenderer.render(bufferSource, poseStack, state, levelRenderState.field_63082.field_63078, modules.blockOutline);
            }
         }
      }
   }
}
