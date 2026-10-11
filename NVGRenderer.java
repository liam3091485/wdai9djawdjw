package com.larpclient.render.nanovg;

import com.larpclient.LarpClient;
import com.larpclient.util.Colors;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class NVGRenderer {
   public static final String FONT_POPPINS = "poppins";
   public static final String FONT_BEBAS = "bebas";
   public static final String FONT_PACIFICO = "pacifico";
   public static final String FONT_ANTON = "anton";
   public static final String FONT_BUNGEE = "bungee";
   public static final String FONT_XUONG = "xuong";
   public static final String FONT_FALLBACK = "larp_system_fallback";
   private static NVGRenderer instance;
   private final long ctx;
   private boolean poppinsLoaded;
   private boolean bebasLoaded;
   private boolean pacificoLoaded;
   private boolean antonLoaded;
   private boolean bungeeLoaded;
   private boolean xuongLoaded;
   private boolean fallbackLoaded;
   private final List<ByteBuffer> retainedFontData = new ArrayList<>();
   private String activeFont = "poppins";
   private String fontStyleMode = "Poppins";
   private final ArrayDeque<Float> alphaStack = new ArrayDeque<>();
   private float appliedAlpha = 1.0F;
   private long lastExternalFontRetryMs = 0L;

   private NVGRenderer() {
      this.ctx = NanoVGGL3.nvgCreate(3);
      if (this.ctx == 0L) {
         throw new IllegalStateException("Failed to create NanoVG context.");
      } else {
         this.ensureAutomaticFonts();
         this.poppinsLoaded = this.loadFontWithExternalFallback("poppins", "assets/larpclient/fonts/Poppins-Regular.ttf", "Poppins-Regular.ttf");
         this.bebasLoaded = this.loadFontWithExternalFallback("bebas", "assets/larpclient/fonts/BebasNeue-Regular.ttf", "BebasNeue-Regular.ttf");
         this.pacificoLoaded = this.loadFontWithExternalFallback("pacifico", "assets/larpclient/fonts/Pacifico-Regular.ttf", "Pacifico-Regular.ttf");
         this.antonLoaded = this.loadFontWithExternalFallback("anton", "assets/larpclient/fonts/Anton-Regular.ttf", "Anton-Regular.ttf");
         this.bungeeLoaded = this.loadFontWithExternalFallback("bungee", "assets/larpclient/fonts/Bungee-Regular.ttf", "Bungee-Regular.ttf");
         this.xuongLoaded = this.loadFontWithExternalFallback("xuong", "assets/larpclient/fonts/Xuong.ttf", "Xuong.ttf", "Xuong.otf");
         this.fallbackLoaded = this.loadSystemFallbackFont();
         if (this.poppinsLoaded) {
            this.activeFont = "poppins";
         } else if (this.bebasLoaded) {
            this.activeFont = "bebas";
         } else if (this.pacificoLoaded) {
            this.activeFont = "pacifico";
         } else if (this.antonLoaded) {
            this.activeFont = "anton";
         } else if (this.bungeeLoaded) {
            this.activeFont = "bungee";
         } else if (this.xuongLoaded) {
            this.activeFont = "xuong";
         } else if (this.fallbackLoaded) {
            this.activeFont = "larp_system_fallback";
         } else {
            this.activeFont = null;
         }
      }
   }

   public static NVGRenderer get() {
      if (instance == null) {
         instance = new NVGRenderer();
      }

      return instance;
   }

   public long ctx() {
      return this.ctx;
   }

   private boolean loadFont(String name, String resourcePath) {
      try {
         boolean var7;
         try (InputStream in = NVGRenderer.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
               LarpClient.LOGGER.warn("Unable to find font resource: {}", resourcePath);
               return false;
            }

            byte[] bytes = in.readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
            buffer.put(bytes);
            buffer.flip();
            int handle = NanoVG.nvgCreateFontMem(this.ctx, name, buffer, false);
            if (handle == -1) {
               MemoryUtil.memFree(buffer);
               LarpClient.LOGGER.warn("Failed to load font: {}", resourcePath);
               return false;
            }

            this.retainedFontData.add(buffer);
            var7 = true;
         }

         return var7;
      } catch (IOException var10) {
         LarpClient.LOGGER.error("Unable to read font resource: {}", resourcePath, var10);
         return false;
      }
   }

   private void ensureAutomaticFonts() {
   }

   private boolean loadFontWithExternalFallback(String name, String resourcePath, String... fileNames) {
      if (this.loadFont(name, resourcePath)) {
         return true;
      } else {
         ArrayList<Path> roots = new ArrayList<>();
         Path fabricConfigFonts = FabricLoader.getInstance().getConfigDir().resolve("larpclient").resolve("fonts");
         roots.add(fabricConfigFonts);

         try {
            Files.createDirectories(fabricConfigFonts);
         } catch (Exception var24) {
         }

         Path gameFonts = FabricLoader.getInstance().getGameDir().resolve("config").resolve("larpclient").resolve("fonts");
         if (!gameFonts.equals(fabricConfigFonts)) {
            roots.add(gameFonts);
         }

         Path legacyFonts = Path.of("config", "larpclient", "fonts").toAbsolutePath().normalize();
         if (!legacyFonts.equals(fabricConfigFonts) && !legacyFonts.equals(gameFonts)) {
            roots.add(legacyFonts);
         }

         String appData = System.getenv("APPDATA");
         if (appData != null && !appData.isBlank()) {
            roots.add(Path.of(appData, "LARPClient", "fonts"));
         }

         String windir = System.getenv("WINDIR");
         if (windir != null && !windir.isBlank()) {
            roots.add(Path.of(windir, "Fonts"));
         }

         String localAppData = System.getenv("LOCALAPPDATA");
         if (localAppData != null && !localAppData.isBlank()) {
            roots.add(Path.of(localAppData, "Microsoft", "Windows", "Fonts"));
         }

         ArrayList<String> wantedNames = new ArrayList<>();

         for (String fileName : fileNames) {
            wantedNames.add(normalizeFontName(fileName));
         }

         wantedNames.add(normalizeFontName(name));

         for (Path root : roots) {
            for (String fileName : fileNames) {
               try {
                  Path candidate = root.resolve(fileName);
                  if (this.loadFontFile(name, candidate)) {
                     return true;
                  }
               } catch (Exception var22) {
               }
            }

            try {
               if (Files.isDirectory(root)) {
                  try (Stream<Path> stream = Files.walk(root, 2)) {
                     List<Path> files = stream.filter(x$0 -> Files.isRegularFile(x$0)).toList();

                     for (Path candidate : files) {
                        String lower = candidate.getFileName().toString().toLowerCase(Locale.ROOT);
                        if (lower.endsWith(".ttf") || lower.endsWith(".otf")) {
                           String normalized = normalizeFontName(lower);
                           boolean match = wantedNames.stream().anyMatch(w -> !w.isBlank() && (normalized.contains(w) || w.contains(normalized)));
                           if (match && this.loadFontFile(name, candidate)) {
                              return true;
                           }
                        }
                     }

                     for (Path archive : files) {
                        String lower = archive.getFileName().toString().toLowerCase(Locale.ROOT);
                        if (lower.endsWith(".zip") && this.loadFontFromZip(name, archive, wantedNames)) {
                           return true;
                        }
                     }
                  }
               }
            } catch (Exception var26) {
               LarpClient.LOGGER.debug("Unable to scan font folder {}: {}", root, var26.toString());
            }
         }

         LarpClient.LOGGER.warn("LARP font '{}' was not found. Put its .ttf/.otf OR original .zip in {}", name, fabricConfigFonts.toAbsolutePath());
         return false;
      }
   }

   private static String normalizeFontName(String value) {
      if (value == null) {
         return "";
      } else {
         String s = value.toLowerCase(Locale.ROOT);
         s = s.replaceAll("\\.(ttf|otf|zip)$", "");
         s = s.replace("demo", "").replace("regular", "");
         return s.replaceAll("[^a-z0-9]", "");
      }
   }

   private boolean loadFontFile(String name, Path candidate) {
      try {
         if (candidate == null || !Files.isRegularFile(candidate)) {
            return false;
         }

         int handle = NanoVG.nvgCreateFont(this.ctx, name, candidate.toAbsolutePath().toString());
         if (handle != -1) {
            LarpClient.LOGGER.info("Loaded LARP font {} from {}", name, candidate.toAbsolutePath());
            return true;
         }
      } catch (Exception var4) {
      }

      return false;
   }

   private boolean loadFontFromZip(String name, Path archive, List<String> wantedNames) {
      try (ZipFile zip = new ZipFile(archive.toFile())) {
         Enumeration<? extends ZipEntry> entries = zip.entries();

         while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (!entry.isDirectory()) {
               String entryName = entry.getName();
               String lower = entryName.toLowerCase(Locale.ROOT);
               if (lower.endsWith(".ttf") || lower.endsWith(".otf")) {
                  String normalized = normalizeFontName(Path.of(entryName).getFileName().toString());
                  boolean match = wantedNames.stream().anyMatch(w -> !w.isBlank() && (normalized.contains(w) || w.contains(normalized)));
                  if (match) {
                     byte[] bytes;
                     try (InputStream in = zip.getInputStream(entry)) {
                        bytes = in.readAllBytes();
                     }

                     ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length);
                     buffer.put(bytes).flip();
                     int handle = NanoVG.nvgCreateFontMem(this.ctx, name, buffer, false);
                     if (handle != -1) {
                        this.retainedFontData.add(buffer);
                        LarpClient.LOGGER.info("Loaded LARP font {} from {}!{}", new Object[]{name, archive.toAbsolutePath(), entryName});
                        return true;
                     }

                     MemoryUtil.memFree(buffer);
                  }
               }
            }
         }

         return false;
      } catch (Exception var19) {
         return false;
      }
   }

   private boolean loadSystemFallbackFont() {
      String[] candidates = new String[]{
         System.getenv("WINDIR") == null ? null : System.getenv("WINDIR") + "\\Fonts\\segoeui.ttf",
         System.getenv("WINDIR") == null ? null : System.getenv("WINDIR") + "\\Fonts\\arial.ttf",
         "C:\\Windows\\Fonts\\segoeui.ttf",
         "C:\\Windows\\Fonts\\arial.ttf",
         "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
         "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
      };

      for (String path : candidates) {
         if (path != null && !path.isBlank()) {
            try {
               if (Files.isRegularFile(Path.of(path))) {
                  int handle = NanoVG.nvgCreateFont(this.ctx, "larp_system_fallback", path);
                  if (handle != -1) {
                     LarpClient.LOGGER.info("Loaded LARP GUI fallback font from {}", path);
                     return true;
                  }
               }
            } catch (Exception var7) {
            }
         }
      }

      LarpClient.LOGGER.error("No usable LARP GUI font was found. Check internet access or install a standard system font.");
      return false;
   }

   private void retryMissingExternalFonts() {
      long now = System.currentTimeMillis();
      if (now - this.lastExternalFontRetryMs >= 5000L) {
         this.lastExternalFontRetryMs = now;
         this.ensureAutomaticFonts();
         if (!this.poppinsLoaded) {
            this.poppinsLoaded = this.loadFontWithExternalFallback("poppins", "assets/larpclient/fonts/Poppins-Regular.ttf", "Poppins-Regular.ttf");
         }

         if (!this.bebasLoaded) {
            this.bebasLoaded = this.loadFontWithExternalFallback("bebas", "assets/larpclient/fonts/BebasNeue-Regular.ttf", "BebasNeue-Regular.ttf");
         }

         if (!this.pacificoLoaded) {
            this.pacificoLoaded = this.loadFontWithExternalFallback("pacifico", "assets/larpclient/fonts/Pacifico-Regular.ttf", "Pacifico-Regular.ttf");
         }

         if (!this.antonLoaded) {
            this.antonLoaded = this.loadFontWithExternalFallback("anton", "assets/larpclient/fonts/Anton-Regular.ttf", "Anton-Regular.ttf");
         }

         if (!this.bungeeLoaded) {
            this.bungeeLoaded = this.loadFontWithExternalFallback("bungee", "assets/larpclient/fonts/Bungee-Regular.ttf", "Bungee-Regular.ttf");
         }

         if (!this.xuongLoaded) {
            this.xuongLoaded = this.loadFontWithExternalFallback("xuong", "assets/larpclient/fonts/Xuong.ttf", "Xuong.ttf", "Xuong.otf");
         }
      }
   }

   public void reloadExternalFonts() {
      this.lastExternalFontRetryMs = 0L;
      this.retryMissingExternalFonts();
      this.setFontMode(this.fontStyleMode);
   }

   public Path externalFontFolder() {
      return FabricLoader.getInstance().getConfigDir().resolve("larpclient").resolve("fonts");
   }

   public void setFontMode(String mode) {
      this.retryMissingExternalFonts();
      this.fontStyleMode = mode == null ? "Poppins" : mode;
      String var3 = this.fontStyleMode.toLowerCase();

      String wanted = switch (var3) {
         case "bebas", "bebas neue" -> this.bebasLoaded ? "bebas" : null;
         case "pacifico" -> this.pacificoLoaded ? "pacifico" : null;
         case "anton" -> this.antonLoaded ? "anton" : null;
         case "bungee" -> this.bungeeLoaded ? "bungee" : null;
         case "xuong", "xueong" -> this.xuongLoaded ? "xuong" : null;
         default -> this.poppinsLoaded ? "poppins" : null;
      };
      if (wanted != null) {
         this.activeFont = wanted;
      } else if (this.poppinsLoaded) {
         this.activeFont = "poppins";
      } else if (this.bebasLoaded) {
         this.activeFont = "bebas";
      } else if (this.pacificoLoaded) {
         this.activeFont = "pacifico";
      } else if (this.antonLoaded) {
         this.activeFont = "anton";
      } else if (this.bungeeLoaded) {
         this.activeFont = "bungee";
      } else if (this.xuongLoaded) {
         this.activeFont = "xuong";
      } else if (this.fallbackLoaded) {
         this.activeFont = "larp_system_fallback";
      } else {
         this.activeFont = null;
      }
   }

   public boolean hasFont() {
      return this.activeFont != null;
   }

   public void beginFrame(float width, float height, float pixelRatio) {
      this.alphaStack.clear();
      this.appliedAlpha = 1.0F;
      NanoVG.nvgBeginFrame(this.ctx, width, height, pixelRatio);
   }

   public void endFrame() {
      NanoVG.nvgEndFrame(this.ctx);
   }

   public void save() {
      this.alphaStack.push(this.appliedAlpha);
      NanoVG.nvgSave(this.ctx);
   }

   public void restore() {
      if (!this.alphaStack.isEmpty()) {
         this.appliedAlpha = this.alphaStack.pop();
      }

      NanoVG.nvgRestore(this.ctx);
   }

   public void scale(float s) {
      NanoVG.nvgScale(this.ctx, s, s);
   }

   public void translate(float x, float y) {
      NanoVG.nvgTranslate(this.ctx, x, y);
   }

   public void alpha(float a) {
      this.appliedAlpha = this.appliedAlpha * Math.clamp(a, 0.0F, 1.0F);
      NanoVG.nvgGlobalAlpha(this.ctx, this.appliedAlpha);
   }

   public void scissor(float x, float y, float w, float h) {
      NanoVG.nvgIntersectScissor(this.ctx, x, y, w, h);
   }

   public void rect(float x, float y, float w, float h, float radius, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x, y, w, h, radius);
         NanoVG.nvgFillColor(this.ctx, color(stack, argb));
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void rectGradient(float x, float y, float w, float h, float radius, int from, int to, boolean vertical) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         float ex = vertical ? x : x + w;
         float ey = vertical ? y + h : y;
         NanoVG.nvgLinearGradient(this.ctx, x, y, ex, ey, color(stack, from), color(stack, to), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x, y, w, h, radius);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var14) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var13) {
               var14.addSuppressed(var13);
            }
         }

         throw var14;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void rectVaryingGradient(float x, float y, float w, float h, float rtl, float rtr, float rbr, float rbl, int from, int to) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgLinearGradient(this.ctx, x, y, x, y + h, color(stack, from), color(stack, to), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRectVarying(this.ctx, x, y, w, h, rtl, rtr, rbr, rbl);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var15) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void rectOutline(float x, float y, float w, float h, float radius, float stroke, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x + stroke / 2.0F, y + stroke / 2.0F, w - stroke, h - stroke, radius);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var12) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void chevron(float cx, float cy, float size, float stroke, int argb, boolean pointDown) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         if (pointDown) {
            NanoVG.nvgMoveTo(this.ctx, cx - size, cy - size / 2.0F);
            NanoVG.nvgLineTo(this.ctx, cx, cy + size / 2.0F);
            NanoVG.nvgLineTo(this.ctx, cx + size, cy - size / 2.0F);
         } else {
            NanoVG.nvgMoveTo(this.ctx, cx - size / 2.0F, cy - size);
            NanoVG.nvgLineTo(this.ctx, cx + size / 2.0F, cy);
            NanoVG.nvgLineTo(this.ctx, cx - size / 2.0F, cy + size);
         }

         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgLineCap(this.ctx, 1);
         NanoVG.nvgLineJoin(this.ctx, 1);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void triangle(float x0, float y0, float x1, float y1, float x2, float y2, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgMoveTo(this.ctx, x0, y0);
         NanoVG.nvgLineTo(this.ctx, x1, y1);
         NanoVG.nvgLineTo(this.ctx, x2, y2);
         NanoVG.nvgClosePath(this.ctx);
         NanoVG.nvgFillColor(this.ctx, color(stack, argb));
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var12) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void circle(float cx, float cy, float r, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgCircle(this.ctx, cx, cy, r);
         NanoVG.nvgFillColor(this.ctx, color(stack, argb));
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var9) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }
         }

         throw var9;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void circleOutline(float cx, float cy, float r, float stroke, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgCircle(this.ctx, cx, cy, r);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void circleGlow(float cx, float cy, float r, float spread, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgRadialGradient(this.ctx, cx, cy, r * 0.25F, r + spread, color(stack, argb), color(stack, Colors.withAlpha(argb, 0)), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgCircle(this.ctx, cx, cy, r + spread);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void glow(float x, float y, float w, float h, float radius, float spread, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NVGPaint paint = NVGPaint.malloc(stack);
         NanoVG.nvgBoxGradient(this.ctx, x, y, w, h, radius, spread * 2.0F, color(stack, argb), color(stack, Colors.withAlpha(argb, 0)), paint);
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgRoundedRect(this.ctx, x - spread, y - spread, w + spread * 2.0F, h + spread * 2.0F, radius + spread);
         NanoVG.nvgFillPaint(this.ctx, paint);
         NanoVG.nvgFill(this.ctx);
      } catch (Throwable var12) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }
         }

         throw var12;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void line(float x1, float y1, float x2, float y2, float width, int argb) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgMoveTo(this.ctx, x1, y1);
         NanoVG.nvgLineTo(this.ctx, x2, y2);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, width);
         NanoVG.nvgLineCap(this.ctx, 1);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void checkmark(float x, float y, float size, float stroke, int argb) {
      float x1 = x + size * 0.22F;
      float y1 = y + size * 0.55F;
      float x2 = x + size * 0.42F;
      float y2 = y + size * 0.74F;
      float x3 = x + size * 0.78F;
      float y3 = y + size * 0.3F;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         NanoVG.nvgBeginPath(this.ctx);
         NanoVG.nvgMoveTo(this.ctx, x1, y1);
         NanoVG.nvgLineTo(this.ctx, x2, y2);
         NanoVG.nvgLineTo(this.ctx, x3, y3);
         NanoVG.nvgStrokeColor(this.ctx, color(stack, argb));
         NanoVG.nvgStrokeWidth(this.ctx, stroke);
         NanoVG.nvgLineCap(this.ctx, 1);
         NanoVG.nvgLineJoin(this.ctx, 1);
         NanoVG.nvgStroke(this.ctx);
      } catch (Throwable var16) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (stack != null) {
         stack.close();
      }
   }

   public void cross(float x, float y, float size, float stroke, int argb) {
      float pad = size * 0.3F;
      this.line(x + pad, y + pad, x + size - pad, y + size - pad, stroke, argb);
      this.line(x + size - pad, y + pad, x + pad, y + size - pad, stroke, argb);
   }

   private void applyFontStyle(float size) {
      if (this.activeFont != null) {
         NanoVG.nvgFontFace(this.ctx, this.activeFont);
         float spacing = "Poppins".equalsIgnoreCase(this.fontStyleMode) ? 0.1F : 0.0F;
         NanoVG.nvgTextLetterSpacing(this.ctx, spacing);
         NanoVG.nvgFontSize(this.ctx, size);
         NanoVG.nvgTextAlign(this.ctx, 17);
      }
   }

   public float text(String str, float x, float y, float size, int argb) {
      return this.text(str, x, y, size, argb, this.activeFont);
   }

   public float text(String str, float x, float y, float size, int argb, String font) {
      if (font == null) {
         return 0.0F;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         float var8;
         try {
            if (!font.equals(this.activeFont)) {
               NanoVG.nvgFontFace(this.ctx, font);
               NanoVG.nvgTextLetterSpacing(this.ctx, 0.0F);
               NanoVG.nvgFontSize(this.ctx, size);
               NanoVG.nvgTextAlign(this.ctx, 17);
            } else {
               this.applyFontStyle(size);
            }

            NanoVG.nvgFillColor(this.ctx, color(stack, argb));
            var8 = NanoVG.nvgText(this.ctx, x, y, str) - x;
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }

         return var8;
      }
   }

   public float textGradient(String str, float x, float y, float size, int top, int bottom) {
      if (this.activeFont == null) {
         return 0.0F;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         float var9;
         try {
            NVGPaint paint = NVGPaint.malloc(stack);
            NanoVG.nvgLinearGradient(this.ctx, x, y - size / 2.0F, x, y + size / 2.0F, color(stack, top), color(stack, bottom), paint);
            this.applyFontStyle(size);
            NanoVG.nvgFillPaint(this.ctx, paint);
            var9 = NanoVG.nvgText(this.ctx, x, y, str) - x;
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }

         return var9;
      }
   }

   public void textGlow(String str, float x, float y, float size, int argb) {
      if (this.activeFont != null) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            this.applyFontStyle(size);
            NanoVG.nvgFontBlur(this.ctx, 4.0F);
            NanoVG.nvgFillColor(this.ctx, color(stack, argb));
            NanoVG.nvgText(this.ctx, x, y, str);
            NanoVG.nvgFontBlur(this.ctx, 0.0F);
         } catch (Throwable var10) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   public float textTruncated(String str, float x, float y, float size, int argb, float maxWidth) {
      if (this.textWidth(str, size) <= maxWidth) {
         return this.text(str, x, y, size, argb);
      } else {
         String cut = str;

         while (cut.length() > 1 && this.textWidth(cut + "…", size) > maxWidth) {
            cut = cut.substring(0, cut.length() - 1);
         }

         return this.text(cut + "…", x, y, size, argb);
      }
   }

   public float textWidth(String str, float size) {
      if (this.activeFont == null) {
         return 0.0F;
      } else {
         this.applyFontStyle(size);
         return NanoVG.nvgTextBounds(this.ctx, 0.0F, 0.0F, str, (FloatBuffer)null);
      }
   }

   public void rotate(float radians) {
      NanoVG.nvgRotate(this.ctx, radians);
   }

   public void imagePattern(int image, float patternX, float patternY, float patternW, float patternH, float x, float y, float w, float h, float alphaMul) {
      if (image > 0) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            NVGPaint paint = NVGPaint.malloc(stack);
            NanoVG.nvgImagePattern(this.ctx, patternX, patternY, patternW, patternH, 0.0F, image, alphaMul, paint);
            NanoVG.nvgBeginPath(this.ctx);
            NanoVG.nvgRect(this.ctx, x, y, w, h);
            NanoVG.nvgFillPaint(this.ctx, paint);
            NanoVG.nvgFill(this.ctx);
         } catch (Throwable var15) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   public void image(int image, float x, float y, float w, float h, int tint) {
      if (image > 0) {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            NVGPaint paint = NVGPaint.malloc(stack);
            NanoVG.nvgImagePattern(this.ctx, x, y, w, h, 0.0F, image, 1.0F, paint);
            paint.innerColor(color(stack, tint));
            NanoVG.nvgBeginPath(this.ctx);
            NanoVG.nvgRect(this.ctx, x, y, w, h);
            NanoVG.nvgFillPaint(this.ctx, paint);
            NanoVG.nvgFill(this.ctx);
         } catch (Throwable var11) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var11.addSuppressed(var10);
               }
            }

            throw var11;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   private static NVGColor color(MemoryStack stack, int argb) {
      return NanoVG.nvgRGBA((byte)Colors.red(argb), (byte)Colors.green(argb), (byte)Colors.blue(argb), (byte)Colors.alpha(argb), NVGColor.malloc(stack));
   }
}
