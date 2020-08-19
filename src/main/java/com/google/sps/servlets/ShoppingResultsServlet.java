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

@WebServlet("/search-shopping-results")
public class ShoppingResultsServlet extends HttpServlet {
  
  public static final String GOOGLE_SEARCH_URL = 
      "https://www.google.com//search";  
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String safetyCheck = "safe=strict";
    String languageParam = "hl=en";
    String searchType = "tbm=shop";
    String source = "source=h";
    String tbs = "tbs=vw:l"; // tbs=vw for view and removes ads
    String query = "&q=Running%20Shoes%20for%20women";
    String maxResultsNum = "num=8";

    String searchURL = GOOGLE_SEARCH_URL + "?" + safetyCheck + "&" + languageParam + "&" + searchType 
    + "&" +source + "&" + tbs + "&" + query + "&" + maxResultsNum;
    
    // Without proper User-Agent, it will result in a 403 error.
    Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

    response.setContentType("text/html");
    response.getWriter().println(doc.html());
  }
}
