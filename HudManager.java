package com.larpclient.hud;

import com.google.gson.JsonObject;
import com.larpclient.hud.components.ArmorHud;
import com.larpclient.hud.components.ArrayListHud;
import com.larpclient.hud.components.InfoHud;
import com.larpclient.hud.components.KeystrokesHud;
import com.larpclient.hud.components.PotionsHud;
import com.larpclient.hud.components.RadarHud;
import com.larpclient.hud.components.RegionMapHud;
import com.larpclient.hud.components.SpotifyHud;
import com.larpclient.hud.components.StaffListHud;
import com.larpclient.hud.components.WatermarkHud;
import com.larpclient.module.ModuleManager;
import com.larpclient.module.Modules;
import com.larpclient.notification.NotificationManager;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.spotify.SpotifyService;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.CpsTracker;
import com.larpclient.util.TpsTracker;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_310;
import net.minecraft.class_640;
import net.minecraft.class_746;

public class HudManager {
   private final List<HudComponent> components = new ArrayList<>();
   private static final long SESSION_STARTED = System.currentTimeMillis();
   private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
   private static double lastSpeedX;
   private static double lastSpeedZ;
   private static long lastSpeedAt;
   private static double cachedSpeed;

   public HudManager(ModuleManager modules, ThemeManager themes, SpotifyService spotify, NotificationManager notifications) {
      Modules.HudModule hud = modules.hud;
      this.components.add(new WatermarkHud(themes, () -> hud.isEnabled() && hud.watermark.get()));
      this.components.add(new ArrayListHud(modules, hud, themes, () -> hud.isEnabled() && hud.arrayList.get()));
      this.components
         .add(
            new InfoHud(
               "fps",
               themes,
               "FPS",
               () -> Integer.toString(class_310.method_1551().method_47599()),
               0.006F,
               0.985F,
               () -> hud.isEnabled() && hud.fps.get()
            )
         );
      this.components
         .add(
            new InfoHud(
               "ping", themes, "Ping", HudManager::pingString, 0.055F, 0.985F, () -> hud.isEnabled() && hud.ping.get()
            )
         );
      this.components
         .add(
            new InfoHud(
               "coords",
               themes,
               "XYZ",
               HudManager::coordsString,
               0.115F,
               0.985F,
               () -> hud.isEnabled() && hud.coordinates.get()
            )
         );
      this.components
         .add(
            new InfoHud(
               "direction",
               themes,
               "Facing",
               HudManager::directionString,
               0.24F,
               0.985F,
               () -> hud.isEnabled() && hud.direction.get()
            )
         );
      this.components
         .add(
            new InfoHud(
               "tps",
               themes,
               "TPS",
               () -> String.format("%.1f", TpsTracker.get()),
               0.33F,
               0.985F,
               () -> hud.isEnabled() && hud.tps.get()
            )
         );
      this.components
         .add(
            new InfoHud(
               "cps",
               themes,
               "CPS",
               () -> CpsTracker.get(0) + " | " + CpsTracker.get(1),
               0.4F,
               0.985F,
               () -> hud.isEnabled() && hud.cps.get()
            )
         );
      this.components.add(new InfoHud("larp_clock", themes, "TIME", HudManager::clockString, 0.48F, 0.985F, () -> hud.isEnabled() && hud.clock.get()));
      this.components.add(new InfoHud("larp_memory", themes, "RAM", HudManager::memoryString, 0.57F, 0.985F, () -> hud.isEnabled() && hud.memory.get()));
      this.components
         .add(new InfoHud("larp_session", themes, "SESSION", HudManager::sessionString, 0.7F, 0.985F, () -> hud.isEnabled() && hud.sessionTime.get()));
      this.components
         .add(new InfoHud("larp_inventory", themes, "SLOTS", HudManager::freeSlotsString, 0.78F, 0.985F, () -> hud.isEnabled() && hud.inventoryInfo.get()));
      this.components.add(new InfoHud("larp_totems", themes, "TOTEMS", HudManager::totemString, 0.84F, 0.985F, () -> hud.isEnabled() && hud.totemHud.get()));
      this.components.add(new InfoHud("larp_speed", themes, "SPEED", HudManager::speedString, 0.9F, 0.985F, () -> hud.isEnabled() && hud.speedHud.get()));
      this.components
         .add(new InfoHud("larp_durability", themes, "DURA", HudManager::durabilityString, 0.94F, 0.94F, () -> hud.isEnabled() && hud.durabilityHud.get()));
      this.components.add(new ArmorHud(themes, () -> hud.isEnabled() && hud.armor.get()));
      this.components.add(new PotionsHud(themes, () -> hud.isEnabled() && hud.potions.get()));
      this.components.add(new KeystrokesHud(themes, () -> hud.isEnabled() && hud.keystrokes.get()));
      this.components.add(new RadarHud(hud, modules.susChunkFinder, themes, () -> hud.isEnabled() && hud.radar.get()));
      this.components.add(new RegionMapHud(modules.regionMap, themes));
      this.components.add(new StaffListHud(modules.staffList, themes));
      this.components.add(new SpotifyHud(modules.spotify, spotify, themes));
      this.components.add(notifications);
   }

   private static String coordsString() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return "0, 0, 0";
      } else {
         class_2338 pos = player.method_24515();
         return pos.method_10263() + ", " + pos.method_10264() + ", " + pos.method_10260();
      }
   }

   private static String pingString() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && mc.method_1562() != null) {
         class_640 info = mc.method_1562().method_2871(mc.field_1724.method_5667());
         return info == null ? "0ms" : info.method_2959() + "ms";
      } else {
         return "0ms";
      }
   }

   private static String directionString() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return "N";
      } else {
         class_2350 dir = player.method_5735();

         return switch (dir) {
            case field_11043 -> "N  -Z";
            case field_11035 -> "S  +Z";
            case field_11039 -> "W  -X";
            case field_11034 -> "E  +X";
            default -> dir.method_10151().toUpperCase();
         };
      }
   }

   private static String clockString() {
      return LocalTime.now().format(CLOCK_FORMAT);
   }

   private static String memoryString() {
      Runtime rt = Runtime.getRuntime();
      long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
      long max = rt.maxMemory() / 1048576L;
      return used + " / " + max + " MB";
   }

   private static String sessionString() {
      long seconds = Math.max(0L, (System.currentTimeMillis() - SESSION_STARTED) / 1000L);
      long hours = seconds / 3600L;
      long minutes = seconds % 3600L / 60L;
      long secs = seconds % 60L;
      return String.format("%02d:%02d:%02d", hours, minutes, secs);
   }

   private static String freeSlotsString() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return "-";
      } else {
         int free = 0;

         for (int i = 0; i < 36; i++) {
            if (player.method_31548().method_5438(i).method_7960()) {
               free++;
            }
         }

         return free + " free";
      }
   }

   private static String totemString() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return "-";
      } else {
         int count = 0;

         for (int i = 0; i < 36; i++) {
            class_1799 stack = player.method_31548().method_5438(i);
            if (!stack.method_7960() && stack.method_31574(class_1802.field_8288)) {
               count += stack.method_7947();
            }
         }

         class_1799 offhand = player.method_6079();
         if (!offhand.method_7960() && offhand.method_31574(class_1802.field_8288)) {
            count += offhand.method_7947();
         }

         return Integer.toString(count);
      }
   }

   private static String speedString() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         lastSpeedAt = 0L;
         cachedSpeed = 0.0;
         return "0.0 b/s";
      } else {
         long now = System.currentTimeMillis();
         double x = player.method_23317();
         double z = player.method_23321();
         if (lastSpeedAt == 0L) {
            lastSpeedX = x;
            lastSpeedZ = z;
            lastSpeedAt = now;
         } else {
            long elapsedMs = now - lastSpeedAt;
            if (elapsedMs >= 45L) {
               double seconds = elapsedMs / 1000.0;
               double distance = Math.hypot(x - lastSpeedX, z - lastSpeedZ);
               cachedSpeed = distance / seconds;
               if (!Double.isFinite(cachedSpeed) || cachedSpeed > 250.0) {
                  cachedSpeed = 0.0;
               }

               lastSpeedX = x;
               lastSpeedZ = z;
               lastSpeedAt = now;
            }
         }

         return String.format("%.1f b/s", cachedSpeed);
      }
   }

   private static String durabilityString() {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return "-";
      } else {
         int lowest = 101;

         for (int i = 0; i < 36; i++) {
            class_1799 stack = player.method_31548().method_5438(i);
            if (!stack.method_7960() && stack.method_7936() > 0) {
               int pct = Math.round((1.0F - (float)stack.method_7919() / stack.method_7936()) * 100.0F);
               if (pct < lowest) {
                  lowest = pct;
               }
            }
         }

         return lowest == 101 ? "-" : lowest + "%";
      }
   }

   public List<HudComponent> getComponents() {
      return this.components;
   }

   public List<HudManager.Placement> layout(NVGRenderer vg, float uiWidth, float uiHeight, boolean includeHidden) {
      ArrayList<HudManager.Placement> placements = new ArrayList<>();

      for (HudComponent component : this.components) {
         if (includeHidden || component.visible()) {
            float scale = component.getScale();
            float w = component.measureWidth(vg) * scale;
            float h = component.measureHeight(vg) * scale;
            float x = component.getFx() * (uiWidth - w);
            float y = component.getFy() * (uiHeight - h);
            placements.add(new HudManager.Placement(component, x, y, w, h));
         }
      }

      return placements;
   }

   public boolean hasVisibleComponents() {
      for (HudComponent component : this.components) {
         if (component.visible() && !(component instanceof NotificationManager)) {
            return true;
         }
      }

      return false;
   }

   public void render(NVGRenderer vg, float uiWidth, float uiHeight) {
      for (HudComponent component : this.components) {
         if (component.visible() && !(component instanceof NotificationManager)) {
            float scale = component.getScale();
            float w = component.measureWidth(vg) * scale;
            float h = component.measureHeight(vg) * scale;
            float x = component.getFx() * (uiWidth - w);
            float y = component.getFy() * (uiHeight - h);
            this.renderPlacement(vg, new HudManager.Placement(component, x, y, w, h));
         }
      }
   }

   public void renderPlacement(NVGRenderer vg, HudManager.Placement p) {
      float scale = p.component().getScale();
      vg.save();
      vg.translate(Math.round(p.x()), Math.round(p.y()));
      vg.scale(scale);
      p.component().render(vg, 0.0F, 0.0F, p.w() / scale, p.h() / scale);
      vg.restore();
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();

      for (HudComponent component : this.components) {
         JsonObject entry = new JsonObject();
         entry.addProperty("fx", component.getFx());
         entry.addProperty("fy", component.getFy());
         entry.addProperty("scale", component.getScale());
         json.add(component.getId(), entry);
      }

      return json;
   }

   public void fromJson(JsonObject json) {
      for (HudComponent component : this.components) {
         JsonObject entry = json.getAsJsonObject(component.getId());
         if (entry != null && entry.has("fx") && entry.has("fy")) {
            component.setPosition(entry.get("fx").getAsFloat(), entry.get("fy").getAsFloat());
            if (entry.has("scale")) {
               component.setScale(entry.get("scale").getAsFloat());
            }
         }
      }
   }

   public record Placement(HudComponent component, float x, float y, float w, float h) {
      public boolean contains(float px, float py) {
         return px >= this.x && px <= this.x + this.w && py >= this.y && py <= this.y + this.h;
      }
   }
}
