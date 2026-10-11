package com.larpclient;

import com.larpclient.config.ConfigManager;
import com.larpclient.config.ConfigStore;
import com.larpclient.gui.ClickGuiScreen;
import com.larpclient.hud.HudManager;
import com.larpclient.license.LicenseManager;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.module.ModuleManager;
import com.larpclient.module.impl.FreecamModule;
import com.larpclient.notification.NotificationManager;
import com.larpclient.render.MotionBlurRenderer;
import com.larpclient.render.OverlayRenderer;
import com.larpclient.render.SusChunkRenderer;
import com.larpclient.spotify.SpotifyService;
import com.larpclient.suschunk.ServerLightCache;
import com.larpclient.theme.SoundSettings;
import com.larpclient.theme.ThemeManager;
import com.larpclient.util.UiSoundEvents;
import com.larpclient.util.UiSounds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.ClientStopping;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents.AfterInit;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_442;
import net.minecraft.class_304.class_11900;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LarpClient implements ClientModInitializer {
   public static final String MOD_ID = "larpclient";
   public static final String NAME = "LARP Client";
   public static final String VERSION = "2.2.0";
   public static final Logger LOGGER = LoggerFactory.getLogger("LARP Client");
   private static ModuleManager modules;
   private static LicenseManager license;
   private static ThemeManager themes;
   private static ConfigManager config;
   private static ConfigStore configStore;
   private static HudManager hud;
   private static NotificationManager notifications;
   private static SpotifyService spotify;
   private static SoundSettings soundSettings;
   private static boolean startupSoundPlayed;
   private static class_304 openGuiKey;
   private static class_304 panicKey;
   private static boolean panicActive;

   public static LicenseManager license() {
      return license;
   }

   public static ModuleManager modules() {
      return modules;
   }

   public static ThemeManager themes() {
      return themes;
   }

   public static ConfigManager config() {
      return config;
   }

   public static ConfigStore configStore() {
      return configStore;
   }

   public static HudManager hud() {
      return hud;
   }

   public static SpotifyService spotify() {
      return spotify;
   }

   public static SoundSettings sounds() {
      return soundSettings;
   }

   public static NotificationManager notifications() {
      return notifications;
   }

   public void onInitializeClient() {
      LOGGER.info("{} {} initializing", "LARP Client", "2.2.0");
      UiSoundEvents.bootstrap();
      openGuiKey = KeyBindingHelper.registerKeyBinding(new class_304("key.larpclient.open_gui", 344, class_11900.field_62556));
      panicKey = KeyBindingHelper.registerKeyBinding(new class_304("key.larpclient.panic", 297, class_11900.field_62556));
      themes = new ThemeManager();
      license = new LicenseManager();
      license.validateSaved();
      soundSettings = new SoundSettings();
      modules = new ModuleManager();
      spotify = new SpotifyService();
      notifications = new NotificationManager(themes, modules.hud);
      hud = new HudManager(modules, themes, spotify, notifications);
      config = new ConfigManager(modules, themes);
      config.addSection("hud", hud::toJson, hud::fromJson);
      config.addSection("panels", ClickGuiScreen.state()::toJson, ClickGuiScreen.state()::fromJson);
      config.addSection("sounds", soundSettings::toJson, soundSettings::fromJson);
      config.load();
      configStore = new ConfigStore(config);
      configStore.loadAll();
      UiSounds.init(soundSettings);
      modules.setOpenGuiAction(() -> {
         if (!panicActive) {
            class_310.method_1551().method_1507(new ClickGuiScreen());
         }
      });
      modules.setToggleListener((module, enabled) -> {
         if (!ConfigStore.applying) {
            if (module.getCategory() != Category.CLIENT) {
               if (class_310.method_1551().field_1687 != null) {
                  if (modules.hud.notifications.get()) {
                     notifications.push(module.getName(), enabled);
                     UiSounds.notification(enabled);
                  }
               }
            }
         }
      });
      OverlayRenderer.init(hud, notifications);
      ScreenEvents.AFTER_INIT.register((AfterInit)(client, screen, scaledWidth, scaledHeight) -> {
         if (screen instanceof class_442 && !startupSoundPlayed) {
            startupSoundPlayed = true;
            UiSounds.playStartup();
         }
      });
      spotify.start();
      ClientPlayConnectionEvents.JOIN.register((Join)(handler, sender, client) -> clearSusState());
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> clearSusState());
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> {
         modules.onTick();

         while (panicKey != null && panicKey.method_1436()) {
            togglePanic();
         }

         while (openGuiKey != null && openGuiKey.method_1436()) {
            if (!panicActive && !(client.field_1755 instanceof ClickGuiScreen)) {
               client.method_1507(new ClickGuiScreen(client.field_1755));
            }
         }
      });
      ClientTickEvents.START_CLIENT_TICK.register((StartTick)client -> {
         FreecamModule f = modules.freecam;
         if (f != null && f.isActive()) {
            FreecamModule.reapplyBodyInput(client);
         }

         modules.onCombatTick();
      });
      ClientLifecycleEvents.CLIENT_STOPPING.register((ClientStopping)client -> {
         config.save();
         spotify.stop();
      });
   }

   public static boolean isPanicActive() {
      return panicActive;
   }

   public static void togglePanic() {
      panicActive = !panicActive;
      class_310 client = class_310.method_1551();
      if (panicActive) {
         if (modules != null) {
            for (Module module : modules.all()) {
               if (module != null && module.isEnabled()) {
                  module.setEnabled(false);
               }
            }
         }

         if (client != null && client.field_1755 instanceof ClickGuiScreen) {
            client.method_1507(null);
         }

         LOGGER.info("LARP panic enabled - modules disabled and GUI locked until F8 is pressed again");
      } else {
         LOGGER.info("LARP panic disabled - GUI is available again");
      }
   }

   private static void clearSusState() {
      ServerLightCache.get().clear();
      modules.susChunkFinder.scanner.clear();
      SusChunkRenderer.reset();
      if (modules.chunkFinder != null) {
         modules.chunkFinder.clear();
      }

      if (modules.blockEntityEsp != null) {
         modules.blockEntityEsp.clear();
      }

      if (modules.jumpCircles != null) {
         modules.jumpCircles.clear();
      }

      if (modules.hitParticles != null) {
         modules.hitParticles.clear();
      }

      if (modules.customAccessories != null) {
         modules.customAccessories.clear();
      }

      MotionBlurRenderer.reset();
   }
}
