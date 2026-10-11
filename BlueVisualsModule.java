package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;

public class BlueVisualsModule extends Module {
   public final BooleanSetting forceBlue = this.addSetting(
      new BooleanSetting("Force Blue", "Keeps supported ESP and tracer visuals on the LARP blue palette", true)
   );

   public BlueVisualsModule() {
      super("Blue Visuals", "Keeps supported client visuals consistent with the LARP blue theme.", Category.VISUALS);
      this.setEnabled(true);
   }
}
