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
import java.io.IOException;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/** 
 * Builds the shopping results page, by adding the product containers with the corresponding info.
 */
public class ShoppingResultsUIBuilder {

  /** 
   * @param products - array of Product objects, containing extracted data from scraping Google Shopping.
   * @param shoppingResultsDoc - the skeleton of the shopping results page.
   * @return - the completed shopping results page, having products info appended.
   */
  public Document buildShoppingResultsUI(List<Product> products, Document shoppingResultsDoc) {
    Element shoppingResultsWrapper = shoppingResultsDoc.select("#shopping-results-wrapper").first();

    // Integrate the products into the webpage.
    for (Product product : products) {
      // Create an HTML node for the item container.
      Element productContainer = new Element("div");
      productContainer.addClass("col-md-4");

      // Get the HTML content for the container.
      String productElementHTML = getProductElementHTML(product.getTitle(),
                                                        product.getImageLink(),
                                                        product.getPriceAndSeller(),
                                                        product.getLink(),
                                                        product.getShippingPrice());
      productContainer.append(productElementHTML);

      // Add the container to the results page, into the corresponding product wrapper.
      productContainer.appendTo(shoppingResultsWrapper);
    }
    return shoppingResultsDoc;
  }

  /**
   * Returns the HTML content for a product container, based on the arguments.
   */
  public String getProductElementHTML(String productTitle,
                                      String productImageLink,
                                      String productPriceAndSeller,
                                      String productLink,
                                      String productShippingPrice) {
    return 
          "<div class=\"card mb-4 shadow-sm\">"
        +   "<div class=\"col-4\">"
        +     "<img src=\"" + productImageLink + "\" class=\"mx-auto d-block\">"
        +   "</div>"
        +   "<div class=\"card-body\">"
        +     "<p class=\"card-text\">" + productTitle + "</p>"
        +     "<p class=\"card-text\">" + productPriceAndSeller + "</p>"
        +     "<div class=\"d-flex justify-content-between align-items-center\">"
        +       "<div class=\"btn-group\">"
        +         "<button type=\"button\" class=\"btn btn-sm btn-outline-secondary\""
        +                 "onclick=\"window.location.href='" + productLink + "'\">"
        +             "View"
        +         "</button>"
        +       "</div>"
        +       "<small class=\"text-muted\">" + productShippingPrice + "</small>"
        +     "</div>"
        +   "</div>"
        + "</div>";
  }
}
