package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.render.BlockScanCache;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_2246;

public class SpawnerNametagsModule extends Module {
   public final BooleanSetting nametag = this.addSetting(
      new BooleanSetting("Nametag", "Floating type + distance label", true)
   );
   public final BooleanSetting box = this.addSetting(new BooleanSetting("Box", "Through-wall box on the spawner", true));
   public final BooleanSetting rangeRing = this.addSetting(
      new BooleanSetting("Range Ring", "Draw the 16-block activation ring", true)
   );
   public final BooleanSetting distance = this.addSetting(
      new BooleanSetting("Distance", "Append the distance in blocks", true)
   );
   public final SliderSetting opacity = this.addSetting(
      new SliderSetting("Opacity", "Nametag transparency", 100.0, 10.0, 100.0, 5.0, "%")
   );
   public final BooleanSetting tracers = this.addSetting(
      new BooleanSetting("Tracers", "Draw lines from the crosshair to each spawner", false)
   );
   public final BlockScanCache scan = new BlockScanCache(
      state -> state.method_27852(class_2246.field_10260) || state.method_27852(class_2246.field_47336), 12, 8, 400, 96.0
   );

   public SpawnerNametagsModule() {
      super("SpawnerNametags", "Shows spawner type + activation range", Category.RENDER);
   }

   @Override
   public void onTick() {
      this.scan.scan();
   }

   @Override
   protected void onDisable() {
      this.scan.clear();
   }
}
