package com.conduent.nationalhighways.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.conduent.nationalhighways.R;

/**
 * This is proof-of-concept software for demonstration purposes and is not a robust implementation
 * of anything. This software is provided "AS IS", wothout warranty of any kind.
 * See <a href="http://unlicense.org">Unlicense.org</a> for details.
 * <p>
 * Custom view to provide accessible regions of the screen. This View mimics placing views
 * within a ViewGroup without having to create a ViewGroup. This is useful to provide
 * groups of widgets with a single group content description without having to make changes to the
 * underlying layout. This class is primarily intended  to be used with
 * ConstraintLayout where a flat view hierarchy is preferred.
 *
 * <p>This class groups a set of widgets together as an "accessible_group" through XML as follows:
 * <pre>{@code
 *     <com.example.constraintlayoutaccessibility.AccessibilityOverlay
 *          android:id="@+id/overlay"
 *          android:layout_width="0dp"
 *          android:layout_height="0dp"
 *          android:focusable="true" <!-- Important -->
 *          app:accessible_group="id1, id2, id3, id4, ..."
 *          app:layout_constraintBottom_toBottomOf="@+id/id_on_bottom"
 *          app:layout_constraintEnd_toEndOf="@+id/id_at_end"
 *          app:layout_constraintStart_toEndOf="@id/id_at_start"
 *          app:layout_constraintTop_toTopOf="@id/id_at_start" /> }</pre>
 *
 * <p>The overlay should be defined in the XML <i>after</i> the referenced ids and should overlay
 * all of the referenced views although this is not enforced. {@code android:focusable="true"} should
 * be set in the XML for the overlay.
 *
 * <p>When this widget gains focus, the content descriptions of all the references views will be
 * collected together and delivered as a unit using {@link AccessibilityEvent}.
 * If a referenced view does not have a content description and is a TextView, the current text
 * of the TextView will be used and, if still empty, the hint will be used.
 */

public class AccessibilityOverlay extends View {
    private int[] mAccessibleIds;

    public AccessibilityOverlay(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public AccessibilityOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public AccessibilityOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public AccessibilityOverlay(Context context, @Nullable AttributeSet attrs,
                                int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, @Nullable AttributeSet attrs,
                      int defStyleAttr, int defStyleRes) {
        String accessibleIdString;

        TypedArray a = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.AccessibilityOverlay,
            defStyleAttr, defStyleRes);

        try {
            accessibleIdString = a.getString(R.styleable.AccessibilityOverlay_accessible_group);
        } finally {
            a.recycle();
        }
        mAccessibleIds = extractAccessibleIds(context, accessibleIdString);
    }

    @NonNull
    private int[] extractAccessibleIds(@NonNull Context context, @Nullable String idNameString) {
        if (TextUtils.isEmpty(idNameString)) {
            return new int[]{};
        }
        String[] idNames = idNameString.split(ID_DELIM);
        int[] resIds = new int[idNames.length];
        Resources resources = context.getResources();
        String packageName = context.getPackageName();
        int idCount = 0;
        for (String idName : idNames) {
            idName = idName.trim();
            if (idName.length() > 0) {
                int resId = resources.getIdentifier(idName, ID_DEFTYPE, packageName);
                if (resId != 0) {
                    resIds[idCount++] = resId;
                }
            }
        }
        return resIds;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        View view;
        ViewGroup parent = (ViewGroup) getParent();
        for (int id : mAccessibleIds) {
            if (id == 0) {
                break;
            }
            view = parent.findViewById(id);
            if (view != null) {
                view.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
            }
        }
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);

        int eventType = event.getEventType();
        if (eventType == AccessibilityEvent.TYPE_VIEW_SELECTED ||
            eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED &&
                getContentDescription() == null) {
            event.getText().add(getAccessibilityText());
        }
    }

    @NonNull
    private String getAccessibilityText() {
        ViewGroup parent = (ViewGroup) getParent();
        View view;
        StringBuilder sb = new StringBuilder();

        for (int id : mAccessibleIds) {
            if (id == 0) {
                break;
            }
            view = parent.findViewById(id);
            if (view != null && view.getVisibility() == View.VISIBLE) {
                CharSequence description = view.getContentDescription();

                // This misbehaves if the view is an EditText or Button or otherwise derived
                // from TextView by voicing the content when the ViewGroup approach remains
                // silent.
                if (TextUtils.isEmpty(description) && view instanceof TextView) {
                    TextView tv = (TextView) view;
                    description = tv.getText();
                    if (TextUtils.isEmpty(description)) {
                        description = tv.getHint();
                    }
                }
                if (description != null) {
//                    sb.append(",");
                    sb.append("       ");
                    sb.append("       ");
                    sb.append("       ");
                    sb.append(description);
                }
            }
        }
        return (sb.length() > 0) ? sb.deleteCharAt(0).toString() : "";
    }

    private static final String ID_DELIM = ",";
    private static final String ID_DEFTYPE = "id";
}