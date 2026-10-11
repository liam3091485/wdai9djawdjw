package com.larpclient.gui.panel;

import com.larpclient.gui.ClickGuiState;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.module.ModuleManager;
import com.larpclient.render.nanovg.NVGIcons;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.theme.ThemeManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CategoryPanel extends Panel {
   private static final float ENTRY_GAP = 3.0F;
   private static final float ENTRY_INSET = 6.0F;
   private final Category category;
   private final List<ModuleEntry> entries = new ArrayList<>();
   private String filter = "";

   public CategoryPanel(Category category, ModuleManager modules, ThemeManager themes, ClickGuiState state) {
      super(themes, state.panel(category.name()));
      this.category = category;

      for (Module module : modules.inCategory(category)) {
         this.entries.add(new ModuleEntry(module, themes, state));
      }
   }

   public void setFilter(String query) {
      this.filter = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
   }

   private List<ModuleEntry> visibleEntries() {
      return this.filter.isEmpty()
         ? this.entries
         : this.entries.stream().filter(e -> e.getModule().getName().toLowerCase(Locale.ROOT).contains(this.filter)).toList();
   }

   @Override
   protected String title() {
      return this.category.getDisplayName();
   }

   @Override
   protected int icon() {
      return NVGIcons.get(this.category);
   }

   @Override
   protected float contentHeight(NVGRenderer vg) {
      float h = 12.0F;

      for (ModuleEntry entry : this.visibleEntries()) {
         h += entry.height(vg) + 3.0F;
      }

      return h - 3.0F;
   }

   @Override
   protected void renderContent(NVGRenderer vg, float startY, float mx, float my, float viewTop, float viewBottom) {
      float rowY = startY;
      float entryWidth = 218.0F;

      for (ModuleEntry entry : this.visibleEntries()) {
         float h = entry.height(vg);
         entry.setBounds(this.ps.x + 6.0F, rowY, entryWidth);
         if (rowY + h >= viewTop - 20.0F && rowY <= viewBottom + 20.0F) {
            float fade = this.edgeFade(rowY, rowY + h, viewTop, viewBottom);
            entry.render(vg, mx, my, fade);
         }

         rowY += h + 3.0F;
      }
   }

   @Override
   public boolean mouseClicked(float mx, float my, int button) {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.mouseClicked(mx, my, button)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void mouseDragged(float mx, float my) {
      for (ModuleEntry entry : this.visibleEntries()) {
         entry.mouseDragged(mx, my);
      }
   }

   @Override
   public void mouseReleased() {
      for (ModuleEntry entry : this.visibleEntries()) {
         entry.mouseReleased();
      }
   }

   @Override
   public boolean keyPressed(int keyCode) {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.keyPressed(keyCode)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean charTyped(int codepoint) {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.charTyped(codepoint)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isListening() {
      for (ModuleEntry entry : this.visibleEntries()) {
         if (entry.isListening()) {
            return true;
         }
      }

      return false;
   }
}
