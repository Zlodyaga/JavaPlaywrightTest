package com.demo.pages.Ithillel;

import com.demo.core.base.PageTools;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class HomePage extends PageTools {

    public HomePage(Page page) {
        super(page);
    }

    private static String course = "//li[@class='block-course-cats_item' and a[div[p[normalize-space(text())='%s']]]]";

    @Step("Click course {course}")
    public void clickCourse(String value){
        click(course, value);
    }




}
