package com.larpclient.util;

public final class TpsTracker {
   private static long lastPacketNanos = -1L;
   private static float tps = 20.0F;

   private TpsTracker() {
   }

   public static void onTimePacket() {
      long now = System.nanoTime();
      float seconds;
      if (lastPacketNanos > 0L && (seconds = (float)(now - lastPacketNanos) / 1.0E9F) > 0.05F) {
         float measured = Math.clamp(20.0F / seconds, 0.0F, 20.0F);
         tps = tps * 0.7F + measured * 0.3F;
      }

      lastPacketNanos = now;
   }

   public static void reset() {
      lastPacketNanos = -1L;
      tps = 20.0F;
   }

   public static float get() {
      return tps;
   }
}
