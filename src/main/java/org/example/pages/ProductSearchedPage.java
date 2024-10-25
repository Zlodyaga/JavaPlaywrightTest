package org.example.pages;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class ProductSearchedPage {

    private final Page page;

    public ProductSearchedPage(Page page) {
        this.page = page;
    }

    @Step("Получены продукты со страницы поиска")
    public List<String> getProducts() {
        List<String> productElements = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            // Формируем локатор для конкретного элемента
            String product = String.format("(//div[@data-component-type='s-search-result'])[%d]", i);
            productElements.add(product);
        }
        return productElements;
    }

    public String getTitle(String product) {
        return page.locator(product + "//span[@class='a-size-medium a-color-base a-text-normal']").innerText();
    }

    public List<String> getPricesKindle(String product) {
        List<String> prices = new ArrayList<>();
        Locator priceElements = page.locator(product +
                "//div[@class='a-row a-spacing-mini']//div[@class='a-row']" +
                "//a[@class='a-link-normal s-no-hover s-underline-text s-underline-link-text s-link-style a-text-normal']" +
                "//span[@class='a-price']/span[@class='a-offscreen']"
        );

        for (ElementHandle price : priceElements.elementHandles()) {
            prices.add(price.innerText());
        }
        return prices;
    }


    @Step("Получение имени автора")
    public String getAuthorName(String product) {
        StringBuilder authorsText = new StringBuilder();

        Locator authorAll = page.locator(product +
                "//div[@class='a-row']//span[@class='a-size-base' and not(contains(@class, 'a-color-secondary'))] |" + product +
                "//div[@class='a-row']//a[@class='a-size-base a-link-normal s-underline-text s-underline-link-text s-link-style' and not(contains(@class, 'a-text-bold'))]"
        );

        for (ElementHandle authorElement : authorAll.elementHandles()) {
            authorsText.append(authorElement.innerText().trim()).append(" ");
        }
        String author = authorsText.toString().trim();

        return author;
    }

    public boolean isBesteller(String product) {
        return page.locator(product).innerText().contains("Best Seller");
    }
}
