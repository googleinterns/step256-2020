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
import java.util.List;
import com.google.sps.data.Product;

/** 
 * Class containing Google Shopping ShoppingResult item data.
 */
@AutoValue
public abstract class ShoppingResult {

  public static ShoppingResult create(String query, List<Product> products) {
    return new AutoValue_ShoppingResult(query, products);
  }

  public abstract String getQuery();
  public abstract List<Product> getProducts();
}
