#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

#markets="google official uc appchina waps gfan 91 hiapk goapk mumayi eoe nduo feiliu crossmo huawei qq 3g 360 baidu sohu 163 samsung coolmart meizu moto xiaomi lenovo nearme dev"
markets="google uc official dev"
for market in $markets
do
    echo packaging healthworld_2.1_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"WAPS_PID\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=healthworld -Dapk-version=v2.1 -Dapk-market=$market
done
