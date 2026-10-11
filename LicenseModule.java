package com.larpclient.module.impl;

import com.larpclient.LarpClient;
import com.larpclient.module.Category;
import com.larpclient.module.Module;
import com.larpclient.settings.StringSetting;

public final class LicenseModule extends Module {
   public final StringSetting key = this.addSetting(new StringSetting("License Key", "Paste the license key from your LARP dashboard", "", 96, "LARP-..."));

   public LicenseModule() {
      super("License", "Activate this installation using your LARP Client license.", Category.CLIENT);
      String saved = LarpClient.license().savedKey();
      if (!saved.isBlank()) {
         this.key.set(saved);
      }
   }

   @Override
   protected boolean requiresLicense() {
      return false;
   }

   @Override
   protected void onEnable() {
      boolean ok = LarpClient.license().validate(this.key.get(), true);
      if (LarpClient.notifications() != null) {
         if (ok) {
            LarpClient.notifications().pushInfo("License verified" + (LarpClient.license().plan().isBlank() ? "" : " • " + LarpClient.license().plan()));
         } else {
            LarpClient.notifications().pushInfo(LarpClient.license().lastError());
         }
      }

      this.setEnabled(false);
   }
}
