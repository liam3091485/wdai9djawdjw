package com.larpclient.suschunk;

import com.larpclient.LarpClient;
import com.larpclient.module.Modules;
import com.larpclient.util.UiSounds;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_310;
import net.minecraft.class_4076;
import net.minecraft.class_2338.class_2339;

public class SusChunkScanner {
   public static final int POINTS_PER_SENSITIVITY = 5;
   private static final int GEODE_LINK_DISTANCE = 5;
   private static final boolean DEBUG_LOG = Boolean.getBoolean("sixseven.sus.debug");
   private final Modules.SusChunkFinderModule module;
   private final Map<Long, SusChunkScanner.ChunkScore> scores = new ConcurrentHashMap<>();
   private final Deque<Long> queue = new ArrayDeque<>();
   private final Set<Long> lightRescans = ConcurrentHashMap.newKeySet();
   private final Set<Long> alertedChunks = new HashSet<>();
   private volatile List<SusChunkScanner.Flag> flags = List.of();
   private volatile List<SusChunkScanner.Zone> zones = List.of();
   private class_1923 lastQueueCenter;
   private int tickCounter;

   public SusChunkScanner(Modules.SusChunkFinderModule module) {
      this.module = module;
      ServerLightCache.get().addDirtyListener(this.lightRescans::add);
   }

   public List<SusChunkScanner.Flag> flags() {
      return this.flags;
   }

   public List<SusChunkScanner.Zone> zones() {
      return this.zones;
   }

   public int threshold() {
      return this.module.sensitivity.getInt() * 5;
   }

   public void clear() {
      this.scores.clear();
      this.queue.clear();
      this.lightRescans.clear();
      this.alertedChunks.clear();
      this.flags = List.of();
      this.zones = List.of();
      this.lastQueueCenter = null;
   }

   public void tick() {
      class_310 mc = class_310.method_1551();
      if (mc.field_1687 != null && mc.field_1724 != null) {
         try {
            this.refillQueueIfNeeded(mc);
            int budget = this.module.scanSpeed.getInt();
            long deadline = System.nanoTime() + 2000000L;
            int scanned = this.scanLightRescans(mc, budget, deadline);
            this.scanQueue(mc, budget - scanned, deadline);
            if (++this.tickCounter % 10 == 0) {
               this.rebuild(mc);
            }
         } catch (Exception var6) {
            LarpClient.LOGGER.warn("SusChunkFinder scan error: {}", var6.toString());
         }
      }
   }

   private int scanLightRescans(class_310 mc, int budget, long deadline) {
      if (this.lightRescans.isEmpty()) {
         return 0;
      } else {
         int scanned = 0;
         Iterator<Long> iterator = this.lightRescans.iterator();

         while (iterator.hasNext() && scanned < budget && System.nanoTime() < deadline) {
            long key = iterator.next();
            iterator.remove();
            class_2818 chunk = mc.field_1687.method_2935().method_12126(class_1923.method_8325(key), class_1923.method_8332(key), false);
            if (chunk != null && this.scores.containsKey(key)) {
               this.scores.put(key, this.scanChunk(mc, chunk));
               scanned++;
            }
         }

         return scanned;
      }
   }

   private void scanQueue(class_310 mc, int budget, long deadline) {
      int scanned = 0;

      for (int polled = 0; scanned < budget && polled < 128 && !this.queue.isEmpty() && System.nanoTime() < deadline; polled++) {
         long key = this.queue.pollFirst();
         class_2818 chunk;
         if (!this.scores.containsKey(key)
            && (chunk = mc.field_1687.method_2935().method_12126(class_1923.method_8325(key), class_1923.method_8332(key), false)) != null) {
            this.scores.put(key, this.scanChunk(mc, chunk));
            scanned++;
         }
      }
   }

   private void refillQueueIfNeeded(class_310 mc) {
      class_1923 center = mc.field_1724.method_31476();
      if (this.queue.isEmpty()
         || this.lastQueueCenter == null
         || Math.max(Math.abs(center.field_9181 - this.lastQueueCenter.field_9181), Math.abs(center.field_9180 - this.lastQueueCenter.field_9180)) >= 3) {
         this.lastQueueCenter = center;
         this.queue.clear();
         int radius = Math.min((Integer)mc.field_1690.method_42503().method_41753() + 1, 16);
         ArrayList<Long> order = new ArrayList<>();

         for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
               long key = class_1923.method_8331(center.field_9181 + dx, center.field_9180 + dz);
               if (!this.scores.containsKey(key)) {
                  order.add(key);
               }
            }
         }

         order.sort(Comparator.comparingDouble(k -> Math.hypot(class_1923.method_8325(k) - center.field_9181, class_1923.method_8332(k) - center.field_9180)));
         this.queue.addAll(order);
      }
   }

   private static boolean isPlantTarget(class_2680 state) {
      class_2248 block = state.method_26204();
      return block == class_2246.field_9993
         || block == class_2246.field_10463
         || block == class_2246.field_10211
         || block == class_2246.field_16999
         || block == class_2246.field_10597
         || block == class_2246.field_28048;
   }

   private static boolean isAmethystStructure(class_2680 state) {
      return state.method_27852(class_2246.field_27160) || state.method_27852(class_2246.field_27159);
   }

   private SusChunkScanner.ChunkScore scanChunk(class_310 mc, class_2818 chunk) {
      SusChunkScanner.ChunkScore result = new SusChunkScanner.ChunkScore(chunk.method_12004().method_8324());
      if (this.module.amethyst.get()) {
         this.detectAmethyst(mc, chunk, result);
      }

      this.detectGrowth(mc, chunk, result);
      if (this.module.rotatedDeepslate.get()) {
         this.detectRotatedDeepslate(chunk, result);
      }

      result.computeScore();
      if (DEBUG_LOG && result.score > 0.0) {
         LarpClient.LOGGER.info("[sus] chunk {} score={} hits={}", new Object[]{chunk.method_12004(), result.score, result.hits});
      }

      return result;
   }

   private void detectAmethyst(class_310 mc, class_2818 chunk, SusChunkScanner.ChunkScore result) {
      List<class_2338> cells = ServerLightCache.get().light5Positions(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
      if (!cells.isEmpty()) {
         class_2339 cursor = new class_2339();

         for (class_2338 cell : cells) {
            class_2680 state = mc.field_1687.method_8320(cell);
            if ((state.method_26215() || state.method_27852(class_2246.field_27161)) && hasAmethystNeighbour(mc, cell, cursor)) {
               result.amethystCells.add(cell.method_10062());
               if (DEBUG_LOG) {
                  LarpClient.LOGGER
                     .info(
                        "[sus] AMETHYST light-5 cell @{} ({})",
                        cell,
                        state.method_26215() ? "hidden" : "visible"
                     );
               }
            }
         }
      }
   }

   private static boolean hasAmethystNeighbour(class_310 mc, class_2338 cell, class_2339 cursor) {
      for (int dx = -1; dx <= 1; dx++) {
         for (int dy = -1; dy <= 1; dy++) {
            for (int dz = -1; dz <= 1; dz++) {
               if (dx != 0 || dy != 0 || dz != 0) {
                  cursor.method_10103(cell.method_10263() + dx, cell.method_10264() + dy, cell.method_10260() + dz);
                  if (isAmethystStructure(mc.field_1687.method_8320(cursor))) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void detectGrowth(class_310 mc, class_2818 chunk, SusChunkScanner.ChunkScore result) {
      class_2826[] sections = chunk.method_12006();
      int minSectionY = chunk.method_32891();
      int loSection = class_4076.method_18675(-12);
      int hiSection = class_4076.method_18675(80);
      int baseX = chunk.method_12004().method_8326();
      int baseZ = chunk.method_12004().method_8328();
      class_2339 cursor = new class_2339();

      for (int s = 0; s < sections.length; s++) {
         int sectionY = minSectionY + s;
         class_2826 section;
         if (sectionY >= loSection
            && sectionY <= hiSection
            && !(section = sections[s]).method_38292()
            && section.method_12265().method_19526(SusChunkScanner::isPlantTarget)) {
            int baseY = sectionY << 4;

            for (int y = 0; y < 16; y++) {
               int worldY = baseY + y;
               if (worldY >= -12 && worldY <= 80) {
                  for (int z = 0; z < 16; z++) {
                     for (int x = 0; x < 16; x++) {
                        class_2680 state = section.method_12254(x, y, z);
                        if (isPlantTarget(state)) {
                           cursor.method_10103(baseX + x, worldY, baseZ + z);
                           this.inspectPlant(mc, state, cursor, result);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isRotatedDeepslate(class_2680 state) {
      String text = state.toString().toLowerCase(Locale.ROOT);
      return text.contains("minecraft:deepslate") && (text.contains("axis=x") || text.contains("axis=z"));
   }

   private void detectRotatedDeepslate(class_2818 chunk, SusChunkScanner.ChunkScore result) {
      class_2826[] sections = chunk.method_12006();
      int minSectionY = chunk.method_32891();
      int baseX = chunk.method_12004().method_8326();
      int baseZ = chunk.method_12004().method_8328();
      class_2339 cursor = new class_2339();

      for (int s = 0; s < sections.length; s++) {
         int sectionY = minSectionY + s;
         int baseY = sectionY << 4;
         if (baseY <= 32) {
            class_2826 section = sections[s];
            if (!section.method_38292() && section.method_12265().method_19526(SusChunkScanner::isRotatedDeepslate)) {
               for (int y = 0; y < 16; y++) {
                  for (int z = 0; z < 16; z++) {
                     for (int x = 0; x < 16; x++) {
                        class_2680 state = section.method_12254(x, y, z);
                        if (isRotatedDeepslate(state)) {
                           cursor.method_10103(baseX + x, baseY + y, baseZ + z);
                           result.add(SusChunkScanner.SignalType.ROTATED_DEEPSLATE, cursor);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void inspectPlant(class_310 mc, class_2680 state, class_2338 pos, SusChunkScanner.ChunkScore result) {
      class_2248 block = state.method_26204();
      if ((block == class_2246.field_9993 || block == class_2246.field_10463) && this.module.kelp.get()) {
         class_2680 above = mc.field_1687.method_8320(pos.method_10084());
         if (above.method_27852(class_2246.field_9993) || above.method_27852(class_2246.field_10463)) {
            return;
         }

         int height = columnLength(mc, pos, class_2350.field_11033, class_2246.field_9993, class_2246.field_10463);
         boolean maxAge = block == class_2246.field_9993
            && state.method_28498(class_2741.field_12517)
            && (Integer)state.method_11654(class_2741.field_12517) == 25;
         if (maxAge && height >= 8 || height >= 14) {
            result.add(SusChunkScanner.SignalType.KELP, pos);
            if (DEBUG_LOG) {
               LarpClient.LOGGER.info("[sus] KELP @{} height={} maxAge={}", new Object[]{pos, height, maxAge});
            }
         }
      } else if (block == class_2246.field_10211 && this.module.bamboo.get()) {
         if (mc.field_1687.method_8320(pos.method_10074()).method_27852(class_2246.field_10211)) {
            return;
         }

         int height = columnLength(mc, pos, class_2350.field_11036, class_2246.field_10211);
         if (height >= 12) {
            result.add(SusChunkScanner.SignalType.BAMBOO, pos);
            if (DEBUG_LOG) {
               LarpClient.LOGGER.info("[sus] BAMBOO @{} height={}", pos, height);
            }
         }
      } else if (block == class_2246.field_16999 && this.module.berries.get()) {
         if (state.method_28498(class_2741.field_12497) && (Integer)state.method_11654(class_2741.field_12497) == 3) {
            result.add(SusChunkScanner.SignalType.BERRIES, pos);
            if (DEBUG_LOG) {
               LarpClient.LOGGER.info("[sus] BERRIES max age @{}", pos);
            }
         }
      } else if (block == class_2246.field_10597 && this.module.vines.get()) {
         if (mc.field_1687.method_8320(pos.method_10084()).method_27852(class_2246.field_10597)) {
            return;
         }

         int length = columnLength(mc, pos, class_2350.field_11033, class_2246.field_10597);
         if (length >= 7) {
            result.add(SusChunkScanner.SignalType.VINES, pos);
            if (DEBUG_LOG) {
               LarpClient.LOGGER.info("[sus] VINES @{} hang={}", pos, length);
            }
         }
      } else if (block == class_2246.field_28048 && this.module.dripstone.get()) {
         if (mc.field_1687.method_8320(pos.method_10084()).method_27852(class_2246.field_28048)
            || !mc.field_1687.method_8320(pos.method_10074()).method_27852(class_2246.field_28048)) {
            return;
         }

         int length = columnLength(mc, pos, class_2350.field_11033, class_2246.field_28048);
         if (length >= 5) {
            result.add(SusChunkScanner.SignalType.DRIPSTONE, pos);
            if (DEBUG_LOG) {
               LarpClient.LOGGER.info("[sus] DRIPSTONE @{} length={}", pos, length);
            }
         }
      }
   }

   private static int columnLength(class_310 mc, class_2338 start, class_2350 dir, class_2248... blocks) {
      class_2339 cursor = start.method_25503();

      int length;
      for (length = 1; length < 40; length++) {
         cursor.method_10098(dir);
         class_2680 state = mc.field_1687.method_8320(cursor);

         for (class_2248 block : blocks) {
            if (state.method_27852(block)) {
               break;
            }
         }
      }

      return length;
   }

   private void rebuild(class_310 mc) {
      this.scores.keySet().removeIf(key -> mc.field_1687.method_2935().method_12126(class_1923.method_8325(key), class_1923.method_8332(key), false) == null);
      int threshold = this.threshold();
      HashMap<Long, SusChunkScanner.FlagAggregate> aggregates = new HashMap<>();

      for (SusChunkScanner.ChunkScore score : this.scores.values()) {
         if (score.score >= threshold) {
            SusChunkScanner.FlagAggregate agg = aggregates.computeIfAbsent(score.chunkKey, SusChunkScanner.FlagAggregate::new);
            agg.score = Math.max(agg.score, score.score);
            agg.hitWeight = agg.hitWeight + score.hitWeight;
            agg.hitX = agg.hitX + score.hitX;
            agg.hitZ = agg.hitZ + score.hitZ;
         }
      }

      for (SusChunkScanner.Geode geode : this.clusterGeodes()) {
         if (!(geode.score() < threshold)) {
            for (long chunkKey : geode.chunks()) {
               SusChunkScanner.FlagAggregate agg = aggregates.computeIfAbsent(chunkKey, SusChunkScanner.FlagAggregate::new);
               agg.score = Math.max(agg.score, geode.score());
            }

            double weight = SusChunkScanner.SignalType.AMETHYST.weight;

            for (class_2338 cell : geode.cells()) {
               SusChunkScanner.FlagAggregate agg = aggregates.get(class_1923.method_8331(cell.method_10263() >> 4, cell.method_10260() >> 4));
               if (agg != null) {
                  agg.hitWeight += weight;
                  agg.hitX = agg.hitX + (cell.method_10263() + 0.5) * weight;
                  agg.hitZ = agg.hitZ + (cell.method_10260() + 0.5) * weight;
               }
            }

            if (DEBUG_LOG) {
               LarpClient.LOGGER
                  .info(
                     "[sus] GEODE {} cells / {} chunks, score {} -> FLAG",
                     new Object[]{geode.cells().size(), geode.chunks().size(), geode.score()}
                  );
            }
         }
      }

      ArrayList<SusChunkScanner.Flag> flagged = new ArrayList<>();

      for (SusChunkScanner.FlagAggregate agg : aggregates.values()) {
         flagged.add(new SusChunkScanner.Flag(agg.chunkKey, agg.score));
      }

      this.flags = List.copyOf(flagged);
      this.zones = this.buildZones(flagged, aggregates);
      this.fireAlerts(mc);
   }

   private List<SusChunkScanner.Geode> clusterGeodes() {
      ArrayList<class_2338> cells = new ArrayList<>();

      for (SusChunkScanner.ChunkScore score : this.scores.values()) {
         cells.addAll(score.amethystCells);
      }

      int n = cells.size();
      if (n == 0) {
         return List.of();
      } else {
         int[] parent = new int[n];
         int i = 0;

         while (i < n) {
            parent[i] = i++;
         }

         for (int var16 = 0; var16 < n; var16++) {
            class_2338 a = cells.get(var16);

            for (int j = var16 + 1; j < n; j++) {
               class_2338 b = cells.get(j);
               int chebyshev = Math.max(
                  Math.abs(a.method_10263() - b.method_10263()),
                  Math.max(Math.abs(a.method_10264() - b.method_10264()), Math.abs(a.method_10260() - b.method_10260()))
               );
               if (chebyshev <= 5) {
                  parent[find(parent, var16)] = find(parent, j);
               }
            }
         }

         HashMap<Integer, List<class_2338>> groups = new HashMap<>();

         for (int i2 = 0; i2 < n; i2++) {
            groups.computeIfAbsent(find(parent, i2), k -> new ArrayList<>()).add(cells.get(i2));
         }

         ArrayList<SusChunkScanner.Geode> geodes = new ArrayList<>();

         for (List<class_2338> group : groups.values()) {
            HashSet<Long> chunks = new HashSet<>();
            double sx = 0.0;
            double sz = 0.0;

            for (class_2338 c : group) {
               chunks.add(class_1923.method_8331(c.method_10263() >> 4, c.method_10260() >> 4));
               sx += c.method_10263() + 0.5;
               sz += c.method_10260() + 0.5;
            }

            double score = SusChunkScanner.SignalType.AMETHYST.weight * Math.min(group.size(), SusChunkScanner.SignalType.AMETHYST.cap);
            geodes.add(new SusChunkScanner.Geode(List.copyOf(group), Set.copyOf(chunks), score, sx / group.size(), sz / group.size()));
         }

         return geodes;
      }
   }

   private static int find(int[] parent, int i) {
      while (parent[i] != i) {
         parent[i] = parent[parent[i]];
         i = parent[i];
      }

      return i;
   }

   private List<SusChunkScanner.Zone> buildZones(List<SusChunkScanner.Flag> flagged, Map<Long, SusChunkScanner.FlagAggregate> aggregates) {
      int radius = Math.max(1, this.module.mergeRadius.getInt());
      HashMap<Long, SusChunkScanner.Flag> byKey = new HashMap<>();

      for (SusChunkScanner.Flag flag : flagged) {
         byKey.put(flag.chunkKey(), flag);
      }

      ArrayList<SusChunkScanner.Zone> built = new ArrayList<>();
      HashSet<Long> visited = new HashSet<>();

      for (SusChunkScanner.Flag seed : flagged) {
         if (visited.add(seed.chunkKey())) {
            ArrayList<SusChunkScanner.Flag> group = new ArrayList<>();
            ArrayDeque<SusChunkScanner.Flag> frontier = new ArrayDeque<>(List.of(seed));

            while (!frontier.isEmpty()) {
               SusChunkScanner.Flag current = frontier.poll();
               group.add(current);
               int cx = class_1923.method_8325(current.chunkKey());
               int cz = class_1923.method_8332(current.chunkKey());

               for (int dx = -radius; dx <= radius; dx++) {
                  for (int dz = -radius; dz <= radius; dz++) {
                     SusChunkScanner.Flag neighbour;
                     if ((dx != 0 || dz != 0) && (neighbour = byKey.get(class_1923.method_8331(cx + dx, cz + dz))) != null && visited.add(neighbour.chunkKey())
                        )
                      {
                        frontier.add(neighbour);
                     }
                  }
               }
            }

            built.add(this.makeZone(group, aggregates));
         }
      }

      built.sort(Comparator.comparingDouble(SusChunkScanner.Zone::totalScore).reversed());
      return built;
   }

   private SusChunkScanner.Zone makeZone(List<SusChunkScanner.Flag> group, Map<Long, SusChunkScanner.FlagAggregate> aggregates) {
      HashSet<Long> members = new HashSet<>();
      double totalScore = 0.0;
      double maxScore = 0.0;
      double hitWeight = 0.0;
      double hitX = 0.0;
      double hitZ = 0.0;
      double chunkX = 0.0;
      double chunkZ = 0.0;

      for (SusChunkScanner.Flag flag : group) {
         members.add(flag.chunkKey());
         totalScore += flag.score();
         maxScore = Math.max(maxScore, flag.score());
         SusChunkScanner.FlagAggregate agg = aggregates.get(flag.chunkKey());
         if (agg != null && agg.hitWeight > 0.0) {
            hitWeight += agg.hitWeight;
            hitX += agg.hitX;
            hitZ += agg.hitZ;
         }

         chunkX += (class_1923.method_8325(flag.chunkKey()) * 16 + 8) * flag.score();
         chunkZ += (class_1923.method_8332(flag.chunkKey()) * 16 + 8) * flag.score();
      }

      double cx = hitWeight > 0.0 ? hitX / hitWeight : chunkX / totalScore;
      double cz = hitWeight > 0.0 ? hitZ / hitWeight : chunkZ / totalScore;
      return new SusChunkScanner.Zone(Set.copyOf(members), cx, cz, totalScore, maxScore);
   }

   private void fireAlerts(class_310 mc) {
      for (SusChunkScanner.Zone zone : this.zones) {
         boolean isNew = true;

         for (long member : zone.members()) {
            if (this.alertedChunks.contains(member)) {
               isNew = false;
               break;
            }
         }

         if (!isNew) {
            this.alertedChunks.addAll(zone.members());
         } else {
            this.alertedChunks.addAll(zone.members());
            if (!this.module.notifications.is("Off")) {
               int bx = (int)Math.round(zone.centroidX());
               int bz = (int)Math.round(zone.centroidZ());
               int dist = (int)Math.hypot(bx - mc.field_1724.method_23317(), bz - mc.field_1724.method_23321());
               String message = "Sus zone · " + dist + "m · " + bx + ", " + bz;
               if (LarpClient.notifications() != null) {
                  LarpClient.notifications().pushInfo(message);
                  UiSounds.notification(true);
               }
            }
         }
      }
   }

   public static final class ChunkScore {
      public final long chunkKey;
      public final EnumMap<SusChunkScanner.SignalType, Integer> hits = new EnumMap<>(SusChunkScanner.SignalType.class);
      public final List<class_2338> amethystCells = new ArrayList<>();
      public double score;
      double hitWeight;
      double hitX;
      double hitZ;

      ChunkScore(long chunkKey) {
         this.chunkKey = chunkKey;
      }

      void add(SusChunkScanner.SignalType type, class_2338 pos) {
         this.hits.merge(type, 1, Integer::sum);
         this.hitWeight = this.hitWeight + type.weight;
         this.hitX = this.hitX + (pos.method_10263() + 0.5) * type.weight;
         this.hitZ = this.hitZ + (pos.method_10260() + 0.5) * type.weight;
      }

      void computeScore() {
         double total = 0.0;

         for (Entry<SusChunkScanner.SignalType, Integer> entry : this.hits.entrySet()) {
            total += entry.getKey().weight * Math.min(entry.getValue(), entry.getKey().cap);
         }

         this.score = total;
      }
   }

   public record Flag(long chunkKey, double score) {
   }

   private static final class FlagAggregate {
      final long chunkKey;
      double score;
      double hitWeight;
      double hitX;
      double hitZ;

      FlagAggregate(long chunkKey) {
         this.chunkKey = chunkKey;
      }
   }

   public record Geode(List<class_2338> cells, Set<Long> chunks, double score, double centroidX, double centroidZ) {
   }

   public static enum SignalType {
      AMETHYST(6.0, 16),
      KELP(2.0, 6),
      BAMBOO(2.0, 6),
      BERRIES(2.0, 5),
      VINES(2.0, 6),
      DRIPSTONE(2.0, 5),
      ROTATED_DEEPSLATE(4.0, 8);

      public final double weight;
      public final int cap;

      private SignalType(double weight, int cap) {
         this.weight = weight;
         this.cap = cap;
      }
   }

   public record Zone(Set<Long> members, double centroidX, double centroidZ, double totalScore, double maxScore) {
   }
}
