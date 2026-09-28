package app.matthew.chrome.extension;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import java.util.List;

public final class ThemePicker {
    private static int binding;
    private static final String TAG = "chrome.patch.black.choice";
    private ThemePicker() {}
    public static String summary(String original) { return PatchSettings.enabled(PatchSettings.BLACK) ? "Black" : original; }
    public static void beginBinding() { binding++; }
    public static void nativeChoice(Object preference) {
        if (binding != 0 || !PatchSettings.enabled(PatchSettings.BLACK)) return;
        PatchSettings.set(PatchSettings.BLACK, false);
        View choice = NativeBridge.themeChoices(preference).get(0);
        choice.post(() -> {
            // Black and Dark share Chrome's dark setting, so selecting Dark need not trigger
            // Chrome's configuration observer. Recreate to restore its original drawables.
            Activity activity = activity(choice.getContext());
            if (NativeBridge.themeSetting() == 2 && activity != null && !activity.isDestroyed()) activity.recreate();
        });
    }
    private static Activity activity(Context context) {
        while (context instanceof ContextWrapper && !(context instanceof Activity)) context = ((ContextWrapper)context).getBaseContext();
        return context instanceof Activity ? (Activity)context : null;
    }
    public static void finishBinding(Object preference) {
        try {
            List<View> choices = NativeBridge.themeChoices(preference);
            View dark = choices.get(2);
            ViewGroup parent = (ViewGroup) dark.getParent().getParent();
            RadioButton black = parent.findViewWithTag(TAG);
            if (black == null) {
                black = new RadioButton(parent.getContext());
                black.setTag(TAG); black.setText("Black"); black.setTextSize(18);
                float density = parent.getResources().getDisplayMetrics().density;
                black.setPadding(Math.round(16 * density), Math.round(12 * density), Math.round(16 * density), Math.round(12 * density));
                black.setMinHeight(Math.round(66 * density));
                LinearLayout.LayoutParams row = new LinearLayout.LayoutParams(-1, -2);
                row.setMarginStart(Math.round(32 * density)); row.setMarginEnd(Math.round(32 * density));
                parent.addView(black, row);
                black.setOnClickListener(v -> {
                    PatchSettings.set(PatchSettings.BLACK, true);
                    Context c = v.getContext();
                    while (c instanceof ContextWrapper && !(c instanceof Activity)) c = ((ContextWrapper)c).getBaseContext();
                    if (c instanceof Activity) ((Activity)c).recreate();
                });
            }
            boolean selected = PatchSettings.enabled(PatchSettings.BLACK);
            black.setChecked(selected);
            if (selected) NativeBridge.setThemeRadioChecked(dark, false);
        } finally { binding = Math.max(0, binding - 1); }
    }
}
