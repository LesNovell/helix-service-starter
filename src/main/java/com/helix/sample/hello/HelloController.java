package com.helix.sample.hello;

import co.paralleluniverse.fibers.SuspendExecution;
import com.helix.sample.error.RequestFailedException;
import com.helix.feature.restservice.controller.HttpMethod;
import com.helix.feature.restservice.controller.Request;
import com.helix.feature.restservice.controller.Response;
import com.helix.feature.restservice.controller.annotation.Controller;
import com.helix.feature.restservice.controller.annotation.Endpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HelloController {
    private static final Logger LOG = LoggerFactory.getLogger(HelloController.class);

    private HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @Endpoint(value = "/sample/v1/hello/:name", methods = HttpMethod.GET)
    public Response<HelloResponse> sayHello(Request request) throws SuspendExecution {
        String name = request.getParam("name", "default name");

        LOG.info("Saying Hello to name=" + name);

        HelloResponse helloResponse = new HelloResponse();
        helloResponse.setSay(helloService.respond(name));

        return Response.successResponse(helloResponse);
    }

    @Endpoint(value = "/sample/v1/goodbye/:name", methods = HttpMethod.DELETE)
    public Response<String> sayGoodbye(Request request) throws SuspendExecution {
        String name = request.getParam("name", "default name");

        LOG.info("Saying Goodbye to name=" + name);
        return Response.successResponse("Goodbye " + name);
    }

    @Endpoint(value = "/sample/v1/error1", methods = HttpMethod.GET)
    public Response<HelloResponse> error1(Request request) {

        LOG.info("Generating RequestFailedException error");
        throw new RequestFailedException("Incorrect Request, Try Again");
    }

    @Endpoint(value = "/sample/v1/error2", methods = HttpMethod.GET)
    public Response<HelloResponse> error2(Request request) {

        LOG.info("Generating IllegalStateException error");
        throw new IllegalStateException("Error Message");
    }
}
