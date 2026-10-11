package com.larpclient.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Arrays;
import java.util.List;

public class ModeSetting extends Setting<String> {
   private final List<String> modes;

   public ModeSetting(String name, String description, String defaultValue, String... modes) {
      super(name, description, sanitizeDefault(defaultValue, modes));
      this.modes = sanitizeModes(modes);
      if (!this.modes.contains(this.value)) {
         throw new IllegalArgumentException("Default mode '" + this.value + "' not in modes for " + name);
      }
   }

   private static List<String> sanitizeModes(String[] modes) {
      List<String> filtered = Arrays.stream(modes).filter(mode -> mode != null && !mode.equalsIgnoreCase("Chat")).toList();
      if (filtered.isEmpty()) {
         throw new IllegalArgumentException("ModeSetting cannot contain only the removed Chat mode");
      } else {
         return filtered;
      }
   }

   private static String sanitizeDefault(String defaultValue, String[] modes) {
      return defaultValue != null && !defaultValue.equalsIgnoreCase("Chat")
         ? defaultValue
         : Arrays.stream(modes)
            .filter(mode -> mode != null && !mode.equalsIgnoreCase("Chat"))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("ModeSetting cannot contain only the removed Chat mode"));
   }

   public List<String> getModes() {
      return this.modes;
   }

   public boolean is(String mode) {
      return this.get().equals(mode);
   }

   public void cycle() {
      int next = (this.modes.indexOf(this.get()) + 1) % this.modes.size();
      this.set(this.modes.get(next));
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(this.value);
   }

   @Override
   public void fromJson(JsonElement element) {
      if (element != null && element.isJsonPrimitive() && this.modes.contains(element.getAsString())) {
         this.value = element.getAsString();
      }
   }
}
