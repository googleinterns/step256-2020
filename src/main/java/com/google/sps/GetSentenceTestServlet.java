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
@WebServlet("/test")
public class GetSentenceTestServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {



  }

//   public boolean isInSameLine(int yCord, int prevYCord) {    
//     if (prevYCord == null) {
//         prevYCord = yCord;
//         return true;
//     }
//     if (yCord == prevYCord) {
//         return true;
//     }
//     return false;
//   }
}
    