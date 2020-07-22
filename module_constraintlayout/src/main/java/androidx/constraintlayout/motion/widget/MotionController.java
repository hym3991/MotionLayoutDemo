package androidx.constraintlayout.motion.widget;

import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.motion.utils.CurveFit;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.constraintlayout.motion.utils.VelocityMatrix;
import androidx.constraintlayout.motion.widget.KeyCycleOscillator;
import androidx.constraintlayout.motion.widget.SplineSet;
import androidx.constraintlayout.motion.widget.TimeCycleSplineSet;
import androidx.constraintlayout.solver.widgets.ConstraintWidget;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MotionController {
    private static final boolean DEBUG = false;
    public static final int DRAW_PATH_AS_CONFIGURED = 4;
    public static final int DRAW_PATH_BASIC = 1;
    public static final int DRAW_PATH_CARTESIAN = 3;
    public static final int DRAW_PATH_NONE = 0;
    public static final int DRAW_PATH_RECTANGLE = 5;
    public static final int DRAW_PATH_RELATIVE = 2;
    public static final int DRAW_PATH_SCREEN = 6;
    private static final boolean FAVOR_FIXED_SIZE_VIEWS = false;
    public static final int HORIZONTAL_PATH_X = 2;
    public static final int HORIZONTAL_PATH_Y = 3;
    public static final int PATH_PERCENT = 0;
    public static final int PATH_PERPENDICULAR = 1;
    private static final String TAG = "MotionController";
    public static final int VERTICAL_PATH_X = 4;
    public static final int VERTICAL_PATH_Y = 5;
    private int MAX_DIMENSION = 4;
    String[] attributeTable;
    private CurveFit mArcSpline;
    private int[] mAttributeInterpCount;
    private String[] mAttributeNames;
    private HashMap<String, SplineSet> mAttributesMap;
    String mConstraintTag;
    private int mCurveFitType = -1;
    private HashMap<String, KeyCycleOscillator> mCycleMap;
    private MotionPaths mEndMotionPath = new MotionPaths();
    private MotionConstrainedPoint mEndPoint = new MotionConstrainedPoint();
    int mId;
    private double[] mInterpolateData;
    private int[] mInterpolateVariables;
    private double[] mInterpolateVelocity;
    private ArrayList<Key> mKeyList = new ArrayList<>();
    private KeyTrigger[] mKeyTriggers;
    private ArrayList<MotionPaths> mMotionPaths = new ArrayList<>();
    float mMotionStagger = Float.NaN;
    private int mPathMotionArc = Key.UNSET;
    private CurveFit[] mSpline;
    float mStaggerOffset = 0.0f;
    float mStaggerScale = 1.0f;
    private MotionPaths mStartMotionPath = new MotionPaths();
    private MotionConstrainedPoint mStartPoint = new MotionConstrainedPoint();
    private HashMap<String, TimeCycleSplineSet> mTimeCycleAttributesMap;
    private float[] mValuesBuff = new float[this.MAX_DIMENSION];
    private float[] mVelocity = new float[1];
    View mView;

    /* access modifiers changed from: package-private */
    public MotionPaths getKeyFrame(int i) {
        return this.mMotionPaths.get(i);
    }

    MotionController(View view) {
        setView(view);
    }

    /* access modifiers changed from: package-private */
    public float getStartX() {
        return this.mStartMotionPath.x;
    }

    /* access modifiers changed from: package-private */
    public float getStartY() {
        return this.mStartMotionPath.y;
    }

    /* access modifiers changed from: package-private */
    public float getFinalX() {
        return this.mEndMotionPath.x;
    }

    /* access modifiers changed from: package-private */
    public float getFinalY() {
        return this.mEndMotionPath.y;
    }

    /* access modifiers changed from: package-private */
    public void buildPath(float[] points, int pointCount) {
        KeyCycleOscillator osc_y;
        float mils = 1.0f / ((float) (pointCount - 1));
        SplineSet trans_x = this.mAttributesMap == null ? null : this.mAttributesMap.get("translationX");
        SplineSet trans_y = this.mAttributesMap == null ? null : this.mAttributesMap.get("translationY");
        KeyCycleOscillator osc_x = this.mCycleMap == null ? null : this.mCycleMap.get("translationX");
        if (this.mCycleMap == null) {
            osc_y = null;
        } else {
            osc_y = this.mCycleMap.get("translationY");
        }
        for (int i = 0; i < pointCount; i++) {
            float position = ((float) i) * mils;
            if (this.mStaggerScale != 1.0f) {
                if (position < this.mStaggerOffset) {
                    position = 0.0f;
                }
                if (position > this.mStaggerOffset && ((double) position) < 1.0d) {
                    position = (position - this.mStaggerOffset) * this.mStaggerScale;
                }
            }
            double p = (double) position;
            Easing easing = this.mStartMotionPath.mKeyFrameEasing;
            float start = 0.0f;
            float end = Float.NaN;
            Iterator<MotionPaths> it = this.mMotionPaths.iterator();
            while (it.hasNext()) {
                MotionPaths frame = it.next();
                if (frame.mKeyFrameEasing != null) {
                    if (frame.time < position) {
                        easing = frame.mKeyFrameEasing;
                        start = frame.time;
                    } else if (Float.isNaN(end)) {
                        end = frame.time;
                    }
                }
            }
            if (easing != null) {
                if (Float.isNaN(end)) {
                    end = 1.0f;
                }
                p = (double) (((end - start) * ((float) easing.get((double) ((position - start) / (end - start))))) + start);
            }
            this.mSpline[0].getPos(p, this.mInterpolateData);
            if (this.mArcSpline != null && this.mInterpolateData.length > 0) {
                this.mArcSpline.getPos(p, this.mInterpolateData);
            }
            this.mStartMotionPath.getCenter(this.mInterpolateVariables, this.mInterpolateData, points, i * 2);
            if (osc_x != null) {
                int i2 = i * 2;
                points[i2] = points[i2] + osc_x.get(position);
            } else if (trans_x != null) {
                int i3 = i * 2;
                points[i3] = points[i3] + trans_x.get(position);
            }
            if (osc_y != null) {
                int i4 = (i * 2) + 1;
                points[i4] = points[i4] + osc_y.get(position);
            } else if (trans_y != null) {
                int i5 = (i * 2) + 1;
                points[i5] = points[i5] + trans_y.get(position);
            }
        }
    }

    private float getPreCycleDistance() {
        float[] points = new float[2];
        float sum = 0.0f;
        float mils = 1.0f / ((float) 99);
        double x = 0.0d;
        double y = 0.0d;
        for (int i = 0; i < 100; i++) {
            float position = ((float) i) * mils;
            double p = (double) position;
            Easing easing = this.mStartMotionPath.mKeyFrameEasing;
            float start = 0.0f;
            float end = Float.NaN;
            Iterator<MotionPaths> it = this.mMotionPaths.iterator();
            while (it.hasNext()) {
                MotionPaths frame = it.next();
                if (frame.mKeyFrameEasing != null) {
                    if (frame.time < position) {
                        easing = frame.mKeyFrameEasing;
                        start = frame.time;
                    } else if (Float.isNaN(end)) {
                        end = frame.time;
                    }
                }
            }
            if (easing != null) {
                if (Float.isNaN(end)) {
                    end = 1.0f;
                }
                p = (double) (((end - start) * ((float) easing.get((double) ((position - start) / (end - start))))) + start);
            }
            this.mSpline[0].getPos(p, this.mInterpolateData);
            this.mStartMotionPath.getCenter(this.mInterpolateVariables, this.mInterpolateData, points, 0);
            if (i > 0) {
                sum = (float) (((double) sum) + Math.hypot(y - ((double) points[1]), x - ((double) points[0])));
            }
            x = (double) points[0];
            y = (double) points[1];
        }
        return sum;
    }

    /* access modifiers changed from: package-private */
    public KeyPositionBase getPositionKeyframe(int layoutWidth, int layoutHeight, float x, float y) {
        RectF start = new RectF();
        start.left = this.mStartMotionPath.x;
        start.top = this.mStartMotionPath.y;
        start.right = start.left + this.mStartMotionPath.width;
        start.bottom = start.top + this.mStartMotionPath.height;
        RectF end = new RectF();
        end.left = this.mEndMotionPath.x;
        end.top = this.mEndMotionPath.y;
        end.right = end.left + this.mEndMotionPath.width;
        end.bottom = end.top + this.mEndMotionPath.height;
        Iterator<Key> it = this.mKeyList.iterator();
        while (it.hasNext()) {
            Key key = it.next();
            if ((key instanceof KeyPositionBase) && ((KeyPositionBase) key).intersects(layoutWidth, layoutHeight, start, end, x, y)) {
                return (KeyPositionBase) key;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public int buildKeyFrames(float[] keyFrames, int[] mode) {
        if (keyFrames == null) {
            return 0;
        }
        int count = 0;
        double[] time = this.mSpline[0].getTimePoints();
        if (mode != null) {
            Iterator<MotionPaths> it = this.mMotionPaths.iterator();
            while (it.hasNext()) {
                mode[count] = it.next().mMode;
                count++;
            }
            count = 0;
        }
        for (double d : time) {
            this.mSpline[0].getPos(d, this.mInterpolateData);
            this.mStartMotionPath.getCenter(this.mInterpolateVariables, this.mInterpolateData, keyFrames, count);
            count += 2;
        }
        return count / 2;
    }

    /* access modifiers changed from: package-private */
    public int getAttributeValues(String attributeType, float[] points, int pointCount) {
        float f = 1.0f / ((float) (pointCount - 1));
        SplineSet spline = this.mAttributesMap.get(attributeType);
        if (spline == null) {
            return -1;
        }
        for (int j = 0; j < points.length; j++) {
            points[j] = spline.get((float) (j / (points.length - 1)));
        }
        return points.length;
    }

    /* access modifiers changed from: package-private */
    public void buildRect(float p, float[] path, int offset) {
        this.mSpline[0].getPos((double) getAdjustedPosition(p, null), this.mInterpolateData);
        this.mStartMotionPath.getRect(this.mInterpolateVariables, this.mInterpolateData, path, offset);
    }

    /* access modifiers changed from: package-private */
    public void buildRectangles(float[] path, int pointCount) {
        float mils = 1.0f / ((float) (pointCount - 1));
        for (int i = 0; i < pointCount; i++) {
            this.mSpline[0].getPos((double) getAdjustedPosition(((float) i) * mils, null), this.mInterpolateData);
            this.mStartMotionPath.getRect(this.mInterpolateVariables, this.mInterpolateData, path, i * 8);
        }
    }

    /* access modifiers changed from: package-private */
    public float getKeyFrameParameter(int type, float x, float y) {
        float dx = this.mEndMotionPath.x - this.mStartMotionPath.x;
        float dy = this.mEndMotionPath.y - this.mStartMotionPath.y;
        float startCenterX = this.mStartMotionPath.x + (this.mStartMotionPath.width / 2.0f);
        float startCenterY = this.mStartMotionPath.y + (this.mStartMotionPath.height / 2.0f);
        float hypot = (float) Math.hypot((double) dx, (double) dy);
        if (((double) hypot) < 1.0E-7d) {
            return Float.NaN;
        }
        float vx = x - startCenterX;
        float vy = y - startCenterY;
        if (((float) Math.hypot((double) vx, (double) vy)) == 0.0f) {
            return 0.0f;
        }
        float pathDistance = (vx * dx) + (vy * dy);
        switch (type) {
            case 0:
                return pathDistance / hypot;
            case 1:
                return (float) Math.sqrt((double) ((hypot * hypot) - (pathDistance * pathDistance)));
            case 2:
                return vx / dx;
            case 3:
                return vy / dx;
            case 4:
                return vx / dy;
            case 5:
                return vy / dy;
            default:
                return 0.0f;
        }
    }

    private void insertKey(MotionPaths point) {
        int pos = Collections.binarySearch(this.mMotionPaths, point);
        if (pos == 0) {
            Log.e(TAG, " KeyPath positon \"" + point.position + "\" outside of range");
        }
        this.mMotionPaths.add((-pos) - 1, point);
    }

    /* access modifiers changed from: package-private */
    public void addKeys(ArrayList<Key> list) {
        this.mKeyList.addAll(list);
    }

    /* access modifiers changed from: package-private */
    public void addKey(Key key) {
        this.mKeyList.add(key);
    }

    public void setPathMotionArc(int arc) {
        this.mPathMotionArc = arc;
    }

    public void setup(int parentWidth, int parentHeight, float transitionDuration, long currentTime) {
        TimeCycleSplineSet splineSets;
        ConstraintAttribute customAttribute;
        SplineSet splineSets2;
        ConstraintAttribute customAttribute2;
        new HashSet();
        HashSet<String> timeCycleAttributes = new HashSet<>();
        HashSet<String> splineAttributes = new HashSet<>();
        HashSet<String> cycleAttributes = new HashSet<>();
        HashMap<String, Integer> interpolation = new HashMap<>();
        ArrayList<KeyTrigger> triggerList = null;
        if (this.mPathMotionArc != Key.UNSET) {
            this.mStartMotionPath.mPathMotionArc = this.mPathMotionArc;
        }
        this.mStartPoint.different(this.mEndPoint, splineAttributes);
        if (this.mKeyList != null) {
            Iterator<Key> it = this.mKeyList.iterator();
            while (it.hasNext()) {
                Key key = it.next();
                if (key instanceof KeyPosition) {
                    KeyPosition keyPath = (KeyPosition) key;
                    insertKey(new MotionPaths(parentWidth, parentHeight, keyPath, this.mStartMotionPath, this.mEndMotionPath));
                    if (keyPath.mCurveFit != Key.UNSET) {
                        this.mCurveFitType = keyPath.mCurveFit;
                    }
                } else if (key instanceof KeyCycle) {
                    key.getAttributeNames(cycleAttributes);
                } else if (key instanceof KeyTimeCycle) {
                    key.getAttributeNames(timeCycleAttributes);
                } else if (key instanceof KeyTrigger) {
                    if (triggerList == null) {
                        triggerList = new ArrayList<>();
                    }
                    triggerList.add((KeyTrigger) key);
                } else {
                    key.setInterpolation(interpolation);
                    key.getAttributeNames(splineAttributes);
                }
            }
        }
        if (triggerList != null) {
            this.mKeyTriggers = (KeyTrigger[]) triggerList.toArray(new KeyTrigger[0]);
        }
        if (!splineAttributes.isEmpty()) {
            this.mAttributesMap = new HashMap<>();
            Iterator<String> it2 = splineAttributes.iterator();
            while (it2.hasNext()) {
                String attribute = it2.next();
                if (attribute.startsWith("CUSTOM,")) {
                    SparseArray<ConstraintAttribute> attrList = new SparseArray<>();
                    String customAttributeName = attribute.split(",")[1];
                    Iterator<Key> it3 = this.mKeyList.iterator();
                    while (it3.hasNext()) {
                        Key key2 = it3.next();
                        if (!(key2.mCustomConstraints == null || (customAttribute2 = key2.mCustomConstraints.get(customAttributeName)) == null)) {
                            attrList.append(key2.mFramePosition, customAttribute2);
                        }
                    }
                    splineSets2 = SplineSet.makeCustomSpline(attribute, attrList);
                } else {
                    splineSets2 = SplineSet.makeSpline(attribute);
                }
                if (splineSets2 != null) {
                    splineSets2.setType(attribute);
                    this.mAttributesMap.put(attribute, splineSets2);
                }
            }
            if (this.mKeyList != null) {
                Iterator<Key> it4 = this.mKeyList.iterator();
                while (it4.hasNext()) {
                    Key key3 = it4.next();
                    if (key3 instanceof KeyAttributes) {
                        key3.addValues(this.mAttributesMap);
                    }
                }
            }
            this.mStartPoint.addValues(this.mAttributesMap, 0);
            this.mEndPoint.addValues(this.mAttributesMap, 100);
            for (String spline : this.mAttributesMap.keySet()) {
                int curve = 0;
                if (interpolation.containsKey(spline)) {
                    curve = interpolation.get(spline).intValue();
                }
                this.mAttributesMap.get(spline).setup(curve);
            }
        }
        if (!timeCycleAttributes.isEmpty()) {
            if (this.mTimeCycleAttributesMap == null) {
                this.mTimeCycleAttributesMap = new HashMap<>();
            }
            Iterator<String> it5 = timeCycleAttributes.iterator();
            while (it5.hasNext()) {
                String attribute2 = it5.next();
                if (!this.mTimeCycleAttributesMap.containsKey(attribute2)) {
                    if (attribute2.startsWith("CUSTOM,")) {
                        SparseArray<ConstraintAttribute> attrList2 = new SparseArray<>();
                        String customAttributeName2 = attribute2.split(",")[1];
                        Iterator<Key> it6 = this.mKeyList.iterator();
                        while (it6.hasNext()) {
                            Key key4 = it6.next();
                            if (!(key4.mCustomConstraints == null || (customAttribute = key4.mCustomConstraints.get(customAttributeName2)) == null)) {
                                attrList2.append(key4.mFramePosition, customAttribute);
                            }
                        }
                        splineSets = TimeCycleSplineSet.makeCustomSpline(attribute2, attrList2);
                    } else {
                        splineSets = TimeCycleSplineSet.makeSpline(attribute2, currentTime);
                    }
                    if (splineSets != null) {
                        splineSets.setType(attribute2);
                        this.mTimeCycleAttributesMap.put(attribute2, splineSets);
                    }
                }
            }
            if (this.mKeyList != null) {
                Iterator<Key> it7 = this.mKeyList.iterator();
                while (it7.hasNext()) {
                    Key key5 = it7.next();
                    if (key5 instanceof KeyTimeCycle) {
                        ((KeyTimeCycle) key5).addTimeValues(this.mTimeCycleAttributesMap);
                    }
                }
            }
            for (String spline2 : this.mTimeCycleAttributesMap.keySet()) {
                int curve2 = 0;
                if (interpolation.containsKey(spline2)) {
                    curve2 = interpolation.get(spline2).intValue();
                }
                this.mTimeCycleAttributesMap.get(spline2).setup(curve2);
            }
        }
        MotionPaths[] points = new MotionPaths[(this.mMotionPaths.size() + 2)];
        int count = 1;
        points[0] = this.mStartMotionPath;
        points[points.length - 1] = this.mEndMotionPath;
        if (this.mMotionPaths.size() > 0 && this.mCurveFitType == -1) {
            this.mCurveFitType = 0;
        }
        Iterator<MotionPaths> it8 = this.mMotionPaths.iterator();
        while (it8.hasNext()) {
            points[count] = it8.next();
            count++;
        }
        HashSet<String> attributeNameSet = new HashSet<>();
        for (String s : this.mEndMotionPath.attributes.keySet()) {
            if (this.mStartMotionPath.attributes.containsKey(s) && !splineAttributes.contains("CUSTOM," + s)) {
                attributeNameSet.add(s);
            }
        }
        this.mAttributeNames = (String[]) attributeNameSet.toArray(new String[0]);
        this.mAttributeInterpCount = new int[this.mAttributeNames.length];
        for (int i = 0; i < this.mAttributeNames.length; i++) {
            String attributeName = this.mAttributeNames[i];
            this.mAttributeInterpCount[i] = 0;
            int j = 0;
            while (true) {
                if (j >= points.length) {
                    break;
                } else if (points[j].attributes.containsKey(attributeName)) {
                    int[] iArr = this.mAttributeInterpCount;
                    iArr[i] = points[j].attributes.get(attributeName).noOfInterpValues() + iArr[i];
                    break;
                } else {
                    j++;
                }
            }
        }
        boolean arcMode = points[0].mPathMotionArc != Key.UNSET;
        boolean[] mask = new boolean[(this.mAttributeNames.length + 18)];
        for (int i2 = 1; i2 < points.length; i2++) {
            points[i2].different(points[i2 - 1], mask, this.mAttributeNames, arcMode);
        }
        int count2 = 0;
        for (int i3 = 1; i3 < mask.length; i3++) {
            if (mask[i3]) {
                count2++;
            }
        }
        this.mInterpolateVariables = new int[count2];
        this.mInterpolateData = new double[this.mInterpolateVariables.length];
        this.mInterpolateVelocity = new double[this.mInterpolateVariables.length];
        int count3 = 0;
        for (int i4 = 1; i4 < mask.length; i4++) {
            if (mask[i4]) {
                this.mInterpolateVariables[count3] = i4;
                count3++;
            }
        }
        double[][] splineData = (double[][]) Array.newInstance(Double.TYPE, points.length, this.mInterpolateVariables.length);
        double[] timePoint = new double[points.length];
        for (int i5 = 0; i5 < points.length; i5++) {
            points[i5].fillStandard(splineData[i5], this.mInterpolateVariables);
            timePoint[i5] = (double) points[i5].time;
        }
        for (int j2 = 0; j2 < this.mInterpolateVariables.length; j2++) {
            if (this.mInterpolateVariables[j2] < MotionPaths.names.length) {
                String s2 = MotionPaths.names[this.mInterpolateVariables[j2]] + " [";
                for (int i6 = 0; i6 < points.length; i6++) {
                    s2 = s2 + splineData[i6][j2];
                }
            }
        }
        this.mSpline = new CurveFit[(this.mAttributeNames.length + 1)];
        for (int i7 = 0; i7 < this.mAttributeNames.length; i7++) {
            int pointCount = 0;
            double[][] splinePoints = null;
            double[] timePoints = null;
            String name = this.mAttributeNames[i7];
            for (int j3 = 0; j3 < points.length; j3++) {
                if (points[j3].hasCustomData(name)) {
                    if (splinePoints == null) {
                        timePoints = new double[points.length];
                        splinePoints = (double[][]) Array.newInstance(Double.TYPE, points.length, points[j3].getCustomDataCount(name));
                    }
                    timePoints[pointCount] = (double) points[j3].time;
                    points[j3].getCustomData(name, splinePoints[pointCount], 0);
                    pointCount++;
                }
            }
            this.mSpline[i7 + 1] = CurveFit.get(this.mCurveFitType, Arrays.copyOf(timePoints, pointCount), (double[][]) Arrays.copyOf(splinePoints, pointCount));
        }
        this.mSpline[0] = CurveFit.get(this.mCurveFitType, timePoint, splineData);
        if (points[0].mPathMotionArc != Key.UNSET) {
            int size = points.length;
            int[] mode = new int[size];
            double[] time = new double[size];
            double[][] values = (double[][]) Array.newInstance(Double.TYPE, size, 2);
            for (int i8 = 0; i8 < size; i8++) {
                mode[i8] = points[i8].mPathMotionArc;
                time[i8] = (double) points[i8].time;
                values[i8][0] = (double) points[i8].x;
                values[i8][1] = (double) points[i8].y;
            }
            this.mArcSpline = CurveFit.getArc(mode, time, values);
        }
        float distance = Float.NaN;
        this.mCycleMap = new HashMap<>();
        if (this.mKeyList != null) {
            Iterator<String> it9 = cycleAttributes.iterator();
            while (it9.hasNext()) {
                String attribute3 = it9.next();
                KeyCycleOscillator cycle = KeyCycleOscillator.makeSpline(attribute3);
                if (cycle != null) {
                    if (cycle.variesByPath() && Float.isNaN(distance)) {
                        distance = getPreCycleDistance();
                    }
                    cycle.setType(attribute3);
                    this.mCycleMap.put(attribute3, cycle);
                }
            }
            Iterator<Key> it10 = this.mKeyList.iterator();
            while (it10.hasNext()) {
                Key key6 = it10.next();
                if (key6 instanceof KeyCycle) {
                    ((KeyCycle) key6).addCycleValues(this.mCycleMap);
                }
            }
            for (KeyCycleOscillator cycle2 : this.mCycleMap.values()) {
                cycle2.setup(distance);
            }
        }
    }

    public String toString() {
        return " start: x: " + this.mStartMotionPath.x + " y: " + this.mStartMotionPath.y + " end: x: " + this.mEndMotionPath.x + " y: " + this.mEndMotionPath.y;
    }

    private void readView(MotionPaths motionPaths) {
        motionPaths.setBounds((float) ((int) this.mView.getX()), (float) ((int) this.mView.getY()), (float) this.mView.getWidth(), (float) this.mView.getHeight());
    }

    public void setView(View view) {
        this.mView = view;
        this.mId = view.getId();
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof ConstraintLayout.LayoutParams) {
            this.mConstraintTag = ((ConstraintLayout.LayoutParams) lp).getConstraintTag();
        }
    }

    /* access modifiers changed from: package-private */
    public void setStartCurrentState(View v) {
        this.mStartMotionPath.time = 0.0f;
        this.mStartMotionPath.position = 0.0f;
        this.mStartMotionPath.setBounds(v.getX(), v.getY(), (float) v.getWidth(), (float) v.getHeight());
        this.mStartPoint.setState(v);
    }

    /* access modifiers changed from: package-private */
    public void setStartState(ConstraintWidget cw, ConstraintSet constraintSet) {
        this.mStartMotionPath.time = 0.0f;
        this.mStartMotionPath.position = 0.0f;
        readView(this.mStartMotionPath);
        this.mStartMotionPath.setBounds((float) cw.getX(), (float) cw.getY(), (float) cw.getWidth(), (float) cw.getHeight());
        ConstraintSet.Constraint constraint = constraintSet.getParameters(this.mId);
        this.mStartMotionPath.applyParameters(constraint);
        this.mMotionStagger = constraint.motion.mMotionStagger;
        this.mStartPoint.setState(cw, constraintSet, this.mId);
    }

    /* access modifiers changed from: package-private */
    public void setEndState(ConstraintWidget cw, ConstraintSet constraintSet) {
        this.mEndMotionPath.time = 1.0f;
        this.mEndMotionPath.position = 1.0f;
        readView(this.mEndMotionPath);
        this.mEndMotionPath.setBounds((float) cw.getX(), (float) cw.getY(), (float) cw.getWidth(), (float) cw.getHeight());
        this.mEndMotionPath.applyParameters(constraintSet.getParameters(this.mId));
        this.mEndPoint.setState(cw, constraintSet, this.mId);
    }

    private float getAdjustedPosition(float position, float[] velocity) {
        if (velocity != null) {
            velocity[0] = 1.0f;
        } else if (((double) this.mStaggerScale) != 1.0d) {
            if (position < this.mStaggerOffset) {
                position = 0.0f;
            }
            if (position > this.mStaggerOffset && ((double) position) < 1.0d) {
                position = (position - this.mStaggerOffset) * this.mStaggerScale;
            }
        }
        float adjusted = position;
        Easing easing = this.mStartMotionPath.mKeyFrameEasing;
        float start = 0.0f;
        float end = Float.NaN;
        Iterator<MotionPaths> it = this.mMotionPaths.iterator();
        while (it.hasNext()) {
            MotionPaths frame = it.next();
            if (frame.mKeyFrameEasing != null) {
                if (frame.time < position) {
                    easing = frame.mKeyFrameEasing;
                    start = frame.time;
                } else if (Float.isNaN(end)) {
                    end = frame.time;
                }
            }
        }
        if (easing != null) {
            if (Float.isNaN(end)) {
                end = 1.0f;
            }
            float offset = (position - start) / (end - start);
            adjusted = ((end - start) * ((float) easing.get((double) offset))) + start;
            if (velocity != null) {
                velocity[0] = (float) easing.getDiff((double) offset);
            }
        }
        return adjusted;
    }

    /* access modifiers changed from: package-private */
    public boolean interpolate(View child, float global_position, long time, KeyCache keyCache) {
        boolean timeAnimation = false;
        float position = getAdjustedPosition(global_position, null);
        TimeCycleSplineSet.PathRotate timePathRotate = null;
        if (this.mAttributesMap != null) {
            for (SplineSet aSpline : this.mAttributesMap.values()) {
                aSpline.setProperty(child, position);
            }
        }
        if (this.mTimeCycleAttributesMap != null) {
            for (TimeCycleSplineSet aSpline2 : this.mTimeCycleAttributesMap.values()) {
                if (aSpline2 instanceof TimeCycleSplineSet.PathRotate) {
                    timePathRotate = (TimeCycleSplineSet.PathRotate) aSpline2;
                } else {
                    timeAnimation |= aSpline2.setProperty(child, position, time, keyCache);
                }
            }
        }
        if (this.mSpline != null) {
            this.mSpline[0].getPos((double) position, this.mInterpolateData);
            this.mSpline[0].getSlope((double) position, this.mInterpolateVelocity);
            if (this.mArcSpline != null && this.mInterpolateData.length > 0) {
                this.mArcSpline.getPos((double) position, this.mInterpolateData);
                this.mArcSpline.getSlope((double) position, this.mInterpolateVelocity);
            }
            this.mStartMotionPath.setView(child, this.mInterpolateVariables, this.mInterpolateData, this.mInterpolateVelocity, null);
            if (this.mAttributesMap != null) {
                for (SplineSet aSpline3 : this.mAttributesMap.values()) {
                    if (aSpline3 instanceof SplineSet.PathRotate) {
                        ((SplineSet.PathRotate) aSpline3).setPathRotate(child, position, this.mInterpolateVelocity[0], this.mInterpolateVelocity[1]);
                    }
                }
            }
            if (timePathRotate != null) {
                timeAnimation |= timePathRotate.setPathRotate(child, keyCache, position, time, this.mInterpolateVelocity[0], this.mInterpolateVelocity[1]);
            }
            for (int i = 1; i < this.mSpline.length; i++) {
                this.mSpline[i].getPos((double) position, this.mValuesBuff);
                this.mStartMotionPath.attributes.get(this.mAttributeNames[i - 1]).setInterpolatedValue(child, this.mValuesBuff);
            }
            if (this.mStartPoint.mVisibilityMode == 0) {
                if (position <= 0.0f) {
                    child.setVisibility(this.mStartPoint.visibility);
                } else if (position >= 1.0f) {
                    child.setVisibility(this.mEndPoint.visibility);
                } else if (this.mEndPoint.visibility != this.mStartPoint.visibility) {
                    child.setVisibility(0);
                }
            }
            if (this.mKeyTriggers != null) {
                for (int i2 = 0; i2 < this.mKeyTriggers.length; i2++) {
                    this.mKeyTriggers[i2].conditionallyFire(position, child);
                }
            }
        } else {
            float float_l = this.mStartMotionPath.x + ((this.mEndMotionPath.x - this.mStartMotionPath.x) * position);
            float float_t = this.mStartMotionPath.y + ((this.mEndMotionPath.y - this.mStartMotionPath.y) * position);
            float float_width = this.mStartMotionPath.width + ((this.mEndMotionPath.width - this.mStartMotionPath.width) * position);
            int l = (int) (0.5f + float_l);
            int t = (int) (0.5f + float_t);
            int r = (int) (0.5f + float_l + float_width);
            int b = (int) (0.5f + float_t + this.mStartMotionPath.height + ((this.mEndMotionPath.height - this.mStartMotionPath.height) * position));
            int width = r - l;
            int height = b - t;
            if (!(this.mEndMotionPath.width == this.mStartMotionPath.width && this.mEndMotionPath.height == this.mStartMotionPath.height)) {
                child.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
            }
            child.layout(l, t, r, b);
        }
        if (this.mCycleMap != null) {
            for (KeyCycleOscillator osc : this.mCycleMap.values()) {
                if (osc instanceof KeyCycleOscillator.PathRotateSet) {
                    ((KeyCycleOscillator.PathRotateSet) osc).setPathRotate(child, position, this.mInterpolateVelocity[0], this.mInterpolateVelocity[1]);
                } else {
                    osc.setProperty(child, position);
                }
            }
        }
        return timeAnimation;
    }

    /* access modifiers changed from: package-private */
    public void getDpDt(float position, float locationX, float locationY, float[] mAnchorDpDt) {
        float position2 = getAdjustedPosition(position, this.mVelocity);
        if (this.mSpline != null) {
            this.mSpline[0].getSlope((double) position2, this.mInterpolateVelocity);
            this.mSpline[0].getPos((double) position2, this.mInterpolateData);
            float v = this.mVelocity[0];
            for (int i = 0; i < this.mInterpolateVelocity.length; i++) {
                double[] dArr = this.mInterpolateVelocity;
                dArr[i] = dArr[i] * ((double) v);
            }
            if (this.mArcSpline == null) {
                this.mStartMotionPath.setDpDt(locationX, locationY, mAnchorDpDt, this.mInterpolateVariables, this.mInterpolateVelocity, this.mInterpolateData);
            } else if (this.mInterpolateData.length > 0) {
                this.mArcSpline.getPos((double) position2, this.mInterpolateData);
                this.mArcSpline.getSlope((double) position2, this.mInterpolateVelocity);
                this.mStartMotionPath.setDpDt(locationX, locationY, mAnchorDpDt, this.mInterpolateVariables, this.mInterpolateVelocity, this.mInterpolateData);
            }
        } else {
            float dleft = this.mEndMotionPath.x - this.mStartMotionPath.x;
            float dTop = this.mEndMotionPath.y - this.mStartMotionPath.y;
            mAnchorDpDt[0] = ((1.0f - locationX) * dleft) + ((dleft + (this.mEndMotionPath.width - this.mStartMotionPath.width)) * locationX);
            mAnchorDpDt[1] = ((1.0f - locationY) * dTop) + ((dTop + (this.mEndMotionPath.height - this.mStartMotionPath.height)) * locationY);
        }
    }

    /* access modifiers changed from: package-private */
    public void getPostLayoutDvDp(float position, int width, int height, float locationX, float locationY, float[] mAnchorDpDt) {
        KeyCycleOscillator osc_sy;
        float position2 = getAdjustedPosition(position, this.mVelocity);
        SplineSet trans_x = this.mAttributesMap == null ? null : this.mAttributesMap.get("translationX");
        SplineSet trans_y = this.mAttributesMap == null ? null : this.mAttributesMap.get("translationY");
        SplineSet rotation = this.mAttributesMap == null ? null : this.mAttributesMap.get("rotation");
        SplineSet scale_x = this.mAttributesMap == null ? null : this.mAttributesMap.get("scaleX");
        SplineSet scale_y = this.mAttributesMap == null ? null : this.mAttributesMap.get("scaleY");
        KeyCycleOscillator osc_x = this.mCycleMap == null ? null : this.mCycleMap.get("translationX");
        KeyCycleOscillator osc_y = this.mCycleMap == null ? null : this.mCycleMap.get("translationY");
        KeyCycleOscillator osc_r = this.mCycleMap == null ? null : this.mCycleMap.get("rotation");
        KeyCycleOscillator osc_sx = this.mCycleMap == null ? null : this.mCycleMap.get("scaleX");
        if (this.mCycleMap == null) {
            osc_sy = null;
        } else {
            osc_sy = this.mCycleMap.get("scaleY");
        }
        VelocityMatrix vmat = new VelocityMatrix();
        vmat.clear();
        vmat.setRotationVelocity(rotation, position2);
        vmat.setTranslationVelocity(trans_x, trans_y, position2);
        vmat.setScaleVelocity(scale_x, scale_y, position2);
        vmat.setRotationVelocity(osc_r, position2);
        vmat.setTranslationVelocity(osc_x, osc_y, position2);
        vmat.setScaleVelocity(osc_sx, osc_sy, position2);
        if (this.mArcSpline != null) {
            if (this.mInterpolateData.length > 0) {
                this.mArcSpline.getPos((double) position2, this.mInterpolateData);
                this.mArcSpline.getSlope((double) position2, this.mInterpolateVelocity);
                this.mStartMotionPath.setDpDt(locationX, locationY, mAnchorDpDt, this.mInterpolateVariables, this.mInterpolateVelocity, this.mInterpolateData);
            }
            vmat.applyTransform(locationX, locationY, width, height, mAnchorDpDt);
        } else if (this.mSpline != null) {
            float position3 = getAdjustedPosition(position2, this.mVelocity);
            this.mSpline[0].getSlope((double) position3, this.mInterpolateVelocity);
            this.mSpline[0].getPos((double) position3, this.mInterpolateData);
            float v = this.mVelocity[0];
            for (int i = 0; i < this.mInterpolateVelocity.length; i++) {
                double[] dArr = this.mInterpolateVelocity;
                dArr[i] = dArr[i] * ((double) v);
            }
            this.mStartMotionPath.setDpDt(locationX, locationY, mAnchorDpDt, this.mInterpolateVariables, this.mInterpolateVelocity, this.mInterpolateData);
            vmat.applyTransform(locationX, locationY, width, height, mAnchorDpDt);
        } else {
            float dleft = this.mEndMotionPath.x - this.mStartMotionPath.x;
            float dTop = this.mEndMotionPath.y - this.mStartMotionPath.y;
            mAnchorDpDt[0] = ((1.0f - locationX) * dleft) + ((dleft + (this.mEndMotionPath.width - this.mStartMotionPath.width)) * locationX);
            mAnchorDpDt[1] = ((1.0f - locationY) * dTop) + ((dTop + (this.mEndMotionPath.height - this.mStartMotionPath.height)) * locationY);
            vmat.clear();
            vmat.setRotationVelocity(rotation, position2);
            vmat.setTranslationVelocity(trans_x, trans_y, position2);
            vmat.setScaleVelocity(scale_x, scale_y, position2);
            vmat.setRotationVelocity(osc_r, position2);
            vmat.setTranslationVelocity(osc_x, osc_y, position2);
            vmat.setScaleVelocity(osc_sx, osc_sy, position2);
            vmat.applyTransform(locationX, locationY, width, height, mAnchorDpDt);
        }
    }

    public int getDrawPath() {
        int mode = this.mStartMotionPath.mDrawPath;
        Iterator<MotionPaths> it = this.mMotionPaths.iterator();
        while (it.hasNext()) {
            mode = Math.max(mode, it.next().mDrawPath);
        }
        return Math.max(mode, this.mEndMotionPath.mDrawPath);
    }

    public void setDrawPath(int debugMode) {
        this.mStartMotionPath.mDrawPath = debugMode;
    }

    /* access modifiers changed from: package-private */
    public String name() {
        return this.mView.getContext().getResources().getResourceEntryName(this.mView.getId());
    }

    /* access modifiers changed from: package-private */
    public void positionKeyframe(View view, KeyPositionBase key, float x, float y, String[] attribute, float[] value) {
        RectF start = new RectF();
        start.left = this.mStartMotionPath.x;
        start.top = this.mStartMotionPath.y;
        start.right = start.left + this.mStartMotionPath.width;
        start.bottom = start.top + this.mStartMotionPath.height;
        RectF end = new RectF();
        end.left = this.mEndMotionPath.x;
        end.top = this.mEndMotionPath.y;
        end.right = end.left + this.mEndMotionPath.width;
        end.bottom = end.top + this.mEndMotionPath.height;
        key.positionAttributes(view, start, end, x, y, attribute, value);
    }

    public int getkeyFramePositions(int[] type, float[] pos) {
        int i = 0;
        int count = 0;
        Iterator<Key> it = this.mKeyList.iterator();
        while (it.hasNext()) {
            Key key = it.next();
            type[i] = key.mFramePosition + (key.mType * 1000);
            this.mSpline[0].getPos((double) (((float) key.mFramePosition) / 100.0f), this.mInterpolateData);
            this.mStartMotionPath.getCenter(this.mInterpolateVariables, this.mInterpolateData, pos, count);
            count += 2;
            i++;
        }
        return i;
    }

    public int getKeyFrameInfo(int type, int[] info) {
        int count = 0;
        int cursor = 0;
        float[] pos = new float[2];
        Iterator<Key> it = this.mKeyList.iterator();
        while (it.hasNext()) {
            Key key = it.next();
            if (key.mType == type || type != -1) {
                info[cursor] = 0;
                int cursor2 = cursor + 1;
                info[cursor2] = key.mType;
                int cursor3 = cursor2 + 1;
                info[cursor3] = key.mFramePosition;
                this.mSpline[0].getPos((double) (((float) key.mFramePosition) / 100.0f), this.mInterpolateData);
                this.mStartMotionPath.getCenter(this.mInterpolateVariables, this.mInterpolateData, pos, 0);
                int cursor4 = cursor3 + 1;
                info[cursor4] = Float.floatToIntBits(pos[0]);
                int cursor5 = cursor4 + 1;
                info[cursor5] = Float.floatToIntBits(pos[1]);
                if (key instanceof KeyPosition) {
                    KeyPosition kp = (KeyPosition) key;
                    int cursor6 = cursor5 + 1;
                    info[cursor6] = kp.mPositionType;
                    int cursor7 = cursor6 + 1;
                    info[cursor7] = Float.floatToIntBits(kp.mPercentX);
                    cursor5 = cursor7 + 1;
                    info[cursor5] = Float.floatToIntBits(kp.mPercentY);
                }
                cursor = cursor5 + 1;
                info[cursor] = cursor - cursor;
                count++;
            }
        }
        return count;
    }
}
