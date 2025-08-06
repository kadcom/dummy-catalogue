package dev.kadcom.commerce.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Utility class for programmatic styling without XML
 * Provides Material Design-inspired styles and consistent spacing
 */
public class StyleUtils {
    
    // Material Design Colors
    public static final int PRIMARY_COLOR = Color.parseColor("#1976D2");
    public static final int PRIMARY_DARK = Color.parseColor("#1565C0");
    public static final int ACCENT_COLOR = Color.parseColor("#FF4081");
    public static final int SURFACE_COLOR = Color.parseColor("#FFFFFF");
    public static final int BACKGROUND_COLOR = Color.parseColor("#F5F5F5");
    public static final int TEXT_PRIMARY = Color.parseColor("#212121");
    public static final int TEXT_SECONDARY = Color.parseColor("#757575");
    public static final int DIVIDER_COLOR = Color.parseColor("#BDBDBD");
    
    // Spacing Constants (dp)
    public static final int SPACING_TINY = 4;
    public static final int SPACING_SMALL = 8;
    public static final int SPACING_MEDIUM = 16;
    public static final int SPACING_LARGE = 24;
    public static final int SPACING_XL = 32;
    
    // Border Radius
    public static final int RADIUS_SMALL = 4;
    public static final int RADIUS_MEDIUM = 8;
    public static final int RADIUS_LARGE = 12;
    
    /**
     * Convert dp to pixels
     */
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp, 
            context.getResources().getDisplayMetrics()
        );
    }
    
    /**
     * Convert sp to pixels for text size
     */
    public static int spToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp, 
            context.getResources().getDisplayMetrics()
        );
    }
    
    /**
     * Create rounded rectangle drawable with shadow
     */
    public static GradientDrawable createCardBackground(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(radiusDp);
        return drawable;
    }
    
    /**
     * Create elevated card background
     */
    public static GradientDrawable createElevatedCard(Context context) {
        GradientDrawable drawable = createCardBackground(SURFACE_COLOR, dpToPx(context, RADIUS_MEDIUM));
        // Note: Real elevation requires StateListAnimator or custom shadow drawing
        return drawable;
    }
    
    /**
     * Set standard margin parameters
     */
    public static ViewGroup.MarginLayoutParams createMarginParams(
            Context context, int widthDp, int heightDp, int marginDp) {
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
            widthDp == -1 ? ViewGroup.LayoutParams.MATCH_PARENT : dpToPx(context, widthDp),
            heightDp == -1 ? ViewGroup.LayoutParams.WRAP_CONTENT : dpToPx(context, heightDp)
        );
        int margin = dpToPx(context, marginDp);
        params.setMargins(margin, margin, margin, margin);
        return params;
    }
    
    /**
     * Set linear layout parameters with weight
     */
    public static LinearLayout.LayoutParams createLinearParams(
            Context context, int widthDp, int heightDp, float weight) {
        return new LinearLayout.LayoutParams(
            widthDp == -1 ? ViewGroup.LayoutParams.MATCH_PARENT : dpToPx(context, widthDp),
            heightDp == -1 ? ViewGroup.LayoutParams.WRAP_CONTENT : dpToPx(context, heightDp),
            weight
        );
    }
    
    /**
     * Typography styles
     */
    public static class Typography {
        public static final int HEADLINE_SIZE = 24;
        public static final int TITLE_SIZE = 20;
        public static final int SUBTITLE_SIZE = 16;
        public static final int BODY_SIZE = 14;
        public static final int CAPTION_SIZE = 12;
        
        public static Typeface getRegularTypeface() {
            return Typeface.DEFAULT;
        }
        
        public static Typeface getBoldTypeface() {
            return Typeface.DEFAULT_BOLD;
        }
    }
    
    /**
     * Animation durations
     */
    public static class Animation {
        public static final int DURATION_SHORT = 150;
        public static final int DURATION_MEDIUM = 300;
        public static final int DURATION_LONG = 500;
    }
}