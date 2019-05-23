package com.guleryuz.puantajonline;

import java.util.ArrayList;

/**
 * Created by mehmet_erenoglu on 28.02.2017.
 */

public class HeaderInfo {

    private String name;
    private ArrayList<DetailInfo> productList = new ArrayList<DetailInfo>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DetailInfo> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<DetailInfo> productList) {
        this.productList = productList;
    }
}
