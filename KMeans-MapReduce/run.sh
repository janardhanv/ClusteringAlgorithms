#!/bin/bash
 
rm -r output/
clear
bin/hadoop jar KMeansMR.jar com.kmeansmr.KMeansMR ./input ./output

