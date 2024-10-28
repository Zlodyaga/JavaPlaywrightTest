package org.example.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.example.Book;

import java.util.List;
import java.util.stream.Collectors;

public class TestBookPage {
    private final Page page;

    public TestBookPage(Page page) {
        this.page = page;
    }

    public void open() {
        page.navigate("https://www.amazon.com/Head-First-Java-Brain-Friendly-Guide/dp/1491910771");
    }

    public Book getBook() {
        String title = page.locator("//span[@id='productTitle']").innerText().trim();
        Locator authorCollection = page.locator("//div[@class='a-section a-spacing-micro bylineHidden feature']//span[@class='author notFaded']//a[@class='a-link-normal']");
        String author = getFromCollectionAuthor(authorCollection.elementHandles());

        String[] prices = page.locator("//a[@id='a-autoid-0-announce']//span[@class='slot-price']//span[@class='a-size-base a-color-secondary']")
                .innerText().split(" - ");
        String priceRent = prices[0].trim();
        String priceFull = prices.length > 1 ? prices[1].trim() : "";

        boolean isBestSeller = page.locator("//div[@id='centerAttributesLeftColumn']").innerText().contains("Best Seller");

        return new Book(title, author, priceFull, priceRent, isBestSeller);
    }

    private String getFromCollectionAuthor(List<ElementHandle> authorAll) {
        return authorAll.stream()
                .map(element -> element.innerText().trim())
                .collect(Collectors.joining(", "));
    }
}
