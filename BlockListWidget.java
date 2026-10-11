package com.larpclient.gui.widget;

import com.larpclient.gui.ClickGuiScreen;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.BlockListSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;
import net.minecraft.class_310;

public class BlockListWidget extends SettingWidget {
   private static final float ROW = 26.0F;
   private final BlockListSetting setting;

   public BlockListWidget(ThemeManager themes, BlockListSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 26.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      float cy = this.y + 13.0F;
      vg.text(this.setting.getName(), this.x, cy, 12.5F, theme.textMuted());
      float bw = 88.0F;
      float bh = 18.0F;
      float bx = this.x + this.width - bw;
      float by = cy - 0.0F;
      boolean hovered = mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
      vg.rectGradient(bx, by, bw, bh, 0.0F, theme.headerTop(), theme.headerBottom(), true);
      vg.rectOutline(bx, by, bw, bh, 0.0F, 1.1F, Colors.withAlpha(hovered ? theme.accentBright() : theme.accent(), hovered ? 0.9F : 0.4F));
      String label = "Pick Block";
      float tw = vg.textWidth(label, 12.0F);
      vg.textGradient(label, bx + (bw - tw) / 2.0F, by + 0.0F, 12.0F, theme.accentBright(), theme.accent());
      String count = this.setting.enabledCount() + "/" + this.setting.size();
      vg.text(count, bx - 8.0F - vg.textWidth(count, 11.5F), cy, 11.5F, theme.textDisabled());
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (button == 0 && this.contains(mx, my)) {
         if (class_310.method_1551().field_1755 instanceof ClickGuiScreen gui) {
            gui.openBlockPicker(this.setting);
            UiSounds.select();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
