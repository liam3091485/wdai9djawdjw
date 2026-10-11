package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.render.BlockEspRenderer;
import com.larpclient.settings.BlockListSetting;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ColorSetting;
import com.larpclient.settings.ModeSetting;
import com.larpclient.settings.SliderSetting;

public class BlockEspModule extends Module {
   public final BlockListSetting targets = this.addSetting(
      new BlockListSetting("Target Blocks", "Pick which blocks to highlight")
   );
   public final ModeSetting shapeMode = this.addSetting(
      new ModeSetting(
         "Shape Mode",
         "How highlights are drawn",
         "Both",
         "Both",
         "Lines",
         "Sides"
      )
   );
   public final ColorSetting lineColor = this.addSetting(
      new ColorSetting("Default Outline Color", "Color used for newly added blocks", -16734209)
   );
   public final ColorSetting sideColor = this.addSetting(
      new ColorSetting("Fill Overlay", "Default fill tint", 637577215)
   );
   public final BooleanSetting tracers = this.addSetting(
      new BooleanSetting("Tracers", "Draw lines from the crosshair to each block", false)
   );
   public final BooleanSetting tracer = this.addSetting(
      new BooleanSetting("Tracer", "Secondary tracer enable", true)
   );
   public final ColorSetting tracerColor = this.addSetting(
      new ColorSetting("Default Tracer Tint", "Tracer line color / alpha", -872372225)
   );
   public final SliderSetting highlightAlpha = this.addSetting(
      new SliderSetting("Highlight Alpha", "Box opacity", 255.0, 0.0, 255.0, 5.0)
   );
   public final SliderSetting rangeExtraChunks = this.addSetting(
      new SliderSetting("Range Extra Chunks", "Extra scan radius beyond render distance", 1.0, 0.0, 4.0, 1.0)
   );

   public BlockEspModule() {
      super("BlockESP", "Highlights chosen blocks through walls", Category.RENDER);
      this.targets.seedDefaults();
      this.tracer.visibleWhen(this.tracers::get);
      this.tracerColor.visibleWhen(this.tracers::get);
   }

   @Override
   public void onTick() {
      BlockEspRenderer.scan(this);
   }

   @Override
   protected void onDisable() {
      BlockEspRenderer.clear();
   }
}
