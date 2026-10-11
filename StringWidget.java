package com.larpclient.gui.widget;

import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.StringSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;

public class StringWidget extends SettingWidget {
   private static final float HEIGHT = 34.0F;
   private static final float BOX_H = 18.0F;
   private static final float FONT = 12.0F;
   private static StringWidget active;
   private final StringSetting setting;
   private boolean focused;
   private int cursor;

   public StringWidget(ThemeManager themes, StringSetting setting) {
      super(themes, setting);
      this.setting = setting;
   }

   public static StringWidget active() {
      return active;
   }

   @Override
   public float height(NVGRenderer vg) {
      return 34.0F;
   }

   @Override
   public void render(NVGRenderer vg, float mx, float my) {
      Theme theme = this.theme();
      vg.text(this.setting.getName(), this.x, this.y + 8.0F, 12.5F, theme.textMuted());
      float bx = this.x;
      float by = this.y + 14.0F;
      float bw = this.width;
      vg.rect(bx, by, bw, 18.0F, 0.0F, Colors.withAlpha(-16777216, 0.45F));
      vg.rectOutline(bx, by, bw, 18.0F, 0.0F, 1.1F, Colors.withAlpha(this.focused ? theme.accentBright() : theme.accent(), this.focused ? 0.9F : 0.35F));
      float textX = bx + 8.0F;
      float textY = by + 9.0F;
      String value = this.setting.get();
      if (value.isEmpty() && !this.focused) {
         vg.text(this.setting.getPlaceholder(), textX, textY, 12.0F, theme.textDisabled());
      } else {
         vg.text(value, textX, textY, 12.0F, theme.textPrimary());
         if (this.focused && System.nanoTime() / 400000000L % 2L == 0L) {
            int safeCursor = Math.clamp((long)this.cursor, 0, value.length());
            String left = value.substring(0, safeCursor);
            float leftWidth = vg.textWidth(left, 12.0F);
            vg.rect(textX + leftWidth + 1.0F, textY - 6.0F, 1.4F, 12.0F, 0.0F, theme.accentBright());
         }
      }
   }

   private void focus() {
      if (active != null && active != this) {
         active.focused = false;
      }

      active = this;
      this.focused = true;
      String value = this.setting.get();
      this.cursor = value.length();
   }

   private void unfocus() {
      this.focused = false;
      if (active == this) {
         active = null;
      }
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      float by = this.y + 14.0F;
      boolean overBox = mx >= this.x && mx <= this.x + this.width && my >= by && my <= by + 18.0F;
      if (button == 0 && overBox) {
         this.focus();
         UiSounds.select();
         return true;
      } else {
         if (this.focused && button == 0) {
            this.unfocus();
         }

         return false;
      }
   }

   @Override
   public boolean keyPressed(int keyCode) {
      if (!this.focused) {
         return false;
      } else {
         String value = this.setting.get();
         this.cursor = Math.clamp((long)this.cursor, 0, value.length());
         switch (keyCode) {
            case 256:
            case 257:
            case 335:
               this.unfocus();
               return true;
            case 259:
               if (this.cursor > 0 && !value.isEmpty()) {
                  String next = value.substring(0, this.cursor - 1) + value.substring(this.cursor);
                  this.setting.set(next);
                  this.cursor--;
               }

               return true;
            case 261:
               if (this.cursor < value.length()) {
                  String next = value.substring(0, this.cursor) + value.substring(this.cursor + 1);
                  this.setting.set(next);
               }

               return true;
            case 262:
               if (this.cursor < value.length()) {
                  this.cursor++;
               }

               return true;
            case 263:
               if (this.cursor > 0) {
                  this.cursor--;
               }

               return true;
            case 268:
               this.cursor = 0;
               return true;
            case 269:
               this.cursor = value.length();
               return true;
            default:
               return true;
         }
      }
   }

   @Override
   public boolean charTyped(int codepoint) {
      if (!this.focused) {
         return false;
      } else if (Character.isValidCodePoint(codepoint) && !Character.isISOControl(codepoint)) {
         String value = this.setting.get();
         if (value.length() >= this.setting.getMaxLength()) {
            return true;
         } else {
            String typed = new String(Character.toChars(codepoint));
            int safeCursor = Math.clamp((long)this.cursor, 0, value.length());
            String next = value.substring(0, safeCursor) + typed + value.substring(safeCursor);
            if (next.length() > this.setting.getMaxLength()) {
               next = next.substring(0, this.setting.getMaxLength());
            }

            this.setting.set(next);
            this.cursor = Math.min(next.length(), safeCursor + typed.length());
            return true;
         }
      } else {
         return true;
      }
   }

   @Override
   public boolean isListening() {
      return this.focused;
   }
}
