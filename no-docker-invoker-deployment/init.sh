#!/bin/bash

# Licensed to the Apache Software Foundation (ASF) under one or more contributor
# license agreements; and to You under the Apache License, Version 2.0.

source environment-variables.sh
export INVOKER_OPTS="$INVOKER_OPTS $(./transformEnvironment.sh)"

./invoker/bin/invoker --id 0 --uniqueName 0 #>> ./logs/invoker0_logs.log 2>&1
