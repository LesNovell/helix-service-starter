package com.helix.sample.hello;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HelloResponse {
    @JsonProperty("say")
    private String say;

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }
}
