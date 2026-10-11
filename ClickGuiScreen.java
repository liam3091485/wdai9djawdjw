package com.larpclient.gui;

import com.larpclient.LarpClient;
import com.larpclient.gui.picker.BlockGridModel;
import com.larpclient.gui.picker.IconListGridModel;
import com.larpclient.module.Modules;
import com.larpclient.module.impl.BlockEspModule;
import com.larpclient.render.BlurHook;
import com.larpclient.render.NvgDrawable;
import com.larpclient.render.OverlayRenderer;
import com.larpclient.render.anim.Animation;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.settings.BlockListSetting;
import com.larpclient.settings.IconListSetting;
import com.larpclient.theme.Theme;
import com.larpclient.util.UiSounds;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends class_437 implements NvgDrawable {
   private static final ClickGuiState STATE = new ClickGuiState();
   private final class_437 parent;
   private final Animation openAnim = new Animation(220.0F, 0.0F);
   private final SidebarModuleView view;
   private final StringBuilder licenseInput = new StringBuilder();
   private boolean licenseFocused = true;
   private boolean closing;
   private float licenseBoxX;
   private float licenseBoxY;
   private float licenseBoxW;
   private float licenseBoxH;
   private float verifyX;
   private float verifyY;
   private float verifyW;
   private float verifyH;
   private float pasteX;
   private float pasteY;
   private float pasteW;
   private float pasteH;

   public ClickGuiScreen() {
      this(null);
   }

   public ClickGuiScreen(class_437 var1) {
      super(class_2561.method_43470("LARP Client"));
      this.parent = var1;
      this.view = new SidebarModuleView(LarpClient.modules(), LarpClient.themes(), STATE, LarpClient::togglePanic);
      String var2 = LarpClient.license() == null ? "" : LarpClient.license().savedKey();
      if (!var2.isBlank()) {
         this.licenseInput.append(var2);
      }

      this.openAnim.setTarget(1.0F);
   }

   public static ClickGuiState state() {
      return STATE;
   }

   private Modules.ClickGuiModule guiModule() {
      return LarpClient.modules().clickGui;
   }

   public void method_49589() {
      super.method_49589();
      UiSounds.guiOpen();
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25419() {
      if (!this.closing) {
         this.closing = true;
         this.openAnim.setTarget(0.0F);
         UiSounds.guiClose();
      }
   }

   public void method_25432() {
      BlurHook.clear();
      LarpClient.config().save();
   }

   public void method_25394(class_332 var1, int var2, int var3, float var4) {
      int var5 = (int)(82.0F * this.openAnim.value());
      var1.method_25294(0, 0, this.field_22789, this.field_22790, var5 << 24 | 330258);
      this.finishCloseIfDone();
   }

   public void method_25393() {
      this.finishCloseIfDone();
   }

   private void finishCloseIfDone() {
      if (this.closing && this.openAnim.isDone()) {
         this.field_22787.method_1507(this.parent);
      }
   }

   public void method_25420(class_332 var1, int var2, int var3, float var4) {
      if (this.field_22787.field_1687 == null) {
         this.method_57728(var1, var4);
      }

      if (this.guiModule().blur.get()) {
         BlurHook.set(this.guiModule().blurStrength.getFloat() * this.openAnim.value());
         var1.method_71278();
      } else {
         BlurHook.clear();
      }
   }

   @Override
   public void renderNvg(NVGRenderer var1, float var2, float var3, float var4, float var5) {
      var1.setFontMode(this.guiModule().font.get());
      if (var1.hasFont()) {
         Theme var6 = LarpClient.themes().current();
         var6.setAccent(this.guiModule().interfaceColor.get());
         float var7 = this.openAnim.value();
         if (!(var7 <= 0.002F) || !this.closing) {
            var1.save();
            var1.alpha(var7);
            float var8 = 0.96F + 0.04F * var7;
            var1.translate(var4 / 2.0F, var5 / 2.0F);
            var1.scale(var8);
            var1.translate(-var4 / 2.0F, -var5 / 2.0F);
            if (LarpClient.license() != null && LarpClient.license().isValid()) {
               this.view.render(var1, var2, var3, var4, var5);
            } else {
               this.renderLicenseGate(var1, var2, var3, var4, var5);
            }

            var1.restore();
         }
      }
   }

   private void renderLicenseGate(NVGRenderer var1, float var2, float var3, float var4, float var5) {
   }

   private float uiX(double var1) {
      return OverlayRenderer.guiToUi(var1);
   }

   private float uiY(double var1) {
      return OverlayRenderer.guiToUi(var1);
   }

   public boolean method_25402(class_11909 var1, boolean var2) {
      float var3 = this.uiX(var1.comp_4798());
      float var4 = this.uiY(var1.comp_4799());
      if (LarpClient.license() != null && LarpClient.license().isValid()) {
         return this.view.mouseClicked(var3, var4, var1.method_74245(), OverlayRenderer.uiWidth(), OverlayRenderer.uiHeight());
      } else if (hit(var3, var4, this.licenseBoxX, this.licenseBoxY, this.licenseBoxW, this.licenseBoxH)) {
         this.licenseFocused = true;
         if (var1.method_74245() == 1) {
            this.pasteLicenseFromClipboard();
         }

         UiSounds.select();
         return true;
      } else if (var1.method_74245() == 0 && hit(var3, var4, this.pasteX, this.pasteY, this.pasteW, this.pasteH)) {
         this.pasteLicenseFromClipboard();
         UiSounds.select();
         return true;
      } else if (var1.method_74245() == 0 && hit(var3, var4, this.verifyX, this.verifyY, this.verifyW, this.verifyH)) {
         boolean var5 = LarpClient.license() != null && LarpClient.license().validate(this.licenseInput.toString(), true);
         UiSounds.toggle(var5);
         return true;
      } else {
         return true;
      }
   }

   public boolean method_25403(class_11909 var1, double var2, double var4) {
      if (this.isLicensed()) {
         this.view.mouseDragged(this.uiX(var1.comp_4798()), this.uiY(var1.comp_4799()));
      }

      return true;
   }

   public boolean method_25406(class_11909 var1) {
      if (this.isLicensed()) {
         this.view.mouseReleased();
      }

      return true;
   }

   public boolean method_25401(double var1, double var3, double var5, double var7) {
      if (this.isLicensed()) {
         this.view.scroll(var7);
      }

      return true;
   }

   public boolean method_25404(class_11908 var1) {
      int var2 = var1.comp_4795();
      if (this.isLicensed()) {
         if (this.view.keyPressed(var2)) {
            return true;
         } else if (!var1.method_74231() && !this.guiModule().getKeybind().matches(var2)) {
            return super.method_25404(var1);
         } else {
            this.method_25419();
            return true;
         }
      } else {
         if (this.licenseFocused && this.controlDown()) {
            if (var2 == 86) {
               this.pasteLicenseFromClipboard();
               return true;
            }

            if (var2 == 67) {
               this.setClipboard(this.licenseInput.toString());
               return true;
            }

            if (var2 == 65) {
               this.licenseInput.setLength(0);
               return true;
            }
         }

         if (var2 == 259 && this.licenseFocused && this.licenseInput.length() > 0) {
            this.licenseInput.deleteCharAt(this.licenseInput.length() - 1);
            return true;
         } else if ((var2 == 257 || var2 == 335) && this.licenseFocused) {
            boolean var3 = LarpClient.license().validate(this.licenseInput.toString(), true);
            UiSounds.toggle(var3);
            return true;
         } else if (!var1.method_74231() && !this.guiModule().getKeybind().matches(var2)) {
            return true;
         } else {
            this.method_25419();
            return true;
         }
      }
   }

   public boolean method_25400(class_11905 var1) {
      if (!this.isLicensed()) {
         if (this.licenseFocused) {
            char var2 = (char)var1.comp_4793();
            if (var2 >= ' ' && var2 < 127 && this.licenseInput.length() < 96) {
               this.licenseInput.append(Character.toUpperCase(var2));
            }

            return true;
         } else {
            return true;
         }
      } else {
         return this.view.charTyped(var1.comp_4793()) ? true : super.method_25400(var1);
      }
   }

   private boolean controlDown() {
      long var1 = class_310.method_1551().method_22683().method_4490();
      return GLFW.glfwGetKey(var1, 341) == 1 || GLFW.glfwGetKey(var1, 345) == 1;
   }

   private void pasteLicenseFromClipboard() {
      try {
         String var1 = class_310.method_1551().field_1774.method_1460();
         if (var1 == null) {
            return;
         }

         String var2 = var1.trim().replaceAll("\\s+", "").toUpperCase();
         if (var2.length() > 96) {
            var2 = var2.substring(0, 96);
         }

         this.licenseInput.setLength(0);
         this.licenseInput.append(var2);
         this.licenseFocused = true;
      } catch (Exception var3) {
      }
   }

   private void setClipboard(String var1) {
      try {
         class_310.method_1551().field_1774.method_1455(var1 == null ? "" : var1);
      } catch (Exception var3) {
      }
   }

   private boolean isLicensed() {
      return true;
   }

   private static boolean hit(float var0, float var1, float var2, float var3, float var4, float var5) {
      return var0 >= var2 && var0 <= var2 + var4 && var1 >= var3 && var1 <= var3 + var5;
   }

   public void openBlockPicker(BlockListSetting var1) {
      BlockGridModel var2 = new BlockGridModel(var1, () -> {
         BlockEspModule var0 = LarpClient.modules().blockEsp;
         return var0 != null ? var0.lineColor.get() : -16734209;
      }, "Select Blocks");
      this.field_22787.method_1507(new IconPickerScreen(this, var2, LarpClient.themes()));
   }

   public void openIconPicker(IconListSetting var1) {
      this.field_22787.method_1507(new IconPickerScreen(this, new IconListGridModel(var1), LarpClient.themes()));
   }
}
