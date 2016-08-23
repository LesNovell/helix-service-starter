package com.helix.sample;

import io.helixservice.core.feature.Feature;
import io.helixservice.core.server.HelixServer;
import io.helixservice.feature.accesslog.AccessLogFeature;
import io.helixservice.feature.configuration.ConfigurationFeature;
import io.helixservice.feature.configuration.locator.ClasspathResourceLocator;
import io.helixservice.feature.context.RequestContextFeature;
import io.helixservice.feature.health.HealthCheckFeature;
import io.helixservice.feature.jpa.JpaHibernateFeature;
import io.helixservice.feature.jpa.transaction.DeclarativeTransactionsFeature;
import io.helixservice.feature.metrics.MetricsFeature;
import io.helixservice.feature.restclient.RestClientFeature;
import io.helixservice.feature.restservice.RestServiceFeature;
import io.helixservice.feature.vertx.VertxNativeFeature;
import io.helixservice.feature.worker.BlockingWorkerFeature;
import com.helix.sample.configuration.SampleAppFeature;
import io.helixservice.feature.configuration.dynamo.DynamoConfigFeature;

public class Application {
    public static void main(String[] args) {
        displayBanner();

        HelixServer helixServer = new HelixServer(Application::bootstrapFeatures, Application::installedFeatures);
        helixServer.start();
    }

    public static Feature[] bootstrapFeatures() {
        ConfigurationFeature configurationFeature           = new ConfigurationFeature();

        // Enable Spring cloud config:
        // CloudConfigFeature cloudConfigFeature               = new CloudConfigFeature();
        DynamoConfigFeature dynamoConfigFeature              = new DynamoConfigFeature();

        return new Feature[] { configurationFeature, dynamoConfigFeature };
    }

    public static Feature[] installedFeatures() {
        // Features to be installed for this application
        VertxNativeFeature vertxNativeFeature               = new VertxNativeFeature();
        RequestContextFeature requestContextFeature         = new RequestContextFeature();
        AccessLogFeature accessLogFeature                   = new AccessLogFeature();
        HealthCheckFeature healthCheckFeature               = new HealthCheckFeature();
        JpaHibernateFeature jpaHibernateFeature             = new JpaHibernateFeature();
        DeclarativeTransactionsFeature transactionFeature   = new DeclarativeTransactionsFeature();
        BlockingWorkerFeature blockingWorkerFeature         = new BlockingWorkerFeature();
        MetricsFeature metricsFeature                       = new MetricsFeature();
        RestServiceFeature restServiceFeature               = new RestServiceFeature();
        RestClientFeature restClientFeature                 = new RestClientFeature(vertxNativeFeature);

        // Feature to present this application
        SampleAppFeature sampleAppFeature                   = new SampleAppFeature(requestContextFeature, restClientFeature);

        return new Feature[] {
                vertxNativeFeature, requestContextFeature, restClientFeature, accessLogFeature,
                healthCheckFeature, jpaHibernateFeature, transactionFeature, blockingWorkerFeature, metricsFeature,
                sampleAppFeature, restServiceFeature };
    }

    private static void displayBanner() {
        try {
            System.out.println(new ClasspathResourceLocator().getString("banner.txt").get());
        } catch (Throwable t) {
            // Unable to load banner, it's okay...
        }
    }
}
