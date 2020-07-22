package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;
import androidx.constraintlayout.motion.utils.StopLogic;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.constraintlayout.solver.widgets.Barrier;
import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.solver.widgets.Flow;
import androidx.constraintlayout.solver.widgets.Guideline;
import androidx.constraintlayout.solver.widgets.Helper;
import androidx.constraintlayout.solver.widgets.HelperWidget;
import androidx.constraintlayout.solver.widgets.VirtualLayout;
import androidx.constraintlayout.widget.ConstraintHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Constraints;
import androidx.constraintlayout.R;
import androidx.core.view.NestedScrollingParent3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MotionLayout extends ConstraintLayout implements NestedScrollingParent3 {
    private static final boolean DEBUG = false;
    public static final int DEBUG_SHOW_NONE = 0;
    public static final int DEBUG_SHOW_PATH = 2;
    public static final int DEBUG_SHOW_PROGRESS = 1;
    public static boolean IS_IN_EDIT_MODE = false;
    static final int MAX_KEY_FRAMES = 50;
    static final String TAG = "MotionLayout";
    public static final int TOUCH_UP_COMPLETE = 0;
    public static final int TOUCH_UP_COMPLETE_TO_END = 2;
    public static final int TOUCH_UP_COMPLETE_TO_START = 1;
    public static final int TOUCH_UP_DECELERATE = 4;
    public static final int TOUCH_UP_DECELERATE_AND_COMPLETE = 5;
    public static final int TOUCH_UP_STOP = 3;
    public static final int VELOCITY_LAYOUT = 1;
    public static final int VELOCITY_POST_LAYOUT = 0;
    public static final int VELOCITY_STATIC_LAYOUT = 3;
    public static final int VELOCITY_STATIC_POST_LAYOUT = 2;
    boolean firstDown = true;
    private float lastPos;
    private float lastY;
    private long mAnimationStartTime = 0;
    /* access modifiers changed from: private */
    public int mBeginState = -1;
    private RectF mBoundsCheck = new RectF();
    int mCurrentState = -1;
    int mDebugPath = 0;
    private DecelerateInterpolator mDecelerateLogic = new DecelerateInterpolator();
    private DesignTool mDesignTool;
    DevModeDraw mDevModeDraw;
    /* access modifiers changed from: private */
    public int mEndState = -1;
    int mEndWrapHeight;
    int mEndWrapWidth;
    HashMap<View, MotionController> mFrameArrayList = new HashMap<>();
    private int mFrames = 0;
    int mHeightMeasureMode;
    private boolean mInLayout = false;
    boolean mInTransition = false;
    boolean mIndirectTransition = false;
    private boolean mInteractionEnabled = true;
    Interpolator mInterpolator;
    boolean mIsAnimating = false;
    private boolean mKeepAnimating = false;
    private KeyCache mKeyCache = new KeyCache();
    private long mLastDrawTime = -1;
    private float mLastFps = 0.0f;
    /* access modifiers changed from: private */
    public int mLastHeightMeasureSpec = 0;
    int mLastLayoutHeight;
    int mLastLayoutWidth;
    float mLastVelocity = 0.0f;
    /* access modifiers changed from: private */
    public int mLastWidthMeasureSpec = 0;
    private float mListenerPosition = 0.0f;
    private int mListenerState = 0;
    protected boolean mMeasureDuringTransition = false;
    Model mModel = new Model();
    private boolean mNeedsFireTransitionCompleted = false;
    int mOldHeight;
    int mOldWidth;
    private ArrayList<MotionHelper> mOnHideHelpers = null;
    private ArrayList<MotionHelper> mOnShowHelpers = null;
    float mPostInterpolationPosition;
    private View mRegionView = null;
    MotionScene mScene;
    float mScrollTargetDT;
    float mScrollTargetDX;
    float mScrollTargetDY;
    long mScrollTargetTime;
    int mStartWrapHeight;
    int mStartWrapWidth;
    private StateCache mStateCache;
    private StopLogic mStopLogic = new StopLogic();
    private boolean mTemporalInterpolator = false;
    ArrayList<Integer> mTransitionCompleted = new ArrayList<>();
    private float mTransitionDuration = 1.0f;
    float mTransitionGoalPosition = 0.0f;
    private boolean mTransitionInstantly;
    float mTransitionLastPosition = 0.0f;
    private long mTransitionLastTime;
    private TransitionListener mTransitionListener;
    private ArrayList<TransitionListener> mTransitionListeners = null;
    float mTransitionPosition = 0.0f;
    boolean mUndergoingMotion = false;
    int mWidthMeasureMode;

    protected interface MotionTracker {
        void addMovement(MotionEvent motionEvent);

        void clear();

        void computeCurrentVelocity(int i);

        void computeCurrentVelocity(int i, float f);

        float getXVelocity();

        float getXVelocity(int i);

        float getYVelocity();

        float getYVelocity(int i);

        void recycle();
    }

    public interface TransitionListener {
        void onTransitionChange(MotionLayout motionLayout, int i, int i2, float f);

        void onTransitionCompleted(MotionLayout motionLayout, int i);

        void onTransitionStarted(MotionLayout motionLayout, int i, int i2);

        void onTransitionTrigger(MotionLayout motionLayout, int i, boolean z, float f);
    }

    public MotionLayout(Context context) {
        super(context);
        init(null);
    }

    public MotionLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MotionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    /* access modifiers changed from: protected */
    public long getNanoTime() {
        return System.nanoTime();
    }

    /* access modifiers changed from: protected */
    public MotionTracker obtainVelocityTracker() {
        return MyTracker.obtain();
    }

    public void enableTransition(int transitionID, boolean enable) {
        MotionScene.Transition t = getTransition(transitionID);
        if (enable) {
            t.setEnable(true);
            return;
        }
        if (t == this.mScene.mCurrentTransition) {
            Iterator<MotionScene.Transition> it = this.mScene.getTransitionsWithState(this.mCurrentState).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MotionScene.Transition transition = it.next();
                if (transition.isEnabled()) {
                    this.mScene.mCurrentTransition = transition;
                    break;
                }
            }
        }
        t.setEnable(false);
    }

    private static class MyTracker implements MotionTracker {
        private static MyTracker me = new MyTracker();
        VelocityTracker tracker;

        private MyTracker() {
        }

        public static MyTracker obtain() {
            me.tracker = VelocityTracker.obtain();
            return me;
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public void recycle() {
            this.tracker.recycle();
            this.tracker = null;
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public void clear() {
            this.tracker.clear();
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public void addMovement(MotionEvent event) {
            if (this.tracker != null) {
                this.tracker.addMovement(event);
            }
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public void computeCurrentVelocity(int units) {
            this.tracker.computeCurrentVelocity(units);
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public void computeCurrentVelocity(int units, float maxVelocity) {
            this.tracker.computeCurrentVelocity(units, maxVelocity);
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public float getXVelocity() {
            return this.tracker.getXVelocity();
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public float getYVelocity() {
            return this.tracker.getYVelocity();
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public float getXVelocity(int id) {
            return this.tracker.getXVelocity(id);
        }

        @Override // androidx.constraintlayout.motion.widget.MotionLayout.MotionTracker
        public float getYVelocity(int id) {
            return getYVelocity(id);
        }
    }

    public void setTransition(int beginId, int endId) {
        if (!isAttachedToWindow()) {
            if (this.mStateCache == null) {
                this.mStateCache = new StateCache();
            }
            this.mStateCache.setStartState(beginId);
            this.mStateCache.setEndState(endId);
        } else if (this.mScene != null) {
            this.mBeginState = beginId;
            this.mEndState = endId;
            this.mScene.setTransition(beginId, endId);
            this.mModel.initFrom(this.mLayoutWidget, this.mScene.getConstraintSet(beginId), this.mScene.getConstraintSet(endId));
            rebuildScene();
            this.mTransitionLastPosition = 0.0f;
            transitionToStart();
        }
    }

    public void setTransition(int transitionId) {
        float f;
        if (this.mScene != null) {
            MotionScene.Transition transition = getTransition(transitionId);
            int i = this.mCurrentState;
            this.mBeginState = transition.getStartConstraintSetId();
            this.mEndState = transition.getEndConstraintSetId();
            if (!isAttachedToWindow()) {
                if (this.mStateCache == null) {
                    this.mStateCache = new StateCache();
                }
                this.mStateCache.setStartState(this.mBeginState);
                this.mStateCache.setEndState(this.mEndState);
                return;
            }
            float pos = Float.NaN;
            if (this.mCurrentState == this.mBeginState) {
                pos = 0.0f;
            } else if (this.mCurrentState == this.mEndState) {
                pos = 1.0f;
            }
            this.mScene.setTransition(transition);
            this.mModel.initFrom(this.mLayoutWidget, this.mScene.getConstraintSet(this.mBeginState), this.mScene.getConstraintSet(this.mEndState));
            rebuildScene();
            if (Float.isNaN(pos)) {
                f = 0.0f;
            } else {
                f = pos;
            }
            this.mTransitionLastPosition = f;
            if (Float.isNaN(pos)) {
                Log.v(TAG, Debug.getLocation() + " transitionToStart ");
                transitionToStart();
                return;
            }
            setProgress(pos);
        }
    }

    /* access modifiers changed from: protected */
    public void setTransition(MotionScene.Transition transition) {
        this.mScene.setTransition(transition);
        if (this.mCurrentState == this.mScene.getEndId()) {
            this.mTransitionLastPosition = 1.0f;
            this.mTransitionPosition = 1.0f;
            this.mTransitionGoalPosition = 1.0f;
        } else {
            this.mTransitionLastPosition = 0.0f;
            this.mTransitionPosition = 0.0f;
            this.mTransitionGoalPosition = 0.0f;
        }
        this.mTransitionLastTime = -1;
        int newBeginState = this.mScene.getStartId();
        int newEndState = this.mScene.getEndId();
        if (newBeginState != this.mBeginState || newEndState != this.mEndState) {
            this.mBeginState = newBeginState;
            this.mEndState = newEndState;
            this.mScene.setTransition(this.mBeginState, this.mEndState);
            this.mModel.initFrom(this.mLayoutWidget, this.mScene.getConstraintSet(this.mBeginState), this.mScene.getConstraintSet(this.mEndState));
            this.mModel.setMeasuredId(this.mBeginState, this.mEndState);
            this.mModel.reEvaluateState();
            rebuildScene();
            fireTransitionStarted(this, this.mBeginState, this.mEndState);
        }
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void loadLayoutDescription(int motionScene) {
        if (motionScene != 0) {
            try {
                this.mScene = new MotionScene(getContext(), this, motionScene);
                if (Build.VERSION.SDK_INT < 19 || isAttachedToWindow()) {
                    this.mScene.readFallback(this);
                    this.mModel.initFrom(this.mLayoutWidget, this.mScene.getConstraintSet(this.mBeginState), this.mScene.getConstraintSet(this.mEndState));
                    rebuildScene();
                    this.mScene.setRtl(isRtl());
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException("unable to parse MotionScene file", ex);
            }
        } else {
            this.mScene = null;
        }
    }

    public boolean isAttachedToWindow() {
        if (Build.VERSION.SDK_INT >= 19) {
            return super.isAttachedToWindow();
        }
        return getWindowToken() != null;
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void setState(int id, int screenWidth, int screenHeight) {
        this.mCurrentState = id;
        this.mBeginState = -1;
        this.mEndState = -1;
        if (this.mConstraintLayoutSpec != null) {
            this.mConstraintLayoutSpec.updateConstraints(id, (float) screenWidth, (float) screenHeight);
        } else if (this.mScene != null) {
            this.mScene.getConstraintSet(id).applyTo(this);
        }
    }

    public void setInterpolatedProgress(float pos) {
        Interpolator interpolator;
        if (this.mScene == null || (interpolator = this.mScene.getInterpolator()) == null) {
            setProgress(pos);
        } else {
            setProgress(interpolator.getInterpolation(pos));
        }
    }

    public void setProgress(float pos, float velocity) {
        if (!isAttachedToWindow()) {
            if (this.mStateCache == null) {
                this.mStateCache = new StateCache();
            }
            this.mStateCache.setProgress(pos);
            this.mStateCache.setVelocity(velocity);
            return;
        }
        setProgress(pos);
        this.mLastVelocity = velocity;
        animateTo(1.0f);
    }

    class StateCache {
        final String KeyEndState = "motion.EndState";
        final String KeyProgress = "motion.progress";
        final String KeyStartState = "motion.StartState";
        final String KeyVelocity = "motion.velocity";
        int endState = -1;
        float mProgress = Float.NaN;
        float mVelocity = Float.NaN;
        int startState = -1;

        StateCache() {
        }

        /* access modifiers changed from: package-private */
        public void apply() {
            if (!(this.startState == -1 && this.endState == -1)) {
                if (this.startState == -1) {
                    MotionLayout.this.transitionToState(this.endState);
                } else if (this.endState == -1) {
                    MotionLayout.this.setState(this.startState, -1, -1);
                } else {
                    MotionLayout.this.setTransition(this.startState, this.endState);
                }
            }
            if (!Float.isNaN(this.mVelocity)) {
                MotionLayout.this.setProgress(this.mProgress, this.mVelocity);
                this.mProgress = Float.NaN;
                this.mVelocity = Float.NaN;
                this.startState = -1;
                this.endState = -1;
            } else if (!Float.isNaN(this.mProgress)) {
                MotionLayout.this.setProgress(this.mProgress);
            }
        }

        public Bundle getTransitionState() {
            Bundle bundle = new Bundle();
            bundle.putFloat("motion.progress", this.mProgress);
            bundle.putFloat("motion.velocity", this.mVelocity);
            bundle.putInt("motion.StartState", this.startState);
            bundle.putInt("motion.EndState", this.endState);
            return bundle;
        }

        public void setTransitionState(Bundle bundle) {
            this.mProgress = bundle.getFloat("motion.progress");
            this.mVelocity = bundle.getFloat("motion.velocity");
            this.startState = bundle.getInt("motion.StartState");
            this.endState = bundle.getInt("motion.EndState");
        }

        public void setProgress(float progress) {
            this.mProgress = progress;
        }

        public void setEndState(int endState2) {
            this.endState = endState2;
        }

        public void setVelocity(float mVelocity2) {
            this.mVelocity = mVelocity2;
        }

        public void setStartState(int startState2) {
            this.startState = startState2;
        }

        public void recordState() {
            this.endState = MotionLayout.this.mEndState;
            this.startState = MotionLayout.this.mBeginState;
            this.mVelocity = MotionLayout.this.getVelocity();
            this.mProgress = MotionLayout.this.getProgress();
        }
    }

    public void setTransitionState(Bundle bundle) {
        if (this.mStateCache == null) {
            this.mStateCache = new StateCache();
        }
        this.mStateCache.setTransitionState(bundle);
        if (isAttachedToWindow()) {
            this.mStateCache.apply();
        }
    }

    public Bundle getTransitionState() {
        if (this.mStateCache == null) {
            this.mStateCache = new StateCache();
        }
        this.mStateCache.recordState();
        return this.mStateCache.getTransitionState();
    }

    public void setProgress(float pos) {
        if (!isAttachedToWindow()) {
            if (this.mStateCache == null) {
                this.mStateCache = new StateCache();
            }
            this.mStateCache.setProgress(pos);
            return;
        }
        if (pos <= 0.0f) {
            this.mCurrentState = this.mBeginState;
        } else if (pos >= 1.0f) {
            this.mCurrentState = this.mEndState;
        } else {
            this.mCurrentState = -1;
        }
        if (this.mScene != null) {
            this.mTransitionInstantly = true;
            this.mTransitionGoalPosition = pos;
            this.mTransitionPosition = pos;
            this.mTransitionLastTime = -1;
            this.mAnimationStartTime = -1;
            this.mInterpolator = null;
            this.mInTransition = true;
            invalidate();
        }
    }

    /* access modifiers changed from: private */
    public void setupMotionViews() {
        int n = getChildCount();
        this.mModel.build();
        this.mInTransition = true;
        int layoutWidth = getWidth();
        int layoutHeight = getHeight();
        int arc = this.mScene.gatPathMotionArc();
        if (arc != -1) {
            for (int i = 0; i < n; i++) {
                MotionController motionController = this.mFrameArrayList.get(getChildAt(i));
                if (motionController != null) {
                    motionController.setPathMotionArc(arc);
                }
            }
        }
        for (int i2 = 0; i2 < n; i2++) {
            MotionController motionController2 = this.mFrameArrayList.get(getChildAt(i2));
            if (motionController2 != null) {
                this.mScene.getKeyFrames(motionController2);
                motionController2.setup(layoutWidth, layoutHeight, this.mTransitionDuration, getNanoTime());
            }
        }
        float stagger = this.mScene.getStaggered();
        if (stagger != 0.0f) {
            boolean flip = ((double) stagger) < 0.0d;
            boolean useMotionStagger = false;
            float stagger2 = Math.abs(stagger);
            float min = Float.MAX_VALUE;
            float max = -3.4028235E38f;
            int i3 = 0;
            while (true) {
                if (i3 >= n) {
                    break;
                }
                MotionController f = this.mFrameArrayList.get(getChildAt(i3));
                if (!Float.isNaN(f.mMotionStagger)) {
                    useMotionStagger = true;
                    break;
                }
                float x = f.getFinalX();
                float y = f.getFinalY();
                float mdist = flip ? y - x : y + x;
                min = Math.min(min, mdist);
                max = Math.max(max, mdist);
                i3++;
            }
            if (useMotionStagger) {
                float min2 = Float.MAX_VALUE;
                float max2 = -3.4028235E38f;
                for (int i4 = 0; i4 < n; i4++) {
                    MotionController f2 = this.mFrameArrayList.get(getChildAt(i4));
                    if (!Float.isNaN(f2.mMotionStagger)) {
                        min2 = Math.min(min2, f2.mMotionStagger);
                        max2 = Math.max(max2, f2.mMotionStagger);
                    }
                }
                for (int i5 = 0; i5 < n; i5++) {
                    MotionController f3 = this.mFrameArrayList.get(getChildAt(i5));
                    if (!Float.isNaN(f3.mMotionStagger)) {
                        f3.mStaggerScale = 1.0f / (1.0f - stagger2);
                        if (flip) {
                            f3.mStaggerOffset = stagger2 - (((max2 - f3.mMotionStagger) / (max2 - min2)) * stagger2);
                        } else {
                            f3.mStaggerOffset = stagger2 - (((f3.mMotionStagger - min2) * stagger2) / (max2 - min2));
                        }
                    }
                }
                return;
            }
            for (int i6 = 0; i6 < n; i6++) {
                MotionController f4 = this.mFrameArrayList.get(getChildAt(i6));
                float x2 = f4.getFinalX();
                float y2 = f4.getFinalY();
                float mdist2 = flip ? y2 - x2 : y2 + x2;
                f4.mStaggerScale = 1.0f / (1.0f - stagger2);
                f4.mStaggerOffset = stagger2 - (((mdist2 - min) * stagger2) / (max - min));
            }
        }
    }

    public void touchAnimateTo(int touchUpMode, float position, float currentVelocity) {
        float f;
        float f2 = 1.0f;
        if (this.mScene != null && this.mTransitionLastPosition != position) {
            this.mTemporalInterpolator = true;
            this.mAnimationStartTime = getNanoTime();
            this.mTransitionDuration = ((float) this.mScene.getDuration()) / 1000.0f;
            this.mTransitionGoalPosition = position;
            this.mInTransition = true;
            switch (touchUpMode) {
                case 0:
                case 1:
                case 2:
                    if (touchUpMode == 1) {
                        position = 0.0f;
                    } else if (touchUpMode == 2) {
                        position = 1.0f;
                    }
                    this.mStopLogic.config(this.mTransitionLastPosition, position, currentVelocity, this.mTransitionDuration, this.mScene.getMaxAcceleration(), this.mScene.getMaxVelocity());
                    int currentState = this.mCurrentState;
                    if (position == 0.0f) {
                        f = 1.0f;
                    } else {
                        f = 0.0f;
                    }
                    setProgress(f);
                    this.mCurrentState = currentState;
                    this.mInterpolator = this.mStopLogic;
                    break;
                case 4:
                    this.mDecelerateLogic.config(currentVelocity, this.mTransitionLastPosition, this.mScene.getMaxAcceleration());
                    this.mInterpolator = this.mDecelerateLogic;
                    break;
                case 5:
                    if (!willJump(currentVelocity, this.mTransitionLastPosition, this.mScene.getMaxAcceleration())) {
                        this.mStopLogic.config(this.mTransitionLastPosition, position, currentVelocity, this.mTransitionDuration, this.mScene.getMaxAcceleration(), this.mScene.getMaxVelocity());
                        this.mLastVelocity = 0.0f;
                        int currentState2 = this.mCurrentState;
                        if (position != 0.0f) {
                            f2 = 0.0f;
                        }
                        setProgress(f2);
                        this.mCurrentState = currentState2;
                        this.mInterpolator = this.mStopLogic;
                        break;
                    } else {
                        this.mDecelerateLogic.config(currentVelocity, this.mTransitionLastPosition, this.mScene.getMaxAcceleration());
                        this.mInterpolator = this.mDecelerateLogic;
                        break;
                    }
            }
            this.mTransitionInstantly = false;
            this.mAnimationStartTime = getNanoTime();
            invalidate();
        }
    }

    private static boolean willJump(float velocity, float position, float maxAcceleration) {
        if (velocity > 0.0f) {
            float time = velocity / maxAcceleration;
            return position + ((velocity * time) - (((maxAcceleration * time) * time) / 2.0f)) > 1.0f;
        }
        float time2 = (-velocity) / maxAcceleration;
        return position + ((velocity * time2) + (((maxAcceleration * time2) * time2) / 2.0f)) < 0.0f;
    }

    class DecelerateInterpolator extends MotionInterpolator {
        float currentP = 0.0f;
        float initalV = 0.0f;
        float maxA;

        DecelerateInterpolator() {
        }

        public void config(float velocity, float position, float maxAcceleration) {
            this.initalV = velocity;
            this.currentP = position;
            this.maxA = maxAcceleration;
        }

        @Override // androidx.constraintlayout.motion.widget.MotionInterpolator
        public float getInterpolation(float time) {
            if (this.initalV > 0.0f) {
                if (this.initalV / this.maxA < time) {
                    time = this.initalV / this.maxA;
                }
                MotionLayout.this.mLastVelocity = this.initalV - (this.maxA * time);
                return this.currentP + ((this.initalV * time) - (((this.maxA * time) * time) / 2.0f));
            }
            if ((-this.initalV) / this.maxA < time) {
                time = (-this.initalV) / this.maxA;
            }
            MotionLayout.this.mLastVelocity = this.initalV + (this.maxA * time);
            return this.currentP + (this.initalV * time) + (((this.maxA * time) * time) / 2.0f);
        }

        @Override // androidx.constraintlayout.motion.widget.MotionInterpolator
        public float getVelocity() {
            return MotionLayout.this.mLastVelocity;
        }
    }

    /* access modifiers changed from: package-private */
    public void animateTo(float position) {
        if (this.mScene != null) {
            if (this.mTransitionLastPosition != this.mTransitionPosition && this.mTransitionInstantly) {
                this.mTransitionLastPosition = this.mTransitionPosition;
            }
            if (this.mTransitionLastPosition != position) {
                this.mTemporalInterpolator = false;
                float currentPosition = this.mTransitionLastPosition;
                this.mTransitionGoalPosition = position;
                this.mTransitionDuration = ((float) this.mScene.getDuration()) / 1000.0f;
                setProgress(this.mTransitionGoalPosition);
                this.mInterpolator = this.mScene.getInterpolator();
                this.mTransitionInstantly = false;
                this.mAnimationStartTime = getNanoTime();
                this.mInTransition = true;
                this.mTransitionPosition = currentPosition;
                this.mTransitionLastPosition = currentPosition;
                invalidate();
            }
        }
    }

    private void computeCurrentPositions() {
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            View v = getChildAt(i);
            MotionController frame = this.mFrameArrayList.get(v);
            if (frame != null) {
                frame.setStartCurrentState(v);
            }
        }
    }

    public void transitionToStart() {
        animateTo(0.0f);
    }

    public void transitionToEnd() {
        animateTo(1.0f);
    }

    public void transitionToState(int id) {
        if (!isAttachedToWindow()) {
            if (this.mStateCache == null) {
                this.mStateCache = new StateCache();
            }
            this.mStateCache.setEndState(id);
            return;
        }
        transitionToState(id, -1, -1);
    }

    public void transitionToState(int id, int screenWidth, int screenHeight) {
        if (!(this.mScene == null || this.mScene.mStateSet == null)) {
            int tmp_id = this.mScene.mStateSet.convertToConstraintSet(this.mCurrentState, id, (float) screenWidth, (float) screenHeight);
            if (tmp_id != -1) {
                id = tmp_id;
            }
        }
        if (this.mCurrentState != id) {
            if (this.mBeginState == id) {
                animateTo(0.0f);
            } else if (this.mEndState == id) {
                animateTo(1.0f);
            } else {
                this.mEndState = id;
                if (this.mCurrentState != -1) {
                    setTransition(this.mCurrentState, id);
                    animateTo(1.0f);
                    this.mTransitionLastPosition = 0.0f;
                    transitionToEnd();
                    return;
                }
                this.mTemporalInterpolator = false;
                this.mTransitionGoalPosition = 1.0f;
                this.mTransitionPosition = 0.0f;
                this.mTransitionLastPosition = 0.0f;
                this.mTransitionLastTime = getNanoTime();
                this.mAnimationStartTime = getNanoTime();
                this.mTransitionInstantly = false;
                this.mInterpolator = null;
                this.mTransitionDuration = ((float) this.mScene.getDuration()) / 1000.0f;
                this.mBeginState = -1;
                this.mScene.setTransition(this.mBeginState, this.mEndState);
                this.mScene.getStartId();
                int n = getChildCount();
                this.mFrameArrayList.clear();
                for (int i = 0; i < n; i++) {
                    View v = getChildAt(i);
                    this.mFrameArrayList.put(v, new MotionController(v));
                }
                this.mInTransition = true;
                this.mModel.initFrom(this.mLayoutWidget, null, this.mScene.getConstraintSet(id));
                rebuildScene();
                this.mModel.build();
                computeCurrentPositions();
                int layoutWidth = getWidth();
                int layoutHeight = getHeight();
                for (int i2 = 0; i2 < n; i2++) {
                    MotionController motionController = this.mFrameArrayList.get(getChildAt(i2));
                    this.mScene.getKeyFrames(motionController);
                    motionController.setup(layoutWidth, layoutHeight, this.mTransitionDuration, getNanoTime());
                }
                float stagger = this.mScene.getStaggered();
                if (stagger != 0.0f) {
                    float min = Float.MAX_VALUE;
                    float max = -3.4028235E38f;
                    for (int i3 = 0; i3 < n; i3++) {
                        MotionController f = this.mFrameArrayList.get(getChildAt(i3));
                        float x = f.getFinalX();
                        float y = f.getFinalY();
                        min = Math.min(min, y + x);
                        max = Math.max(max, y + x);
                    }
                    for (int i4 = 0; i4 < n; i4++) {
                        MotionController f2 = this.mFrameArrayList.get(getChildAt(i4));
                        float x2 = f2.getFinalX();
                        float y2 = f2.getFinalY();
                        f2.mStaggerScale = 1.0f / (1.0f - stagger);
                        f2.mStaggerOffset = stagger - ((((x2 + y2) - min) * stagger) / (max - min));
                    }
                }
                this.mTransitionPosition = 0.0f;
                this.mTransitionLastPosition = 0.0f;
                this.mInTransition = true;
                invalidate();
            }
        }
    }

    public float getVelocity() {
        return this.mLastVelocity;
    }

    public void getViewVelocity(View view, float posOnViewX, float posOnViewY, float[] returnVelocity, int type) {
        float v = this.mLastVelocity;
        float position = this.mTransitionLastPosition;
        if (this.mInterpolator != null) {
            float dir = Math.signum(this.mTransitionGoalPosition - this.mTransitionLastPosition);
            float interpos = this.mInterpolator.getInterpolation(this.mTransitionLastPosition + 1.0E-5f);
            position = this.mInterpolator.getInterpolation(this.mTransitionLastPosition);
            v = (dir * ((interpos - position) / 1.0E-5f)) / this.mTransitionDuration;
        }
        if (this.mInterpolator instanceof MotionInterpolator) {
            v = ((MotionInterpolator) this.mInterpolator).getVelocity();
        }
        MotionController f = this.mFrameArrayList.get(view);
        if ((type & 1) == 0) {
            f.getPostLayoutDvDp(position, view.getWidth(), view.getHeight(), posOnViewX, posOnViewY, returnVelocity);
        } else {
            f.getDpDt(position, posOnViewX, posOnViewY, returnVelocity);
        }
        if (type < 2) {
            returnVelocity[0] = returnVelocity[0] * v;
            returnVelocity[1] = returnVelocity[1] * v;
        }
    }

    class Model {
        ConstraintSet mEnd = null;
        int mEndId;
        ConstraintWidgetContainer mLayoutEnd = new ConstraintWidgetContainer();
        ConstraintWidgetContainer mLayoutStart = new ConstraintWidgetContainer();
        ConstraintSet mStart = null;
        int mStartId;

        Model() {
        }

        /* access modifiers changed from: package-private */
        public void copy(ConstraintWidgetContainer src, ConstraintWidgetContainer dest) {
            ConstraintWidget child_d;
            ArrayList<ConstraintWidget> children = src.getChildren();
            HashMap<ConstraintWidget, ConstraintWidget> map = new HashMap<>();
            map.put(src, dest);
            dest.getChildren().clear();
            dest.copy(src, map);
            Iterator<ConstraintWidget> it = children.iterator();
            while (it.hasNext()) {
                ConstraintWidget child_s = it.next();
                if (child_s instanceof Barrier) {
                    child_d = new Barrier();
                } else if (child_s instanceof Guideline) {
                    child_d = new Guideline();
                } else if (child_s instanceof Flow) {
                    child_d = new Flow();
                } else if (child_s instanceof Helper) {
                    child_d = new HelperWidget();
                } else {
                    child_d = new ConstraintWidget();
                }
                dest.add(child_d);
                map.put(child_s, child_d);
            }
            Iterator<ConstraintWidget> it2 = children.iterator();
            while (it2.hasNext()) {
                ConstraintWidget child_s2 = it2.next();
                map.get(child_s2).copy(child_s2, map);
            }
        }

        /* access modifiers changed from: package-private */
        public void initFrom(ConstraintWidgetContainer baseLayout, ConstraintSet start, ConstraintSet end) {
            this.mStart = start;
            this.mEnd = end;
            this.mLayoutStart.setMeasurer(MotionLayout.this.mLayoutWidget.getMeasurer());
            this.mLayoutEnd.setMeasurer(MotionLayout.this.mLayoutWidget.getMeasurer());
            this.mLayoutStart.removeAllChildren();
            this.mLayoutEnd.removeAllChildren();
            copy(MotionLayout.this.mLayoutWidget, this.mLayoutStart);
            copy(MotionLayout.this.mLayoutWidget, this.mLayoutEnd);
            if (start != null) {
                setupConstraintWidget(this.mLayoutStart, start);
            }
            setupConstraintWidget(this.mLayoutEnd, end);
            this.mLayoutStart.setRtl(MotionLayout.this.isRtl());
            this.mLayoutStart.updateHierarchy();
            this.mLayoutEnd.setRtl(MotionLayout.this.isRtl());
            this.mLayoutEnd.updateHierarchy();
            ViewGroup.LayoutParams layoutParams = MotionLayout.this.getLayoutParams();
            if (layoutParams != null) {
                if (layoutParams.width == -2) {
                    this.mLayoutStart.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                    this.mLayoutEnd.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                }
                if (layoutParams.height == -2) {
                    this.mLayoutStart.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                    this.mLayoutEnd.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.WRAP_CONTENT);
                }
            }
        }

        private void setupConstraintWidget(ConstraintWidgetContainer base, ConstraintSet cset) {
            SparseArray<ConstraintWidget> mapIdToWidget = new SparseArray<>();
            Constraints.LayoutParams layoutParams = new Constraints.LayoutParams(-2, -2);
            mapIdToWidget.clear();
            mapIdToWidget.put(0, base);
            mapIdToWidget.put(MotionLayout.this.getId(), base);
            Iterator it = base.getChildren().iterator();
            while (it.hasNext()) {
                ConstraintWidget child = (ConstraintWidget) it.next();
                mapIdToWidget.put(((View) child.getCompanionWidget()).getId(), child);
            }
            Iterator it2 = base.getChildren().iterator();
            while (it2.hasNext()) {
                ConstraintWidget child2 = (ConstraintWidget) it2.next();
                View view = (View) child2.getCompanionWidget();
                cset.applyToLayoutParams(view.getId(), layoutParams);
                child2.setWidth(cset.getWidth(view.getId()));
                child2.setHeight(cset.getHeight(view.getId()));
                if (view instanceof ConstraintHelper) {
                    cset.applyToHelper((ConstraintHelper) view, child2, layoutParams, mapIdToWidget);
                    if (view instanceof androidx.constraintlayout.widget.Barrier) {
                        ((androidx.constraintlayout.widget.Barrier) view).validateParams();
                    }
                }
                if (Build.VERSION.SDK_INT >= 17) {
                    layoutParams.resolveLayoutDirection(MotionLayout.this.getLayoutDirection());
                } else {
                    layoutParams.resolveLayoutDirection(0);
                }
                MotionLayout.this.applyConstraintsFromLayoutParams(false, view, child2, layoutParams, mapIdToWidget);
                if (cset.getVisibilityMode(view.getId()) == 1) {
                    child2.setVisibility(view.getVisibility());
                } else {
                    child2.setVisibility(cset.getVisibility(view.getId()));
                }
            }
            Iterator it3 = base.getChildren().iterator();
            while (it3.hasNext()) {
                ConstraintWidget child3 = (ConstraintWidget) it3.next();
                if (child3 instanceof VirtualLayout) {
                    ConstraintHelper viewx = (ConstraintHelper)child3.getCompanionWidget();
                    Helper helper = (Helper)child3;
                    viewx.updatePreLayout(base, helper, mapIdToWidget);
                    VirtualLayout virtualLayout = (VirtualLayout)helper;
                    virtualLayout.captureWidgets();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public ConstraintWidget getWidget(ConstraintWidgetContainer container, View view) {
            if (container.getCompanionWidget() == view) {
                return container;
            }
            ArrayList<ConstraintWidget> children = container.getChildren();
            int count = children.size();
            for (int i = 0; i < count; i++) {
                ConstraintWidget widget = children.get(i);
                if (widget.getCompanionWidget() == view) {
                    return widget;
                }
            }
            return null;
        }

        private void debugLayoutParam(String str, ConstraintLayout.LayoutParams params) {
            Log.v(MotionLayout.TAG, str + ((((((((((((" " + (params.startToStart != -1 ? "SS" : "__")) + (params.startToEnd != -1 ? "|SE" : "|__")) + (params.endToStart != -1 ? "|ES" : "|__")) + (params.endToEnd != -1 ? "|EE" : "|__")) + (params.leftToLeft != -1 ? "|LL" : "|__")) + (params.leftToRight != -1 ? "|LR" : "|__")) + (params.rightToLeft != -1 ? "|RL" : "|__")) + (params.rightToRight != -1 ? "|RR" : "|__")) + (params.topToTop != -1 ? "|TT" : "|__")) + (params.topToBottom != -1 ? "|TB" : "|__")) + (params.bottomToTop != -1 ? "|BT" : "|__")) + (params.bottomToBottom != -1 ? "|BB" : "|__")));
        }

        private void debugWidget(String str, ConstraintWidget child) {
            String str2;
            String str3;
            String str4;
            String str5;
            StringBuilder append = new StringBuilder().append(" ");
            if (child.mTop.mTarget != null) {
                str2 = "T" + (child.mTop.mTarget.mType == ConstraintAnchor.Type.TOP ? "T" : "B");
            } else {
                str2 = "__";
            }
            StringBuilder append2 = new StringBuilder().append(append.append(str2).toString());
            if (child.mBottom.mTarget != null) {
                str3 = "B" + (child.mBottom.mTarget.mType == ConstraintAnchor.Type.TOP ? "T" : "B");
            } else {
                str3 = "__";
            }
            StringBuilder append3 = new StringBuilder().append(append2.append(str3).toString());
            if (child.mLeft.mTarget != null) {
                str4 = "L" + (child.mLeft.mTarget.mType == ConstraintAnchor.Type.LEFT ? "L" : "R");
            } else {
                str4 = "__";
            }
            StringBuilder append4 = new StringBuilder().append(append3.append(str4).toString());
            if (child.mRight.mTarget != null) {
                str5 = "R" + (child.mRight.mTarget.mType == ConstraintAnchor.Type.LEFT ? "L" : "R");
            } else {
                str5 = "__";
            }
            Log.v(MotionLayout.TAG, str + append4.append(str5).toString() + " ---  " + child);
        }

        private void debugLayout(String title, ConstraintWidgetContainer c) {
            String cName = title + " " + Debug.getName((View) c.getCompanionWidget());
            Log.v(MotionLayout.TAG, cName + "  ========= " + c);
            int count = c.getChildren().size();
            for (int i = 0; i < count; i++) {
                String str = cName + "[" + i + "] ";
                ConstraintWidget child = (ConstraintWidget) c.getChildren().get(i);
                String a = ((("" + (child.mTop.mTarget != null ? "T" : "_")) + (child.mBottom.mTarget != null ? "B" : "_")) + (child.mLeft.mTarget != null ? "L" : "_")) + (child.mRight.mTarget != null ? "R" : "_");
                View v = (View) child.getCompanionWidget();
                String name = Debug.getName(v);
                if (v instanceof TextView) {
                    name = name + "(" + ((Object) ((TextView) v).getText()) + ")";
                }
                Log.v(MotionLayout.TAG, str + "  " + name + " " + child + " " + a);
            }
            Log.v(MotionLayout.TAG, cName + " done. ");
        }

        public void reEvaluateState() {
            measure(MotionLayout.this.mLastWidthMeasureSpec, MotionLayout.this.mLastHeightMeasureSpec);
            MotionLayout.this.setupMotionViews();
        }

        public void measure(int widthMeasureSpec, int heightMeasureSpec) {
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            MotionLayout.this.mWidthMeasureMode = widthMode;
            MotionLayout.this.mHeightMeasureMode = heightMode;
            int optimisationLevel = MotionLayout.this.getOptimizationLevel();
            if (MotionLayout.this.mCurrentState == MotionLayout.this.getStartState()) {
                MotionLayout.this.resolveSystem(this.mLayoutEnd, optimisationLevel, widthMeasureSpec, heightMeasureSpec);
                if (this.mStart != null) {
                    MotionLayout.this.resolveSystem(this.mLayoutStart, optimisationLevel, widthMeasureSpec, heightMeasureSpec);
                }
            } else {
                if (this.mStart != null) {
                    MotionLayout.this.resolveSystem(this.mLayoutStart, optimisationLevel, widthMeasureSpec, heightMeasureSpec);
                }
                MotionLayout.this.resolveSystem(this.mLayoutEnd, optimisationLevel, widthMeasureSpec, heightMeasureSpec);
            }
            MotionLayout.this.mStartWrapWidth = this.mLayoutStart.getWidth();
            MotionLayout.this.mStartWrapHeight = this.mLayoutStart.getHeight();
            MotionLayout.this.mEndWrapWidth = this.mLayoutEnd.getWidth();
            MotionLayout.this.mEndWrapHeight = this.mLayoutEnd.getHeight();
            MotionLayout.this.mMeasureDuringTransition = (MotionLayout.this.mStartWrapWidth == MotionLayout.this.mEndWrapWidth && MotionLayout.this.mStartWrapHeight == MotionLayout.this.mEndWrapHeight) ? false : true;
            int width = MotionLayout.this.mStartWrapWidth;
            int height = MotionLayout.this.mStartWrapHeight;
            if (MotionLayout.this.mWidthMeasureMode == Integer.MIN_VALUE) {
                width = (int) (((float) MotionLayout.this.mStartWrapWidth) + (MotionLayout.this.mPostInterpolationPosition * ((float) (MotionLayout.this.mEndWrapWidth - MotionLayout.this.mStartWrapWidth))));
            }
            if (MotionLayout.this.mHeightMeasureMode == Integer.MIN_VALUE) {
                height = (int) (((float) MotionLayout.this.mStartWrapHeight) + (MotionLayout.this.mPostInterpolationPosition * ((float) (MotionLayout.this.mEndWrapHeight - MotionLayout.this.mStartWrapHeight))));
            }
            MotionLayout.this.resolveMeasuredDimension(widthMeasureSpec, heightMeasureSpec, width, height, this.mLayoutStart.isWidthMeasuredTooSmall() || this.mLayoutEnd.isWidthMeasuredTooSmall(), this.mLayoutStart.isHeightMeasuredTooSmall() || this.mLayoutEnd.isHeightMeasuredTooSmall());
        }

        public void build() {
            int n = MotionLayout.this.getChildCount();
            MotionLayout.this.mFrameArrayList.clear();
            for (int i = 0; i < n; i++) {
                View v = MotionLayout.this.getChildAt(i);
                MotionLayout.this.mFrameArrayList.put(v, new MotionController(v));
            }
            for (int i2 = 0; i2 < n; i2++) {
                View v2 = MotionLayout.this.getChildAt(i2);
                MotionController motionController = MotionLayout.this.mFrameArrayList.get(v2);
                if (motionController != null) {
                    if (this.mStart != null) {
                        ConstraintWidget startWidget = getWidget(this.mLayoutStart, v2);
                        if (startWidget != null) {
                            motionController.setStartState(startWidget, this.mStart);
                        } else if (MotionLayout.this.mDebugPath != 0) {
                            Log.e(MotionLayout.TAG, Debug.getLocation() + "no widget for  " + Debug.getName(v2) + " (" + v2.getClass().getName() + ")");
                        }
                    }
                    if (this.mEnd != null) {
                        ConstraintWidget endWidget = getWidget(this.mLayoutEnd, v2);
                        if (endWidget != null) {
                            motionController.setEndState(endWidget, this.mEnd);
                        } else if (MotionLayout.this.mDebugPath != 0) {
                            Log.e(MotionLayout.TAG, Debug.getLocation() + "no widget for  " + Debug.getName(v2) + " (" + v2.getClass().getName() + ")");
                        }
                    }
                }
            }
        }

        public void setMeasuredId(int startId, int endId) {
            this.mStartId = startId;
            this.mEndId = endId;
        }

        public boolean isNotConfiguredWith(int startId, int endId) {
            return (startId == this.mStartId && endId == this.mEndId) ? false : true;
        }
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void requestLayout() {
        if (this.mMeasureDuringTransition || this.mCurrentState != -1 || this.mScene == null || this.mScene.mCurrentTransition == null || this.mScene.mCurrentTransition.getLayoutDuringTransition() != 0) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean recalc;
        if (this.mScene == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (this.mLastWidthMeasureSpec == widthMeasureSpec && this.mLastHeightMeasureSpec == heightMeasureSpec) {
            recalc = false;
        } else {
            recalc = true;
        }
        if (this.mNeedsFireTransitionCompleted) {
            this.mNeedsFireTransitionCompleted = false;
            onNewStateAttachHandlers();
            if (this.mIsAnimating) {
                processTransitionCompleted();
            }
            recalc = true;
        }
        if (this.mDirtyHierarchy) {
            recalc = true;
        }
        this.mLastWidthMeasureSpec = widthMeasureSpec;
        this.mLastHeightMeasureSpec = heightMeasureSpec;
        int startId = this.mScene.getStartId();
        int endId = this.mScene.getEndId();
        if ((recalc || this.mModel.isNotConfiguredWith(startId, endId)) && this.mBeginState != -1) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.mModel.initFrom(this.mLayoutWidget, this.mScene.getConstraintSet(startId), this.mScene.getConstraintSet(endId));
            this.mModel.reEvaluateState();
            this.mModel.setMeasuredId(startId, endId);
        } else {
            int heightPadding = getPaddingTop() + getPaddingBottom();
            int androidLayoutWidth = this.mLayoutWidget.getWidth() + getPaddingLeft() + getPaddingRight();
            int androidLayoutHeight = this.mLayoutWidget.getHeight() + heightPadding;
            if (this.mWidthMeasureMode == Integer.MIN_VALUE) {
                androidLayoutWidth = (int) (((float) this.mStartWrapWidth) + (this.mPostInterpolationPosition * ((float) (this.mEndWrapWidth - this.mStartWrapWidth))));
                requestLayout();
            }
            if (this.mHeightMeasureMode == Integer.MIN_VALUE) {
                androidLayoutHeight = (int) (((float) this.mStartWrapHeight) + (this.mPostInterpolationPosition * ((float) (this.mEndWrapHeight - this.mStartWrapHeight))));
                requestLayout();
            }
            setMeasuredDimension(androidLayoutWidth, androidLayoutHeight);
        }
        evaluateLayout();
    }

    public boolean onStartNestedScroll(View child, View target, int axes, int type) {
        if (this.mScene == null || this.mScene.mCurrentTransition == null || this.mScene.mCurrentTransition.getTouchResponse() == null || (this.mScene.mCurrentTransition.getTouchResponse().getFlags() & 2) != 0) {
            return false;
        }
        return true;
    }

    public void onNestedScrollAccepted(View child, View target, int axes, int type) {
    }

    public void onStopNestedScroll(View target, int type) {
        if (this.mScene != null) {
            this.mScene.processScrollUp(this.mScrollTargetDX / this.mScrollTargetDT, this.mScrollTargetDY / this.mScrollTargetDT);
        }
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
        if (!(!this.mUndergoingMotion && dxConsumed == 0 && dyConsumed == 0)) {
            consumed[0] = consumed[0] + dxUnconsumed;
            consumed[1] = consumed[1] + dyUnconsumed;
        }
        this.mUndergoingMotion = false;
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    public void onNestedPreScroll(final View target, int dx, int dy, int[] consumed, int type) {
        TouchResponse touchResponse;
        int regionId;
        if (this.mScene != null && this.mScene.mCurrentTransition != null && this.mScene.mCurrentTransition.isEnabled()) {
            MotionScene.Transition currentTransition = this.mScene.mCurrentTransition;
            if (currentTransition != null && currentTransition.isEnabled() && (touchResponse = currentTransition.getTouchResponse()) != null && (regionId = touchResponse.getTouchRegionId()) != -1 && target.getId() != regionId) {
                return;
            }
            if (this.mScene == null || !this.mScene.getMoveWhenScrollAtTop() || (!(this.mTransitionPosition == 1.0f || this.mTransitionPosition == 0.0f) || !target.canScrollVertically(-1))) {
                if (!(currentTransition.getTouchResponse() == null || (this.mScene.mCurrentTransition.getTouchResponse().getFlags() & 1) == 0)) {
                    float dir = this.mScene.getProgressDirection((float) dx, (float) dy);
                    if ((this.mTransitionLastPosition <= 0.0f && dir < 0.0f) || (this.mTransitionLastPosition >= 1.0f && dir > 0.0f)) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            target.setNestedScrollingEnabled(false);
                            target.post(new Runnable() {
                                /* class androidx.constraintlayout.motion.widget.MotionLayout.AnonymousClass1 */

                                public void run() {
                                    target.setNestedScrollingEnabled(true);
                                }
                            });
                            return;
                        }
                        return;
                    }
                }
                float progress = this.mTransitionPosition;
                long time = getNanoTime();
                this.mScrollTargetDX = (float) dx;
                this.mScrollTargetDY = (float) dy;
                this.mScrollTargetDT = (float) (((double) (time - this.mScrollTargetTime)) * 1.0E-9d);
                this.mScrollTargetTime = time;
                this.mScene.processScrollMove((float) dx, (float) dy);
                if (progress != this.mTransitionPosition) {
                    consumed[0] = dx;
                    consumed[1] = dy;
                }
                evaluate(false);
                if (consumed[0] != 0 || consumed[1] != 0) {
                    this.mUndergoingMotion = true;
                }
            }
        }
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    private class DevModeDraw {
        private static final int DEBUG_PATH_TICKS_PER_MS = 16;
        final int DIAMOND_SIZE = 10;
        final int GRAPH_COLOR = -13391360;
        final int KEYFRAME_COLOR = -2067046;
        final int RED_COLOR = -21965;
        final int SHADOW_COLOR = 1996488704;
        Rect mBounds = new Rect();
        DashPathEffect mDashPathEffect;
        Paint mFillPaint;
        int mKeyFrameCount;
        float[] mKeyFramePoints;
        Paint mPaint = new Paint();
        Paint mPaintGraph;
        Paint mPaintKeyframes;
        Path mPath;
        int[] mPathMode;
        float[] mPoints;
        boolean mPresentationMode = false;
        private float[] mRectangle;
        int mShadowTranslate = 1;
        Paint mTextPaint;

        public DevModeDraw() {
            this.mPaint.setAntiAlias(true);
            this.mPaint.setColor(-21965);
            this.mPaint.setStrokeWidth(2.0f);
            this.mPaint.setStyle(Paint.Style.STROKE);
            this.mPaintKeyframes = new Paint();
            this.mPaintKeyframes.setAntiAlias(true);
            this.mPaintKeyframes.setColor(-2067046);
            this.mPaintKeyframes.setStrokeWidth(2.0f);
            this.mPaintKeyframes.setStyle(Paint.Style.STROKE);
            this.mPaintGraph = new Paint();
            this.mPaintGraph.setAntiAlias(true);
            this.mPaintGraph.setColor(-13391360);
            this.mPaintGraph.setStrokeWidth(2.0f);
            this.mPaintGraph.setStyle(Paint.Style.STROKE);
            this.mTextPaint = new Paint();
            this.mTextPaint.setAntiAlias(true);
            this.mTextPaint.setColor(-13391360);
            this.mTextPaint.setTextSize(12.0f * MotionLayout.this.getContext().getResources().getDisplayMetrics().density);
            this.mRectangle = new float[8];
            this.mFillPaint = new Paint();
            this.mFillPaint.setAntiAlias(true);
            this.mDashPathEffect = new DashPathEffect(new float[]{4.0f, 8.0f}, 0.0f);
            this.mPaintGraph.setPathEffect(this.mDashPathEffect);
            this.mKeyFramePoints = new float[100];
            this.mPathMode = new int[50];
            if (this.mPresentationMode) {
                this.mPaint.setStrokeWidth(8.0f);
                this.mFillPaint.setStrokeWidth(8.0f);
                this.mPaintKeyframes.setStrokeWidth(8.0f);
                this.mShadowTranslate = 4;
            }
        }

        public void draw(Canvas canvas, HashMap<View, MotionController> frameArrayList, int duration, int debugPath) {
            if (frameArrayList != null && frameArrayList.size() != 0) {
                canvas.save();
                if (!MotionLayout.this.isInEditMode() && (debugPath & 1) == 2) {
                    String str = MotionLayout.this.getContext().getResources().getResourceName(MotionLayout.this.mEndState) + ":" + MotionLayout.this.getProgress();
                    canvas.drawText(str, 10.0f, (float) (MotionLayout.this.getHeight() - 30), this.mTextPaint);
                    canvas.drawText(str, 11.0f, (float) (MotionLayout.this.getHeight() - 29), this.mPaint);
                }
                for (MotionController motionController : frameArrayList.values()) {
                    int mode = motionController.getDrawPath();
                    if (debugPath > 0 && mode == 0) {
                        mode = 1;
                    }
                    if (mode != 0) {
                        this.mKeyFrameCount = motionController.buildKeyFrames(this.mKeyFramePoints, this.mPathMode);
                        if (mode >= 1) {
                            int frames = duration / 16;
                            if (this.mPoints == null || this.mPoints.length != frames * 2) {
                                this.mPoints = new float[(frames * 2)];
                                this.mPath = new Path();
                            }
                            canvas.translate((float) this.mShadowTranslate, (float) this.mShadowTranslate);
                            this.mPaint.setColor(1996488704);
                            this.mFillPaint.setColor(1996488704);
                            this.mPaintKeyframes.setColor(1996488704);
                            this.mPaintGraph.setColor(1996488704);
                            motionController.buildPath(this.mPoints, frames);
                            drawAll(canvas, mode, this.mKeyFrameCount, motionController);
                            this.mPaint.setColor(-21965);
                            this.mPaintKeyframes.setColor(-2067046);
                            this.mFillPaint.setColor(-2067046);
                            this.mPaintGraph.setColor(-13391360);
                            canvas.translate((float) (-this.mShadowTranslate), (float) (-this.mShadowTranslate));
                            drawAll(canvas, mode, this.mKeyFrameCount, motionController);
                            if (mode == 5) {
                                drawRectangle(canvas, motionController);
                            }
                        }
                    }
                }
                canvas.restore();
            }
        }

        public void drawAll(Canvas canvas, int mode, int keyFrames, MotionController motionController) {
            if (mode == 4) {
                drawPathAsConfigured(canvas);
            }
            if (mode == 2) {
                drawPathRelative(canvas);
            }
            if (mode == 3) {
                drawPathCartesian(canvas);
            }
            drawBasicPath(canvas);
            drawTicks(canvas, mode, keyFrames, motionController);
        }

        private void drawBasicPath(Canvas canvas) {
            canvas.drawLines(this.mPoints, this.mPaint);
        }

        private void drawTicks(Canvas canvas, int mode, int keyFrames, MotionController motionController) {
            int viewWidth = 0;
            int viewHeight = 0;
            if (motionController.mView != null) {
                viewWidth = motionController.mView.getWidth();
                viewHeight = motionController.mView.getHeight();
            }
            for (int i = 1; i < keyFrames - 1; i++) {
                if (mode != 4 || this.mPathMode[i - 1] != 0) {
                    float x = this.mKeyFramePoints[i * 2];
                    float y = this.mKeyFramePoints[(i * 2) + 1];
                    this.mPath.reset();
                    this.mPath.moveTo(x, 10.0f + y);
                    this.mPath.lineTo(10.0f + x, y);
                    this.mPath.lineTo(x, y - 10.0f);
                    this.mPath.lineTo(x - 10.0f, y);
                    this.mPath.close();
                    motionController.getKeyFrame(i - 1);
                    if (mode == 4) {
                        if (this.mPathMode[i - 1] == 1) {
                            drawPathRelativeTicks(canvas, x - 0.0f, y - 0.0f);
                        } else if (this.mPathMode[i - 1] == 2) {
                            drawPathCartesianTicks(canvas, x - 0.0f, y - 0.0f);
                        } else if (this.mPathMode[i - 1] == 3) {
                            drawPathScreenTicks(canvas, x - 0.0f, y - 0.0f, viewWidth, viewHeight);
                        }
                        canvas.drawPath(this.mPath, this.mFillPaint);
                    }
                    if (mode == 2) {
                        drawPathRelativeTicks(canvas, x - 0.0f, y - 0.0f);
                    }
                    if (mode == 3) {
                        drawPathCartesianTicks(canvas, x - 0.0f, y - 0.0f);
                    }
                    if (mode == 6) {
                        drawPathScreenTicks(canvas, x - 0.0f, y - 0.0f, viewWidth, viewHeight);
                    }
                    if (0.0f == 0.0f && 0.0f == 0.0f) {
                        canvas.drawPath(this.mPath, this.mFillPaint);
                    } else {
                        drawTranslation(canvas, x - 0.0f, y - 0.0f, x, y);
                    }
                }
            }
            if (this.mPoints.length > 1) {
                canvas.drawCircle(this.mPoints[0], this.mPoints[1], 8.0f, this.mPaintKeyframes);
                canvas.drawCircle(this.mPoints[this.mPoints.length - 2], this.mPoints[this.mPoints.length - 1], 8.0f, this.mPaintKeyframes);
            }
        }

        private void drawTranslation(Canvas canvas, float x1, float y1, float x2, float y2) {
            canvas.drawRect(x1, y1, x2, y2, this.mPaintGraph);
            canvas.drawLine(x1, y1, x2, y2, this.mPaintGraph);
        }

        private void drawPathRelative(Canvas canvas) {
            canvas.drawLine(this.mPoints[0], this.mPoints[1], this.mPoints[this.mPoints.length - 2], this.mPoints[this.mPoints.length - 1], this.mPaintGraph);
        }

        private void drawPathAsConfigured(Canvas canvas) {
            boolean path = false;
            boolean cart = false;
            for (int i = 0; i < this.mKeyFrameCount; i++) {
                if (this.mPathMode[i] == 1) {
                    path = true;
                }
                if (this.mPathMode[i] == 2) {
                    cart = true;
                }
            }
            if (path) {
                drawPathRelative(canvas);
            }
            if (cart) {
                drawPathCartesian(canvas);
            }
        }

        private void drawPathRelativeTicks(Canvas canvas, float x, float y) {
            float x1 = this.mPoints[0];
            float y1 = this.mPoints[1];
            float x2 = this.mPoints[this.mPoints.length - 2];
            float y2 = this.mPoints[this.mPoints.length - 1];
            float dist = (float) Math.hypot((double) (x1 - x2), (double) (y1 - y2));
            float t = (((x - x1) * (x2 - x1)) + ((y - y1) * (y2 - y1))) / (dist * dist);
            float xp = x1 + ((x2 - x1) * t);
            float yp = y1 + ((y2 - y1) * t);
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(xp, yp);
            float len = (float) Math.hypot((double) (xp - x), (double) (yp - y));
            String text = "" + (((float) ((int) ((100.0f * len) / dist))) / 100.0f);
            getTextBounds(text, this.mTextPaint);
            canvas.drawTextOnPath(text, path, (len / 2.0f) - ((float) (this.mBounds.width() / 2)), -20.0f, this.mTextPaint);
            canvas.drawLine(x, y, xp, yp, this.mPaintGraph);
        }

        /* access modifiers changed from: package-private */
        public void getTextBounds(String text, Paint paint) {
            paint.getTextBounds(text, 0, text.length(), this.mBounds);
        }

        private void drawPathCartesian(Canvas canvas) {
            float x1 = this.mPoints[0];
            float y1 = this.mPoints[1];
            float x2 = this.mPoints[this.mPoints.length - 2];
            float y2 = this.mPoints[this.mPoints.length - 1];
            canvas.drawLine(Math.min(x1, x2), Math.max(y1, y2), Math.max(x1, x2), Math.max(y1, y2), this.mPaintGraph);
            canvas.drawLine(Math.min(x1, x2), Math.min(y1, y2), Math.min(x1, x2), Math.max(y1, y2), this.mPaintGraph);
        }

        private void drawPathCartesianTicks(Canvas canvas, float x, float y) {
            float x1 = this.mPoints[0];
            float y1 = this.mPoints[1];
            float x2 = this.mPoints[this.mPoints.length - 2];
            float y2 = this.mPoints[this.mPoints.length - 1];
            float minx = Math.min(x1, x2);
            float maxy = Math.max(y1, y2);
            float xgap = x - Math.min(x1, x2);
            float ygap = Math.max(y1, y2) - y;
            String text = "" + (((float) ((int) (0.5d + ((double) ((100.0f * xgap) / Math.abs(x2 - x1)))))) / 100.0f);
            getTextBounds(text, this.mTextPaint);
            canvas.drawText(text, ((xgap / 2.0f) - ((float) (this.mBounds.width() / 2))) + minx, y - 20.0f, this.mTextPaint);
            canvas.drawLine(x, y, Math.min(x1, x2), y, this.mPaintGraph);
            String text2 = "" + (((float) ((int) (0.5d + ((double) ((100.0f * ygap) / Math.abs(y2 - y1)))))) / 100.0f);
            getTextBounds(text2, this.mTextPaint);
            canvas.drawText(text2, 5.0f + x, maxy - ((ygap / 2.0f) - ((float) (this.mBounds.height() / 2))), this.mTextPaint);
            canvas.drawLine(x, y, x, Math.max(y1, y2), this.mPaintGraph);
        }

        private void drawPathScreenTicks(Canvas canvas, float x, float y, int viewWidth, int viewHeight) {
            String text = "" + (((float) ((int) (0.5d + ((double) ((100.0f * (x - ((float) (viewWidth / 2)))) / ((float) (MotionLayout.this.getWidth() - viewWidth))))))) / 100.0f);
            getTextBounds(text, this.mTextPaint);
            canvas.drawText(text, ((x / 2.0f) - ((float) (this.mBounds.width() / 2))) + 0.0f, y - 20.0f, this.mTextPaint);
            canvas.drawLine(x, y, Math.min(0.0f, 1.0f), y, this.mPaintGraph);
            String text2 = "" + (((float) ((int) (0.5d + ((double) ((100.0f * (y - ((float) (viewHeight / 2)))) / ((float) (MotionLayout.this.getHeight() - viewHeight))))))) / 100.0f);
            getTextBounds(text2, this.mTextPaint);
            canvas.drawText(text2, 5.0f + x, 0.0f - ((y / 2.0f) - ((float) (this.mBounds.height() / 2))), this.mTextPaint);
            canvas.drawLine(x, y, x, Math.max(0.0f, 1.0f), this.mPaintGraph);
        }

        private void drawRectangle(Canvas canvas, MotionController motionController) {
            this.mPath.reset();
            for (int i = 0; i <= 50; i++) {
                motionController.buildRect(((float) i) / ((float) 50), this.mRectangle, 0);
                this.mPath.moveTo(this.mRectangle[0], this.mRectangle[1]);
                this.mPath.lineTo(this.mRectangle[2], this.mRectangle[3]);
                this.mPath.lineTo(this.mRectangle[4], this.mRectangle[5]);
                this.mPath.lineTo(this.mRectangle[6], this.mRectangle[7]);
                this.mPath.close();
            }
            this.mPaint.setColor(1140850688);
            canvas.translate(2.0f, 2.0f);
            canvas.drawPath(this.mPath, this.mPaint);
            canvas.translate(-2.0f, -2.0f);
            this.mPaint.setColor(-65536);
            canvas.drawPath(this.mPath, this.mPaint);
        }
    }

    private void debugPos() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Log.v(TAG, " " + Debug.getLocation() + " " + Debug.getName(this) + " " + Debug.getName(getContext(), this.mCurrentState) + " " + Debug.getName(child) + child.getLeft() + " " + child.getTop());
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void dispatchDraw(Canvas canvas) {
        String state;
        evaluate(false);
        super.dispatchDraw(canvas);
        if (this.mScene != null) {
            if ((this.mDebugPath & 1) == 1 && !isInEditMode()) {
                this.mFrames++;
                long currentDrawTime = getNanoTime();
                if (this.mLastDrawTime != -1) {
                    long delay = currentDrawTime - this.mLastDrawTime;
                    if (delay > 200000000) {
                        this.mLastFps = ((float) ((int) (100.0f * (((float) this.mFrames) / (((float) delay) * 1.0E-9f))))) / 100.0f;
                        this.mFrames = 0;
                        this.mLastDrawTime = currentDrawTime;
                    }
                } else {
                    this.mLastDrawTime = currentDrawTime;
                }
                Paint paint = new Paint();
                paint.setTextSize(42.0f);
                StringBuilder append = new StringBuilder().append(this.mLastFps + " fps " + Debug.getState(this, this.mBeginState) + " -> ").append(Debug.getState(this, this.mEndState)).append(" (progress: ").append(((float) ((int) (getProgress() * 1000.0f))) / 10.0f).append(" ) state=");
                if (this.mCurrentState == -1) {
                    state = "undefined";
                } else {
                    state = Debug.getState(this, this.mCurrentState);
                }
                String str = append.append(state).toString();
                paint.setColor(-16777216);
                canvas.drawText(str, 11.0f, (float) (getHeight() - 29), paint);
                paint.setColor(-7864184);
                canvas.drawText(str, 10.0f, (float) (getHeight() - 30), paint);
            }
            if (this.mDebugPath > 1) {
                if (this.mDevModeDraw == null) {
                    this.mDevModeDraw = new DevModeDraw();
                }
                this.mDevModeDraw.draw(canvas, this.mFrameArrayList, this.mScene.getDuration(), this.mDebugPath);
            }
        }
    }

    private void evaluateLayout() {
        float dir = Math.signum(this.mTransitionGoalPosition - this.mTransitionLastPosition);
        long currentTime = getNanoTime();
        float deltaPos = 0.0f;
        if (!(this.mInterpolator instanceof StopLogic)) {
            deltaPos = ((((float) (currentTime - this.mTransitionLastTime)) * dir) * 1.0E-9f) / this.mTransitionDuration;
        }
        float position = this.mTransitionLastPosition + deltaPos;
        boolean done = false;
        if (this.mTransitionInstantly) {
            position = this.mTransitionGoalPosition;
        }
        if ((dir > 0.0f && position >= this.mTransitionGoalPosition) || (dir <= 0.0f && position <= this.mTransitionGoalPosition)) {
            position = this.mTransitionGoalPosition;
            done = true;
        }
        if (this.mInterpolator != null && !done) {
            if (this.mTemporalInterpolator) {
                position = this.mInterpolator.getInterpolation(((float) (currentTime - this.mAnimationStartTime)) * 1.0E-9f);
            } else {
                position = this.mInterpolator.getInterpolation(position);
            }
        }
        if ((dir > 0.0f && position >= this.mTransitionGoalPosition) || (dir <= 0.0f && position <= this.mTransitionGoalPosition)) {
            position = this.mTransitionGoalPosition;
        }
        this.mPostInterpolationPosition = position;
        int n = getChildCount();
        long time = getNanoTime();
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            MotionController frame = this.mFrameArrayList.get(child);
            if (frame != null) {
                frame.interpolate(child, position, time, this.mKeyCache);
            }
        }
        if (this.mMeasureDuringTransition) {
            requestLayout();
        }
    }

    /* access modifiers changed from: package-private */
    public void evaluate(boolean force) {
        if (this.mTransitionLastTime == -1) {
            this.mTransitionLastTime = getNanoTime();
        }
        if (this.mTransitionLastPosition > 0.0f && this.mTransitionLastPosition < 1.0f) {
            this.mCurrentState = -1;
        }
        boolean newState = false;
        if (this.mKeepAnimating || (this.mInTransition && (force || this.mTransitionGoalPosition != this.mTransitionLastPosition))) {
            float dir = Math.signum(this.mTransitionGoalPosition - this.mTransitionLastPosition);
            long currentTime = getNanoTime();
            float deltaPos = 0.0f;
            if (!(this.mInterpolator instanceof MotionInterpolator)) {
                deltaPos = ((((float) (currentTime - this.mTransitionLastTime)) * dir) * 1.0E-9f) / this.mTransitionDuration;
            }
            float position = this.mTransitionLastPosition + deltaPos;
            boolean done = false;
            if (this.mTransitionInstantly) {
                position = this.mTransitionGoalPosition;
            }
            if ((dir > 0.0f && position >= this.mTransitionGoalPosition) || (dir <= 0.0f && position <= this.mTransitionGoalPosition)) {
                position = this.mTransitionGoalPosition;
                this.mInTransition = false;
                done = true;
            }
            this.mTransitionLastPosition = position;
            this.mTransitionPosition = position;
            this.mTransitionLastTime = currentTime;
            if (this.mTransitionListener != null || (this.mTransitionListeners != null && !this.mTransitionListeners.isEmpty())) {
                fireTransitionChange();
                this.mIsAnimating = true;
                if (done && this.mIsAnimating) {
                    fireTransitionCompleted();
                }
            }
            if (this.mInterpolator != null && !done) {
                if (this.mTemporalInterpolator) {
                    position = this.mInterpolator.getInterpolation(((float) (currentTime - this.mAnimationStartTime)) * 1.0E-9f);
                    this.mTransitionLastPosition = position;
                    this.mTransitionLastTime = currentTime;
                    if (this.mInterpolator instanceof MotionInterpolator) {
                        float lastVelocity = ((MotionInterpolator) this.mInterpolator).getVelocity();
                        this.mLastVelocity = lastVelocity;
                        if (Math.abs(lastVelocity) <= 1.0E-4f) {
                            this.mInTransition = false;
                        }
                        if (lastVelocity > 0.0f && position >= 1.0f) {
                            position = 1.0f;
                            this.mTransitionLastPosition = 1.0f;
                            this.mInTransition = false;
                        }
                        if (lastVelocity < 0.0f && position <= 0.0f) {
                            position = 0.0f;
                            this.mTransitionLastPosition = 0.0f;
                            this.mInTransition = false;
                        }
                    }
                } else {
                    position = this.mInterpolator.getInterpolation(position);
                    if (this.mInterpolator instanceof MotionInterpolator) {
                        this.mLastVelocity = ((MotionInterpolator) this.mInterpolator).getVelocity();
                    } else {
                        this.mLastVelocity = ((this.mInterpolator.getInterpolation(position + deltaPos) - position) * dir) / deltaPos;
                    }
                }
            }
            if ((dir > 0.0f && position >= this.mTransitionGoalPosition) || (dir <= 0.0f && position <= this.mTransitionGoalPosition)) {
                position = this.mTransitionGoalPosition;
                this.mInTransition = false;
            }
            if (position >= 1.0f || position <= 0.0f) {
                this.mInTransition = false;
            }
            int n = getChildCount();
            this.mKeepAnimating = false;
            long time = getNanoTime();
            this.mPostInterpolationPosition = position;
            for (int i = 0; i < n; i++) {
                View child = getChildAt(i);
                MotionController frame = this.mFrameArrayList.get(child);
                if (frame != null) {
                    this.mKeepAnimating = frame.interpolate(child, position, time, this.mKeyCache) | this.mKeepAnimating;
                }
            }
            if (this.mMeasureDuringTransition) {
                requestLayout();
            }
            if (this.mKeepAnimating) {
                invalidate();
            }
            if (this.mInTransition) {
                invalidate();
            }
            if (position <= 0.0f && this.mBeginState != -1) {
                if (this.mCurrentState != this.mBeginState) {
                    newState = true;
                }
                this.mCurrentState = this.mBeginState;
                this.mScene.getConstraintSet(this.mBeginState).applyCustomAttributes(this);
            }
            if (((double) position) >= 1.0d) {
                if (this.mCurrentState != this.mEndState) {
                    newState = true;
                }
                this.mCurrentState = this.mEndState;
                this.mScene.getConstraintSet(this.mEndState).applyCustomAttributes(this);
            }
        }
        if (this.mTransitionLastPosition >= 1.0f) {
            if (this.mCurrentState != this.mEndState) {
                newState = true;
            }
            this.mCurrentState = this.mEndState;
        } else if (this.mTransitionLastPosition <= 0.0f) {
            if (this.mCurrentState != this.mBeginState) {
                newState = true;
            }
            this.mCurrentState = this.mBeginState;
        }
        if (this.mIsAnimating) {
            if (((double) this.mTransitionLastPosition) <= 0.0d && this.mListenerState == this.mBeginState) {
                newState = true;
            }
            if (((double) this.mTransitionLastPosition) >= 1.0d && this.mListenerState == this.mEndState) {
                newState = true;
            }
        }
        this.mNeedsFireTransitionCompleted |= newState;
        if (newState && !this.mInLayout) {
            requestLayout();
        }
        this.mTransitionPosition = this.mTransitionLastPosition;
        if (newState || this.mIsAnimating) {
            fireTransitionChange();
            if (newState) {
                fireTransitionCompleted();
            }
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.mInLayout = true;
        try {
            if (this.mScene == null) {
                super.onLayout(changed, left, top, right, bottom);
                return;
            }
            int w = right - left;
            int h = bottom - top;
            if (!(this.mLastLayoutWidth == w && this.mLastLayoutHeight == h)) {
                rebuildScene();
                evaluate(true);
            }
            this.mLastLayoutWidth = w;
            this.mLastLayoutHeight = h;
            this.mOldWidth = w;
            this.mOldHeight = h;
            this.mInLayout = false;
        } finally {
            this.mInLayout = false;
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void parseLayoutDescription(int id) {
        this.mConstraintLayoutSpec = null;
    }

    private void init(AttributeSet attrs) {
        IS_IN_EDIT_MODE = isInEditMode();
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MotionLayout);
            int N = a.getIndexCount();
            boolean apply = true;
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.MotionLayout_layoutDescription) {
                    this.mScene = new MotionScene(getContext(), this, a.getResourceId(attr, -1));
                } else if (attr == R.styleable.MotionLayout_currentState) {
                    this.mCurrentState = a.getResourceId(attr, -1);
                } else if (attr == R.styleable.MotionLayout_motionProgress) {
                    this.mTransitionGoalPosition = a.getFloat(attr, 0.0f);
                    this.mInTransition = true;
                } else if (attr == R.styleable.MotionLayout_applyMotionScene) {
                    apply = a.getBoolean(attr, apply);
                } else if (attr == R.styleable.MotionLayout_showPaths) {
                    if (this.mDebugPath == 0) {
                        this.mDebugPath = a.getBoolean(attr, false) ? 2 : 0;
                    }
                } else if (attr == R.styleable.MotionLayout_motionDebug) {
                    this.mDebugPath = a.getInt(attr, 0);
                }
            }
            a.recycle();
            if (this.mScene == null) {
                Log.e(TAG, "WARNING NO app:layoutDescription tag");
            }
            if (!apply) {
                this.mScene = null;
            }
        }
        if (this.mDebugPath != 0) {
            checkStructure();
        }
        if (this.mCurrentState == -1 && this.mScene != null) {
            this.mCurrentState = this.mScene.getStartId();
            this.mBeginState = this.mScene.getStartId();
            this.mEndState = this.mScene.getEndId();
        }
    }

    public void setScene(MotionScene scene) {
        this.mScene = scene;
        this.mScene.setRtl(isRtl());
        rebuildScene();
    }

    private void checkStructure() {
        if (this.mScene == null) {
            Log.e(TAG, "CHECK: motion scene not set! set \"app:layoutDescription=\"@xml/file\"");
            return;
        }
        checkStructure(this.mScene.getStartId(), this.mScene.getConstraintSet(this.mScene.getStartId()));
        SparseIntArray startToEnd = new SparseIntArray();
        SparseIntArray endToStart = new SparseIntArray();
        Iterator<MotionScene.Transition> it = this.mScene.getDefinedTransitions().iterator();
        while (it.hasNext()) {
            MotionScene.Transition definedTransition = it.next();
            if (definedTransition == this.mScene.mCurrentTransition) {
                Log.v(TAG, "CHECK: CURRENT");
            }
            checkStructure(definedTransition);
            int startId = definedTransition.getStartConstraintSetId();
            int endId = definedTransition.getEndConstraintSetId();
            String startString = Debug.getName(getContext(), startId);
            String endString = Debug.getName(getContext(), endId);
            if (startToEnd.get(startId) == endId) {
                Log.e(TAG, "CHECK: two transitions with the same start and end " + startString + "->" + endString);
            }
            if (endToStart.get(endId) == startId) {
                Log.e(TAG, "CHECK: you can't have reverse transitions" + startString + "->" + endString);
            }
            startToEnd.put(startId, endId);
            endToStart.put(endId, startId);
            if (this.mScene.getConstraintSet(startId) == null) {
                Log.e(TAG, " no such constraintSetStart " + startString);
            }
            if (this.mScene.getConstraintSet(endId) == null) {
                Log.e(TAG, " no such constraintSetEnd " + startString);
            }
        }
    }

    private void checkStructure(int csetId, ConstraintSet set) {
        String setName = Debug.getName(getContext(), csetId);
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            View v = getChildAt(i);
            int id = v.getId();
            if (id == -1) {
                Log.w(TAG, "CHECK: " + setName + " ALL VIEWS SHOULD HAVE ID's " + v.getClass().getName() + " does not!");
            }
            if (set.getConstraint(id) == null) {
                Log.w(TAG, "CHECK: " + setName + " NO CONSTRAINTS for " + Debug.getName(v));
            }
        }
        int[] ids = set.getKnownIds();
        for (int i2 = 0; i2 < ids.length; i2++) {
            int id2 = ids[i2];
            String idString = Debug.getName(getContext(), id2);
            if (findViewById(ids[i2]) == null) {
                Log.w(TAG, "CHECK: " + setName + " NO View matches id " + idString);
            }
            if (set.getHeight(id2) == -1) {
                Log.w(TAG, "CHECK: " + setName + "(" + idString + ") no LAYOUT_HEIGHT");
            }
            if (set.getWidth(id2) == -1) {
                Log.w(TAG, "CHECK: " + setName + "(" + idString + ") no LAYOUT_HEIGHT");
            }
        }
    }

    private void checkStructure(MotionScene.Transition transition) {
        Log.v(TAG, "CHECK: transition = " + transition.debugString(getContext()));
        Log.v(TAG, "CHECK: transition.setDuration = " + transition.getDuration());
        if (transition.getStartConstraintSetId() == transition.getEndConstraintSetId()) {
            Log.e(TAG, "CHECK: start and end constraint set should not be the same!");
        }
    }

    public void setDebugMode(int debugMode) {
        this.mDebugPath = debugMode;
        invalidate();
    }

    public void getDebugMode(boolean showPaths) {
        this.mDebugPath = showPaths ? 2 : 1;
        invalidate();
    }

    private boolean handlesTouchEvent(float x, float y, View view, MotionEvent event) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                if (handlesTouchEvent(((float) view.getLeft()) + x, ((float) view.getTop()) + y, group.getChildAt(i), event)) {
                    return true;
                }
            }
        }
        this.mBoundsCheck.set(((float) view.getLeft()) + x, ((float) view.getTop()) + y, ((float) view.getRight()) + x, ((float) view.getBottom()) + y);
        if (event.getAction() == 0) {
            if (!this.mBoundsCheck.contains(event.getX(), event.getY()) || !view.onTouchEvent(event)) {
                return false;
            }
            return true;
        } else if (view.onTouchEvent(event)) {
            return true;
        }
        return false;
    }

    public boolean onInterceptTouchEvent(MotionEvent event) {
        MotionScene.Transition currentTransition;
        TouchResponse touchResponse;
        int regionId;
        RectF region;
        if (this.mScene == null || !this.mInteractionEnabled || (currentTransition = this.mScene.mCurrentTransition) == null || !currentTransition.isEnabled() || (touchResponse = currentTransition.getTouchResponse()) == null) {
            return false;
        }
        if ((event.getAction() == 0 && (region = touchResponse.getTouchRegion(this, new RectF())) != null && !region.contains(event.getX(), event.getY())) || (regionId = touchResponse.getTouchRegionId()) == -1) {
            return false;
        }
        if (this.mRegionView == null || this.mRegionView.getId() != regionId) {
            this.mRegionView = findViewById(regionId);
        }
        if (this.mRegionView == null) {
            return false;
        }
        this.mBoundsCheck.set((float) this.mRegionView.getLeft(), (float) this.mRegionView.getTop(), (float) this.mRegionView.getRight(), (float) this.mRegionView.getBottom());
        if (!this.mBoundsCheck.contains(event.getX(), event.getY()) || handlesTouchEvent(0.0f, 0.0f, this.mRegionView, event)) {
            return false;
        }
        return onTouchEvent(event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mScene == null || !this.mInteractionEnabled || !this.mScene.supportTouch()) {
            return super.onTouchEvent(event);
        }
        MotionScene.Transition currentTransition = this.mScene.mCurrentTransition;
        if (currentTransition != null && !currentTransition.isEnabled()) {
            return super.onTouchEvent(event);
        }
        this.mScene.processTouchEvent(event, getCurrentState(), this);
        return true;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!(this.mScene == null || this.mCurrentState == -1)) {
            ConstraintSet cSet = this.mScene.getConstraintSet(this.mCurrentState);
            this.mScene.readFallback(this);
            if (cSet != null) {
                cSet.applyTo(this);
            }
            this.mBeginState = this.mCurrentState;
        }
        onNewStateAttachHandlers();
        if (this.mStateCache != null) {
            this.mStateCache.apply();
        }
    }

    public void onRtlPropertiesChanged(int layoutDirection) {
        if (this.mScene != null) {
            this.mScene.setRtl(isRtl());
        }
    }

    private void onNewStateAttachHandlers() {
        if (this.mScene != null) {
            if (this.mScene.autoTransition(this, this.mCurrentState)) {
                requestLayout();
                return;
            }
            if (this.mCurrentState != -1) {
                this.mScene.addOnClickListeners(this, this.mCurrentState);
            }
            if (this.mScene.supportTouch()) {
                this.mScene.setupTouch();
            }
        }
    }

    public int getCurrentState() {
        return this.mCurrentState;
    }

    public float getProgress() {
        return this.mTransitionLastPosition;
    }

    /* access modifiers changed from: package-private */
    public void getAnchorDpDt(int mTouchAnchorId, float pos, float locationX, float locationY, float[] mAnchorDpDt) {
        String idName;
        HashMap<View, MotionController> hashMap = this.mFrameArrayList;
        View v = getViewById(mTouchAnchorId);
        MotionController f = hashMap.get(v);
        if (f != null) {
            f.getDpDt(pos, locationX, locationY, mAnchorDpDt);
            float y = v.getY();
            float deltaPos = pos - this.lastPos;
            float deltaY = y - this.lastY;
            if (deltaPos != 0.0f) {
                float f2 = deltaY / deltaPos;
            }
            this.lastPos = pos;
            this.lastY = y;
            return;
        }
        if (v == null) {
            idName = "" + mTouchAnchorId;
        } else {
            idName = v.getContext().getResources().getResourceName(mTouchAnchorId);
        }
        Log.w(TAG, "WARNING could not find view id " + idName);
    }

    public long getTransitionTimeMs() {
        if (this.mScene != null) {
            this.mTransitionDuration = ((float) this.mScene.getDuration()) / 1000.0f;
        }
        return (long) (this.mTransitionDuration * 1000.0f);
    }

    public void setTransitionListener(TransitionListener listener) {
        this.mTransitionListener = listener;
    }

    public void addTransitionListener(TransitionListener listener) {
        if (this.mTransitionListeners == null) {
            this.mTransitionListeners = new ArrayList<>();
        }
        this.mTransitionListeners.add(listener);
    }

    public boolean removeTransitionListener(TransitionListener listener) {
        if (this.mTransitionListeners == null) {
            return false;
        }
        return this.mTransitionListeners.remove(listener);
    }

    public void fireTrigger(int triggerId, boolean positive, float progress) {
        if (this.mTransitionListener != null) {
            this.mTransitionListener.onTransitionTrigger(this, triggerId, positive, progress);
        }
        if (this.mTransitionListeners != null) {
            Iterator<TransitionListener> it = this.mTransitionListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionTrigger(this, triggerId, positive, progress);
            }
        }
    }

    private void fireTransitionChange() {
        if ((this.mTransitionListener != null || (this.mTransitionListeners != null && !this.mTransitionListeners.isEmpty())) && this.mListenerPosition != this.mTransitionPosition) {
            if (this.mListenerState != -1) {
                if (this.mTransitionListener != null) {
                    this.mTransitionListener.onTransitionStarted(this, this.mBeginState, this.mEndState);
                }
                if (this.mTransitionListeners != null) {
                    Iterator<TransitionListener> it = this.mTransitionListeners.iterator();
                    while (it.hasNext()) {
                        it.next().onTransitionStarted(this, this.mBeginState, this.mEndState);
                    }
                }
                this.mIsAnimating = true;
            }
            this.mListenerState = -1;
            this.mListenerPosition = this.mTransitionPosition;
            if (this.mTransitionListener != null) {
                this.mTransitionListener.onTransitionChange(this, this.mBeginState, this.mEndState, this.mTransitionPosition);
            }
            if (this.mTransitionListeners != null) {
                Iterator<TransitionListener> it2 = this.mTransitionListeners.iterator();
                while (it2.hasNext()) {
                    it2.next().onTransitionChange(this, this.mBeginState, this.mEndState, this.mTransitionPosition);
                }
            }
            this.mIsAnimating = true;
        }
    }

    /* access modifiers changed from: protected */
    public void fireTransitionCompleted() {
        if ((this.mTransitionListener != null || (this.mTransitionListeners != null && !this.mTransitionListeners.isEmpty())) && this.mListenerState == -1) {
            this.mListenerState = this.mCurrentState;
            int lastState = -1;
            if (!this.mTransitionCompleted.isEmpty()) {
                lastState = this.mTransitionCompleted.get(this.mTransitionCompleted.size() - 1).intValue();
            }
            if (lastState != this.mCurrentState && this.mCurrentState != -1) {
                this.mTransitionCompleted.add(Integer.valueOf(this.mCurrentState));
            }
        }
    }

    private void processTransitionCompleted() {
        if (this.mTransitionListener != null || (this.mTransitionListeners != null && !this.mTransitionListeners.isEmpty())) {
            this.mIsAnimating = false;
            Iterator<Integer> it = this.mTransitionCompleted.iterator();
            while (it.hasNext()) {
                Integer state = it.next();
                if (this.mTransitionListener != null) {
                    this.mTransitionListener.onTransitionCompleted(this, state.intValue());
                }
                if (this.mTransitionListeners != null) {
                    Iterator<TransitionListener> it2 = this.mTransitionListeners.iterator();
                    while (it2.hasNext()) {
                        it2.next().onTransitionCompleted(this, state.intValue());
                    }
                }
            }
            this.mTransitionCompleted.clear();
        }
    }

    public DesignTool getDesignTool() {
        if (this.mDesignTool == null) {
            this.mDesignTool = new DesignTool(this);
        }
        return this.mDesignTool;
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void onViewAdded(View view) {
        super.onViewAdded(view);
        if (view instanceof MotionHelper) {
            MotionHelper helper = (MotionHelper) view;
            if (this.mTransitionListeners == null) {
                this.mTransitionListeners = new ArrayList<>();
            }
            this.mTransitionListeners.add(helper);
            if (helper.isUsedOnShow()) {
                if (this.mOnShowHelpers == null) {
                    this.mOnShowHelpers = new ArrayList<>();
                }
                this.mOnShowHelpers.add(helper);
            }
            if (helper.isUseOnHide()) {
                if (this.mOnHideHelpers == null) {
                    this.mOnHideHelpers = new ArrayList<>();
                }
                this.mOnHideHelpers.add(helper);
            }
        }
    }

    @Override // androidx.constraintlayout.widget.ConstraintLayout
    public void onViewRemoved(View view) {
        super.onViewRemoved(view);
        if (this.mOnShowHelpers != null) {
            this.mOnShowHelpers.remove(view);
        }
        if (this.mOnHideHelpers != null) {
            this.mOnHideHelpers.remove(view);
        }
    }

    public void setOnShow(float progress) {
        if (this.mOnShowHelpers != null) {
            int count = this.mOnShowHelpers.size();
            for (int i = 0; i < count; i++) {
                this.mOnShowHelpers.get(i).setProgress(progress);
            }
        }
    }

    public void setOnHide(float progress) {
        if (this.mOnHideHelpers != null) {
            int count = this.mOnHideHelpers.size();
            for (int i = 0; i < count; i++) {
                this.mOnHideHelpers.get(i).setProgress(progress);
            }
        }
    }

    public int[] getConstraintSetIds() {
        if (this.mScene == null) {
            return null;
        }
        return this.mScene.getConstraintSetIds();
    }

    public ConstraintSet getConstraintSet(int id) {
        if (this.mScene == null) {
            return null;
        }
        return this.mScene.getConstraintSet(id);
    }

    @Deprecated
    public void rebuildMotion() {
        Log.e(TAG, "This method is deprecated. Please call rebuildScene() instead.");
        rebuildScene();
    }

    public void rebuildScene() {
        this.mModel.reEvaluateState();
        invalidate();
    }

    public void updateState(int stateId, ConstraintSet set) {
        if (this.mScene != null) {
            this.mScene.setConstraintSet(stateId, set);
        }
        updateState();
        if (this.mCurrentState == stateId) {
            set.applyTo(this);
        }
    }

    public void updateState() {
        this.mModel.initFrom(this.mLayoutWidget, this.mScene.getConstraintSet(this.mBeginState), this.mScene.getConstraintSet(this.mEndState));
        rebuildScene();
    }

    public ArrayList<MotionScene.Transition> getDefinedTransitions() {
        if (this.mScene == null) {
            return null;
        }
        return this.mScene.getDefinedTransitions();
    }

    public int getStartState() {
        return this.mBeginState;
    }

    public int getEndState() {
        return this.mEndState;
    }

    public float getTargetPosition() {
        return this.mTransitionGoalPosition;
    }

    public void setTransitionDuration(int milliseconds) {
        if (this.mScene == null) {
            Log.e(TAG, "MotionScene not defined");
        } else {
            this.mScene.setDuration(milliseconds);
        }
    }

    public MotionScene.Transition getTransition(int id) {
        return this.mScene.getTransitionById(id);
    }

    /* access modifiers changed from: package-private */
    public int lookUpConstraintId(String id) {
        if (this.mScene == null) {
            return 0;
        }
        return this.mScene.lookUpConstraintId(id);
    }

    /* access modifiers changed from: package-private */
    public String getConstraintSetNames(int id) {
        if (this.mScene == null) {
            return null;
        }
        return this.mScene.lookUpConstraintName(id);
    }

    /* access modifiers changed from: package-private */
    public void disableAutoTransition(boolean disable) {
        if (this.mScene != null) {
            this.mScene.disableAutoTransition(disable);
        }
    }

    public void setInteractionEnabled(boolean enabled) {
        this.mInteractionEnabled = enabled;
    }

    public boolean isInteractionEnabled() {
        return this.mInteractionEnabled;
    }

    private void fireTransitionStarted(MotionLayout motionLayout, int mBeginState2, int mEndState2) {
        if (this.mTransitionListener != null) {
            this.mTransitionListener.onTransitionStarted(this, mBeginState2, mEndState2);
        }
        if (this.mTransitionListeners != null) {
            Iterator<TransitionListener> it = this.mTransitionListeners.iterator();
            while (it.hasNext()) {
                it.next().onTransitionStarted(motionLayout, mBeginState2, mEndState2);
            }
        }
    }
}
