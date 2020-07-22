package androidx.constraintlayout.widget;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ConstraintProperties {
    public static final int BASELINE = 5;
    public static final int BOTTOM = 4;
    public static final int END = 7;
    public static final int LEFT = 1;
    public static final int MATCH_CONSTRAINT = 0;
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    public static final int PARENT_ID = 0;
    public static final int RIGHT = 2;
    public static final int START = 6;
    public static final int TOP = 3;
    public static final int UNSET = -1;
    public static final int WRAP_CONTENT = -2;
    ConstraintLayout.LayoutParams mParams;
    View mView;

    public ConstraintProperties center(int firstID, int firstSide, int firstMargin, int secondId, int secondSide, int secondMargin, float bias) {
        if (firstMargin < 0) {
            throw new IllegalArgumentException("margin must be > 0");
        } else if (secondMargin < 0) {
            throw new IllegalArgumentException("margin must be > 0");
        } else if (bias <= 0.0f || bias > 1.0f) {
            throw new IllegalArgumentException("bias must be between 0 and 1 inclusive");
        } else {
            if (firstSide == 1 || firstSide == 2) {
                connect(1, firstID, firstSide, firstMargin);
                connect(2, secondId, secondSide, secondMargin);
                this.mParams.horizontalBias = bias;
            } else if (firstSide == 6 || firstSide == 7) {
                connect(6, firstID, firstSide, firstMargin);
                connect(7, secondId, secondSide, secondMargin);
                this.mParams.horizontalBias = bias;
            } else {
                connect(3, firstID, firstSide, firstMargin);
                connect(4, secondId, secondSide, secondMargin);
                this.mParams.verticalBias = bias;
            }
            return this;
        }
    }

    public ConstraintProperties centerHorizontally(int leftId, int leftSide, int leftMargin, int rightId, int rightSide, int rightMargin, float bias) {
        connect(1, leftId, leftSide, leftMargin);
        connect(2, rightId, rightSide, rightMargin);
        this.mParams.horizontalBias = bias;
        return this;
    }

    public ConstraintProperties centerHorizontallyRtl(int startId, int startSide, int startMargin, int endId, int endSide, int endMargin, float bias) {
        connect(6, startId, startSide, startMargin);
        connect(7, endId, endSide, endMargin);
        this.mParams.horizontalBias = bias;
        return this;
    }

    public ConstraintProperties centerVertically(int topId, int topSide, int topMargin, int bottomId, int bottomSide, int bottomMargin, float bias) {
        connect(3, topId, topSide, topMargin);
        connect(4, bottomId, bottomSide, bottomMargin);
        this.mParams.verticalBias = bias;
        return this;
    }

    public ConstraintProperties centerHorizontally(int toView) {
        if (toView == 0) {
            center(0, 1, 0, 0, 2, 0, 0.5f);
        } else {
            center(toView, 2, 0, toView, 1, 0, 0.5f);
        }
        return this;
    }

    public ConstraintProperties centerHorizontallyRtl(int toView) {
        if (toView == 0) {
            center(0, 6, 0, 0, 7, 0, 0.5f);
        } else {
            center(toView, 7, 0, toView, 6, 0, 0.5f);
        }
        return this;
    }

    public ConstraintProperties centerVertically(int toView) {
        if (toView == 0) {
            center(0, 3, 0, 0, 4, 0, 0.5f);
        } else {
            center(toView, 4, 0, toView, 3, 0, 0.5f);
        }
        return this;
    }

    public ConstraintProperties removeConstraints(int anchor) {
        switch (anchor) {
            case 1:
                ConstraintLayout.LayoutParams layoutParams = this.mParams;
                ConstraintLayout.LayoutParams layoutParams2 = this.mParams;
                layoutParams.leftToRight = -1;
                ConstraintLayout.LayoutParams layoutParams3 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams4 = this.mParams;
                layoutParams3.leftToLeft = -1;
                ConstraintLayout.LayoutParams layoutParams5 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams6 = this.mParams;
                layoutParams5.leftMargin = -1;
                ConstraintLayout.LayoutParams layoutParams7 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams8 = this.mParams;
                layoutParams7.goneLeftMargin = -1;
                break;
            case 2:
                ConstraintLayout.LayoutParams layoutParams9 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams10 = this.mParams;
                layoutParams9.rightToRight = -1;
                ConstraintLayout.LayoutParams layoutParams11 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams12 = this.mParams;
                layoutParams11.rightToLeft = -1;
                ConstraintLayout.LayoutParams layoutParams13 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams14 = this.mParams;
                layoutParams13.rightMargin = -1;
                ConstraintLayout.LayoutParams layoutParams15 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams16 = this.mParams;
                layoutParams15.goneRightMargin = -1;
                break;
            case 3:
                ConstraintLayout.LayoutParams layoutParams17 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams18 = this.mParams;
                layoutParams17.topToBottom = -1;
                ConstraintLayout.LayoutParams layoutParams19 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams20 = this.mParams;
                layoutParams19.topToTop = -1;
                ConstraintLayout.LayoutParams layoutParams21 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams22 = this.mParams;
                layoutParams21.topMargin = -1;
                ConstraintLayout.LayoutParams layoutParams23 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams24 = this.mParams;
                layoutParams23.goneTopMargin = -1;
                break;
            case 4:
                ConstraintLayout.LayoutParams layoutParams25 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams26 = this.mParams;
                layoutParams25.bottomToTop = -1;
                ConstraintLayout.LayoutParams layoutParams27 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams28 = this.mParams;
                layoutParams27.bottomToBottom = -1;
                ConstraintLayout.LayoutParams layoutParams29 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams30 = this.mParams;
                layoutParams29.bottomMargin = -1;
                ConstraintLayout.LayoutParams layoutParams31 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams32 = this.mParams;
                layoutParams31.goneBottomMargin = -1;
                break;
            case 5:
                ConstraintLayout.LayoutParams layoutParams33 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams34 = this.mParams;
                layoutParams33.baselineToBaseline = -1;
                break;
            case 6:
                ConstraintLayout.LayoutParams layoutParams35 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams36 = this.mParams;
                layoutParams35.startToEnd = -1;
                ConstraintLayout.LayoutParams layoutParams37 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams38 = this.mParams;
                layoutParams37.startToStart = -1;
                ConstraintLayout.LayoutParams layoutParams39 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams40 = this.mParams;
                layoutParams39.setMarginStart(-1);
                ConstraintLayout.LayoutParams layoutParams41 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams42 = this.mParams;
                layoutParams41.goneStartMargin = -1;
                break;
            case 7:
                ConstraintLayout.LayoutParams layoutParams43 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams44 = this.mParams;
                layoutParams43.endToStart = -1;
                ConstraintLayout.LayoutParams layoutParams45 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams46 = this.mParams;
                layoutParams45.endToEnd = -1;
                ConstraintLayout.LayoutParams layoutParams47 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams48 = this.mParams;
                layoutParams47.setMarginEnd(-1);
                ConstraintLayout.LayoutParams layoutParams49 = this.mParams;
                ConstraintLayout.LayoutParams layoutParams50 = this.mParams;
                layoutParams49.goneEndMargin = -1;
                break;
            default:
                throw new IllegalArgumentException("unknown constraint");
        }
        return this;
    }

    public ConstraintProperties margin(int anchor, int value) {
        switch (anchor) {
            case 1:
                this.mParams.leftMargin = value;
                break;
            case 2:
                this.mParams.rightMargin = value;
                break;
            case 3:
                this.mParams.topMargin = value;
                break;
            case 4:
                this.mParams.bottomMargin = value;
                break;
            case 5:
                throw new IllegalArgumentException("baseline does not support margins");
            case 6:
                this.mParams.setMarginStart(value);
                break;
            case 7:
                this.mParams.setMarginEnd(value);
                break;
            default:
                throw new IllegalArgumentException("unknown constraint");
        }
        return this;
    }

    public ConstraintProperties goneMargin(int anchor, int value) {
        switch (anchor) {
            case 1:
                this.mParams.goneLeftMargin = value;
                break;
            case 2:
                this.mParams.goneRightMargin = value;
                break;
            case 3:
                this.mParams.goneTopMargin = value;
                break;
            case 4:
                this.mParams.goneBottomMargin = value;
                break;
            case 5:
                throw new IllegalArgumentException("baseline does not support margins");
            case 6:
                this.mParams.goneStartMargin = value;
                break;
            case 7:
                this.mParams.goneEndMargin = value;
                break;
            default:
                throw new IllegalArgumentException("unknown constraint");
        }
        return this;
    }

    public ConstraintProperties horizontalBias(float bias) {
        this.mParams.horizontalBias = bias;
        return this;
    }

    public ConstraintProperties verticalBias(float bias) {
        this.mParams.verticalBias = bias;
        return this;
    }

    public ConstraintProperties dimensionRatio(String ratio) {
        this.mParams.dimensionRatio = ratio;
        return this;
    }

    public ConstraintProperties visibility(int visibility) {
        this.mView.setVisibility(visibility);
        return this;
    }

    public ConstraintProperties alpha(float alpha) {
        this.mView.setAlpha(alpha);
        return this;
    }

    public ConstraintProperties elevation(float elevation) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.mView.setElevation(elevation);
        }
        return this;
    }

    public ConstraintProperties rotation(float rotation) {
        this.mView.setRotation(rotation);
        return this;
    }

    public ConstraintProperties rotationX(float rotationX) {
        this.mView.setRotationX(rotationX);
        return this;
    }

    public ConstraintProperties rotationY(float rotationY) {
        this.mView.setRotationY(rotationY);
        return this;
    }

    public ConstraintProperties scaleX(float scaleX) {
        this.mView.setScaleY(scaleX);
        return this;
    }

    public ConstraintProperties scaleY(float scaleY) {
        return this;
    }

    public ConstraintProperties transformPivotX(float transformPivotX) {
        this.mView.setPivotX(transformPivotX);
        return this;
    }

    public ConstraintProperties transformPivotY(float transformPivotY) {
        this.mView.setPivotY(transformPivotY);
        return this;
    }

    public ConstraintProperties transformPivot(float transformPivotX, float transformPivotY) {
        this.mView.setPivotX(transformPivotX);
        this.mView.setPivotY(transformPivotY);
        return this;
    }

    public ConstraintProperties translationX(float translationX) {
        this.mView.setTranslationX(translationX);
        return this;
    }

    public ConstraintProperties translationY(float translationY) {
        this.mView.setTranslationY(translationY);
        return this;
    }

    public ConstraintProperties translation(float translationX, float translationY) {
        this.mView.setTranslationX(translationX);
        this.mView.setTranslationY(translationY);
        return this;
    }

    public ConstraintProperties translationZ(float translationZ) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.mView.setTranslationZ(translationZ);
        }
        return this;
    }

    public ConstraintProperties constrainHeight(int height) {
        this.mParams.height = height;
        return this;
    }

    public ConstraintProperties constrainWidth(int width) {
        this.mParams.width = width;
        return this;
    }

    public ConstraintProperties constrainMaxHeight(int height) {
        this.mParams.matchConstraintMaxHeight = height;
        return this;
    }

    public ConstraintProperties constrainMaxWidth(int width) {
        this.mParams.matchConstraintMaxWidth = width;
        return this;
    }

    public ConstraintProperties constrainMinHeight(int height) {
        this.mParams.matchConstraintMinHeight = height;
        return this;
    }

    public ConstraintProperties constrainMinWidth(int width) {
        this.mParams.matchConstraintMinWidth = width;
        return this;
    }

    public ConstraintProperties constrainDefaultHeight(int height) {
        this.mParams.matchConstraintDefaultHeight = height;
        return this;
    }

    public ConstraintProperties constrainDefaultWidth(int width) {
        this.mParams.matchConstraintDefaultWidth = width;
        return this;
    }

    public ConstraintProperties horizontalWeight(float weight) {
        this.mParams.horizontalWeight = weight;
        return this;
    }

    public ConstraintProperties verticalWeight(float weight) {
        this.mParams.verticalWeight = weight;
        return this;
    }

    public ConstraintProperties horizontalChainStyle(int chainStyle) {
        this.mParams.horizontalChainStyle = chainStyle;
        return this;
    }

    public ConstraintProperties verticalChainStyle(int chainStyle) {
        this.mParams.verticalChainStyle = chainStyle;
        return this;
    }

    public ConstraintProperties addToHorizontalChain(int leftId, int rightId) {
        int i;
        connect(1, leftId, leftId == 0 ? 1 : 2, 0);
        if (rightId == 0) {
            i = 2;
        } else {
            i = 1;
        }
        connect(2, rightId, i, 0);
        if (leftId != 0) {
            new ConstraintProperties(((ViewGroup) this.mView.getParent()).findViewById(leftId)).connect(2, this.mView.getId(), 1, 0);
        }
        if (rightId != 0) {
            new ConstraintProperties(((ViewGroup) this.mView.getParent()).findViewById(rightId)).connect(1, this.mView.getId(), 2, 0);
        }
        return this;
    }

    public ConstraintProperties addToHorizontalChainRTL(int leftId, int rightId) {
        int i;
        connect(6, leftId, leftId == 0 ? 6 : 7, 0);
        if (rightId == 0) {
            i = 7;
        } else {
            i = 6;
        }
        connect(7, rightId, i, 0);
        if (leftId != 0) {
            new ConstraintProperties(((ViewGroup) this.mView.getParent()).findViewById(leftId)).connect(7, this.mView.getId(), 6, 0);
        }
        if (rightId != 0) {
            new ConstraintProperties(((ViewGroup) this.mView.getParent()).findViewById(rightId)).connect(6, this.mView.getId(), 7, 0);
        }
        return this;
    }

    public ConstraintProperties addToVerticalChain(int topId, int bottomId) {
        int i;
        connect(3, topId, topId == 0 ? 3 : 4, 0);
        if (bottomId == 0) {
            i = 4;
        } else {
            i = 3;
        }
        connect(4, bottomId, i, 0);
        if (topId != 0) {
            new ConstraintProperties(((ViewGroup) this.mView.getParent()).findViewById(topId)).connect(4, this.mView.getId(), 3, 0);
        }
        if (bottomId != 0) {
            new ConstraintProperties(((ViewGroup) this.mView.getParent()).findViewById(bottomId)).connect(3, this.mView.getId(), 4, 0);
        }
        return this;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0055, code lost:
        if (r0 != -1) goto L_0x0057;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0012, code lost:
        if (r0 != -1) goto L_0x0014;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.constraintlayout.widget.ConstraintProperties removeFromVerticalChain() {
        /*
            r12 = this;
            r11 = 0
            r10 = 4
            r9 = 3
            r8 = -1
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            int r3 = r6.topToBottom
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            int r0 = r6.bottomToTop
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            if (r3 != r8) goto L_0x0014
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            if (r0 == r8) goto L_0x0048
        L_0x0014:
            android.view.View r6 = r12.mView
            android.view.ViewParent r6 = r6.getParent()
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            android.view.View r5 = r6.findViewById(r3)
            androidx.constraintlayout.widget.ConstraintProperties r4 = new androidx.constraintlayout.widget.ConstraintProperties
            r4.<init>(r5)
            android.view.View r6 = r12.mView
            android.view.ViewParent r6 = r6.getParent()
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            android.view.ViewGroup r6 = (android.view.ViewGroup) r6
            android.view.View r2 = r6.findViewById(r0)
            androidx.constraintlayout.widget.ConstraintProperties r1 = new androidx.constraintlayout.widget.ConstraintProperties
            r1.<init>(r2)
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            if (r3 == r8) goto L_0x004f
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            if (r0 == r8) goto L_0x004f
            r4.connect(r10, r0, r9, r11)
            r1.connect(r9, r3, r10, r11)
        L_0x0048:
            r12.removeConstraints(r9)
            r12.removeConstraints(r10)
            return r12
        L_0x004f:
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            if (r3 != r8) goto L_0x0057
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            if (r0 == r8) goto L_0x0048
        L_0x0057:
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            int r6 = r6.bottomToBottom
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r7 = r12.mParams
            if (r6 == r8) goto L_0x0067
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            int r6 = r6.bottomToBottom
            r4.connect(r10, r6, r10, r11)
            goto L_0x0048
        L_0x0067:
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            int r6 = r6.topToTop
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r7 = r12.mParams
            if (r6 == r8) goto L_0x0048
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r6 = r12.mParams
            int r6 = r6.topToTop
            r1.connect(r9, r6, r9, r11)
            goto L_0x0048
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintProperties.removeFromVerticalChain():androidx.constraintlayout.widget.ConstraintProperties");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0077, code lost:
        if (r7 != -1) goto L_0x0079;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00cb, code lost:
        if (r1 != -1) goto L_0x00cd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x012b, code lost:
        if (r1 != -1) goto L_0x012d;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.constraintlayout.widget.ConstraintProperties removeFromHorizontalChain() {
        /*
            r17 = this;
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r4 = r13.leftToRight
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r7 = r13.rightToLeft
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r4 != r13) goto L_0x001a
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r7 == r13) goto L_0x00b3
        L_0x001a:
            r0 = r17
            android.view.View r13 = r0.mView
            android.view.ViewParent r13 = r13.getParent()
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.View r6 = r13.findViewById(r4)
            androidx.constraintlayout.widget.ConstraintProperties r5 = new androidx.constraintlayout.widget.ConstraintProperties
            r5.<init>(r6)
            r0 = r17
            android.view.View r13 = r0.mView
            android.view.ViewParent r13 = r13.getParent()
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.View r9 = r13.findViewById(r7)
            androidx.constraintlayout.widget.ConstraintProperties r8 = new androidx.constraintlayout.widget.ConstraintProperties
            r8.<init>(r9)
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r4 == r13) goto L_0x006b
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r7 == r13) goto L_0x006b
            r13 = 2
            r14 = 1
            r15 = 0
            r5.connect(r13, r7, r14, r15)
            r13 = 1
            r14 = 2
            r15 = 0
            r8.connect(r13, r4, r14, r15)
        L_0x005e:
            r13 = 1
            r0 = r17
            r0.removeConstraints(r13)
            r13 = 2
            r0 = r17
            r0.removeConstraints(r13)
        L_0x006a:
            return r17
        L_0x006b:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r4 != r13) goto L_0x0079
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r7 == r13) goto L_0x005e
        L_0x0079:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r13 = r13.rightToRight
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            r14 = -1
            if (r13 == r14) goto L_0x0096
            r13 = 2
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            int r14 = r14.rightToRight
            r15 = 2
            r16 = 0
            r0 = r16
            r5.connect(r13, r14, r15, r0)
            goto L_0x005e
        L_0x0096:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r13 = r13.leftToLeft
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            r14 = -1
            if (r13 == r14) goto L_0x005e
            r13 = 1
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            int r14 = r14.leftToLeft
            r15 = 1
            r16 = 0
            r0 = r16
            r8.connect(r13, r14, r15, r0)
            goto L_0x005e
        L_0x00b3:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r10 = r13.startToEnd
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r1 = r13.endToStart
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r10 != r13) goto L_0x00cd
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r1 == r13) goto L_0x0111
        L_0x00cd:
            r0 = r17
            android.view.View r13 = r0.mView
            android.view.ViewParent r13 = r13.getParent()
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.View r12 = r13.findViewById(r10)
            androidx.constraintlayout.widget.ConstraintProperties r11 = new androidx.constraintlayout.widget.ConstraintProperties
            r11.<init>(r12)
            r0 = r17
            android.view.View r13 = r0.mView
            android.view.ViewParent r13 = r13.getParent()
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.ViewGroup r13 = (android.view.ViewGroup) r13
            android.view.View r3 = r13.findViewById(r1)
            androidx.constraintlayout.widget.ConstraintProperties r2 = new androidx.constraintlayout.widget.ConstraintProperties
            r2.<init>(r3)
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r10 == r13) goto L_0x011f
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r1 == r13) goto L_0x011f
            r13 = 7
            r14 = 6
            r15 = 0
            r11.connect(r13, r1, r14, r15)
            r13 = 6
            r14 = 7
            r15 = 0
            r2.connect(r13, r4, r14, r15)
        L_0x0111:
            r13 = 6
            r0 = r17
            r0.removeConstraints(r13)
            r13 = 7
            r0 = r17
            r0.removeConstraints(r13)
            goto L_0x006a
        L_0x011f:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r4 != r13) goto L_0x012d
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            r13 = -1
            if (r1 == r13) goto L_0x0111
        L_0x012d:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r13 = r13.rightToRight
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            r14 = -1
            if (r13 == r14) goto L_0x014a
            r13 = 7
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            int r14 = r14.rightToRight
            r15 = 7
            r16 = 0
            r0 = r16
            r11.connect(r13, r14, r15, r0)
            goto L_0x0111
        L_0x014a:
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r13 = r0.mParams
            int r13 = r13.leftToLeft
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            r14 = -1
            if (r13 == r14) goto L_0x0111
            r13 = 6
            r0 = r17
            androidx.constraintlayout.widget.ConstraintLayout$LayoutParams r14 = r0.mParams
            int r14 = r14.leftToLeft
            r15 = 6
            r16 = 0
            r0 = r16
            r2.connect(r13, r14, r15, r0)
            goto L_0x0111
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintProperties.removeFromHorizontalChain():androidx.constraintlayout.widget.ConstraintProperties");
    }

    public ConstraintProperties connect(int startSide, int endID, int endSide, int margin) {
        switch (startSide) {
            case 1:
                if (endSide == 1) {
                    this.mParams.leftToLeft = endID;
                    ConstraintLayout.LayoutParams layoutParams = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams2 = this.mParams;
                    layoutParams.leftToRight = -1;
                } else if (endSide == 2) {
                    this.mParams.leftToRight = endID;
                    ConstraintLayout.LayoutParams layoutParams3 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams4 = this.mParams;
                    layoutParams3.leftToLeft = -1;
                } else {
                    throw new IllegalArgumentException("Left to " + sideToString(endSide) + " undefined");
                }
                this.mParams.leftMargin = margin;
                break;
            case 2:
                if (endSide == 1) {
                    this.mParams.rightToLeft = endID;
                    ConstraintLayout.LayoutParams layoutParams5 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams6 = this.mParams;
                    layoutParams5.rightToRight = -1;
                } else if (endSide == 2) {
                    this.mParams.rightToRight = endID;
                    ConstraintLayout.LayoutParams layoutParams7 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams8 = this.mParams;
                    layoutParams7.rightToLeft = -1;
                } else {
                    throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
                }
                this.mParams.rightMargin = margin;
                break;
            case 3:
                if (endSide == 3) {
                    this.mParams.topToTop = endID;
                    ConstraintLayout.LayoutParams layoutParams9 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams10 = this.mParams;
                    layoutParams9.topToBottom = -1;
                    ConstraintLayout.LayoutParams layoutParams11 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams12 = this.mParams;
                    layoutParams11.baselineToBaseline = -1;
                } else if (endSide == 4) {
                    this.mParams.topToBottom = endID;
                    ConstraintLayout.LayoutParams layoutParams13 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams14 = this.mParams;
                    layoutParams13.topToTop = -1;
                    ConstraintLayout.LayoutParams layoutParams15 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams16 = this.mParams;
                    layoutParams15.baselineToBaseline = -1;
                } else {
                    throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
                }
                this.mParams.topMargin = margin;
                break;
            case 4:
                if (endSide == 4) {
                    this.mParams.bottomToBottom = endID;
                    ConstraintLayout.LayoutParams layoutParams17 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams18 = this.mParams;
                    layoutParams17.bottomToTop = -1;
                    ConstraintLayout.LayoutParams layoutParams19 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams20 = this.mParams;
                    layoutParams19.baselineToBaseline = -1;
                } else if (endSide == 3) {
                    this.mParams.bottomToTop = endID;
                    ConstraintLayout.LayoutParams layoutParams21 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams22 = this.mParams;
                    layoutParams21.bottomToBottom = -1;
                    ConstraintLayout.LayoutParams layoutParams23 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams24 = this.mParams;
                    layoutParams23.baselineToBaseline = -1;
                } else {
                    throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
                }
                this.mParams.bottomMargin = margin;
                break;
            case 5:
                if (endSide == 5) {
                    this.mParams.baselineToBaseline = endID;
                    ConstraintLayout.LayoutParams layoutParams25 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams26 = this.mParams;
                    layoutParams25.bottomToBottom = -1;
                    ConstraintLayout.LayoutParams layoutParams27 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams28 = this.mParams;
                    layoutParams27.bottomToTop = -1;
                    ConstraintLayout.LayoutParams layoutParams29 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams30 = this.mParams;
                    layoutParams29.topToTop = -1;
                    ConstraintLayout.LayoutParams layoutParams31 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams32 = this.mParams;
                    layoutParams31.topToBottom = -1;
                    break;
                } else {
                    throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
                }
            case 6:
                if (endSide == 6) {
                    this.mParams.startToStart = endID;
                    ConstraintLayout.LayoutParams layoutParams33 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams34 = this.mParams;
                    layoutParams33.startToEnd = -1;
                } else if (endSide == 7) {
                    this.mParams.startToEnd = endID;
                    ConstraintLayout.LayoutParams layoutParams35 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams36 = this.mParams;
                    layoutParams35.startToStart = -1;
                } else {
                    throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
                }
                if (Build.VERSION.SDK_INT >= 17) {
                    this.mParams.setMarginStart(margin);
                    break;
                }
                break;
            case 7:
                if (endSide == 7) {
                    this.mParams.endToEnd = endID;
                    ConstraintLayout.LayoutParams layoutParams37 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams38 = this.mParams;
                    layoutParams37.endToStart = -1;
                } else if (endSide == 6) {
                    this.mParams.endToStart = endID;
                    ConstraintLayout.LayoutParams layoutParams39 = this.mParams;
                    ConstraintLayout.LayoutParams layoutParams40 = this.mParams;
                    layoutParams39.endToEnd = -1;
                } else {
                    throw new IllegalArgumentException("right to " + sideToString(endSide) + " undefined");
                }
                if (Build.VERSION.SDK_INT >= 17) {
                    this.mParams.setMarginEnd(margin);
                    break;
                }
                break;
            default:
                throw new IllegalArgumentException(sideToString(startSide) + " to " + sideToString(endSide) + " unknown");
        }
        return this;
    }

    private String sideToString(int side) {
        switch (side) {
            case 1:
                return "left";
            case 2:
                return "right";
            case 3:
                return "top";
            case 4:
                return "bottom";
            case 5:
                return "baseline";
            case 6:
                return "start";
            case 7:
                return "end";
            default:
                return "undefined";
        }
    }

    public ConstraintProperties(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof ConstraintLayout.LayoutParams) {
            this.mParams = (ConstraintLayout.LayoutParams) params;
            this.mView = view;
            return;
        }
        throw new RuntimeException("Only children of ConstraintLayout.LayoutParams supported");
    }

    public void apply() {
    }
}
