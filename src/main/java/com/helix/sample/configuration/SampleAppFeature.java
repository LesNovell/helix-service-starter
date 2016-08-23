package com.helix.sample.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.helixservice.core.feature.AbstractFeature;
import io.helixservice.feature.context.RequestContextFeature;
import io.helixservice.feature.restclient.RestClientFeature;
import io.helixservice.feature.restservice.controller.component.ControllerComponent;
import io.helixservice.feature.restservice.controller.component.EndpointComponent;
import io.helixservice.feature.restservice.controller.component.ErrorHandlerComponent;
import io.helixservice.feature.restservice.marshal.JacksonMarshaller;
import com.helix.sample.error.ErrorHandlers;
import com.helix.sample.hello.HelloController;
import com.helix.sample.hello.HelloService;
import com.helix.sample.music.MusicSearchController;

import java.util.UUID;

import static io.helixservice.feature.restservice.controller.HttpMethod.DELETE;
import static io.helixservice.feature.restservice.controller.HttpMethod.GET;

public class SampleAppFeature extends AbstractFeature {

    public SampleAppFeature(RequestContextFeature requestContextFeature, RestClientFeature restClientFeature) {

        //
        // Setup header propagation rules
        //
        requestContextFeature
                .captureHeader("TMPS-Correlation-Id")
                .ifEmptyThenInitializeTo(() -> UUID.randomUUID().toString())
                .addToLogMessages()
                .propagateToOutgoingRequests()
                .copyAsResponseHeader();

        //
        // Create Controllers & Services
        //
        HelloService helloService = new HelloService();
        HelloController helloController = new HelloController(helloService);
        MusicSearchController musicSearchController = new MusicSearchController(restClientFeature.restClient());

        register(
                //
                // Add new controllers here...
                //
                ControllerComponent.fromAnnotationsOn(helloController),
                ControllerComponent.fromAnnotationsOn(musicSearchController),

                //
                // Add Lambda Endpoints (if preferred over annotations)
                //
                EndpointComponent
                        .forPath("/sample/v1/hello/lambda/:name")
                        .handle(GET, helloController::sayHello)
                        .handle(DELETE, helloController::sayGoodbye),

                //
                // Add Error Handlers
                //
                ErrorHandlerComponent.forAllPaths()
                        .handle(ErrorHandlers::requestFailedException)
                        .handle(ErrorHandlers::anyOtherException),

                //
                // Add new filters here... (in order of invocation)
                //
                //  FilterDefinition
                //          .filterAllPaths(new ExampleFilter()),

                //
                // Customize marshaller here
                // Default is Jackson+JSON
                //
                new JacksonMarshaller(new ObjectMapper())

        );
    }
}
