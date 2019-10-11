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


public class DialogInput extends Dialog {

    private String title, message, input;
    private OnDialogClosedListener listenerClose;
    private OnInputInsertedListener listenerInput;


    public DialogInput(Context context) {
        super(context);
    }

    public DialogInput setTitle(String title) {
        this.title = title;
        return this;
    }

    public DialogInput setMessage(String message) {
        this.message = message;
        return this;
    }

    public DialogInput setInput(String input) {
        this.input = input;
        return this;
    }

    public DialogInput setListenerClose(OnDialogClosedListener listenerClose) {
        this.listenerClose = listenerClose;
        return this;
    }

    public DialogInput setListenerInput(OnInputInsertedListener listenerInput) {
        this.listenerInput = listenerInput;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_input);

        final EditText etPhone = (EditText) findViewById(R.id.etPhone);
        if (input != null) etPhone.setText(input);

        ((TextView) findViewById(R.id.label_title)).setText(title);

        if (message == null) {
            ((TextView) findViewById(R.id.label_message)).setVisibility(View.GONE);
        } else if (message.equals("")){
            ((TextView) findViewById(R.id.label_message)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.label_message)).setText(message);
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
                    listenerInput.onInputInserted(etPhone.getText().toString());
                }
            }
        });
    }

    public static DialogInput createDialog(Context context) {
        return new DialogInput(context);
    }

    public interface OnDialogClosedListener {
        public void onDialogClosed();
    }
}
