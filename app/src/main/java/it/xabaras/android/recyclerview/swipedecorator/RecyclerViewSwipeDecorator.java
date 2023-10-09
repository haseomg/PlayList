//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package it.xabaras.android.recyclerview.swipedecorator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class RecyclerViewSwipeDecorator {
    private Canvas canvas;
    private RecyclerView recyclerView;
    private ViewHolder viewHolder;
    private float dX;
    private float dY;
    private int actionState;
    private boolean isCurrentlyActive;
    private int swipeLeftBackgroundColor;
    private int swipeLeftActionIconId;
    private Integer swipeLeftActionIconTint;
    private int swipeRightBackgroundColor;
    private int swipeRightActionIconId;
    private Integer swipeRightActionIconTint;
    private int iconHorizontalMargin;
    private String mSwipeLeftText;
    private float mSwipeLeftTextSize;
    private int mSwipeLeftTextUnit;
    private int mSwipeLeftTextColor;
    private Typeface mSwipeLeftTypeface;
    private String mSwipeRightText;
    private float mSwipeRightTextSize;
    private int mSwipeRightTextUnit;
    private int mSwipeRightTextColor;
    private Typeface mSwipeRightTypeface;

    private RecyclerViewSwipeDecorator() {
        this.mSwipeLeftTextSize = 14.0F;
        this.mSwipeLeftTextUnit = 2;
        this.mSwipeLeftTextColor = -12303292;
        this.mSwipeLeftTypeface = Typeface.SANS_SERIF;
        this.mSwipeRightTextSize = 14.0F;
        this.mSwipeRightTextUnit = 2;
        this.mSwipeRightTextColor = -12303292;
        this.mSwipeRightTypeface = Typeface.SANS_SERIF;
        this.swipeLeftBackgroundColor = 0;
        this.swipeRightBackgroundColor = 0;
        this.swipeLeftActionIconId = 0;
        this.swipeRightActionIconId = 0;
        this.swipeLeftActionIconTint = null;
        this.swipeRightActionIconTint = null;
    }

    /** @deprecated */
    @Deprecated
    public RecyclerViewSwipeDecorator(Context context, Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        this(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    public RecyclerViewSwipeDecorator(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        this();
        this.canvas = canvas;
        this.recyclerView = recyclerView;
        this.viewHolder = viewHolder;
        this.dX = dX;
        this.dY = dY;
        this.actionState = actionState;
        this.isCurrentlyActive = isCurrentlyActive;
        this.iconHorizontalMargin = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0F, recyclerView.getContext().getResources().getDisplayMetrics());
    }

    public void setBackgroundColor(int backgroundColor) {
        this.swipeLeftBackgroundColor = backgroundColor;
        this.swipeRightBackgroundColor = backgroundColor;
    }

    public void setActionIconId(int actionIconId) {
        this.swipeLeftActionIconId = actionIconId;
        this.swipeRightActionIconId = actionIconId;
    }

    public void setActionIconTint(int color) {
        this.setSwipeLeftActionIconTint(color);
        this.setSwipeRightActionIconTint(color);
    }

    public void setSwipeLeftBackgroundColor(int swipeLeftBackgroundColor) {
        this.swipeLeftBackgroundColor = swipeLeftBackgroundColor;
    }

    public void setSwipeLeftActionIconId(int swipeLeftActionIconId) {
        this.swipeLeftActionIconId = swipeLeftActionIconId;
    }

    public void setSwipeLeftActionIconTint(int color) {
        this.swipeLeftActionIconTint = color;
    }

    public void setSwipeRightBackgroundColor(int swipeRightBackgroundColor) {
        this.swipeRightBackgroundColor = swipeRightBackgroundColor;
    }

    public void setSwipeRightActionIconId(int swipeRightActionIconId) {
        this.swipeRightActionIconId = swipeRightActionIconId;
    }

    public void setSwipeRightActionIconTint(int color) {
        this.swipeRightActionIconTint = color;
    }

    public void setSwipeRightLabel(String label) {
        this.mSwipeRightText = label;
    }

    public void setSwipeRightTextSize(int unit, float size) {
        this.mSwipeRightTextUnit = unit;
        this.mSwipeRightTextSize = size;
    }

    public void setSwipeRightTextColor(int color) {
        this.mSwipeRightTextColor = color;
    }

    public void setSwipeRightTypeface(Typeface typeface) {
        this.mSwipeRightTypeface = typeface;
    }

    /** @deprecated */
    @Deprecated
    public void setIconHorizontalMargin(int iconHorizontalMargin) {
        this.setIconHorizontalMargin(1, iconHorizontalMargin);
    }

    public void setIconHorizontalMargin(int unit, int iconHorizontalMargin) {
        this.iconHorizontalMargin = (int)TypedValue.applyDimension(unit, (float)iconHorizontalMargin, this.recyclerView.getContext().getResources().getDisplayMetrics());
    }

    public void setSwipeLeftLabel(String label) {
        this.mSwipeLeftText = label;
    }

    public void setSwipeLeftTextSize(int unit, float size) {
        this.mSwipeLeftTextUnit = unit;
        this.mSwipeLeftTextSize = size;
    }

    public void setSwipeLeftTextColor(int color) {
        this.mSwipeLeftTextColor = color;
    }

    public void setSwipeLeftTypeface(Typeface typeface) {
        this.mSwipeLeftTypeface = typeface;
    }

    public void decorate() {
        try {
            if (this.actionState != 1) {
                return;
            }

            ColorDrawable background;
            int halfIcon;
            int iconSize;
            if (this.dX > 0.0F) {
                if (this.swipeRightBackgroundColor != 0) {
                    background = new ColorDrawable(this.swipeRightBackgroundColor);
                    background.setBounds(this.viewHolder.itemView.getLeft(), this.viewHolder.itemView.getTop(), this.viewHolder.itemView.getLeft() + (int)this.dX, this.viewHolder.itemView.getBottom());
                    background.draw(this.canvas);
                }

                iconSize = 0;
                int textTop;
                if (this.swipeRightActionIconId != 0 && this.dX > (float)this.iconHorizontalMargin) {
                    Drawable icon = ContextCompat.getDrawable(this.recyclerView.getContext(), this.swipeRightActionIconId);
                    if (icon != null) {
                        iconSize = icon.getIntrinsicHeight();
                        textTop = iconSize / 2;
                        halfIcon = this.viewHolder.itemView.getTop() + ((this.viewHolder.itemView.getBottom() - this.viewHolder.itemView.getTop()) / 2 - textTop);
                        icon.setBounds(this.viewHolder.itemView.getLeft() + this.iconHorizontalMargin, halfIcon, this.viewHolder.itemView.getLeft() + this.iconHorizontalMargin + icon.getIntrinsicWidth(), halfIcon + icon.getIntrinsicHeight());
                        if (this.swipeRightActionIconTint != null) {
                            icon.setColorFilter(this.swipeRightActionIconTint, Mode.SRC_IN);
                        }

                        icon.draw(this.canvas);
                    }
                }

                if (this.mSwipeRightText != null && this.mSwipeRightText.length() > 0 && this.dX > (float)(this.iconHorizontalMargin + iconSize)) {
                    TextPaint textPaint = new TextPaint();
                    textPaint.setAntiAlias(true);
                    textPaint.setTextSize(TypedValue.applyDimension(this.mSwipeRightTextUnit, this.mSwipeRightTextSize, this.recyclerView.getContext().getResources().getDisplayMetrics()));
                    textPaint.setColor(this.mSwipeRightTextColor);
                    textPaint.setTypeface(this.mSwipeRightTypeface);
                    textTop = (int)((double)this.viewHolder.itemView.getTop() + (double)(this.viewHolder.itemView.getBottom() - this.viewHolder.itemView.getTop()) / 2.0D + (double)(textPaint.getTextSize() / 2.0F));
                    this.canvas.drawText(this.mSwipeRightText, (float)(this.viewHolder.itemView.getLeft() + this.iconHorizontalMargin + iconSize + (iconSize > 0 ? this.iconHorizontalMargin / 2 : 0)), (float)textTop, textPaint);
                }
            } else if (this.dX < 0.0F) {
                if (this.swipeLeftBackgroundColor != 0) {
                    background = new ColorDrawable(this.swipeLeftBackgroundColor);
                    background.setBounds(this.viewHolder.itemView.getRight() + (int)this.dX, this.viewHolder.itemView.getTop(), this.viewHolder.itemView.getRight(), this.viewHolder.itemView.getBottom());
                    background.draw(this.canvas);
                }

                iconSize = 0;
                int imgLeft = this.viewHolder.itemView.getRight();
                int textTop;
                if (this.swipeLeftActionIconId != 0 && this.dX < (float)(-this.iconHorizontalMargin)) {
                    Drawable icon = ContextCompat.getDrawable(this.recyclerView.getContext(), this.swipeLeftActionIconId);
                    if (icon != null) {
                        iconSize = icon.getIntrinsicHeight();
                        halfIcon = iconSize / 2;
                        textTop = this.viewHolder.itemView.getTop() + ((this.viewHolder.itemView.getBottom() - this.viewHolder.itemView.getTop()) / 2 - halfIcon);
                        imgLeft = this.viewHolder.itemView.getRight() - this.iconHorizontalMargin - halfIcon * 2;
                        icon.setBounds(imgLeft, textTop, this.viewHolder.itemView.getRight() - this.iconHorizontalMargin, textTop + icon.getIntrinsicHeight());
                        if (this.swipeLeftActionIconTint != null) {
                            icon.setColorFilter(this.swipeLeftActionIconTint, Mode.SRC_IN);
                        }

                        icon.draw(this.canvas);
                    }
                }

                if (this.mSwipeLeftText != null && this.mSwipeLeftText.length() > 0 && this.dX < (float)(-this.iconHorizontalMargin - iconSize)) {
                    TextPaint textPaint = new TextPaint();
                    textPaint.setAntiAlias(true);
                    textPaint.setTextSize(TypedValue.applyDimension(this.mSwipeLeftTextUnit, this.mSwipeLeftTextSize, this.recyclerView.getContext().getResources().getDisplayMetrics()));
                    textPaint.setColor(this.mSwipeLeftTextColor);
                    textPaint.setTypeface(this.mSwipeLeftTypeface);
                    float width = textPaint.measureText(this.mSwipeLeftText);
                    textTop = (int)((double)this.viewHolder.itemView.getTop() + (double)(this.viewHolder.itemView.getBottom() - this.viewHolder.itemView.getTop()) / 2.0D + (double)(textPaint.getTextSize() / 2.0F));
                    this.canvas.drawText(this.mSwipeLeftText, (float)imgLeft - width - (float)(imgLeft == this.viewHolder.itemView.getRight() ? this.iconHorizontalMargin : this.iconHorizontalMargin / 2), (float)textTop, textPaint);
                }
            }
        } catch (Exception var6) {
            Log.e(this.getClass().getName(), var6.getMessage());
        }

    }

    public static class Builder {
        private RecyclerViewSwipeDecorator mDecorator;

        /** @deprecated */
        @Deprecated
        public Builder(Context context, Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            this(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public Builder(Canvas canvas, RecyclerView recyclerView, ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            this.mDecorator = new RecyclerViewSwipeDecorator(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public RecyclerViewSwipeDecorator.Builder addBackgroundColor(int color) {
            this.mDecorator.setBackgroundColor(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addActionIcon(int drawableId) {
            this.mDecorator.setActionIconId(drawableId);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setActionIconTint(int color) {
            this.mDecorator.setActionIconTint(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addSwipeRightBackgroundColor(int color) {
            this.mDecorator.setSwipeRightBackgroundColor(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addSwipeRightActionIcon(int drawableId) {
            this.mDecorator.setSwipeRightActionIconId(drawableId);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeRightActionIconTint(int color) {
            this.mDecorator.setSwipeRightActionIconTint(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addSwipeRightLabel(String label) {
            this.mDecorator.setSwipeRightLabel(label);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeRightLabelColor(int color) {
            this.mDecorator.setSwipeRightTextColor(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeRightLabelTextSize(int unit, float size) {
            this.mDecorator.setSwipeRightTextSize(unit, size);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeRightLabelTypeface(Typeface typeface) {
            this.mDecorator.setSwipeRightTypeface(typeface);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addSwipeLeftBackgroundColor(int color) {
            this.mDecorator.setSwipeLeftBackgroundColor(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addSwipeLeftActionIcon(int drawableId) {
            this.mDecorator.setSwipeLeftActionIconId(drawableId);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeLeftActionIconTint(int color) {
            this.mDecorator.setSwipeLeftActionIconTint(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder addSwipeLeftLabel(String label) {
            this.mDecorator.setSwipeLeftLabel(label);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeLeftLabelColor(int color) {
            this.mDecorator.setSwipeLeftTextColor(color);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeLeftLabelTextSize(int unit, float size) {
            this.mDecorator.setSwipeLeftTextSize(unit, size);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setSwipeLeftLabelTypeface(Typeface typeface) {
            this.mDecorator.setSwipeLeftTypeface(typeface);
            return this;
        }

        /** @deprecated */
        @Deprecated
        public RecyclerViewSwipeDecorator.Builder setIconHorizontalMargin(int pixels) {
            this.mDecorator.setIconHorizontalMargin(pixels);
            return this;
        }

        public RecyclerViewSwipeDecorator.Builder setIconHorizontalMargin(int unit, int iconHorizontalMargin) {
            this.mDecorator.setIconHorizontalMargin(unit, iconHorizontalMargin);
            return this;
        }

        public RecyclerViewSwipeDecorator create() {
            return this.mDecorator;
        }
    }
}
