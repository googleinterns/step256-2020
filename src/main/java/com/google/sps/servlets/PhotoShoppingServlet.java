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

import com.google.gson.Gson;
import com.google.sps.data.GoogleShoppingResultsWrapper;
import com.google.sps.data.DetectText;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Generate a search query and return search results, for that query, to front-end.
 */
@WebServlet("/photo-shopping-request")
public class PhotoShoppingServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Based on request.getParameter("photo-category"), call methods from photo detection classes, 
    // passing request.getParameter("blob-key") as argument. These methods build the shopping query and call 
    // the {@code getShoppingResultsPage} method from GoogleShoppingResultsWrapper.

    List<String> result = new ArrayList<>();
    List<String> queries = new ArrayList<>();
    queries = getQuery(request.getParameter("photo-category"), request.getParameter("blob-key"));
    for(String query : queries) {
        result.add(GoogleShoppingResultsWrapper.getShoppingResultsPage(query));
    }
System.out.println("queries:"+queries);
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(result));
  }

  private List<String> getQuery(String photoCategory, String blobKeyString) throws IOException {
    List<String> result = new ArrayList<>();
    switch (photoCategory) {
      case "product":
        result.add("Fountain pen");
        break;
      case "list":
        return DetectText.detectText(blobKeyString);
      case "barcode":
        result.add("Cotton candy");
        break;
      default:
        result.add("Tooth Brush");
    }
    return result;
  }
}
