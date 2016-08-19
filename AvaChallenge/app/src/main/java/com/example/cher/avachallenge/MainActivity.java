package com.example.cher.avachallenge;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String SUBSCRIBE_KEY = "sub-c-897a7150-da55-11e5-9ce2-0619f8945a4f";
    private static final String PUBLISH_KEY = "pub-c-6590f75c-b2bb-4acc-9922-d5fe5aa8dec9";
    private static final String CHANNEL = "00001c72";
    private PNConfiguration pnConfiguration;
    private PubNub pubnub;
    private List<AvaMessage> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeAPI();
        messageArrayList = new ArrayList<AvaMessage>();
        Log.i(TAG, "***************onCreate: OG messageArrayList " + messageArrayList.size());
        setListeners();
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

        Log.i(TAG, "******************initializeAPI: getUuid default " + pnConfiguration.getUuid());
        Log.i(TAG, "******************initializeAPI: setUuid to cherence" + pnConfiguration.setUuid("Cherence"));
        Log.i(TAG, "******************initializeAPI: getUuid after hardcoding" + pnConfiguration.getUuid());
    }


    private void setListeners(){

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

                }
                else {
                    // Message has been received on channel stored in
                    // message.getSubscribedChannel()
                    //use message.getmessage to get the jsonnode object.
                    Log.i(TAG, "*******************GETmessage has been received on channel group stored in message.getSubscribedChannel: " + message.getMessage());
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        AvaMessage avaMessage;
                        avaMessage = objectMapper.treeToValue(message.getMessage(), AvaMessage.class);
                        Log.i(TAG, "**************message: check if avaMessage created TRANSCRIPT " + avaMessage.getTranscript());
                        Log.i(TAG, "**************message: check if avaMessage created SPEAKERID " + avaMessage.getSpeakerId());
                        Log.i(TAG, "**************message: check if avaMessage created REQUESTCOMMAND " + avaMessage.getRequestCommand());
                        Log.i(TAG, "**************message: check if avaMessage created BLOCID " + avaMessage.getBlocId());
                        messageArrayList.add(avaMessage);
                        Log.i(TAG, "*************message: arrayList exists and has a size of " + messageArrayList.size());
                    }
                    catch (JsonParseException e) {
                        e.printStackTrace(); }
                    catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult presence) {

            }
        });

    }

    private void subscribeToChannel(){
        pubnub.subscribe()
                .channels(Arrays.asList(CHANNEL))
//                .withPresence() // also subscribe to related presence information MIGHT NOT NEED
                .execute();
    }
}

