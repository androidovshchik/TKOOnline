package ru.iqsolution.tkoonline.screens.common;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * https://github.com/IPL/MultiplelineEllipsizeTextView
 */
public class EllipsizingTextView extends TextView {

    private static final String ELLIPSIS = "...";

    private boolean isStale = true;
    private boolean programmaticChange;
    private String fullText;
    private float lineSpacingMultiplier = 1.0f;
    private float lineAdditionalVerticalPadding = 0.0f;

    public EllipsizingTextView(Context context) {
        super(context);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        this.lineAdditionalVerticalPadding = add;
        this.lineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!programmaticChange) {
            fullText = text.toString();
            isStale = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStale) {
            resetText();
        }
        super.onDraw(canvas);
    }

    private void resetText() {
        int maxLines = getMaxLines();
        String workingText = fullText;
        if (maxLines != -1) {
            Layout layout = createWorkingLayout(workingText);
            int originalLineCount = layout.getLineCount();
            if (originalLineCount > maxLines) {
                if (getEllipsize() == TextUtils.TruncateAt.START) {
                    workingText = fullText.substring(layout.getLineStart(originalLineCount - maxLines - 1)).trim();
                    while (createWorkingLayout(ELLIPSIS + workingText).getLineCount() > maxLines) {
                        int firstSpace = workingText.indexOf(' ');
                        if (firstSpace == -1) {
                            workingText = workingText.substring(1);
                        } else {
                            workingText = workingText.substring(firstSpace + 1);
                        }
                    }
                    workingText = ELLIPSIS + workingText;
                }
            }
        }
        if (!workingText.contentEquals(getText())) {
            programmaticChange = true;
            try {
                setText(workingText);
            } finally {
                programmaticChange = false;
            }
        }
        isStale = false;
    }

    @SuppressWarnings("deprecation")
    private Layout createWorkingLayout(String workingText) {
        return new StaticLayout(workingText, getPaint(), getWidth() - getPaddingLeft() - getPaddingRight(),
                Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineAdditionalVerticalPadding, false);
    }
}