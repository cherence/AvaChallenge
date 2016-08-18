package com.example.cher.avachallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String SUBSCRIBE_KEY = "sub-c-897a7150-da55-11e5-9ce2-0619f8945a4f";
    private static final String PUBLISH_KEY = "pub-c-6590f75c-b2bb-4acc-9922-d5fe5aa8dec9";
    private static final String CHANNEL = "00001c72";
    private PNConfiguration pnConfiguration;
    private PubNub pubnub;
    private AvaMessage avaMessage;
    private JSONPObject jsonPObject;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAPI();
        setListeners2();
        subscribeToChannel();
    }

    private void initializeAPI(){
        pnConfiguration = new PNConfiguration();
        pnConfiguration.setSubscribeKey(SUBSCRIBE_KEY);
//        pnConfiguration.setPublishKey(PUBLISH_KEY);
//        pnConfiguration.setUuid("Cherence"); this sets/ hard codes the UUID
        pnConfiguration.setReconnectionPolicy(PNReconnectionPolicy.LINEAR);
        pubnub = new PubNub(pnConfiguration);
        pnConfiguration.getUuid(); //a default UUID is generated if not provided
    }

    private void setListeners(){
        pubnub.addListener(new SubscribeCallback() {
            @Override

            public void status(PubNub pubnub, PNStatus status) {
                // the status object returned is always related to subscribe but could contain
                // information about subscribe, heartbeat, or errors
                // use the operationType to switch on different options
                switch (status.getOperation()) {
                    // let's combine unsubscribe and subscribe handling for ease of use
                    case PNSubscribeOperation:
                    case PNUnsubscribeOperation:
                        // note: subscribe statuses never have traditional
                        // errors, they just have categories to represent the
                        // different issues or successes that occur as part of subscribe
                        switch(status.getCategory()) {
                            case PNConnectedCategory:
                                // this is expected for a subscribe, this means there is no error or issue whatsoever
                            case PNReconnectedCategory:
                                // this usually occurs if subscribe temporarily fails but reconnects. This means
                                // there was an error but there is no longer any issue
                            case PNDisconnectedCategory:
                                // this is the expected category for an unsubscribe. This means there
                                // was no error in unsubscribing from everything
                            case PNUnexpectedDisconnectCategory:
                                // this is usually an issue with the internet connection, this is an error, handle appropriately
                                // retry will be called automatically
                            case PNAccessDeniedCategory:
                                // this means that PAM does allow this client to subscribe to this
                                // channel and channel group configuration. This is another explicit error
                            default:
                                // More errors can be directly specified by creating explicit cases for other
                                // error categories of `PNStatusCategory` such as `PNTimeoutCategory` or `PNMalformedFilterExpressionCategory` or `PNDecryptionErrorCategory`
                        }

                    case PNHeartbeatOperation:
                        // heartbeat operations can in fact have errors, so it is important to check first for an error.
                        // For more information on how to configure heartbeat notifications through the status
                        // PNObjectEventListener callback, consult <link to the PNCONFIGURATION heartbeart config>
                        if (status.isError()) {
                            // There was an error with the heartbeat operation, handle here
                        } else {
                            // heartbeat operation was successful
                        }
                    default: {
                        // Encountered unknown status type
                    }
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // handle incoming messages
                // i want to take the message, save it as an avamessage object and add it to an
                // arraylist that will be displayed by the recyclerview

//                avaMessage = message;
//
//                avaMessage = new AvaMessage();
//                gson = new Gson();
//                avaMessage = gson.fromJson(message, AvaMessage.class);
//                jsonPObject = message.getMessage();

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {
                // handle incoming presence data
            }
        });

    }

    private void setListeners2(){

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {

            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

        pubnub.addListener(new SubscribeCallback() {
            @Override
            public void status(PubNub pubnub, PNStatus status) {


                if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                    // This event happens when radio / connectivity is lost
                    Log.i(TAG, "*******************status: connectivity is lost");
                }

                else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                    Log.i(TAG, "*******************status: successfully connected to channel");

                    // Connect event. You can do stuff like publish, and know you'll get it.
                    // Or just use the connected event to confirm you are subscribed for
                    // UI / internal notifications, etc
                }
                else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
                    Log.i(TAG, "*******************status: reconnecting to channel b/c connectivity regained");
                    // Happens as part of our regular operation. This event happens when
                    // radio / connectivity is lost, then regained.
                }
                else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
                    Log.i(TAG, "*******************status: decryption error occurred");
                    // Handle messsage decryption error. Probably client configured to
                    // encrypt messages and on live data feed it received plain text.
                }
            }

            @Override
            public void message(PubNub pubnub, PNMessageResult message) {
                // Handle new message stored in message.data.message
                if (message.getActualChannel() != null) {
                    // Message has been received on channel group stored in
                    // message.getActualChannel()
                    Log.i(TAG, "*******************GETmessage has been received on channel group stored in message.getActualChannel: " + message.getMessage());
                    Log.i(TAG, "*******************message has been received on channel group stored in message.getActualChannel: " + message);

                }
                else {
                    // Message has been received on channel stored in
                    // message.getSubscribedChannel()
                    Log.i(TAG, "*******************GETmessage has been received on channel group stored in message.getSubscribedChannel: " + message.getMessage());
                    Log.i(TAG, "*******************message has been received on channel group stored in message.getSubscribedChannel: " + message);

                }

                Log.i(TAG, "*******************message GET MESSAGE: " + message.getMessage());
                Log.i(TAG, "*******************message MESSAGE: " + message);
                Log.i(TAG, "*******************message GET TIME TOKEN: " + message.getTimetoken());
                Log.i(TAG, "*******************message GET ACTUAL CHANNEL: " + message.getActualChannel());
                Log.i(TAG, "*******************message GET SUBSCRIBED CHANNEL: " + message.getSubscribedChannel());
                Log.i(TAG, "*******************message GET USER META DATA: " + message.getUserMetadata());

            /*
                log the following items with your favorite logger
                    - message.getMessage()
                    - message.getSubscribedChannel()
                    - message.getTimetoken()
            */
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

    }

    private void subscribeToChannel(){
        pubnub.subscribe()
                .channels(Arrays.asList(CHANNEL)) // subscribe to channel groups. was .channels(Arrays.asList(CHANNEL))
//                .withPresence() // also subscribe to related presence information MIGHT NOT NEED
                .execute();
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

//



//pubNub.addListener(new SubscribeCallback() {
//@Override
//public void status(PubNub pubnub, PNStatus status) {
//
//
//        if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
//        // This event happens when radio / connectivity is lost
//        }
//
//        else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
//
//        // Connect event. You can do stuff like publish, and know you'll get it.
//        // Or just use the connected event to confirm you are subscribed for
//        // UI / internal notifications, etc
//
//        if (status.getCategory() == PNStatusCategory.PNConnectedCategory){
//        pubnub.publish().channel("awesomeChannel").message("hello!!").async(new PNCallback<PNPublishResult>() {
//@Override
//public void onResponse(PNPublishResult result, PNStatus status) {
//        // Check whether request successfully completed or not.
//        if (!status.isError()) {
//
//        // Message successfully published to specified channel.
//        }
//        // Request processing failed.
//        else {
//
//        // Handle message publish error. Check 'category' property to find out possible issue
//        // because of which request did fail.
//        //
//        // Request can be resent using: [status retry];
//        }
//        }
//        });
//        }
//        }
//        else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {
//
//        // Happens as part of our regular operation. This event happens when
//        // radio / connectivity is lost, then regained.
//        }
//        else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {
//
//        // Handle messsage decryption error. Probably client configured to
//        // encrypt messages and on live data feed it received plain text.
//        }
//        }
//
//@Override
//public void message(PubNub pubnub, PNMessageResult message) {
//        // Handle new message stored in message.data.message
//        if (message.getActualChannel() != null) {
//        // Message has been received on channel group stored in
//        // message.getActualChannel()
//        }
//        else {
//        // Message has been received on channel stored in
//        // message.getSubscribedChannel()
//        }
//
//            /*
//                log the following items with your favorite logger
//                    - message.getMessage()
//                    - message.getSubscribedChannel()
//                    - message.getTimetoken()
//            */
//        }
//
//@Override
//public void presence(PubNub pubnub, PNPresenceEventResult presence) {
//
//        }

//    ObjectMapper mapper = new ObjectMapper();
//    BufferedReader fileReader = new BufferedReader(new FileReader(projPath));
//
//    JsonNode rootNode = mapper.readTree(fileReader);
//
//    //Upgrade our file in memory
//    applyVersioningStrategy(rootNode);
//
//    ProjectModel project = mapJsonNodeToProject(rootNode);
