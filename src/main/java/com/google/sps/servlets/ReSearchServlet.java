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

import com.google.sps.GoogleShoppingQuerier;
import com.google.sps.ShoppingQuerierConnectionException;
import com.google.sps.data.Product;
import com.google.sps.data.ShoppingQueryInput;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet("/re-search")
public class ReSearchServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {    
      
    // String shoppingQuery = request.getParameter("shoppingQuery");  
    
    response.setContentType("text/html");

    response.getWriter().println(request.getParameter("test"));

/*
    ShoppingQueryInput shoppingQueryInput = 
        new ShoppingQueryInput.Builder(shoppingQuery).language("en").maxResultsNumber(24).build();

    GoogleShoppingQuerier querier = new GoogleShoppingQuerier();

    List<Product> shoppingQuerierResults = new ArrayList<>();
    try {
      shoppingQuerierResults = querier.query(shoppingQueryInput);
    } catch (IllegalArgumentException | ShoppingQuerierConnectionException | IOException exception) {
      response.sendError(SC_INTERNAL_SERVER_ERROR, exception.getMessage());
      return;
    }
     
    // Convert {@code shoppingQuery} and products List - {@code shoppingQuerierResults} - into JSON strings 
    // using Gson library and send a JSON array with both of the JSON strings as response.
    Gson gson = new Gson();

    String shoppingQueryJSON = gson.toJson(shoppingQuery); 
    String shoppingQuerierResultsJSON = gson.toJson(shoppingQuerierResults); 
    response.setContentType("application/json;");
    response.getWriter().write("[" + shoppingQueryJSON + "," + shoppingQuerierResultsJSON + "]");
*/
  }
}
