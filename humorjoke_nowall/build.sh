#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

markets="hiapk goapk mumayi eoe nduo dev"
#markets="youmi 10020 goapk dev"
for market in $markets
do
    echo packaging humorjoke_2.0_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"YOUMI_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=humorjoke -Dapk-version=v2.0 -Dapk-market=$market
done
