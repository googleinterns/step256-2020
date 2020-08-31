// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.data;

import com.google.auto.value.AutoValue;

/** 
 * Class containing Google Shopping product item data.
 */
@AutoValue
public abstract class Product {

  public static Product create(String title, 
                               String imageLink, 
                               String priceAndSeller,
                               String link,
                               String shippingPrice) {
    return new AutoValue_Product(title, imageLink, priceAndSeller, link, shippingPrice);
  }

  public abstract String getTitle();
  public abstract String getImageLink();
  public abstract String getPriceAndSeller();
  public abstract String getLink();
  public abstract String getShippingPrice();
}
