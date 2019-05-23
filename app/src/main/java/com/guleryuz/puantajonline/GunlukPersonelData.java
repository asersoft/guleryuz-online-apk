package com.guleryuz.puantajonline;

/**
 * Created by Asersoft on 28.02.2017.
 */

public class GunlukPersonelData{
    public String sicilno;
    public String adi;
    public String soyadi;
    public String tc;
    public String cinsiyet;
    public String urun;
    public String mesai;
    public String kartlaeklendi;
    public String kartno;

    public GunlukPersonelData(String s, String ad, String cins, String m, String kartlaeklendi, String urun, String soyadi, String tc, String kartno){
        this.sicilno=s;
        this.adi=ad;
        this.cinsiyet=cins;
        this.mesai=m;
        this.kartlaeklendi=kartlaeklendi;
        this.urun=urun;
        this.soyadi=soyadi;
        this.tc=tc;
        this.kartno=kartno;
    }
}
