package com.larpclient.gui.widget;

import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.SliderSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.UiSounds;

public final class SliderWidget extends SettingWidget {
   private final SliderSetting setting;
   private boolean dragging;

   public SliderWidget(ThemeManager themes, SliderSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 34.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme t = this.theme();
      vg.text(this.setting.getName(), this.x, this.y + 9.0F, 12.5F, t.textMuted());
      String val = this.setting.formatValue();
      vg.text(val, this.x + this.width - vg.textWidth(val, 12.0F), this.y + 9.0F, 12.0F, t.textPrimary());
      float by = this.y + 23.0F;
      float bh = 8.0F;
      float n = (float)Math.clamp(this.setting.getNormalized(), 0.0, 1.0);
      vg.rect(this.x, by, this.width, bh, 0.0F, -15722457);
      vg.rect(this.x, by, this.width * n, bh, 0.0F, t.accent());
      vg.rectOutline(this.x, by, this.width, bh, 0.0F, 1.0F, -14272947);
      vg.rect(this.x + Math.max(0.0F, this.width * n - 2.0F), by - 3.0F, 3.0F, bh + 6.0F, 0.0F, t.accentBright());
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button == 0 && this.contains(mx, my) && !(my < this.y + 14.0F)) {
         this.dragging = true;
         this.apply(mx);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void mouseDragged(float mx, float my) {
      if (this.dragging) {
         this.apply(mx);
      }
   }

   @Override
   public void mouseReleased() {
      this.dragging = false;
   }

   private void apply(float mx) {
      double before = this.setting.getNormalized();
      this.setting.setNormalized((mx - this.x) / this.width);
      if (this.setting.getNormalized() != before) {
         UiSounds.sliderTick((float)this.setting.getNormalized());
      }
   }
}
