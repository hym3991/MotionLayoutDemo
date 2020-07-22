package androidx.constraintlayout.motion.utils;

public class LinearCurveFit extends CurveFit {
    private static final String TAG = "LinearCurveFit";
    private double[] mT;
    private double mTotalLength = Double.NaN;
    private double[][] mY;

    public LinearCurveFit(double[] time, double[][] y) {
        int length = time.length;
        int dim = y[0].length;
        this.mT = time;
        this.mY = y;
        if (dim > 2) {
            double sum = 0.0d;
            double lastx = 0.0d;
            double lasty = 0.0d;
            for (int i = 0; i < time.length; i++) {
                double px = y[i][0];
                double py = y[i][0];
                if (i > 0) {
                    sum += Math.hypot(px - lastx, py - lasty);
                }
                lastx = px;
                lasty = py;
            }
            this.mTotalLength = 0.0d;
        }
    }

    private double getLength2D(double t) {
        if (Double.isNaN(this.mTotalLength)) {
            return 0.0d;
        }
        int n = this.mT.length;
        if (t <= this.mT[0]) {
            return 0.0d;
        }
        if (t >= this.mT[n - 1]) {
            return this.mTotalLength;
        }
        double sum = 0.0d;
        double last_x = 0.0d;
        double last_y = 0.0d;
        for (int i = 0; i < n - 1; i++) {
            double px = this.mY[i][0];
            double py = this.mY[i][1];
            if (i > 0) {
                sum += Math.hypot(px - last_x, py - last_y);
            }
            last_x = px;
            last_y = py;
            if (t == this.mT[i]) {
                return sum;
            }
            if (t < this.mT[i + 1]) {
                double x = (t - this.mT[i]) / (this.mT[i + 1] - this.mT[i]);
                double x1 = this.mY[i][0];
                double x2 = this.mY[i + 1][0];
                return sum + Math.hypot(py - (((1.0d - x) * this.mY[i][1]) + (this.mY[i + 1][1] * x)), px - (((1.0d - x) * x1) + (x2 * x)));
            }
        }
        return 0.0d;
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
                    double x = (t - this.mT[i]) / (this.mT[i + 1] - this.mT[i]);
                    for (int j4 = 0; j4 < dim; j4++) {
                        v[j4] = ((1.0d - x) * this.mY[i][j4]) + (this.mY[i + 1][j4] * x);
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
                    double x = (t - this.mT[i]) / (this.mT[i + 1] - this.mT[i]);
                    for (int j4 = 0; j4 < dim; j4++) {
                        v[j4] = (float) (((1.0d - x) * this.mY[i][j4]) + (this.mY[i + 1][j4] * x));
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
                double x = (t - this.mT[i]) / (this.mT[i + 1] - this.mT[i]);
                return ((1.0d - x) * this.mY[i][j]) + (this.mY[i + 1][j] * x);
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
                double d = (t - this.mT[i]) / h;
                for (int j = 0; j < dim; j++) {
                    v[j] = (this.mY[i + 1][j] - this.mY[i][j]) / h;
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
                double d = (t - this.mT[i]) / h;
                return (this.mY[i + 1][j] - this.mY[i][j]) / h;
            }
        }
        return 0.0d;
    }

    @Override // androidx.constraintlayout.motion.utils.CurveFit
    public double[] getTimePoints() {
        return this.mT;
    }
}
