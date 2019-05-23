package com.guleryuz.puantajonline;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mehmet_erenoglu on 27.02.2017.
 */

public class GunlukPuantajData implements Serializable{
    private String globalid;
    private String userid, bolge, calismaalani, firma, yetkili, urun, ekiplideri, tarih, fisno, servispersonel;
    private HashMap<String,List<GunlukPersonelData>> gorevler;
    private HashMap<String,String> gorevMesai;
    private String gorevSelected, gorevIDSelected;
    private HashMap<String,String> servis;
    public String[] servisBilgileri ;
    private String sayiDoc1, sayiDoc2, sayiDoc3, sayiAciklama;
    private int calismavar;
    private String kayitdurumu;


    public GunlukPuantajData(String userid, String bolge, String calismaalani, String firma, String yetkili, String urun, String ekiplideri, String tarih, String fisno, String durumu){
        this.userid=userid;
        this.firma=firma;
        this.bolge=bolge;
        this.calismaalani=calismaalani;
        this.yetkili=yetkili;
        this.urun=urun;
        this.ekiplideri=ekiplideri;
        this.tarih=tarih;
        this.fisno=fisno;
        this.gorevler=new HashMap<String, List<GunlukPersonelData>>();
        this.gorevMesai=new HashMap<String, String>();
        this.servis=new HashMap<String, String>();
        this.calismavar=1;
        this.kayitdurumu=durumu;
    }

    public void setGlobalid(String f){this.globalid=f;}

    public void setFisno(String f){
        this.fisno=f;
    }

    public HashMap<String,List<GunlukPersonelData>> getGorevler(){
        return gorevler;
    }

    public void addGorev(String data){
        List<GunlukPersonelData> tmp = new ArrayList<GunlukPersonelData>();
        this.gorevler.put(data, tmp);
        this.gorevMesai.put(data,"");
    }

    public void addMesai(String gorev, String mesai){
        this.gorevMesai.put(this.gorevMesai.get(gorev),mesai);
    }

    public void addPersonel(String gorev, String sicilno, String adi, String cinsiyet, String mesai, String kartlaeklendi, String urun, String soyadi, String tc, String kartno){
        try {
            GunlukPersonelData tmp = new GunlukPersonelData(sicilno, adi, cinsiyet, mesai, kartlaeklendi, urun, soyadi, tc, kartno);

            List<GunlukPersonelData> tmp2 = this.gorevler.get(gorev);
            if (tmp2==null) {
                Log.w("here3", "null - "+gorev+" "+adi+" "+cinsiyet+" "+mesai);
                tmp2=new ArrayList<GunlukPersonelData>();
            }
            tmp2.add(tmp);
            this.gorevler.put(gorev, tmp2);
        }catch (Exception ex){
            Log.w("GunlukPuantajData", ex.getMessage());
        }
    }

    public String addPersonel(String gorev, GunlukPersonelData tmp){
        boolean stat=false;
        int index=-1;
        String g="";
        try {
            for ( String key : this.getGorevler().keySet() ) {
                List<GunlukPersonelData> gperd = this.gorevler.get(key);
                if(gperd!=null) {
                    for (int i = 0; i < gperd.size(); i++) {
                        if(gperd.get(i).sicilno.equals(tmp.sicilno)){
                            g=key;
                            index=i;
                            break;
                        }
                    }
                }
                if(!g.equals(""))
                    break;
            }

            if (index!=-1){
                this.gorevler.get(g).remove(index);
            }else{
                g=gorev;
            }

            List<GunlukPersonelData> tmp2 = this.gorevler.get(gorev);
            //tmp2.add(tmp);
            if (tmp2==null) {
                Log.w("here3", "null");
                tmp2=new ArrayList<GunlukPersonelData>();
            }

            /*for (int i=0; i<tmp2.size(); i++){
                if (tmp2.get(i).sicilno.equals(tmp.sicilno)){
                    index=i;
                    break;
                }
            }

            if(index!=-1){
                tmp2.remove(index);
            }*/

            tmp2.add(tmp);
            this.gorevler.put(gorev, tmp2);
            MainActivity.sendPersonels2Server();
            stat=true;
        }catch (Exception ex){
            stat=false;
            Log.w("GunlukPuantajData", ex.getMessage());
        }

        return g;
    }

    public void delPersonel(String gorev, String sicilno){
        try {
            List<GunlukPersonelData> tmp2 = this.gorevler.get(gorev);
            if (tmp2!=null) {
                int ind=-1;
                for (int i=0; i<tmp2.size(); i++){
                    if (tmp2.get(i).sicilno.equals(sicilno)){
                        ind=i;
                        break;
                    }
                }
                if (ind!=-1){
                    tmp2.remove(ind);
                    this.gorevler.put(gorev, tmp2);
                }
                MainActivity.sendPersonels2Server();
            }

        }catch (Exception ex){
            Log.w("GunlukPuantajData", ex.getMessage());
        }
    }

    public void setPersonelTopluMesai(String gorev, String mesai){
        try {
            for (int i=0; i<this.gorevler.get(gorev).size(); i++){
                this.gorevler.get(gorev).get(i).mesai=mesai;
            }
            MainActivity.sendPersonels2Server();
        }catch (Exception ex){
            Log.w("GPD-Mesai Guncelle", ex.getMessage());
        }
    }

    public int getGorevSize(){

        return this.gorevler.size();
    }

    public int getPersonelSize(String gorev){

        return (this.gorevler.get(gorev)!=null?this.gorevler.get(gorev).size():0);
    }

    public int[] getPersonelBayanErkek(String gorev){
        int num[]=new int[]{0,0};
        try {
            if(this.gorevler.containsKey(gorev)) {
                int maxInd = this.gorevler.get(gorev).size();
                for (int i = 0; i < maxInd; i++) {
                    if (this.gorevler.get(gorev).get(i).cinsiyet.equals("Erkek")) {
                        num[0]++;
                    } else {
                        num[1]++;
                    }
                }
            }
        }catch (Exception ex){
            Log.w("getPersonelBayanErkek", gorev+" "+ex.getMessage());
        }
        return num;
    }


    public List<GunlukPersonelData> getPersonel(String gorev){
        return this.gorevler.get(gorev);
    }

    public String getSelectedGorev(){
        return this.gorevSelected;
    }

    public String getSelectedGorevID(){
        return this.gorevSelected;
    }
    public void setSelectedGorev(String gorev, String gorevid){
        this.gorevIDSelected=gorevid;
        this.gorevSelected=gorev;
    }

    public void emptyServis(){
        this.servis=new HashMap<String, String>();
}

    public void addServis(String s, String ssayisi){
        this.servis.put(s, ssayisi);
    }

    public HashMap<String, String> getServis(){
        return this.servis;
    }

    public int getServisSayisi()
    {
        for (String key : this.servis.keySet()){
            Log.w("Servisx", key+" - "+this.servis.get(key));
        }
        //Servis Sayısı Yok -> 0, KisiBasi->-1, Diğer->sayi
        int ind=0;
        if(this.servis!=null){
            if(this.servis.size()==1){
                if(this.servis.containsKey("Yok")) {
                    ind = 0;
                }else if(this.servis.containsKey("KisiBasi")){
                    ind=-1;
                }else{
                    ind=1;
                }
            }else{
                ind=this.servis.size();
            }
        }

        return ind;
    }

    public String getUserid(){
        return userid;
    }

    public String getBolge(){
        return bolge;
    }

    public String getUrun(){
        return urun;
    }

    public String getCalismaalani(){
        return calismaalani;
    }

    public String getFirma(){
        return firma;
    }

    public String getYetkili(){
        return yetkili;
    }

    public String getEkiplideri(){
        return ekiplideri;
    }

    public String getTarih(){
        return tarih;
    }

    public String getTarihFormatli(){
        String[] ntarih = tarih.split(" / ");
        String ttarih= ntarih[2] + "-" + ntarih[1] + "-" + ntarih[0];
        return ttarih;
    }

    public String getFisno(){
        return fisno;
    }

    public String getGlobalid(){ return globalid; }

    public void setEkliDoc(String doc1, String doc2, String doc3, String ack){
        this.sayiDoc1=doc1;
        this.sayiDoc2=doc2;
        this.sayiDoc3=doc3;
        this.sayiAciklama=ack;
    }

    public String getAciklama(){
        return this.sayiAciklama;
    }

    public String getSayiDoc1(){
        return this.sayiDoc1;
    }

    public String getSayiDoc2(){
        return this.sayiDoc2;
    }
    public String getSayiDoc3(){
        return this.sayiDoc3;
    }

    public int getCalismavar(){
        return this.calismavar;
    }

    public void setCalismavar(int stat){
        this.calismavar=stat;
    }

    public String getKayitdurumu(){ return this.kayitdurumu; }
}

/*public class GunlukPuantajData implements Parcelable {
    private String bolge, calismaalani, firma, yetkili, ekiplideri, tarih, fisno;
    private List<String> gorev, gorevId;


    public GunlukPuantajData(String bolge, String calismaalani, String firma, String yetkili, String ekiplideri, String tarih, String fisno){
        this.firma=firma;
        this.bolge=bolge;
        this.calismaalani=calismaalani;
        this.yetkili=yetkili;
        this.ekiplideri=ekiplideri;
        this.tarih=tarih;
        this.fisno=fisno;
    }

    public GunlukPuantajData(Parcel in) {

        //String[] data = new String[3];
        //the order needs to be the same as in writeToParcel() method
        this.bolge=in.readString();
        this.calismaalani=in.readString();
        this.firma=in.readString();
        this.yetkili=in.readString();
        this.ekiplideri=in.readString();
        this.tarih=in.readString();
        this.fisno=in.readString();
    }

    public int describeContents() {
        return 0;
    }
    public void writeToParcel(Parcel out, int flags) {
        //out.writeStringArray(new String[] {this.bolge, this.calismaalani, this.firma});
        out.writeString(this.bolge);
        out.writeString(this.calismaalani);
        out.writeString(this.firma);
        out.writeString(this.yetkili);
        out.writeString(this.ekiplideri);
        out.writeString(this.tarih);
        out.writeString(this.fisno);
    }



intent.putExtra("student", new Student("1","Mike","6"));

Bundle data = getIntent().getExtras();
Student student = (Student) data.getParcelable("student");

    public static final Parcelable.Creator<GunlukPuantajData> CREATOR
            = new Parcelable.Creator<GunlukPuantajData>() {
        public GunlukPuantajData createFromParcel(Parcel in) {
            return new GunlukPuantajData(in);
        }

        public GunlukPuantajData[] newArray(int size) {
            return new GunlukPuantajData[size];
        }
    };
}*/
