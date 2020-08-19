package com.journaldev.jsoup;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/search-shopping-results")
public class ShoppingResultsServlet extends HttpServlet {

  public static final String GOOGLE_SEARCH_URL = 
      "https://www.google.com//search?safe=strict&hl=en&tbm=shop&source=h&tbs=vw:l";  // &tbs=vw:g for view and removes ads, tbm=shop means google shopping
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String searchURL = GOOGLE_SEARCH_URL + "&q=Running%20Shoes%20for%20women&num=10";

		// Without proper User-Agent, we will get 403 error
		Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

	response.setContentType("text/html");
    response.getWriter().println(doc.html());
  }
}
