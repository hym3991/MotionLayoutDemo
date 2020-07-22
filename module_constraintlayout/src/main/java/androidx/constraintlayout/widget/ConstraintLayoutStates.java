package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;

import androidx.constraintlayout.R;

import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConstraintLayoutStates {
    private static final boolean DEBUG = false;
    public static final String TAG = "ConstraintLayoutStates";
    private final ConstraintLayout mConstraintLayout;
    private SparseArray<ConstraintSet> mConstraintSetMap = new SparseArray<>();
    private ConstraintsChangedListener mConstraintsChangedListener = null;
    int mCurrentConstraintNumber = -1;
    int mCurrentStateId = -1;
    ConstraintSet mDefaultConstraintSet;
    private SparseArray<State> mStateList = new SparseArray<>();

    ConstraintLayoutStates(Context context, ConstraintLayout layout, int resourceID) {
        this.mConstraintLayout = layout;
        load(context, resourceID);
    }

    public boolean needsToChange(int id, float width, float height) {
        if (this.mCurrentStateId != id) {
            return true;
        }
        State state = id == -1 ? this.mStateList.valueAt(0) : this.mStateList.get(this.mCurrentStateId);
        if (this.mCurrentConstraintNumber == -1 || !state.mVariants.get(this.mCurrentConstraintNumber).match(width, height)) {
            return this.mCurrentConstraintNumber != state.findMatch(width, height);
        }
        return false;
    }

    public void updateConstraints(int id, float width, float height) {
        ConstraintSet constraintSet;
        int cid;
        State state;
        int match;
        ConstraintSet constraintSet2;
        int cid2;
        if (this.mCurrentStateId == id) {
            if (id == -1) {
                state = this.mStateList.valueAt(0);
            } else {
                state = this.mStateList.get(this.mCurrentStateId);
            }
            if ((this.mCurrentConstraintNumber == -1 || !state.mVariants.get(this.mCurrentConstraintNumber).match(width, height)) && this.mCurrentConstraintNumber != (match = state.findMatch(width, height))) {
                if (match == -1) {
                    constraintSet2 = this.mDefaultConstraintSet;
                } else {
                    constraintSet2 = state.mVariants.get(match).mConstraintSet;
                }
                if (match == -1) {
                    cid2 = state.mConstraintID;
                } else {
                    cid2 = state.mVariants.get(match).mConstraintID;
                }
                if (constraintSet2 != null) {
                    this.mCurrentConstraintNumber = match;
                    if (this.mConstraintsChangedListener != null) {
                        this.mConstraintsChangedListener.preLayoutChange(-1, cid2);
                    }
                    constraintSet2.applyTo(this.mConstraintLayout);
                    if (this.mConstraintsChangedListener != null) {
                        this.mConstraintsChangedListener.postLayoutChange(-1, cid2);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        this.mCurrentStateId = id;
        State state2 = this.mStateList.get(this.mCurrentStateId);
        int match2 = state2.findMatch(width, height);
        if (match2 == -1) {
            constraintSet = state2.mConstraintSet;
        } else {
            constraintSet = state2.mVariants.get(match2).mConstraintSet;
        }
        if (match2 == -1) {
            cid = state2.mConstraintID;
        } else {
            cid = state2.mVariants.get(match2).mConstraintID;
        }
        if (constraintSet == null) {
            Log.v("ConstraintLayoutStates", "NO Constraint set found ! id=" + id + ", dim =" + width + ", " + height);
            return;
        }
        this.mCurrentConstraintNumber = match2;
        if (this.mConstraintsChangedListener != null) {
            this.mConstraintsChangedListener.preLayoutChange(id, cid);
        }
        constraintSet.applyTo(this.mConstraintLayout);
        if (this.mConstraintsChangedListener != null) {
            this.mConstraintsChangedListener.postLayoutChange(id, cid);
        }
    }

    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
    }

    static class State {
        int mConstraintID = -1;
        ConstraintSet mConstraintSet;
        int mId;
        ArrayList<Variant> mVariants = new ArrayList<>();

        public State(Context context, XmlPullParser parser) {
            TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.State);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.State_android_id) {
                    this.mId = a.getResourceId(attr, this.mId);
                } else if (attr == R.styleable.State_constraints) {
                    this.mConstraintID = a.getResourceId(attr, this.mConstraintID);
                    String type = context.getResources().getResourceTypeName(this.mConstraintID);
                    context.getResources().getResourceName(this.mConstraintID);
                    if ("layout".equals(type)) {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.clone(context, this.mConstraintID);
                    }
                }
            }
            a.recycle();
        }

        /* access modifiers changed from: package-private */
        public void add(Variant size) {
            this.mVariants.add(size);
        }

        public int findMatch(float width, float height) {
            for (int i = 0; i < this.mVariants.size(); i++) {
                if (this.mVariants.get(i).match(width, height)) {
                    return i;
                }
            }
            return -1;
        }
    }

    static class Variant {
        int mConstraintID = -1;
        ConstraintSet mConstraintSet;
        int mId;
        float mMaxHeight = Float.NaN;
        float mMaxWidth = Float.NaN;
        float mMinHeight = Float.NaN;
        float mMinWidth = Float.NaN;

        public Variant(Context context, XmlPullParser parser) {
            TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.Variant);
            int N = a.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.Variant_constraints) {
                    this.mConstraintID = a.getResourceId(attr, this.mConstraintID);
                    String type = context.getResources().getResourceTypeName(this.mConstraintID);
                    context.getResources().getResourceName(this.mConstraintID);
                    if ("layout".equals(type)) {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.clone(context, this.mConstraintID);
                    }
                } else if (attr == R.styleable.Variant_region_heightLessThan) {
                    this.mMaxHeight = a.getDimension(attr, this.mMaxHeight);
                } else if (attr == R.styleable.Variant_region_heightMoreThan) {
                    this.mMinHeight = a.getDimension(attr, this.mMinHeight);
                } else if (attr == R.styleable.Variant_region_widthLessThan) {
                    this.mMaxWidth = a.getDimension(attr, this.mMaxWidth);
                } else if (attr == R.styleable.Variant_region_widthMoreThan) {
                    this.mMinWidth = a.getDimension(attr, this.mMinWidth);
                } else {
                    Log.v("ConstraintLayoutStates", "Unknown tag");
                }
            }
            a.recycle();
        }

        /* access modifiers changed from: package-private */
        public boolean match(float widthDp, float heightDp) {
            if (!Float.isNaN(this.mMinWidth) && widthDp < this.mMinWidth) {
                return false;
            }
            if (!Float.isNaN(this.mMinHeight) && heightDp < this.mMinHeight) {
                return false;
            }
            if (!Float.isNaN(this.mMaxWidth) && widthDp > this.mMaxWidth) {
                return false;
            }
            if (Float.isNaN(this.mMaxHeight) || heightDp <= this.mMaxHeight) {
                return true;
            }
            return false;
        }
    }

    private void load(Context context, int resourceId) {
        State state;
        XmlPullParser parser = context.getResources().getXml(resourceId);
        Exception e;
        try {
            int eventType = parser.getEventType();
            State state2 = null;
            while (eventType != 1) {
                switch (eventType) {
                    case 0:
                        parser.getName();
                        state = state2;
                        break;
                    case 2:
                        String tagName = parser.getName();
                        char c = 65535;
                        switch (tagName.hashCode()) {
                            case -1349929691:
                                if (tagName.equals("ConstraintSet")) {
                                    c = 4;
                                    break;
                                }
                                break;
                            case 80204913:
                                if (tagName.equals("State")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 1382829617:
                                if (tagName.equals("StateSet")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 1657696882:
                                if (tagName.equals("layoutDescription")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1901439077:
                                if (tagName.equals("Variant")) {
                                    c = 3;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                state = state2;
                                break;
                            case 1:
                                state = state2;
                                break;
                            case 2:
                                state = new State(context, parser);
                                this.mStateList.put(state.mId, state);
                                break;
                            case 3:
                                Variant match = new Variant(context, parser);
                                if (state2 != null) {
                                    state2.add(match);
                                    state = state2;
                                    break;
                                }
                            case 4:
                                parseConstraintSet(context, parser);
                                state = state2;
                                break;
                            default:
                                Log.v("ConstraintLayoutStates", "unknown tag " + tagName);
                        }
                    case 1:
                    default:
                        state = state2;
                        break;
                    case 3:
                        state = state2;
                        break;
                }
                eventType = parser.next();
                state2 = state;
            }
        } catch (XmlPullParserException e3) {
            e = e3;
            e.printStackTrace();
        } catch (IOException e4) {
            e = e4;
            e.printStackTrace();
        }
    }

    private void parseConstraintSet(Context context, XmlPullParser parser) {
        ConstraintSet set = new ConstraintSet();
        int count = parser.getAttributeCount();
        for (int i = 0; i < count; i++) {
            if ("id".equals(parser.getAttributeName(i))) {
                String s = parser.getAttributeValue(i);
                int id = -1;
                if (s.contains("/")) {
                    id = context.getResources().getIdentifier(s.substring(s.indexOf(47) + 1), "id", context.getPackageName());
                }
                if (id == -1) {
                    if (s == null || s.length() <= 1) {
                        Log.e("ConstraintLayoutStates", "error in parsing id");
                    } else {
                        id = Integer.parseInt(s.substring(1));
                    }
                }
                set.load(context, parser);
                this.mConstraintSetMap.put(id, set);
                return;
            }
        }
    }
}
