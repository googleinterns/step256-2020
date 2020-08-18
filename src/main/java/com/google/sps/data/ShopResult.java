package com.google.sps.data;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ShopResult {

  public static ShopResult create(String imgSrc,String price, String link, String title) {
    return new AutoValue_ShopResult(imgSrc, price, link, title);
  }  
  
  public abstract String imgSrc();
  public abstract String  price();
  public abstract String link();
  public abstract String title();
}
