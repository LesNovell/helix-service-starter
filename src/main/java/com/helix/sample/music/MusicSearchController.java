package com.helix.sample.music;

import co.paralleluniverse.fibers.SuspendExecution;
import com.helix.sample.error.RequestFailedException;
import io.helixservice.feature.restclient.RestClient;
import io.helixservice.feature.restclient.RestResponse;
import io.helixservice.feature.restservice.controller.HttpMethod;
import io.helixservice.feature.restservice.controller.Request;
import io.helixservice.feature.restservice.controller.Response;
import io.helixservice.feature.restservice.controller.annotation.Controller;
import io.helixservice.feature.restservice.controller.annotation.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class MusicSearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicSearchController.class);

    private RestClient restClient;

    public MusicSearchController(RestClient restClient) {
        this.restClient = restClient;
    }

    @Endpoint(value = "/sample/v1/music/:searchTerm", methods = HttpMethod.GET)
    public Response<ITunesResponse> musicSearch(Request request) throws SuspendExecution {
        String searchTerm = request.getParam("searchTerm", "default term");

        LOGGER.info("Searching iTunes for music with searchTerm=" + searchTerm);

        RestResponse<ITunesResponse> response = restClient
                .get("http://{itunesServer}/search")
                .pathVariable("itunesServer", "itunes.apple.com")
                .parameter("term", searchTerm)
                .asObject(ITunesResponse.class);

        if (response.is200Successful()) {
            return Response.successResponse(response.getBody());
        } else {
            throw new RequestFailedException(response.getStatus() + " error occurred when contacting iTunes");
        }
    }
}
