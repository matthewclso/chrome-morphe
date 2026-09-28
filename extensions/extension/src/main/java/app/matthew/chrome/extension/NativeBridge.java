package app.matthew.chrome.extension;

import android.app.Activity;

/** Bodies are replaced with validated native Chrome calls by the patch. */
public final class NativeBridge {
    private NativeBridge() {}
    public static boolean isIncognito(Activity activity) { throw new IllegalStateException("Unpatched bridge"); }
    public static int tabCount(Activity activity, boolean incognito) { throw new IllegalStateException("Unpatched bridge"); }
    public static void selectModel(Activity activity, boolean incognito) { throw new IllegalStateException("Unpatched bridge"); }
    public static boolean newTab(Activity activity, int menuId) { throw new IllegalStateException("Unpatched bridge"); }
    public static boolean incognitoAllowed(Activity activity) { throw new IllegalStateException("Unpatched bridge"); }
    public static boolean defaultFeatureEnabled() { return false; }
    public static void writeChromeInt(int value, String key) { throw new IllegalStateException("Unpatched bridge"); }
    public static int themeSetting() { return 0; }
    public static void setBottomPosition() { throw new IllegalStateException("Unpatched bridge"); }
    public static java.util.List<android.view.View> themeChoices(Object preference) { throw new IllegalStateException("Unpatched bridge"); }
    public static void setThemeRadioChecked(android.view.View radio, boolean checked) { throw new IllegalStateException("Unpatched bridge"); }
    public static boolean bottomSelected() { return false; }
    public static void unanchorSearchResults(android.view.View view) { throw new IllegalStateException("Unpatched bridge"); }
}
