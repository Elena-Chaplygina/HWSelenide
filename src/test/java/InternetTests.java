import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class InternetTests {
    SelenideElement keyPressesButton = $x("//a[@href=\"/key_presses\"]"),
            inputOnKeyPressesPage = $x("//input"),
            resultOnKeyPressesPage = $x("//p[@id=\"result\"]"),
            infiniteScrollButton = $x("//a[@href=\"/infinite_scroll\"]"),
            infiniteScrollPageFooter = $x("//div[@id=\"page-footer\"]"),
            contextMenuButton = $x("//a[@href=\"/context_menu\"]"),
            contextMenuItem = $x("//div[@id=\"hot-spot\"]"),
            dragAndDropButton = $x("//a[@href=\"/drag_and_drop\"]"),
            elementA = $x("//div[@id=\"column-a\"]"),
            elementB = $x("//div[@id=\"column-b\"]");

    ElementsCollection textOnScrollPage = $$x("//div[@class=\"jscroll-added\"]");

    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        open("https://the-internet.herokuapp.com/");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }


    @DisplayName("Drag and Drop")
    @Test
    void verifyDragAndDrop() {
        step("Переход на страницу Drag and Drop и проверка url", () -> {
            dragAndDropButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/drag_and_drop"));
        });
        step("Перемещение элемента А на место элемента В", () -> {
            actions().clickAndHold(elementA)
                    .moveToElement(elementB)
                    .release()
                    .perform();
        });
        step("Проверка что элементы перемещены", () -> {
            String newTextA = elementA.getText(),
                    newTextB = elementB.getText();
            assertEquals(newTextA, "B", "Элемент A должен стать B!");
            assertEquals(newTextB, "A", "Элемент B должен стать A!");
        });
    }


    @DisplayName("Context menu")
    @Test
    void verifyContextMenuAlert() {
        step("Переход на страницу Context Menu и проверка url", () -> {
            contextMenuButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/context_menu"));
        });
        step("Клик ПКМ на выделенной области", () -> {
            contextMenuItem.contextClick();
        });
        step("Проверка что текст соответствует ожидаемому", () -> {
            String alertText = switchTo().alert().getText();
            String expectedText = "You selected a context menu";
            assertEquals(expectedText, alertText);
        });
    }


    @DisplayName("Тест на странице Infinite Scroll")
    @Test
    void verifyInfiniteScroll() {
        step("Переход на страницу Key Presses и проверка url", () -> {
            infiniteScrollButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/infinite_scroll"));
        });
        step("Поиск выражения", () -> {
            boolean isContainText = false;
            int attempt=0;
            while (!isContainText && attempt < 30) {
                actions().scrollToElement(infiniteScrollPageFooter).perform();
                SelenideElement lastElement = textOnScrollPage.last();
                String elementText = lastElement.getText();
                isContainText = elementText.contains("Eius");
                if (isContainText) {
                    System.out.println("Текст найден!");
                    break;
                }
                attempt++;
            }
            if (!isContainText) {
                throw new AssertionError("Текст 'Eius' не найден после " + 30 + " попыток.");
            }
        });
    }


    @DisplayName("Тест на странице Key Presses")
    @Test
    void verifyKeyPressesFunc() {
        step("Переход на страницу Key Presses и проверка url", () -> {
            keyPressesButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/key_presses"));
        });
        step("Проверка латинских символов", () -> {
            String[] keysToPress = {"a", "l", "y", "h", "w", "e", "s", "q", "i", "x"};
            for (String key : keysToPress) {
                inputOnKeyPressesPage.sendKeys(key);
                resultOnKeyPressesPage.getText().equals("You entered: " + key.toUpperCase());
            }
        });
        step("Проверка Enter, Ctrl, Alt, Tab", () -> {
            inputOnKeyPressesPage.sendKeys(Keys.CONTROL);
            resultOnKeyPressesPage.getText().equals("You entered: CONTROL");
            inputOnKeyPressesPage.sendKeys(Keys.ALT);
            resultOnKeyPressesPage.getText().equals("You entered: ALT");
            inputOnKeyPressesPage.sendKeys(Keys.TAB);
            resultOnKeyPressesPage.getText().equals("You entered: TAB");
            inputOnKeyPressesPage.sendKeys(Keys.ENTER);
            resultOnKeyPressesPage.getText().equals("You entered: ENTER");
        });
    }


    @AfterEach
    void tearDown() {
        getWebDriver().quit();
    }
}
