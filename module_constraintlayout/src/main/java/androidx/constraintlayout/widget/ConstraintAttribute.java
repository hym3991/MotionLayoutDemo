package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;
import android.view.View;

import androidx.constraintlayout.R;
import androidx.constraintlayout.motion.widget.Debug;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;

public class ConstraintAttribute {
    private static final String TAG = "TransitionLayout";
    boolean mBooleanValue;
    private int mColorValue;
    private float mFloatValue;
    private int mIntegerValue;
    String mName;
    private String mStringValue;
    private AttributeType mType;

    public enum AttributeType {
        INT_TYPE,
        FLOAT_TYPE,
        COLOR_TYPE,
        COLOR_DRAWABLE_TYPE,
        STRING_TYPE,
        BOOLEAN_TYPE,
        DIMENSION_TYPE
    }

    public AttributeType getType() {
        return this.mType;
    }

    public void setFloatValue(float value) {
        this.mFloatValue = value;
    }

    public void setColorValue(int value) {
        this.mColorValue = value;
    }

    public void setIntValue(int value) {
        this.mIntegerValue = value;
    }

    public void setStringValue(String value) {
        this.mStringValue = value;
    }

    public int noOfInterpValues() {
        switch (this.mType) {
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                return 4;
            default:
                return 1;
        }
    }

    public float getValueToInterpolate() {
        switch (this.mType) {
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case INT_TYPE:
                return (float) this.mIntegerValue;
            case FLOAT_TYPE:
                return this.mFloatValue;
            case STRING_TYPE:
                throw new RuntimeException("Cannot interpolate String");
            case BOOLEAN_TYPE:
                return this.mBooleanValue ? 0.0f : 1.0f;
            case DIMENSION_TYPE:
                return this.mFloatValue;
            default:
                return Float.NaN;
        }
    }

    public void getValuesToInterpolate(float[] ret) {
        switch (this.mType) {
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                int a = (this.mColorValue >> 24) & 255;
                int r = (this.mColorValue >> 16) & 255;
                int g = (this.mColorValue >> 8) & 255;
                int b = this.mColorValue & 255;
                float f_r = (float) Math.pow((double) (((float) r) / 255.0f), 2.2d);
                float f_g = (float) Math.pow((double) (((float) g) / 255.0f), 2.2d);
                ret[0] = f_r;
                ret[1] = f_g;
                ret[2] = (float) Math.pow((double) (((float) b) / 255.0f), 2.2d);
                ret[3] = ((float) a) / 255.0f;
                return;
            case INT_TYPE:
                ret[0] = (float) this.mIntegerValue;
                return;
            case FLOAT_TYPE:
                ret[0] = this.mFloatValue;
                return;
            case STRING_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case BOOLEAN_TYPE:
                ret[0] = this.mBooleanValue ? 0.0f : 1.0f;
                return;
            case DIMENSION_TYPE:
                ret[0] = this.mFloatValue;
                return;
            default:
                return;
        }
    }

    public void setValue(float[] value) {
        boolean z = false;
        switch (this.mType) {
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                this.mColorValue = Color.HSVToColor(value);
                this.mColorValue = (this.mColorValue & 16777215) | (clamp((int) (255.0f * value[3])) << 24);
                return;
            case INT_TYPE:
                this.mIntegerValue = (int) value[0];
                return;
            case FLOAT_TYPE:
                this.mFloatValue = value[0];
                return;
            case STRING_TYPE:
                throw new RuntimeException("Color does not have a single color to interpolate");
            case BOOLEAN_TYPE:
                if (((double) value[0]) > 0.5d) {
                    z = true;
                }
                this.mBooleanValue = z;
                return;
            case DIMENSION_TYPE:
                this.mFloatValue = value[0];
                return;
            default:
                return;
        }
    }

    public boolean diff(ConstraintAttribute constraintAttribute) {
        if (constraintAttribute == null || this.mType != constraintAttribute.mType) {
            return false;
        }
        switch (this.mType) {
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                return this.mColorValue == constraintAttribute.mColorValue;
            case INT_TYPE:
                return this.mIntegerValue == constraintAttribute.mIntegerValue;
            case FLOAT_TYPE:
                return this.mFloatValue == constraintAttribute.mFloatValue;
            case STRING_TYPE:
                return this.mIntegerValue == constraintAttribute.mIntegerValue;
            case BOOLEAN_TYPE:
                return this.mBooleanValue == constraintAttribute.mBooleanValue;
            case DIMENSION_TYPE:
                return this.mFloatValue == constraintAttribute.mFloatValue;
            default:
                return false;
        }
    }

    public ConstraintAttribute(String name, AttributeType attributeType) {
        this.mName = name;
        this.mType = attributeType;
    }

    public ConstraintAttribute(String name, AttributeType attributeType, Object value) {
        this.mName = name;
        this.mType = attributeType;
        setValue(value);
    }

    public ConstraintAttribute(ConstraintAttribute source, Object value) {
        this.mName = source.mName;
        this.mType = source.mType;
        setValue(value);
    }

    public void setValue(Object value) {
        switch (this.mType) {
            case COLOR_TYPE:
            case COLOR_DRAWABLE_TYPE:
                this.mColorValue = ((Integer) value).intValue();
                return;
            case INT_TYPE:
                this.mIntegerValue = ((Integer) value).intValue();
                return;
            case FLOAT_TYPE:
                this.mFloatValue = ((Float) value).floatValue();
                return;
            case STRING_TYPE:
                this.mStringValue = (String) value;
                return;
            case BOOLEAN_TYPE:
                this.mBooleanValue = ((Boolean) value).booleanValue();
                return;
            case DIMENSION_TYPE:
                this.mFloatValue = ((Float) value).floatValue();
                return;
            default:
                return;
        }
    }

    public static HashMap<String, ConstraintAttribute> extractAttributes(HashMap<String, ConstraintAttribute> base, View view) {
        HashMap<String, ConstraintAttribute> ret = new HashMap<>();
        Class<?> cls = view.getClass();
        for (String name : base.keySet()) {
            ConstraintAttribute constraintAttribute = base.get(name);
            try {
                if (name.equals("BackgroundColor")) {
                    ret.put(name, new ConstraintAttribute(constraintAttribute, Integer.valueOf(((ColorDrawable) view.getBackground()).getColor())));
                } else {
                    ret.put(name, new ConstraintAttribute(constraintAttribute, cls.getMethod("getMap" + name, new Class[0]).invoke(view, new Object[0])));
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                e3.printStackTrace();
            }
        }
        return ret;
    }

    public static void setAttributes(View view, HashMap<String, ConstraintAttribute> map) {
        Class<?> cls = view.getClass();
        for (String name : map.keySet()) {
            ConstraintAttribute constraintAttribute = map.get(name);
            String methodName = "set" + name;
            try {
                switch (constraintAttribute.mType) {
                    case COLOR_TYPE:
                        cls.getMethod(methodName, Integer.TYPE).invoke(view, Integer.valueOf(constraintAttribute.mColorValue));
                        continue;
                    case COLOR_DRAWABLE_TYPE:
                        Method method = cls.getMethod(methodName, Drawable.class);
                        ColorDrawable drawable = new ColorDrawable();
                        drawable.setColor(constraintAttribute.mColorValue);
                        method.invoke(view, drawable);
                        continue;
                    case INT_TYPE:
                        cls.getMethod(methodName, Integer.TYPE).invoke(view, Integer.valueOf(constraintAttribute.mIntegerValue));
                        continue;
                    case FLOAT_TYPE:
                        cls.getMethod(methodName, Float.TYPE).invoke(view, Float.valueOf(constraintAttribute.mFloatValue));
                        continue;
                    case STRING_TYPE:
                        cls.getMethod(methodName, CharSequence.class).invoke(view, constraintAttribute.mStringValue);
                        continue;
                    case BOOLEAN_TYPE:
                        cls.getMethod(methodName, Boolean.TYPE).invoke(view, Boolean.valueOf(constraintAttribute.mBooleanValue));
                        continue;
                    case DIMENSION_TYPE:
                        cls.getMethod(methodName, Float.TYPE).invoke(view, Float.valueOf(constraintAttribute.mFloatValue));
                        continue;
                    default:
                        continue;
                }
            } catch (NoSuchMethodException e) {
                Log.e(TAG, e.getMessage());
                Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + cls.getName());
                Log.e(TAG, cls.getName() + " must have a method " + methodName);
            } catch (IllegalAccessException e2) {
                Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + cls.getName());
                e2.printStackTrace();
            } catch (InvocationTargetException e3) {
                Log.e(TAG, " Custom Attribute \"" + name + "\" not found on " + cls.getName());
                e3.printStackTrace();
            }
        }
    }

    private static int clamp(int c) {
        int c2 = (c & ((c >> 31) ^ -1)) - 255;
        return (c2 & (c2 >> 31)) + 255;
    }

    public void setInterpolatedValue(View view, float[] value) {
        Class<?> cls = view.getClass();
        String methodName = "set" + this.mName;
        try {
            switch (this.mType) {
                case COLOR_TYPE:
                    cls.getMethod(methodName, Integer.TYPE).invoke(view, Integer.valueOf((clamp((int) (value[3] * 255.0f)) << 24) | (clamp((int) (((float) Math.pow((double) value[0], 0.45454545454545453d)) * 255.0f)) << 16) | (clamp((int) (((float) Math.pow((double) value[1], 0.45454545454545453d)) * 255.0f)) << 8) | clamp((int) (((float) Math.pow((double) value[2], 0.45454545454545453d)) * 255.0f))));
                    return;
                case COLOR_DRAWABLE_TYPE:
                    Method method = cls.getMethod(methodName, Drawable.class);
                    int r = clamp((int) (((float) Math.pow((double) value[0], 0.45454545454545453d)) * 255.0f));
                    int g = clamp((int) (((float) Math.pow((double) value[1], 0.45454545454545453d)) * 255.0f));
                    int b = clamp((int) (((float) Math.pow((double) value[2], 0.45454545454545453d)) * 255.0f));
                    ColorDrawable drawable = new ColorDrawable();
                    drawable.setColor((clamp((int) (value[3] * 255.0f)) << 24) | (r << 16) | (g << 8) | b);
                    method.invoke(view, drawable);
                    return;
                case INT_TYPE:
                    cls.getMethod(methodName, Integer.TYPE).invoke(view, Integer.valueOf((int) value[0]));
                    return;
                case FLOAT_TYPE:
                    cls.getMethod(methodName, Float.TYPE).invoke(view, Float.valueOf(value[0]));
                    return;
                case STRING_TYPE:
                    throw new RuntimeException("unable to interpolate strings " + this.mName);
                case BOOLEAN_TYPE:
                    Method method2 = cls.getMethod(methodName, Boolean.TYPE);
                    Object[] objArr = new Object[1];
                    objArr[0] = Boolean.valueOf(value[0] > 0.5f);
                    method2.invoke(view, objArr);
                    return;
                case DIMENSION_TYPE:
                    cls.getMethod(methodName, Float.TYPE).invoke(view, Float.valueOf(value[0]));
                    return;
                default:
                    return;
            }
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "no method " + methodName + "on View \"" + Debug.getName(view) + "\"");
            e.printStackTrace();
        } catch (IllegalAccessException e2) {
            Log.e(TAG, "cannot access method " + methodName + "on View \"" + Debug.getName(view) + "\"");
            e2.printStackTrace();
        } catch (InvocationTargetException e3) {
            e3.printStackTrace();
        }
    }

    public static void parse(Context context, XmlPullParser parser, HashMap<String, ConstraintAttribute> custom) {
        TypedArray a = context.obtainStyledAttributes(Xml.asAttributeSet(parser), R.styleable.CustomAttribute);
        String name = null;
        Object value = null;
        AttributeType type = null;
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CustomAttribute_attributeName) {
                name = a.getString(attr);
                if (name != null && name.length() > 0) {
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                }
            } else if (attr == R.styleable.CustomAttribute_customBoolean) {
                value = Boolean.valueOf(a.getBoolean(attr, false));
                type = AttributeType.BOOLEAN_TYPE;
            } else if (attr == R.styleable.CustomAttribute_customColorValue) {
                type = AttributeType.COLOR_TYPE;
                value = Integer.valueOf(a.getColor(attr, 0));
            } else if (attr == R.styleable.CustomAttribute_customColorDrawableValue) {
                type = AttributeType.COLOR_DRAWABLE_TYPE;
                value = Integer.valueOf(a.getColor(attr, 0));
            } else if (attr == R.styleable.CustomAttribute_customPixelDimension) {
                type = AttributeType.DIMENSION_TYPE;
                value = Float.valueOf(TypedValue.applyDimension(1, a.getDimension(attr, 0.0f), context.getResources().getDisplayMetrics()));
            } else if (attr == R.styleable.CustomAttribute_customDimension) {
                type = AttributeType.DIMENSION_TYPE;
                value = Float.valueOf(a.getDimension(attr, 0.0f));
            } else if (attr == R.styleable.CustomAttribute_customFloatValue) {
                type = AttributeType.FLOAT_TYPE;
                value = Float.valueOf(a.getFloat(attr, Float.NaN));
            } else if (attr == R.styleable.CustomAttribute_customIntegerValue) {
                type = AttributeType.INT_TYPE;
                value = Integer.valueOf(a.getInteger(attr, -1));
            } else if (attr == R.styleable.CustomAttribute_customStringValue) {
                type = AttributeType.STRING_TYPE;
                value = a.getString(attr);
            }
        }
        if (!(name == null || value == null)) {
            custom.put(name, new ConstraintAttribute(name, type, value));
        }
        a.recycle();
    }
}
