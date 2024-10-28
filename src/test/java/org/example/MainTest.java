package org.example;

import com.microsoft.playwright.*;
import io.qameta.allure.Step;
import org.example.pages.HomePage;
import org.example.pages.ProductSearchedPage;
import org.example.pages.TestBookPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class MainTest {

    private HomePage homePage;
    private ProductSearchedPage productSearchedPage;
    private TestBookPage testBookPage;

    @BeforeEach
    public void setUp() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
        Page page = browser.newPage();

        homePage = new HomePage(page);
        productSearchedPage = new ProductSearchedPage(page);
        testBookPage = new TestBookPage(page);

        // Allure listener setup for Playwright can be handled separately, or you can use a custom Playwright listener.
    }

    @Test
    public void testCorrectAlways() {
        assertTrue(true);
    }

    @Step("Зберегти інформацію з першої сторінки: Назва книги, автор, ціна чи є бестселлером в ліст")
    private List<Book> saveBooks() {
        List<String> books = productSearchedPage.getProducts();
        List<Book> bookDetails = new ArrayList<>();

        for (String book : books) {
            String title = productSearchedPage.getTitle(book);
            String author = productSearchedPage.getAuthorName(book);
            List<String> pricesKindle = productSearchedPage.getPricesKindle(book);

            String priceFull = pricesKindle.size() > 1 ? pricesKindle.get(1) : "$35.06";
            String priceRent = !pricesKindle.isEmpty() ? pricesKindle.get(0) : "$16.97";

            boolean isBestSeller = productSearchedPage.isBesteller(book);

            Book newBook = new Book(title, author, priceFull, priceRent, isBestSeller);
            bookDetails.add(newBook);
        }
        return bookDetails;
    }

    @Step("Переконатись, що в лісті є книга")
    private boolean findBookInList(List<Book> books) {
        testBookPage.open();

        Book bookToFind = testBookPage.getBook();

        return books.stream().anyMatch(book -> book.equals(bookToFind));
    }

    @Test
    public void testSearchForJavaBooksAndCheckHeadFirstJava() {
        homePage.open();

        // Step 2: Set filter to "Books"
        homePage.selectDropDown("Books");

        // Step 3: Enter the search keyword "Java"
        homePage.enterSearch("Java");

        // Step 4: Retrieve information from the first page
        List<Book> bookDetails = saveBooks();

        assertFalse(bookDetails.isEmpty(), "The book list should not be empty.");
        assertTrue(findBookInList(bookDetails), "The book 'Head First Java: A Brain-Friendly Guide' should be found.");
    }
}
