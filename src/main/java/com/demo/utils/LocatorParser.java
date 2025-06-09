package com.demo.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.MissingFormatArgumentException;

public class LocatorParser {
    public static Locator parseLocator(Page page, String pattern, Object... args) {
        String formatted = formatPattern(pattern, args);

        // Если начинается с "/" или "//" — это XPath
        if (formatted.startsWith("/") || formatted.startsWith("//")) {
            return page.locator("xpath=" + formatted);
        }

        // Во всех остальных случаях — CSS-селектор
        return page.locator(formatted);
    }

    /**
     * Форматирует строку через String.format, пробрасывая понятную ошибку
     * в случае несоответствия количества аргументов.
     */
    private static String formatPattern(String pattern, Object... args) {
        try {
            return String.format(pattern.trim(), args);
        } catch (MissingFormatArgumentException e) {
            throw new MissingFormatArgumentException(
                    "Неверное количество аргументов для паттерна: `" + pattern + "`");
        }
    }
}
