package androidx.constraintlayout.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.R;
import androidx.constraintlayout.solver.Metrics;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.solver.widgets.Guideline;
import androidx.constraintlayout.solver.widgets.VirtualLayout;
import androidx.constraintlayout.solver.widgets.analyzer.BasicMeasure;
import java.util.ArrayList;
import java.util.HashMap;
@SuppressLint("WrongConstant")
public class ConstraintLayout extends ViewGroup {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_DRAW_CONSTRAINTS = false;
    public static final int DESIGN_INFO_ID = 0;
    private static final String TAG = "ConstraintLayout";
    private static final boolean USE_CONSTRAINTS_HELPER = true;
    public static final String VERSION = "ConstraintLayout-2.0-beta8";
    SparseArray<View> mChildrenByIds = new SparseArray<>();
    /* access modifiers changed from: private */
    public ArrayList<ConstraintHelper> mConstraintHelpers = new ArrayList<>(4);
    protected ConstraintLayoutStates mConstraintLayoutSpec = null;
    private ConstraintSet mConstraintSet = null;
    private int mConstraintSetId = -1;
    private ConstraintsChangedListener mConstraintsChangedListener;
    private HashMap<String, Integer> mDesignIds = new HashMap<>();
    protected boolean mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    private int mLastMeasureHeight = -1;
    int mLastMeasureHeightMode = 0;
    int mLastMeasureHeightSize = -1;
    private int mLastMeasureWidth = -1;
    int mLastMeasureWidthMode = 0;
    int mLastMeasureWidthSize = -1;
    protected ConstraintWidgetContainer mLayoutWidget = new ConstraintWidgetContainer();
    private int mMaxHeight = Integer.MAX_VALUE;
    private int mMaxWidth = Integer.MAX_VALUE;
    Measurer mMeasurer = new Measurer(this);
    private Metrics mMetrics;
    private int mMinHeight = 0;
    private int mMinWidth = 0;
    /* access modifiers changed from: private */
    public int mOnMeasureHeightMeasureSpec = 0;
    /* access modifiers changed from: private */
    public int mOnMeasureWidthMeasureSpec = 0;
    private int mOptimizationLevel = 7;
    private SparseArray<ConstraintWidget> mTempMapIdToWidget = new SparseArray<>();

    public void setDesignInformation(int type, Object value1, Object value2) {
        if (type == 0 && (value1 instanceof String) && (value2 instanceof Integer)) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<>();
            }
            String name = (String) value1;
            int index = name.indexOf("/");
            if (index != -1) {
                name = name.substring(index + 1);
            }
            this.mDesignIds.put(name, Integer.valueOf(((Integer) value2).intValue()));
        }
    }

    public Object getDesignInformation(int type, Object value) {
        if (type == 0 && (value instanceof String)) {
            String name = (String) value;
            if (this.mDesignIds != null && this.mDesignIds.containsKey(name)) {
                return this.mDesignIds.get(name);
            }
        }
        return null;
    }

    public ConstraintLayout(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public ConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @TargetApi(LayoutParams.Table.LAYOUT_GONE_MARGIN_LEFT)
    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    public void setId(int id) {
        this.mChildrenByIds.remove(getId());
        super.setId(id);
        this.mChildrenByIds.put(getId(), this);
    }

    class Measurer implements BasicMeasure.Measurer {
        ConstraintLayout layout;

        public Measurer(ConstraintLayout l) {
            this.layout = l;
        }

        @SuppressLint({"WrongCall"})
        public final void measure(ConstraintWidget widget, BasicMeasure.Measure measure) {
            if (widget != null) {
                if (widget.getVisibility() != 8 || widget.isInPlaceholder()) {
                    ConstraintWidget.DimensionBehaviour horizontalBehavior = measure.horizontalBehavior;
                    ConstraintWidget.DimensionBehaviour verticalBehavior = measure.verticalBehavior;
                    int horizontalDimension = measure.horizontalDimension;
                    int verticalDimension = measure.verticalDimension;
                    int horizontalSpec = 0;
                    int verticalSpec = 0;
                    int heightPadding = this.layout.getPaddingTop() + this.layout.getPaddingBottom();
                    int widthPadding = this.layout.getPaddingWidth();
                    boolean didHorizontalWrap = false;
                    boolean didVerticalWrap = false;
                    switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[horizontalBehavior.ordinal()]) {
                        case 1:
                            horizontalSpec = View.MeasureSpec.makeMeasureSpec(horizontalDimension, 1073741824);
                            break;
                        case 2:
                            horizontalSpec = ViewGroup.getChildMeasureSpec(this.layout.mOnMeasureWidthMeasureSpec, widthPadding, -2);
                            didHorizontalWrap = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                            break;
                        case 3:
                            horizontalSpec = ViewGroup.getChildMeasureSpec(this.layout.mOnMeasureWidthMeasureSpec, widget.getHorizontalMargin() + widthPadding, -1);
                            break;
                        case 4:
                            horizontalSpec = ViewGroup.getChildMeasureSpec(this.layout.mOnMeasureWidthMeasureSpec, widthPadding, -2);
                            didHorizontalWrap = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                            boolean shouldDoWrap = widget.mMatchConstraintDefaultWidth == 1 ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                            if (measure.useDeprecated && (!shouldDoWrap || (shouldDoWrap && widget.wrapMeasure[0] != widget.getWidth()))) {
                                horizontalSpec = View.MeasureSpec.makeMeasureSpec(widget.getWidth(), 1073741824);
                                didHorizontalWrap = false;
                                break;
                            }
                    }
                    switch (AnonymousClass1.$SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[verticalBehavior.ordinal()]) {
                        case 1:
                            verticalSpec = View.MeasureSpec.makeMeasureSpec(verticalDimension, 1073741824);
                            break;
                        case 2:
                            verticalSpec = ViewGroup.getChildMeasureSpec(this.layout.mOnMeasureHeightMeasureSpec, heightPadding, -2);
                            didVerticalWrap = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                            break;
                        case 3:
                            verticalSpec = ViewGroup.getChildMeasureSpec(this.layout.mOnMeasureHeightMeasureSpec, widget.getVerticalMargin() + heightPadding, -1);
                            break;
                        case 4:
                            verticalSpec = ViewGroup.getChildMeasureSpec(this.layout.mOnMeasureHeightMeasureSpec, heightPadding, -2);
                            didVerticalWrap = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                            boolean shouldDoWrap2 = widget.mMatchConstraintDefaultHeight == 1 ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                            if (measure.useDeprecated && (!shouldDoWrap2 || (shouldDoWrap2 && widget.wrapMeasure[1] != widget.getHeight()))) {
                                verticalSpec = View.MeasureSpec.makeMeasureSpec(widget.getHeight(), 1073741824);
                                didVerticalWrap = false;
                                break;
                            }
                    }
                    boolean horizontalMatchConstraints = horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                    boolean verticalMatchConstraints = verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                    boolean verticalDimensionKnown = (verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT || verticalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                    boolean horizontalDimensionKnown = (horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_PARENT || horizontalBehavior == ConstraintWidget.DimensionBehaviour.FIXED) ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                    boolean horizontalUseRatio = (!horizontalMatchConstraints || widget.mDimensionRatio <= 0.0f) ? false : ConstraintLayout.USE_CONSTRAINTS_HELPER;
                    boolean verticalUseRatio = (!verticalMatchConstraints || widget.mDimensionRatio <= 0.0f) ? false : ConstraintLayout.USE_CONSTRAINTS_HELPER;
                    View child = (View) widget.getCompanionWidget();
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    int width = 0;
                    int height = 0;
                    int baseline = 0;
                    if (measure.useDeprecated || !horizontalMatchConstraints || widget.mMatchConstraintDefaultWidth != 0 || !verticalMatchConstraints || widget.mMatchConstraintDefaultHeight != 0) {
                        if (child instanceof androidx.constraintlayout.widget.VirtualLayout && widget instanceof androidx.constraintlayout.solver.widgets.VirtualLayout) {
                            androidx.constraintlayout.solver.widgets.VirtualLayout layout = (androidx.constraintlayout.solver.widgets.VirtualLayout)widget;
                            ((androidx.constraintlayout.widget.VirtualLayout)child).onMeasure(layout, horizontalSpec, verticalSpec);
                        } else {
                            child.measure(horizontalSpec, verticalSpec);
                        }
                        int w = child.getMeasuredWidth();
                        int h = child.getMeasuredHeight();
                        baseline = child.getBaseline();
                        width = w;
                        height = h;
                        if (didHorizontalWrap) {
                            widget.wrapMeasure[0] = w;
                        }
                        if (didVerticalWrap) {
                            widget.wrapMeasure[1] = h;
                        }
                        if (widget.mMatchConstraintMinWidth > 0) {
                            width = Math.max(widget.mMatchConstraintMinWidth, width);
                        }
                        if (widget.mMatchConstraintMaxWidth > 0) {
                            width = Math.min(widget.mMatchConstraintMaxWidth, width);
                        }
                        if (widget.mMatchConstraintMinHeight > 0) {
                            height = Math.max(widget.mMatchConstraintMinHeight, height);
                        }
                        if (widget.mMatchConstraintMaxHeight > 0) {
                            height = Math.min(widget.mMatchConstraintMaxHeight, height);
                        }
                        if (horizontalUseRatio && verticalDimensionKnown) {
                            width = (int) (0.5f + (((float) height) * widget.mDimensionRatio));
                        } else if (verticalUseRatio && horizontalDimensionKnown) {
                            height = (int) (0.5f + (((float) width) / widget.mDimensionRatio));
                        }
                        if (!(w == width && h == height)) {
                            if (w != width) {
                                horizontalSpec = View.MeasureSpec.makeMeasureSpec(width, 1073741824);
                            }
                            if (h != height) {
                                verticalSpec = View.MeasureSpec.makeMeasureSpec(height, 1073741824);
                            }
                            child.measure(horizontalSpec, verticalSpec);
                            width = child.getMeasuredWidth();
                            height = child.getMeasuredHeight();
                            baseline = child.getBaseline();
                        }
                    }
                    boolean hasBaseline = baseline != -1 ? ConstraintLayout.USE_CONSTRAINTS_HELPER : false;
                    measure.measuredNeedsSolverPass = (width == measure.horizontalDimension && height == measure.verticalDimension) ? false : ConstraintLayout.USE_CONSTRAINTS_HELPER;
                    if (params.needsBaseline) {
                        hasBaseline = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                    }
                    if (!(!hasBaseline || baseline == -1 || widget.getBaselineDistance() == baseline)) {
                        measure.measuredNeedsSolverPass = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                    }
                    measure.measuredWidth = width;
                    measure.measuredHeight = height;
                    measure.measuredHasBaseline = hasBaseline;
                    measure.measuredBaseline = baseline;
                    return;
                }
                measure.measuredWidth = 0;
                measure.measuredHeight = 0;
                measure.measuredBaseline = 0;
            }
        }

        public final void didMeasures() {
            int widgetsCount = this.layout.getChildCount();
            for (int i = 0; i < widgetsCount; i++) {
                View child = this.layout.getChildAt(i);
                if (child instanceof Placeholder) {
                    ((Placeholder) child).updatePostMeasure(this.layout);
                }
            }
            int helperCount = this.layout.mConstraintHelpers.size();
            if (helperCount > 0) {
                for (int i2 = 0; i2 < helperCount; i2++) {
                    ((ConstraintHelper) this.layout.mConstraintHelpers.get(i2)).updatePostMeasure(this.layout);
                }
            }
        }
    }

    /* renamed from: androidx.constraintlayout.widget.ConstraintLayout$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour = new int[ConstraintWidget.DimensionBehaviour.values().length];

        static {
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[ConstraintWidget.DimensionBehaviour.FIXED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[ConstraintWidget.DimensionBehaviour.WRAP_CONTENT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[ConstraintWidget.DimensionBehaviour.MATCH_PARENT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$androidx$constraintlayout$solver$widgets$ConstraintWidget$DimensionBehaviour[ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mLayoutWidget.setMeasurer(this.mMeasurer);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout, defStyleAttr, defStyleRes);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = a.getDimensionPixelOffset(attr, this.mMinWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = a.getDimensionPixelOffset(attr, this.mMinHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = a.getDimensionPixelOffset(attr, this.mMaxWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = a.getDimensionPixelOffset(attr, this.mMaxHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = a.getInt(attr, this.mOptimizationLevel);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layoutDescription) {
                    int id = a.getResourceId(attr, 0);
                    if (id != 0) {
                        try {
                            parseLayoutDescription(id);
                        } catch (Resources.NotFoundException e) {
                            this.mConstraintLayoutSpec = null;
                        }
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_constraintSet) {
                    int id2 = a.getResourceId(attr, 0);
                    try {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.load(getContext(), id2);
                    } catch (Resources.NotFoundException e2) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = id2;
                }
            }
            a.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    /* access modifiers changed from: protected */
    public void parseLayoutDescription(int id) {
        this.mConstraintLayoutSpec = new ConstraintLayoutStates(getContext(), this, id);
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (Build.VERSION.SDK_INT < 14) {
            onViewAdded(child);
        }
    }

    public void removeView(View view) {
        super.removeView(view);
        if (Build.VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    public void onViewAdded(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget widget = getViewWidget(view);
        if (view instanceof androidx.constraintlayout.widget.Guideline && !(widget instanceof androidx.constraintlayout.solver.widgets.Guideline)) {
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)view.getLayoutParams();
            layoutParams.widget = new androidx.constraintlayout.solver.widgets.Guideline();
            layoutParams.isGuideline = true;
            ((androidx.constraintlayout.solver.widgets.Guideline)layoutParams.widget).setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            ConstraintHelper helper = (ConstraintHelper) view;
            helper.validateParams();
            ((LayoutParams) view.getLayoutParams()).isHelper = USE_CONSTRAINTS_HELPER;
            if (!this.mConstraintHelpers.contains(helper)) {
                this.mConstraintHelpers.add(helper);
            }
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void onViewRemoved(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        this.mLayoutWidget.remove(getViewWidget(view));
        this.mConstraintHelpers.remove(view);
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
    }

    public void setMinWidth(int value) {
        if (value != this.mMinWidth) {
            this.mMinWidth = value;
            requestLayout();
        }
    }

    public void setMinHeight(int value) {
        if (value != this.mMinHeight) {
            this.mMinHeight = value;
            requestLayout();
        }
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setMaxWidth(int value) {
        if (value != this.mMaxWidth) {
            this.mMaxWidth = value;
            requestLayout();
        }
    }

    public void setMaxHeight(int value) {
        if (value != this.mMaxHeight) {
            this.mMaxHeight = value;
            requestLayout();
        }
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    private boolean updateHierarchy() {
        int count = getChildCount();
        boolean recompute = false;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            } else if (getChildAt(i).isLayoutRequested()) {
                recompute = USE_CONSTRAINTS_HELPER;
                break;
            } else {
                i++;
            }
        }
        if (recompute) {
            setChildrenConstraints();
        }
        return recompute;
    }

    private void setChildrenConstraints() {
        boolean isInEditMode = isInEditMode();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = getViewWidget(getChildAt(i));
            if (widget != null) {
                widget.reset();
            }
        }
        if (isInEditMode) {
            for (int i2 = 0; i2 < count; i2++) {
                View view = getChildAt(i2);
                try {
                    String IdAsString = getResources().getResourceName(view.getId());
                    setDesignInformation(0, IdAsString, Integer.valueOf(view.getId()));
                    int slashIndex = IdAsString.indexOf(47);
                    if (slashIndex != -1) {
                        IdAsString = IdAsString.substring(slashIndex + 1);
                    }
                    getTargetWidget(view.getId()).setDebugName(IdAsString);
                } catch (Resources.NotFoundException e) {
                }
            }
        }
        if (this.mConstraintSetId != -1) {
            for (int i3 = 0; i3 < count; i3++) {
                View child = getChildAt(i3);
                if (child.getId() == this.mConstraintSetId && (child instanceof Constraints)) {
                    this.mConstraintSet = ((Constraints) child).getConstraintSet();
                }
            }
        }
        if (this.mConstraintSet != null) {
            this.mConstraintSet.applyToInternal(this, USE_CONSTRAINTS_HELPER);
        }
        this.mLayoutWidget.removeAllChildren();
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i4 = 0; i4 < helperCount; i4++) {
                this.mConstraintHelpers.get(i4).updatePreLayout(this);
            }
        }
        for (int i5 = 0; i5 < count; i5++) {
            View child2 = getChildAt(i5);
            if (child2 instanceof Placeholder) {
                ((Placeholder) child2).updatePreLayout(this);
            }
        }
        this.mTempMapIdToWidget.clear();
        this.mTempMapIdToWidget.put(0, this.mLayoutWidget);
        this.mTempMapIdToWidget.put(getId(), this.mLayoutWidget);
        for (int i6 = 0; i6 < count; i6++) {
            View child3 = getChildAt(i6);
            this.mTempMapIdToWidget.put(child3.getId(), getViewWidget(child3));
        }
        for (int i7 = 0; i7 < count; i7++) {
            View child4 = getChildAt(i7);
            ConstraintWidget widget2 = getViewWidget(child4);
            if (widget2 != null) {
                this.mLayoutWidget.add(widget2);
                applyConstraintsFromLayoutParams(isInEditMode, child4, widget2, (LayoutParams) child4.getLayoutParams(), this.mTempMapIdToWidget);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void applyConstraintsFromLayoutParams(boolean isInEditMode, View child, ConstraintWidget widget, LayoutParams layoutParams, SparseArray<ConstraintWidget> idToWidget) {
        ConstraintWidget target;
        ConstraintWidget target2;
        ConstraintWidget target3;
        ConstraintWidget target4;
        layoutParams.validate();
        layoutParams.helped = false;
        widget.setVisibility(child.getVisibility());
        if (layoutParams.isInPlaceholder) {
            widget.setInPlaceholder((boolean) USE_CONSTRAINTS_HELPER);
            widget.setVisibility(8);
        }
        widget.setCompanionWidget(child);
        if (child instanceof ConstraintHelper) {
            ((ConstraintHelper) child).resolveRtl(widget, this.mLayoutWidget.isRtl());
        }
        if (layoutParams.isGuideline) {
            Guideline guideline = (Guideline) widget;
            int resolvedGuideBegin = layoutParams.resolvedGuideBegin;
            int resolvedGuideEnd = layoutParams.resolvedGuideEnd;
            float resolvedGuidePercent = layoutParams.resolvedGuidePercent;
            if (Build.VERSION.SDK_INT < 17) {
                resolvedGuideBegin = layoutParams.guideBegin;
                resolvedGuideEnd = layoutParams.guideEnd;
                resolvedGuidePercent = layoutParams.guidePercent;
            }
            if (resolvedGuidePercent != -1.0f) {
                guideline.setGuidePercent(resolvedGuidePercent);
            } else if (resolvedGuideBegin != -1) {
                guideline.setGuideBegin(resolvedGuideBegin);
            } else if (resolvedGuideEnd != -1) {
                guideline.setGuideEnd(resolvedGuideEnd);
            }
        } else {
            int resolvedLeftToLeft = layoutParams.resolvedLeftToLeft;
            int resolvedLeftToRight = layoutParams.resolvedLeftToRight;
            int resolvedRightToLeft = layoutParams.resolvedRightToLeft;
            int resolvedRightToRight = layoutParams.resolvedRightToRight;
            int resolveGoneLeftMargin = layoutParams.resolveGoneLeftMargin;
            int resolveGoneRightMargin = layoutParams.resolveGoneRightMargin;
            float resolvedHorizontalBias = layoutParams.resolvedHorizontalBias;
            if (Build.VERSION.SDK_INT < 17) {
                resolvedLeftToLeft = layoutParams.leftToLeft;
                resolvedLeftToRight = layoutParams.leftToRight;
                resolvedRightToLeft = layoutParams.rightToLeft;
                resolvedRightToRight = layoutParams.rightToRight;
                resolveGoneLeftMargin = layoutParams.goneLeftMargin;
                resolveGoneRightMargin = layoutParams.goneRightMargin;
                resolvedHorizontalBias = layoutParams.horizontalBias;
                if (resolvedLeftToLeft == -1 && resolvedLeftToRight == -1) {
                    if (layoutParams.startToStart != -1) {
                        resolvedLeftToLeft = layoutParams.startToStart;
                    } else if (layoutParams.startToEnd != -1) {
                        resolvedLeftToRight = layoutParams.startToEnd;
                    }
                }
                if (resolvedRightToLeft == -1 && resolvedRightToRight == -1) {
                    if (layoutParams.endToStart != -1) {
                        resolvedRightToLeft = layoutParams.endToStart;
                    } else if (layoutParams.endToEnd != -1) {
                        resolvedRightToRight = layoutParams.endToEnd;
                    }
                }
            }
            if (layoutParams.circleConstraint != -1) {
                ConstraintWidget target5 = idToWidget.get(layoutParams.circleConstraint);
                if (target5 != null) {
                    widget.connectCircularConstraint(target5, layoutParams.circleAngle, layoutParams.circleRadius);
                }
            } else {
                if (resolvedLeftToLeft != -1) {
                    ConstraintWidget target6 = idToWidget.get(resolvedLeftToLeft);
                    if (target6 != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.LEFT, target6, ConstraintAnchor.Type.LEFT, layoutParams.leftMargin, resolveGoneLeftMargin);
                    }
                } else if (!(resolvedLeftToRight == -1 || (target = idToWidget.get(resolvedLeftToRight)) == null)) {
                    widget.immediateConnect(ConstraintAnchor.Type.LEFT, target, ConstraintAnchor.Type.RIGHT, layoutParams.leftMargin, resolveGoneLeftMargin);
                }
                if (resolvedRightToLeft != -1) {
                    ConstraintWidget target7 = idToWidget.get(resolvedRightToLeft);
                    if (target7 != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.RIGHT, target7, ConstraintAnchor.Type.LEFT, layoutParams.rightMargin, resolveGoneRightMargin);
                    }
                } else if (!(resolvedRightToRight == -1 || (target2 = idToWidget.get(resolvedRightToRight)) == null)) {
                    widget.immediateConnect(ConstraintAnchor.Type.RIGHT, target2, ConstraintAnchor.Type.RIGHT, layoutParams.rightMargin, resolveGoneRightMargin);
                }
                if (layoutParams.topToTop != -1) {
                    ConstraintWidget target8 = idToWidget.get(layoutParams.topToTop);
                    if (target8 != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.TOP, target8, ConstraintAnchor.Type.TOP, layoutParams.topMargin, layoutParams.goneTopMargin);
                    }
                } else if (!(layoutParams.topToBottom == -1 || (target3 = idToWidget.get(layoutParams.topToBottom)) == null)) {
                    widget.immediateConnect(ConstraintAnchor.Type.TOP, target3, ConstraintAnchor.Type.BOTTOM, layoutParams.topMargin, layoutParams.goneTopMargin);
                }
                if (layoutParams.bottomToTop != -1) {
                    ConstraintWidget target9 = idToWidget.get(layoutParams.bottomToTop);
                    if (target9 != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.BOTTOM, target9, ConstraintAnchor.Type.TOP, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                    }
                } else if (!(layoutParams.bottomToBottom == -1 || (target4 = idToWidget.get(layoutParams.bottomToBottom)) == null)) {
                    widget.immediateConnect(ConstraintAnchor.Type.BOTTOM, target4, ConstraintAnchor.Type.BOTTOM, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                }
                if (layoutParams.baselineToBaseline != -1) {
                    View view = this.mChildrenByIds.get(layoutParams.baselineToBaseline);
                    ConstraintWidget target10 = idToWidget.get(layoutParams.baselineToBaseline);
                    if (!(target10 == null || view == null || !(view.getLayoutParams() instanceof LayoutParams))) {
                        LayoutParams targetParams = (LayoutParams) view.getLayoutParams();
                        layoutParams.needsBaseline = USE_CONSTRAINTS_HELPER;
                        targetParams.needsBaseline = USE_CONSTRAINTS_HELPER;
                        widget.getAnchor(ConstraintAnchor.Type.BASELINE).connect(target10.getAnchor(ConstraintAnchor.Type.BASELINE), 0, -1, (boolean) USE_CONSTRAINTS_HELPER);
                        widget.setHasBaseline((boolean) USE_CONSTRAINTS_HELPER);
                        targetParams.widget.setHasBaseline((boolean) USE_CONSTRAINTS_HELPER);
                        widget.getAnchor(ConstraintAnchor.Type.TOP).reset();
                        widget.getAnchor(ConstraintAnchor.Type.BOTTOM).reset();
                    }
                }
                if (resolvedHorizontalBias >= 0.0f) {
                    widget.setHorizontalBiasPercent(resolvedHorizontalBias);
                }
                if (layoutParams.verticalBias >= 0.0f) {
                    widget.setVerticalBiasPercent(layoutParams.verticalBias);
                }
            }
            if (isInEditMode && !(layoutParams.editorAbsoluteX == -1 && layoutParams.editorAbsoluteY == -1)) {
                widget.setOrigin(layoutParams.editorAbsoluteX, layoutParams.editorAbsoluteY);
            }
            if (layoutParams.horizontalDimensionFixed) {
                widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                widget.setWidth(layoutParams.width);
                if (layoutParams.width == -2) {
                    widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                }
            } else if (layoutParams.width == -1) {
                if (layoutParams.constrainedWidth) {
                    widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                } else {
                    widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                }
                widget.getAnchor(ConstraintAnchor.Type.LEFT).mMargin = layoutParams.leftMargin;
                widget.getAnchor(ConstraintAnchor.Type.RIGHT).mMargin = layoutParams.rightMargin;
            } else {
                widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                widget.setWidth(0);
            }
            if (layoutParams.verticalDimensionFixed) {
                widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                widget.setHeight(layoutParams.height);
                if (layoutParams.height == -2) {
                    widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                }
            } else if (layoutParams.height == -1) {
                if (layoutParams.constrainedHeight) {
                    widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                } else {
                    widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                }
                widget.getAnchor(ConstraintAnchor.Type.TOP).mMargin = layoutParams.topMargin;
                widget.getAnchor(ConstraintAnchor.Type.BOTTOM).mMargin = layoutParams.bottomMargin;
            } else {
                widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                widget.setHeight(0);
            }
            if (layoutParams.dimensionRatio != null) {
                widget.setDimensionRatio(layoutParams.dimensionRatio);
            }
            widget.setHorizontalWeight(layoutParams.horizontalWeight);
            widget.setVerticalWeight(layoutParams.verticalWeight);
            widget.setHorizontalChainStyle(layoutParams.horizontalChainStyle);
            widget.setVerticalChainStyle(layoutParams.verticalChainStyle);
            widget.setHorizontalMatchStyle(layoutParams.matchConstraintDefaultWidth, layoutParams.matchConstraintMinWidth, layoutParams.matchConstraintMaxWidth, layoutParams.matchConstraintPercentWidth);
            widget.setVerticalMatchStyle(layoutParams.matchConstraintDefaultHeight, layoutParams.matchConstraintMinHeight, layoutParams.matchConstraintMaxHeight, layoutParams.matchConstraintPercentHeight);
        }
    }

    private final ConstraintWidget getTargetWidget(int id) {
        if (id == 0) {
            return this.mLayoutWidget;
        }
        View view = this.mChildrenByIds.get(id);
        if (view == null && (view = findViewById(id)) != null && view != this && view.getParent() == this) {
            onViewAdded(view);
        }
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    public final ConstraintWidget getViewWidget(View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    public void fillMetrics(Metrics metrics) {
        this.mMetrics = metrics;
        this.mLayoutWidget.fillMetrics(metrics);
    }

    /* access modifiers changed from: protected */
    public void resolveSystem(ConstraintWidgetContainer layout, int optimizationLevel, int widthMeasureSpec, int heightMeasureSpec) {
        int paddingX;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int paddingY = getPaddingTop();
        int paddingHeight = paddingY + getPaddingBottom();
        int paddingWidth = getPaddingWidth();
        if (Build.VERSION.SDK_INT >= 17) {
            int paddingStart = getPaddingStart();
            int paddingEnd = getPaddingEnd();
            if (paddingStart <= 0 && paddingEnd <= 0) {
                paddingX = getPaddingLeft();
            } else if (isRtl()) {
                paddingX = paddingEnd;
            } else {
                paddingX = paddingStart;
            }
        } else {
            paddingX = getPaddingLeft();
        }
        int widthSize2 = widthSize - paddingWidth;
        int heightSize2 = heightSize - paddingHeight;
        setSelfDimensionBehaviour(layout, widthMode, widthSize2, heightMode, heightSize2);
        layout.measure(optimizationLevel, widthMode, widthSize2, heightMode, heightSize2, this.mLastMeasureWidth, this.mLastMeasureHeight, paddingX, paddingY);
    }

    /* access modifiers changed from: protected */
    public void resolveMeasuredDimension(int widthMeasureSpec, int heightMeasureSpec, int measuredWidth, int measuredHeight, boolean isWidthMeasuredTooSmall, boolean isHeightMeasuredTooSmall) {
        int androidLayoutWidth = measuredWidth + getPaddingWidth();
        int androidLayoutHeight = measuredHeight + getPaddingTop() + getPaddingBottom();
        if (Build.VERSION.SDK_INT >= 11) {
            int resolvedWidthSize = resolveSizeAndState(androidLayoutWidth, widthMeasureSpec, 0);
            int resolvedWidthSize2 = Math.min(this.mMaxWidth, resolvedWidthSize & 16777215);
            int resolvedHeightSize = Math.min(this.mMaxHeight, resolveSizeAndState(androidLayoutHeight, heightMeasureSpec, 0) & 16777215);
            if (isWidthMeasuredTooSmall) {
                resolvedWidthSize2 |= 16777216;
            }
            if (isHeightMeasuredTooSmall) {
                resolvedHeightSize |= 16777216;
            }
            setMeasuredDimension(resolvedWidthSize2, resolvedHeightSize);
            this.mLastMeasureWidth = resolvedWidthSize2;
            this.mLastMeasureHeight = resolvedHeightSize;
            return;
        }
        setMeasuredDimension(androidLayoutWidth, androidLayoutHeight);
        this.mLastMeasureWidth = androidLayoutWidth;
        this.mLastMeasureHeight = androidLayoutHeight;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mOnMeasureWidthMeasureSpec = widthMeasureSpec;
        this.mOnMeasureHeightMeasureSpec = heightMeasureSpec;
        this.mLayoutWidget.setRtl(isRtl());
        if (this.mDirtyHierarchy) {
            this.mDirtyHierarchy = false;
            if (updateHierarchy()) {
                this.mLayoutWidget.updateHierarchy();
            }
        }
        resolveSystem(this.mLayoutWidget, this.mOptimizationLevel, widthMeasureSpec, heightMeasureSpec);
        resolveMeasuredDimension(widthMeasureSpec, heightMeasureSpec, this.mLayoutWidget.getWidth(), this.mLayoutWidget.getHeight(), this.mLayoutWidget.isWidthMeasuredTooSmall(), this.mLayoutWidget.isHeightMeasuredTooSmall());
    }

    /* access modifiers changed from: protected */
    public boolean isRtl() {
        boolean isRtlSupported;
        if (Build.VERSION.SDK_INT < 17) {
            return false;
        }
        if ((getContext().getApplicationInfo().flags & 4194304) != 0) {
            isRtlSupported = true;
        } else {
            isRtlSupported = false;
        }
        if (!isRtlSupported || 1 != getLayoutDirection()) {
            return false;
        }
        return USE_CONSTRAINTS_HELPER;
    }

    /* access modifiers changed from: private */
    public int getPaddingWidth() {
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int rtlPadding = 0;
        if (Build.VERSION.SDK_INT >= 17) {
            rtlPadding = getPaddingStart() + getPaddingEnd();
        }
        return rtlPadding > 0 ? rtlPadding : widthPadding;
    }

    /* access modifiers changed from: protected */
    public void setSelfDimensionBehaviour(ConstraintWidgetContainer layout, int widthMode, int widthSize, int heightMode, int heightSize) {
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingWidth();
        ConstraintWidget.DimensionBehaviour widthBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        ConstraintWidget.DimensionBehaviour heightBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        int desiredWidth = 0;
        int desiredHeight = 0;
        int childCount = getChildCount();
        switch (widthMode) {
            case Integer.MIN_VALUE:
                widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                desiredWidth = widthSize;
                if (childCount == 0) {
                    desiredWidth = Math.max(0, this.mMinWidth);
                    break;
                }
                break;
            case 0:
                widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                if (childCount == 0) {
                    desiredWidth = Math.max(0, this.mMinWidth);
                    break;
                }
                break;
            case 1073741824:
                desiredWidth = Math.min(this.mMaxWidth - widthPadding, widthSize);
                break;
        }
        switch (heightMode) {
            case Integer.MIN_VALUE:
                heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                desiredHeight = heightSize;
                if (childCount == 0) {
                    desiredHeight = Math.max(0, this.mMinHeight);
                    break;
                }
                break;
            case 0:
                heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                if (childCount == 0) {
                    desiredHeight = Math.max(0, this.mMinHeight);
                    break;
                }
                break;
            case 1073741824:
                desiredHeight = Math.min(this.mMaxHeight - heightPadding, heightSize);
                break;
        }
        if (!(desiredWidth == layout.getWidth() && desiredHeight == layout.getHeight())) {
            layout.invalidateMeasures();
        }
        layout.setX(0);
        layout.setY(0);
        layout.setMaxWidth(this.mMaxWidth - widthPadding);
        layout.setMaxHeight(this.mMaxHeight - heightPadding);
        layout.setMinWidth(0);
        layout.setMinHeight(0);
        layout.setHorizontalDimensionBehaviour(widthBehaviour);
        layout.setWidth(desiredWidth);
        layout.setVerticalDimensionBehaviour(heightBehaviour);
        layout.setHeight(desiredHeight);
        layout.setMinWidth(this.mMinWidth - widthPadding);
        layout.setMinHeight(this.mMinHeight - heightPadding);
    }

    public void setState(int id, int screenWidth, int screenHeight) {
        if (this.mConstraintLayoutSpec != null) {
            this.mConstraintLayoutSpec.updateConstraints(id, (float) screenWidth, (float) screenHeight);
        }
    }

    /* access modifiers changed from: protected */

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View content;
        int widgetsCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            ConstraintWidget widget = params.widget;
            if ((child.getVisibility() != 8 || params.isGuideline || params.isHelper || params.isVirtualGroup || isInEditMode) && !params.isInPlaceholder) {
                int l = widget.getX();
                int t = widget.getY();
                int r = l + widget.getWidth();
                int b = t + widget.getHeight();
                child.layout(l, t, r, b);
                if ((child instanceof Placeholder) && (content = ((Placeholder) child).getContent()) != null) {
                    content.setVisibility(0);
                    content.layout(l, t, r, b);
                }
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i2 = 0; i2 < helperCount; i2++) {
                this.mConstraintHelpers.get(i2).updatePostLayout(this);
            }
        }
    }

    public void setOptimizationLevel(int level) {
        this.mOptimizationLevel = level;
        this.mLayoutWidget.setOptimizationLevel(level);
    }

    public int getOptimizationLevel() {
        return this.mLayoutWidget.getOptimizationLevel();
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    /* access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setConstraintSet(ConstraintSet set) {
        this.mConstraintSet = set;
    }

    public View getViewById(int id) {
        return this.mChildrenByIds.get(id);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        Object tag;
        int helperCount;
        if (this.mConstraintHelpers != null && (helperCount = this.mConstraintHelpers.size()) > 0) {
            for (int i = 0; i < helperCount; i++) {
                this.mConstraintHelpers.get(i).updatePreDraw(this);
            }
        }
        super.dispatchDraw(canvas);
        if (isInEditMode()) {
            int count = getChildCount();
            float cw = (float) getWidth();
            float ch = (float) getHeight();
            for (int i2 = 0; i2 < count; i2++) {
                View child = getChildAt(i2);
                if (!(child.getVisibility() == 8 || (tag = child.getTag()) == null || !(tag instanceof String))) {
                    String[] split = ((String) tag).split(",");
                    if (split.length == 4) {
                        int x = Integer.parseInt(split[0]);
                        int y = Integer.parseInt(split[1]);
                        int x2 = (int) ((((float) x) / 1080.0f) * cw);
                        int y2 = (int) ((((float) y) / 1920.0f) * ch);
                        int w = (int) ((((float) Integer.parseInt(split[2])) / 1080.0f) * cw);
                        int h = (int) ((((float) Integer.parseInt(split[3])) / 1920.0f) * ch);
                        Paint paint = new Paint();
                        paint.setColor(-65536);
                        canvas.drawLine((float) x2, (float) y2, (float) (x2 + w), (float) y2, paint);
                        canvas.drawLine((float) (x2 + w), (float) y2, (float) (x2 + w), (float) (y2 + h), paint);
                        canvas.drawLine((float) (x2 + w), (float) (y2 + h), (float) x2, (float) (y2 + h), paint);
                        canvas.drawLine((float) x2, (float) (y2 + h), (float) x2, (float) y2, paint);
                        paint.setColor(-16711936);
                        canvas.drawLine((float) x2, (float) y2, (float) (x2 + w), (float) (y2 + h), paint);
                        canvas.drawLine((float) x2, (float) (y2 + h), (float) (x2 + w), (float) y2, paint);
                    }
                }
            }
        }
    }

    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
        if (this.mConstraintLayoutSpec != null) {
            this.mConstraintLayoutSpec.setOnConstraintsChanged(constraintsChangedListener);
        }
    }

    public void loadLayoutDescription(int layoutDescription) {
        if (layoutDescription != 0) {
            try {
                this.mConstraintLayoutSpec = new ConstraintLayoutStates(getContext(), this, layoutDescription);
            } catch (Resources.NotFoundException e) {
                this.mConstraintLayoutSpec = null;
            }
        } else {
            this.mConstraintLayoutSpec = null;
        }
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int BASELINE = 5;
        public static final int BOTTOM = 4;
        public static final int CHAIN_PACKED = 2;
        public static final int CHAIN_SPREAD = 0;
        public static final int CHAIN_SPREAD_INSIDE = 1;
        public static final int END = 7;
        public static final int HORIZONTAL = 0;
        public static final int LEFT = 1;
        public static final int MATCH_CONSTRAINT = 0;
        public static final int MATCH_CONSTRAINT_PERCENT = 2;
        public static final int MATCH_CONSTRAINT_SPREAD = 0;
        public static final int MATCH_CONSTRAINT_WRAP = 1;
        public static final int PARENT_ID = 0;
        public static final int RIGHT = 2;
        public static final int START = 6;
        public static final int TOP = 3;
        public static final int UNSET = -1;
        public static final int VERTICAL = 1;
        public int baselineToBaseline = -1;
        public int bottomToBottom = -1;
        public int bottomToTop = -1;
        public float circleAngle = 0.0f;
        public int circleConstraint = -1;
        public int circleRadius = 0;
        public boolean constrainedHeight = false;
        public boolean constrainedWidth = false;
        public String constraintTag = null;
        public String dimensionRatio = null;
        int dimensionRatioSide = 1;
        float dimensionRatioValue = 0.0f;
        public int editorAbsoluteX = -1;
        public int editorAbsoluteY = -1;
        public int endToEnd = -1;
        public int endToStart = -1;
        public int goneBottomMargin = -1;
        public int goneEndMargin = -1;
        public int goneLeftMargin = -1;
        public int goneRightMargin = -1;
        public int goneStartMargin = -1;
        public int goneTopMargin = -1;
        public int guideBegin = -1;
        public int guideEnd = -1;
        public float guidePercent = -1.0f;
        public boolean helped = false;
        public float horizontalBias = 0.5f;
        public int horizontalChainStyle = 0;
        boolean horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
        public float horizontalWeight = -1.0f;
        boolean isGuideline = false;
        boolean isHelper = false;
        boolean isInPlaceholder = false;
        boolean isVirtualGroup = false;
        public int leftToLeft = -1;
        public int leftToRight = -1;
        public int matchConstraintDefaultHeight = 0;
        public int matchConstraintDefaultWidth = 0;
        public int matchConstraintMaxHeight = 0;
        public int matchConstraintMaxWidth = 0;
        public int matchConstraintMinHeight = 0;
        public int matchConstraintMinWidth = 0;
        public float matchConstraintPercentHeight = 1.0f;
        public float matchConstraintPercentWidth = 1.0f;
        boolean needsBaseline = false;
        public int orientation = -1;
        int resolveGoneLeftMargin = -1;
        int resolveGoneRightMargin = -1;
        int resolvedGuideBegin;
        int resolvedGuideEnd;
        float resolvedGuidePercent;
        float resolvedHorizontalBias = 0.5f;
        int resolvedLeftToLeft = -1;
        int resolvedLeftToRight = -1;
        int resolvedRightToLeft = -1;
        int resolvedRightToRight = -1;
        public int rightToLeft = -1;
        public int rightToRight = -1;
        public int startToEnd = -1;
        public int startToStart = -1;
        public int topToBottom = -1;
        public int topToTop = -1;
        public float verticalBias = 0.5f;
        public int verticalChainStyle = 0;
        boolean verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
        public float verticalWeight = -1.0f;
        ConstraintWidget widget = new ConstraintWidget();

        public ConstraintWidget getConstraintWidget() {
            return this.widget;
        }

        public void setWidgetDebugName(String text) {
            this.widget.setDebugName(text);
        }

        public void reset() {
            if (this.widget != null) {
                this.widget.reset();
            }
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.guideBegin = source.guideBegin;
            this.guideEnd = source.guideEnd;
            this.guidePercent = source.guidePercent;
            this.leftToLeft = source.leftToLeft;
            this.leftToRight = source.leftToRight;
            this.rightToLeft = source.rightToLeft;
            this.rightToRight = source.rightToRight;
            this.topToTop = source.topToTop;
            this.topToBottom = source.topToBottom;
            this.bottomToTop = source.bottomToTop;
            this.bottomToBottom = source.bottomToBottom;
            this.baselineToBaseline = source.baselineToBaseline;
            this.circleConstraint = source.circleConstraint;
            this.circleRadius = source.circleRadius;
            this.circleAngle = source.circleAngle;
            this.startToEnd = source.startToEnd;
            this.startToStart = source.startToStart;
            this.endToStart = source.endToStart;
            this.endToEnd = source.endToEnd;
            this.goneLeftMargin = source.goneLeftMargin;
            this.goneTopMargin = source.goneTopMargin;
            this.goneRightMargin = source.goneRightMargin;
            this.goneBottomMargin = source.goneBottomMargin;
            this.goneStartMargin = source.goneStartMargin;
            this.goneEndMargin = source.goneEndMargin;
            this.horizontalBias = source.horizontalBias;
            this.verticalBias = source.verticalBias;
            this.dimensionRatio = source.dimensionRatio;
            this.dimensionRatioValue = source.dimensionRatioValue;
            this.dimensionRatioSide = source.dimensionRatioSide;
            this.horizontalWeight = source.horizontalWeight;
            this.verticalWeight = source.verticalWeight;
            this.horizontalChainStyle = source.horizontalChainStyle;
            this.verticalChainStyle = source.verticalChainStyle;
            this.constrainedWidth = source.constrainedWidth;
            this.constrainedHeight = source.constrainedHeight;
            this.matchConstraintDefaultWidth = source.matchConstraintDefaultWidth;
            this.matchConstraintDefaultHeight = source.matchConstraintDefaultHeight;
            this.matchConstraintMinWidth = source.matchConstraintMinWidth;
            this.matchConstraintMaxWidth = source.matchConstraintMaxWidth;
            this.matchConstraintMinHeight = source.matchConstraintMinHeight;
            this.matchConstraintMaxHeight = source.matchConstraintMaxHeight;
            this.matchConstraintPercentWidth = source.matchConstraintPercentWidth;
            this.matchConstraintPercentHeight = source.matchConstraintPercentHeight;
            this.editorAbsoluteX = source.editorAbsoluteX;
            this.editorAbsoluteY = source.editorAbsoluteY;
            this.orientation = source.orientation;
            this.horizontalDimensionFixed = source.horizontalDimensionFixed;
            this.verticalDimensionFixed = source.verticalDimensionFixed;
            this.needsBaseline = source.needsBaseline;
            this.isGuideline = source.isGuideline;
            this.resolvedLeftToLeft = source.resolvedLeftToLeft;
            this.resolvedLeftToRight = source.resolvedLeftToRight;
            this.resolvedRightToLeft = source.resolvedRightToLeft;
            this.resolvedRightToRight = source.resolvedRightToRight;
            this.resolveGoneLeftMargin = source.resolveGoneLeftMargin;
            this.resolveGoneRightMargin = source.resolveGoneRightMargin;
            this.resolvedHorizontalBias = source.resolvedHorizontalBias;
            this.constraintTag = source.constraintTag;
            this.widget = source.widget;
        }

        public static class Table {
            public static final int ANDROID_ORIENTATION = 1;
            public static final int LAYOUT_CONSTRAINED_HEIGHT = 28;
            public static final int LAYOUT_CONSTRAINED_WIDTH = 27;
            public static final int LAYOUT_CONSTRAINT_BASELINE_CREATOR = 43;
            public static final int LAYOUT_CONSTRAINT_BASELINE_TO_BASELINE_OF = 16;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_CREATOR = 42;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF = 15;
            public static final int LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF = 14;
            public static final int LAYOUT_CONSTRAINT_CIRCLE = 2;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_ANGLE = 4;
            public static final int LAYOUT_CONSTRAINT_CIRCLE_RADIUS = 3;
            public static final int LAYOUT_CONSTRAINT_DIMENSION_RATIO = 44;
            public static final int LAYOUT_CONSTRAINT_END_TO_END_OF = 20;
            public static final int LAYOUT_CONSTRAINT_END_TO_START_OF = 19;
            public static final int LAYOUT_CONSTRAINT_GUIDE_BEGIN = 5;
            public static final int LAYOUT_CONSTRAINT_GUIDE_END = 6;
            public static final int LAYOUT_CONSTRAINT_GUIDE_PERCENT = 7;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_DEFAULT = 32;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MAX = 37;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_MIN = 36;
            public static final int LAYOUT_CONSTRAINT_HEIGHT_PERCENT = 38;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_BIAS = 29;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE = 47;
            public static final int LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT = 45;
            public static final int LAYOUT_CONSTRAINT_LEFT_CREATOR = 39;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_LEFT_OF = 8;
            public static final int LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF = 9;
            public static final int LAYOUT_CONSTRAINT_RIGHT_CREATOR = 41;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF = 10;
            public static final int LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF = 11;
            public static final int LAYOUT_CONSTRAINT_START_TO_END_OF = 17;
            public static final int LAYOUT_CONSTRAINT_START_TO_START_OF = 18;
            public static final int LAYOUT_CONSTRAINT_TAG = 51;
            public static final int LAYOUT_CONSTRAINT_TOP_CREATOR = 40;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF = 13;
            public static final int LAYOUT_CONSTRAINT_TOP_TO_TOP_OF = 12;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_BIAS = 30;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE = 48;
            public static final int LAYOUT_CONSTRAINT_VERTICAL_WEIGHT = 46;
            public static final int LAYOUT_CONSTRAINT_WIDTH_DEFAULT = 31;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MAX = 34;
            public static final int LAYOUT_CONSTRAINT_WIDTH_MIN = 33;
            public static final int LAYOUT_CONSTRAINT_WIDTH_PERCENT = 35;
            public static final int LAYOUT_EDITOR_ABSOLUTEX = 49;
            public static final int LAYOUT_EDITOR_ABSOLUTEY = 50;
            public static final int LAYOUT_GONE_MARGIN_BOTTOM = 24;
            public static final int LAYOUT_GONE_MARGIN_END = 26;
            public static final int LAYOUT_GONE_MARGIN_LEFT = 21;
            public static final int LAYOUT_GONE_MARGIN_RIGHT = 23;
            public static final int LAYOUT_GONE_MARGIN_START = 25;
            public static final int LAYOUT_GONE_MARGIN_TOP = 22;
            public static final int UNUSED = 0;
            public static final SparseIntArray map = new SparseIntArray();

            private Table() {
            }

            static {
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf, 8);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf, 9);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf, 10);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf, 11);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf, 12);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf, 13);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf, 14);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf, 15);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf, 16);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircle, 2);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleRadius, 3);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintCircleAngle, 4);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX, 49);
                map.append(R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY, 50);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin, 5);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end, 6);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent, 7);
                map.append(R.styleable.ConstraintLayout_Layout_android_orientation, 1);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf, 17);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf, 18);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf, 19);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf, 20);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft, 21);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginTop, 22);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginRight, 23);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom, 24);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginStart, 25);
                map.append(R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd, 26);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias, 29);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias, 30);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio, 44);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight, 45);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight, 46);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle, 47);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle, 48);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedWidth, 27);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constrainedHeight, 28);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default, 31);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default, 32);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min, 33);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max, 34);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent, 35);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min, 36);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max, 37);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent, 38);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator, 39);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator, 40);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator, 41);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator, 42);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator, 43);
                map.append(R.styleable.ConstraintLayout_Layout_layout_constraintTag, 51);
            }
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int commaIndex;
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (Table.map.get(attr)) {
                    case 1:
                        this.orientation = a.getInt(attr, this.orientation);
                        break;
                    case 2:
                        this.circleConstraint = a.getResourceId(attr, this.circleConstraint);
                        if (this.circleConstraint != -1) {
                            break;
                        } else {
                            this.circleConstraint = a.getInt(attr, -1);
                            break;
                        }
                    case 3:
                        this.circleRadius = a.getDimensionPixelSize(attr, this.circleRadius);
                        break;
                    case 4:
                        this.circleAngle = a.getFloat(attr, this.circleAngle) % 360.0f;
                        if (this.circleAngle >= 0.0f) {
                            break;
                        } else {
                            this.circleAngle = (360.0f - this.circleAngle) % 360.0f;
                            break;
                        }
                    case 5:
                        this.guideBegin = a.getDimensionPixelOffset(attr, this.guideBegin);
                        break;
                    case 6:
                        this.guideEnd = a.getDimensionPixelOffset(attr, this.guideEnd);
                        break;
                    case 7:
                        this.guidePercent = a.getFloat(attr, this.guidePercent);
                        break;
                    case 8:
                        this.leftToLeft = a.getResourceId(attr, this.leftToLeft);
                        if (this.leftToLeft != -1) {
                            break;
                        } else {
                            this.leftToLeft = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF /*{ENCODED_INT: 9}*/:
                        this.leftToRight = a.getResourceId(attr, this.leftToRight);
                        if (this.leftToRight != -1) {
                            break;
                        } else {
                            this.leftToRight = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF /*{ENCODED_INT: 10}*/:
                        this.rightToLeft = a.getResourceId(attr, this.rightToLeft);
                        if (this.rightToLeft != -1) {
                            break;
                        } else {
                            this.rightToLeft = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF /*{ENCODED_INT: 11}*/:
                        this.rightToRight = a.getResourceId(attr, this.rightToRight);
                        if (this.rightToRight != -1) {
                            break;
                        } else {
                            this.rightToRight = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_TOP_TO_TOP_OF /*{ENCODED_INT: 12}*/:
                        this.topToTop = a.getResourceId(attr, this.topToTop);
                        if (this.topToTop != -1) {
                            break;
                        } else {
                            this.topToTop = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF /*{ENCODED_INT: 13}*/:
                        this.topToBottom = a.getResourceId(attr, this.topToBottom);
                        if (this.topToBottom != -1) {
                            break;
                        } else {
                            this.topToBottom = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF /*{ENCODED_INT: 14}*/:
                        this.bottomToTop = a.getResourceId(attr, this.bottomToTop);
                        if (this.bottomToTop != -1) {
                            break;
                        } else {
                            this.bottomToTop = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF /*{ENCODED_INT: 15}*/:
                        this.bottomToBottom = a.getResourceId(attr, this.bottomToBottom);
                        if (this.bottomToBottom != -1) {
                            break;
                        } else {
                            this.bottomToBottom = a.getInt(attr, -1);
                            break;
                        }
                    case 16:
                        this.baselineToBaseline = a.getResourceId(attr, this.baselineToBaseline);
                        if (this.baselineToBaseline != -1) {
                            break;
                        } else {
                            this.baselineToBaseline = a.getInt(attr, -1);
                            break;
                        }
                    case 17:
                        this.startToEnd = a.getResourceId(attr, this.startToEnd);
                        if (this.startToEnd != -1) {
                            break;
                        } else {
                            this.startToEnd = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_START_TO_START_OF /*{ENCODED_INT: 18}*/:
                        this.startToStart = a.getResourceId(attr, this.startToStart);
                        if (this.startToStart != -1) {
                            break;
                        } else {
                            this.startToStart = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_END_TO_START_OF /*{ENCODED_INT: 19}*/:
                        this.endToStart = a.getResourceId(attr, this.endToStart);
                        if (this.endToStart != -1) {
                            break;
                        } else {
                            this.endToStart = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_END_TO_END_OF /*{ENCODED_INT: 20}*/:
                        this.endToEnd = a.getResourceId(attr, this.endToEnd);
                        if (this.endToEnd != -1) {
                            break;
                        } else {
                            this.endToEnd = a.getInt(attr, -1);
                            break;
                        }
                    case Table.LAYOUT_GONE_MARGIN_LEFT /*{ENCODED_INT: 21}*/:
                        this.goneLeftMargin = a.getDimensionPixelSize(attr, this.goneLeftMargin);
                        break;
                    case Table.LAYOUT_GONE_MARGIN_TOP /*{ENCODED_INT: 22}*/:
                        this.goneTopMargin = a.getDimensionPixelSize(attr, this.goneTopMargin);
                        break;
                    case Table.LAYOUT_GONE_MARGIN_RIGHT /*{ENCODED_INT: 23}*/:
                        this.goneRightMargin = a.getDimensionPixelSize(attr, this.goneRightMargin);
                        break;
                    case Table.LAYOUT_GONE_MARGIN_BOTTOM /*{ENCODED_INT: 24}*/:
                        this.goneBottomMargin = a.getDimensionPixelSize(attr, this.goneBottomMargin);
                        break;
                    case Table.LAYOUT_GONE_MARGIN_START /*{ENCODED_INT: 25}*/:
                        this.goneStartMargin = a.getDimensionPixelSize(attr, this.goneStartMargin);
                        break;
                    case Table.LAYOUT_GONE_MARGIN_END /*{ENCODED_INT: 26}*/:
                        this.goneEndMargin = a.getDimensionPixelSize(attr, this.goneEndMargin);
                        break;
                    case Table.LAYOUT_CONSTRAINED_WIDTH /*{ENCODED_INT: 27}*/:
                        this.constrainedWidth = a.getBoolean(attr, this.constrainedWidth);
                        break;
                    case Table.LAYOUT_CONSTRAINED_HEIGHT /*{ENCODED_INT: 28}*/:
                        this.constrainedHeight = a.getBoolean(attr, this.constrainedHeight);
                        break;
                    case Table.LAYOUT_CONSTRAINT_HORIZONTAL_BIAS /*{ENCODED_INT: 29}*/:
                        this.horizontalBias = a.getFloat(attr, this.horizontalBias);
                        break;
                    case Table.LAYOUT_CONSTRAINT_VERTICAL_BIAS /*{ENCODED_INT: 30}*/:
                        this.verticalBias = a.getFloat(attr, this.verticalBias);
                        break;
                    case Table.LAYOUT_CONSTRAINT_WIDTH_DEFAULT /*{ENCODED_INT: 31}*/:
                        this.matchConstraintDefaultWidth = a.getInt(attr, 0);
                        if (this.matchConstraintDefaultWidth != 1) {
                            break;
                        } else {
                            Log.e(ConstraintLayout.TAG, "layout_constraintWidth_default=\"wrap\" is deprecated.\nUse layout_width=\"WRAP_CONTENT\" and layout_constrainedWidth=\"true\" instead.");
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_HEIGHT_DEFAULT /*{ENCODED_INT: 32}*/:
                        this.matchConstraintDefaultHeight = a.getInt(attr, 0);
                        if (this.matchConstraintDefaultHeight != 1) {
                            break;
                        } else {
                            Log.e(ConstraintLayout.TAG, "layout_constraintHeight_default=\"wrap\" is deprecated.\nUse layout_height=\"WRAP_CONTENT\" and layout_constrainedHeight=\"true\" instead.");
                            break;
                        }
                    case Table.LAYOUT_CONSTRAINT_WIDTH_MIN /*{ENCODED_INT: 33}*/:
                        try {
                            this.matchConstraintMinWidth = a.getDimensionPixelSize(attr, this.matchConstraintMinWidth);
                            break;
                        } catch (Exception e) {
                            if (a.getInt(attr, this.matchConstraintMinWidth) != -2) {
                                break;
                            } else {
                                this.matchConstraintMinWidth = -2;
                                break;
                            }
                        }
                    case Table.LAYOUT_CONSTRAINT_WIDTH_MAX /*{ENCODED_INT: 34}*/:
                        try {
                            this.matchConstraintMaxWidth = a.getDimensionPixelSize(attr, this.matchConstraintMaxWidth);
                            break;
                        } catch (Exception e2) {
                            if (a.getInt(attr, this.matchConstraintMaxWidth) != -2) {
                                break;
                            } else {
                                this.matchConstraintMaxWidth = -2;
                                break;
                            }
                        }
                    case Table.LAYOUT_CONSTRAINT_WIDTH_PERCENT /*{ENCODED_INT: 35}*/:
                        this.matchConstraintPercentWidth = Math.max(0.0f, a.getFloat(attr, this.matchConstraintPercentWidth));
                        this.matchConstraintDefaultWidth = 2;
                        break;
                    case Table.LAYOUT_CONSTRAINT_HEIGHT_MIN /*{ENCODED_INT: 36}*/:
                        try {
                            this.matchConstraintMinHeight = a.getDimensionPixelSize(attr, this.matchConstraintMinHeight);
                            break;
                        } catch (Exception e3) {
                            if (a.getInt(attr, this.matchConstraintMinHeight) != -2) {
                                break;
                            } else {
                                this.matchConstraintMinHeight = -2;
                                break;
                            }
                        }
                    case Table.LAYOUT_CONSTRAINT_HEIGHT_MAX /*{ENCODED_INT: 37}*/:
                        try {
                            this.matchConstraintMaxHeight = a.getDimensionPixelSize(attr, this.matchConstraintMaxHeight);
                            break;
                        } catch (Exception e4) {
                            if (a.getInt(attr, this.matchConstraintMaxHeight) != -2) {
                                break;
                            } else {
                                this.matchConstraintMaxHeight = -2;
                                break;
                            }
                        }
                    case Table.LAYOUT_CONSTRAINT_HEIGHT_PERCENT /*{ENCODED_INT: 38}*/:
                        this.matchConstraintPercentHeight = Math.max(0.0f, a.getFloat(attr, this.matchConstraintPercentHeight));
                        this.matchConstraintDefaultHeight = 2;
                        break;
                    case Table.LAYOUT_CONSTRAINT_DIMENSION_RATIO /*{ENCODED_INT: 44}*/:
                        this.dimensionRatio = a.getString(attr);
                        this.dimensionRatioValue = Float.NaN;
                        this.dimensionRatioSide = -1;
                        if (this.dimensionRatio == null) {
                            break;
                        } else {
                            int len = this.dimensionRatio.length();
                            int commaIndex2 = this.dimensionRatio.indexOf(44);
                            if (commaIndex2 <= 0 || commaIndex2 >= len - 1) {
                                commaIndex = 0;
                            } else {
                                String dimension = this.dimensionRatio.substring(0, commaIndex2);
                                if (dimension.equalsIgnoreCase("W")) {
                                    this.dimensionRatioSide = 0;
                                } else if (dimension.equalsIgnoreCase("H")) {
                                    this.dimensionRatioSide = 1;
                                }
                                commaIndex = commaIndex2 + 1;
                            }
                            int colonIndex = this.dimensionRatio.indexOf(58);
                            if (colonIndex >= 0 && colonIndex < len - 1) {
                                String nominator = this.dimensionRatio.substring(commaIndex, colonIndex);
                                String denominator = this.dimensionRatio.substring(colonIndex + 1);
                                if (nominator.length() > 0 && denominator.length() > 0) {
                                    try {
                                        float nominatorValue = Float.parseFloat(nominator);
                                        float denominatorValue = Float.parseFloat(denominator);
                                        if (nominatorValue > 0.0f && denominatorValue > 0.0f) {
                                            if (this.dimensionRatioSide != 1) {
                                                this.dimensionRatioValue = Math.abs(nominatorValue / denominatorValue);
                                                break;
                                            } else {
                                                this.dimensionRatioValue = Math.abs(denominatorValue / nominatorValue);
                                                break;
                                            }
                                        }
                                    } catch (NumberFormatException e5) {
                                        break;
                                    }
                                }
                            } else {
                                String r = this.dimensionRatio.substring(commaIndex);
                                if (r.length() <= 0) {
                                    break;
                                } else {
                                    try {
                                        this.dimensionRatioValue = Float.parseFloat(r);
                                        break;
                                    } catch (NumberFormatException e6) {
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case Table.LAYOUT_CONSTRAINT_HORIZONTAL_WEIGHT /*{ENCODED_INT: 45}*/:
                        this.horizontalWeight = a.getFloat(attr, this.horizontalWeight);
                        break;
                    case Table.LAYOUT_CONSTRAINT_VERTICAL_WEIGHT /*{ENCODED_INT: 46}*/:
                        this.verticalWeight = a.getFloat(attr, this.verticalWeight);
                        break;
                    case Table.LAYOUT_CONSTRAINT_HORIZONTAL_CHAINSTYLE /*{ENCODED_INT: 47}*/:
                        this.horizontalChainStyle = a.getInt(attr, 0);
                        break;
                    case Table.LAYOUT_CONSTRAINT_VERTICAL_CHAINSTYLE /*{ENCODED_INT: 48}*/:
                        this.verticalChainStyle = a.getInt(attr, 0);
                        break;
                    case Table.LAYOUT_EDITOR_ABSOLUTEX /*{ENCODED_INT: 49}*/:
                        this.editorAbsoluteX = a.getDimensionPixelOffset(attr, this.editorAbsoluteX);
                        break;
                    case Table.LAYOUT_EDITOR_ABSOLUTEY /*{ENCODED_INT: 50}*/:
                        this.editorAbsoluteY = a.getDimensionPixelOffset(attr, this.editorAbsoluteY);
                        break;
                    case Table.LAYOUT_CONSTRAINT_TAG /*{ENCODED_INT: 51}*/:
                        this.constraintTag = a.getString(attr);
                        break;
                }
            }
            a.recycle();
            validate();
        }

        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
            if (this.width == -2 && this.constrainedWidth) {
                this.horizontalDimensionFixed = false;
                if (this.matchConstraintDefaultWidth == 0) {
                    this.matchConstraintDefaultWidth = 1;
                }
            }
            if (this.height == -2 && this.constrainedHeight) {
                this.verticalDimensionFixed = false;
                if (this.matchConstraintDefaultHeight == 0) {
                    this.matchConstraintDefaultHeight = 1;
                }
            }
            if (this.width == 0 || this.width == -1) {
                this.horizontalDimensionFixed = false;
                if (this.width == 0 && this.matchConstraintDefaultWidth == 1) {
                    this.width = -2;
                    this.constrainedWidth = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.height == 0 || this.height == -1) {
                this.verticalDimensionFixed = false;
                if (this.height == 0 && this.matchConstraintDefaultHeight == 1) {
                    this.height = -2;
                    this.constrainedHeight = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.horizontalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                this.verticalDimensionFixed = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                if (!(this.widget instanceof Guideline)) {
                    this.widget = new Guideline();
                }
                ((androidx.constraintlayout.solver.widgets.Guideline)this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        @TargetApi(17)
        public void resolveLayoutDirection(int layoutDirection) {
            int preLeftMargin = this.leftMargin;
            int preRightMargin = this.rightMargin;
            boolean isRtl = false;
            if (Build.VERSION.SDK_INT >= 17) {
                super.resolveLayoutDirection(layoutDirection);
                isRtl = 1 == getLayoutDirection();
            }
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            this.resolvedGuideBegin = this.guideBegin;
            this.resolvedGuideEnd = this.guideEnd;
            this.resolvedGuidePercent = this.guidePercent;
            if (isRtl) {
                boolean startEndDefined = false;
                if (this.startToEnd != -1) {
                    this.resolvedRightToLeft = this.startToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                } else if (this.startToStart != -1) {
                    this.resolvedRightToRight = this.startToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToStart != -1) {
                    this.resolvedLeftToRight = this.endToStart;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.endToEnd != -1) {
                    this.resolvedLeftToLeft = this.endToEnd;
                    startEndDefined = ConstraintLayout.USE_CONSTRAINTS_HELPER;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneRightMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneEndMargin;
                }
                if (startEndDefined) {
                    this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
                }
                if (this.isGuideline && this.orientation == 1) {
                    if (this.guidePercent != -1.0f) {
                        this.resolvedGuidePercent = 1.0f - this.guidePercent;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuideEnd = -1;
                    } else if (this.guideBegin != -1) {
                        this.resolvedGuideEnd = this.guideBegin;
                        this.resolvedGuideBegin = -1;
                        this.resolvedGuidePercent = -1.0f;
                    } else if (this.guideEnd != -1) {
                        this.resolvedGuideBegin = this.guideEnd;
                        this.resolvedGuideEnd = -1;
                        this.resolvedGuidePercent = -1.0f;
                    }
                }
            } else {
                if (this.startToEnd != -1) {
                    this.resolvedLeftToRight = this.startToEnd;
                }
                if (this.startToStart != -1) {
                    this.resolvedLeftToLeft = this.startToStart;
                }
                if (this.endToStart != -1) {
                    this.resolvedRightToLeft = this.endToStart;
                }
                if (this.endToEnd != -1) {
                    this.resolvedRightToRight = this.endToEnd;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneRightMargin = this.goneEndMargin;
                }
            }
            if (this.endToStart == -1 && this.endToEnd == -1 && this.startToStart == -1 && this.startToEnd == -1) {
                if (this.rightToLeft != -1) {
                    this.resolvedRightToLeft = this.rightToLeft;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                } else if (this.rightToRight != -1) {
                    this.resolvedRightToRight = this.rightToRight;
                    if (this.rightMargin <= 0 && preRightMargin > 0) {
                        this.rightMargin = preRightMargin;
                    }
                }
                if (this.leftToLeft != -1) {
                    this.resolvedLeftToLeft = this.leftToLeft;
                    if (this.leftMargin <= 0 && preLeftMargin > 0) {
                        this.leftMargin = preLeftMargin;
                    }
                } else if (this.leftToRight != -1) {
                    this.resolvedLeftToRight = this.leftToRight;
                    if (this.leftMargin <= 0 && preLeftMargin > 0) {
                        this.leftMargin = preLeftMargin;
                    }
                }
            }
        }

        public String getConstraintTag() {
            return this.constraintTag;
        }
    }

    public void requestLayout() {
        markHierarchyDirty();
        super.requestLayout();
    }

    public void forceLayout() {
        markHierarchyDirty();
        super.forceLayout();
    }

    private void markHierarchyDirty() {
        this.mDirtyHierarchy = USE_CONSTRAINTS_HELPER;
        this.mLastMeasureWidth = -1;
        this.mLastMeasureHeight = -1;
        this.mLastMeasureWidthSize = -1;
        this.mLastMeasureHeightSize = -1;
        this.mLastMeasureWidthMode = 0;
        this.mLastMeasureHeightMode = 0;
    }

    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
