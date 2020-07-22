package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.R;
import androidx.core.widget.NestedScrollView;
import org.xmlpull.v1.XmlPullParser;

/* access modifiers changed from: package-private */
public class TouchResponse {
    private static final boolean DEBUG = false;
    static final int FLAG_DISABLE_POST_SCROLL = 1;
    static final int FLAG_DISABLE_SCROLL = 2;
    private static final int SIDE_BOTTOM = 3;
    private static final int SIDE_END = 6;
    private static final int SIDE_LEFT = 1;
    private static final int SIDE_MIDDLE = 4;
    private static final int SIDE_RIGHT = 2;
    private static final int SIDE_START = 5;
    private static final int SIDE_TOP = 0;
    private static final String TAG = "TouchResponse";
    private static final float[][] TOUCH_DIRECTION = {new float[]{0.0f, -1.0f}, new float[]{0.0f, 1.0f}, new float[]{-1.0f, 0.0f}, new float[]{1.0f, 0.0f}, new float[]{-1.0f, 0.0f}, new float[]{1.0f, 0.0f}};
    private static final int TOUCH_DOWN = 1;
    private static final int TOUCH_END = 5;
    private static final int TOUCH_LEFT = 2;
    private static final int TOUCH_RIGHT = 3;
    private static final float[][] TOUCH_SIDES = {new float[]{0.5f, 0.0f}, new float[]{0.0f, 0.5f}, new float[]{1.0f, 0.5f}, new float[]{0.5f, 1.0f}, new float[]{0.5f, 0.5f}, new float[]{0.0f, 0.5f}, new float[]{1.0f, 0.5f}};
    private static final int TOUCH_START = 4;
    private static final int TOUCH_UP = 0;
    private float[] mAnchorDpDt = new float[2];
    private float mDragScale = 1.0f;
    private boolean mDragStarted = false;
    private float mDragThreshold = 10.0f;
    private int mFlags = 0;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mLimitBoundsTo = -1;
    private float mMaxAcceleration = 1.2f;
    private float mMaxVelocity = 4.0f;
    private final MotionLayout mMotionLayout;
    private boolean mMoveWhenScrollAtTop = true;
    private int mOnTouchUp = 0;
    private int mTouchAnchorId = -1;
    private int mTouchAnchorSide = 0;
    private float mTouchAnchorX = 0.5f;
    private float mTouchAnchorY = 0.5f;
    private float mTouchDirectionX = 0.0f;
    private float mTouchDirectionY = 1.0f;
    private int mTouchRegionId = -1;
    private int mTouchSide = 0;

    TouchResponse(Context context, MotionLayout layout, XmlPullParser parser) {
        this.mMotionLayout = layout;
        fillFromAttributeList(context, Xml.asAttributeSet(parser));
    }

    public void setRTL(boolean rtl) {
        if (rtl) {
            TOUCH_DIRECTION[4] = TOUCH_DIRECTION[3];
            TOUCH_DIRECTION[5] = TOUCH_DIRECTION[2];
            TOUCH_SIDES[5] = TOUCH_SIDES[2];
            TOUCH_SIDES[6] = TOUCH_SIDES[1];
        } else {
            TOUCH_DIRECTION[4] = TOUCH_DIRECTION[2];
            TOUCH_DIRECTION[5] = TOUCH_DIRECTION[3];
            TOUCH_SIDES[5] = TOUCH_SIDES[1];
            TOUCH_SIDES[6] = TOUCH_SIDES[2];
        }
        this.mTouchAnchorX = TOUCH_SIDES[this.mTouchAnchorSide][0];
        this.mTouchAnchorY = TOUCH_SIDES[this.mTouchAnchorSide][1];
        this.mTouchDirectionX = TOUCH_DIRECTION[this.mTouchSide][0];
        this.mTouchDirectionY = TOUCH_DIRECTION[this.mTouchSide][1];
    }

    private void fillFromAttributeList(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OnSwipe);
        fill(a);
        a.recycle();
    }

    private void fill(TypedArray a) {
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.OnSwipe_touchAnchorId) {
                this.mTouchAnchorId = a.getResourceId(attr, this.mTouchAnchorId);
            } else if (attr == R.styleable.OnSwipe_touchAnchorSide) {
                this.mTouchAnchorSide = a.getInt(attr, this.mTouchAnchorSide);
                this.mTouchAnchorX = TOUCH_SIDES[this.mTouchAnchorSide][0];
                this.mTouchAnchorY = TOUCH_SIDES[this.mTouchAnchorSide][1];
            } else if (attr == R.styleable.OnSwipe_dragDirection) {
                this.mTouchSide = a.getInt(attr, this.mTouchSide);
                this.mTouchDirectionX = TOUCH_DIRECTION[this.mTouchSide][0];
                this.mTouchDirectionY = TOUCH_DIRECTION[this.mTouchSide][1];
            } else if (attr == R.styleable.OnSwipe_maxVelocity) {
                this.mMaxVelocity = a.getFloat(attr, this.mMaxVelocity);
            } else if (attr == R.styleable.OnSwipe_maxAcceleration) {
                this.mMaxAcceleration = a.getFloat(attr, this.mMaxAcceleration);
            } else if (attr == R.styleable.OnSwipe_moveWhenScrollAtTop) {
                this.mMoveWhenScrollAtTop = a.getBoolean(attr, this.mMoveWhenScrollAtTop);
            } else if (attr == R.styleable.OnSwipe_dragScale) {
                this.mDragScale = a.getFloat(attr, this.mDragScale);
            } else if (attr == R.styleable.OnSwipe_dragThreshold) {
                this.mDragThreshold = a.getFloat(attr, this.mDragThreshold);
            } else if (attr == R.styleable.OnSwipe_touchRegionId) {
                this.mTouchRegionId = a.getResourceId(attr, this.mTouchRegionId);
            } else if (attr == R.styleable.OnSwipe_onTouchUp) {
                this.mOnTouchUp = a.getInt(attr, this.mOnTouchUp);
            } else if (attr == R.styleable.OnSwipe_nestedScrollFlags) {
                this.mFlags = a.getInteger(attr, 0);
            } else if (attr == R.styleable.OnSwipe_limitBoundsTo) {
                this.mLimitBoundsTo = a.getResourceId(attr, 0);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setUpTouchEvent(float lastTouchX, float lastTouchY) {
        this.mLastTouchX = lastTouchX;
        this.mLastTouchY = lastTouchY;
        this.mDragStarted = false;
    }

    /* access modifiers changed from: package-private */
    public void processTouchEvent(MotionEvent event, MotionLayout.MotionTracker velocityTracker, int currentState, MotionScene motionScene) {
        float velocity;
        float f;
        float change;
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case 0:
                this.mLastTouchX = event.getRawX();
                this.mLastTouchY = event.getRawY();
                this.mDragStarted = false;
                return;
            case 1:
                this.mDragStarted = false;
                velocityTracker.computeCurrentVelocity(1000);
                float tvx = velocityTracker.getXVelocity();
                float tvy = velocityTracker.getYVelocity();
                float pos = this.mMotionLayout.getProgress();
                if (this.mTouchAnchorId != -1) {
                    this.mMotionLayout.getAnchorDpDt(this.mTouchAnchorId, pos, this.mTouchAnchorX, this.mTouchAnchorY, this.mAnchorDpDt);
                } else {
                    float minSize = (float) Math.min(this.mMotionLayout.getWidth(), this.mMotionLayout.getHeight());
                    this.mAnchorDpDt[1] = this.mTouchDirectionY * minSize;
                    this.mAnchorDpDt[0] = this.mTouchDirectionX * minSize;
                }
                float f2 = (this.mTouchDirectionX * this.mAnchorDpDt[0]) + (this.mTouchDirectionY * this.mAnchorDpDt[1]);
                if (this.mTouchDirectionX != 0.0f) {
                    velocity = tvx / this.mAnchorDpDt[0];
                } else {
                    velocity = tvy / this.mAnchorDpDt[1];
                }
                if (!Float.isNaN(velocity)) {
                    pos += velocity / 3.0f;
                }
                if (pos != 0.0f && pos != 1.0f && this.mOnTouchUp != 3) {
                    MotionLayout motionLayout = this.mMotionLayout;
                    int i = this.mOnTouchUp;
                    if (((double) pos) < 0.5d) {
                        f = 0.0f;
                    } else {
                        f = 1.0f;
                    }
                    motionLayout.touchAnimateTo(i, f, velocity);
                    return;
                }
                return;
            case 2:
                float dy = event.getRawY() - this.mLastTouchY;
                float dx = event.getRawX() - this.mLastTouchX;
                if (Math.abs((this.mTouchDirectionX * dx) + (this.mTouchDirectionY * dy)) > this.mDragThreshold || this.mDragStarted) {
                    float pos2 = this.mMotionLayout.getProgress();
                    if (!this.mDragStarted) {
                        this.mDragStarted = true;
                        this.mMotionLayout.setProgress(pos2);
                    }
                    if (this.mTouchAnchorId != -1) {
                        this.mMotionLayout.getAnchorDpDt(this.mTouchAnchorId, pos2, this.mTouchAnchorX, this.mTouchAnchorY, this.mAnchorDpDt);
                    } else {
                        float minSize2 = (float) Math.min(this.mMotionLayout.getWidth(), this.mMotionLayout.getHeight());
                        this.mAnchorDpDt[1] = this.mTouchDirectionY * minSize2;
                        this.mAnchorDpDt[0] = this.mTouchDirectionX * minSize2;
                    }
                    if (((double) Math.abs(((this.mTouchDirectionX * this.mAnchorDpDt[0]) + (this.mTouchDirectionY * this.mAnchorDpDt[1])) * this.mDragScale)) < 0.01d) {
                        this.mAnchorDpDt[0] = 0.01f;
                        this.mAnchorDpDt[1] = 0.01f;
                    }
                    if (this.mTouchDirectionX != 0.0f) {
                        change = dx / this.mAnchorDpDt[0];
                    } else {
                        change = dy / this.mAnchorDpDt[1];
                    }
                    float pos3 = Math.max(Math.min(pos2 + change, 1.0f), 0.0f);
                    if (pos3 != this.mMotionLayout.getProgress()) {
                        this.mMotionLayout.setProgress(pos3);
                        velocityTracker.computeCurrentVelocity(1000);
                        this.mMotionLayout.mLastVelocity = this.mTouchDirectionX != 0.0f ? velocityTracker.getXVelocity() / this.mAnchorDpDt[0] : velocityTracker.getYVelocity() / this.mAnchorDpDt[1];
                    } else {
                        this.mMotionLayout.mLastVelocity = 0.0f;
                    }
                    this.mLastTouchX = event.getRawX();
                    this.mLastTouchY = event.getRawY();
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public void setDown(float lastTouchX, float lastTouchY) {
        this.mLastTouchX = lastTouchX;
        this.mLastTouchY = lastTouchY;
    }

    /* access modifiers changed from: package-private */
    public float getProgressDirection(float dx, float dy) {
        this.mMotionLayout.getAnchorDpDt(this.mTouchAnchorId, this.mMotionLayout.getProgress(), this.mTouchAnchorX, this.mTouchAnchorY, this.mAnchorDpDt);
        if (this.mTouchDirectionX != 0.0f) {
            if (this.mAnchorDpDt[0] == 0.0f) {
                this.mAnchorDpDt[0] = 1.0E-7f;
            }
            return (this.mTouchDirectionX * dx) / this.mAnchorDpDt[0];
        }
        if (this.mAnchorDpDt[1] == 0.0f) {
            this.mAnchorDpDt[1] = 1.0E-7f;
        }
        return (this.mTouchDirectionY * dy) / this.mAnchorDpDt[1];
    }

    /* access modifiers changed from: package-private */
    public void scrollUp(float dx, float dy) {
        float velocity;
        boolean z;
        float f;
        boolean z2 = true;
        this.mDragStarted = false;
        float pos = this.mMotionLayout.getProgress();
        this.mMotionLayout.getAnchorDpDt(this.mTouchAnchorId, pos, this.mTouchAnchorX, this.mTouchAnchorY, this.mAnchorDpDt);
        float f2 = (this.mTouchDirectionX * this.mAnchorDpDt[0]) + (this.mTouchDirectionY * this.mAnchorDpDt[1]);
        if (this.mTouchDirectionX != 0.0f) {
            velocity = (this.mTouchDirectionX * dx) / this.mAnchorDpDt[0];
        } else {
            velocity = (this.mTouchDirectionY * dy) / this.mAnchorDpDt[1];
        }
        if (!Float.isNaN(velocity)) {
            pos += velocity / 3.0f;
        }
        if (pos != 0.0f) {
            if (pos != 1.0f) {
                z = true;
            } else {
                z = false;
            }
            if (this.mOnTouchUp == 3) {
                z2 = false;
            }
            if (z && z2) {
                MotionLayout motionLayout = this.mMotionLayout;
                int i = this.mOnTouchUp;
                if (((double) pos) < 0.5d) {
                    f = 0.0f;
                } else {
                    f = 1.0f;
                }
                motionLayout.touchAnimateTo(i, f, velocity);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void scrollMove(float dx, float dy) {
        float change;
        float f = (this.mTouchDirectionX * dx) + (this.mTouchDirectionY * dy);
        float pos = this.mMotionLayout.getProgress();
        if (!this.mDragStarted) {
            this.mDragStarted = true;
            this.mMotionLayout.setProgress(pos);
        }
        this.mMotionLayout.getAnchorDpDt(this.mTouchAnchorId, pos, this.mTouchAnchorX, this.mTouchAnchorY, this.mAnchorDpDt);
        if (((double) Math.abs((this.mTouchDirectionX * this.mAnchorDpDt[0]) + (this.mTouchDirectionY * this.mAnchorDpDt[1]))) < 0.01d) {
            this.mAnchorDpDt[0] = 0.01f;
            this.mAnchorDpDt[1] = 0.01f;
        }
        if (this.mTouchDirectionX != 0.0f) {
            change = (this.mTouchDirectionX * dx) / this.mAnchorDpDt[0];
        } else {
            change = (this.mTouchDirectionY * dy) / this.mAnchorDpDt[1];
        }
        float pos2 = Math.max(Math.min(pos + change, 1.0f), 0.0f);
        if (pos2 != this.mMotionLayout.getProgress()) {
            this.mMotionLayout.setProgress(pos2);
        }
    }

    /* access modifiers changed from: package-private */
    public void setupTouch() {
        View view = this.mMotionLayout.findViewById(this.mTouchAnchorId);
        if (view == null) {
            Log.w(TAG, " cannot find view to handle touch");
        }
        if (view instanceof NestedScrollView) {
            NestedScrollView sv = (NestedScrollView) view;
            sv.setOnTouchListener(new View.OnTouchListener() {
                /* class androidx.constraintlayout.motion.widget.TouchResponse.AnonymousClass1 */

                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });
            sv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                /* class androidx.constraintlayout.motion.widget.TouchResponse.AnonymousClass2 */

                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                }
            });
        }
    }

    public void setAnchorId(int id) {
        this.mTouchAnchorId = id;
    }

    public int getAnchorId() {
        return this.mTouchAnchorId;
    }

    public void setTouchAnchorLocation(float x, float y) {
        this.mTouchAnchorX = x;
        this.mTouchAnchorY = y;
    }

    public void setMaxVelocity(float velocity) {
        this.mMaxVelocity = velocity;
    }

    public void setMaxAcceleration(float acceleration) {
        this.mMaxAcceleration = acceleration;
    }

    /* access modifiers changed from: package-private */
    public float getMaxAcceleration() {
        return this.mMaxAcceleration;
    }

    public float getMaxVelocity() {
        return this.mMaxVelocity;
    }

    /* access modifiers changed from: package-private */
    public boolean getMoveWhenScrollAtTop() {
        return this.mMoveWhenScrollAtTop;
    }

    /* access modifiers changed from: package-private */
    public RectF getTouchRegion(ViewGroup layout, RectF rect) {
        if (this.mTouchRegionId == -1) {
            return null;
        }
        View view = layout.findViewById(this.mTouchRegionId);
        if (view == null) {
            return null;
        }
        rect.set((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom());
        return rect;
    }

    /* access modifiers changed from: package-private */
    public int getTouchRegionId() {
        return this.mTouchRegionId;
    }

    /* access modifiers changed from: package-private */
    public RectF getLimitBoundsTo(ViewGroup layout, RectF rect) {
        if (this.mLimitBoundsTo == -1) {
            return null;
        }
        View view = layout.findViewById(this.mLimitBoundsTo);
        if (view == null) {
            return null;
        }
        rect.set((float) view.getLeft(), (float) view.getTop(), (float) view.getRight(), (float) view.getBottom());
        return rect;
    }

    /* access modifiers changed from: package-private */
    public int getLimitBoundsToId() {
        return this.mLimitBoundsTo;
    }

    /* access modifiers changed from: package-private */
    public float dot(float dx, float dy) {
        return (this.mTouchDirectionX * dx) + (this.mTouchDirectionY * dy);
    }

    public String toString() {
        return this.mTouchDirectionX + " , " + this.mTouchDirectionY;
    }

    public int getFlags() {
        return this.mFlags;
    }
}
