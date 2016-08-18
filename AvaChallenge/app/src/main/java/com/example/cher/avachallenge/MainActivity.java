package com.example.cher.avachallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}

//    public static void main(String[] args) {
//        PNConfiguration pnConfiguration = new PNConfiguration();
//        pnConfiguration.setSubscribeKey("demo");
//        pnConfiguration.setPublishKey("demo");
//
//        PubNub pubNub = new PubNub(pnConfiguration);
//
//        pubNub.addListener(new SubscribeCallback() {
//            @Override
//            public void status(PubNub pubnub, PNStatus status) {
//
//
//                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
//                    // This event happens when radio / connectivity is lost
//                }
//
//                else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
//
//                    // Connect event. You can do stuff like publish, and know you'll get it.
//                    // Or just use the connected event to confirm you are subscribed for
//                    // UI / internal notifications, etc
//
//                    if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
//                        pubnub.publish().channel("awesomeChannel").message("hello!!").async(new PNCallback<PNPublishResult>() {
//                            @Override
//                            public void onResponse(PNPublishResult result, PNStatus status) {
//                                // Check whether request successfully completed or not.
//                                if (!status.isError()) {
//
//                                    // Message successfully published to specified channel.
//                                }
//                                // Request processing failed.
//                                else {
//
//                                    // Handle message publish error. Check 'category' property to find out possible issue
//                                    // because of which request did fail.
//                                    //
//                                    // Request can be resent using: [status retry];
//                                }
//                            }
//                        });
//                    }
//                }
//                else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
//
//                    // Happens as part of our regular operation. This event happens when
//                    // radio / connectivity is lost, then regained.
//                }
//                else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
//
//                    // Handle messsage decryption error. Probably client configured to
//                    // encrypt messages and on live data feed it received plain text.
//                }
//            }
//
//            @Override
//            public void message(PubNub pubnub, PNMessageResult message) {
//                // Handle new message stored in message.data.message
//                if (message.getActualChannel() != null) {
//                    // Message has been received on channel group stored in
//                    // message.getActualChannel()
//                }
//                else {
//                    // Message has been received on channel stored in
//                    // message.getSubscribedChannel()
//                }
//
//            /*
//                log the following items with your favorite logger
//                    - message.getMessage()
//                    - message.getSubscribedChannel()
//                    - message.getTimetoken()
//            */
//            }
//
//            @Override
//            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
//
//            }
//        });
//
//        pubNub.subscribe().channels(Arrays.asList("awesomeChannel!")).execute();
//    }