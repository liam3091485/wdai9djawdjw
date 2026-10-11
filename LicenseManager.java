package com.larpclient.license;

public final class LicenseManager {
   public static final String VERIFY_URL = "";

   public boolean isValid() {
      return true;
   }

   public String email() {
      return "No credits for the cracker, fuck you";
   }

   public String plan() {
      return "premium";
   }

   public String lastError() {
      return "";
   }

   public String savedKey() {
      return "No credits for the cracker, fuck you";
   }

   public boolean validateSaved() {
      return true;
   }

   public synchronized boolean validate(String var1, boolean var2) {
      return true;
   }

   public void logout() {
   }
}
