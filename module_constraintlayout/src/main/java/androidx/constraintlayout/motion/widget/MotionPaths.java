package androidx.constraintlayout.motion.widget;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.utils.Easing;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.ConstraintSet;
import java.util.Arrays;
import java.util.LinkedHashMap;

class MotionPaths implements Comparable<MotionPaths> {
    static final int CARTESIAN = 2;
    public static final boolean DEBUG = false;
    static final int OFF_HEIGHT = 4;
    static final int OFF_PATH_ROTATE = 5;
    static final int OFF_POSITION = 0;
    static final int OFF_WIDTH = 3;
    static final int OFF_X = 1;
    static final int OFF_Y = 2;
    public static final boolean OLD_WAY = false;
    static final int PERPENDICULAR = 1;
    static final int SCREEN = 3;
    public static final String TAG = "MotionPaths";
    static String[] names = {"position", "x", "y", "width", "height", "pathRotate"};
    LinkedHashMap<String, ConstraintAttribute> attributes;
    float height;
    int mDrawPath;
    Easing mKeyFrameEasing;
    int mLastMeasuredHeight;
    int mLastMeasuredWidth;
    int mMode;
    int mPathMotionArc;
    float mPathRotate;
    float mProgress;
    double[] mTempDelta;
    double[] mTempValue;
    float position;
    float time;
    float width;
    float x;
    float y;

    public MotionPaths() {
        this.mDrawPath = 0;
        this.mPathRotate = Float.NaN;
        this.mProgress = Float.NaN;
        this.mPathMotionArc = Key.UNSET;
        this.mLastMeasuredWidth = 0;
        this.mLastMeasuredHeight = 0;
        this.attributes = new LinkedHashMap<>();
        this.mMode = 0;
        this.mTempValue = new double[18];
        this.mTempDelta = new double[18];
    }

    /* access modifiers changed from: package-private */
    public void initCartesian(KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        float scaleHeight;
        float position2 = ((float) c.mFramePosition) / 100.0f;
        this.time = position2;
        this.mDrawPath = c.mDrawPath;
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position2 : c.mPercentWidth;
        if (Float.isNaN(c.mPercentHeight)) {
            scaleHeight = position2;
        } else {
            scaleHeight = c.mPercentHeight;
        }
        float scaleX = endTimePoint.width - startTimePoint.width;
        float scaleY = endTimePoint.height - startTimePoint.height;
        this.position = this.time;
        float startCenterX = startTimePoint.x + (startTimePoint.width / 2.0f);
        float startCenterY = startTimePoint.y + (startTimePoint.height / 2.0f);
        float pathVectorX = (endTimePoint.x + (endTimePoint.width / 2.0f)) - startCenterX;
        float pathVectorY = (endTimePoint.y + (endTimePoint.height / 2.0f)) - startCenterY;
        this.x = (float) ((int) ((startTimePoint.x + (pathVectorX * position2)) - ((scaleX * scaleWidth) / 2.0f)));
        this.y = (float) ((int) ((startTimePoint.y + (pathVectorY * position2)) - ((scaleY * scaleHeight) / 2.0f)));
        this.width = (float) ((int) (startTimePoint.width + (scaleX * scaleWidth)));
        this.height = (float) ((int) (startTimePoint.height + (scaleY * scaleHeight)));
        float dxdx = Float.isNaN(c.mPercentX) ? position2 : c.mPercentX;
        float dydx = Float.isNaN(c.mAltPercentY) ? 0.0f : c.mAltPercentY;
        float dydy = Float.isNaN(c.mPercentY) ? position2 : c.mPercentY;
        float dxdy = Float.isNaN(c.mAltPercentX) ? 0.0f : c.mAltPercentX;
        this.mMode = 2;
        this.x = (float) ((int) (((startTimePoint.x + (pathVectorX * dxdx)) + (pathVectorY * dxdy)) - ((scaleX * scaleWidth) / 2.0f)));
        this.y = (float) ((int) (((startTimePoint.y + (pathVectorX * dydx)) + (pathVectorY * dydy)) - ((scaleY * scaleHeight) / 2.0f)));
        this.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        this.mPathMotionArc = c.mPathMotionArc;
    }

    public MotionPaths(int parentWidth, int parentHeight, KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        this.mDrawPath = 0;
        this.mPathRotate = Float.NaN;
        this.mProgress = Float.NaN;
        this.mPathMotionArc = Key.UNSET;
        this.mLastMeasuredWidth = 0;
        this.mLastMeasuredHeight = 0;
        this.attributes = new LinkedHashMap<>();
        this.mMode = 0;
        this.mTempValue = new double[18];
        this.mTempDelta = new double[18];
        this.mLastMeasuredHeight = endTimePoint.mLastMeasuredHeight;
        this.mLastMeasuredWidth = endTimePoint.mLastMeasuredWidth;
        switch (c.mPositionType) {
            case 1:
                initPath(c, startTimePoint, endTimePoint);
                return;
            case 2:
                initScreen(parentWidth, parentHeight, c, startTimePoint, endTimePoint);
                return;
            default:
                initCartesian(c, startTimePoint, endTimePoint);
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public void initScreen(int parentWidth, int parentHeight, KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        float position2 = ((float) c.mFramePosition) / 100.0f;
        this.time = position2;
        this.mDrawPath = c.mDrawPath;
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position2 : c.mPercentWidth;
        float scaleHeight = Float.isNaN(c.mPercentHeight) ? position2 : c.mPercentHeight;
        float scaleX = endTimePoint.width - startTimePoint.width;
        float scaleY = endTimePoint.height - startTimePoint.height;
        this.position = this.time;
        float startCenterX = startTimePoint.x + (startTimePoint.width / 2.0f);
        float startCenterY = startTimePoint.y + (startTimePoint.height / 2.0f);
        this.x = (float) ((int) ((startTimePoint.x + (((endTimePoint.x + (endTimePoint.width / 2.0f)) - startCenterX) * position2)) - ((scaleX * scaleWidth) / 2.0f)));
        this.y = (float) ((int) ((startTimePoint.y + (((endTimePoint.y + (endTimePoint.height / 2.0f)) - startCenterY) * position2)) - ((scaleY * scaleHeight) / 2.0f)));
        this.width = (float) ((int) (startTimePoint.width + (scaleX * scaleWidth)));
        this.height = (float) ((int) (startTimePoint.height + (scaleY * scaleHeight)));
        this.mMode = 3;
        if (!Float.isNaN(c.mPercentX)) {
            this.x = (float) ((int) (c.mPercentX * ((float) ((int) (((float) parentWidth) - this.width)))));
        }
        if (!Float.isNaN(c.mPercentY)) {
            this.y = (float) ((int) (c.mPercentY * ((float) ((int) (((float) parentHeight) - this.height)))));
        }
        this.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        this.mPathMotionArc = c.mPathMotionArc;
    }

    /* access modifiers changed from: package-private */
    public void initPath(KeyPosition c, MotionPaths startTimePoint, MotionPaths endTimePoint) {
        float path;
        float position2 = ((float) c.mFramePosition) / 100.0f;
        this.time = position2;
        this.mDrawPath = c.mDrawPath;
        float scaleWidth = Float.isNaN(c.mPercentWidth) ? position2 : c.mPercentWidth;
        float scaleHeight = Float.isNaN(c.mPercentHeight) ? position2 : c.mPercentHeight;
        float scaleX = endTimePoint.width - startTimePoint.width;
        float scaleY = endTimePoint.height - startTimePoint.height;
        this.position = this.time;
        if (Float.isNaN(c.mPercentX)) {
            path = position2;
        } else {
            path = c.mPercentX;
        }
        float startCenterX = startTimePoint.x + (startTimePoint.width / 2.0f);
        float startCenterY = startTimePoint.y + (startTimePoint.height / 2.0f);
        float pathVectorX = (endTimePoint.x + (endTimePoint.width / 2.0f)) - startCenterX;
        float pathVectorY = (endTimePoint.y + (endTimePoint.height / 2.0f)) - startCenterY;
        this.x = (float) ((int) ((startTimePoint.x + (pathVectorX * path)) - ((scaleX * scaleWidth) / 2.0f)));
        this.y = (float) ((int) ((startTimePoint.y + (pathVectorY * path)) - ((scaleY * scaleHeight) / 2.0f)));
        this.width = (float) ((int) (startTimePoint.width + (scaleX * scaleWidth)));
        this.height = (float) ((int) (startTimePoint.height + (scaleY * scaleHeight)));
        float perpendicular = Float.isNaN(c.mPercentY) ? 0.0f : c.mPercentY;
        this.mMode = 1;
        this.x = (float) ((int) ((startTimePoint.x + (pathVectorX * path)) - ((scaleX * scaleWidth) / 2.0f)));
        this.y = (float) ((int) ((startTimePoint.y + (pathVectorY * path)) - ((scaleY * scaleHeight) / 2.0f)));
        this.x += (-pathVectorY) * perpendicular;
        this.y += pathVectorX * perpendicular;
        this.mKeyFrameEasing = Easing.getInterpolator(c.mTransitionEasing);
        this.mPathMotionArc = c.mPathMotionArc;
    }

    private static final float xRotate(float sin, float cos, float cx, float cy, float x2, float y2) {
        return (((x2 - cx) * cos) - ((y2 - cy) * sin)) + cx;
    }

    private static final float yRotate(float sin, float cos, float cx, float cy, float x2, float y2) {
        return ((x2 - cx) * sin) + ((y2 - cy) * cos) + cy;
    }

    private boolean diff(float a, float b) {
        return (Float.isNaN(a) || Float.isNaN(b)) ? Float.isNaN(a) != Float.isNaN(b) : Math.abs(a - b) > 1.0E-6f;
    }

    /* access modifiers changed from: package-private */
    public void different(MotionPaths points, boolean[] mask, String[] custom, boolean arcMode) {
        int c = 0 + 1;
        mask[0] = mask[0] | diff(this.position, points.position);
        int c2 = c + 1;
        mask[c] = mask[c] | diff(this.x, points.x) | arcMode;
        int c3 = c2 + 1;
        mask[c2] = mask[c2] | diff(this.y, points.y) | arcMode;
        int c4 = c3 + 1;
        mask[c3] = mask[c3] | diff(this.width, points.width);
        int i = c4 + 1;
        mask[c4] = mask[c4] | diff(this.height, points.height);
    }

    /* access modifiers changed from: package-private */
    public void getCenter(int[] toUse, double[] data, float[] point, int offset) {
        float v_x = this.x;
        float v_y = this.y;
        float v_width = this.width;
        float v_height = this.height;
        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];
            switch (toUse[i]) {
                case 1:
                    v_x = value;
                    break;
                case 2:
                    v_y = value;
                    break;
                case 3:
                    v_width = value;
                    break;
                case 4:
                    v_height = value;
                    break;
            }
        }
        point[offset] = (v_width / 2.0f) + v_x + 0.0f;
        point[offset + 1] = (v_height / 2.0f) + v_y + 0.0f;
    }

    /* access modifiers changed from: package-private */
    public void setView(View view, int[] toUse, double[] data, double[] slope, double[] cycle) {
        float rot;
        float v_x = this.x;
        float v_y = this.y;
        float v_width = this.width;
        float v_height = this.height;
        float dv_x = 0.0f;
        float dv_y = 0.0f;
        float dv_width = 0.0f;
        float dv_height = 0.0f;
        float path_rotate = Float.NaN;
        if (toUse.length != 0 && this.mTempValue.length <= toUse[toUse.length - 1]) {
            int scratch_data_length = toUse[toUse.length - 1] + 1;
            this.mTempValue = new double[scratch_data_length];
            this.mTempDelta = new double[scratch_data_length];
        }
        Arrays.fill(this.mTempValue, Double.NaN);
        for (int i = 0; i < toUse.length; i++) {
            this.mTempValue[toUse[i]] = data[i];
            this.mTempDelta[toUse[i]] = slope[i];
        }
        for (int i2 = 0; i2 < this.mTempValue.length; i2++) {
            if (!Double.isNaN(this.mTempValue[i2]) || !(cycle == null || cycle[i2] == 0.0d)) {
                double deltaCycle = cycle != null ? cycle[i2] : 0.0d;
                if (!Double.isNaN(this.mTempValue[i2])) {
                    deltaCycle += this.mTempValue[i2];
                }
                float value = (float) deltaCycle;
                float dvalue = (float) this.mTempDelta[i2];
                switch (i2) {
                    case 0:
                        continue;
                    case 1:
                        v_x = value;
                        dv_x = dvalue;
                        continue;
                    case 2:
                        v_y = value;
                        dv_y = dvalue;
                        continue;
                    case 3:
                        v_width = value;
                        dv_width = dvalue;
                        continue;
                    case 4:
                        v_height = value;
                        dv_height = dvalue;
                        continue;
                    case 5:
                        path_rotate = value;
                        continue;
                }
            }
        }
        if (!Float.isNaN(path_rotate)) {
            if (Float.isNaN(Float.NaN)) {
                rot = 0.0f;
            } else {
                rot = Float.NaN;
            }
            view.setRotation((float) (((double) rot) + ((double) path_rotate) + Math.toDegrees(Math.atan2((double) (dv_y + (dv_height / 2.0f)), (double) (dv_x + (dv_width / 2.0f))))));
        } else if (!Float.isNaN(Float.NaN)) {
            view.setRotation(Float.NaN);
        }
        int l = (int) (0.5f + v_x);
        int t = (int) (0.5f + v_y);
        int r = (int) (0.5f + v_x + v_width);
        int b = (int) (0.5f + v_y + v_height);
        int i_width = r - l;
        int i_height = b - t;
        boolean remeasure = (i_width == view.getWidth() && i_height == view.getHeight()) ? false : true;
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(i_width, 1073741824);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(i_height, 1073741824);
        if (!(this.mLastMeasuredWidth == widthMeasureSpec && this.mLastMeasuredHeight == heightMeasureSpec)) {
            remeasure = true;
        }
        if (remeasure) {
            this.mLastMeasuredWidth = widthMeasureSpec;
            this.mLastMeasuredHeight = heightMeasureSpec;
            view.measure(widthMeasureSpec, heightMeasureSpec);
        }
        view.layout(l, t, r, b);
    }

    /* access modifiers changed from: package-private */
    public void getRect(int[] toUse, double[] data, float[] path, int offset) {
        float v_x = this.x;
        float v_y = this.y;
        float v_width = this.width;
        float v_height = this.height;
        for (int i = 0; i < toUse.length; i++) {
            float value = (float) data[i];
            switch (toUse[i]) {
                case 1:
                    v_x = value;
                    break;
                case 2:
                    v_y = value;
                    break;
                case 3:
                    v_width = value;
                    break;
                case 4:
                    v_height = value;
                    break;
            }
        }
        float x1 = v_x;
        float y1 = v_y;
        float x2 = v_x + v_width;
        float y2 = y1;
        float x3 = x2;
        float y3 = v_y + v_height;
        float x4 = x1;
        float y4 = y3;
        float cx = x1 + (v_width / 2.0f);
        float cy = y1 + (v_height / 2.0f);
        if (!Float.isNaN(Float.NaN)) {
            cx = x1 + ((x2 - x1) * Float.NaN);
        }
        if (!Float.isNaN(Float.NaN)) {
            cy = y1 + ((y3 - y1) * Float.NaN);
        }
        if (1.0f != 1.0f) {
            float midx = (x1 + x2) / 2.0f;
            x1 = ((x1 - midx) * 1.0f) + midx;
            x2 = ((x2 - midx) * 1.0f) + midx;
            x3 = ((x3 - midx) * 1.0f) + midx;
            x4 = ((x4 - midx) * 1.0f) + midx;
        }
        if (1.0f != 1.0f) {
            float midy = (y1 + y3) / 2.0f;
            y1 = ((y1 - midy) * 1.0f) + midy;
            y2 = ((y2 - midy) * 1.0f) + midy;
            y3 = ((y3 - midy) * 1.0f) + midy;
            y4 = ((y4 - midy) * 1.0f) + midy;
        }
        if (0.0f != 0.0f) {
            float sin = (float) Math.sin(Math.toRadians((double) 0.0f));
            float cos = (float) Math.cos(Math.toRadians((double) 0.0f));
            float tx1 = xRotate(sin, cos, cx, cy, x1, y1);
            float ty1 = yRotate(sin, cos, cx, cy, x1, y1);
            float tx2 = xRotate(sin, cos, cx, cy, x2, y2);
            float ty2 = yRotate(sin, cos, cx, cy, x2, y2);
            float tx3 = xRotate(sin, cos, cx, cy, x3, y3);
            float ty3 = yRotate(sin, cos, cx, cy, x3, y3);
            float tx4 = xRotate(sin, cos, cx, cy, x4, y4);
            float ty4 = yRotate(sin, cos, cx, cy, x4, y4);
            x1 = tx1;
            y1 = ty1;
            x2 = tx2;
            y2 = ty2;
            x3 = tx3;
            y3 = ty3;
            x4 = tx4;
            y4 = ty4;
        }
        int offset2 = offset + 1;
        path[offset] = x1 + 0.0f;
        int offset3 = offset2 + 1;
        path[offset2] = y1 + 0.0f;
        int offset4 = offset3 + 1;
        path[offset3] = x2 + 0.0f;
        int offset5 = offset4 + 1;
        path[offset4] = y2 + 0.0f;
        int offset6 = offset5 + 1;
        path[offset5] = x3 + 0.0f;
        int offset7 = offset6 + 1;
        path[offset6] = y3 + 0.0f;
        int offset8 = offset7 + 1;
        path[offset7] = x4 + 0.0f;
        int i2 = offset8 + 1;
        path[offset8] = y4 + 0.0f;
    }

    /* access modifiers changed from: package-private */
    public void setDpDt(float locationX, float locationY, float[] mAnchorDpDt, int[] toUse, double[] deltaData, double[] data) {
        float d_x = 0.0f;
        float d_y = 0.0f;
        float d_width = 0.0f;
        float d_height = 0.0f;
        for (int i = 0; i < toUse.length; i++) {
            float deltaV = (float) deltaData[i];
            float f = (float) data[i];
            switch (toUse[i]) {
                case 1:
                    d_x = deltaV;
                    break;
                case 2:
                    d_y = deltaV;
                    break;
                case 3:
                    d_width = deltaV;
                    break;
                case 4:
                    d_height = deltaV;
                    break;
            }
        }
        float deltaX = d_x - ((0.0f * d_width) / 2.0f);
        float deltaY = d_y - ((0.0f * d_height) / 2.0f);
        mAnchorDpDt[0] = ((1.0f - locationX) * deltaX) + ((deltaX + (d_width * (1.0f + 0.0f))) * locationX) + 0.0f;
        mAnchorDpDt[1] = ((1.0f - locationY) * deltaY) + ((deltaY + (d_height * (1.0f + 0.0f))) * locationY) + 0.0f;
    }

    /* access modifiers changed from: package-private */
    public void fillStandard(double[] data, int[] toUse) {
        float[] set = {this.position, this.x, this.y, this.width, this.height, this.mPathRotate};
        int c = 0;
        for (int i = 0; i < toUse.length; i++) {
            if (toUse[i] < set.length) {
                data[c] = (double) set[toUse[i]];
                c++;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasCustomData(String name) {
        return this.attributes.containsKey(name);
    }

    /* access modifiers changed from: package-private */
    public int getCustomDataCount(String name) {
        return this.attributes.get(name).noOfInterpValues();
    }

    /* access modifiers changed from: package-private */
    public int getCustomData(String name, double[] value, int offset) {
        int N = 1;
        ConstraintAttribute a = this.attributes.get(name);
        if (a.noOfInterpValues() == 1) {
            value[offset] = (double) a.getValueToInterpolate();
        } else {
            N = a.noOfInterpValues();
            float[] f = new float[N];
            a.getValuesToInterpolate(f);
            int i = 0;
            int offset2 = offset;
            while (i < N) {
                value[offset2] = (double) f[i];
                i++;
                offset2++;
            }
        }
        return N;
    }

    /* access modifiers changed from: package-private */
    public void setBounds(float x2, float y2, float w, float h) {
        this.x = x2;
        this.y = y2;
        this.width = w;
        this.height = h;
        this.mLastMeasuredWidth = View.MeasureSpec.makeMeasureSpec((int) w, 1073741824);
        this.mLastMeasuredHeight = View.MeasureSpec.makeMeasureSpec((int) h, 1073741824);
    }

    public int compareTo(@NonNull MotionPaths o) {
        return Float.compare(this.position, o.position);
    }

    public void applyParameters(ConstraintSet.Constraint c) {
        this.mKeyFrameEasing = Easing.getInterpolator(c.motion.mTransitionEasing);
        this.mPathMotionArc = c.motion.mPathMotionArc;
        this.mPathRotate = c.motion.mPathRotate;
        this.mDrawPath = c.motion.mDrawPath;
        this.mProgress = c.propertySet.mProgress;
        for (String s : c.mCustomConstraints.keySet()) {
            ConstraintAttribute attr = c.mCustomConstraints.get(s);
            if (attr.getType() != ConstraintAttribute.AttributeType.STRING_TYPE) {
                this.attributes.put(s, attr);
            }
        }
    }
}
