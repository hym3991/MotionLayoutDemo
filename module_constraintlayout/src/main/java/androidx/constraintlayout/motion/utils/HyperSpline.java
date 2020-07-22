package androidx.constraintlayout.motion.utils;

import java.lang.reflect.Array;

public class HyperSpline {
    double[][] mCtl;
    Cubic[][] mCurve;
    double[] mCurveLength;
    int mDimensionality;
    int mPoints;
    double mTotalLength;

    public HyperSpline(double[][] points) {
        setup(points);
    }

    public HyperSpline() {
    }

    public void setup(double[][] points) {
        this.mDimensionality = points[0].length;
        this.mPoints = points.length;
        this.mCtl = (double[][]) Array.newInstance(Double.TYPE, this.mDimensionality, this.mPoints);
        this.mCurve = new Cubic[this.mDimensionality][];
        for (int d = 0; d < this.mDimensionality; d++) {
            for (int p = 0; p < this.mPoints; p++) {
                this.mCtl[d][p] = points[p][d];
            }
        }
        for (int d2 = 0; d2 < this.mDimensionality; d2++) {
            this.mCurve[d2] = calcNaturalCubic(this.mCtl[d2].length, this.mCtl[d2]);
        }
        this.mCurveLength = new double[(this.mPoints - 1)];
        this.mTotalLength = 0.0d;
        Cubic[] temp = new Cubic[this.mDimensionality];
        for (int p2 = 0; p2 < this.mCurveLength.length; p2++) {
            for (int d3 = 0; d3 < this.mDimensionality; d3++) {
                temp[d3] = this.mCurve[d3][p2];
            }
            double d4 = this.mTotalLength;
            double[] dArr = this.mCurveLength;
            double approxLength = approxLength(temp);
            dArr[p2] = approxLength;
            this.mTotalLength = d4 + approxLength;
        }
    }

    public void getVelocity(double p, double[] v) {
        double pos = p * this.mTotalLength;
        int k = 0;
        while (k < this.mCurveLength.length - 1 && this.mCurveLength[k] < pos) {
            pos -= this.mCurveLength[k];
            k++;
        }
        for (int i = 0; i < v.length; i++) {
            v[i] = this.mCurve[i][k].vel(pos / this.mCurveLength[k]);
        }
    }

    public void getPos(double p, double[] x) {
        double pos = p * this.mTotalLength;
        int k = 0;
        while (k < this.mCurveLength.length - 1 && this.mCurveLength[k] < pos) {
            pos -= this.mCurveLength[k];
            k++;
        }
        for (int i = 0; i < x.length; i++) {
            x[i] = this.mCurve[i][k].eval(pos / this.mCurveLength[k]);
        }
    }

    public void getPos(double p, float[] x) {
        double pos = p * this.mTotalLength;
        int k = 0;
        while (k < this.mCurveLength.length - 1 && this.mCurveLength[k] < pos) {
            pos -= this.mCurveLength[k];
            k++;
        }
        for (int i = 0; i < x.length; i++) {
            x[i] = (float) this.mCurve[i][k].eval(pos / this.mCurveLength[k]);
        }
    }

    public double getPos(double p, int splineNumber) {
        double pos = p * this.mTotalLength;
        int k = 0;
        while (k < this.mCurveLength.length - 1 && this.mCurveLength[k] < pos) {
            pos -= this.mCurveLength[k];
            k++;
        }
        return this.mCurve[splineNumber][k].eval(pos / this.mCurveLength[k]);
    }

    public double approxLength(Cubic[] curve) {
        double sum = 0.0d;
        int length = curve.length;
        double[] old = new double[curve.length];
        for (double i = 0.0d; i < 1.0d; i += 0.1d) {
            double s = 0.0d;
            for (int j = 0; j < curve.length; j++) {
                double tmp = old[j];
                double eval = curve[j].eval(i);
                old[j] = eval;
                double tmp2 = tmp - eval;
                s += tmp2 * tmp2;
            }
            if (i > 0.0d) {
                sum += Math.sqrt(s);
            }
        }
        double s2 = 0.0d;
        for (int j2 = 0; j2 < curve.length; j2++) {
            double tmp3 = old[j2];
            double eval2 = curve[j2].eval(1.0d);
            old[j2] = eval2;
            double tmp4 = tmp3 - eval2;
            s2 += tmp4 * tmp4;
        }
        return sum + Math.sqrt(s2);
    }

    static Cubic[] calcNaturalCubic(int n, double[] x) {
        double[] gamma = new double[n];
        double[] delta = new double[n];
        double[] D = new double[n];
        int n2 = n - 1;
        gamma[0] = 0.5d;
        for (int i = 1; i < n2; i++) {
            gamma[i] = 1.0d / (4.0d - gamma[i - 1]);
        }
        gamma[n2] = 1.0d / (2.0d - gamma[n2 - 1]);
        delta[0] = 3.0d * (x[1] - x[0]) * gamma[0];
        for (int i2 = 1; i2 < n2; i2++) {
            delta[i2] = ((3.0d * (x[i2 + 1] - x[i2 - 1])) - delta[i2 - 1]) * gamma[i2];
        }
        delta[n2] = ((3.0d * (x[n2] - x[n2 - 1])) - delta[n2 - 1]) * gamma[n2];
        D[n2] = delta[n2];
        for (int i3 = n2 - 1; i3 >= 0; i3--) {
            D[i3] = delta[i3] - (gamma[i3] * D[i3 + 1]);
        }
        Cubic[] C = new Cubic[n2];
        for (int i4 = 0; i4 < n2; i4++) {
            C[i4] = new Cubic((double) ((float) x[i4]), D[i4], ((3.0d * (x[i4 + 1] - x[i4])) - (2.0d * D[i4])) - D[i4 + 1], (2.0d * (x[i4] - x[i4 + 1])) + D[i4] + D[i4 + 1]);
        }
        return C;
    }

    public static class Cubic {
        public static final double HALF = 0.5d;
        public static final double THIRD = 0.3333333333333333d;
        double mA;
        double mB;
        double mC;
        double mD;

        public Cubic(double a, double b, double c, double d) {
            this.mA = a;
            this.mB = b;
            this.mC = c;
            this.mD = d;
        }

        public double eval(double u) {
            return (((((this.mD * u) + this.mC) * u) + this.mB) * u) + this.mA;
        }

        public double vel(double v) {
            return (((this.mD * 0.3333333333333333d * v) + (this.mC * 0.5d)) * v) + this.mB;
        }
    }
}
