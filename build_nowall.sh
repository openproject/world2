#!/bin/bash

# get the file parent dir
basedir=$(cd "$(dirname "$0")";pwd)

# get project name by dir name
project=$(echo $basedir | awk -F "/" '{print $NF}')
project=${project/_nowall/}

# enter the right parent dir
cd $basedir

markets="youmi hiapk goapk lenovo huawei mumayi eoe nduo uc dev"
#markets="youmi 10020 goapk dev"
for market in $markets
do
    echo packaging $project.v2.0_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"YOUMI_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=$project -Dapk-version=v2.0 -Dapk-market=$market
done
