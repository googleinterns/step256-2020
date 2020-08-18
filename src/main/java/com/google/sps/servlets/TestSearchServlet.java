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

@WebServlet("/test-search")
public class TestSearchServlet extends HttpServlet {

  public static final String GOOGLE_SEARCH_URL = "https://www.google.com//search?safe=strict&hl=en&tbm=shop&source=h&tbs=vw:l";//&tbs=vw:g for view and removes ads, tbm=shop means google shopping
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String searchURL = GOOGLE_SEARCH_URL + "&q=Shoe&num=3";
		//without proper User-Agent, we will get 403 error
		Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();
		
		//below will print HTML data, save it to a file and open in browser to compare
		//System.out.println(doc.html());
		
		//If google search results HTML change the <h3 class="r" to <h3 class="r1"
		//we need to change below accordingly
		// Elements results = doc.select("h3.r > a");

		// for (Element result : results) {
		// 	String linkHref = result.attr("href");
		// 	String linkText = result.text();
		// 	System.out.println("Text::" + linkText + ", URL::" + linkHref.substring(6, linkHref.indexOf("&")));
		// }

	response.setContentType("text/html");
    response.getWriter().println(doc.html());
  }
}