package com.helix.sample.hello;

import co.paralleluniverse.fibers.SuspendExecution;
import com.helix.sample.entity.HelloAuditTrailEntity;
import com.helix.feature.configuration.ConfigProperty;
import com.helix.feature.worker.BlockingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.Random;

public class HelloService {
    private static final Logger LOG = LoggerFactory.getLogger(HelloService.class);

    //
    // ConfigProperty example
    //
    // This is an updatable configuration property.  If this value, is changed
    // the updated value will always be returned when using this property.
    //
    // Use ConfigProperties to reference a set of properties with a certain prefix
    //
    private ConfigProperty delayInMs = new ConfigProperty("service.hello.delay");

    // Note: Injected by JPA transaction manager
    EntityManager entityManager;


    public HelloService() {
        // Example of how to watch a change on properties, and take some action
        delayInMs.setChangeListener(newValue -> LOG.warn("Delay has been set to " + newValue + " ms"));
    }

    @Transactional
    @BlockingWorker
    public String respond(String name) throws SuspendExecution {
        String helloMessage = "Hello " + name;

        LOG.info("Executing blocking code on thread " + Thread.currentThread().getName());

        try {
            HelloAuditTrailEntity helloAuditTrailEntity = new HelloAuditTrailEntity();
            helloAuditTrailEntity.setId(new Random().nextLong());
            helloAuditTrailEntity.setTimestamp(new Timestamp(System.currentTimeMillis()));
            helloAuditTrailEntity.setMessageSent(helloMessage);

            // Blocking Operation Here
            entityManager.persist(helloAuditTrailEntity);
            Thread.sleep(delayInMs.asInt());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return helloMessage;
    }
}
