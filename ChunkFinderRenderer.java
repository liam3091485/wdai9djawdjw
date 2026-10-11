package com.larpclient.render;

import com.larpclient.LarpClient;
import com.larpclient.module.impl.ChunkFinderModule;
import com.larpclient.util.Colors;
import net.minecraft.class_1923;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4597.class_4598;

public final class ChunkFinderRenderer {
   private static final float RENDER_Y = 55.0F;
   private static final int FILL_ALPHA = 190;

   private ChunkFinderRenderer() {
   }

   public static void render(class_4598 bufferSource, class_4587 poseStack, class_243 camera, ChunkFinderModule module) {
      if (!module.flaggedChunks().isEmpty()) {
         int accent = LarpClient.themes().current().accent();
         int fill = Colors.withAlpha(Colors.darken(accent, 0.58F), 190);
         int outline = Colors.withAlpha(accent, 255);

         for (long key : module.flaggedChunks()) {
            double x0 = class_1923.method_8325(key) * 16.0;
            double z0 = class_1923.method_8332(key) * 16.0;
            double x1 = x0 + 16.0;
            double z1 = z0 + 16.0;
            FlatOverlay.fillQuad(bufferSource, poseStack, camera, x0, z0, x1, z1, 55.0, fill);
            FlatOverlay.edge(bufferSource, poseStack, camera, x0, z0, x1, z0, 55.0, outline, 2.0F);
            FlatOverlay.edge(bufferSource, poseStack, camera, x1, z0, x1, z1, 55.0, outline, 2.0F);
            FlatOverlay.edge(bufferSource, poseStack, camera, x1, z1, x0, z1, 55.0, outline, 2.0F);
            FlatOverlay.edge(bufferSource, poseStack, camera, x0, z1, x0, z0, 55.0, outline, 2.0F);
         }

         FlatOverlay.flush(bufferSource);
      }
   }
}
