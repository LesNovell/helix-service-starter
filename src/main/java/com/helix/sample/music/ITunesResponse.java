package com.helix.sample.music;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ITunesResponse {

    @JsonProperty("resultCount")
    private Integer resultCount;
    @JsonProperty("results")
    private List<ITunesResponseItem> results = new ArrayList<ITunesResponseItem>();

    public Integer getResultCount() {
        return resultCount;
    }

    public void setResultCount(Integer resultCount) {
        this.resultCount = resultCount;
    }

    public List<ITunesResponseItem> getResults() {
        return results;
    }

    public void setResults(List<ITunesResponseItem> results) {
        this.results = results;
    }


}
