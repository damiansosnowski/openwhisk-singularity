#!/bin/bash

for i in $(seq 1 20);
  do
  ../bin/wsk action create action$i $1 -i
done

for n in $(seq 1 20);
  do
  for i in $(seq 1 20);
    do
    sleep 0.2
    ../bin/wsk action invoke action$i -i --blocking | grep duration | sed 's/"duration"://' | sed 's/,//'; 
  done;
done;

for i in $(seq 1 20);
  do
  ../bin/wsk action delete action$i -i
done