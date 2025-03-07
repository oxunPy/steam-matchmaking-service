package org.example.steammatchmakingservice.response;

public class MatchmakingResponse {
    public enum Status {
        WAIT_TIME,
        SUCCESS,
        FAILED,
        INTERNAL_SERVER_ERROR
    }

    private String sessionId;

    private String message;

    private Status status;

    private int maxPlayers;

    public static Builder builder() {
        return new Builder(new MatchmakingResponse());
    }

    public static class Builder {
        private MatchmakingResponse ptr;

        public Builder(MatchmakingResponse p) {
            ptr = p;
        }

        public Builder sessionId(String sessionId) {
            ptr.setSessionId(sessionId);
            return this;
        }

        public Builder message(String message) {
            ptr.setMessage(message);
            return this;
        }

        public Builder status(Status status) {
            ptr.setStatus(status);
            return this;
        }

        public MatchmakingResponse build() {
            return ptr;
        }
    }

    public static MatchmakingResponse waitingForPlayers(String sessionId, int maxPlayers) {
        MatchmakingResponse response = new MatchmakingResponse();
        response.setSessionId(sessionId);
        response.setMaxPlayers(maxPlayers);
        response.setStatus(Status.WAIT_TIME);
        response.setMessage("Waiting for others to join...");
        return response;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
