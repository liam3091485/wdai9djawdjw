package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.SliderSetting;

public class FullbrightModule extends Module {
   public final SliderSetting gamma = this.addSetting(
      new SliderSetting("Gamma", "Brightness boost", 12.0, 1.0, 15.0, 1.0)
   );

   public FullbrightModule() {
      super("FullBright", "Maximum brightness everywhere", Category.RENDER);
   }
}
