package org.example.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;
import io.qameta.allure.Step;

public class HomePage {

    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    @Step("Перейти на https://www.amazon.com/")
    public void open() {
        page.navigate("https://www.amazon.com/");
    }

    @Step("Установить фильтр Book")
    public void selectDropDown(String choose) {
        page.selectOption("[aria-describedby='searchDropdownDescription']", new SelectOption().setLabel(choose));
    }

    @Step("Ввести поисковое слово Java")
    public void enterSearch(String text) {
        page.fill("#twotabsearchtextbox", text);
        page.press("#twotabsearchtextbox", "Enter");
    }
}
