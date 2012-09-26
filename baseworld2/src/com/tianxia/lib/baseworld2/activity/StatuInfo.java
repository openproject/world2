package com.tianxia.lib.baseworld2.activity;

import android.os.Parcelable;
import android.os.Parcel;

public class StatuInfo implements Parcelable
{

    public long id;
    public String created;
    public String author;
    public String avatar;
    public String name;
    public String text;
    public String pic_thumbnail;
    public String pic_middle;
    public String pic_original;
    public String from;

    public static final Parcelable.Creator<StatuInfo> CREATOR = new Creator<StatuInfo>() {
        @Override
        public StatuInfo createFromParcel(Parcel source) {
            StatuInfo statuInfo = new StatuInfo();
            statuInfo.id = source.readLong();
            statuInfo.created = source.readString();
            statuInfo.author = source.readString();
            statuInfo.avatar = source.readString();
            statuInfo.name = source.readString();
            statuInfo.text = source.readString();
            statuInfo.pic_thumbnail = source.readString();
            statuInfo.pic_middle = source.readString();
            statuInfo.pic_original = source.readString();
            statuInfo.from = source.readString();
            return statuInfo;
        }
        @Override
        public StatuInfo[] newArray(int size) {
            return new StatuInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(created);
        dest.writeString(author);
        dest.writeString(avatar);
        dest.writeString(name);
        dest.writeString(text);
        dest.writeString(pic_thumbnail);
        dest.writeString(pic_middle);
        dest.writeString(pic_original);
    }
}
