package com.google.sps.data;

import com.google.auto.value.AutoValue;

@AutoValue public abstract class Image{

  public static Image create(long id, String blobKey, long timestamp) 
{
 return new AutoValue_Image(id, blobKey, timestamp);
}  
  
  public abstract long getId();
  public abstract String blobKey();
  public abstract long timestamp();
}
