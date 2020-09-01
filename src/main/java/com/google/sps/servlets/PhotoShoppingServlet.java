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

import com.google.sps.GoogleShoppingQuerier;
import com.google.sps.ShoppingQuerierConnectionException;
import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jsoup.HttpStatusException;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

/** Generate a search query and return search results, for that query, to front-end. */
@WebServlet("/photo-shopping-request")
public class PhotoShoppingServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Based on HttpSession session variable's "photoCategory", call methods from photo detection
    // classes, passing {@code session.getAttribute("blobKeyString")} as argument. These methods build the 
    // shopping query and call the {@code query} method from GoogleShoppingQuerier.

    // Get the session, which contains user-specific data
	HttpSession session = request.getSession();

    String shoppingQuery = getQuery(session.getAttribute("photoCategory").toString());
    // Build the shopping query input - set language and maxResultsNumber to hard-coded values for now.
    ShoppingQueryInput input = 
        new ShoppingQueryInput.Builder(shoppingQuery).language("en").maxResultsNumber(20).build();

    // Initialize the Google Shopping querier.
    GoogleShoppingQuerier querier = new GoogleShoppingQuerier();
    
    response.setContentType("application/json;");
    
    List<Product> shoppingQuerierResults = new ArrayList<>();
    try {
      shoppingQuerierResults = querier.query(input);
    } catch(IllegalArgumentException | ShoppingQuerierConnectionException | IOException exception) {
      response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
     
    // Convert products List into a JSON string using Gson library and
    // send the JSON as the response.
    Gson gson = new Gson();

    response.getWriter().println(gson.toJson(shoppingQuerierResults));
  }

  private String getQuery(String photoCategory) {
    switch (photoCategory) {
      case "product":
        return "Fountain pen";
      case "shopping-list":
        return "Fuzzy socks";
      case "barcode":
        return "Running shoes";
      default:
        return "Notebook";
    }
  }
}
