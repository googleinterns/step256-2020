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

import com.google.sps.GoogleShoppingQuerier;
import com.google.sps.ShoppingQuerierConnectionException;
import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/text-search")
public class TextInputSearchServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Get the text shopping query.
    String shoppingQuery = request.getParameter("text-query");
    // if (shoppingQuery.isEmpty()) {
    //   response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing text query.");
    //   return;
    // }

    ShoppingQueryInput shoppingQueryInput =
        new ShoppingQueryInput.Builder(shoppingQuery).language("en").maxResultsNumber(24).build();

    // Initialize the Google Shopping querier.
    GoogleShoppingQuerier querier = new GoogleShoppingQuerier();

    List<Product> shoppingQuerierResults = new ArrayList<>();
    // try {
    //   shoppingQuerierResults = querier.query(shoppingQueryInput);
    // } catch (IllegalArgumentException
    //     | ShoppingQuerierConnectionException
    //     | IOException exception) {
    //   response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    //   return;
    // }

    // Convert {@code shoppingQuery} and products List - {@code shoppingQuerierResults} - into JSON
    // strings
    // using Gson library and send a JSON array with both of the JSON strings as response.
    Gson gson = new Gson();

    String shoppingQueryJSON = gson.toJson(shoppingQuery);
    String shoppingQuerierResultsJSON = gson.toJson(shoppingQuerierResults);
    response.setContentType("application/json;");
    response.getWriter().write("[" + shoppingQueryJSON + "," + shoppingQuerierResultsJSON + "]");
  }
}
