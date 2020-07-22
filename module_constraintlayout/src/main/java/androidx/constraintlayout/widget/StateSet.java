package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import androidx.constraintlayout.R;

public class StateSet {
    private static final boolean DEBUG = false;
    public static final String TAG = "ConstraintLayoutStates";
    private SparseArray<ConstraintSet> mConstraintSetMap = new SparseArray<>();
    private ConstraintsChangedListener mConstraintsChangedListener = null;
    int mCurrentConstraintNumber = -1;
    int mCurrentStateId = -1;
    ConstraintSet mDefaultConstraintSet;
    int mDefaultState = -1;
    private SparseArray<State> mStateList = new SparseArray<>();

    public StateSet(Context context, XmlPullParser parser) {
        load(context, parser);
    }

    private void load(Context context, XmlPullParser parser) {
        TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.StateSet);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.StateSet_defaultState) {
                this.mDefaultState = a.getResourceId(attr, this.mDefaultState);
            }
        }
        State state = null;
        Exception e;
        try {
            int eventType = parser.getEventType();
            while (eventType != 1) {
                switch (eventType) {
                    case 0:
                        parser.getName();
                        state = state;
                        break;
                    case 2:
                        String tagName = parser.getName();
                        char c = 65535;
                        switch (tagName.hashCode()) {
                            case 80204913:
                                if (tagName.equals("State")) {
                                    c = 2;
                                    break;
                                }
                                break;
                            case 1301459538:
                                if (tagName.equals("LayoutDescription")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1382829617:
                                if (tagName.equals("StateSet")) {
                                    c = 1;
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
                                state = state;
                                break;
                            case 1:
                                state = state;
                                break;
                            case 2:
                                state = new State(context, parser);
                                this.mStateList.put(state.mId, state);
                                break;
                            case 3:
                                Variant match = new Variant(context, parser);
                                if (state != null) {
                                    state.add(match);
                                    state = state;
                                    break;
                                }
                            default:
                                Log.v("ConstraintLayoutStates", "unknown tag " + tagName);
                        }
                    case 1:
                    default:
                        state = state;
                        break;
                    case 3:
                        if (!"StateSet".equals(parser.getName())) {
                            state = state;
                            break;
                        } else {
                            return;
                        }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e3) {
            e = e3;
            e.printStackTrace();
        } catch (IOException e4) {
            e = e4;
            e.printStackTrace();
        }
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

    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
    }

    public int stateGetConstraintID(int id, int width, int height) {
        return updateConstraints(-1, id, (float) width, (float) height);
    }

    public int convertToConstraintSet(int currentConstrainSettId, int stateId, float width, float height) {
        State state = this.mStateList.get(stateId);
        if (state == null) {
            return stateId;
        }
        if (width != -1.0f && height != -1.0f) {
            Variant match = null;
            Iterator<Variant> it = state.mVariants.iterator();
            while (it.hasNext()) {
                Variant mVariant = it.next();
                if (mVariant.match(width, height)) {
                    if (currentConstrainSettId == mVariant.mConstraintID) {
                        return currentConstrainSettId;
                    }
                    match = mVariant;
                }
            }
            if (match != null) {
                return match.mConstraintID;
            }
            return state.mConstraintID;
        } else if (state.mConstraintID == currentConstrainSettId) {
            return currentConstrainSettId;
        } else {
            Iterator<Variant> it2 = state.mVariants.iterator();
            while (it2.hasNext()) {
                if (currentConstrainSettId == it2.next().mConstraintID) {
                    return currentConstrainSettId;
                }
            }
            return state.mConstraintID;
        }
    }

    public int updateConstraints(int currentid, int id, float width, float height) {
        State state;
        int match;
        if (currentid == id) {
            if (id == -1) {
                state = this.mStateList.valueAt(0);
            } else {
                state = this.mStateList.get(this.mCurrentStateId);
            }
            if (state == null) {
                return -1;
            }
            if ((this.mCurrentConstraintNumber != -1 && state.mVariants.get(currentid).match(width, height)) || currentid == (match = state.findMatch(width, height))) {
                return currentid;
            }
            return match == -1 ? state.mConstraintID : state.mVariants.get(match).mConstraintID;
        }
        State state2 = this.mStateList.get(id);
        if (state2 == null) {
            return -1;
        }
        int match2 = state2.findMatch(width, height);
        return match2 == -1 ? state2.mConstraintID : state2.mVariants.get(match2).mConstraintID;
    }

    static class State {
        int mConstraintID = -1;
        int mId;
        boolean mIsLayout = false;
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
                        this.mIsLayout = true;
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
        int mId;
        boolean mIsLayout = false;
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
                        this.mIsLayout = true;
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
}
