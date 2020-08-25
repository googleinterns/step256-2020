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

package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Class responsible for returning the Google Shopping results for a given query 
 * in the form of an HTML page. 
 */
public class ShoppingResultsWrapper {

  private static final String GOOGLE_SEARCH_URL = "https://www.google.com//search";
  private static final String searchType = "tbm=shop";
  private static final String tbs = "tbs=vw:l"; // "tbs=vw" removes ads and specifies the viewing style.
  private static final String safetyCheck = "safe=strict";

  public static String getShoppingResultsPage() throws IOException {

    String languageParam = "hl=en";
    String source = "source=h";
    String query = request.getParameter("query");
    query = "q=" + query;
    String maxResultsNum = "num=12";

    String searchURL =
        GOOGLE_SEARCH_URL
            + "?"
            + searchType
            + "&"
            + tbs
            + "&"
            + safetyCheck
            + "&"
            + languageParam
            + "&"
            + source
            + "&"
            + query
            + "&"
            + maxResultsNum;

    // Without proper User-Agent, it will result in a 403 error.
    Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

    // Send back the HTML code of the search results.
    return doc.html();
  }
}
