package com.example.fmdriver.retrofit.responses;

public class ResponseDeletePosition {

    private int result_code;
    private String message;

    public ResponseDeletePosition() {}

    public ResponseDeletePosition(int result_code, String message) {
        this.result_code = result_code;
        this.message = message;
    }

    public int getResult_code() {
        return result_code;
    }

    public void setResult_code(int result_code) {
        this.result_code = result_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
