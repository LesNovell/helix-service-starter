package com.helix.sample;

import com.helix.core.feature.Feature;
import com.helix.core.server.HelixServer;
import com.helix.feature.accesslog.AccessLogFeature;
import com.helix.feature.configuration.ConfigurationFeature;
import com.helix.feature.configuration.locator.ClasspathResourceLocator;
import com.helix.feature.context.RequestContextFeature;
import com.helix.feature.health.HealthCheckFeature;
import com.helix.feature.jpa.JpaHibernateFeature;
import com.helix.feature.jpa.transaction.DeclarativeTransactionsFeature;
import com.helix.feature.metrics.MetricsFeature;
import com.helix.feature.restclient.RestClientFeature;
import com.helix.feature.restservice.RestServiceFeature;
import com.helix.feature.vertx.VertxNativeFeature;
import com.helix.feature.worker.BlockingWorkerFeature;
import com.helix.sample.configuration.SampleAppFeature;

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

        return new Feature[] { configurationFeature };
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
                vertxNativeFeature, restServiceFeature, requestContextFeature, restClientFeature, accessLogFeature,
                healthCheckFeature, jpaHibernateFeature, transactionFeature, blockingWorkerFeature, metricsFeature,
                sampleAppFeature };
    }

    private static void displayBanner() {
        try {
            System.out.println(new ClasspathResourceLocator().getString("banner.txt").get());
        } catch (Throwable t) {
            // Unable to load banner, it's okay...
        }
    }
}
