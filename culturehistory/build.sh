#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

#markets="google appchina waps gfan 91 hiapk goapk mumayi eoe nduo feiliu crossmo huawei qq 3g 360 baidu sohu 163 samsung coolmart meizu moto xiaomi lenovo nearme official dev"
markets="juzi dev"
for market in $markets
do
    echo packaging healthworld_1.0_base_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"WAPS_PID\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=culturehistory -Dapk-version=v1.0_base -Dapk-market=$market
done
