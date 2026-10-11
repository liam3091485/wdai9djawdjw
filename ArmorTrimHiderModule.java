package com.larpclient.module.impl;

import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ModeSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_5455;
import net.minecraft.class_6880;
import net.minecraft.class_7924;
import net.minecraft.class_8053;
import net.minecraft.class_8054;
import net.minecraft.class_8056;
import org.jetbrains.annotations.Nullable;

public class ArmorTrimHiderModule extends Module {
   public final ModeSetting mode = this.addSetting(
      new ModeSetting(
         "Mode",
         "Hide wipes worn trims; Random gives every piece a random one",
         "Hide",
         "Hide",
         "Random"
      )
   );
   public final BooleanSetting ownArmor = this.addSetting(
      new BooleanSetting("Own Armor", "Also affect your own worn armor (F5 / inventory)", true)
   );
   private class_5455 cachedAccess;
   private final List<class_6880<class_8054>> materials = new ArrayList<>();
   private final List<class_6880<class_8056>> patterns = new ArrayList<>();

   public ArmorTrimHiderModule() {
      super("ArmorTrimHider", "Hides or randomizes worn armor trims", Category.VISUALS);
   }

   public boolean affectsOwn() {
      return this.ownArmor.get();
   }

   @Nullable
   public class_8053 mapTrim(class_1799 stack, @Nullable class_8053 original) {
      if (this.mode.is("Random")) {
         class_8053 random = this.randomTrim(stack);
         return random != null ? random : original;
      } else {
         return null;
      }
   }

   @Nullable
   private class_8053 randomTrim(class_1799 stack) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1687 == null) {
         return null;
      } else {
         class_5455 access = mc.field_1687.method_30349();
         if (access != this.cachedAccess) {
            this.rebuildCache(access);
         }

         if (!this.materials.isEmpty() && !this.patterns.isEmpty()) {
            Random rng = new Random(stack.method_7909().method_7876().hashCode());
            class_6880<class_8054> material = this.materials.get(rng.nextInt(this.materials.size()));
            class_6880<class_8056> pattern = this.patterns.get(rng.nextInt(this.patterns.size()));
            return new class_8053(material, pattern);
         } else {
            return null;
         }
      }
   }

   private void rebuildCache(class_5455 access) {
      this.cachedAccess = access;
      this.materials.clear();
      this.patterns.clear();

      try {
         access.method_30530(class_7924.field_42083).method_42017().forEach(this.materials::add);
         access.method_30530(class_7924.field_42082).method_42017().forEach(this.patterns::add);
      } catch (Exception var3) {
         this.materials.clear();
         this.patterns.clear();
      }
   }

   @Override
   protected void onDisable() {
      this.cachedAccess = null;
      this.materials.clear();
      this.patterns.clear();
   }
}
