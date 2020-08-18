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

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

  public static final String GOOGLE_SEARCH_URL = "https://www.google.com//search?safe=strict&hl=en&tbm=shop&source=h&tbs=vw:l";//&tbs=vw:g for view and removes ads, tbm=shop means google shopping
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String searchURL = GOOGLE_SEARCH_URL + "&q=Shoe&num=3";
		// Without proper User-Agent, we will get 403 error
		Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

        ArrayList<String> media_list =new ArrayList<>();
        ArrayList<String> imports_list =new ArrayList<>();
        ArrayList<String> links_list =new ArrayList<>();


        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");

        media_list.add("\nMedia: "+ media.size());
        for (Element src : media) {
            if (src./*normalName().*/equals("img"))
                media_list.add(String.format(" * %s: <%s> %sx%s (%s)",
                        src.tagName(), src.attr("abs:src"), src.attr("width"), src.attr("height"),
                        trim(src.attr("alt"), 20)));
            else
                media_list.add(String.format(" * %s: <%s>", src.tagName(), src.attr("abs:src")));
        }

        imports_list.add(String.format("\nImports: (%d)", imports.size()));
        for (Element link : imports) {
            imports_list.add(String.format(" * %s <%s> (%s)", link.tagName(),link.attr("abs:href"), link.attr("rel")));
        }

        links_list.add(String.format("\nLinks: (%d)", links.size()));
        for (Element link : links) {
            links_list.add(String.format(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35)));
        }

    List<List<String>> shop_results = new ArrayList<>();
    shop_results.add(media_list);
    shop_results.add(imports_list);
    shop_results.add(links_list);


    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(shop_results));
  }

  private static String trim(String s, int width) {
    if (s.length() > width) {
      return s.substring(0, width-1) + ".";
    } else {
      return s;
    }
  }

}
