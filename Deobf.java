package com.larpclient.rt;

public final class Deobf {
   private static final String KEY = "s3v3n_v31l_67c!ent_x0r_k3y_9E3779B1";

   private Deobf() {
   }

   public static String decrypt(String string) {
      char[] cArray = string.toCharArray();

      for (int i = 0; i < cArray.length; i++) {
         cArray[i] = (char)(cArray[i] ^ "s3v3n_v31l_67c!ent_x0r_k3y_9E3779B1".charAt(i % "s3v3n_v31l_67c!ent_x0r_k3y_9E3779B1".length()) ^ i * 31);
      }

      return new String(cArray);
   }
}
