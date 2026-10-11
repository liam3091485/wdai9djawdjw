package com.larpclient.module;

import com.larpclient.module.impl.AnchorMacroModule;
import com.larpclient.module.impl.AntiAfkModule;
import com.larpclient.module.impl.ArmorTrimHiderModule;
import com.larpclient.module.impl.AutoClickerModule;
import com.larpclient.module.impl.AutoCrystalModule;
import com.larpclient.module.impl.AutoInventoryTotemModule;
import com.larpclient.module.impl.AutoJumpModule;
import com.larpclient.module.impl.AutoMineModule;
import com.larpclient.module.impl.AutoSprintModule;
import com.larpclient.module.impl.AutoTotemModule;
import com.larpclient.module.impl.AutoTpaModule;
import com.larpclient.module.impl.AutoWalkModule;
import com.larpclient.module.impl.BlockEntityEspModule;
import com.larpclient.module.impl.BlockEspModule;
import com.larpclient.module.impl.BluePulseModule;
import com.larpclient.module.impl.BlueVisualsModule;
import com.larpclient.module.impl.BreakReminderModule;
import com.larpclient.module.impl.ChatMacroModule;
import com.larpclient.module.impl.ChunkFinderModule;
import com.larpclient.module.impl.CleanHudModule;
import com.larpclient.module.impl.CoordSnapperModule;
import com.larpclient.module.impl.CoordinateReminderModule;
import com.larpclient.module.impl.CopyCoordsModule;
import com.larpclient.module.impl.CustomAccessoriesModule;
import com.larpclient.module.impl.CustomCrosshairModule;
import com.larpclient.module.impl.CustomFovModule;
import com.larpclient.module.impl.DeathCoordsModule;
import com.larpclient.module.impl.DebugHoleEspModule;
import com.larpclient.module.impl.DoubleAnchorModule;
import com.larpclient.module.impl.DurabilityAlertModule;
import com.larpclient.module.impl.ElytraSwapModule;
import com.larpclient.module.impl.EmptySlotModule;
import com.larpclient.module.impl.FakeStatsModule;
import com.larpclient.module.impl.FastUseModule;
import com.larpclient.module.impl.FirstFilledSlotModule;
import com.larpclient.module.impl.FreeLookModule;
import com.larpclient.module.impl.FreecamModule;
import com.larpclient.module.impl.FullHealthAlertModule;
import com.larpclient.module.impl.FullbrightModule;
import com.larpclient.module.impl.HealthDropAlertModule;
import com.larpclient.module.impl.HealthRecoveryAlertModule;
import com.larpclient.module.impl.HighAltitudeAlertModule;
import com.larpclient.module.impl.HitBoxModule;
import com.larpclient.module.impl.HitParticlesModule;
import com.larpclient.module.impl.HoldUseModule;
import com.larpclient.module.impl.HotbarLockModule;
import com.larpclient.module.impl.HoverTotemModule;
import com.larpclient.module.impl.IdleSneakModule;
import com.larpclient.module.impl.InventoryFullAlertModule;
import com.larpclient.module.impl.ItemSaverModule;
import com.larpclient.module.impl.JumpCirclesModule;
import com.larpclient.module.impl.LicenseModule;
import com.larpclient.module.impl.LowAltitudeAlertModule;
import com.larpclient.module.impl.LowHealthAlertModule;
import com.larpclient.module.impl.LowHealthBackpedalModule;
import com.larpclient.module.impl.LowHealthSlotModule;
import com.larpclient.module.impl.LowHealthSneakModule;
import com.larpclient.module.impl.LowHealthStopForwardModule;
import com.larpclient.module.impl.LowHealthStopSprintModule;
import com.larpclient.module.impl.MaceBomberModule;
import com.larpclient.module.impl.MaceSwapModule;
import com.larpclient.module.impl.MenuInputGuardModule;
import com.larpclient.module.impl.MobEspModule;
import com.larpclient.module.impl.MotionBlurModule;
import com.larpclient.module.impl.NameProtectModule;
import com.larpclient.module.impl.NameTagsModule;
import com.larpclient.module.impl.NextSlotModule;
import com.larpclient.module.impl.PanicSlotModule;
import com.larpclient.module.impl.PlayerEspModule;
import com.larpclient.module.impl.PreviousSlotModule;
import com.larpclient.module.impl.RegionMapModule;
import com.larpclient.module.impl.ShieldBreakerModule;
import com.larpclient.module.impl.SkinProtectModule;
import com.larpclient.module.impl.SlotReminderModule;
import com.larpclient.module.impl.SpawnerNametagsModule;
import com.larpclient.module.impl.SpawnerProtectModule;
import com.larpclient.module.impl.SprintJumpModule;
import com.larpclient.module.impl.StaffListModule;
import com.larpclient.module.impl.StorageEspModule;
import com.larpclient.module.impl.SwingSpeedModule;
import com.larpclient.module.impl.ToggleSneakModule;
import com.larpclient.module.impl.TotemCounterModule;
import com.larpclient.module.impl.TriggerbotModule;
import com.larpclient.module.impl.UseSneakModule;
import com.larpclient.module.impl.WeatherNotifierModule;
import com.larpclient.module.impl.ZoomModule;
import com.larpclient.settings.BooleanSetting;
import com.larpclient.settings.ModeSetting;
import com.larpclient.settings.Setting;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ModuleManager {
   private final List<Module> modules = new ArrayList<>();
   private final Map<Category, List<Module>> byCategory = new LinkedHashMap<>();
   private final List<Module> activeNonCombat = new ArrayList<>();
   private final List<Module> activeCombat = new ArrayList<>();
   public final Modules.ClickGuiModule clickGui;
   public final Modules.HudModule hud;
   public final Modules.SpotifyModule spotify;
   public final Modules.BlockOutlineModule blockOutline;
   public final Modules.SusChunkFinderModule susChunkFinder;
   public FullbrightModule fullbright;
   public AutoWalkModule autoWalk;
   public AutoSprintModule autoSprint;
   public AutoJumpModule autoJump;
   public AutoMineModule autoMine;
   public SprintJumpModule sprintJump;
   public LowHealthSneakModule lowHealthSneak;
   public HotbarLockModule hotbarLock;
   public ItemSaverModule itemSaver;
   public LowHealthSlotModule lowHealthSlot;
   public UseSneakModule useSneak;
   public PanicSlotModule panicSlot;
   public IdleSneakModule idleSneak;
   public LowHealthBackpedalModule lowHealthBackpedal;
   public SlotReminderModule slotReminder;
   public HealthRecoveryAlertModule healthRecoveryAlert;
   public MenuInputGuardModule menuInputGuard;
   public ToggleSneakModule toggleSneak;
   public HoldUseModule holdUse;
   public AntiAfkModule antiAfk;
   public WeatherNotifierModule weatherNotifier;
   public SwingSpeedModule swingSpeed;
   public StorageEspModule storageEsp;
   public BlockEspModule blockEsp;
   public AutoTotemModule autoTotem;
   public MaceSwapModule maceSwap;
   public AnchorMacroModule anchorMacro;
   public AutoCrystalModule autoCrystal;
   public HitBoxModule hitBox;
   public ElytraSwapModule elytraSwap;
   public HoverTotemModule hoverTotem;
   public ShieldBreakerModule shieldBreaker;
   public TriggerbotModule triggerbot;
   public DoubleAnchorModule doubleAnchor;
   public MaceBomberModule maceBomber;
   public NameProtectModule nameProtect;
   public SkinProtectModule skinProtect;
   public NameTagsModule nameTags;
   public FastUseModule fastUse;
   public FakeStatsModule fakeStats;
   public AutoInventoryTotemModule autoInventoryTotem;
   public PlayerEspModule playerEsp;
   public MobEspModule mobEsp;
   public BlockEntityEspModule blockEntityEsp;
   public SpawnerNametagsModule spawnerNametags;
   public DebugHoleEspModule debugHoleEsp;
   public FreecamModule freecam;
   public AutoTpaModule autoTpa;
   public JumpCirclesModule jumpCircles;
   public CustomCrosshairModule customCrosshair;
   public ZoomModule zoom;
   public CustomFovModule customFov;
   public HitParticlesModule hitParticles;
   public MotionBlurModule motionBlur;
   public CustomAccessoriesModule customAccessories;
   public ChunkFinderModule chunkFinder;
   public FreeLookModule freeLook;
   public AutoClickerModule autoClicker;
   public CoordSnapperModule coordSnapper;
   public RegionMapModule regionMap;
   public ChatMacroModule chatMacro;
   public StaffListModule staffList;
   public ArmorTrimHiderModule armorTrimHider;
   public SpawnerProtectModule spawnerProtect;
   public LowHealthAlertModule lowHealthAlert;
   public DurabilityAlertModule durabilityAlert;
   public InventoryFullAlertModule inventoryFullAlert;
   public TotemCounterModule totemCounter;
   public BluePulseModule bluePulse;
   public CleanHudModule cleanHud;
   public BlueVisualsModule blueVisuals;
   public LicenseModule licenseModule;
   public CopyCoordsModule copyCoords;
   public DeathCoordsModule deathCoords;
   public BreakReminderModule breakReminder;
   private Runnable openGuiAction = () -> {};
   private BiConsumer<Module, Boolean> toggleListener = (m, e) -> {};

   public ModuleManager() {
      for (Category category : Category.values()) {
         this.byCategory.put(category, new ArrayList<>());
      }

      this.licenseModule = new LicenseModule();
      this.register(this.licenseModule);
      this.susChunkFinder = new Modules.SusChunkFinderModule();
      this.registerPlaceholders();
      this.blockOutline = new Modules.BlockOutlineModule();
      this.register(this.blockOutline);
      this.hitParticles = new HitParticlesModule();
      this.register(this.hitParticles);
      this.customAccessories = new CustomAccessoriesModule();
      this.register(this.customAccessories);
      this.motionBlur = new MotionBlurModule();
      this.register(this.motionBlur);
      this.hud = new Modules.HudModule();
      this.register(this.hud);
      this.spotify = new Modules.SpotifyModule();
      this.register(this.spotify);
      this.chatMacro = new ChatMacroModule();
      this.register(this.chatMacro);
      this.swingSpeed = new SwingSpeedModule();
      this.register(this.swingSpeed);
      this.jumpCircles = new JumpCirclesModule();
      this.register(this.jumpCircles);
      this.clickGui = new Modules.ClickGuiModule();
      this.register(this.clickGui);
   }

   private void registerPlaceholders() {
      this.autoTotem = new AutoTotemModule();
      this.register(this.autoTotem);
      this.autoCrystal = new AutoCrystalModule();
      this.register(this.autoCrystal);
      this.anchorMacro = new AnchorMacroModule();
      this.register(this.anchorMacro);
      this.doubleAnchor = new DoubleAnchorModule();
      this.register(this.doubleAnchor);
      this.autoInventoryTotem = new AutoInventoryTotemModule();
      this.register(this.autoInventoryTotem);
      this.maceSwap = new MaceSwapModule();
      this.register(this.maceSwap);
      this.hitBox = new HitBoxModule();
      this.register(this.hitBox);
      this.elytraSwap = new ElytraSwapModule();
      this.register(this.elytraSwap);
      this.hoverTotem = new HoverTotemModule();
      this.register(this.hoverTotem);
      this.shieldBreaker = new ShieldBreakerModule();
      this.register(this.shieldBreaker);
      this.triggerbot = new TriggerbotModule();
      this.register(this.triggerbot);
      this.maceBomber = new MaceBomberModule();
      this.register(this.maceBomber);
      this.skinProtect = new SkinProtectModule();
      this.register(this.skinProtect);
      this.nameProtect = new NameProtectModule();
      this.register(this.nameProtect);
      this.freecam = new FreecamModule();
      this.register(this.freecam);
      this.autoTpa = new AutoTpaModule();
      this.register(this.autoTpa);
      this.autoClicker = new AutoClickerModule();
      this.register(this.autoClicker);
      this.fastUse = new FastUseModule();
      this.register(this.fastUse);
      this.fakeStats = new FakeStatsModule();
      this.register(this.fakeStats);
      this.nameTags = new NameTagsModule();
      this.register(this.nameTags);
      this.weatherNotifier = new WeatherNotifierModule();
      this.register(this.weatherNotifier);
      this.armorTrimHider = new ArmorTrimHiderModule();
      this.register(this.armorTrimHider);
      this.customCrosshair = new CustomCrosshairModule();
      this.ph(
         "Media/StaffNames/Icons",
         "Marks media & staff players with icons",
         Category.MISC,
         new BooleanSetting("Media", "Show media icons", true),
         new BooleanSetting("Staff", "Show staff icons", true),
         new ModeSetting(
            "Position",
            "Icon position",
            "Prefix",
            "Prefix",
            "Suffix"
         )
      );
      this.staffList = new StaffListModule();
      this.register(this.staffList);
      this.customFov = new CustomFovModule();
      this.register(this.customFov);
      this.coordSnapper = new CoordSnapperModule();
      this.register(this.coordSnapper);
      this.autoWalk = new AutoWalkModule();
      this.register(this.autoWalk);
      this.autoSprint = new AutoSprintModule();
      this.register(this.autoSprint);
      this.autoJump = new AutoJumpModule();
      this.register(this.autoJump);
      this.sprintJump = new SprintJumpModule();
      this.register(this.sprintJump);
      this.autoMine = new AutoMineModule();
      this.register(this.autoMine);
      this.lowHealthSneak = new LowHealthSneakModule();
      this.register(this.lowHealthSneak);
      this.hotbarLock = new HotbarLockModule();
      this.register(this.hotbarLock);
      this.itemSaver = new ItemSaverModule();
      this.register(this.itemSaver);
      this.lowHealthSlot = new LowHealthSlotModule();
      this.register(this.lowHealthSlot);
      this.useSneak = new UseSneakModule();
      this.register(this.useSneak);
      this.panicSlot = new PanicSlotModule();
      this.register(this.panicSlot);
      this.idleSneak = new IdleSneakModule();
      this.register(this.idleSneak);
      this.lowHealthBackpedal = new LowHealthBackpedalModule();
      this.register(this.lowHealthBackpedal);
      this.slotReminder = new SlotReminderModule();
      this.register(this.slotReminder);
      this.healthRecoveryAlert = new HealthRecoveryAlertModule();
      this.register(this.healthRecoveryAlert);
      this.menuInputGuard = new MenuInputGuardModule();
      this.register(this.menuInputGuard);
      this.register(new NextSlotModule());
      this.register(new PreviousSlotModule());
      this.register(new EmptySlotModule());
      this.register(new FirstFilledSlotModule());
      this.register(new HealthDropAlertModule());
      this.register(new FullHealthAlertModule());
      this.register(new LowHealthStopSprintModule());
      this.register(new LowHealthStopForwardModule());
      this.register(new HighAltitudeAlertModule());
      this.register(new LowAltitudeAlertModule());
      this.register(new CoordinateReminderModule());
      this.toggleSneak = new ToggleSneakModule();
      this.register(this.toggleSneak);
      this.holdUse = new HoldUseModule();
      this.register(this.holdUse);
      this.antiAfk = new AntiAfkModule();
      this.register(this.antiAfk);
      this.zoom = new ZoomModule();
      this.register(this.zoom);
      this.freeLook = new FreeLookModule();
      this.register(this.freeLook);
      this.spawnerProtect = new SpawnerProtectModule();
      this.register(this.spawnerProtect);
      this.blockEsp = new BlockEspModule();
      this.register(this.blockEsp);
      this.storageEsp = new StorageEspModule();
      this.register(this.storageEsp);
      this.blockEntityEsp = new BlockEntityEspModule();
      this.register(this.blockEntityEsp);
      this.debugHoleEsp = new DebugHoleEspModule();
      this.register(this.debugHoleEsp);
      this.fullbright = new FullbrightModule();
      this.register(this.fullbright);
      this.playerEsp = new PlayerEspModule();
      this.register(this.playerEsp);
      this.mobEsp = new MobEspModule();
      this.register(this.mobEsp);
      this.spawnerNametags = new SpawnerNametagsModule();
      this.register(this.spawnerNametags);
      this.register(this.susChunkFinder);
      this.chunkFinder = new ChunkFinderModule();
      this.register(this.chunkFinder);
      this.regionMap = new RegionMapModule();
      this.register(this.regionMap);
      this.lowHealthAlert = new LowHealthAlertModule();
      this.register(this.lowHealthAlert);
      this.durabilityAlert = new DurabilityAlertModule();
      this.register(this.durabilityAlert);
      this.inventoryFullAlert = new InventoryFullAlertModule();
      this.register(this.inventoryFullAlert);
      this.totemCounter = new TotemCounterModule();
      this.register(this.totemCounter);
      this.bluePulse = new BluePulseModule();
      this.register(this.bluePulse);
      this.cleanHud = new CleanHudModule();
      this.register(this.cleanHud);
      this.blueVisuals = new BlueVisualsModule();
      this.register(this.blueVisuals);
      this.copyCoords = new CopyCoordsModule();
      this.register(this.copyCoords);
      this.deathCoords = new DeathCoordsModule();
      this.register(this.deathCoords);
      this.breakReminder = new BreakReminderModule();
      this.register(this.breakReminder);
   }

   private void ph(String name, String description, Category category, Setting<?>... settings) {
      this.register(new Modules.Placeholder(name, description, category, settings));
   }

   public void register(Module module) {
      this.modules.add(module);
      this.byCategory.get(module.getCategory()).add(module);
      module.setToggleCallback((m, enabled) -> {
         this.updateActiveCache(m, enabled);
         this.notifyToggle(m, enabled);
      });
      if (module.isEnabled()) {
         this.updateActiveCache(module, true);
      }
   }

   private void updateActiveCache(Module module, boolean enabled) {
      List<Module> target = module.getCategory() == Category.COMBAT ? this.activeCombat : this.activeNonCombat;
      if (enabled) {
         if (!target.contains(module)) {
            target.add(module);
         }
      } else {
         target.remove(module);
      }
   }

   public List<Module> all() {
      return this.modules;
   }

   public List<Module> inCategory(Category category) {
      return this.byCategory.get(category);
   }

   public void setOpenGuiAction(Runnable action) {
      this.openGuiAction = action;
   }

   public void setToggleListener(BiConsumer<Module, Boolean> listener) {
      this.toggleListener = listener;
   }

   public void notifyToggle(Module module, boolean enabled) {
      this.toggleListener.accept(module, enabled);
   }

   public boolean onKeyPressed(int keyCode) {
      if (this.clickGui.getKeybind().matches(keyCode)) {
         this.openGuiAction.run();
         return true;
      } else {
         boolean handled = false;

         for (Module module : this.modules) {
            if (module != this.clickGui && module.getKeybind().matches(keyCode)) {
               module.toggle();
               handled = true;
            }
         }

         for (Module modulex : this.modules) {
            if (modulex.isEnabled() && modulex.onKeyPress(keyCode)) {
               handled = true;
            }
         }

         return handled;
      }
   }

   public void onTick() {
      int i = 0;

      for (int size = this.activeNonCombat.size(); i < size; i++) {
         this.activeNonCombat.get(i).onTick();
      }
   }

   public void onCombatTick() {
      int i = 0;

      for (int size = this.activeCombat.size(); i < size; i++) {
         this.activeCombat.get(i).onTick();
      }
   }
}
