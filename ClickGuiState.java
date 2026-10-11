package com.larpclient.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.larpclient.module.Category;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ClickGuiState {
   public static final String THEMES_PANEL = "__themes__";
   private final Map<String, ClickGuiState.PanelState> panels = new LinkedHashMap<>();
   private final Set<String> expandedModules = new HashSet<>();
   private final Set<String> favoriteModules = new HashSet<>();
   private boolean laidOut;
   private boolean customized;
   private float lastLayoutWidth = -1.0F;
   private float lastLayoutHeight = -1.0F;
   private static final int LAYOUT_VERSION = 5;
   private static final float PANEL_W = 230.0F;
   private static final float SEARCH_W = 280.0F;

   public void markCustomized() {
      this.customized = true;
   }

   public ClickGuiState() {
      float x = 16.0F;

      for (Category category : Category.values()) {
         this.panels.put(category.name(), new ClickGuiState.PanelState(x, 16.0F));
         x += 246.0F;
      }

      this.panels.put("__themes__", new ClickGuiState.PanelState(x, 320.0F));
   }

   public void ensureDefaultLayout(float uiWidth, float uiHeight) {
      if (!this.customized && (!this.laidOut || uiWidth != this.lastLayoutWidth || uiHeight != this.lastLayoutHeight)) {
         this.laidOut = true;
         this.lastLayoutWidth = uiWidth;
         this.lastLayoutHeight = uiHeight;
         String[] order = new String[]{
            Category.COMBAT.name(),
            Category.MOVEMENT.name(),
            Category.PLAYER.name(),
            Category.WORLD.name(),
            Category.RENDER.name(),
            Category.VISUALS.name(),
            Category.CLIENT.name(),
            Category.MISC.name(),
            "__themes__"
         };
         if (uiWidth >= 1180.0F) {
            float gap = 24.0F;
            float rowWidth = 690.0F + 2.0F * gap;
            float startX = (uiWidth - rowWidth) / 2.0F;
            float topY = 72.0F;
            float bottomY = Math.max(310.0F, uiHeight * 0.52F);

            for (int i = 0; i < order.length; i++) {
               ClickGuiState.PanelState ps = this.panel(order[i]);
               int column = i % 3;
               int row = i / 3;
               ps.x = startX + column * (230.0F + gap);
               ps.y = row == 0 ? topY : bottomY;
               ps.collapsed = row > 0;
            }
         } else {
            float spacing = 244.0F;
            int perRow = Math.max(1, (int)((uiWidth - 24.0F) / spacing));

            for (int i = 0; i < order.length; i++) {
               ClickGuiState.PanelState ps = this.panel(order[i]);
               ps.x = 12.0F + i % perRow * spacing;
               ps.y = 68.0F + i / perRow * (uiHeight * 0.42F);
               ps.collapsed = i / perRow > 0;
            }
         }
      }
   }

   public ClickGuiState.PanelState panel(String key) {
      return this.panels.computeIfAbsent(key, k -> new ClickGuiState.PanelState(16.0F, 16.0F));
   }

   public boolean isExpanded(String moduleKey) {
      return this.expandedModules.contains(moduleKey);
   }

   public boolean isFavorite(String moduleKey) {
      return this.favoriteModules.contains(moduleKey);
   }

   public void toggleFavorite(String moduleKey) {
      if (!this.favoriteModules.add(moduleKey)) {
         this.favoriteModules.remove(moduleKey);
      }
   }

   public int favoriteCount() {
      return this.favoriteModules.size();
   }

   public void setExpanded(String moduleKey, boolean expanded) {
      if (expanded) {
         this.expandedModules.add(moduleKey);
      } else {
         this.expandedModules.remove(moduleKey);
      }
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      json.addProperty("v", 5);
      json.addProperty("custom", this.customized);
      JsonArray favs = new JsonArray();

      for (String key : this.favoriteModules) {
         favs.add(key);
      }

      json.add("larp_favorites", favs);

      for (Entry<String, ClickGuiState.PanelState> entry : this.panels.entrySet()) {
         JsonObject p = new JsonObject();
         p.addProperty("x", entry.getValue().x);
         p.addProperty("y", entry.getValue().y);
         p.addProperty("collapsed", entry.getValue().collapsed);
         json.add(entry.getKey(), p);
      }

      return json;
   }

   public void fromJson(JsonObject json) {
      if (json.has("v") && json.get("v").getAsInt() >= 5) {
         if (json.has("custom") && json.get("custom").getAsBoolean()) {
            this.laidOut = true;
            this.customized = true;
            this.favoriteModules.clear();
            if (json.has("larp_favorites") && json.get("larp_favorites").isJsonArray()) {
               for (JsonElement e : json.getAsJsonArray("larp_favorites")) {
                  this.favoriteModules.add(e.getAsString());
               }
            }

            for (Entry<String, ClickGuiState.PanelState> entry : this.panels.entrySet()) {
               JsonObject p = json.getAsJsonObject(entry.getKey());
               if (p != null) {
                  if (p.has("x")) {
                     entry.getValue().x = p.get("x").getAsFloat();
                  }

                  if (p.has("y")) {
                     entry.getValue().y = p.get("y").getAsFloat();
                  }

                  if (p.has("collapsed")) {
                     entry.getValue().collapsed = p.get("collapsed").getAsBoolean();
                  }
               }
            }
         }
      }
   }

   public static class PanelState {
      public float x;
      public float y;
      public boolean collapsed;

      PanelState(float x, float y) {
         this.x = x;
         this.y = y;
      }
   }
}
