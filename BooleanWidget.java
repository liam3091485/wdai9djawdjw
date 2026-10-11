package com.larpclient.gui.widget;

import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.UiSounds;

public final class BooleanWidget extends SettingWidget {
   private final BooleanSetting setting;

   public BooleanWidget(ThemeManager themes, BooleanSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 30.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme t = this.theme();
      float cy = this.y + 15.0F;
      boolean on = this.setting.get();
      vg.text(this.setting.getName(), this.x, cy, 12.5F, t.textMuted());
      float bw = 58.0F;
      float bh = 22.0F;
      float bx = this.x + this.width - bw;
      float by = this.y + 4.0F;
      vg.rect(bx, by, bw, bh, 0.0F, on ? t.accent() : -15656921);
      vg.rectOutline(bx, by, bw, bh, 0.0F, 1.0F, on ? t.accentBright() : -13418155);
      vg.text(on ? "ENABLED" : "DISABLED", bx + (on ? 8 : 5), by + 11.0F, 9.5F, on ? -16644596 : t.textDisabled());
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button == 0 && this.contains(mx, my)) {
         this.setting.toggle();
         UiSounds.checkbox(this.setting.get());
         return true;
      } else {
         return false;
      }
   }
}
