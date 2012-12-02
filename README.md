百科"世界"系列应用
==============================

介绍
--------
- 一系列类应用：幽默笑话，玩转英语，星座星语 ...

使用说明
--------
- 1. Create a android keystore which should locate in world2 root dir for packaging project to apk
- 2. Define some properties by build.xml's var, named it keystore.properties to world2 root

分市场编译
--------
- 因为各市场不同要求原因，支持的广告模式不一样，主要是不太支持万普的积分墙，所以作为实例，分为两大分支：万普广告(包括积分墙)和无积分墙的有米广告。
- 一般每个主题都有两个工程：wall(省略了wall)和nowall。
- wall版本下的build.sh主要生成第一种万普广告，包括积分墙。
- nowall版本下的build.sh主要是生成第二种无积分墙的有米广告的APKs。
- 目前支持万普积分强的广告有：google，qq，appchina，gfan。
- 其他的不支持的有：
- 1. 安智一律封杀积分墙；
- 2. 联想乐商店封杀了万普的积分墙；
- 3. 安卓市场也开始封杀万普的积分墙了；
- 4. N多网封杀积分系统，推荐应用和广告自行下载的应用；
- 5. 木蚂蚁封杀一些太多见的应用，如“暂不收录笑话类应用”；
- 6. 华为智汇云不支持万普积分墙

技术支持
------------------------------------
- Member: 冯建
-   Site: <https://github.com/openproject/world2/>
-     QQ: 673592063
