package razerdp.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import razerdp.basepopup.BasePopupSDK;
import razerdp.util.log.PopupLog;

/**
 * Created by 大灯泡 on 2018/12/23.
 */
public class PopupUiUtils {

    public static final String POPUP_DECORVIEW = "android.widget.PopupWindow$PopupDecorView";
    public static final String POPUP_VIEWCONTAINER = "android.widget.PopupWindow$PopupViewContainer";
    public static final String POPUP_BACKGROUNDVIEW = "android.widget.PopupWindow$PopupBackgroundView";

    private static final List<String> NAVIGATION_BAR_NAMES = new ArrayList<>();

    static {
        NAVIGATION_BAR_NAMES.add("navigationbarbackground");
        NAVIGATION_BAR_NAMES.add("immersion_navigation_bar_view");
    }

    public static boolean isPopupDecorView(View view) {
        return view != null && TextUtils.equals(view.getClass().getName(), POPUP_DECORVIEW);
    }

    public static boolean isPopupViewContainer(View view) {
        return view != null && TextUtils.equals(view.getClass().getName(), POPUP_VIEWCONTAINER);
    }

    public static boolean isPopupBackgroundView(View view) {
        return view != null && TextUtils.equals(view.getClass().getName(), POPUP_BACKGROUNDVIEW);
    }

    private static int statusBarHeight;

    public static void requestFocus(View v) {
        if (v == null) return;
        if (v.isInTouchMode()) {
            v.requestFocusFromTouch();
        } else {
            v.requestFocus();
        }
    }

    /**
     * 方法参考
     * https://juejin.im/post/5bb5c4e75188255c72285b54
     */
    @SuppressLint("NewApi")
    public static int getNavigationBarHeight(Context context) {
        Activity act = PopupUtils.getActivity(context);
        if (!PopupUtils.isActivityAlive(act)) return 0;
        ViewGroup decorView = (ViewGroup) act.getWindow().getDecorView();
        final int childCount = decorView.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = decorView.getChildAt(i);
            if (child.getId() == View.NO_ID || !child.isShown()) continue;
            String resourceEntryName;
            try {
                resourceEntryName = act.getResources().getResourceEntryName(child.getId());
                if (NAVIGATION_BAR_NAMES.contains(resourceEntryName.toLowerCase())) {
                    return child.getHeight();
                }
            } catch (Exception e) {
                //do nothing
            }
        }
        return 0;
    }

    public static int getScreenOrientation() {
        return BasePopupSDK.getApplication().getResources().getConfiguration().orientation;
    }

    public static int getStatusBarHeight() {
        checkStatusBarHeight();
        return statusBarHeight;
    }

    private static void checkStatusBarHeight() {
        if (statusBarHeight != 0) return;
        int result = 0;
        //获取状态栏高度的资源id
        Resources resources = BasePopupSDK.getApplication().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId);
        }
        statusBarHeight = result;
    }

    public static void setBackground(View v, Drawable background) {
        if (v == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            v.setBackground(background);
        } else {
            v.setBackgroundDrawable(background);
        }
    }

    public static void safeAddGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        try {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
            v.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        } catch (Exception e) {
            PopupLog.e(e);
        }
    }

    public static void safeRemoveGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        try {
            v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
        } catch (Exception e) {
            PopupLog.e(e);
        }
    }

    public static boolean isActivityFullScreen(Activity act) {
        if (act == null || act.getWindow() == null) return false;
        return (act.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN;

    }

    public static int computeGravity(@NonNull Rect popupRect, @NonNull Rect anchorRect) {
        int gravity = Gravity.NO_GRAVITY;
        int xDelta = popupRect.centerX() - anchorRect.centerX();
        int yDelta = popupRect.centerY() - anchorRect.centerY();
        if (xDelta == 0) {
            gravity = yDelta == 0 ? Gravity.CENTER : Gravity.CENTER_HORIZONTAL | ((yDelta > 0) ? Gravity.BOTTOM : Gravity.TOP);
        }
        if (yDelta == 0) {
            gravity = xDelta == 0 ? Gravity.CENTER : Gravity.CENTER_VERTICAL | ((xDelta > 0) ? Gravity.RIGHT : Gravity.LEFT);
        }
        if (gravity == Gravity.NO_GRAVITY) {
            gravity = xDelta > 0 ? Gravity.RIGHT : Gravity.LEFT;
            gravity |= yDelta > 0 ? Gravity.BOTTOM : Gravity.TOP;
        }
        return gravity;
    }
}
