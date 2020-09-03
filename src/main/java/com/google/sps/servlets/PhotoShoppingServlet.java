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

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import com.google.gson.Gson;
import com.google.sps.DetectText;
import com.google.sps.GoogleShoppingQuerier;
import com.google.sps.ShoppingQuerierConnectionException;
import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.sps.PhotoShoppingException;

/** Generate a search query and return search results, for that query, to front-end. */
@WebServlet("/photo-shopping-request")
public class PhotoShoppingServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Based on HttpSession session variable's "photoCategory", call method from Detect text
    // class, passing {@code session.getAttribute("blobKeyString")} as argument. This method will
    // build the shopping query and
    // call the {@code query} method from GoogleShoppingQuerier.

    // Get the session, which contains user-specific data
    HttpSession session = request.getSession();

    List<String> shoppingQuery =new ArrayList<>();
    try {
        shoppingQuery = getQuery(
            session.getAttribute("photoCategory").toString(),
            session.getAttribute("blobKeyString").toString());
    } catch (PhotoShoppingException exception) {
            response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
    System.out.println("Shopping query: " + shoppingQuery);

    // Build the shopping query input - set language and maxResultsNumber to hard-coded values for
    // now.
    List<ShoppingQueryInput> inputs = new ArrayList<>();
    for (String query : shoppingQuery) {
      inputs.add(new ShoppingQueryInput.Builder(query).language("en").maxResultsNumber(18).build());
    }

    // Initialize the Google Shopping querier.
    GoogleShoppingQuerier querier = new GoogleShoppingQuerier();

    List<List<Product>> shoppingQuerierResults = new ArrayList<>();
    List<Product> result = new ArrayList<>();
    for (ShoppingQueryInput input : inputs) {
      try {
        result = querier.query(input);
      } catch (IllegalArgumentException
          | ShoppingQuerierConnectionException
          | IOException exception) {
        response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
      }
      shoppingQuerierResults.add(result);
    }

 

    // Convert products List into a JSON string using Gson library and
    // send the JSON as the response.
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(shoppingQuerierResults));
  }

  private List<String> getQuery(String photoCategory, String blobKeyString) throws IOException, PhotoShoppingException {
    List<String> result = new ArrayList<>();
    switch (photoCategory) {
      case "product":
        result.add("Fountain pen");
        break;
      case "shopping-list":
        DetectText detectText = new DetectText();
        return detectText.productDetection(blobKeyString);
      case "barcode":
        result.add("Cotton candy");
        break;
      default:
        result.add("Tooth Brush");
    }
    return result;
  }
}
