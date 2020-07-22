package androidx.constraintlayout.motion.utils;

import java.lang.reflect.Array;

public class MonotonicCurveFit extends CurveFit {
    private static final String TAG = "MonotonicCurveFit";
    private double[] mT;
    private double[][] mTangent;
    private double[][] mY;

    public MonotonicCurveFit(double[] time, double[][] y) {
        int N = time.length;
        int dim = y[0].length;
        double[][] slope = (double[][]) Array.newInstance(Double.TYPE, N - 1, dim);
        double[][] tangent = (double[][]) Array.newInstance(Double.TYPE, N, dim);
        for (int j = 0; j < dim; j++) {
            for (int i = 0; i < N - 1; i++) {
                slope[i][j] = (y[i + 1][j] - y[i][j]) / (time[i + 1] - time[i]);
                if (i == 0) {
                    tangent[i][j] = slope[i][j];
                } else {
                    tangent[i][j] = (slope[i - 1][j] + slope[i][j]) * 0.5d;
                }
            }
            tangent[N - 1][j] = slope[N - 2][j];
        }
        for (int i2 = 0; i2 < N - 1; i2++) {
            for (int j2 = 0; j2 < dim; j2++) {
                if (slope[i2][j2] == 0.0d) {
                    tangent[i2][j2] = 0.0d;
                    tangent[i2 + 1][j2] = 0.0d;
                } else {
                    double a = tangent[i2][j2] / slope[i2][j2];
                    double b = tangent[i2 + 1][j2] / slope[i2][j2];
                    double h = Math.hypot(a, b);
                    if (h > 9.0d) {
                        double t = 3.0d / h;
                        tangent[i2][j2] = t * a * slope[i2][j2];
                        tangent[i2 + 1][j2] = t * b * slope[i2][j2];
                    }
                }
            }
        }
        this.mT = time;
        this.mY = y;
        this.mTangent = tangent;
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double t, double[] v) {
        int n = this.mT.length;
        int dim = this.mY[0].length;
        if (t <= this.mT[0]) {
            for (int j = 0; j < dim; j++) {
                v[j] = this.mY[0][j];
            }
        } else if (t >= this.mT[n - 1]) {
            for (int j2 = 0; j2 < dim; j2++) {
                v[j2] = this.mY[n - 1][j2];
            }
        } else {
            for (int i = 0; i < n - 1; i++) {
                if (t == this.mT[i]) {
                    for (int j3 = 0; j3 < dim; j3++) {
                        v[j3] = this.mY[i][j3];
                    }
                }
                if (t < this.mT[i + 1]) {
                    double h = this.mT[i + 1] - this.mT[i];
                    double x = (t - this.mT[i]) / h;
                    for (int j4 = 0; j4 < dim; j4++) {
                        v[j4] = interpolate(h, x, this.mY[i][j4], this.mY[i + 1][j4], this.mTangent[i][j4], this.mTangent[i + 1][j4]);
                    }
                    return;
                }
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getPos(double t, float[] v) {
        int n = this.mT.length;
        int dim = this.mY[0].length;
        if (t <= this.mT[0]) {
            for (int j = 0; j < dim; j++) {
                v[j] = (float) this.mY[0][j];
            }
        } else if (t >= this.mT[n - 1]) {
            for (int j2 = 0; j2 < dim; j2++) {
                v[j2] = (float) this.mY[n - 1][j2];
            }
        } else {
            for (int i = 0; i < n - 1; i++) {
                if (t == this.mT[i]) {
                    for (int j3 = 0; j3 < dim; j3++) {
                        v[j3] = (float) this.mY[i][j3];
                    }
                }
                if (t < this.mT[i + 1]) {
                    double h = this.mT[i + 1] - this.mT[i];
                    double x = (t - this.mT[i]) / h;
                    for (int j4 = 0; j4 < dim; j4++) {
                        v[j4] = (float) interpolate(h, x, this.mY[i][j4], this.mY[i + 1][j4], this.mTangent[i][j4], this.mTangent[i + 1][j4]);
                    }
                    return;
                }
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getPos(double t, int j) {
        int n = this.mT.length;
        if (t <= this.mT[0]) {
            return this.mY[0][j];
        }
        if (t >= this.mT[n - 1]) {
            return this.mY[n - 1][j];
        }
        for (int i = 0; i < n - 1; i++) {
            if (t == this.mT[i]) {
                return this.mY[i][j];
            }
            if (t < this.mT[i + 1]) {
                double h = this.mT[i + 1] - this.mT[i];
                return interpolate(h, (t - this.mT[i]) / h, this.mY[i][j], this.mY[i + 1][j], this.mTangent[i][j], this.mTangent[i + 1][j]);
            }
        }
        return 0.0d;
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public void getSlope(double t, double[] v) {
        int n = this.mT.length;
        int dim = this.mY[0].length;
        if (t <= this.mT[0]) {
            t = this.mT[0];
        } else if (t >= this.mT[n - 1]) {
            t = this.mT[n - 1];
        }
        for (int i = 0; i < n - 1; i++) {
            if (t <= this.mT[i + 1]) {
                double h = this.mT[i + 1] - this.mT[i];
                double x = (t - this.mT[i]) / h;
                for (int j = 0; j < dim; j++) {
                    v[j] = diff(h, x, this.mY[i][j], this.mY[i + 1][j], this.mTangent[i][j], this.mTangent[i + 1][j]) / h;
                }
                return;
            }
        }
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double getSlope(double t, int j) {
        int n = this.mT.length;
        if (t < this.mT[0]) {
            t = this.mT[0];
        } else if (t >= this.mT[n - 1]) {
            t = this.mT[n - 1];
        }
        for (int i = 0; i < n - 1; i++) {
            if (t <= this.mT[i + 1]) {
                double h = this.mT[i + 1] - this.mT[i];
                return diff(h, (t - this.mT[i]) / h, this.mY[i][j], this.mY[i + 1][j], this.mTangent[i][j], this.mTangent[i + 1][j]) / h;
            }
        }
        return 0.0d;
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double[] getTimePoints() {
        return this.mT;
    }

    private static double interpolate(double h, double x, double y1, double y2, double t1, double t2) {
        double x2 = x * x;
        double x3 = x2 * x;
        return ((((((((((-2.0d * x3) * y2) + ((3.0d * x2) * y2)) + ((2.0d * x3) * y1)) - ((3.0d * x2) * y1)) + y1) + ((h * t2) * x3)) + ((h * t1) * x3)) - ((h * t2) * x2)) - (((2.0d * h) * t1) * x2)) + (h * t1 * x);
    }

    private static double diff(double h, double x, double y1, double y2, double t1, double t2) {
        double x2 = x * x;
        return (((((((((-6.0d * x2) * y2) + ((6.0d * x) * y2)) + ((6.0d * x2) * y1)) - ((6.0d * x) * y1)) + (((3.0d * h) * t2) * x2)) + (((3.0d * h) * t1) * x2)) - (((2.0d * h) * t2) * x)) - (((4.0d * h) * t1) * x)) + (h * t1);
    }
}
