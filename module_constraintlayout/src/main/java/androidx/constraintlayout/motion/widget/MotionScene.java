package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.R;
import androidx.constraintlayout.widget.StateSet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MotionScene {
    static final int ANTICIPATE = 4;
    static final int BOUNCE = 5;
    private static final boolean DEBUG = false;
    static final int EASE_IN = 1;
    static final int EASE_IN_OUT = 0;
    static final int EASE_OUT = 2;
    private static final int INTERPOLATOR_REFRENCE_ID = -2;
    public static final int LAYOUT_HONOR_REQUEST = 1;
    public static final int LAYOUT_IGNORE_REQUEST = 0;
    static final int LINEAR = 3;
    private static final int SPLINE_STRING = -1;
    public static final String TAG = "MotionScene";
    static final int TRANSITION_BACKWARD = 0;
    static final int TRANSITION_FORWARD = 1;
    public static final int UNSET = -1;
    private boolean DEBUG_DESKTOP = false;
    private ArrayList<Transition> mAbstractTransitionList = new ArrayList<>();
    private HashMap<String, Integer> mConstraintSetIdMap = new HashMap<>();
    /* access modifiers changed from: private */
    public SparseArray<ConstraintSet> mConstraintSetMap = new SparseArray<>();
    Transition mCurrentTransition = null;
    /* access modifiers changed from: private */
    public int mDefaultDuration = 100;
    private Transition mDefaultTransition = null;
    private SparseIntArray mDeriveMap = new SparseIntArray();
    private boolean mDisableAutoTransition = false;
    private MotionEvent mLastTouchDown;
    float mLastTouchX;
    float mLastTouchY;
    /* access modifiers changed from: private */
    public int mLayoutDuringTransition = 0;
    /* access modifiers changed from: private */
    public final MotionLayout mMotionLayout;
    private boolean mMotionOutsideRegion = false;
    private boolean mRtl;
    StateSet mStateSet = null;
    private ArrayList<Transition> mTransitionList = new ArrayList<>();
    private MotionLayout.MotionTracker mVelocityTracker;

    /* access modifiers changed from: package-private */
    public void setTransition(int beginId, int endId) {
        int start = beginId;
        int end = endId;
        if (this.mStateSet != null) {
            int tmp = this.mStateSet.stateGetConstraintID(beginId, -1, -1);
            if (tmp != -1) {
                start = tmp;
            }
            int tmp2 = this.mStateSet.stateGetConstraintID(endId, -1, -1);
            if (tmp2 != -1) {
                end = tmp2;
            }
        }
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            Transition transition = it.next();
            if ((transition.mConstraintSetEnd == end && transition.mConstraintSetStart == start) || (transition.mConstraintSetEnd == endId && transition.mConstraintSetStart == beginId)) {
                this.mCurrentTransition = transition;
                if (this.mCurrentTransition != null && this.mCurrentTransition.mTouchResponse != null) {
                    this.mCurrentTransition.mTouchResponse.setRTL(this.mRtl);
                    return;
                }
                return;
            }
        }
        Transition matchTransiton = this.mDefaultTransition;
        Iterator<Transition> it2 = this.mAbstractTransitionList.iterator();
        while (it2.hasNext()) {
            Transition transition2 = it2.next();
            if (transition2.mConstraintSetEnd == endId) {
                matchTransiton = transition2;
            }
        }
        Transition t = new Transition(this, matchTransiton);
        int unused = t.mConstraintSetStart = start;
        int unused2 = t.mConstraintSetEnd = end;
        if (start != -1) {
            this.mTransitionList.add(t);
        }
        this.mCurrentTransition = t;
    }

    public void addTransition(Transition transition) {
        int index = getIndex(transition);
        if (index == -1) {
            this.mTransitionList.add(transition);
        } else {
            this.mTransitionList.set(index, transition);
        }
    }

    public void removeTransition(Transition transition) {
        int index = getIndex(transition);
        if (index != -1) {
            this.mTransitionList.remove(index);
        }
    }

    private int getIndex(Transition transition) {
        int id = transition.mId;
        if (id == -1) {
            throw new IllegalArgumentException("The transition must have an id");
        }
        for (int index = 0; index < this.mTransitionList.size(); index++) {
            if (this.mTransitionList.get(index).mId == id) {
                return index;
            }
        }
        return -1;
    }

    public boolean validateLayout(MotionLayout layout) {
        return layout == this.mMotionLayout && layout.mScene == this;
    }

    public void setTransition(Transition transition) {
        this.mCurrentTransition = transition;
        if (this.mCurrentTransition != null && this.mCurrentTransition.mTouchResponse != null) {
            this.mCurrentTransition.mTouchResponse.setRTL(this.mRtl);
        }
    }

    private int getRealID(int stateid) {
        int tmp;
        return (this.mStateSet == null || (tmp = this.mStateSet.stateGetConstraintID(stateid, -1, -1)) == -1) ? stateid : tmp;
    }

    public List<Transition> getTransitionsWithState(int stateid) {
        int stateid2 = getRealID(stateid);
        ArrayList<Transition> ret = new ArrayList<>();
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            Transition transition = it.next();
            if (transition.mConstraintSetStart == stateid2 || transition.mConstraintSetEnd == stateid2) {
                ret.add(transition);
            }
        }
        return ret;
    }

    public void addOnClickListeners(MotionLayout motionLayout, int currentState) {
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            Transition transition = it.next();
            if (transition.mOnClicks.size() > 0) {
                Iterator it2 = transition.mOnClicks.iterator();
                while (it2.hasNext()) {
                    ((Transition.TransitionOnClick) it2.next()).removeOnClickListeners(motionLayout);
                }
            }
        }
        Iterator<Transition> it3 = this.mAbstractTransitionList.iterator();
        while (it3.hasNext()) {
            Transition transition2 = it3.next();
            if (transition2.mOnClicks.size() > 0) {
                Iterator it4 = transition2.mOnClicks.iterator();
                while (it4.hasNext()) {
                    ((Transition.TransitionOnClick) it4.next()).removeOnClickListeners(motionLayout);
                }
            }
        }
        Iterator<Transition> it5 = this.mTransitionList.iterator();
        while (it5.hasNext()) {
            Transition transition3 = it5.next();
            if (transition3.mOnClicks.size() > 0) {
                Iterator it6 = transition3.mOnClicks.iterator();
                while (it6.hasNext()) {
                    ((Transition.TransitionOnClick) it6.next()).addOnClickListeners(motionLayout, currentState, transition3);
                }
            }
        }
        Iterator<Transition> it7 = this.mAbstractTransitionList.iterator();
        while (it7.hasNext()) {
            Transition transition4 = it7.next();
            if (transition4.mOnClicks.size() > 0) {
                Iterator it8 = transition4.mOnClicks.iterator();
                while (it8.hasNext()) {
                    ((Transition.TransitionOnClick) it8.next()).addOnClickListeners(motionLayout, currentState, transition4);
                }
            }
        }
    }

    public Transition bestTransitionFor(int currentState, float dx, float dy, MotionEvent mLastTouchDown2) {
        RectF region;
        float val;
        if (currentState == -1) {
            return this.mCurrentTransition;
        }
        List<Transition> candidates = getTransitionsWithState(currentState);
        float max = 0.0f;
        Transition best = null;
        RectF cache = new RectF();
        for (Transition transition : candidates) {
            if (!transition.mDisable && transition.mTouchResponse != null) {
                transition.mTouchResponse.setRTL(this.mRtl);
                RectF region2 = transition.mTouchResponse.getTouchRegion(this.mMotionLayout, cache);
                if ((region2 == null || mLastTouchDown2 == null || region2.contains(mLastTouchDown2.getX(), mLastTouchDown2.getY())) && ((region = transition.mTouchResponse.getTouchRegion(this.mMotionLayout, cache)) == null || mLastTouchDown2 == null || region.contains(mLastTouchDown2.getX(), mLastTouchDown2.getY()))) {
                    float val2 = transition.mTouchResponse.dot(dx, dy);
                    if (transition.mConstraintSetEnd == currentState) {
                        val = val2 * -1.0f;
                    } else {
                        val = val2 * 1.1f;
                    }
                    if (val > max) {
                        max = val;
                        best = transition;
                    }
                }
            }
        }
        return best;
    }

    public ArrayList<Transition> getDefinedTransitions() {
        return this.mTransitionList;
    }

    public Transition getTransitionById(int id) {
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            Transition transition = it.next();
            if (transition.mId == id) {
                return transition;
            }
        }
        return null;
    }

    public int[] getConstraintSetIds() {
        int[] ids = new int[this.mConstraintSetMap.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = this.mConstraintSetMap.keyAt(i);
        }
        return ids;
    }

    /* access modifiers changed from: package-private */
    public boolean autoTransition(MotionLayout motionLayout, int currentState) {
        if (isProcessingTouch() || this.mDisableAutoTransition) {
            return false;
        }
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            Transition transition = it.next();
            if (transition.mAutoTransition != 0) {
                if (currentState == transition.mConstraintSetStart && (transition.mAutoTransition == 4 || transition.mAutoTransition == 2)) {
                    motionLayout.setTransition(transition);
                    if (transition.mAutoTransition == 4) {
                        motionLayout.transitionToEnd();
                    } else {
                        motionLayout.setProgress(1.0f);
                    }
                    return true;
                } else if (currentState == transition.mConstraintSetEnd && (transition.mAutoTransition == 3 || transition.mAutoTransition == 1)) {
                    motionLayout.setTransition(transition);
                    if (transition.mAutoTransition == 3) {
                        motionLayout.transitionToStart();
                    } else {
                        motionLayout.setProgress(0.0f);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isProcessingTouch() {
        return this.mVelocityTracker != null;
    }

    public void setRtl(boolean rtl) {
        this.mRtl = rtl;
        if (this.mCurrentTransition != null && this.mCurrentTransition.mTouchResponse != null) {
            this.mCurrentTransition.mTouchResponse.setRTL(this.mRtl);
        }
    }

    public static class Transition {
        public static final int AUTO_ANIMATE_TO_END = 4;
        public static final int AUTO_ANIMATE_TO_START = 3;
        public static final int AUTO_JUMP_TO_END = 2;
        public static final int AUTO_JUMP_TO_START = 1;
        public static final int AUTO_NONE = 0;
        /* access modifiers changed from: private */
        public int mAutoTransition = 0;
        /* access modifiers changed from: private */
        public int mConstraintSetEnd = -1;
        /* access modifiers changed from: private */
        public int mConstraintSetStart = -1;
        /* access modifiers changed from: private */
        public int mDefaultInterpolator = 0;
        /* access modifiers changed from: private */
        public int mDefaultInterpolatorID = -1;
        /* access modifiers changed from: private */
        public String mDefaultInterpolatorString = null;
        /* access modifiers changed from: private */
        public boolean mDisable = false;
        /* access modifiers changed from: private */
        public int mDuration = 400;
        /* access modifiers changed from: private */
        public int mId = -1;
        /* access modifiers changed from: private */
        public boolean mIsAbstract = false;
        /* access modifiers changed from: private */
        public ArrayList<KeyFrames> mKeyFramesList = new ArrayList<>();
        private int mLayoutDuringTransition = 0;
        /* access modifiers changed from: private */
        public final MotionScene mMotionScene;
        /* access modifiers changed from: private */
        public ArrayList<TransitionOnClick> mOnClicks = new ArrayList<>();
        /* access modifiers changed from: private */
        public int mPathMotionArc = -1;
        /* access modifiers changed from: private */
        public float mStagger = 0.0f;
        /* access modifiers changed from: private */
        public TouchResponse mTouchResponse = null;

        public int getLayoutDuringTransition() {
            return this.mLayoutDuringTransition;
        }

        public void addOnClick(Context context, XmlPullParser parser) {
            this.mOnClicks.add(new TransitionOnClick(context, this, parser));
        }

        public int getEndConstraintSetId() {
            return this.mConstraintSetEnd;
        }

        public int getStartConstraintSetId() {
            return this.mConstraintSetStart;
        }

        public void setDuration(int duration) {
            this.mDuration = duration;
        }

        public int getDuration() {
            return this.mDuration;
        }

        public float getStagger() {
            return this.mStagger;
        }

        public List<KeyFrames> getKeyFrameList() {
            return this.mKeyFramesList;
        }

        public List<TransitionOnClick> getOnClickList() {
            return this.mOnClicks;
        }

        public TouchResponse getTouchResponse() {
            return this.mTouchResponse;
        }

        public void setStagger(float stagger) {
            this.mStagger = stagger;
        }

        public void setPathMotionArc(int arcMode) {
            this.mPathMotionArc = arcMode;
        }

        public int getPathMotionArc() {
            return this.mPathMotionArc;
        }

        public boolean isEnabled() {
            return !this.mDisable;
        }

        public void setEnable(boolean enable) {
            this.mDisable = !enable;
        }

        public String debugString(Context context) {
            String ret;
            if (this.mConstraintSetStart == -1) {
                ret = "null";
            } else {
                ret = context.getResources().getResourceEntryName(this.mConstraintSetStart);
            }
            if (this.mConstraintSetEnd == -1) {
                return ret + " -> null";
            }
            return ret + " -> " + context.getResources().getResourceEntryName(this.mConstraintSetEnd);
        }

        /* access modifiers changed from: package-private */
        public static class TransitionOnClick implements View.OnClickListener {
            public static final int ANIM_TOGGLE = 17;
            public static final int ANIM_TO_END = 1;
            public static final int ANIM_TO_START = 16;
            public static final int JUMP_TO_END = 256;
            public static final int JUMP_TO_START = 4096;
            int mMode = 17;
            int mTargetId = -1;
            private final Transition mTransition;

            public TransitionOnClick(Context context, Transition transition, XmlPullParser parser) {
                this.mTransition = transition;
                TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.OnClick);
                int N = a.getIndexCount();
                for (int i = 0; i < N; i++) {
                    int attr = a.getIndex(i);
                    if (attr == R.styleable.OnClick_targetId) {
                        this.mTargetId = a.getResourceId(attr, this.mTargetId);
                    } else if (attr == R.styleable.OnClick_clickAction) {
                        this.mMode = a.getInt(attr, this.mMode);
                    }
                }
                a.recycle();
            }

            public void addOnClickListeners(MotionLayout motionLayout, int currentState, Transition transition) {
                boolean listen;
                boolean z;
                boolean z2;
                boolean z3;
                boolean z4 = true;
                View v = this.mTargetId == -1 ? motionLayout : motionLayout.findViewById(this.mTargetId);
                if (v == null) {
                    Log.e(MotionScene.TAG, "OnClick could not find id " + this.mTargetId);
                    return;
                }
                int start = transition.mConstraintSetStart;
                int end = transition.mConstraintSetEnd;
                if (start == -1) {
                    v.setOnClickListener(this);
                    return;
                }
                if ((this.mMode & 1) == 0 || currentState != start) {
                    listen = false;
                } else {
                    listen = true;
                }
                if ((this.mMode & JUMP_TO_END) == 0 || currentState != start) {
                    z = false;
                } else {
                    z = true;
                }
                boolean listen2 = listen | z;
                if ((this.mMode & 1) == 0 || currentState != start) {
                    z2 = false;
                } else {
                    z2 = true;
                }
                boolean listen3 = listen2 | z2;
                if ((this.mMode & 16) == 0 || currentState != end) {
                    z3 = false;
                } else {
                    z3 = true;
                }
                boolean listen4 = listen3 | z3;
                if ((this.mMode & JUMP_TO_START) == 0 || currentState != end) {
                    z4 = false;
                }
                if (listen4 || z4) {
                    v.setOnClickListener(this);
                }
            }

            public void removeOnClickListeners(MotionLayout motionLayout) {
                View v = motionLayout.findViewById(this.mTargetId);
                if (v == null) {
                    Log.e(MotionScene.TAG, " (*)  could not find id " + this.mTargetId);
                } else {
                    v.setOnClickListener(null);
                }
            }

            /* access modifiers changed from: package-private */
            public boolean isTransitionViable(Transition current, MotionLayout tl) {
                boolean z = false;
                if (this.mTransition == current) {
                    return true;
                }
                int dest = this.mTransition.mConstraintSetEnd;
                int from = this.mTransition.mConstraintSetStart;
                if (from == -1) {
                    return tl.mCurrentState != dest;
                }
                if (tl.mCurrentState == from || tl.mCurrentState == dest) {
                    z = true;
                }
                return z;
            }

            public void onClick(View view) {
                boolean forward;
                boolean backward;
                boolean bidirectional;
                MotionLayout tl = this.mTransition.mMotionScene.mMotionLayout;
                if (tl.isInteractionEnabled()) {
                    if (this.mTransition.mConstraintSetStart == -1) {
                        int currentState = tl.getCurrentState();
                        if (currentState == -1) {
                            tl.transitionToState(this.mTransition.mConstraintSetEnd);
                            return;
                        }
                        Transition t = new Transition(this.mTransition.mMotionScene, this.mTransition);
                        int unused = t.mConstraintSetStart = currentState;
                        int unused2 = t.mConstraintSetEnd = this.mTransition.mConstraintSetEnd;
                        tl.setTransition(t);
                        tl.transitionToEnd();
                        return;
                    }
                    Transition current = this.mTransition.mMotionScene.mCurrentTransition;
                    if ((this.mMode & 1) == 0 && (this.mMode & JUMP_TO_END) == 0) {
                        forward = false;
                    } else {
                        forward = true;
                    }
                    if ((this.mMode & 16) == 0 && (this.mMode & JUMP_TO_START) == 0) {
                        backward = false;
                    } else {
                        backward = true;
                    }
                    if (!forward || !backward) {
                        bidirectional = false;
                    } else {
                        bidirectional = true;
                    }
                    if (bidirectional) {
                        if (this.mTransition.mMotionScene.mCurrentTransition != this.mTransition) {
                            tl.setTransition(this.mTransition);
                        }
                        if (tl.getCurrentState() == tl.getEndState() || tl.getProgress() > 0.5f) {
                            forward = false;
                        } else {
                            backward = false;
                        }
                    }
                    if (!isTransitionViable(current, tl)) {
                        return;
                    }
                    if (forward && (this.mMode & 1) != 0) {
                        tl.setTransition(this.mTransition);
                        tl.transitionToEnd();
                    } else if (backward && (this.mMode & 16) != 0) {
                        tl.setTransition(this.mTransition);
                        tl.transitionToStart();
                    } else if (forward && (this.mMode & JUMP_TO_END) != 0) {
                        tl.setTransition(this.mTransition);
                        tl.setProgress(1.0f);
                    } else if (backward && (this.mMode & JUMP_TO_START) != 0) {
                        tl.setTransition(this.mTransition);
                        tl.setProgress(0.0f);
                    }
                }
            }
        }

        Transition(MotionScene motionScene, Transition global) {
            this.mMotionScene = motionScene;
            if (global != null) {
                this.mPathMotionArc = global.mPathMotionArc;
                this.mDefaultInterpolator = global.mDefaultInterpolator;
                this.mDefaultInterpolatorString = global.mDefaultInterpolatorString;
                this.mDefaultInterpolatorID = global.mDefaultInterpolatorID;
                this.mDuration = global.mDuration;
                this.mKeyFramesList = global.mKeyFramesList;
                this.mStagger = global.mStagger;
                this.mLayoutDuringTransition = global.mLayoutDuringTransition;
            }
        }

        public Transition(int id, MotionScene motionScene, int constraintSetStartId, int constraintSetEndId) {
            this.mId = id;
            this.mMotionScene = motionScene;
            this.mConstraintSetStart = constraintSetStartId;
            this.mConstraintSetEnd = constraintSetEndId;
            this.mDuration = motionScene.mDefaultDuration;
            this.mLayoutDuringTransition = motionScene.mLayoutDuringTransition;
        }

        Transition(MotionScene motionScene, Context context, XmlPullParser parser) {
            this.mDuration = motionScene.mDefaultDuration;
            this.mLayoutDuringTransition = motionScene.mLayoutDuringTransition;
            this.mMotionScene = motionScene;
            fillFromAttributeList(motionScene, context, Xml.asAttributeSet(parser));
        }

        private void fillFromAttributeList(MotionScene motionScene, Context context, AttributeSet attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Transition);
            fill(motionScene, context, a);
            a.recycle();
        }

        private void fill(MotionScene motionScene, Context context, TypedArray a) {
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.Transition_constraintSetEnd) {
                    this.mConstraintSetEnd = a.getResourceId(attr, this.mConstraintSetEnd);
                    if ("layout".equals(context.getResources().getResourceTypeName(this.mConstraintSetEnd))) {
                        ConstraintSet cSet = new ConstraintSet();
                        cSet.load(context, this.mConstraintSetEnd);
                        motionScene.mConstraintSetMap.append(this.mConstraintSetEnd, cSet);
                    }
                } else if (attr == R.styleable.Transition_constraintSetStart) {
                    this.mConstraintSetStart = a.getResourceId(attr, this.mConstraintSetStart);
                    if ("layout".equals(context.getResources().getResourceTypeName(this.mConstraintSetStart))) {
                        ConstraintSet cSet2 = new ConstraintSet();
                        cSet2.load(context, this.mConstraintSetStart);
                        motionScene.mConstraintSetMap.append(this.mConstraintSetStart, cSet2);
                    }
                } else if (attr == R.styleable.Transition_motionInterpolator) {
                    TypedValue type = a.peekValue(attr);
                    if (type.type == 1) {
                        this.mDefaultInterpolatorID = a.getResourceId(attr, -1);
                        if (this.mDefaultInterpolatorID != -1) {
                            this.mDefaultInterpolator = -2;
                        }
                    } else if (type.type == 3) {
                        this.mDefaultInterpolatorString = a.getString(attr);
                        if (this.mDefaultInterpolatorString.indexOf("/") > 0) {
                            this.mDefaultInterpolatorID = a.getResourceId(attr, -1);
                            this.mDefaultInterpolator = -2;
                        } else {
                            this.mDefaultInterpolator = -1;
                        }
                    } else {
                        this.mDefaultInterpolator = a.getInteger(attr, this.mDefaultInterpolator);
                    }
                } else if (attr == R.styleable.Transition_duration) {
                    this.mDuration = a.getInt(attr, this.mDuration);
                } else if (attr == R.styleable.Transition_staggered) {
                    this.mStagger = a.getFloat(attr, this.mStagger);
                } else if (attr == R.styleable.Transition_autoTransition) {
                    this.mAutoTransition = a.getInteger(attr, this.mAutoTransition);
                } else if (attr == R.styleable.Transition_android_id) {
                    this.mId = a.getResourceId(attr, this.mId);
                } else if (attr == R.styleable.Transition_transitionDisable) {
                    this.mDisable = a.getBoolean(attr, this.mDisable);
                } else if (attr == R.styleable.Transition_pathMotionArc) {
                    this.mPathMotionArc = a.getInteger(attr, -1);
                } else if (attr == R.styleable.Transition_layoutDuringTransition) {
                    this.mLayoutDuringTransition = a.getInteger(attr, 0);
                }
            }
            if (this.mConstraintSetStart == -1) {
                this.mIsAbstract = true;
            }
        }
    }

    public MotionScene(MotionLayout layout) {
        this.mMotionLayout = layout;
    }

    MotionScene(Context context, MotionLayout layout, int resourceID) {
        this.mMotionLayout = layout;
        load(context, resourceID);
        this.mConstraintSetMap.put(R.id.motion_base, new ConstraintSet());
        this.mConstraintSetIdMap.put("motion_base", Integer.valueOf(R.id.motion_base));
    }

    private void load(Context context, int resourceId) {
        XmlPullParser parser = context.getResources().getXml(resourceId);
        Transition transition = null;
        try {
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                switch (eventType) {
                    case 0:
                        try {
                            parser.getName();
                            transition = transition;
                            break;
                        } catch (XmlPullParserException e) {
                            e = e;
                            e.printStackTrace();
                        } catch (IOException e2) {
                            e = e2;
                            e.printStackTrace();
                        }
                    case 2:
                        String tagName = parser.getName();
                        if (this.DEBUG_DESKTOP) {
                            System.out.println("parsing = " + tagName);
                        }
                        char c = 65535;
                        switch (tagName.hashCode()) {
                            case -1349929691:
                                if (tagName.equals("ConstraintSet")) {
                                    c = 5;
                                    break;
                                }
                                break;
                            case -1239391468:
                                if (tagName.equals("KeyFrameSet")) {
                                    c = 6;
                                    break;
                                }
                                break;
                            case 269306229:
                                if (tagName.equals("Transition")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 312750793:
                                if (tagName.equals("OnClick")) {
                                    c = 3;
                                    break;
                                }
                                break;
                            case 327855227:
                                if (tagName.equals("OnSwipe")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 793277014:
                                if (tagName.equals(TAG)) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1382829617:
                                if (tagName.equals("StateSet")) {
                                    c = 4;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                parseMotionSceneTags(context, parser);
                                transition = transition;
                                break;
                            case 1:
                                ArrayList<Transition> arrayList = this.mTransitionList;
                                transition = new Transition(this, context, parser);
                                arrayList.add(transition);
                                if (this.mCurrentTransition == null && !transition.mIsAbstract) {
                                    this.mCurrentTransition = transition;
                                    if (!(this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null)) {
                                        this.mCurrentTransition.mTouchResponse.setRTL(this.mRtl);
                                    }
                                }
                                if (transition.mIsAbstract) {
                                    if (transition.mConstraintSetEnd == -1) {
                                        this.mDefaultTransition = transition;
                                    } else {
                                        this.mAbstractTransitionList.add(transition);
                                    }
                                    this.mTransitionList.remove(transition);
                                    break;
                                }
                                break;
                            case 2:
                                if (transition == null) {
                                    String name = context.getResources().getResourceEntryName(resourceId);
                                    Log.v(TAG, " OnSwipe (" + name + ".xml:" + parser.getLineNumber() + ")");
                                }
                                TouchResponse unused = transition.mTouchResponse = new TouchResponse(context, this.mMotionLayout, parser);
                                transition = transition;
                                break;
                            case 3:
                                transition.addOnClick(context, parser);
                                transition = transition;
                                break;
                            case 4:
                                this.mStateSet = new StateSet(context, parser);
                                transition = transition;
                                break;
                            case 5:
                                parseConstraintSet(context, parser);
                                transition = transition;
                                break;
                            case 6:
                                transition.mKeyFramesList.add(new KeyFrames(context, parser));
                                transition = transition;
                                break;
                            default:
                                Log.v(TAG, "WARNING UNKNOWN ATTRIBUTE " + tagName);
                        }
                        break;
                    case 1:
                    default:
                        transition = transition;
                        break;
                    case 3:
                        transition = transition;
                        break;
                }
            }
        } catch (XmlPullParserException e3) {
            e = e3;
            e.printStackTrace();
        } catch (IOException e4) {
            e = e4;
            e.printStackTrace();
        }
    }

    private void parseMotionSceneTags(Context context, XmlPullParser parser) {
        TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.MotionScene);
        int count = a.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.MotionScene_defaultDuration) {
                this.mDefaultDuration = a.getInt(attr, this.mDefaultDuration);
            } else if (attr == R.styleable.MotionScene_layoutDuringTransition) {
                this.mLayoutDuringTransition = a.getInteger(attr, 0);
            }
        }
        a.recycle();
    }

    private int getId(Context context, String idString) {
        int id = -1;
        if (idString.contains("/")) {
            id = context.getResources().getIdentifier(idString.substring(idString.indexOf(47) + 1), "id", context.getPackageName());
            if (this.DEBUG_DESKTOP) {
                System.out.println("id getMap res = " + id);
            }
        }
        if (id != -1) {
            return id;
        }
        if (idString != null && idString.length() > 1) {
            return Integer.parseInt(idString.substring(1));
        }
        Log.e(TAG, "error in parsing id");
        return id;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    private void parseConstraintSet(Context context, XmlPullParser parser) {
        boolean z;
        ConstraintSet set = new ConstraintSet();
        set.setForceId(false);
        int count = parser.getAttributeCount();
        int id = -1;
        int derivedId = -1;
        for (int i = 0; i < count; i++) {
            String name = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            if (this.DEBUG_DESKTOP) {
                System.out.println("id string = " + value);
            }
            switch (name.hashCode()) {
                case -1496482599:
                    if (name.equals("deriveConstraintsFrom")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 3355:
                    if (name.equals("id")) {
                        z = false;
                        break;
                    }
                    z = true;
                    break;
                default:
                    z = true;
                    break;
            }
            switch (z) {
                case false:
                    id = getId(context, value);
                    this.mConstraintSetIdMap.put(stripID(value), Integer.valueOf(id));
                    break;
                case true:
                    derivedId = getId(context, value);
                    break;
            }
        }
        if (id != -1) {
            if (this.mMotionLayout.mDebugPath != 0) {
                set.setValidateOnParse(true);
            }
            set.load(context, parser);
            if (derivedId != -1) {
                this.mDeriveMap.put(id, derivedId);
            }
            this.mConstraintSetMap.put(id, set);
        }
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    public ConstraintSet getConstraintSet(Context context, String id) {
        if (this.DEBUG_DESKTOP) {
            System.out.println("id " + id);
            System.out.println("size " + this.mConstraintSetMap.size());
        }
        for (int i = 0; i < this.mConstraintSetMap.size(); i++) {
            int key = this.mConstraintSetMap.keyAt(i);
            String IdAsString = context.getResources().getResourceName(key);
            if (this.DEBUG_DESKTOP) {
                System.out.println("Id for <" + i + "> is <" + IdAsString + "> looking for <" + id + ">");
            }
            if (id.equals(IdAsString)) {
                return this.mConstraintSetMap.get(key);
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public ConstraintSet getConstraintSet(int id) {
        return getConstraintSet(id, -1, -1);
    }

    /* access modifiers changed from: package-private */
    public ConstraintSet getConstraintSet(int id, int width, int height) {
        int cid;
        if (this.DEBUG_DESKTOP) {
            System.out.println("id " + id);
            System.out.println("size " + this.mConstraintSetMap.size());
        }
        if (!(this.mStateSet == null || (cid = this.mStateSet.stateGetConstraintID(id, width, height)) == -1)) {
            id = cid;
        }
        if (this.mConstraintSetMap.get(id) != null) {
            return this.mConstraintSetMap.get(id);
        }
        Log.e(TAG, "Warning could not find ConstraintSet id/" + Debug.getName(this.mMotionLayout.getContext(), id) + " In MotionScene");
        return this.mConstraintSetMap.get(this.mConstraintSetMap.keyAt(0));
    }

    public void setConstraintSet(int id, ConstraintSet set) {
        this.mConstraintSetMap.put(id, set);
    }

    public void getKeyFrames(MotionController motionController) {
        if (this.mCurrentTransition != null) {
            Iterator it = this.mCurrentTransition.mKeyFramesList.iterator();
            while (it.hasNext()) {
                ((KeyFrames) it.next()).addFrames(motionController);
            }
        } else if (this.mDefaultTransition != null) {
            Iterator it2 = this.mDefaultTransition.mKeyFramesList.iterator();
            while (it2.hasNext()) {
                ((KeyFrames) it2.next()).addFrames(motionController);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public Key getKeyFrame(Context context, int type, int target, int position) {
        if (this.mCurrentTransition == null) {
            return null;
        }
        Iterator it = this.mCurrentTransition.mKeyFramesList.iterator();
        while (it.hasNext()) {
            KeyFrames keyFrames = (KeyFrames) it.next();
            Iterator<Integer> it2 = keyFrames.getKeys().iterator();
            while (true) {
                if (it2.hasNext()) {
                    Integer integer = it2.next();
                    if (target == integer.intValue()) {
                        Iterator<Key> it3 = keyFrames.getKeyFramesForView(integer.intValue()).iterator();
                        while (it3.hasNext()) {
                            Key key = it3.next();
                            if (key.mFramePosition == position && key.mType == type) {
                                return key;
                            }
                        }
                        continue;
                    }
                }
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int getTransitionDirection(int stateId) {
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            if (it.next().mConstraintSetStart == stateId) {
                return 0;
            }
        }
        return 1;
    }

    /* access modifiers changed from: package-private */
    public boolean hasKeyFramePosition(View view, int position) {
        if (this.mCurrentTransition == null) {
            return false;
        }
        Iterator it = this.mCurrentTransition.mKeyFramesList.iterator();
        while (it.hasNext()) {
            Iterator<Key> it2 = ((KeyFrames) it.next()).getKeyFramesForView(view.getId()).iterator();
            while (true) {
                if (it2.hasNext()) {
                    if (it2.next().mFramePosition == position) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setKeyframe(View view, int position, String name, Object value) {
        if (this.mCurrentTransition != null) {
            Iterator it = this.mCurrentTransition.mKeyFramesList.iterator();
            while (it.hasNext()) {
                Iterator<Key> it2 = ((KeyFrames) it.next()).getKeyFramesForView(view.getId()).iterator();
                while (it2.hasNext()) {
                    if (it2.next().mFramePosition == position) {
                        float v = 0.0f;
                        if (value != null) {
                            v = ((Float) value).floatValue();
                        }
                        if (v == 0.0f) {
                        }
                        if (name.equalsIgnoreCase("app:PerpendicularPath_percent")) {
                        }
                    }
                }
            }
        }
    }

    public float getPathPercent(View view, int position) {
        return 0.0f;
    }

    /* access modifiers changed from: package-private */
    public boolean supportTouch() {
        Iterator<Transition> it = this.mTransitionList.iterator();
        while (it.hasNext()) {
            if (it.next().mTouchResponse != null) {
                return true;
            }
        }
        if (this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Can't fix incorrect switch cases order, some code will duplicate */
    /* access modifiers changed from: package-private */
    public void processTouchEvent(MotionEvent event, int currentState, MotionLayout motionLayout) {
        RectF cache = new RectF();
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = this.mMotionLayout.obtainVelocityTracker();
        }
        this.mVelocityTracker.addMovement(event);
        if (currentState != -1) {
            switch (event.getAction()) {
                case 0:
                    this.mLastTouchX = event.getRawX();
                    this.mLastTouchY = event.getRawY();
                    this.mLastTouchDown = event;
                    if (this.mCurrentTransition.mTouchResponse != null) {
                        RectF region = this.mCurrentTransition.mTouchResponse.getLimitBoundsTo(this.mMotionLayout, cache);
                        if (region == null || region.contains(this.mLastTouchDown.getX(), this.mLastTouchDown.getY())) {
                            RectF region2 = this.mCurrentTransition.mTouchResponse.getTouchRegion(this.mMotionLayout, cache);
                            if (region2 == null || region2.contains(this.mLastTouchDown.getX(), this.mLastTouchDown.getY())) {
                                this.mMotionOutsideRegion = false;
                            } else {
                                this.mMotionOutsideRegion = true;
                            }
                            this.mCurrentTransition.mTouchResponse.setDown(this.mLastTouchX, this.mLastTouchY);
                            return;
                        }
                        this.mLastTouchDown = null;
                        return;
                    }
                    return;
                case 2:
                    float dy = event.getRawY() - this.mLastTouchY;
                    float dx = event.getRawX() - this.mLastTouchX;
                    if ((((double) dx) != 0.0d || ((double) dy) != 0.0d) && this.mLastTouchDown != null) {
                        Transition transition = bestTransitionFor(currentState, dx, dy, this.mLastTouchDown);
                        if (transition != null) {
                            motionLayout.setTransition(transition);
                            RectF region3 = this.mCurrentTransition.mTouchResponse.getTouchRegion(this.mMotionLayout, cache);
                            this.mMotionOutsideRegion = region3 != null && !region3.contains(this.mLastTouchDown.getX(), this.mLastTouchDown.getY());
                            this.mCurrentTransition.mTouchResponse.setUpTouchEvent(this.mLastTouchX, this.mLastTouchY);
                            break;
                        }
                    } else {
                        return;
                    }
                    break;
            }
        }
        if (!(this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null || this.mMotionOutsideRegion)) {
            this.mCurrentTransition.mTouchResponse.processTouchEvent(event, this.mVelocityTracker, currentState, this);
        }
        this.mLastTouchX = event.getRawX();
        this.mLastTouchY = event.getRawY();
        if (event.getAction() == 1 && this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            if (motionLayout.mCurrentState != -1) {
                autoTransition(motionLayout, motionLayout.mCurrentState);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void processScrollMove(float dx, float dy) {
        if (this.mCurrentTransition != null && this.mCurrentTransition.mTouchResponse != null) {
            this.mCurrentTransition.mTouchResponse.scrollMove(dx, dy);
        }
    }

    /* access modifiers changed from: package-private */
    public void processScrollUp(float dx, float dy) {
        if (this.mCurrentTransition != null && this.mCurrentTransition.mTouchResponse != null) {
            this.mCurrentTransition.mTouchResponse.scrollUp(dx, dy);
        }
    }

    /* access modifiers changed from: package-private */
    public float getProgressDirection(float dx, float dy) {
        if (this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null) {
            return 0.0f;
        }
        return this.mCurrentTransition.mTouchResponse.getProgressDirection(dx, dy);
    }

    /* access modifiers changed from: package-private */
    public int getStartId() {
        if (this.mCurrentTransition == null) {
            return -1;
        }
        return this.mCurrentTransition.mConstraintSetStart;
    }

    /* access modifiers changed from: package-private */
    public int getEndId() {
        if (this.mCurrentTransition == null) {
            return -1;
        }
        return this.mCurrentTransition.mConstraintSetEnd;
    }

    public Interpolator getInterpolator() {
        switch (this.mCurrentTransition.mDefaultInterpolator) {
            case -2:
                return AnimationUtils.loadInterpolator(this.mMotionLayout.getContext(), this.mCurrentTransition.mDefaultInterpolatorID);
            case -1:
                final Easing easing = Easing.getInterpolator(this.mCurrentTransition.mDefaultInterpolatorString);
                return new Interpolator() {
                    /* class androidx.constraintlayout.motion.widget.MotionScene.AnonymousClass1 */

                    public float getInterpolation(float v) {
                        return (float) easing.get((double) v);
                    }
                };
            case 0:
                return new AccelerateDecelerateInterpolator();
            case 1:
                return new AccelerateInterpolator();
            case 2:
                return new DecelerateInterpolator();
            case 3:
            default:
                return null;
            case 4:
                return new AnticipateInterpolator();
            case 5:
                return new BounceInterpolator();
        }
    }

    public int getDuration() {
        if (this.mCurrentTransition != null) {
            return this.mCurrentTransition.mDuration;
        }
        return this.mDefaultDuration;
    }

    public void setDuration(int duration) {
        if (this.mCurrentTransition != null) {
            this.mCurrentTransition.setDuration(duration);
        } else {
            this.mDefaultDuration = duration;
        }
    }

    public int gatPathMotionArc() {
        if (this.mCurrentTransition != null) {
            return this.mCurrentTransition.mPathMotionArc;
        }
        return -1;
    }

    public float getStaggered() {
        if (this.mCurrentTransition != null) {
            return this.mCurrentTransition.mStagger;
        }
        return 0.0f;
    }

    /* access modifiers changed from: package-private */
    public float getMaxAcceleration() {
        if (this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null) {
            return 0.0f;
        }
        return this.mCurrentTransition.mTouchResponse.getMaxAcceleration();
    }

    /* access modifiers changed from: package-private */
    public float getMaxVelocity() {
        if (this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null) {
            return 0.0f;
        }
        return this.mCurrentTransition.mTouchResponse.getMaxVelocity();
    }

    /* access modifiers changed from: package-private */
    public void setupTouch() {
        if (this.mCurrentTransition != null && this.mCurrentTransition.mTouchResponse != null) {
            this.mCurrentTransition.mTouchResponse.setupTouch();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean getMoveWhenScrollAtTop() {
        if (this.mCurrentTransition == null || this.mCurrentTransition.mTouchResponse == null) {
            return false;
        }
        return this.mCurrentTransition.mTouchResponse.getMoveWhenScrollAtTop();
    }

    /* access modifiers changed from: package-private */
    public void readFallback(MotionLayout motionLayout) {
        int i = 0;
        while (i < this.mConstraintSetMap.size()) {
            int key = this.mConstraintSetMap.keyAt(i);
            if (hasCycleDependency(key)) {
                Log.e(TAG, "Cannot be derived from yourself");
                return;
            } else {
                readConstraintChain(key);
                i++;
            }
        }
        for (int i2 = 0; i2 < this.mConstraintSetMap.size(); i2++) {
            this.mConstraintSetMap.valueAt(i2).readFallback(motionLayout);
        }
    }

    private boolean hasCycleDependency(int key) {
        int derived = this.mDeriveMap.get(key);
        int len = this.mDeriveMap.size();
        while (derived > 0) {
            if (derived == key) {
                return true;
            }
            int len2 = len - 1;
            if (len < 0) {
                return true;
            }
            derived = this.mDeriveMap.get(derived);
            len = len2;
        }
        return false;
    }

    private void readConstraintChain(int key) {
        int derivedFromId = this.mDeriveMap.get(key);
        if (derivedFromId > 0) {
            readConstraintChain(this.mDeriveMap.get(key));
            ConstraintSet cs = this.mConstraintSetMap.get(key);
            ConstraintSet derivedFrom = this.mConstraintSetMap.get(derivedFromId);
            if (derivedFrom == null) {
                Log.e(TAG, "invalid deriveConstraintsFrom id");
            }
            cs.readFallback(derivedFrom);
            this.mDeriveMap.put(key, -1);
        }
    }

    public static String stripID(String id) {
        if (id == null) {
            return "";
        }
        int index = id.indexOf(47);
        return index >= 0 ? id.substring(index + 1) : id;
    }

    public int lookUpConstraintId(String id) {
        return this.mConstraintSetIdMap.get(id).intValue();
    }

    public String lookUpConstraintName(int id) {
        for (Map.Entry<String, Integer> entry : this.mConstraintSetIdMap.entrySet()) {
            if (entry.getValue().intValue() == id) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void disableAutoTransition(boolean disable) {
        this.mDisableAutoTransition = disable;
    }
}
