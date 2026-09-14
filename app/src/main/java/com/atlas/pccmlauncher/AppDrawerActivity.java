package com.atlas.pccmlauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** PCCM v0.9.2 full-screen application drawer. */
public class AppDrawerActivity extends Activity {
    private final ArrayList<AppEntry> allApps = new ArrayList<>();
    private final ArrayList<AppEntry> visibleApps = new ArrayList<>();
    private GridLayout appGrid;
    private ScrollView appScroll;
    private TextView clock;
    private int columnCount = 5;
    private final Handler clockHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemBars();
        loadApps();
        buildUi();
        startClock();
    }

    @Override protected void onResume() { super.onResume(); hideSystemBars(); }
    @Override protected void onDestroy() { clockHandler.removeCallbacksAndMessages(null); super.onDestroy(); }
    @Override public void onWindowFocusChanged(boolean hasFocus) { super.onWindowFocusChanged(hasFocus); if (hasFocus) hideSystemBars(); }

    private void hideSystemBars() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void loadApps() {
        allApps.clear();
        Set<String> seen = new HashSet<>();
        LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
        UserManager userManager = (UserManager) getSystemService(Context.USER_SERVICE);

        if (launcherApps != null) {
            List<UserHandle> profiles = userManager != null ? userManager.getUserProfiles()
                : Collections.singletonList(Process.myUserHandle());
            for (UserHandle profile : profiles) {
                try {
                    for (LauncherActivityInfo info : launcherApps.getActivityList(null, profile)) {
                        ComponentName component = info.getComponentName();
                        if (component == null || getPackageName().equals(component.getPackageName())) continue;
                        addApp(seen, String.valueOf(info.getLabel()), info.getIcon(0), component, profile);
                    }
                } catch (SecurityException ignored) { }
            }
        }

        // Always merge PackageManager results. Some head units expose only part of the
        // launcher catalog through LauncherApps, even when search can resolve the rest.
        PackageManager pm = getPackageManager();
        addResolvedActivities(pm, seen, Intent.CATEGORY_LAUNCHER);
        addResolvedActivities(pm, seen, Intent.CATEGORY_LEANBACK_LAUNCHER);

        Collections.sort(allApps, (a, b) -> a.label.compareToIgnoreCase(b.label));
        visibleApps.clear();
        visibleApps.addAll(allApps);
    }

    private void addResolvedActivities(PackageManager pm, Set<String> seen, String category) {
        Intent query = new Intent(Intent.ACTION_MAIN);
        query.addCategory(category);
        List<ResolveInfo> matches;
        try {
            matches = pm.queryIntentActivities(query, PackageManager.MATCH_ALL);
        } catch (Exception e) {
            matches = Collections.emptyList();
        }
        for (ResolveInfo info : matches) {
            if (info.activityInfo == null || getPackageName().equals(info.activityInfo.packageName)) continue;
            ComponentName component = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
            addApp(seen, String.valueOf(info.loadLabel(pm)), info.loadIcon(pm), component, Process.myUserHandle());
        }
    }

    private void addApp(Set<String> seen, String label, Drawable icon, ComponentName component, UserHandle user) {
        String key = user.hashCode() + ":" + component.flattenToString();
        if (!seen.add(key)) return;
        if (label == null || label.trim().isEmpty()) label = component.getPackageName();
        allApps.add(new AppEntry(label, icon, component, user));
    }

    private void buildUi() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackgroundColor(Color.rgb(5, 5, 5));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(10), 0, dp(10), 0);
        header.setBackground(makeGradient(Color.rgb(35,35,35), Color.rgb(15,15,15), 0, Color.rgb(72,72,72)));
        page.addView(header, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(64)));

        TextView back = makeHeaderButton("‹", getString(R.string.back));
        back.setTextSize(52); back.setOnClickListener(v -> finish());
        header.addView(back, new LinearLayout.LayoutParams(dp(82), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(this);
        title.setText(R.string.app_drawer_title); title.setTextColor(Color.WHITE); title.setTextSize(24);
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        title.setLetterSpacing(0.05f);
        header.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        clock = new TextView(this);
        clock.setTextColor(Color.WHITE); clock.setTextSize(19); clock.setGravity(Gravity.CENTER);
        clock.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        header.addView(clock, new LinearLayout.LayoutParams(dp(130), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView close = makeHeaderButton("×", getString(R.string.close));
        close.setTextSize(42); close.setOnClickListener(v -> finish());
        header.addView(close, new LinearLayout.LayoutParams(dp(74), ViewGroup.LayoutParams.MATCH_PARENT));

        appScroll = new ScrollView(this);
        appScroll.setFillViewport(true);
        appScroll.setVerticalScrollBarEnabled(true);
        appScroll.setScrollbarFadingEnabled(false);
        appScroll.setSmoothScrollingEnabled(true);
        appScroll.setBackgroundColor(Color.rgb(5,5,5));

        appGrid = new GridLayout(this);
        appGrid.setOrientation(GridLayout.HORIZONTAL);
        appGrid.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
        appGrid.setUseDefaultMargins(false);
        appGrid.setPadding(dp(18), dp(16), dp(18), dp(18));
        appScroll.addView(appGrid, new ScrollView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        page.addView(appScroll, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        appScroll.addOnLayoutChangeListener((v,l,t,r,b,ol,ot,or,ob) -> {
            int width = r - l;
            int columns = width >= dp(1250) ? 6 : (width >= dp(900) ? 5 : 4);
            if (columns != columnCount) { columnCount = columns; renderApps(); }
        });

        LinearLayout searchShell = new LinearLayout(this);
        searchShell.setGravity(Gravity.CENTER_VERTICAL);
        searchShell.setPadding(dp(18), dp(8), dp(18), dp(12));
        searchShell.setBackgroundColor(Color.rgb(5,5,5));
        page.addView(searchShell, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(76)));

        EditText search = new EditText(this);
        search.setSingleLine(true); search.setHint(R.string.search_apps);
        search.setHintTextColor(Color.rgb(135,135,135)); search.setTextColor(Color.WHITE); search.setTextSize(19);
        search.setPadding(dp(22),0,dp(22),0);
        search.setBackground(makeGradient(Color.rgb(35,35,35), Color.rgb(20,20,20), dp(12), Color.rgb(72,72,72)));
        search.setImeOptions(EditorInfo.IME_ACTION_DONE);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) { }
            @Override public void onTextChanged(CharSequence s,int start,int before,int count) { filterApps(s == null ? "" : s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });
        searchShell.addView(search, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54)));

        setContentView(page);
        page.post(this::renderApps);
    }

    private void renderApps() {
        if (appGrid == null) return;
        appGrid.removeAllViews();
        appGrid.setColumnCount(columnCount);
        appGrid.setRowCount(Math.max(1, (visibleApps.size() + columnCount - 1) / columnCount));

        int available = appScroll != null && appScroll.getWidth() > 0 ? appScroll.getWidth() : getResources().getDisplayMetrics().widthPixels;
        int cellWidth = Math.max(dp(132), (available - dp(36)) / columnCount);

        for (int i = 0; i < visibleApps.size(); i++) {
            AppEntry app = visibleApps.get(i);
            View cell = createAppCell(app);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(
                GridLayout.spec(i / columnCount), GridLayout.spec(i % columnCount));
            params.width = cellWidth;
            params.height = dp(142);
            params.setMargins(dp(4), dp(4), dp(4), dp(8));
            appGrid.addView(cell, params);
        }
        appGrid.requestLayout();
    }

    private View createAppCell(AppEntry app) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        cell.setPadding(dp(6), dp(4), dp(6), dp(4));
        cell.setClickable(true); cell.setFocusable(true);
        cell.setOnClickListener(v -> launch(app));

        FrameLayout iconTile = new FrameLayout(this);
        iconTile.setBackground(makeGradient(Color.rgb(40,40,40), Color.rgb(12,12,12), dp(9), Color.rgb(88,88,88)));
        cell.addView(iconTile, new LinearLayout.LayoutParams(dp(88), dp(88)));

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        icon.setPadding(dp(10),dp(10),dp(10),dp(10));
        icon.setImageDrawable(app.icon); icon.setContentDescription(app.label);
        iconTile.addView(icon, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView label = new TextView(this);
        label.setText(app.label); label.setTextColor(Color.rgb(235,235,235)); label.setTextSize(14);
        label.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL); label.setMaxLines(2);
        label.setIncludeFontPadding(false);
        label.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL));
        label.setPaintFlags(label.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(38));
        lp.topMargin = dp(7); cell.addView(label, lp);
        return cell;
    }

    private TextView makeHeaderButton(String text, String description) {
        TextView button = new TextView(this);
        button.setText(text); button.setTextColor(Color.rgb(225,225,225)); button.setGravity(Gravity.CENTER);
        button.setContentDescription(description); button.setClickable(true); button.setFocusable(true);
        return button;
    }

    private void filterApps(String query) {
        String needle = query.trim().toLowerCase(Locale.ROOT);
        visibleApps.clear();
        if (needle.isEmpty()) visibleApps.addAll(allApps);
        else for (AppEntry app : allApps) if (app.label.toLowerCase(Locale.ROOT).contains(needle)) visibleApps.add(app);
        renderApps();
        if (appScroll != null) appScroll.scrollTo(0,0);
    }

    private void launch(AppEntry app) {
        try {
            LauncherApps launcherApps = (LauncherApps) getSystemService(Context.LAUNCHER_APPS_SERVICE);
            if (launcherApps != null) {
                try { launcherApps.startMainActivity(app.component, app.user, null, null); return; }
                catch (Exception ignored) { }
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(app.component);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.unable_open_app, app.label), Toast.LENGTH_SHORT).show();
        }
    }

    private void startClock() {
        Runnable tick = new Runnable() {
            @Override public void run() {
                if (clock != null) {
                    DateFormat format = android.text.format.DateFormat.getTimeFormat(AppDrawerActivity.this);
                    clock.setText(format.format(new Date()));
                }
                clockHandler.postDelayed(this, 1000);
            }
        };
        clockHandler.post(tick);
    }

    @Override public void onBackPressed() { finish(); }

    private GradientDrawable makeGradient(int top,int bottom,int radius,int stroke) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{top,bottom});
        drawable.setCornerRadius(radius); drawable.setStroke(dp(1),stroke); return drawable;
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    private static class AppEntry {
        final String label; final Drawable icon; final ComponentName component; final UserHandle user;
        AppEntry(String label, Drawable icon, ComponentName component, UserHandle user) {
            this.label=label; this.icon=icon; this.component=component; this.user=user;
        }
    }
}
