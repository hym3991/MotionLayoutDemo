package androidx.constraintlayout.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ViewParent;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.R;

public class MotionTelltales extends MockView {
    private static final String TAG = "MotionTelltales";
    Matrix mInvertMatrix;
    MotionLayout mMotionLayout;
    private Paint mPaintTelltales;
    int mTailColor;
    float mTailScale;
    int mVelocityMode;
    float[] velocity;

    public MotionTelltales(Context context) {
        super(context);
        this.mPaintTelltales = new Paint();
        this.velocity = new float[2];
        this.mInvertMatrix = new Matrix();
        this.mVelocityMode = 0;
        this.mTailColor = -65281;
        this.mTailScale = 0.25f;
        init(context, null);
    }

    public MotionTelltales(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaintTelltales = new Paint();
        this.velocity = new float[2];
        this.mInvertMatrix = new Matrix();
        this.mVelocityMode = 0;
        this.mTailColor = -65281;
        this.mTailScale = 0.25f;
        init(context, attrs);
    }

    public MotionTelltales(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaintTelltales = new Paint();
        this.velocity = new float[2];
        this.mInvertMatrix = new Matrix();
        this.mVelocityMode = 0;
        this.mTailColor = -65281;
        this.mTailScale = 0.25f;
        init(context, attrs);
    }

    @Override // androidx.constraintlayout.utils.widget.MockView
    protected void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MotionTelltales);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.MotionTelltales_telltales_tailColor) {
                    this.mTailColor = a.getColor(attr, this.mTailColor);
                } else if (attr == R.styleable.MotionTelltales_telltales_velocityMode) {
                    this.mVelocityMode = a.getInt(attr, this.mVelocityMode);
                } else if (attr == R.styleable.MotionTelltales_telltales_tailScale) {
                    this.mTailScale = a.getFloat(attr, this.mTailScale);
                }
            }
        }
        this.mPaintTelltales.setColor(this.mTailColor);
        this.mPaintTelltales.setStrokeWidth(5.0f);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void setText(CharSequence text) {
        this.mText = text.toString();
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        postInvalidate();
    }

    @Override // androidx.constraintlayout.utils.widget.MockView
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getMatrix().invert(this.mInvertMatrix);
        if (this.mMotionLayout == null) {
            ViewParent vp = getParent();
            if (vp instanceof MotionLayout) {
                this.mMotionLayout = (MotionLayout) vp;
                return;
            }
            return;
        }
        int width = getWidth();
        int height = getHeight();
        float[] f = {0.1f, 0.25f, 0.5f, 0.75f, 0.9f};
        for (int y = 0; y < f.length; y++) {
            float py = f[y];
            for (int x = 0; x < f.length; x++) {
                float px = f[x];
                this.mMotionLayout.getViewVelocity(this, px, py, this.velocity, this.mVelocityMode);
                this.mInvertMatrix.mapVectors(this.velocity);
                float sx = ((float) width) * px;
                float sy = ((float) height) * py;
                float ex = sx - (this.velocity[0] * this.mTailScale);
                float ey = sy - (this.velocity[1] * this.mTailScale);
                this.mInvertMatrix.mapVectors(this.velocity);
                canvas.drawLine(sx, sy, ex, ey, this.mPaintTelltales);
            }
        }
    }
}
