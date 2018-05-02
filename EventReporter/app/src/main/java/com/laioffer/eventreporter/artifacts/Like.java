package com.laioffer.eventreporter.artifacts;

/**
 * Created by yukening on 2018/4/8.
 */

public class Like {
    private String LikeId;
    private String UserId;
    private String eventId;

    public String getLikeId() {
        return LikeId;
    }

    public void setLikeId(String likeId) {
        LikeId = likeId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}

