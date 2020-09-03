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

package com.google.sps;

import com.google.sps.data.Product;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 
 * Extracts item information for each product from Google Shopping results page.
 */
public class ProductListExtractor {

  /** 
   * @param {Document} doc   the HTML content of the Google Shopping results page.
   * @return {List<Product>} the array of Product objects containing the extracted 
   *    data about each item.
   */
  public List<Product> extract(Document doc) {
    // Get the product containers from the page, considering that each has the class "u30d4".
    Elements productElements = doc.select(".u30d4");

    // Initialize the array of product info extracted below.
    List<Product> products = new ArrayList<>();

    // Iterate through {@code productElements} and extract data for each product.
    Iterator<Element> iterator = productElements.iterator();
    while (iterator.hasNext()) {
      Element currentProduct = iterator.next();

      // Do not consider the last element with class ".u30d4", as this does not define a product.
      if (!iterator.hasNext()) { 
        continue;
      }

      String productImageLink = currentProduct.select(".oR27Gd > img").attr("src");
      
      String extractedProductLink = currentProduct.select(".rgHvZc > a").attr("href");
      // Fix product link - if the URL starts with '/url?q=', the URL redirection will not work.
      String wrongStartOfLink = "/url?q=";
      // Therefore delete the start if this is the case, for the redirection to successfully work.
      String productLink;
      if (extractedProductLink.substring(0, wrongStartOfLink.length()).equals(wrongStartOfLink)) {
        productLink = extractedProductLink.substring(wrongStartOfLink.length());
      } else {
        productLink = extractedProductLink;
      }

      // Get the title as HTML instead of text, in order to keep the <b> tags.
      String productTitle = currentProduct.select(".rgHvZc > a").html();
  
      // As some products do not have rating, the classes order may differ, therefore
      // define {@code productPriceAndSeller} for both cases.
      String productPriceAndSeller;
      String productRatingInStars;
      // If the product does not have a rating container, 
      if (currentProduct.select(".dD8iuc:nth-of-type(3)").html().isEmpty()) {
        // only get the price and seller container.
        productPriceAndSeller = currentProduct.select(".dD8iuc:nth-of-type(2)").html();
      } else {
        // Else, assign both values.
        productRatingInStars = currentProduct.select(".dD8iuc:nth-of-type(2)").text();
        productPriceAndSeller = currentProduct.select(".dD8iuc:nth-of-type(3)").html();
      }

      String productShippingPrice = currentProduct.select(".dD8iuc:nth-of-type(1)").text();

      // Build the Product object.
      Product product = Product.create(productTitle, 
                                       productImageLink, 
                                       productPriceAndSeller, 
                                       productLink, 
                                       productShippingPrice);
      products.add(product);
    }

    return products;
  }
}
