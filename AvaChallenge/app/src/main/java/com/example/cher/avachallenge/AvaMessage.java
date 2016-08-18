package com.example.cher.avachallenge;

import android.content.Intent;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by leisforkokomo on 8/17/16.
 */
public class AvaMessage {
    private String requestCommand;
    private String blocId; //might be long or Date instead of String
    private String speakerId; //might be int instead of String
    private String transcript;
    @SerializedName("final")
    private int finalInt;

    public AvaMessage() {
    }

    public AvaMessage(String requestCommand, String blocId, String speakerId, String transcript) {
        this.requestCommand = requestCommand;
        this.blocId = blocId;
        this.speakerId = speakerId;
        this.transcript = transcript;
    }

    public AvaMessage(String requestCommand, String blocId, String speakerId, String transcript, int finalInt) {
        this.requestCommand = requestCommand;
        this.blocId = blocId;
        this.speakerId = speakerId;
        this.transcript = transcript;
        this.finalInt = finalInt;
    }

    public String getRequestCommand() {
        return requestCommand;
    }

    public void setRequestCommand(String requestCommand) {
        this.requestCommand = requestCommand;
    }

    public String getBlocId() {
        return blocId;
    }

    public void setBlocId(String blocId) {
        this.blocId = blocId;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public int getFinalInt() {
        return finalInt;
    }

    public void setFinalInt(int finalInt) {
        this.finalInt = finalInt;
    }
}




/*
"requestCommand" : "room broadcast"
"blocId” : "1456254256252"
"speakerId": "00000004"
"transcript": "I am talking right now"
 */