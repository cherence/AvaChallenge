package com.example.cher.avachallenge;

import java.util.Date;

/**
 * Created by leisforkokomo on 8/17/16.
 */
public class AvaMessage {
    private String requestCommand;
    private String blocId; //might be long or Date instead of String
    private String speakerId; //might be int instead of String
    private String transcript;

    public AvaMessage() {
    }

    public AvaMessage(String requestCommand, String blocId, String speakerId, String transcript) {
        this.requestCommand = requestCommand;
        this.blocId = blocId;
        this.speakerId = speakerId;
        this.transcript = transcript;
    }
}


/*
"requestCommand" : "room broadcast"
"blocId” : "1456254256252"
"speakerId": "00000004"
"transcript": "I am talking right now"
 */