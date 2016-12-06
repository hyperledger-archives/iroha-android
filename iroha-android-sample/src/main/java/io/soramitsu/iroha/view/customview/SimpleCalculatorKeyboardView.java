package io.soramitsu.iroha.view.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;

import io.soramitsu.iroha.R;
import io.soramitsu.iroha.databinding.ViewSimpleCalculatorKeyboardBinding;

public class SimpleCalculatorKeyboardView extends GridLayout {
    public static final String TAG = SimpleCalculatorKeyboardView.class.getSimpleName();

    private ViewSimpleCalculatorKeyboardBinding binding;
    private EditText targetEditView;

    public SimpleCalculatorKeyboardView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public SimpleCalculatorKeyboardView(Context context, AttributeSet attr) {
        super(context, attr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    public SimpleCalculatorKeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @TargetApi(21)
    public SimpleCalculatorKeyboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (!isInEditMode()) {
            init(context);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);
        binding.getRoot().setMinimumHeight(heightSize);
    }

    private void init(Context context) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_simple_calculator_keyboard, this, true);
        binding.input0.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                targetEditView.append(targetEditView.getText().length() == 0 ? "" : "0");
            }
        });
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                targetEditView.append(((Button) view).getText());
            }
        };
        binding.input1.setOnClickListener(onClickListener);
        binding.input2.setOnClickListener(onClickListener);
        binding.input3.setOnClickListener(onClickListener);
        binding.input4.setOnClickListener(onClickListener);
        binding.input5.setOnClickListener(onClickListener);
        binding.input6.setOnClickListener(onClickListener);
        binding.input7.setOnClickListener(onClickListener);
        binding.input8.setOnClickListener(onClickListener);
        binding.input9.setOnClickListener(onClickListener);
        binding.delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String buff = targetEditView.getText().toString();
                if (buff.length() <= 0) {
                    return;
                }
                targetEditView.setText(buff.substring(0, buff.length() - 1));
            }
        });
        binding.reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                targetEditView.setText("");
            }
        });
    }

    public void setTargetTextView(EditText targetEditView) {
        this.targetEditView = targetEditView;
    }
}