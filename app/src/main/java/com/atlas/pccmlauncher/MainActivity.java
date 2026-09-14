package com.atlas.pccmlauncher;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    // Stable internal keys preserve tile assignments when the system language changes.
    private final String[] tileKeys = {
        "Tuner", "Media", "Phone", "Phone Link", "Map", "Sound", "Car", "Apps"
    };
    private final int[] tileLabelRes = {
        R.string.tile_tuner, R.string.tile_media, R.string.tile_phone, R.string.tile_phone_link,
        R.string.tile_map, R.string.tile_sound, R.string.tile_car, R.string.tile_apps
    };
    private final String[] tileIcons = {
        "ic_pccm_tuner", "ic_pccm_media", "ic_pccm_phone", "ic_pccm_nav",
        "ic_pccm_map", "ic_pccm_sound", "ic_pccm_car", "ic_pccm_apps"
    };

    private SharedPreferences prefs;
    private TextView clock;
    private final int WHITE = Color.rgb(232,232,232);
    private final int LABEL = Color.rgb(205,205,205);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs = getSharedPreferences("tile_map", MODE_PRIVATE);
        hideSystemBars();
        buildUi();
        startClock();
    }

    @Override public void onResume() {
        super.onResume();
        hideSystemBars();
    }

    @Override public void onRestart() {
        super.onRestart();
        hideSystemBars();
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemBars();
    }

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
            View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void buildUi() {
        FrameLayout frame = new FrameLayout(this);
        frame.setBackgroundColor(Color.rgb(7,7,7));

        FrameLayout screen = new FrameLayout(this);
        screen.setBackground(new ScreenPanelDrawable());
        frame.addView(screen, new FrameLayout.LayoutParams(-1, -1));

        View topMetal = new View(this);
        topMetal.setBackground(new BrushedBarDrawable(true));
        screen.addView(topMetal);

        View bottomMetal = new View(this);
        bottomMetal.setBackground(new BrushedBarDrawable(false));
        screen.addView(bottomMetal);

        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        grid.setGravity(Gravity.CENTER);
        screen.addView(grid);

        ArrayList<LinearLayout> tileViews = new ArrayList<>();
        for (int r=0; r<2; r++) {
            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER);
            grid.addView(row);
            for (int c=0; c<4; c++) {
                int idx = r*4+c;
                LinearLayout tile = makeTile(tileKeys[idx], tileLabelRes[idx], tileIcons[idx]);
                row.addView(tile);
                tileViews.add(tile);
            }
        }

        clock = new TextView(this);
        clock.setTextColor(Color.rgb(238,238,238));
        clock.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
        clock.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        screen.addView(clock);

        screen.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int w = right - left;
            int h = bottom - top;

            int topBarH = Math.round(h * 0.105f);
            int bottomBarH = Math.round(h * 0.115f);

            FrameLayout.LayoutParams topLp = new FrameLayout.LayoutParams(-1, topBarH, Gravity.TOP);
            topMetal.setLayoutParams(topLp);

            FrameLayout.LayoutParams bottomLp = new FrameLayout.LayoutParams(-1, bottomBarH, Gravity.BOTTOM);
            bottomMetal.setLayoutParams(bottomLp);

            FrameLayout.LayoutParams gridLp = new FrameLayout.LayoutParams(-1, h - topBarH - bottomBarH);
            gridLp.topMargin = topBarH;
            gridLp.bottomMargin = bottomBarH;
            grid.setLayoutParams(gridLp);

            int horizontalPad = Math.round(w * 0.085f);
            int verticalPad = Math.round(h * 0.055f);
            grid.setPadding(horizontalPad, verticalPad, horizontalPad, verticalPad);

            int rowHeight = Math.round((h - topBarH - bottomBarH - (verticalPad * 2)) / 2.0f);
            int tileW = Math.round(w * 0.175f);
            int tileH = Math.round(rowHeight * 0.78f);
            int iconW = Math.round(w * 0.125f);
            int iconH = Math.round(iconW * 0.92f);
            int labelH = Math.round(h * 0.052f);
            int topGap = Math.round(h * 0.014f);
            int horizontalMargin = Math.round(w * 0.028f);

            for (int i = 0; i < grid.getChildCount(); i++) {
                LinearLayout row = (LinearLayout) grid.getChildAt(i);
                row.setGravity(Gravity.CENTER);
                row.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1));
                for (int j = 0; j < row.getChildCount(); j++) {
                    LinearLayout tile = (LinearLayout) row.getChildAt(j);
                    LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(tileW, tileH);
                    tlp.setMargins(horizontalMargin, 0, horizontalMargin, 0);
                    tile.setLayoutParams(tlp);

                    View icon = tile.getChildAt(0);
                    LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconW, iconH);
                    icon.setLayoutParams(ilp);

                    View label = tile.getChildAt(1);
                    LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(-1, labelH);
                    nlp.setMargins(0, topGap, 0, 0);
                    label.setLayoutParams(nlp);
                    if (label instanceof TextView) {
                        ((TextView) label).setTextSize(Math.max(11, h * 0.020f));
                    }
                }
            }

            FrameLayout.LayoutParams clp = new FrameLayout.LayoutParams(Math.round(w * 0.105f), Math.round(h * 0.052f), Gravity.RIGHT|Gravity.BOTTOM);
            clp.setMargins(0,0,Math.round(w * 0.025f),Math.round(h * 0.025f));
            clock.setLayoutParams(clp);
            clock.setTextSize(Math.max(16, h * 0.031f));
        });

        clock.setOnLongClickListener(v -> {
            openAndroidSettings();
            return true;
        });

        setContentView(frame);
    }

    private LinearLayout makeTile(final String key, int labelResId, String iconResourceName) {
        final String label = getString(labelResId);
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);
        tile.setPadding(0, 0, 0, 0);
        tile.setClickable(true);
        tile.setFocusable(true);

        ImageView icon = new ImageView(this);
        int resId = getResources().getIdentifier(iconResourceName, "drawable", getPackageName());
        if (resId != 0) icon.setImageResource(resId);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setAdjustViewBounds(false);
        icon.setAlpha(1.0f);

        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(dp(70), dp(64));
        tile.addView(icon, ilp);

        TextView name = new TextView(this);
        name.setText(label);
        name.setTextColor(Color.rgb(188,186,182));
        name.setTextSize(13);
        name.setGravity(Gravity.CENTER);
        name.setIncludeFontPadding(false);
        name.setLetterSpacing(0.018f);
        name.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        name.setPaintFlags(name.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(-1, dp(24));
        nlp.setMargins(0, dp(11), 0, 0);
        tile.addView(name, nlp);

        tile.setOnClickListener(v -> {
            if ("Apps".equals(key)) {
                showAppDrawer();
            } else {
                launchAssigned(key);
            }
        });

        tile.setOnLongClickListener(v -> {
            if ("Apps".equals(key)) {
                Toast.makeText(this, R.string.apps_drawer_info, Toast.LENGTH_SHORT).show();
            } else {
                chooseAppForTile(key, label);
            }
            return true;
        });

        return tile;
    }

    private void launchAssigned(String key) {
        String pkg = prefs.getString(key, "");
        if (pkg == null || pkg.length() == 0) {
            chooseAppForTile(key, getDisplayLabelForKey(key));
            return;
        }

        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i == null) {
            Toast.makeText(this, R.string.assigned_app_missing, Toast.LENGTH_LONG).show();
            return;
        }

        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private List<ResolveInfo> launchableApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = getPackageManager().queryIntentActivities(intent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(getPackageManager()));
        return apps;
    }

    private void chooseAppForTile(String key, String label) {
        List<ResolveInfo> apps = launchableApps();
        ArrayList<String> names = new ArrayList<>();
        for (ResolveInfo r : apps) names.add(r.loadLabel(getPackageManager()).toString());

        new AlertDialog.Builder(this)
            .setTitle(getString(R.string.assign_app_title, label))
            .setItems(names.toArray(new String[0]), (d, which) -> {
                String pkg = apps.get(which).activityInfo.packageName;
                prefs.edit().putString(key, pkg).apply();
                Toast.makeText(this, getString(R.string.app_assigned, label, names.get(which)), Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton(R.string.clear, (d,w) -> {
                prefs.edit().remove(key).apply();
                Toast.makeText(this, getString(R.string.tile_cleared, label), Toast.LENGTH_SHORT).show();
            })
            .show();
    }

    private String getDisplayLabelForKey(String key) {
        for (int i = 0; i < tileKeys.length; i++) {
            if (tileKeys[i].equals(key)) return getString(tileLabelRes[i]);
        }
        return key;
    }

    private void showAppDrawer() {
        Intent drawer = new Intent(this, AppDrawerActivity.class);
        startActivity(drawer);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startClock() {
        Handler h = new Handler(Looper.getMainLooper());
        Runnable tick = new Runnable() {
            @Override public void run() {
                if (clock != null) {
                    java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(MainActivity.this);
                    clock.setText(timeFormat.format(new Date()));
                }
                h.postDelayed(this, 1000);
            }
        };
        h.post(tick);
    }

    @Override public void onBackPressed() {
        // Launcher-safe behavior: pressing Back on HOME should not close or expose Android underneath.
        hideSystemBars();
    }

    private void openAndroidSettings() {
        try {
            Intent i = new Intent(android.provider.Settings.ACTION_SETTINGS);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, R.string.unable_open_settings, Toast.LENGTH_SHORT).show();
        }
    }

    private int dp(int v) {
        return (int)(v * getResources().getDisplayMetrics().density + 0.5f);
    }

    private class OemBackgroundDrawable extends Drawable {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        @Override public void draw(Canvas c) {
            Rect b = getBounds();
            LinearGradient g = new LinearGradient(0,0,b.width(),b.height(),
                new int[]{Color.rgb(2,2,2), Color.rgb(22,22,22), Color.rgb(8,8,8)},
                new float[]{0f, .55f, 1f}, Shader.TileMode.CLAMP);
            p.setShader(g);
            c.drawRect(b, p);
            p.setShader(null);

            RadialGradient rg = new RadialGradient(b.width()/2f, b.height()/2f, b.width()*0.64f,
                new int[]{Color.argb(65,255,255,255), Color.argb(0,0,0,0)},
                new float[]{0f, 1f}, Shader.TileMode.CLAMP);
            p.setShader(rg);
            c.drawRect(b, p);
            p.setShader(null);
        }
        @Override public void setAlpha(int a) {}
        @Override public void setColorFilter(android.graphics.ColorFilter f) {}
        @Override public int getOpacity() { return PixelFormat.OPAQUE; }
    }

    private class ScreenPanelDrawable extends Drawable {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        @Override public void draw(Canvas c) {
            Rect b = getBounds();

            p.setStyle(Paint.Style.FILL);
            LinearGradient outer = new LinearGradient(0,0,0,b.height(),
                Color.rgb(46,46,46), Color.rgb(4,4,4), Shader.TileMode.CLAMP);
            p.setShader(outer);
            c.drawRect(b, p);
            p.setShader(null);

            p.setColor(Color.rgb(0,0,0));
            c.drawRect(dp(2), dp(43), b.width()-dp(2), b.height()-dp(43), p);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            p.setColor(Color.rgb(82,82,82));
            c.drawRect(0,0,b.width()-1,b.height()-1,p);

            p.setColor(Color.rgb(26,26,26));
            c.drawRect(dp(1),dp(1),b.width()-dp(2),b.height()-dp(2),p);
        }
        @Override public void setAlpha(int a) {}
        @Override public void setColorFilter(android.graphics.ColorFilter f) {}
        @Override public int getOpacity() { return PixelFormat.OPAQUE; }
    }

    private class BrushedBarDrawable extends Drawable {
        private boolean top;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        BrushedBarDrawable(boolean top) { this.top = top; }

        @Override public void draw(Canvas c) {
            Rect b = getBounds();
            LinearGradient g = new LinearGradient(0,0,0,b.height(),
                new int[]{Color.rgb(72,72,72), Color.rgb(42,42,42), Color.rgb(19,19,19)},
                new float[]{0f, .46f, 1f}, Shader.TileMode.CLAMP);
            p.setShader(g);
            c.drawRect(b, p);
            p.setShader(null);

            // brushed horizontal grain
            p.setStrokeWidth(1);
            for (int y=0; y<b.height(); y+=2) {
                int shade = 50 + (y % 6);
                p.setColor(Color.argb(38, shade+50, shade+50, shade+50));
                c.drawLine(0, y, b.width(), y, p);
            }

            p.setColor(top ? Color.rgb(18,18,18) : Color.rgb(92,92,92));
            c.drawLine(0, top ? b.height()-1 : 0, b.width(), top ? b.height()-1 : 0, p);
        }
        @Override public void setAlpha(int a) {}
        @Override public void setColorFilter(android.graphics.ColorFilter f) {}
        @Override public int getOpacity() { return PixelFormat.OPAQUE; }
    }

    private class MetalTileDrawable extends Drawable {
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        @Override public void draw(Canvas c) {
            Rect b = getBounds();

            LinearGradient g = new LinearGradient(0,0,0,b.height(),
                new int[]{Color.rgb(112,112,112), Color.rgb(76,76,76), Color.rgb(43,43,43), Color.rgb(28,28,28)},
                new float[]{0f,.24f,.62f,1f}, Shader.TileMode.CLAMP);
            p.setShader(g);
            c.drawRoundRect(0,0,b.width(),b.height(),dp(3),dp(3),p);
            p.setShader(null);

            // tighter vertical brushed-metal grain
            p.setStrokeWidth(1);
            for (int x=1; x<b.width(); x+=2) {
                int alpha = (x % 8 == 0) ? 34 : 13;
                p.setColor(Color.argb(alpha, 238,238,238));
                c.drawLine(x, 2, x, b.height()-3, p);
            }

            // restrained top highlight, less Android-gloss, more PCM/PCCM metal
            LinearGradient highlight = new LinearGradient(0,0,0,b.height() * 0.36f,
                Color.argb(48,255,255,255), Color.argb(0,255,255,255), Shader.TileMode.CLAMP);
            p.setShader(highlight);
            c.drawRoundRect(dp(1),dp(1),b.width()-dp(1),b.height()*0.50f,dp(3),dp(3),p);
            p.setShader(null);

            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(1);
            p.setColor(Color.rgb(132,132,132));
            c.drawRoundRect(0.5f,0.5f,b.width()-1,b.height()-1,dp(3),dp(3),p);

            p.setColor(Color.rgb(20,20,20));
            c.drawLine(2, b.height()-1, b.width()-2, b.height()-1, p);

            p.setStyle(Paint.Style.FILL);
        }
        @Override public void setAlpha(int a) {}
        @Override public void setColorFilter(android.graphics.ColorFilter f) {}
        @Override public int getOpacity() { return PixelFormat.OPAQUE; }
    }
}
