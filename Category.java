package com.larpclient.module;

public enum Category {
   COMBAT("Combat", "combat"),
   MOVEMENT("Movement", "movement"),
   PLAYER("Player", "player"),
   WORLD("World", "world"),
   RENDER("Render", "render"),
   VISUALS("Visuals", "visuals"),
   CLIENT("Client", "client"),
   MISC("Misc", "misc");

   private final String displayName;
   private final String iconId;

   private Category(String displayName, String iconId) {
      this.displayName = displayName;
      this.iconId = iconId;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getIconId() {
      return this.iconId;
   }
}
