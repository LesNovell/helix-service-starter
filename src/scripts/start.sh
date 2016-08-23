#!/usr/bin/env bash
java\
 -Xmx8g -Xms4g\
 -javaagent:aspectjweaver-1.8.8.jar\
 -javaagent:quasar-core-0.7.6-jdk8.jar=m\
 -jar helix-service-template-sample-fat.jar


#
# Debugging options, which may be helpful:
#
# -Dco.paralleluniverse.fibers.verifyInstrumentation=true 
# -Daj.weaving.verbose=true 
# -Dorg.aspectj.weaver.showWeaveInfo=true 
# -Dco.paralleluniverse.debugMode=true 
# -Dco.paralleluniverse.globalFlightRecorder=true 
# -Dco.paralleluniverse.monitoring.flightRecorderLevel=1 
# -Dco.paralleluniverse.flightRecorderDumpFile=pulsar.log
