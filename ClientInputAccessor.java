package com.larpclient.mixin;

import net.minecraft.class_10185;
import net.minecraft.class_241;
import net.minecraft.class_744;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_744.class})
public interface ClientInputAccessor {
   @Accessor("field_54155")
   void larpclient$setKeyPresses(class_10185 var1);

   @Accessor("field_55868")
   void larpclient$setMoveVector(class_241 var1);
}
