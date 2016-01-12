package org.webpartners.wpedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Jorge Garrido Oval on 06/03/15.
 */
public class WPEditText extends LinearLayout implements TextWatcher {

    public static final int TYPE_ALPHA = 0;
    public static final int TYPE_ALPHANUMERIC = 1;
    public static final int TYPE_EMAIL = 2;
    public static final int TYPE_PASSWORD = 3;
    public static final int TYPE_NUMERIC = 4;
    public static final int TYPE_NUMERIC_WITH_SPACE = 5;
    public static final int TYPE_NUMERIC_WITH_SPACE_AND_PLUS = 6;

    private Context context;

    private ImageView icon;
    private EditText editText;
    private TextView isValid;
    private View underline;

    private int type;
    private int minLength = -1;
    private String valid;
    private String invalid;
    private String empty;

    private final String alphaPattern= "^[a-z\\sA-Z]*$";
    private final String alphaNumericPattern= "^[a-z\\sA-Z0-9]*$";
    private final String emailPattern= "^(.+)@([^@]+[^.])$";
    private final String numeric= "^[0-9]*$";
    private final String numericWithSpacePattern= "^[\\s0-9]*$";
    private final String numericWithSpaceAndPlusPattern= "^[\\s0-9+]*$";

    private boolean ok = false;
    private KeyListener cachedKeyListener;

    public WPEditText(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WPEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attributeSet,
                R.styleable.WPEditText,
                0, 0);

        init();

        try {
            this.setupEditText(
                    a.getResourceId(R.styleable.WPEditText_hint, R.string.wpedittext__sample_hint),
                    a.getInteger(R.styleable.WPEditText_type, TYPE_ALPHANUMERIC),
                    a.getInteger(R.styleable.WPEditText_min_length, 8),
                    a.getBoolean(R.styleable.WPEditText_editable, true)
            );
            this.textColors(
                    a.getResourceId(R.styleable.WPEditText_text_color,  android.R.color.black),
                    a.getResourceId(R.styleable.WPEditText_hint_color, android.R.color.darker_gray)
            );
            this.validationText(
                    a.getString(R.styleable.WPEditText_valid_message),
                    a.getString(R.styleable.WPEditText_invalid_message),
                    a.getString(R.styleable.WPEditText_empty_message)
            );
            this.icon(a.getResourceId(
                    R.styleable.WPEditText_header_icon,
                    android.R.drawable.ic_dialog_info)
            );
        } finally {
            a.recycle();
        }
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wpedittext__component, this, true);

        this.icon = (ImageView) view.findViewById(R.id.icon);
        this.editText = (EditText) view.findViewById(R.id.edit_text);
        this.isValid = (TextView) view.findViewById(R.id.is_valid);
        this.underline = view.findViewById(R.id.underline);

        this.editText.addTextChangedListener(this);

        this.valid = context.getResources().getString(R.string.wpedittext__validation_valid_field);
        this.invalid = context.getResources().getString(R.string.wpedittext__validation_invalid_field);
        this.empty = context.getResources().getString(R.string.wpedittext__validation_empty_field);

        this.validationText(this.valid, this.invalid, this.empty);
    }

    /**
     * setupEditText: EditText fist setup with hint and type
     * @param hint A hint for the EditText
     * @param type A {@link} Type for validate entered text
     * @param minLength Min length (recommended for passwords)
     */
    public void setupEditText(int hint, int type, int minLength, boolean editable) {
        this.minLength = minLength;
        setEditable(editable);
        setupEditText(hint, type);
    }

    /**
     * setupEditText: EditText fist setup with hint and type
     * @param hint A hint for the EditText
     * @param type A {@link} Type for validate entered text
     */
    public void setupEditText(int hint, int type) {
        this.editText.setHint(hint);
        this.type = type;

        switch (type) {
            case TYPE_PASSWORD:
                this.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                this.editText.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
    }

    /**
     * setupEditText: EditText fist setup with hint
     * @param hint A hint for the EditText
     */
    public void setupEditText(String hint) {
        this.editText.setHint(hint);
    }

    public  void setEditable(boolean editable) {
        underline.setVisibility(editable ?  VISIBLE : INVISIBLE);
        isValid.setVisibility(editable ?  VISIBLE : INVISIBLE);
        if (!editable) {
            this.cachedKeyListener = this.editText.getKeyListener();
            this.editText.setKeyListener(null);
        } else {
            if (cachedKeyListener != null)
                this.editText.setKeyListener(cachedKeyListener);
        }
    }

    /**
     * textColors: Set colors for texts and hints
     * @param textColor Resource id
     * @param hintColor Resource id
     */
    public void textColors(@ColorRes int textColor, @ColorRes int hintColor) {
        this.editText.setTextColor(ContextCompat.getColor(context, textColor));
        this.editText.setHintTextColor(ContextCompat.getColor(context, hintColor));
    }

    /**
     * validationText: Set validations message texts
     * @param valid If entered text is valid
     * @param invalid If entered text is invalid
     * @param empty If entered text is empty
     */
    public void validationText(int valid, int invalid, int empty) {
        this.valid = context.getResources().getString(valid);
        this.invalid = context.getResources().getString(invalid);
        this.empty = context.getResources().getString(empty);
    }

    /**
     * validationText: Set validations message texts
     * @param valid If entered text is valid
     * @param invalid If entered text is invalid
     * @param empty If entered text is empty
     */
    public void validationText(String valid, String invalid, String empty) {
        this.valid = valid;
        this.invalid = invalid;
        this.empty = empty;
    }

    /**
     * icon: Set a new icon
     * @param drawable Resource id
     */
    public void icon(int drawable) {
        this.icon.setImageDrawable(context.getResources().getDrawable(drawable));
    }

    /**
     * underLine: Change underline color
     * @param color Resource id
     */
    public void underLine(int color) {
        this.underline.setBackgroundResource(color);
    }

    /**
     * getText: Returns the EditText content
     * @return EditText's string
     */
    public String getText() {
        return this.editText.getText().toString();
    }

    /**
     * setText: Set passed text to the EditText
     * @param text The string for EditText
     */
    public void setText(String text) {
        this.editText.setText(text);
    }

    /**
     * isOk: Get validation state
     * @return Is valid
     */
    public boolean isOk() {
        return this.ok;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0 || s.toString().equals(" ")) {
            this.isValid.setText(this.empty);
            this.isValid.setTextColor(context.getResources().getColor(R.color.valencia));
            this.ok = false;
            return;
        }

        switch (type) {
            case TYPE_ALPHA:
                this.ok = s.toString().matches(this.alphaPattern);
                break;
            case TYPE_ALPHANUMERIC:
            case TYPE_PASSWORD:
                this.ok = s.toString().matches(this.alphaNumericPattern);
                break;
            case TYPE_EMAIL:
                this.ok = s.toString().matches(this.emailPattern);
                break;
            case TYPE_NUMERIC:
                this.ok = s.toString().matches(this.numeric);
                break;
            case TYPE_NUMERIC_WITH_SPACE:
                this.ok = s.toString().matches(this.numericWithSpacePattern);
                break;
            case TYPE_NUMERIC_WITH_SPACE_AND_PLUS:
                this.ok = s.toString().matches(this.numericWithSpaceAndPlusPattern);
                break;
        }

        if (minLength != -1 && s.length() < minLength) {
            this.ok = false;
        }

        if (this.ok) {
            this.isValid.setText(this.valid);
            this.isValid.setTextColor(context.getResources().getColor(R.color.cerulean));
        } else {
            this.isValid.setText(this.invalid);
            this.isValid.setTextColor(context.getResources().getColor(R.color.valencia));
        }

    }

    @Override
    public void afterTextChanged(Editable s) {}

    public void addTextChangedListener(TextWatcher watcher) {
        editText.addTextChangedListener(watcher);
    }

}
