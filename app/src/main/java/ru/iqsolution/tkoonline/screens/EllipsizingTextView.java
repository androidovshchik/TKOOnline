package ru.iqsolution.tkoonline.screens;

import android.content.Context;
import android.text.*;
import android.text.Layout.Alignment;
import android.util.AttributeSet;
import android.widget.TextView;

public class EllipsizingTextView extends TextView {

    public static final int ALL_AVAILABLE = -1;

    private int mMaxLines = 2;

    public EllipsizingTextView(Context context) {
        this(context, null);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setMaxLines(int maxlines) {
        super.setMaxLines(maxlines);
        mMaxLines = maxlines;
    }

    public CharSequence setText(final CharSequence text, int avail) {
        if (text == null || text.length() == 0) {
            return text;
        }
        setEllipsize(null);
        setText(text);
        if (avail == ALL_AVAILABLE) {
            return text;
        }
        Layout layout = getLayout();
        if (layout == null) {
            final int w = getWidth() - getCompoundPaddingLeft() - getCompoundPaddingRight();
            layout = new StaticLayout(text, 0, text.length(), getPaint(), w, Alignment.ALIGN_NORMAL,
                    1.0f, 0f, false);
        }
        // find the last line of text and chop it according to available space
        final int lastLineStart = layout.getLineStart(mMaxLines - 1);
        final CharSequence remainder = TextUtils.ellipsize(text.subSequence(lastLineStart,
                text.length()), getPaint(), avail, TextUtils.TruncateAt.END);
        // assemble just the text portion, without spans
        final SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text.toString(), 0, lastLineStart);
        if (!TextUtils.isEmpty(remainder)) {
            builder.append(remainder.toString());
        }
        // Now copy the original spans into the assembled string, modified for any ellipsizing.
        //
        // Merely assembling the Spanned pieces together would result in duplicate CharacterStyle
        // spans in the assembled version if a CharacterStyle spanned across the lastLineStart
        // offset.
        if (text instanceof Spanned) {
            final Spanned s = (Spanned) text;
            final Object[] spans = s.getSpans(0, s.length(), Object.class);
            final int destLen = builder.length();
            for (int i = 0; i < spans.length; i++) {
                final Object span = spans[i];
                final int start = s.getSpanStart(span);
                final int end = s.getSpanEnd(span);
                final int flags = s.getSpanFlags(span);
                if (start <= destLen) {
                    builder.setSpan(span, start, Math.min(end, destLen), flags);
                }
            }
        }
        setText(builder);
        return builder;
    }
}