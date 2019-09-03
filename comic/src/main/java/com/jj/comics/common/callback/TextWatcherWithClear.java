package com.jj.comics.common.callback;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class TextWatcherWithClear implements TextWatcher {
    private WeakReference<View> mButton;
    private WeakReference<EditText> mEditView;

    public TextWatcherWithClear(View button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEditView != null && mEditView.get() != null)
                    mEditView.get().setText("");
            }
        });
        mButton = new WeakReference<View>(button);
    }

    public void bindEditView(EditText editText) {
        editText.addTextChangedListener(this);
        mEditView = new WeakReference<>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mButton != null && mButton.get() != null) {
            View view = mButton.get();
            if (s.length() > 0 && view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
            } else if (s.length() <= 0 && view.getVisibility() == View.VISIBLE) {
                view.setVisibility(View.GONE);
            }
        }
        if (mEditView != null && mEditView.get() != null) {
            EditText view = mEditView.get();
            view.setSelection(s.length());
        }
    }
}
