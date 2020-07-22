package androidx.constraintlayout.motion.utils;

import java.util.Arrays;

class ArcCurveFit extends CurveFit {
    public static final int ARC_START_FLIP = 3;
    public static final int ARC_START_HORIZONTAL = 2;
    public static final int ARC_START_LINEAR = 0;
    public static final int ARC_START_VERTICAL = 1;
    private static final int START_HORIZONTAL = 2;
    private static final int START_LINEAR = 3;
    private static final int START_VERTICAL = 1;
    Arc[] mArcs;
    private final double[] mTime;

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double t, double[] v) {
        if (t < this.mArcs[0].mTime1) {
            t = this.mArcs[0].mTime1;
        }
        if (t > this.mArcs[this.mArcs.length - 1].mTime2) {
            t = this.mArcs[this.mArcs.length - 1].mTime2;
        }
        int i = 0;
        while (i < this.mArcs.length) {
            if (t > this.mArcs[i].mTime2) {
                i++;
            } else if (this.mArcs[i].linear) {
                v[0] = this.mArcs[i].getLinearX(t);
                v[1] = this.mArcs[i].getLinearY(t);
                return;
            } else {
                this.mArcs[i].setPoint(t);
                v[0] = this.mArcs[i].getX();
                v[1] = this.mArcs[i].getY();
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double t, float[] v) {
        if (t < this.mArcs[0].mTime1) {
            t = this.mArcs[0].mTime1;
        } else if (t > this.mArcs[this.mArcs.length - 1].mTime2) {
            t = this.mArcs[this.mArcs.length - 1].mTime2;
        }
        int i = 0;
        while (i < this.mArcs.length) {
            if (t > this.mArcs[i].mTime2) {
                i++;
            } else if (this.mArcs[i].linear) {
                v[0] = (float) this.mArcs[i].getLinearX(t);
                v[1] = (float) this.mArcs[i].getLinearY(t);
                return;
            } else {
                this.mArcs[i].setPoint(t);
                v[0] = (float) this.mArcs[i].getX();
                v[1] = (float) this.mArcs[i].getY();
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getSlope(double t, double[] v) {
        if (t < this.mArcs[0].mTime1) {
            t = this.mArcs[0].mTime1;
        } else if (t > this.mArcs[this.mArcs.length - 1].mTime2) {
            t = this.mArcs[this.mArcs.length - 1].mTime2;
        }
        int i = 0;
        while (i < this.mArcs.length) {
            if (t > this.mArcs[i].mTime2) {
                i++;
            } else if (this.mArcs[i].linear) {
                v[0] = this.mArcs[i].getLinearDX(t);
                v[1] = this.mArcs[i].getLinearDY(t);
                return;
            } else {
                this.mArcs[i].setPoint(t);
                v[0] = this.mArcs[i].getDX();
                v[1] = this.mArcs[i].getDY();
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getPos(double t, int j) {
        if (t < this.mArcs[0].mTime1) {
            t = this.mArcs[0].mTime1;
        } else if (t > this.mArcs[this.mArcs.length - 1].mTime2) {
            t = this.mArcs[this.mArcs.length - 1].mTime2;
        }
        int i = 0;
        while (i < this.mArcs.length) {
            if (t > this.mArcs[i].mTime2) {
                i++;
            } else if (!this.mArcs[i].linear) {
                this.mArcs[i].setPoint(t);
                if (j == 0) {
                    return this.mArcs[i].getX();
                }
                return this.mArcs[i].getY();
            } else if (j == 0) {
                return this.mArcs[i].getLinearX(t);
            } else {
                return this.mArcs[i].getLinearY(t);
            }
        }
        return Double.NaN;
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getSlope(double t, int j) {
        if (t < this.mArcs[0].mTime1) {
            t = this.mArcs[0].mTime1;
        }
        if (t > this.mArcs[this.mArcs.length - 1].mTime2) {
            t = this.mArcs[this.mArcs.length - 1].mTime2;
        }
        int i = 0;
        while (i < this.mArcs.length) {
            if (t > this.mArcs[i].mTime2) {
                i++;
            } else if (!this.mArcs[i].linear) {
                this.mArcs[i].setPoint(t);
                if (j == 0) {
                    return this.mArcs[i].getDX();
                }
                return this.mArcs[i].getDY();
            } else if (j == 0) {
                return this.mArcs[i].getLinearDX(t);
            } else {
                return this.mArcs[i].getLinearDY(t);
            }
        }
        return Double.NaN;
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double[] getTimePoints() {
        return this.mTime;
    }

    public ArcCurveFit(int[] arcModes, double[] time, double[][] y) {
        this.mTime = time;
        this.mArcs = new Arc[(time.length - 1)];
        int mode = 1;
        int last = 1;
        for (int i = 0; i < this.mArcs.length; i++) {
            switch (arcModes[i]) {
                case 0:
                    mode = 3;
                    break;
                case 1:
                    mode = 1;
                    last = 1;
                    break;
                case 2:
                    mode = 2;
                    last = 2;
                    break;
                case 3:
                    mode = last == 1 ? 2 : 1;
                    last = mode;
                    break;
            }
            this.mArcs[i] = new Arc(mode, time[i], time[i + 1], y[i][0], y[i][1], y[i + 1][0], y[i + 1][1]);
        }
    }

    private static class Arc {
        private static final double EPSILON = 0.001d;
        private static final String TAG = "Arc";
        private static double[] ourPercent = new double[91];
        boolean linear = false;
        double mArcDistance;
        double mArcVelocity;
        double mEllipseA;
        double mEllipseB;
        double mEllipseCenterX;
        double mEllipseCenterY;
        double[] mLut;
        double mOneOverDeltaTime;
        double mTime1;
        double mTime2;
        double mTmpCosAngle;
        double mTmpSinAngle;
        boolean mVertical;
        double mX1;
        double mX2;
        double mY1;
        double mY2;

        Arc(int mode, double t1, double t2, double x1, double y1, double x2, double y2) {
            double d;
            double d2;
            this.mVertical = mode == 1;
            this.mTime1 = t1;
            this.mTime2 = t2;
            this.mOneOverDeltaTime = 1.0d / (this.mTime2 - this.mTime1);
            if (3 == mode) {
                this.linear = true;
            }
            double dx = x2 - x1;
            double dy = y2 - y1;
            if (this.linear || Math.abs(dx) < EPSILON || Math.abs(dy) < EPSILON) {
                this.linear = true;
                this.mX1 = x1;
                this.mX2 = x2;
                this.mY1 = y1;
                this.mY2 = y2;
                this.mArcDistance = Math.hypot(dy, dx);
                this.mArcVelocity = this.mArcDistance * this.mOneOverDeltaTime;
                this.mEllipseCenterX = dx / (this.mTime2 - this.mTime1);
                this.mEllipseCenterY = dy / (this.mTime2 - this.mTime1);
                return;
            }
            this.mLut = new double[101];
            this.mEllipseA = ((double) (this.mVertical ? -1 : 1)) * dx;
            this.mEllipseB = ((double) (this.mVertical ? 1 : -1)) * dy;
            if (this.mVertical) {
                d = x2;
            } else {
                d = x1;
            }
            this.mEllipseCenterX = d;
            if (this.mVertical) {
                d2 = y1;
            } else {
                d2 = y2;
            }
            this.mEllipseCenterY = d2;
            buildTable(x1, y1, x2, y2);
            this.mArcVelocity = this.mArcDistance * this.mOneOverDeltaTime;
        }

        /* access modifiers changed from: package-private */
        public void setPoint(double time) {
            double angle = 1.5707963267948966d * lookup((this.mVertical ? this.mTime2 - time : time - this.mTime1) * this.mOneOverDeltaTime);
            this.mTmpSinAngle = Math.sin(angle);
            this.mTmpCosAngle = Math.cos(angle);
        }

        /* access modifiers changed from: package-private */
        public double getX() {
            return this.mEllipseCenterX + (this.mEllipseA * this.mTmpSinAngle);
        }

        /* access modifiers changed from: package-private */
        public double getY() {
            return this.mEllipseCenterY + (this.mEllipseB * this.mTmpCosAngle);
        }

        /* access modifiers changed from: package-private */
        public double getDX() {
            double vx = this.mEllipseA * this.mTmpCosAngle;
            double norm = this.mArcVelocity / Math.hypot(vx, (-this.mEllipseB) * this.mTmpSinAngle);
            return this.mVertical ? (-vx) * norm : vx * norm;
        }

        /* access modifiers changed from: package-private */
        public double getDY() {
            double vx = this.mEllipseA * this.mTmpCosAngle;
            double vy = (-this.mEllipseB) * this.mTmpSinAngle;
            double norm = this.mArcVelocity / Math.hypot(vx, vy);
            return this.mVertical ? (-vy) * norm : vy * norm;
        }

        public double getLinearX(double t) {
            return this.mX1 + ((this.mX2 - this.mX1) * (t - this.mTime1) * this.mOneOverDeltaTime);
        }

        public double getLinearY(double t) {
            return this.mY1 + ((this.mY2 - this.mY1) * (t - this.mTime1) * this.mOneOverDeltaTime);
        }

        public double getLinearDX(double t) {
            return this.mEllipseCenterX;
        }

        public double getLinearDY(double t) {
            return this.mEllipseCenterY;
        }

        /* access modifiers changed from: package-private */
        public double lookup(double v) {
            if (v <= 0.0d) {
                return 0.0d;
            }
            if (v >= 1.0d) {
                return 1.0d;
            }
            double pos = v * ((double) (this.mLut.length - 1));
            int iv = (int) pos;
            return this.mLut[iv] + ((this.mLut[iv + 1] - this.mLut[iv]) * (pos - ((double) ((int) pos))));
        }

        private void buildTable(double x1, double y1, double x2, double y2) {
            double a = x2 - x1;
            double b = y1 - y2;
            double lx = 0.0d;
            double ly = 0.0d;
            double dist = 0.0d;
            for (int i = 0; i < ourPercent.length; i++) {
                double angle = Math.toRadians((90.0d * ((double) i)) / ((double) (ourPercent.length - 1)));
                double px = a * Math.sin(angle);
                double py = b * Math.cos(angle);
                if (i > 0) {
                    dist += Math.hypot(px - lx, py - ly);
                    ourPercent[i] = dist;
                }
                lx = px;
                ly = py;
            }
            this.mArcDistance = dist;
            for (int i2 = 0; i2 < ourPercent.length; i2++) {
                double[] dArr = ourPercent;
                dArr[i2] = dArr[i2] / dist;
            }
            for (int i3 = 0; i3 < this.mLut.length; i3++) {
                double pos = ((double) i3) / ((double) (this.mLut.length - 1));
                int index = Arrays.binarySearch(ourPercent, pos);
                if (index >= 0) {
                    this.mLut[i3] = (double) (index / (ourPercent.length - 1));
                } else if (index == -1) {
                    this.mLut[i3] = 0.0d;
                } else {
                    int p1 = (-index) - 2;
                    this.mLut[i3] = (((double) p1) + ((pos - ourPercent[p1]) / (ourPercent[(-index) - 1] - ourPercent[p1]))) / ((double) (ourPercent.length - 1));
                }
            }
        }
    }
}
