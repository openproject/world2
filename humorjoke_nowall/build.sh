#!/bin/bash
basedir=$(cd "$(dirname "$0")";pwd)
cd $basedir

#markets="google appchina waps gfan 91 hiapk goapk mumayi eoe nduo feiliu crossmo huawei qq 3g 360 baidu sohu 163 samsung coolmart meizu moto xiaomi lenovo nearme official dev"
markets="youmi 10020 goapk dev"
for market in $markets
do
    echo packaging humorjoke_1.0_$market.apk ...
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"UMENG_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    sed -i "s/\(android:value=\)\"\(.*\)\"\( android:name=\"YOUMI_CHANNEL\"\)/\1\"$market\"\3/g" AndroidManifest.xml
    ant -Dapk-name=humorjoke -Dapk-version=v1.0 -Dapk-market=$market
done
