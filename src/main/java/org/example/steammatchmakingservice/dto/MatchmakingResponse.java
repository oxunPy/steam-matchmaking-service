package org.example.steammatchmakingservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchmakingResponse {
    private String responseText;


    public MatchmakingResponse(String res) {
        responseText = res;
    }

    public MatchmakingResponse() {}
}
