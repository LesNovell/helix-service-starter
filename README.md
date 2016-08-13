# Helix Sample Service

## Getting Started - Quick Code Overview
This sample code will help get you started with Helix. This section explains what
the sample code does.  From there, you can take this service and modify it to
your specific needs.

### Startup Configuration
**com.uapi.sample.Application**<br>
Contains the main() method to start Helix.  Also contains the configuration of
which features to include in this service instance.

**com.uapi.sample.configuration.SampleAppFeature**<br>
Contains the startup configuration for this sample application, including
registration of controllers, endpoints, error handlers.  When developing
a new component, you can add it here to register it with Helix.

### Controller
**com.uapi.sample.hello.HelloController**<br>
Simple hello controller, which echos back request parameter to the caller.

**com.uapi.sample.MusicSearchController**<br>
Music Search service, which takes a search term (for example: "Madonna") and
then uses non-blocking REST client API to iTunes to retrieve music that matches
the search term.  This is a simple example

### Service
**com.uapi.sample.hello.HelloService**<br>
Called by the HelloController, this service includes a simple JPA example
that creates an audit entry in a database.  The JPA example uses BlockingWorker
annotation, so that the blocking database calls will be asynchronously off-loaded
to a thread pool.

### Error Handler
**com.uapi.error.ErrorHandlers**<br>
Demonstrates error handling for different types of exceptions that might occur.

### Configuration Properties
Configurations are preferred to be located inside the distributed JAR file.
There is a base configuration directory, that will get loaded first.
Then, based upon the target environment, additional configuration gets overlaid from environment specific directory.

```
Package Structure:
 +- src/main/resources/config
      - /default/
            application.yaml      <-- Base config file gets loaded first
      - /dev/
            application.yaml      <-- Overrides config properties from Base for DEV environment
      - /preprod/
            application.yaml
      - /prod/
            application.yaml
      - /qa/
            application.yaml
```

Alternatively, configurations can be located on the file system.  Configurations loaded on the file system use the same
directory structure, and are loaded after the classpath configuration has been loaded.

By default, file system configs are in **{service_base_dir}/config**.  This path can be overriden with -Dapp.config parameter.

```
Service Directory Structure:
 +- /bin
 +- /config                       <-- Default config location, can be changed using -Dapp.config=<path>
      - /base/
            application.yaml      <-- Base config file gets loaded first
            application.json      <-- Custom Json config file, you may add additional resources
      - /dev/
            application.yaml      <-- Overrides config properties from Base
      - /preprod/
            application.yaml
      - /prod/
            application.yaml
      - /qa/
            application.yaml
```

### Setting Target Environment using Profiles
To set the target environment, select a profile using **-Dprofile=[name]** Java property.  The default profile is "**dev**".

Target environment should contain only the config values that differ from the base configuration.  This way,
defaults that apply to each environment do not have to be repeated.  Configurations for each environment are minimal.

### Secure Properties
Secure properties can be encoded in a configuration file or in the yaml file.
Secure Yaml properties should begin with {cipher} or end with .secret prefix.
A sample Spring Cloud Config decryption is implemented in Helix, and you can
add your own implementations for your particular decryption service.
