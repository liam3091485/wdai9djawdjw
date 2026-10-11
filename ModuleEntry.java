package com.larpclient.gui.panel;

import com.larpclient.gui.ClickGuiState;
import com.larpclient.gui.widget.BlockListWidget;
import com.larpclient.gui.widget.BooleanWidget;
import com.larpclient.gui.widget.ColorWidget;
import com.larpclient.gui.widget.IconListWidget;
import com.larpclient.gui.widget.KeybindWidget;
import com.larpclient.gui.widget.ModeWidget;
import com.larpclient.gui.widget.SettingWidget;
import com.larpclient.gui.widget.SliderWidget;
import com.larpclient.gui.widget.StringWidget;
import com.larpclient.module.Module;
import com.larpclient.render.anim.Animation;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.BlockListSetting;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ColorSetting;
import com.larpclient.settings.IconListSetting;
import com.larpclient.settings.KeybindSetting;
import com.larpclient.settings.ModeSetting;
import com.larpclient.settings.Setting;
import com.larpclient.settings.SliderSetting;
import com.larpclient.settings.StringSetting;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ModuleEntry {
   public static final float ROW_H = 68.0F;
   private final Module module;
   private final ThemeManager themes;
   private final ClickGuiState state;
   private final String key;
   private final List<SettingWidget> widgets = new ArrayList<>();
   private final Animation hover = new Animation(120.0F, 0.0F);
   private final Animation enable = new Animation(140.0F, 0.0F);
   private final Animation expand;
   private float x;
   private float y;
   private float width;

   public ModuleEntry(Module module, ThemeManager themes, ClickGuiState state) {
      this.module = module;
      this.themes = themes;
      this.state = state;
      this.key = module.getName() + "@" + module.getCategory().name();
      this.expand = new Animation(170.0F, state.isExpanded(this.key) ? 1.0F : 0.0F);
      this.enable.snapTo(module.isEnabled() ? 1.0F : 0.0F);

      for (Setting<?> setting : module.getSettings()) {
         String settingName = setting.getName() == null ? "" : setting.getName().trim().toLowerCase(Locale.ROOT);
         if (!settingName.equals("chat") && !settingName.equals("chat notification") && !settingName.equals("chat notifications")) {
            if (setting instanceof BooleanSetting s) {
               this.widgets.add(new BooleanWidget(themes, s));
            } else if (setting instanceof SliderSetting s) {
               this.widgets.add(new SliderWidget(themes, s));
            } else if (setting instanceof ModeSetting s) {
               this.widgets.add(new ModeWidget(themes, s));
            } else if (setting instanceof ColorSetting s) {
               this.widgets.add(new ColorWidget(themes, s));
            } else if (setting instanceof BlockListSetting s) {
               this.widgets.add(new BlockListWidget(themes, s));
            } else if (setting instanceof IconListSetting s) {
               this.widgets.add(new IconListWidget(themes, s));
            } else if (setting instanceof StringSetting s) {
               this.widgets.add(new StringWidget(themes, s));
            } else if (setting instanceof KeybindSetting s) {
               this.widgets.add(new KeybindWidget(themes, s));
            }
         }
      }

      this.widgets.add(new KeybindWidget(themes, module.getKeybind()));
   }

   public Module getModule() {
      return this.module;
   }

   public boolean isFavorite() {
      return this.state.isFavorite(this.key);
   }

   public void setBounds(float x, float y, float width) {
      this.x = x;
      this.y = y;
      this.width = width;
   }

   private float settingsHeight(NVGRenderer vg) {
      float h = 12.0F;

      for (SettingWidget widget : this.widgets) {
         if (widget.isVisible()) {
            h += widget.height(vg) + 8.0F;
         }
      }

      return h + 10.0F;
   }

   public float height(NVGRenderer vg) {
      float t = this.expand.value();
      return 68.0F + (t <= 0.005F ? 0.0F : t * this.settingsHeight(vg));
   }

   public void render(NVGRenderer vg, float mx, float my, float fadeAlpha) {
      Theme theme = this.themes.current();
      boolean hovered = mx >= this.x && mx <= this.x + this.width && my >= this.y && my <= this.y + 68.0F;
      if (hovered && this.hover.getTarget() < 0.5F) {
         UiSounds.hover();
      }

      this.hover.setTarget(hovered ? 1.0F : 0.0F);
      this.enable.setTarget(this.module.isEnabled() ? 1.0F : 0.0F);
      float ht = this.hover.value();
      float et = this.enable.value();
      float xt = this.expand.value();
      vg.save();
      vg.alpha(fadeAlpha);
      int card = Colors.lerp(-116715237, -15853530, ht * 0.72F);
      if (et > 0.01F) {
         card = Colors.lerp(card, -16049620, et * 0.72F);
      }

      vg.rect(this.x, this.y, this.width, 68.0F, 10.0F, card);
      vg.rectOutline(
         this.x,
         this.y,
         this.width,
         68.0F,
         10.0F,
         1.0F,
         et > 0.08F ? Colors.withAlpha(theme.accentBright(), 0.38F) : Colors.withAlpha(-1, hovered ? 0.095F : 0.055F)
      );
      vg.circle(this.x + 18.0F, this.y + 20.0F, 4.0F, et > 0.5F ? theme.accentBright() : -13023914);
      if (et > 0.5F) {
         vg.circleGlow(this.x + 18.0F, this.y + 20.0F, 3.5F, 6.0F, Colors.withAlpha(theme.accentBright(), 0.35F));
      }

      vg.text(
         this.module.getName(),
         this.x + 31.0F,
         this.y + 20.0F,
         13.5F,
         et > 0.05F ? theme.textPrimary() : Colors.lerp(theme.textMuted(), theme.textPrimary(), ht)
      );
      String desc = this.module.getDescription();
      if (desc != null && !desc.isBlank()) {
         String clipped = desc.length() > 43 ? desc.substring(0, 40) + "..." : desc;
         vg.text(clipped, this.x + 16.0F, this.y + 46.0F, 9.8F, theme.textDisabled());
      }

      float starX = this.x + this.width - 122.0F;
      float starY = this.y + 34.0F;
      boolean favorite = this.state.isFavorite(this.key);
      boolean starHover = mx >= starX - 10.0F && mx <= starX + 10.0F && my >= starY - 10.0F && my <= starY + 10.0F;
      vg.text(
         favorite ? "PIN" : "+",
         starX - (favorite ? 12.0F : 4.0F),
         starY,
         favorite ? 9.5F : 13.0F,
         favorite ? theme.accentBright() : (starHover ? theme.textMuted() : theme.textDisabled())
      );
      float editW = 30.0F;
      float editH = 28.0F;
      float editX = this.x + this.width - 88.0F;
      float editY = this.y + 20.0F;
      if (!this.widgets.isEmpty()) {
         boolean editHover = mx >= editX && mx <= editX + editW && my >= editY && my <= editY + editH;
         vg.rect(editX, editY, editW, editH, 7.0F, xt > 0.5F ? Colors.withAlpha(theme.accent(), 0.19F) : Colors.withAlpha(-1, editHover ? 0.055F : 0.028F));
         vg.rectOutline(editX, editY, editW, editH, 7.0F, 1.0F, Colors.withAlpha(theme.accentBright(), xt > 0.5F ? 0.42F : 0.12F));
         vg.chevron(editX + 15.0F, editY + 14.0F, 4.0F, 1.5F, xt > 0.5F ? theme.accentBright() : theme.textMuted(), xt <= 0.5F);
      }

      float toggleW = 42.0F;
      float toggleH = 24.0F;
      float toggleX = this.x + this.width - 50.0F;
      float toggleY = this.y + 22.0F;
      vg.rect(toggleX, toggleY, toggleW, toggleH, 12.0F, et > 0.5F ? Colors.withAlpha(theme.accent(), 0.94F) : -15195599);
      vg.rectOutline(toggleX, toggleY, toggleW, toggleH, 12.0F, 1.0F, et > 0.5F ? Colors.withAlpha(theme.accentBright(), 0.55F) : -14076345);
      float knobX = et > 0.5F ? toggleX + toggleW - 12.0F : toggleX + 12.0F;
      vg.circle(knobX, toggleY + 12.0F, 7.0F, et > 0.5F ? -787713 : -8943974);
      if (xt > 0.005F) {
         float sh = this.settingsHeight(vg) * xt;
         vg.save();
         vg.scissor(this.x, this.y + 68.0F, this.width, sh);
         vg.alpha(xt);
         vg.rectVaryingGradient(this.x, this.y + 68.0F - 1.0F, this.width, sh + 1.0F, 0.0F, 0.0F, 10.0F, 10.0F, -16248547, -16315630);
         vg.rect(this.x + 14.0F, this.y + 68.0F, this.width - 28.0F, 1.0F, 0.0F, Colors.withAlpha(theme.accent(), 0.16F));
         float wy = this.y + 68.0F + 12.0F;

         for (SettingWidget widget : this.widgets) {
            if (widget.isVisible()) {
               widget.setBounds(this.x + 14.0F, wy, this.width - 28.0F);
               widget.render(vg, mx, my);
               wy += widget.height(vg) + 8.0F;
            }
         }

         vg.restore();
      }

      vg.restore();
   }

   public boolean mouseClicked(float mx, float my, int button) {
      if (mx >= this.x && mx <= this.x + this.width && my >= this.y && my <= this.y + 68.0F) {
         float starX = this.x + this.width - 122.0F;
         if (button == 0 && mx >= starX - 12.0F && mx <= starX + 12.0F) {
            this.state.toggleFavorite(this.key);
            UiSounds.select();
            return true;
         } else {
            float editX = this.x + this.width - 88.0F;
            if (button == 0 && mx >= editX && mx <= editX + 32.0F) {
               this.toggleExpanded();
            } else if (button == 0) {
               this.module.toggle();
               UiSounds.toggle(this.module.isEnabled());
            } else if (button == 1) {
               this.toggleExpanded();
            }

            return true;
         }
      } else if (this.expand.getTarget() > 0.5F && my >= this.y + 68.0F && my <= this.y + this.height(null)) {
         for (SettingWidget widget : this.widgets) {
            if (widget.isVisible() && widget.mouseClicked(mx, my, button)) {
               return true;
            }
         }

         return mx >= this.x && mx <= this.x + this.width;
      } else {
         return false;
      }
   }

   private void toggleExpanded() {
      boolean expanded = !(this.expand.getTarget() > 0.5F);
      this.expand.setTarget(expanded ? 1.0F : 0.0F);
      this.state.setExpanded(this.key, expanded);
      UiSounds.select();
   }

   public void mouseDragged(float mx, float my) {
      for (SettingWidget widget : this.widgets) {
         widget.mouseDragged(mx, my);
      }
   }

   public void mouseReleased() {
      for (SettingWidget widget : this.widgets) {
         widget.mouseReleased();
      }
   }

   public boolean keyPressed(int keyCode) {
      for (SettingWidget widget : this.widgets) {
         if (widget.keyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(int cp) {
      for (SettingWidget widget : this.widgets) {
         if (widget.charTyped(cp)) {
            return true;
         }
      }

      return false;
   }

   public boolean isListening() {
      for (SettingWidget widget : this.widgets) {
         if (widget.isListening()) {
            return true;
         }
      }

      return false;
   }
}
