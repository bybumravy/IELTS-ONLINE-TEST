package web.ielts.Test.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Optional;

public class IpaScraperService {

    public Optional<String> getIpa(String word) {
        try {
            String url = "https://en.wiktionary.org/wiki/" + word;
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")  // tránh bị chặn
                    .get();

            // Lấy phần tử đầu tiên có class "IPA"
            Element ipaElement = doc.selectFirst("span.IPA");
            if (ipaElement != null) {
                return Optional.of(ipaElement.text());
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}

