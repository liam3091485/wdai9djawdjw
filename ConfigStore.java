package com.larpclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.larpclient.LarpClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import net.fabricmc.loader.api.FabricLoader;

public class ConfigStore {
   public static final int SLOT_COUNT = 5;
   public static final int CONFIG_VERSION = 1;
   public static final String FORMAT = "larpclient-config";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   public static volatile boolean applying;
   private final ConfigManager config;
   private final Path dir;
   private final ConfigStore.Slot[] slots = new ConfigStore.Slot[5];
   private int active = -1;

   public ConfigStore(ConfigManager config) {
      this.config = config;
      this.dir = FabricLoader.getInstance().getConfigDir().resolve("sixsevenclient-configs");

      for (int i = 0; i < 5; i++) {
         this.slots[i] = new ConfigStore.Slot(i);
      }
   }

   public static String defaultName(int index) {
      return "Config " + (index + 1);
   }

   public ConfigStore.Slot slot(int index) {
      return this.slots[index];
   }

   public ConfigStore.Slot[] slots() {
      return this.slots;
   }

   public int activeIndex() {
      return this.active;
   }

   public Path directory() {
      return this.dir;
   }

   public void loadAll() {
      for (int i = 0; i < 5; i++) {
         this.refreshSlot(i);
      }

      JsonObject index = this.readJson(this.dir.resolve("index.json"));
      int a;
      if (index != null
         && index.has("active")
         && (a = index.get("active").getAsInt()) >= 0
         && a < 5
         && this.slots[a].filled) {
         this.active = a;
      }
   }

   private void refreshSlot(int i) {
      ConfigStore.Slot slot = this.slots[i];
      JsonObject file = this.readJson(this.slotPath(i));
      JsonObject state = this.extractState(file);
      if (state == null) {
         slot.filled = false;
         slot.savedAt = 0L;
         slot.name = defaultName(i);
      } else {
         slot.filled = true;
         slot.name = file.has("name") && !file.get("name").getAsString().isBlank()
            ? file.get("name").getAsString()
            : defaultName(i);
         slot.savedAt = file.has("savedAt") ? file.get("savedAt").getAsLong() : 0L;
      }
   }

   public boolean save(int i) {
      ConfigStore.Slot slot = this.slots[i];
      JsonObject file = new JsonObject();
      file.addProperty("format", "larpclient-config");
      file.addProperty("version", 1);
      file.addProperty("name", slot.name);
      file.addProperty("savedAt", System.currentTimeMillis());
      file.addProperty("client", "1.6.2");
      file.add("state", this.config.captureState());
      if (!this.write(this.slotPath(i), file)) {
         return false;
      } else {
         this.refreshSlot(i);
         return true;
      }
   }

   public boolean activate(int i) {
      JsonObject state = this.extractState(this.readJson(this.slotPath(i)));
      if (state == null) {
         return false;
      } else {
         applying = true;

         try {
            this.config.applyState(state);
         } finally {
            applying = false;
         }

         this.active = i;
         this.writeIndex();
         return true;
      }
   }

   public boolean delete(int i) {
      try {
         Files.deleteIfExists(this.slotPath(i));
      } catch (IOException var3) {
         LarpClient.LOGGER.error("Failed to delete config file {}", this.slotPath(i).getFileName(), var3);
         return false;
      }

      if (this.active == i) {
         this.active = -1;
         this.writeIndex();
      }

      this.refreshSlot(i);
      return true;
   }

   public boolean rename(int i, String name) {
      ConfigStore.Slot slot = this.slots[i];
      String clean = this.sanitizeName(name);
      if (clean.isEmpty()) {
         clean = defaultName(i);
      }

      slot.name = clean;
      if (!slot.filled) {
         return true;
      } else {
         JsonObject file = this.readJson(this.slotPath(i));
         if (file == null) {
            return false;
         } else {
            file.addProperty("name", clean);
            return this.write(this.slotPath(i), file);
         }
      }
   }

   public String export(int i) {
      JsonObject file = this.readJson(this.slotPath(i));
      if (this.extractState(file) == null) {
         return null;
      } else {
         String json = GSON.toJson(file);
         String base = this.sanitizeFileName(this.slots[i].name);
         this.write(this.dir.resolve(base + ".json"), file);
         return "LARP1:" + Base64.getUrlEncoder().withoutPadding().encodeToString(json.getBytes(StandardCharsets.UTF_8));
      }
   }

   public ConfigStore.ImportResult importInto(int i, String raw) {
      if (raw != null && !raw.isBlank()) {
         raw = raw.trim();
         if (raw.startsWith("LARP1:")) {
            try {
               byte[] decoded = Base64.getUrlDecoder().decode(raw.substring(6));
               raw = new String(decoded, StandardCharsets.UTF_8);
            } catch (IllegalArgumentException var9) {
               return new ConfigStore.ImportResult(false, "Invalid LARP share code");
            }
         }

         JsonObject parsed;
         try {
            parsed = JsonParser.parseString(raw).getAsJsonObject();
         } catch (Exception var8) {
            return new ConfigStore.ImportResult(false, "Not valid config JSON");
         }

         if (parsed.has("version")
            && parsed.get("version").isJsonPrimitive()
            && parsed.get("version").getAsInt() > 1) {
            return new ConfigStore.ImportResult(false, "Config is from a newer client");
         } else {
            JsonObject state = this.extractState(parsed);
            if (state == null) {
               return new ConfigStore.ImportResult(false, "No config data found");
            } else {
               ConfigStore.Slot slot = this.slots[i];
               String name = parsed.has("name") && !parsed.get("name").getAsString().isBlank()
                  ? this.sanitizeName(parsed.get("name").getAsString())
                  : slot.name;
               JsonObject file = new JsonObject();
               file.addProperty("format", "larpclient-config");
               file.addProperty("version", 1);
               file.addProperty("name", name);
               file.addProperty(
                  "savedAt",
                  parsed.has("savedAt")
                     ? parsed.get("savedAt").getAsLong()
                     : System.currentTimeMillis()
               );
               if (parsed.has("client")) {
                  file.addProperty("client", parsed.get("client").getAsString());
               }

               file.add("state", state);
               if (!this.write(this.slotPath(i), file)) {
                  return new ConfigStore.ImportResult(false, "Couldn't write the slot file");
               } else {
                  this.refreshSlot(i);
                  return new ConfigStore.ImportResult(true, "Imported \"" + slot.name + "\"");
               }
            }
         }
      } else {
         return new ConfigStore.ImportResult(false, "Clipboard is empty");
      }
   }

   private JsonObject extractState(JsonObject file) {
      if (file == null) {
         return null;
      } else if (file.has("state") && file.get("state").isJsonObject()) {
         return file.getAsJsonObject("state");
      } else {
         return file.has("modules") && file.get("modules").isJsonObject() ? file : null;
      }
   }

   private Path slotPath(int i) {
      return this.dir.resolve("slot" + (i + 1) + ".json");
   }

   private void writeIndex() {
      JsonObject index = new JsonObject();
      index.addProperty("active", this.active);
      this.write(this.dir.resolve("index.json"), index);
   }

   private JsonObject readJson(Path path) {
      if (!Files.exists(path)) {
         return null;
      } else {
         try {
            return JsonParser.parseString(Files.readString(path)).getAsJsonObject();
         } catch (Exception var3) {
            LarpClient.LOGGER.warn("Ignoring unreadable config file {}", path.getFileName(), var3);
            return null;
         }
      }
   }

   private boolean write(Path path, JsonObject json) {
      try {
         Files.createDirectories(this.dir);
         Files.writeString(path, GSON.toJson(json));
         return true;
      } catch (IOException var4) {
         LarpClient.LOGGER.error("Failed to write config file {}", path.getFileName(), var4);
         return false;
      }
   }

   private String sanitizeName(String name) {
      if (name == null) {
         return "";
      } else {
         String clean = name.replaceAll("[\\r\\n\\t]", " ").trim();
         return clean.length() > 24 ? clean.substring(0, 24) : clean;
      }
   }

   private String sanitizeFileName(String name) {
      String clean = name.replaceAll("[^a-zA-Z0-9-_ ]", "").trim().replace(' ', '_');
      return clean.isEmpty() ? "config" : clean;
   }

   public record ImportResult(boolean ok, String message) {
   }

   public static final class Slot {
      private final int index;
      private String name;
      private boolean filled;
      private long savedAt;

      Slot(int index) {
         this.index = index;
         this.name = ConfigStore.defaultName(index);
      }

      public int index() {
         return this.index;
      }

      public String name() {
         return this.name;
      }

      public boolean filled() {
         return this.filled;
      }

      public long savedAt() {
         return this.savedAt;
      }
   }
}
