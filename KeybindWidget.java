package com.larpclient.gui.widget;

import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.KeybindSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.UiSounds;

public final class KeybindWidget extends SettingWidget {
   private final KeybindSetting setting;
   private boolean listening;

   public KeybindWidget(ThemeManager themes, KeybindSetting setting) {
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
      vg.text(this.setting.getName(), this.x, cy, 12.5F, t.textMuted());
      String label = this.listening ? "PRESS KEY" : this.setting.keyName();
      float bw = Math.max(72.0F, vg.textWidth(label, 11.0F) + 18.0F);
      float bx = this.x + this.width - bw;
      vg.rect(bx, this.y + 4.0F, bw, 22.0F, 0.0F, this.listening ? -15260358 : -15920096);
      vg.rectOutline(bx, this.y + 4.0F, bw, 22.0F, 0.0F, 1.0F, this.listening ? t.accentBright() : -13418155);
      vg.text(label, bx + 9.0F, this.y + 15.0F, 11.0F, this.listening ? t.accentBright() : t.textPrimary());
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      if (this.listening) {
         if (button == 0) {
            this.listening = false;
            return this.contains(mx, my);
         } else {
            this.setting.set(button);
            this.listening = false;
            UiSounds.keybindSet();
            return true;
         }
      } else if (button == 0 && this.contains(mx, my)) {
         this.listening = true;
         UiSounds.keybindListen();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int keyCode) {
      if (!this.listening) {
         return false;
      } else {
         this.setting.set(keyCode == 256 ? -1 : keyCode);
         this.listening = false;
         UiSounds.keybindSet();
         return true;
      }
   }

   @Override
   public boolean isListening() {
      return this.listening;
   }
}
