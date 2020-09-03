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
import com.google.sps.data.ShoppingQueryInput;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import org.jsoup.HttpStatusException;
import org.jsoup.UnsupportedMimeTypeException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

/** 
 * Queries Google Shopping with the given input.
 */
public class GoogleShoppingQuerier {

  // The parameters for {@code GOOGLE_SEARCH_BASE_URL} define the following:
  // "tbm" defines the type of search;
  // "tbs" defines advanced search parameters;
  //       Here, "tbs=vw:l" sets that the items are displayed as list and not as grid.
  // "safe" defines the level of filtering for adult content;
  private final String GOOGLE_SEARCH_BASE_URL =
      "https://www.google.com/search?tbm=shop&tbs=vw:l&safe=active";

  /** 
   * Scrapes Google Shopping based on the input and returns the results.
   * @param ShoppingQueryInput object, containing fields for the values of search parameters.
   */
  public List<Product> query(ShoppingQueryInput shoppingQueryInput) throws IOException, ShoppingQuerierConnectionException {
    // Get the query to be searched and check for validity.
    String shoppingQuery = shoppingQueryInput.getShoppingQuery();

    if (!isValidShoppingQuery(shoppingQuery)) {
      throw new IllegalArgumentException("Invalid Shopping query.");
    }

    // Clean the valid query input before building {@code searchURL}.
    shoppingQuery = polishShoppingQuery(shoppingQuery);

    String query = "q=" + shoppingQuery;
    // "num" parameter defines the maximum number of results to return.
    String maxResultsNumber = "num=" + String.valueOf(shoppingQueryInput.getMaxResultsNumber());
      // "hl" parameter defines the language to use for the Google search.
    String language = "hl=" + shoppingQueryInput.getLanguage();
    
    String searchURL = GOOGLE_SEARCH_BASE_URL + "&" + query + "&" + language + "&" + maxResultsNumber;

    Response response = null;
    
    try {
      // Get the Connection for fetching content from the {@code searchURL}.
      response = Jsoup.connect(searchURL)
          .userAgent("Mozilla/5.0")
          .execute();
    } catch (MalformedURLException | 
             HttpStatusException | 
             UnsupportedMimeTypeException | 
             SocketTimeoutException exception) {
      // Re-throw the exception via ShoppingQuerierConnectionException custom exception.
      throw new ShoppingQuerierConnectionException(
          "Failed to fetch from Google Search.", exception);
    } catch (IOException exception) {
      // Re-throw IOException.
      throw exception;
    }

    // Here, after handling the exceptions, response.statusCode() is 200.
    // Parse the response object to obtain the document.
    Document doc = response.parse();

    // Extract product info from the document.
    ProductListExtractor productListExtractor = new ProductListExtractor();
    List<Product> products = productListExtractor.extract(doc);

    return products;
  }

  /** 
   * Checks if the query to be serched is a valid one.
   */
  private boolean isValidShoppingQuery(String shoppingQuery) {
    // Return false if the string does not contain at least one letter.
    if (!shoppingQuery.matches(".*[a-zA-Z].*")) {
      return false;
    }

    // TO DO: Check if the query defines something that cannot be purchased.

    return true;
  }

  /** 
   * Prepares the query input for Google Search and returns it.
   */
  private String polishShoppingQuery(String shoppingQuery) {
    shoppingQuery =
        shoppingQuery
            .replaceAll("\\s+", " ") // Remove duplicate spaces
            .trim() // Remove spaces from the beginning and end of string
            .replaceAll(" ", "%20") // Replace single space with "%20"
            .replaceAll("[-+=,\n._^\";:~#></|!*]", ""); // Remove special characters
System.out.println("Polished query: "+shoppingQuery);
    return shoppingQuery;
  }
}
