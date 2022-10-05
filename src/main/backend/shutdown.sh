#!/bin/bash

PID=$(cat ./pid_basil)
kill -9 $PID
