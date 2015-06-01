# SNMPTRANS

## Introduction

SNMPTRANS is an equivalent of jmxtrans for SNMP metrics. It collects metrics and it injects these metrics in time series databases such as Graphite or BlueFlood. 

## Building

Build the jar using Maven:

    mvn clean install
    
## Running

When running the resulting jar, some options can be overridden on the command line by doing:

    -Delasticsearch=HOST:PORT
    -Drun.period=PERIOD_IN_MILLISECOND