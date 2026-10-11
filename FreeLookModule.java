package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ModeSetting;
import com.larpclient.settings.SliderSetting;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import org.lwjgl.glfw.GLFW;

public class FreeLookModule extends Module {
   private static FreeLookModule instance;
   public final ModeSetting mode = this.addSetting(
      new ModeSetting(
         "Mode",
         "Which entity the mouse rotates.",
         "Player",
         "Player",
         "Camera"
      )
   );
   public final BooleanSetting togglePerspective = this.addSetting(
      new BooleanSetting("Toggle Perspective", "Switch to third person on toggle.", true)
   );
   public final BooleanSetting throughWalls = this.addSetting(
      new BooleanSetting(
         "Through Walls", "See through walls \u2014 the third-person camera ignores wall collision.", false
      )
   );
   public final SliderSetting sensitivity = this.addSetting(
      new SliderSetting("Camera Sensitivity", "How fast the camera moves in Camera mode.", 8.0, 0.0, 10.0, 0.1)
   );
   public final BooleanSetting arrows = this.addSetting(
      new BooleanSetting(
         "Arrows Control Opposite", "Control the other entity's rotation with the arrow keys.", true
      )
   );
   public final SliderSetting arrowSpeed = this.addSetting(
      new SliderSetting("Arrow Speed", "Rotation speed with the arrow keys.", 4.0, 0.0, 10.0, 0.5)
   );
   private float cameraYaw;
   private float cameraPitch;
   private class_5498 prePers;

   public FreeLookModule() {
      super("FreeLook", "Allows more rotation options in third person.", Category.MOVEMENT);
      instance = this;
   }

   public static FreeLookModule get() {
      return instance;
   }

   @Override
   protected void onEnable() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         this.cameraYaw = mc.field_1724.method_36454();
         this.cameraPitch = mc.field_1724.method_36455();
         this.prePers = mc.field_1690.method_31044();
         if (this.prePers != class_5498.field_26665 && this.togglePerspective.get()) {
            mc.field_1690.method_31043(class_5498.field_26665);
         }
      }
   }

   @Override
   protected void onDisable() {
      class_310 mc = class_310.method_1551();
      if (this.prePers != null && mc.field_1690.method_31044() != this.prePers && this.togglePerspective.get()) {
         mc.field_1690.method_31043(this.prePers);
      }
   }

   @Override
   public void onTick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         if (this.arrows.get()) {
            long win = mc.method_22683().method_4490();
            boolean left = GLFW.glfwGetKey(win, 263) == 1;
            boolean right = GLFW.glfwGetKey(win, 262) == 1;
            boolean up = GLFW.glfwGetKey(win, 265) == 1;
            boolean down = GLFW.glfwGetKey(win, 264) == 1;
            int iterations = (int)(this.arrowSpeed.get() * 2.0);

            for (int i = 0; i < iterations; i++) {
               if (this.mode.is("Player")) {
                  if (left) {
                     this.cameraYaw -= 0.5F;
                  }

                  if (right) {
                     this.cameraYaw += 0.5F;
                  }

                  if (up) {
                     this.cameraPitch -= 0.5F;
                  }

                  if (down) {
                     this.cameraPitch += 0.5F;
                  }
               } else {
                  float yaw = mc.field_1724.method_36454();
                  float pitch = mc.field_1724.method_36455();
                  if (left) {
                     yaw -= 0.5F;
                  }

                  if (right) {
                     yaw += 0.5F;
                  }

                  if (up) {
                     pitch -= 0.5F;
                  }

                  if (down) {
                     pitch += 0.5F;
                  }

                  mc.field_1724.method_36456(yaw);
                  mc.field_1724.method_36457(pitch);
               }
            }
         }

         mc.field_1724.method_36457(class_3532.method_15363(mc.field_1724.method_36455(), -90.0F, 90.0F));
         this.cameraPitch = class_3532.method_15363(this.cameraPitch, -90.0F, 90.0F);
      }
   }

   public boolean isActive() {
      return this.isEnabled() && class_310.method_1551().field_1724 != null;
   }

   public boolean seeThroughWalls() {
      return this.isActive() && this.throughWalls.get();
   }

   public boolean cameraMode() {
      return this.isActive() && this.mode.is("Camera");
   }

   public boolean playerMode() {
      return this.isActive() && class_310.method_1551().field_1690.method_31044() == class_5498.field_26665 && this.mode.is("Player");
   }

   public void addCameraLook(double deltaX, double deltaY) {
      float sens = this.sensitivity.getFloat();
      if (sens <= 0.0F) {
         sens = 1.0F;
      }

      this.cameraYaw += (float)(deltaX / sens);
      this.cameraPitch += (float)(deltaY / sens);
      if (Math.abs(this.cameraPitch) > 90.0F) {
         this.cameraPitch = this.cameraPitch > 0.0F ? 90.0F : -90.0F;
      }
   }

   public float getCameraYaw() {
      return this.cameraYaw;
   }

   public float getCameraPitch() {
      return this.cameraPitch;
   }
}
