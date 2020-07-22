package androidx.constraintlayout.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.R;

public class ImageFilterView extends AppCompatImageView {
    private float mCrossfade = 0.0f;
    private ImageMatrix mImageMatrix = new ImageMatrix();
    LayerDrawable mLayer;
    Drawable[] mLayers;
    private boolean mOverlay = true;
    private Path mPath;
    RectF mRect;
    /* access modifiers changed from: private */
    public float mRound = Float.NaN;
    /* access modifiers changed from: private */
    public float mRoundPercent = 0.0f;
    ViewOutlineProvider mViewOutlineProvider;

    static class ImageMatrix {
        float[] m = new float[20];
        float mBrightness = 1.0f;
        ColorMatrix mColorMatrix = new ColorMatrix();
        float mContrast = 1.0f;
        float mSaturation = 1.0f;
        ColorMatrix mTmpColorMatrix = new ColorMatrix();
        float mWarmth = 1.0f;

        ImageMatrix() {
        }

        private void saturation(float saturationStrength) {
            float MS = 1.0f - saturationStrength;
            float Rt = 0.2999f * MS;
            float Gt = 0.587f * MS;
            float Bt = 0.114f * MS;
            this.m[0] = Rt + saturationStrength;
            this.m[1] = Gt;
            this.m[2] = Bt;
            this.m[3] = 0.0f;
            this.m[4] = 0.0f;
            this.m[5] = Rt;
            this.m[6] = Gt + saturationStrength;
            this.m[7] = Bt;
            this.m[8] = 0.0f;
            this.m[9] = 0.0f;
            this.m[10] = Rt;
            this.m[11] = Gt;
            this.m[12] = Bt + saturationStrength;
            this.m[13] = 0.0f;
            this.m[14] = 0.0f;
            this.m[15] = 0.0f;
            this.m[16] = 0.0f;
            this.m[17] = 0.0f;
            this.m[18] = 1.0f;
            this.m[19] = 0.0f;
        }

        private void warmth(float warmth) {
            float colorG;
            float colorR;
            float colorB;
            float colorG2;
            float colorR2;
            float colorB2;
            if (warmth <= 0.0f) {
                warmth = 0.01f;
            }
            float centiKelvin = (5000.0f / warmth) / 100.0f;
            if (centiKelvin > 66.0f) {
                float tmp = centiKelvin - 60.0f;
                colorR = 329.69873f * ((float) Math.pow((double) tmp, -0.13320475816726685d));
                colorG = 288.12216f * ((float) Math.pow((double) tmp, 0.07551484555006027d));
            } else {
                colorG = (99.4708f * ((float) Math.log((double) centiKelvin))) - 161.11957f;
                colorR = 255.0f;
            }
            if (centiKelvin >= 66.0f) {
                colorB = 255.0f;
            } else if (centiKelvin > 19.0f) {
                colorB = (138.51773f * ((float) Math.log((double) (centiKelvin - 10.0f)))) - 305.0448f;
            } else {
                colorB = 0.0f;
            }
            float tmpColor_r = Math.min(255.0f, Math.max(colorR, 0.0f));
            float tmpColor_g = Math.min(255.0f, Math.max(colorG, 0.0f));
            float tmpColor_b = Math.min(255.0f, Math.max(colorB, 0.0f));
            float centiKelvin2 = 5000.0f / 100.0f;
            if (centiKelvin2 > 66.0f) {
                float tmp2 = centiKelvin2 - 60.0f;
                colorR2 = 329.69873f * ((float) Math.pow((double) tmp2, -0.13320475816726685d));
                colorG2 = 288.12216f * ((float) Math.pow((double) tmp2, 0.07551484555006027d));
            } else {
                colorG2 = (99.4708f * ((float) Math.log((double) centiKelvin2))) - 161.11957f;
                colorR2 = 255.0f;
            }
            if (centiKelvin2 >= 66.0f) {
                colorB2 = 255.0f;
            } else if (centiKelvin2 > 19.0f) {
                colorB2 = (138.51773f * ((float) Math.log((double) (centiKelvin2 - 10.0f)))) - 305.0448f;
            } else {
                colorB2 = 0.0f;
            }
            float tmpColor_r2 = Math.min(255.0f, Math.max(colorR2, 0.0f));
            float tmpColor_g2 = Math.min(255.0f, Math.max(colorG2, 0.0f));
            this.m[0] = tmpColor_r / tmpColor_r2;
            this.m[1] = 0.0f;
            this.m[2] = 0.0f;
            this.m[3] = 0.0f;
            this.m[4] = 0.0f;
            this.m[5] = 0.0f;
            this.m[6] = tmpColor_g / tmpColor_g2;
            this.m[7] = 0.0f;
            this.m[8] = 0.0f;
            this.m[9] = 0.0f;
            this.m[10] = 0.0f;
            this.m[11] = 0.0f;
            this.m[12] = tmpColor_b / Math.min(255.0f, Math.max(colorB2, 0.0f));
            this.m[13] = 0.0f;
            this.m[14] = 0.0f;
            this.m[15] = 0.0f;
            this.m[16] = 0.0f;
            this.m[17] = 0.0f;
            this.m[18] = 1.0f;
            this.m[19] = 0.0f;
        }

        private void brightness(float brightness) {
            this.m[0] = brightness;
            this.m[1] = 0.0f;
            this.m[2] = 0.0f;
            this.m[3] = 0.0f;
            this.m[4] = 0.0f;
            this.m[5] = 0.0f;
            this.m[6] = brightness;
            this.m[7] = 0.0f;
            this.m[8] = 0.0f;
            this.m[9] = 0.0f;
            this.m[10] = 0.0f;
            this.m[11] = 0.0f;
            this.m[12] = brightness;
            this.m[13] = 0.0f;
            this.m[14] = 0.0f;
            this.m[15] = 0.0f;
            this.m[16] = 0.0f;
            this.m[17] = 0.0f;
            this.m[18] = 1.0f;
            this.m[19] = 0.0f;
        }

        /* access modifiers changed from: package-private */
        public void updateMatrix(ImageView view) {
            this.mColorMatrix.reset();
            boolean filter = false;
            if (this.mSaturation != 1.0f) {
                saturation(this.mSaturation);
                this.mColorMatrix.set(this.m);
                filter = true;
            }
            if (this.mContrast != 1.0f) {
                this.mTmpColorMatrix.setScale(this.mContrast, this.mContrast, this.mContrast, 1.0f);
                this.mColorMatrix.postConcat(this.mTmpColorMatrix);
                filter = true;
            }
            if (this.mWarmth != 1.0f) {
                warmth(this.mWarmth);
                this.mTmpColorMatrix.set(this.m);
                this.mColorMatrix.postConcat(this.mTmpColorMatrix);
                filter = true;
            }
            if (this.mBrightness != 1.0f) {
                brightness(this.mBrightness);
                this.mTmpColorMatrix.set(this.m);
                this.mColorMatrix.postConcat(this.mTmpColorMatrix);
                filter = true;
            }
            if (filter) {
                view.setColorFilter(new ColorMatrixColorFilter(this.mColorMatrix));
            } else {
                view.clearColorFilter();
            }
        }
    }

    public ImageFilterView(Context context) {
        super(context);
        init(context, null);
    }

    public ImageFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ImageFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ImageFilterView);
            int N = a.getIndexCount();
            Drawable drawable = a.getDrawable(R.styleable.ImageFilterView_altSrc);
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.ImageFilterView_crossfade) {
                    this.mCrossfade = a.getFloat(attr, 0.0f);
                } else if (attr == R.styleable.ImageFilterView_warmth) {
                    setWarmth(a.getFloat(attr, 0.0f));
                } else if (attr == R.styleable.ImageFilterView_saturation) {
                    setSaturation(a.getFloat(attr, 0.0f));
                } else if (attr == R.styleable.ImageFilterView_contrast) {
                    setContrast(a.getFloat(attr, 0.0f));
                } else if (attr == R.styleable.ImageFilterView_round) {
                    setRound(a.getDimension(attr, 0.0f));
                } else if (attr == R.styleable.ImageFilterView_roundPercent) {
                    setRoundPercent(a.getFloat(attr, 0.0f));
                } else if (attr == R.styleable.ImageFilterView_overlay) {
                    setOverlay(a.getBoolean(attr, this.mOverlay));
                }
            }
            a.recycle();
            if (drawable != null) {
                this.mLayers = new Drawable[2];
                this.mLayers[0] = getDrawable();
                this.mLayers[1] = drawable;
                this.mLayer = new LayerDrawable(this.mLayers);
                this.mLayer.getDrawable(1).setAlpha((int) (255.0f * this.mCrossfade));
                ImageFilterView.super.setImageDrawable(this.mLayer);
            }
        }
    }

    private void setOverlay(boolean overlay) {
        this.mOverlay = overlay;
    }

    public void setSaturation(float saturation) {
        this.mImageMatrix.mSaturation = saturation;
        this.mImageMatrix.updateMatrix(this);
    }

    public float getSaturation() {
        return this.mImageMatrix.mSaturation;
    }

    public void setContrast(float contrast) {
        this.mImageMatrix.mContrast = contrast;
        this.mImageMatrix.updateMatrix(this);
    }

    public float getContrast() {
        return this.mImageMatrix.mContrast;
    }

    public void setWarmth(float warmth) {
        this.mImageMatrix.mWarmth = warmth;
        this.mImageMatrix.updateMatrix(this);
    }

    public float getWarmth() {
        return this.mImageMatrix.mWarmth;
    }

    public void setCrossfade(float crossfade) {
        this.mCrossfade = crossfade;
        if (this.mLayers != null) {
            if (!this.mOverlay) {
                this.mLayer.getDrawable(0).setAlpha((int) ((1.0f - this.mCrossfade) * 255.0f));
            }
            this.mLayer.getDrawable(1).setAlpha((int) (this.mCrossfade * 255.0f));
            ImageFilterView.super.setImageDrawable(this.mLayer);
        }
    }

    public float getCrossfade() {
        return this.mCrossfade;
    }

    public void setBrightness(float brightness) {
        this.mImageMatrix.mBrightness = brightness;
        this.mImageMatrix.updateMatrix(this);
    }

    public float getBrightness() {
        return this.mImageMatrix.mBrightness;
    }

    @RequiresApi(ConstraintLayout.LayoutParams.Table.LAYOUT_GONE_MARGIN_LEFT)
    public void setRoundPercent(float round) {
        boolean change;
        if (this.mRoundPercent != round) {
            change = true;
        } else {
            change = false;
        }
        this.mRoundPercent = round;
        if (this.mRoundPercent != 0.0f) {
            if (this.mPath == null) {
                this.mPath = new Path();
            }
            if (this.mRect == null) {
                this.mRect = new RectF();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                if (this.mViewOutlineProvider == null) {
                    this.mViewOutlineProvider = new ViewOutlineProvider() {
                        /* class androidx.constraintlayout.utils.widget.ImageFilterView.AnonymousClass1 */

                        public void getOutline(View view, Outline outline) {
                            int w = ImageFilterView.this.getWidth();
                            int h = ImageFilterView.this.getHeight();
                            outline.setRoundRect(0, 0, w, h, (((float) Math.min(w, h)) * ImageFilterView.this.mRoundPercent) / 2.0f);
                        }
                    };
                    setOutlineProvider(this.mViewOutlineProvider);
                }
                setClipToOutline(true);
            }
            int w = getWidth();
            int h = getHeight();
            float r = (((float) Math.min(w, h)) * this.mRoundPercent) / 2.0f;
            this.mRect.set(0.0f, 0.0f, (float) w, (float) h);
            this.mPath.reset();
            this.mPath.addRoundRect(this.mRect, r, r, Path.Direction.CW);
        } else if (Build.VERSION.SDK_INT >= 21) {
            setClipToOutline(false);
        }
        if (change && Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
    }

    @RequiresApi(ConstraintLayout.LayoutParams.Table.LAYOUT_GONE_MARGIN_LEFT)
    public void setRound(float round) {
        boolean change;
        if (Float.isNaN(round)) {
            this.mRound = round;
            float tmp = this.mRoundPercent;
            this.mRoundPercent = -1.0f;
            setRoundPercent(tmp);
            return;
        }
        if (this.mRound != round) {
            change = true;
        } else {
            change = false;
        }
        this.mRound = round;
        if (this.mRound != 0.0f) {
            if (this.mPath == null) {
                this.mPath = new Path();
            }
            if (this.mRect == null) {
                this.mRect = new RectF();
            }
            if (Build.VERSION.SDK_INT >= 21) {
                if (this.mViewOutlineProvider == null) {
                    this.mViewOutlineProvider = new ViewOutlineProvider() {
                        /* class androidx.constraintlayout.utils.widget.ImageFilterView.AnonymousClass2 */

                        public void getOutline(View view, Outline outline) {
                            outline.setRoundRect(0, 0, ImageFilterView.this.getWidth(), ImageFilterView.this.getHeight(), ImageFilterView.this.mRound);
                        }
                    };
                    setOutlineProvider(this.mViewOutlineProvider);
                }
                setClipToOutline(true);
            }
            this.mRect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
            this.mPath.reset();
            this.mPath.addRoundRect(this.mRect, this.mRound, this.mRound, Path.Direction.CW);
        } else if (Build.VERSION.SDK_INT >= 21) {
            setClipToOutline(false);
        }
        if (change && Build.VERSION.SDK_INT >= 21) {
            invalidateOutline();
        }
    }

    public float getRoundPercent() {
        return this.mRoundPercent;
    }

    public float getRound() {
        return this.mRound;
    }

    public void draw(Canvas canvas) {
        boolean clip = false;
        if (!(Build.VERSION.SDK_INT >= 21 || this.mRoundPercent == 0.0f || this.mPath == null)) {
            clip = true;
            canvas.save();
            canvas.clipPath(this.mPath);
        }
        ImageFilterView.super.draw(canvas);
        if (clip) {
            canvas.restore();
        }
    }
}
