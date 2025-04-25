import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/ScrapeServlet")
public class ScrapeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String url = request.getParameter("url");
        String[] options = request.getParameterValues("options");

        List<Map<String, String>> resultList = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();

        if (options != null) {
            for (String option : options) {
                switch (option) {
                    case "title":
                        String title = doc.title();
                        Map<String, String> titleMap = new HashMap<>();
                        titleMap.put("Type", "Title");
                        titleMap.put("Content", title);
                        resultList.add(titleMap);
                        break;
                    case "links":
                        Elements links = doc.select("a[href]");
                        for (Element link : links) {
                            Map<String, String> linkMap = new HashMap<>();
                            linkMap.put("Type", "Link");
                            linkMap.put("Content", link.absUrl("href"));
                            resultList.add(linkMap);
                        }
                        break;
                    case "images":
                        Elements images = doc.select("img[src]");
                        for (Element img : images) {
                            Map<String, String> imgMap = new HashMap<>();
                            imgMap.put("Type", "Image");
                            imgMap.put("Content", img.absUrl("src"));
                            resultList.add(imgMap);
                        }
                        break;
                }
            }
        }

        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        session.setAttribute("visitCount", ++visitCount);
        request.setAttribute("visitCount", visitCount);

        request.setAttribute("data", resultList);
        Gson gson = new Gson();
        request.setAttribute("json", gson.toJson(resultList));
        request.getRequestDispatcher("/results.jsp").forward(request, response);
    }
}
