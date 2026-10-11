package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;

public class BluePulseModule extends Module {
   public final SliderSetting speed = this.addSetting(new SliderSetting("Pulse Speed", "Speed used by LARP blue animated visual effects", 1.0, 0.25, 4.0, 0.25));

   public BluePulseModule() {
      super("Blue Pulse", "Adds a configurable LARP-blue pulse profile for visual effects.", Category.VISUALS);
   }

   public float pulse() {
      double phase = System.currentTimeMillis() / 1000.0 * this.speed.getFloat();
      return 0.5F + 0.5F * (float)Math.sin(phase * Math.PI * 2.0);
   }
}
