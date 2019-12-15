package com.robin.pinui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.AppCompatEditText;


public class PinEditText extends AppCompatEditText {
    public static final String XML_NAMESPACE_ANDROID = "http://schemas.android.com/apk/res/android";

    private float mSpace = 16;
    private float mCharSize;
    private float mNumChars = 4;
    private float mLineSpacing = 5;
    private int mMaxLength = 4;

    private OnClickListener mClickListener;

    private float mLineStroke = 3;
    private float mLineStrokeSelected = 3;
    private Paint mLinesPaint;
    int[][] mStates = new int[][]{
            new int[]{android.R.attr.state_selected}, // selected
            new int[]{android.R.attr.state_focused}, // focused
            new int[]{-android.R.attr.state_focused}, // unfocused
    };

    int[] mColors = new int[]{
            Color.GRAY,
            Color.BLACK,
            Color.GRAY
    };

    ColorStateList mColorStates = new ColorStateList(mStates, mColors);
    private boolean lineColor;

    public PinEditText(Context context) {
        super(context);
    }

    public PinEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PinEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }



    private void init(Context context, AttributeSet attrs) {
        float multi = context.getResources().getDisplayMetrics().density;
        mLineStroke = multi * mLineStroke;
        mLineStrokeSelected = multi * mLineStrokeSelected;
        mLinesPaint = new Paint(getPaint());
        mLinesPaint.setStrokeWidth(mLineStroke);

        if (!isInEditMode()) {
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.colorControlActivated,
                    outValue, true);
            final int colorActivated = outValue.data;
            mColors[0] = colorActivated;

            context.getTheme().resolveAttribute(R.attr.colorPrimaryDark,
                    outValue, true);
            final int colorDark = outValue.data;
            mColors[1] = colorDark;

            context.getTheme().resolveAttribute(R.attr.colorControlHighlight,
                    outValue, true);
            final int colorHighlight = outValue.data;
            mColors[2] = colorHighlight;
        }
        setBackgroundResource(0);
        mSpace = multi * mSpace; //convert to pixels for our density
        mLineSpacing = multi * mLineSpacing; //convert to pixels for our density

        mMaxLength = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "length", 4);
        mNumChars = mMaxLength;

        //Disable copy paste
        super.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        // When tapped, move cursor to end of text.
        super.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelection(getText().length());
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
        setLineAnimation();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        mClickListener = l;
    }

    @Override
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        throw new RuntimeException("setCustomSelectionActionModeCallback() not supported.");
    }

    Canvas canvasDraw;

    @Override
    protected void onDraw( Canvas canvas) {
        canvasDraw = canvas;
        //super.onDraw(canvas);
        int availableWidth = getWidth() - getPaddingRight() - getPaddingLeft();

        if (mSpace < 0) {

            mCharSize = (availableWidth / (mNumChars * 2 - 1));

        } else {

            mCharSize = (availableWidth - (mSpace * (mNumChars - 1))) / mNumChars;

        }

        int startX = getPaddingLeft();
        final int bottom = getHeight() - getPaddingBottom();
        int charLength = mMaxLength;
        StringBuilder sb = new StringBuilder();
        while (charLength > 0) {
            sb.append("\u25CF");
            charLength--;

        }


        Editable text = getText();
        int textLength = text.length();
        float[] textWidths = new float[textLength];
        getPaint().getTextWidths(getText(), 0, textLength, textWidths);
        float[] hintTextWidths = new float[mMaxLength];
        getPaint().getTextWidths(sb, 0, mMaxLength, hintTextWidths);


        for (int i = 0; i < mNumChars; i++) {
            updateColorForLines(i == textLength);

            if (getText().length() > i) {

                mLinesPaint.setColor(getColorForState(android.R.attr.state_selected));
                canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mLinesPaint);
                float middle = startX + mCharSize / 2;
                canvas.drawText(text, i, i + 1, middle - textWidths[0] / 2, bottom - mLineSpacing, getPaint());

            } else {
                float middle = startX + mCharSize / 2;
                mLinesPaint.setColor(getColorForState(-android.R.attr.state_focused));
                canvas.drawLine(startX, bottom, startX + mCharSize, bottom, mLinesPaint);
                canvas.drawText(sb, i, i + 1, middle - hintTextWidths[0] / 2, bottom - (mLineSpacing * 2), mLinesPaint);
                if (getText().length() == i) {
                    if(lineColor) {
                        mLinesPaint.setColor(getColorForState(android.R.attr.state_selected));
                        canvas.drawLine(startX, bottom - (mLineSpacing * 1), startX, bottom - (mLineSpacing * 5), mLinesPaint);
                    }
                    else {
                        mLinesPaint.setColor(getColorForState(-android.R.attr.state_focused));
                        canvas.drawLine(startX, bottom - (mLineSpacing * 1), startX, bottom - (mLineSpacing * 5), mLinesPaint);
                    }


                }
            }

            if (mSpace < 0) {
                startX += mCharSize * 2;
            } else {
                startX += mCharSize + mSpace;
            }
        }
    }

    private void setLineAnimation(){

        postDelayed(new Runnable() {
            @Override
            public void run() {
                lineColor = !lineColor;
                invalidate();
                postDelayed(this, 500);
            }
        }, 500);
    }


    private int getColorForState(int... states) {
        return mColorStates.getColorForState(states, Color.parseColor("#D8D8D8"));
    }

    private void updateColorForLines(boolean next) {
        if (isFocused()) {
            mLinesPaint.setStrokeWidth(mLineStrokeSelected);
            mLinesPaint.setColor(getColorForState(android.R.attr.state_focused));
            if (next) {
                mLinesPaint.setColor(getColorForState(android.R.attr.state_selected));
            }
        } else {
            mLinesPaint.setStrokeWidth(mLineStroke);
            mLinesPaint.setColor(getColorForState(-android.R.attr.state_focused));
        }
    }
}
