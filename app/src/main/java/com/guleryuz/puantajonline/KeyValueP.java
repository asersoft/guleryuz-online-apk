package com.guleryuz.puantajonline;

/**
 * Created by Asersoft on 10.12.2016.
 */

public class KeyValueP
{
    public String ID;
    public String name;
    public int param;

    public KeyValueP(){

    }

    public KeyValueP(String i, String n){
        ID=i;
        name=n;
    }
    public KeyValueP(String i, String n, int p){
        ID=i;
        name=n;
        param=p;
    }

    @Override
    public String toString() {
        return name;
    }
}