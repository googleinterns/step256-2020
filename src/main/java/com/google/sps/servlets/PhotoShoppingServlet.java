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

import com.google.sps.data.GoogleShoppingResultsWrapper;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/photo-shopping-request")
public class PhotoShoppingServlet extends HttpServlet {
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO: Based on request.getParameter("photo-category"), call methods from photo detection classes, 
    // passing request.getParameter("blob-key") as argument. These methods build the shopping query and call 
    // the {@code getShoppingResultsPage} method from GoogleShoppingResultsWrapper.

    String query = getQuery(request.getParameter("photo-category"));
    response.setContentType("text/html");
    response.getWriter().println(GoogleShoppingResultsWrapper.getShoppingResultsPage(query));
  }

  private String getQuery(String photoCategory) {
    switch (photoCategory) {
      case "product":
        return "Fountain pen";
      case "list":
        return "Fuzzy socks";
      case "barcode":
        return "Running shoes";
      default:
        return "Notebook"; 
    } 
  }
}
