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

import com.google.sps.data.ShoppingQueryInput;
import java.io.IOException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** 
 * Queries Google Shopping with the given input.
 */
public class GoogleShoppingQuerier {

  // The parameters for {@code GOOGLE_SEARCH_BASE_URL} define the following:
  // "tbm" defines the type of search;
  // "tbs" defines advanced search parameters;
  // "safe" defines the level of filtering for adult content;
  private final String GOOGLE_SEARCH_BASE_URL =
      "https://www.google.com/search?tbm=shop&tbs=vw:l&safe=active&source=h";

//   public String query(String shoppingQuery) throws IOException, HttpStatusException {
  public String query(ShoppingQueryInput shoppingQueryInput) throws IOException, HttpStatusException {
    String shoppingQuery = shoppingQueryInput.getShoppingQuery();

    if (!isValidShoppingQuery(shoppingQuery)) {
      throw new IOException("Invalid Shopping query.");
    }

    shoppingQuery = polishShoppingQuery(shoppingQuery);

    String query = "q=" + shoppingQuery;
    // "num" parameter defines the maximum number of results to return.
    String maxResultsNumber = "num=" + String.valueOf(shoppingQueryInput.getMaxResultsNumber());
      // "hl" parameter defines the language to use for the Google search.
    String language = "hl=" + shoppingQueryInput.getLanguage();
    
    String searchURL = GOOGLE_SEARCH_BASE_URL + "&" + query + "&" + language + "&" + maxResultsNumber;

    Document doc;
    try {
      // Without proper User-Agent, it will result in a 403 error.
      doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
    } catch (HttpStatusException e) {
      throw new HttpStatusException(
          "Error while getting results from Google search", e.getStatusCode(), e.getUrl());
    }

    // Send back the HTML code of the search results.
    return doc.html();
  }

  private boolean isValidShoppingQuery(String shoppingQuery) {
    if (shoppingQuery.isEmpty()) {
      return false;
    }

    if (shoppingQuery == null) {
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
    return shoppingQuery;
  }
}
