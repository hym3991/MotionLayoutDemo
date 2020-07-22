package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.R;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class KeyCycle extends Key {
    public static final int KEY_TYPE = 4;
    static final String NAME = "KeyCycle";
    private static final String TAG = "KeyCycle";
    /* access modifiers changed from: private */
    public float mAlpha = Float.NaN;
    /* access modifiers changed from: private */
    public int mCurveFit = 0;
    /* access modifiers changed from: private */
    public float mElevation = Float.NaN;
    /* access modifiers changed from: private */
    public float mProgress = Float.NaN;
    /* access modifiers changed from: private */
    public float mRotation = Float.NaN;
    /* access modifiers changed from: private */
    public float mRotationX = Float.NaN;
    /* access modifiers changed from: private */
    public float mRotationY = Float.NaN;
    /* access modifiers changed from: private */
    public float mScaleX = Float.NaN;
    /* access modifiers changed from: private */
    public float mScaleY = Float.NaN;
    /* access modifiers changed from: private */
    public String mTransitionEasing = null;
    /* access modifiers changed from: private */
    public float mTransitionPathRotate = Float.NaN;
    /* access modifiers changed from: private */
    public float mTranslationX = Float.NaN;
    /* access modifiers changed from: private */
    public float mTranslationY = Float.NaN;
    /* access modifiers changed from: private */
    public float mTranslationZ = Float.NaN;
    /* access modifiers changed from: private */
    public float mWaveOffset = 0.0f;
    /* access modifiers changed from: private */
    public float mWavePeriod = Float.NaN;
    /* access modifiers changed from: private */
    public int mWaveShape = -1;
    /* access modifiers changed from: private */
    public int mWaveVariesBy = -1;

    public KeyCycle() {
        this.mType = 4;
        this.mCustomConstraints = new HashMap();
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void load(Context context, AttributeSet attrs) {
        Loader.read(this, context.obtainStyledAttributes(attrs, R.styleable.KeyCycle));
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void getAttributeNames(HashSet<String> attributes) {
        if (!Float.isNaN(this.mAlpha)) {
            attributes.add("alpha");
        }
        if (!Float.isNaN(this.mElevation)) {
            attributes.add("elevation");
        }
        if (!Float.isNaN(this.mRotation)) {
            attributes.add("rotation");
        }
        if (!Float.isNaN(this.mRotationX)) {
            attributes.add("rotationX");
        }
        if (!Float.isNaN(this.mRotationY)) {
            attributes.add("rotationY");
        }
        if (!Float.isNaN(this.mScaleX)) {
            attributes.add("scaleX");
        }
        if (!Float.isNaN(this.mScaleY)) {
            attributes.add("scaleY");
        }
        if (!Float.isNaN(this.mTransitionPathRotate)) {
            attributes.add("transitionPathRotate");
        }
        if (!Float.isNaN(this.mTranslationX)) {
            attributes.add("translationX");
        }
        if (!Float.isNaN(this.mTranslationY)) {
            attributes.add("translationY");
        }
        if (!Float.isNaN(this.mTranslationZ)) {
            attributes.add("translationZ");
        }
        if (this.mCustomConstraints.size() > 0) {
            Iterator it = this.mCustomConstraints.keySet().iterator();
            while (it.hasNext()) {
                attributes.add("CUSTOM," + ((String) it.next()));
            }
        }
    }

    public void addCycleValues(HashMap<String, KeyCycleOscillator> oscSet) {
        for (String key : oscSet.keySet()) {
            if (key.startsWith("CUSTOM")) {
                ConstraintAttribute cvalue = (ConstraintAttribute) this.mCustomConstraints.get(key.substring("CUSTOM".length() + 1));
                if (cvalue != null && cvalue.getType() == ConstraintAttribute.AttributeType.FLOAT_TYPE) {
                    oscSet.get(key).setPoint(this.mFramePosition, this.mWaveShape, this.mWaveVariesBy, this.mWavePeriod, this.mWaveOffset, cvalue.getValueToInterpolate(), cvalue);
                }
            }
            float value = getValue(key);
            if (!Float.isNaN(value)) {
                oscSet.get(key).setPoint(this.mFramePosition, this.mWaveShape, this.mWaveVariesBy, this.mWavePeriod, this.mWaveOffset, value);
            }
        }
    }

    public float getValue(String key) {
        char c = 65535;
        switch (key.hashCode()) {
            case -1249320806:
                if (key.equals("rotationX")) {
                    c = 3;
                    break;
                }
                break;
            case -1249320805:
                if (key.equals("rotationY")) {
                    c = 4;
                    break;
                }
                break;
            case -1225497657:
                if (key.equals("translationX")) {
                    c = '\b';
                    break;
                }
                break;
            case -1225497656:
                if (key.equals("translationY")) {
                    c = '\t';
                    break;
                }
                break;
            case -1225497655:
                if (key.equals("translationZ")) {
                    c = '\n';
                    break;
                }
                break;
            case -1001078227:
                if (key.equals("progress")) {
                    c = '\f';
                    break;
                }
                break;
            case -908189618:
                if (key.equals("scaleX")) {
                    c = 6;
                    break;
                }
                break;
            case -908189617:
                if (key.equals("scaleY")) {
                    c = 7;
                    break;
                }
                break;
            case -40300674:
                if (key.equals("rotation")) {
                    c = 2;
                    break;
                }
                break;
            case -4379043:
                if (key.equals("elevation")) {
                    c = 1;
                    break;
                }
                break;
            case 37232917:
                if (key.equals("transitionPathRotate")) {
                    c = 5;
                    break;
                }
                break;
            case 92909918:
                if (key.equals("alpha")) {
                    c = 0;
                    break;
                }
                break;
            case 156108012:
                if (key.equals("waveOffset")) {
                    c = 11;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return this.mAlpha;
            case 1:
                return this.mElevation;
            case 2:
                return this.mRotation;
            case 3:
                return this.mRotationX;
            case 4:
                return this.mRotationY;
            case 5:
                return this.mTransitionPathRotate;
            case 6:
                return this.mScaleX;
            case 7:
                return this.mScaleY;
            case '\b':
                return this.mTranslationX;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF:
                return this.mTranslationY;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF:
                return this.mTranslationZ;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF:
                return this.mWaveOffset;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_TOP_OF:
                return this.mProgress;
            default:
                Log.v("WARNING! KeyCycle", "  UNKNOWN  " + key);
                return Float.NaN;
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void addValues(HashMap<String, SplineSet> splines) {
        Debug.logStack("KeyCycle", "add " + splines.size() + " values", 2);
        for (String s : splines.keySet()) {
            SplineSet splineSet = splines.get(s);
            char c = 65535;
            switch (s.hashCode()) {
                case -1249320806:
                    if (s.equals("rotationX")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1249320805:
                    if (s.equals("rotationY")) {
                        c = 4;
                        break;
                    }
                    break;
                case -1225497657:
                    if (s.equals("translationX")) {
                        c = '\b';
                        break;
                    }
                    break;
                case -1225497656:
                    if (s.equals("translationY")) {
                        c = '\t';
                        break;
                    }
                    break;
                case -1225497655:
                    if (s.equals("translationZ")) {
                        c = '\n';
                        break;
                    }
                    break;
                case -1001078227:
                    if (s.equals("progress")) {
                        c = '\f';
                        break;
                    }
                    break;
                case -908189618:
                    if (s.equals("scaleX")) {
                        c = 6;
                        break;
                    }
                    break;
                case -908189617:
                    if (s.equals("scaleY")) {
                        c = 7;
                        break;
                    }
                    break;
                case -40300674:
                    if (s.equals("rotation")) {
                        c = 2;
                        break;
                    }
                    break;
                case -4379043:
                    if (s.equals("elevation")) {
                        c = 1;
                        break;
                    }
                    break;
                case 37232917:
                    if (s.equals("transitionPathRotate")) {
                        c = 5;
                        break;
                    }
                    break;
                case 92909918:
                    if (s.equals("alpha")) {
                        c = 0;
                        break;
                    }
                    break;
                case 156108012:
                    if (s.equals("waveOffset")) {
                        c = 11;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    splineSet.setPoint(this.mFramePosition, this.mAlpha);
                    break;
                case 1:
                    splineSet.setPoint(this.mFramePosition, this.mElevation);
                    break;
                case 2:
                    splineSet.setPoint(this.mFramePosition, this.mRotation);
                    break;
                case 3:
                    splineSet.setPoint(this.mFramePosition, this.mRotationX);
                    break;
                case 4:
                    splineSet.setPoint(this.mFramePosition, this.mRotationY);
                    break;
                case 5:
                    splineSet.setPoint(this.mFramePosition, this.mTransitionPathRotate);
                    break;
                case 6:
                    splineSet.setPoint(this.mFramePosition, this.mScaleX);
                    break;
                case 7:
                    splineSet.setPoint(this.mFramePosition, this.mScaleY);
                    break;
                case '\b':
                    splineSet.setPoint(this.mFramePosition, this.mTranslationX);
                    break;
                case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF:
                    splineSet.setPoint(this.mFramePosition, this.mTranslationY);
                    break;
                case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF:
                    splineSet.setPoint(this.mFramePosition, this.mTranslationZ);
                    break;
                case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF:
                    splineSet.setPoint(this.mFramePosition, this.mWaveOffset);
                    break;
                case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_TOP_OF:
                    splineSet.setPoint(this.mFramePosition, this.mProgress);
                    break;
                default:
                    Log.v("WARNING KeyCycle", "  UNKNOWN  " + s);
                    break;
            }
        }
    }

    private static class Loader {
        private static final int ANDROID_ALPHA = 9;
        private static final int ANDROID_ELEVATION = 10;
        private static final int ANDROID_ROTATION = 11;
        private static final int ANDROID_ROTATION_X = 12;
        private static final int ANDROID_ROTATION_Y = 13;
        private static final int ANDROID_SCALE_X = 15;
        private static final int ANDROID_SCALE_Y = 16;
        private static final int ANDROID_TRANSLATION_X = 17;
        private static final int ANDROID_TRANSLATION_Y = 18;
        private static final int ANDROID_TRANSLATION_Z = 19;
        private static final int CURVE_FIT = 4;
        private static final int FRAME_POSITION = 2;
        private static final int PROGRESS = 20;
        private static final int TARGET_ID = 1;
        private static final int TRANSITION_EASING = 3;
        private static final int TRANSITION_PATH_ROTATE = 14;
        private static final int WAVE_OFFSET = 7;
        private static final int WAVE_PERIOD = 6;
        private static final int WAVE_SHAPE = 5;
        private static final int WAVE_VARIES_BY = 8;
        private static SparseIntArray mAttrMap = new SparseIntArray();

        private Loader() {
        }

        static {
            mAttrMap.append(R.styleable.KeyCycle_motionTarget, 1);
            mAttrMap.append(R.styleable.KeyCycle_framePosition, 2);
            mAttrMap.append(R.styleable.KeyCycle_transitionEasing, 3);
            mAttrMap.append(R.styleable.KeyCycle_curveFit, 4);
            mAttrMap.append(R.styleable.KeyCycle_waveShape, 5);
            mAttrMap.append(R.styleable.KeyCycle_wavePeriod, 6);
            mAttrMap.append(R.styleable.KeyCycle_waveOffset, 7);
            mAttrMap.append(R.styleable.KeyCycle_waveVariesBy, 8);
            mAttrMap.append(R.styleable.KeyCycle_android_alpha, 9);
            mAttrMap.append(R.styleable.KeyCycle_android_elevation, 10);
            mAttrMap.append(R.styleable.KeyCycle_android_rotation, 11);
            mAttrMap.append(R.styleable.KeyCycle_android_rotationX, 12);
            mAttrMap.append(R.styleable.KeyCycle_android_rotationY, 13);
            mAttrMap.append(R.styleable.KeyCycle_transitionPathRotate, 14);
            mAttrMap.append(R.styleable.KeyCycle_android_scaleX, 15);
            mAttrMap.append(R.styleable.KeyCycle_android_scaleY, 16);
            mAttrMap.append(R.styleable.KeyCycle_android_translationX, 17);
            mAttrMap.append(R.styleable.KeyCycle_android_translationY, 18);
            mAttrMap.append(R.styleable.KeyCycle_android_translationZ, 19);
            mAttrMap.append(R.styleable.KeyCycle_motionProgress, 20);
        }

        /* access modifiers changed from: private */
        public static void read(KeyCycle c, TypedArray a) {
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (mAttrMap.get(attr)) {
                    case 1:
                        if (!MotionLayout.IS_IN_EDIT_MODE) {
                            if (a.peekValue(attr).type != 3) {
                                c.mTargetId = a.getResourceId(attr, c.mTargetId);
                                break;
                            } else {
                                c.mTargetString = a.getString(attr);
                                break;
                            }
                        } else {
                            c.mTargetId = a.getResourceId(attr, c.mTargetId);
                            if (c.mTargetId != -1) {
                                break;
                            } else {
                                c.mTargetString = a.getString(attr);
                                break;
                            }
                        }
                    case 2:
                        c.mFramePosition = a.getInt(attr, c.mFramePosition);
                        break;
                    case 3:
                        String unused = c.mTransitionEasing = a.getString(attr);
                        break;
                    case 4:
                        int unused2 = c.mCurveFit = a.getInteger(attr, c.mCurveFit);
                        break;
                    case 5:
                        int unused3 = c.mWaveShape = a.getInt(attr, c.mWaveShape);
                        break;
                    case 6:
                        float unused4 = c.mWavePeriod = a.getFloat(attr, c.mWavePeriod);
                        break;
                    case 7:
                        if (a.peekValue(attr).type != 5) {
                            float unused5 = c.mWaveOffset = a.getFloat(attr, c.mWaveOffset);
                            break;
                        } else {
                            float unused6 = c.mWaveOffset = a.getDimension(attr, c.mWaveOffset);
                            break;
                        }
                    case 8:
                        int unused7 = c.mWaveVariesBy = a.getInt(attr, c.mWaveVariesBy);
                        break;
                    case 9:
                        float unused8 = c.mAlpha = a.getFloat(attr, c.mAlpha);
                        break;
                    case 10:
                        float unused9 = c.mElevation = a.getDimension(attr, c.mElevation);
                        break;
                    case 11:
                        float unused10 = c.mRotation = a.getFloat(attr, c.mRotation);
                        break;
                    case 12:
                        float unused11 = c.mRotationX = a.getFloat(attr, c.mRotationX);
                        break;
                    case 13:
                        float unused12 = c.mRotationY = a.getFloat(attr, c.mRotationY);
                        break;
                    case 14:
                        float unused13 = c.mTransitionPathRotate = a.getFloat(attr, c.mTransitionPathRotate);
                        break;
                    case 15:
                        float unused14 = c.mScaleX = a.getFloat(attr, c.mScaleX);
                        break;
                    case 16:
                        float unused15 = c.mScaleY = a.getFloat(attr, c.mScaleY);
                        break;
                    case 17:
                        float unused16 = c.mTranslationX = a.getDimension(attr, c.mTranslationX);
                        break;
                    case 18:
                        float unused17 = c.mTranslationY = a.getDimension(attr, c.mTranslationY);
                        break;
                    case 19:
                        if (Build.VERSION.SDK_INT < 21) {
                            break;
                        } else {
                            float unused18 = c.mTranslationZ = a.getDimension(attr, c.mTranslationZ);
                            break;
                        }
                    case 20:
                        float unused19 = c.mProgress = a.getFloat(attr, c.mProgress);
                        break;
                    default:
                        Log.e("KeyCycle", "unused attribute 0x" + Integer.toHexString(attr) + "   " + mAttrMap.get(attr));
                        break;
                }
            }
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void setValue(String tag, Object value) {
        char c = 65535;
        switch (tag.hashCode()) {
            case -1812823328:
                if (tag.equals("transitionEasing")) {
                    c = '\t';
                    break;
                }
                break;
            case -1249320806:
                if (tag.equals("rotationX")) {
                    c = 5;
                    break;
                }
                break;
            case -1249320805:
                if (tag.equals("rotationY")) {
                    c = 6;
                    break;
                }
                break;
            case -1225497657:
                if (tag.equals("translationX")) {
                    c = 11;
                    break;
                }
                break;
            case -1225497656:
                if (tag.equals("translationY")) {
                    c = '\f';
                    break;
                }
                break;
            case -1001078227:
                if (tag.equals("progress")) {
                    c = 3;
                    break;
                }
                break;
            case -908189618:
                if (tag.equals("scaleX")) {
                    c = 7;
                    break;
                }
                break;
            case -908189617:
                if (tag.equals("scaleY")) {
                    c = '\b';
                    break;
                }
                break;
            case -40300674:
                if (tag.equals("rotation")) {
                    c = 4;
                    break;
                }
                break;
            case -4379043:
                if (tag.equals("elevation")) {
                    c = 2;
                    break;
                }
                break;
            case 37232917:
                if (tag.equals("transitionPathRotate")) {
                    c = '\n';
                    break;
                }
                break;
            case 92909918:
                if (tag.equals("alpha")) {
                    c = 0;
                    break;
                }
                break;
            case 156108012:
                if (tag.equals("waveOffset")) {
                    c = 15;
                    break;
                }
                break;
            case 184161818:
                if (tag.equals("wavePeriod")) {
                    c = 14;
                    break;
                }
                break;
            case 579057826:
                if (tag.equals("curveFit")) {
                    c = 1;
                    break;
                }
                break;
            case 1317633238:
                if (tag.equals("mTranslationZ")) {
                    c = '\r';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.mAlpha = toFloat(value);
                return;
            case 1:
                this.mCurveFit = toInt(value);
                return;
            case 2:
                this.mElevation = toFloat(value);
                return;
            case 3:
                this.mProgress = toFloat(value);
                return;
            case 4:
                this.mRotation = toFloat(value);
                return;
            case 5:
                this.mRotationX = toFloat(value);
                return;
            case 6:
                this.mRotationY = toFloat(value);
                return;
            case 7:
                this.mScaleX = toFloat(value);
                return;
            case '\b':
                this.mScaleY = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF:
                this.mTransitionEasing = value.toString();
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF:
                this.mTransitionPathRotate = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF:
                this.mTranslationX = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_TOP_OF:
                this.mTranslationY = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF:
                this.mTranslationZ = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF:
                this.mWavePeriod = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF:
                this.mWaveOffset = toFloat(value);
                return;
            default:
                return;
        }
    }
}
