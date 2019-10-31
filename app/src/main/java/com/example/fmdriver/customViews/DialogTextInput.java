package com.example.fmdriver.customViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fmdriver.R;
import com.example.fmdriver.listeners.OnInputInsertedListener;


public class DialogTextInput extends Dialog {

    private String title, message, input;
    private OnDialogClosedListener listenerClose;
    private OnInputInsertedListener listenerInput;


    public DialogTextInput(Context context) {
        super(context);
    }


    public DialogTextInput setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogTextInput setMessage(String message) {
        this.message = message;
        return this;
    }

    public DialogTextInput setInput(String input) {
        this.input = input;
        return this;
    }

    public DialogTextInput setListenerClose(OnDialogClosedListener listenerClose) {
        this.listenerClose = listenerClose;
        return this;
    }

    public DialogTextInput setListenerInput(OnInputInsertedListener listenerInput) {
        this.listenerInput = listenerInput;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_text_input);

        final EditText etInput = (EditText) findViewById(R.id.etInput);
        if (input != null) etInput.setText(input);

        ((TextView) findViewById(R.id.labelTitle)).setText(title);

        if (message == null) {
            ((TextView) findViewById(R.id.labelMessage)).setVisibility(View.GONE);
        } else if (message.equals("")){
            ((TextView) findViewById(R.id.labelMessage)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.labelMessage)).setText(message);
        }

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listenerClose != null) {
                    listenerClose.onDialogClosed();
                }
            }
        });

        findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listenerInput != null) {
                    listenerInput.onInputInserted(etInput.getText().toString());
                }
            }
        });
    }

    public static DialogTextInput createDialog(Context context) {
        return new DialogTextInput(context);
    }

    public interface OnDialogClosedListener {
        public void onDialogClosed();
    }
}
