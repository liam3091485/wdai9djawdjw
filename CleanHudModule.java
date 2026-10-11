package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;

public class CleanHudModule extends Module {
   public final BooleanSetting subtleGlow = this.addSetting(new BooleanSetting("Subtle Glow", "Use a softer blue glow profile in supported HUD elements", true));

   public CleanHudModule() {
      super("Clean HUD", "A cleaner LARP HUD profile with restrained blue styling.", Category.CLIENT);
   }
}
