package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;

import androidx.constraintlayout.R;
import androidx.constraintlayout.motion.widget.SplineSet;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class KeyAttributes extends Key {
    public static final int KEY_TYPE = 1;
    static final String NAME = "KeyAttribute";
    private static final String TAG = "KeyAttribute";
    /* access modifiers changed from: private */
    public float mAlpha = Float.NaN;
    /* access modifiers changed from: private */
    public int mCurveFit = -1;
    /* access modifiers changed from: private */
    public float mElevation = Float.NaN;
    /* access modifiers changed from: private */
    public float mPivotX = Float.NaN;
    /* access modifiers changed from: private */
    public float mPivotY = Float.NaN;
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
    public String mTransitionEasing;
    /* access modifiers changed from: private */
    public float mTransitionPathRotate = Float.NaN;
    /* access modifiers changed from: private */
    public float mTranslationX = Float.NaN;
    /* access modifiers changed from: private */
    public float mTranslationY = Float.NaN;
    /* access modifiers changed from: private */
    public float mTranslationZ = Float.NaN;
    private boolean mVisibility = false;

    public KeyAttributes() {
        this.mType = 1;
        this.mCustomConstraints = new HashMap();
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void load(Context context, AttributeSet attrs) {
        Loader.read(this, context.obtainStyledAttributes(attrs, R.styleable.KeyAttribute));
    }

    /* access modifiers changed from: package-private */
    public int getCurveFit() {
        return this.mCurveFit;
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
        if (!Float.isNaN(this.mPivotX)) {
            attributes.add("transformPivotX");
        }
        if (!Float.isNaN(this.mPivotY)) {
            attributes.add("transformPivotY");
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
        if (!Float.isNaN(this.mTransitionPathRotate)) {
            attributes.add("transitionPathRotate");
        }
        if (!Float.isNaN(this.mScaleX)) {
            attributes.add("scaleX");
        }
        if (!Float.isNaN(this.mScaleX)) {
            attributes.add("scaleY");
        }
        if (!Float.isNaN(this.mProgress)) {
            attributes.add("progress");
        }
        if (this.mCustomConstraints.size() > 0) {
            Iterator it = this.mCustomConstraints.keySet().iterator();
            while (it.hasNext()) {
                attributes.add("CUSTOM," + ((String) it.next()));
            }
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void setInterpolation(HashMap<String, Integer> interpolation) {
        if (this.mCurveFit != -1) {
            if (!Float.isNaN(this.mAlpha)) {
                interpolation.put("alpha", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mElevation)) {
                interpolation.put("elevation", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mRotation)) {
                interpolation.put("rotation", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mRotationX)) {
                interpolation.put("rotationX", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mRotationY)) {
                interpolation.put("rotationY", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mPivotX)) {
                interpolation.put("transformPivotX", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mPivotY)) {
                interpolation.put("transformPivotY", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mTranslationX)) {
                interpolation.put("translationX", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mTranslationY)) {
                interpolation.put("translationY", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mTranslationZ)) {
                interpolation.put("translationZ", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mTransitionPathRotate)) {
                interpolation.put("transitionPathRotate", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mScaleX)) {
                interpolation.put("scaleX", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mScaleY)) {
                interpolation.put("scaleY", Integer.valueOf(this.mCurveFit));
            }
            if (!Float.isNaN(this.mProgress)) {
                interpolation.put("progress", Integer.valueOf(this.mCurveFit));
            }
            if (this.mCustomConstraints.size() > 0) {
                Iterator it = this.mCustomConstraints.keySet().iterator();
                while (it.hasNext()) {
                    interpolation.put("CUSTOM," + ((String) it.next()), Integer.valueOf(this.mCurveFit));
                }
            }
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void addValues(HashMap<String, SplineSet> splines) {
        for (String s : splines.keySet()) {
            SplineSet splineSet = splines.get(s);
            if (s.startsWith("CUSTOM")) {
                ConstraintAttribute cvalue = (ConstraintAttribute) this.mCustomConstraints.get(s.substring("CUSTOM".length() + 1));
                if (cvalue != null) {
                    ((SplineSet.CustomSet) splineSet).setPoint(this.mFramePosition, cvalue);
                }
            } else {
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
                            c = '\n';
                            break;
                        }
                        break;
                    case -1225497656:
                        if (s.equals("translationY")) {
                            c = 11;
                            break;
                        }
                        break;
                    case -1225497655:
                        if (s.equals("translationZ")) {
                            c = '\f';
                            break;
                        }
                        break;
                    case -1001078227:
                        if (s.equals("progress")) {
                            c = '\r';
                            break;
                        }
                        break;
                    case -908189618:
                        if (s.equals("scaleX")) {
                            c = '\b';
                            break;
                        }
                        break;
                    case -908189617:
                        if (s.equals("scaleY")) {
                            c = '\t';
                            break;
                        }
                        break;
                    case -760884510:
                        if (s.equals("transformPivotX")) {
                            c = 5;
                            break;
                        }
                        break;
                    case -760884509:
                        if (s.equals("transformPivotY")) {
                            c = 6;
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
                            c = 7;
                            break;
                        }
                        break;
                    case 92909918:
                        if (s.equals("alpha")) {
                            c = 0;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        if (!Float.isNaN(this.mAlpha)) {
                            splineSet.setPoint(this.mFramePosition, this.mAlpha);
                            break;
                        } else {
                            continue;
                        }
                    case 1:
                        if (!Float.isNaN(this.mElevation)) {
                            splineSet.setPoint(this.mFramePosition, this.mElevation);
                            break;
                        } else {
                            continue;
                        }
                    case 2:
                        if (!Float.isNaN(this.mRotation)) {
                            splineSet.setPoint(this.mFramePosition, this.mRotation);
                            break;
                        } else {
                            continue;
                        }
                    case 3:
                        if (!Float.isNaN(this.mRotationX)) {
                            splineSet.setPoint(this.mFramePosition, this.mRotationX);
                            break;
                        } else {
                            continue;
                        }
                    case 4:
                        if (!Float.isNaN(this.mRotationY)) {
                            splineSet.setPoint(this.mFramePosition, this.mRotationY);
                            break;
                        } else {
                            continue;
                        }
                    case 5:
                        if (!Float.isNaN(this.mRotationX)) {
                            splineSet.setPoint(this.mFramePosition, this.mPivotX);
                            break;
                        } else {
                            continue;
                        }
                    case 6:
                        if (!Float.isNaN(this.mRotationY)) {
                            splineSet.setPoint(this.mFramePosition, this.mPivotY);
                            break;
                        } else {
                            continue;
                        }
                    case 7:
                        if (!Float.isNaN(this.mTransitionPathRotate)) {
                            splineSet.setPoint(this.mFramePosition, this.mTransitionPathRotate);
                            break;
                        } else {
                            continue;
                        }
                    case '\b':
                        if (!Float.isNaN(this.mScaleX)) {
                            splineSet.setPoint(this.mFramePosition, this.mScaleX);
                            break;
                        } else {
                            continue;
                        }
                    case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF:
                        if (!Float.isNaN(this.mScaleY)) {
                            splineSet.setPoint(this.mFramePosition, this.mScaleY);
                            break;
                        } else {
                            continue;
                        }
                    case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF:
                        if (!Float.isNaN(this.mTranslationX)) {
                            splineSet.setPoint(this.mFramePosition, this.mTranslationX);
                            break;
                        } else {
                            continue;
                        }
                    case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF:
                        if (!Float.isNaN(this.mTranslationY)) {
                            splineSet.setPoint(this.mFramePosition, this.mTranslationY);
                            break;
                        } else {
                            continue;
                        }
                    case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_TOP_OF:
                        if (!Float.isNaN(this.mTranslationZ)) {
                            splineSet.setPoint(this.mFramePosition, this.mTranslationZ);
                            break;
                        } else {
                            continue;
                        }
                    case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF:
                        if (!Float.isNaN(this.mProgress)) {
                            splineSet.setPoint(this.mFramePosition, this.mProgress);
                            break;
                        } else {
                            continue;
                        }
                    default:
                        Log.v("KeyAttributes", "UNKNOWN addValues \"" + s + "\"");
                        continue;
                }
            }
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void setValue(String tag, Object value) {
        char c = 65535;
        switch (tag.hashCode()) {
            case -1913008125:
                if (tag.equals("motionProgress")) {
                    c = 3;
                    break;
                }
                break;
            case -1812823328:
                if (tag.equals("transitionEasing")) {
                    c = 11;
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
                    c = 14;
                    break;
                }
                break;
            case -1225497656:
                if (tag.equals("translationY")) {
                    c = 15;
                    break;
                }
                break;
            case -987906986:
                if (tag.equals("pivotX")) {
                    c = 7;
                    break;
                }
                break;
            case -987906985:
                if (tag.equals("pivotY")) {
                    c = '\b';
                    break;
                }
                break;
            case -908189618:
                if (tag.equals("scaleX")) {
                    c = '\t';
                    break;
                }
                break;
            case -908189617:
                if (tag.equals("scaleY")) {
                    c = '\n';
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
                    c = '\r';
                    break;
                }
                break;
            case 92909918:
                if (tag.equals("alpha")) {
                    c = 0;
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
                    c = 16;
                    break;
                }
                break;
            case 1941332754:
                if (tag.equals("visibility")) {
                    c = '\f';
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
                this.mPivotX = toFloat(value);
                return;
            case '\b':
                this.mPivotY = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_LEFT_TO_RIGHT_OF:
                this.mScaleX = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_LEFT_OF:
                this.mScaleY = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF:
                this.mTransitionEasing = value.toString();
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_TOP_OF:
                this.mVisibility = toBoolean(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_TOP_TO_BOTTOM_OF:
                this.mTransitionPathRotate = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BOTTOM_TO_TOP_OF:
                this.mTranslationX = toFloat(value);
                return;
            case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_BOTTOM_TO_BOTTOM_OF:
                this.mTranslationY = toFloat(value);
                return;
            case 16:
                this.mTranslationZ = toFloat(value);
                return;
            default:
                return;
        }
    }

    private static class Loader {
        private static final int ANDROID_ALPHA = 1;
        private static final int ANDROID_ELEVATION = 2;
        private static final int ANDROID_PIVOT_X = 19;
        private static final int ANDROID_PIVOT_Y = 20;
        private static final int ANDROID_ROTATION = 4;
        private static final int ANDROID_ROTATION_X = 5;
        private static final int ANDROID_ROTATION_Y = 6;
        private static final int ANDROID_SCALE_X = 7;
        private static final int ANDROID_SCALE_Y = 14;
        private static final int ANDROID_TRANSLATION_X = 15;
        private static final int ANDROID_TRANSLATION_Y = 16;
        private static final int ANDROID_TRANSLATION_Z = 17;
        private static final int CURVE_FIT = 13;
        private static final int FRAME_POSITION = 12;
        private static final int PROGRESS = 18;
        private static final int TARGET_ID = 10;
        private static final int TRANSITION_EASING = 9;
        private static final int TRANSITION_PATH_ROTATE = 8;
        private static SparseIntArray mAttrMap = new SparseIntArray();

        private Loader() {
        }

        static {
            mAttrMap.append(R.styleable.KeyAttribute_android_alpha, 1);
            mAttrMap.append(R.styleable.KeyAttribute_android_elevation, 2);
            mAttrMap.append(R.styleable.KeyAttribute_android_rotation, 4);
            mAttrMap.append(R.styleable.KeyAttribute_android_rotationX, 5);
            mAttrMap.append(R.styleable.KeyAttribute_android_rotationY, 6);
            mAttrMap.append(R.styleable.KeyAttribute_android_transformPivotX, 19);
            mAttrMap.append(R.styleable.KeyAttribute_android_transformPivotY, 20);
            mAttrMap.append(R.styleable.KeyAttribute_android_scaleX, 7);
            mAttrMap.append(R.styleable.KeyAttribute_transitionPathRotate, 8);
            mAttrMap.append(R.styleable.KeyAttribute_transitionEasing, 9);
            mAttrMap.append(R.styleable.KeyAttribute_motionTarget, 10);
            mAttrMap.append(R.styleable.KeyAttribute_framePosition, 12);
            mAttrMap.append(R.styleable.KeyAttribute_curveFit, 13);
            mAttrMap.append(R.styleable.KeyAttribute_android_scaleY, 14);
            mAttrMap.append(R.styleable.KeyAttribute_android_translationX, 15);
            mAttrMap.append(R.styleable.KeyAttribute_android_translationY, 16);
            mAttrMap.append(R.styleable.KeyAttribute_android_translationZ, 17);
            mAttrMap.append(R.styleable.KeyAttribute_motionProgress, 18);
        }

        public static void read(KeyAttributes c, TypedArray a) {
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                switch (mAttrMap.get(attr)) {
                    case 1:
                        float unused = c.mAlpha = a.getFloat(attr, c.mAlpha);
                        break;
                    case 2:
                        float unused2 = c.mElevation = a.getDimension(attr, c.mElevation);
                        break;
                    case 3:
                    case ConstraintLayout.LayoutParams.Table.LAYOUT_CONSTRAINT_RIGHT_TO_RIGHT_OF:
                    default:
                        Log.e("KeyAttribute", "unused attribute 0x" + Integer.toHexString(attr) + "   " + mAttrMap.get(attr));
                        break;
                    case 4:
                        float unused3 = c.mRotation = a.getFloat(attr, c.mRotation);
                        break;
                    case 5:
                        float unused4 = c.mRotationX = a.getFloat(attr, c.mRotationX);
                        break;
                    case 6:
                        float unused5 = c.mRotationY = a.getFloat(attr, c.mRotationY);
                        break;
                    case 7:
                        float unused6 = c.mScaleX = a.getFloat(attr, c.mScaleX);
                        break;
                    case 8:
                        float unused7 = c.mTransitionPathRotate = a.getFloat(attr, c.mTransitionPathRotate);
                        break;
                    case 9:
                        String unused8 = c.mTransitionEasing = a.getString(attr);
                        break;
                    case 10:
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
                    case 12:
                        c.mFramePosition = a.getInt(attr, c.mFramePosition);
                        break;
                    case 13:
                        int unused9 = c.mCurveFit = a.getInteger(attr, c.mCurveFit);
                        break;
                    case 14:
                        float unused10 = c.mScaleY = a.getFloat(attr, c.mScaleY);
                        break;
                    case 15:
                        float unused11 = c.mTranslationX = a.getDimension(attr, c.mTranslationX);
                        break;
                    case 16:
                        float unused12 = c.mTranslationY = a.getDimension(attr, c.mTranslationY);
                        break;
                    case 17:
                        if (Build.VERSION.SDK_INT < 21) {
                            break;
                        } else {
                            float unused13 = c.mTranslationZ = a.getDimension(attr, c.mTranslationZ);
                            break;
                        }
                    case 18:
                        float unused14 = c.mProgress = a.getFloat(attr, c.mProgress);
                        break;
                    case 19:
                        float unused15 = c.mPivotX = a.getDimension(attr, c.mPivotX);
                        break;
                    case 20:
                        float unused16 = c.mPivotY = a.getDimension(attr, c.mPivotY);
                        break;
                }
            }
        }
    }
}
