package com.larpclient.gui.widget;

import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.ModeSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;
import java.util.ArrayList;
import java.util.List;

public final class ModeWidget extends SettingWidget {
   private final ModeSetting setting;
   private final List<float[]> bounds = new ArrayList<>();
   private int lines = 1;

   public ModeWidget(ThemeManager themes, ModeSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 23 + this.lines * 26;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme t = this.theme();
      vg.text(this.setting.getName(), this.x, this.y + 9.0F, 12.5F, t.textMuted());
      this.bounds.clear();
      float cx = this.x;
      float cy = this.y + 23.0F;
      this.lines = 1;

      for (String mode : this.setting.getModes()) {
         float tw = vg.textWidth(mode, 11.5F);
         float bw = tw + 18.0F;
         if (cx + bw > this.x + this.width && cx > this.x) {
            cx = this.x;
            cy += 26.0F;
            this.lines++;
         }

         boolean sel = this.setting.is(mode);
         boolean hov = mx >= cx && mx <= cx + bw && my >= cy && my <= cy + 20.0F;
         vg.rect(cx, cy, bw, 20.0F, 0.0F, sel ? Colors.withAlpha(t.accent(), 0.28F) : (hov ? -15458771 : -15920096));
         vg.rectOutline(cx, cy, bw, 20.0F, 0.0F, 1.0F, sel ? t.accent() : -14272947);
         vg.text(mode, cx + 9.0F, cy + 10.0F, 11.5F, sel ? t.accentBright() : t.textMuted());
         this.bounds.add(new float[]{cx, cy, bw});
         cx += bw + 6.0F;
      }
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button != 0) {
         return false;
      } else {
         List<String> modes = this.setting.getModes();

         for (int i = 0; i < this.bounds.size() && i < modes.size(); i++) {
            float[] b = this.bounds.get(i);
            if (mx >= b[0] && mx <= b[0] + b[2] && my >= b[1] && my <= b[1] + 20.0F) {
               this.setting.set(modes.get(i));
               UiSounds.select();
               return true;
            }
         }

         return false;
      }
   }
}
