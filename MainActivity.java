package com.shahid.carromaim;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final int OVERLAY_REQ = 101;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(28, 36, 28, 28);
        TextView title = new TextView(this);
        title.setText("Shahid Carrom Aim\n\nFloating aim guide starter app");
        title.setTextSize(22);
        box.addView(title);
        TextView info = new TextView(this);
        info.setText("\nOverlay permission dene ke baad Start Aim dabao. Floating panel mein S, T aur P set karke screen par points mark kar sakte ho.\n\nNote: ye manual aim guide hai; automatic coin recognition ya guaranteed shot prediction nahi.");
        info.setTextSize(16);
        box.addView(info);
        Button permission = new Button(this);
        permission.setText("Allow display over other apps");
        permission.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(i, OVERLAY_REQ);
            }
        });
        box.addView(permission);
        Button start = new Button(this);
        start.setText("Start floating aim");
        start.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
                permission.performClick();
                return;
            }
            startService(new Intent(this, AimOverlayService.class));
            finish();
        });
        box.addView(start);
        setContentView(box);
    }
}
