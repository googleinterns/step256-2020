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

import java.io.IOException;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** Return the Google Shopping results for a given query in the form of an HTML page. */
public class GoogleShoppingQuerier {

  private String shoppingQuery;
  private final String GOOGLE_SEARCH_BASE_URL =
      "https://www.google.com/search?tbm=shop&tbs=vw:l&safe=strict&hl=en&source=h";
  // Above, "tbm=shop" is the search type; "tbs=vw" removes ads and specifies the viewing style;
  // "hl=en" is the language parameter and "safe=strict" is the parameter for safety check.

  public GoogleShoppingQuerier(String shoppingQuery) {
    this.shoppingQuery = shoppingQuery;
  }

  public String query() throws IOException, HttpStatusException {
    if (!isValidShoppingQuery(shoppingQuery)) {
      throw new IOException("Invalid Shopping query.");
    }

    shoppingQuery = cleanValidShoppingQuery(shoppingQuery);

    String query = "q=" + shoppingQuery;
    String maxResultsNum = "num=6";
    String searchURL = GOOGLE_SEARCH_BASE_URL + "&" + query + "&" + maxResultsNum;

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
    return true;
  }

  private String cleanValidShoppingQuery(String shoppingQuery) {
    shoppingQuery =
        shoppingQuery
            .replaceAll("\\s+", " ") // Remove duplicate spaces
            .trim() // Remove spaces from the beginning and end of string
            .replaceAll(" ", "%20") // Replace all spaces with '%20'
            .replaceAll("[ ,\n-._=+^\";:~#></|!*]", ""); // Remove all other special characters
    return shoppingQuery;
  }
}
