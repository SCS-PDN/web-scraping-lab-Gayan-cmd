import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebScraper {
    public static void main(String[] args) throws IOException {

        final String url = "https://www.bbc.com";
        Document doc = Jsoup.connect(url).get();

        System.out.println("Page Title: " + doc.title());

        System.out.println("\nHeadings:");
        for (int i = 1; i <= 6; i++) {
            Elements headings = doc.select("h" + i);
            for (Element heading : headings) {
                System.out.println("h" + i + ": " + heading.text());
            }
        }

        System.out.println("\nLinks:");
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            System.out.println(link.text() + " => " + link.absUrl("href"));
        }

        System.out.println("\nExtracting news headlines, dates and authors...");
        List<NewsArticle> newsList = extractBBCNewsData(url);

        for (NewsArticle article : newsList) {
            System.out.println(article);
        }
    }

    private static List<NewsArticle> extractBBCNewsData(String url) throws IOException {
        List<NewsArticle> newsArticles = new ArrayList<>();
        Document doc = Jsoup.connect(url).get();
        Elements newsHeadings = doc.select("h2[data-testid=card-headline]");

        for (Element headline : newsHeadings) {
            String title = headline.text();
            Element linkElement = headline.parent().selectFirst("a[href]");
            if (linkElement == null) continue;

            String link = linkElement.absUrl("href");
            if (link == null || link.isEmpty()) continue;

            try {
                Document articleDoc = Jsoup.connect(link).get();
                String date = articleDoc.select("time.sc-801dd632-2").attr("datetime");
                String author = articleDoc.select("span.sc-801dd632-7").text();

                NewsArticle news = new NewsArticle(title, date, author, link);
                newsArticles.add(news);
            } catch (IOException e) {
                continue;
            }
        }

        return newsArticles;
    }




    static class NewsArticle {
        private String title;
        private String publicationDate;
        private String author;
        private String url;

        public NewsArticle(String title, String publicationDate, String author, String url) {
            this.title = title;
            this.publicationDate = publicationDate;
            this.author = author;
            this.url = url;
        }

        public String toString() {
            return String.format("Title: %s\nDate: %s\nAuthor: %s\nURL: %s\n", title, publicationDate, author, url);
        }
    }
}

