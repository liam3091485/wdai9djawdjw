package com.larpclient.gui.panel;

import com.larpclient.gui.ClickGuiState;
import com.larpclient.render.anim.Animation;
import com.larpclient.render.nanovg.NVGRenderer;
import com.larpclient.theme.Theme;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.Colors;
import com.larpclient.util.UiSounds;

public abstract class Panel {
   public static final float WIDTH = 230.0F;
   public static final float HEADER_H = 42.0F;
   public static final float RADIUS = 9.0F;
   protected static final float CONTENT_PAD = 6.0F;
   private static final float FADE_ZONE = 16.0F;
   protected final ThemeManager themes;
   protected final ClickGuiState.PanelState ps;
   private final Animation open;
   private final Animation scroll = new Animation(200.0F, 0.0F);
   private float maxScroll;
   private boolean headerHovered;

   protected Panel(ThemeManager themes, ClickGuiState.PanelState ps) {
      this.themes = themes;
      this.ps = ps;
      this.open = new Animation(200.0F, ps.collapsed ? 0.0F : 1.0F);
   }

   protected Theme theme() {
      return this.themes.current();
   }

   public float x() {
      return this.ps.x;
   }

   public float y() {
      return this.ps.y;
   }

   public void moveTo(float x, float y) {
      this.ps.x = x;
      this.ps.y = y;
   }

   public void toggleCollapsed() {
      this.ps.collapsed = !this.ps.collapsed;
      this.open.setTarget(this.ps.collapsed ? 0.0F : 1.0F);
   }

   protected abstract String title();

   protected int icon() {
      return -1;
   }

   protected abstract float contentHeight(NVGRenderer var1);

   protected abstract void renderContent(NVGRenderer var1, float var2, float var3, float var4, float var5, float var6);

   protected float maxViewHeight(float uiHeight) {
      return Math.max(60.0F, uiHeight - this.ps.y - 42.0F - 24.0F);
   }

   protected float viewHeight(NVGRenderer vg, float uiHeight) {
      return Math.min(this.contentHeight(vg), this.maxViewHeight(uiHeight)) * this.open.value();
   }

   public float totalHeight(NVGRenderer vg, float uiHeight) {
      return 42.0F + this.viewHeight(vg, uiHeight);
   }

   protected float edgeFade(float rowTop, float rowBottom, float viewTop, float viewBottom) {
      float fadeTop = Math.clamp((rowBottom - viewTop) / 16.0F, 0.0F, 1.0F);
      float fadeBottom = Math.clamp((viewBottom - rowTop) / 16.0F, 0.0F, 1.0F);
      return Math.min(fadeTop, fadeBottom);
   }

   public void render(NVGRenderer vg, float mx, float my, float uiWidth, float uiHeight) {
      this.ps.x = Math.clamp(this.ps.x, -170.0F, uiWidth - 40.0F);
      this.ps.y = Math.clamp(this.ps.y, 0.0F, uiHeight - 42.0F);
      Theme theme = this.theme();
      float viewH = this.viewHeight(vg, uiHeight);
      boolean hasBody = viewH > 0.5F;
      boolean hoveredNow = this.headerHit(mx, my);
      if (hoveredNow && !this.headerHovered) {
         UiSounds.hover();
      }

      this.headerHovered = hoveredNow;
      vg.glow(this.ps.x, this.ps.y, 230.0F, 42.0F + viewH, 9.0F, 14.0F, Colors.withAlpha(-16777216, 0.35F));
      vg.rectOutline(this.ps.x, this.ps.y, 230.0F, 42.0F + viewH, 9.0F, 1.0F, Colors.withAlpha(-16734209, 0.42F));
      if (hasBody) {
         vg.rectVaryingGradient(this.ps.x, this.ps.y + 42.0F, 230.0F, viewH, 0.0F, 0.0F, 9.0F, 9.0F, theme.background(), theme.backgroundTo());
      }

      float bottomRadius = hasBody ? 0.0F : 9.0F;
      vg.rectVaryingGradient(this.ps.x, this.ps.y, 230.0F, 42.0F, 9.0F, 9.0F, bottomRadius, bottomRadius, theme.headerTop(), theme.headerBottom());
      if (hasBody) {
         vg.rectGradient(
            this.ps.x + 1.0F,
            this.ps.y + 42.0F - 1.5F,
            228.0F,
            1.5F,
            0.75F,
            Colors.withAlpha(theme.accent(), 0.85F),
            Colors.withAlpha(theme.accentBright(), 0.5F),
            false
         );
      }

      float textX = this.ps.x + 9.0F;
      int iconHandle = this.icon();
      if (iconHandle > 0) {
         vg.image(iconHandle, this.ps.x + 11.0F, this.ps.y + 19.0F - 9.0F, 18.0F, 18.0F, theme.accentBright());
         textX = this.ps.x + 36.0F;
      }

      vg.text(this.title(), textX, this.ps.y + 19.0F, 16.5F, theme.textPrimary());
      float chevX = this.ps.x + 230.0F - 16.0F;
      float chevY = this.ps.y + 19.0F;
      vg.save();
      vg.translate(chevX, chevY);
      vg.rotate((float)(this.open.value() * Math.PI / 2.0));
      vg.chevron(0.0F, 0.0F, 4.5F, 1.8F, theme.textMuted(), false);
      vg.restore();
      if (hasBody) {
         float viewTop = this.ps.y + 42.0F;
         float viewBottom = viewTop + viewH;
         this.maxScroll = Math.max(0.0F, this.contentHeight(vg) - this.maxViewHeight(uiHeight));
         this.scroll.setTarget(Math.clamp(this.scroll.getTarget(), 0.0F, this.maxScroll));
         vg.save();
         vg.scissor(this.ps.x, viewTop, 230.0F, viewH);
         this.renderContent(vg, viewTop + 6.0F - this.scroll.value(), mx, my, viewTop, viewBottom);
         vg.restore();
      }
   }

   public boolean headerHit(float mx, float my) {
      return mx >= this.ps.x && mx <= this.ps.x + 230.0F && my >= this.ps.y && my <= this.ps.y + 42.0F;
   }

   public boolean bodyHit(NVGRenderer vg, float mx, float my, float uiHeight) {
      float viewH = this.viewHeight(vg, uiHeight);
      return mx >= this.ps.x && mx <= this.ps.x + 230.0F && my >= this.ps.y + 42.0F && my <= this.ps.y + 42.0F + viewH;
   }

   public void onScroll(double amount) {
      if (!(this.maxScroll <= 0.0F)) {
         this.scroll.setTarget(Math.clamp(this.scroll.getTarget() - (float)amount * 42.0F, 0.0F, this.maxScroll));
      }
   }

   public boolean mouseClicked(float mx, float my, int button) {
      return false;
   }

   public void mouseDragged(float mx, float my) {
   }

   public void mouseReleased() {
   }

   public boolean keyPressed(int keyCode) {
      return false;
   }

   public boolean charTyped(int codepoint) {
      return false;
   }

   public boolean isListening() {
      return false;
   }
}
