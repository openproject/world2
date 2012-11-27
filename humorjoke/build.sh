#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

#markets="google official appchina waps gfan 91 hiapk goapk mumayi eoe nduo feiliu crossmo huawei qq 3g 360 baidu sohu 163 samsung coolmart meizu moto xiaomi lenovo nearme dev"
markets="google hiapk official dev"
for market in $markets
do
    echo packaging humorjoke_2.0_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"WAPS_PID\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=humorjoke -Dapk-version=v2.0 -Dapk-market=$market
done
