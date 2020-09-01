package com.example.fmdriver.retrofit.objects;

import com.google.gson.annotations.SerializedName;

public class FcmMessageResponse {

    //String specifying a unique ID for each successfully processed message.
    @SerializedName("message_id")
    private String messageId;

    //Optional string specifying the registration token for the client app
    // that the message was processed and sent to.
    @SerializedName("registration_id")
    private String registrationId;

    //String specifying the error that occurred when processing the message for the recipient.
    // The possible values can be found in https://firebase.google.com/docs/cloud-messaging/http-server-ref#table9
    @SerializedName("error")
    private String error;
}
