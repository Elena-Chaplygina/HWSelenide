import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class SelenideTests {
    SelenideElement checkboxesButton = $x("//a[@href=\"/checkboxes\"]"),
            dropdownButton = $x("//a[@href=\"/dropdown\"]"),
            disappearingElementsButton = $x("//a[@href=\"/disappearing_elements\"]"),
            inputButton = $x("//a[@href=\"/inputs\"]"),
            hoversButton = $x("//a[@href=\"/hovers\"]"),
            notificationButton = $x("//a[@href=\"/notification_message\"]"),
            addRemoveButton = $x("//a[@href=\"/add_remove_elements/\"]"),
            statusCodeButton = $x("//a[@href=\"/status_codes\"]"),
            checkboxFirst = $x("//form[@id='checkboxes']/input[@type='checkbox'][1]"),
            checkboxSecond = $x("//form[@id='checkboxes']/input[@type='checkbox'][1]"),
            select = $x("//select[@id=\"dropdown\"]"),
            input = $x("//input"),
            notificationElement = $x("//div[@id='flash'][contains(text(), 'Action successful')]"),
            closeElement = $x("//a[@href=\"#\"]"),
            addElement = $x("//button[@onclick=\"addElement()\"]");


    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        open("https://the-internet.herokuapp.com/");

    }


    @DisplayName("Перейти на страницу Checkboxes. Выделить первый чекбокс, снять выделение со второго чекбокса. " +
            "Вывести в консоль состояние атрибута checked для каждого чекбокса.")

    @Test
    void checkCheckboxAndStateVerification() {
        checkboxesButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/checkboxes"));
        checkboxFirst.click();
        System.out.println("Checkbox 1 is checked: " + checkboxFirst.isSelected());
        checkboxSecond.click();
        System.out.println("Checkbox 2 is checked: " + checkboxSecond.isSelected());
    }


    @DisplayName("Перейти на страницу Dropdown. Выбрать первую опцию, вывести в консоль текущий текст элемента dropdown, выбрать вторую опцию, " +
            "вывести в консоль текущий текст элемента dropdown.")

    @Test
    void dropdownSelectionAndTextOutput() {
        dropdownButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/dropdown"));
        select.selectOption(1);
        String currentText = select.getText();
        System.out.println("текст после выбора первой опции: " + currentText);
        select.selectOption(2);
        currentText = select.getText();
        System.out.println("текст после выбора первой опции: " + currentText);
    }


    @DisplayName("Перейти на страницу Disappearing Elements. Добиться отображения 5 элементов, максимум за 10 попыток, " +
            "если нет, провалить тест с ошибкой.")

    @Test
    void disappearingElementsVisibility() {
        disappearingElementsButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/disappearing_elements"));
        int count = 0;
        boolean elementsVisible = false;
        while (count < 10) {
            ElementsCollection elements = $$x("//ul/li");
            if (elements.size() >= 5) {
                elementsVisible = true;
                System.out.println("Элементы отображаются:");
                break;
            } else {
                System.out.println("Попытка " + (count + 1) + ": Найдено " + elements.size() + " элементов. Повторяем...");
                refresh();
                count++;
            }
        }
        if (!elementsVisible) {
            throw new AssertionError("Тест провален: Не удалось отобразить 5 элементов за 10 попыток.");
        }
    }


    @DisplayName("Перейти на страницу Inputs. Ввести любое случайное число от 1 до 10 000. Вывести в консоль значение элемента Input.")

    @Test
    void inputFieldRandomNumber() {
        inputButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/inputs"));
        input.setValue("45145");
        String value = input.getValue();
        System.out.println("Поле содержит значение " + value);
    }


    @DisplayName("Перейти на страницу Hovers. Навести курсор на каждую картинку. Вывести в консоль текст, который появляется при наведении.")

    @Test
    void verifyHoverTextOnImages() {
        hoversButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/hovers"));
        for (int i = 1; i <= 3; i++) {
            $x("//div[@class=\"figure\"][" + i + "]").hover();
            String hoverText = $x("//div[@class=\"figure\"][" + i + "]").getText();
            System.out.println("Текст при наведении на изображение " + i + ": " + hoverText);
        }
    }


    @DisplayName("Перейти на страницу Notification Message. Кликать до тех пор," +
            " пока не покажется уведомление Action successful. После каждого неудачного клика закрывать всплывающее уведомление.")

    @Test
    void verifyActionSuccessfulNotification() {
        notificationButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/notification_message_rendered"));
        boolean isNotificationDisplayed = false;
        while (!isNotificationDisplayed) {
            notificationButton.click();
            if (notificationElement.is(Condition.visible)) {
                isNotificationDisplayed = true;
            } else {
                closeElement.click();
                System.out.println("Закрываем уведомление");
            }
        }
    }


    @DisplayName("Перейти на страницу Add/Remove Elements. Нажать на кнопку Add Element 5 раз. С каждым нажатием выводить в консоль текст " +
            "появившегося элемента. Нажать на разные кнопки Delete три раза. Выводить в консоль оставшееся количество кнопок Delete и их тексты.")

    @Test
    void verifyAddRemoveElementsFunctionality() {
        addRemoveButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/add_remove_elements/"));
        for (int i = 1; i <= 5; i++) {
            addElement.click();
            SelenideElement element = $x("//div[@id=\"elements\"]/button[" + i + "]");
            String text = element.getText();
            System.out.println("Текст на появившейся кнопке " + text);
        }
        ElementsCollection deleteElements = $$x("//div[@id=\"elements\"]/button");
        deleteElements.get(4).click();
        List<String> textElements = deleteElements.texts();
        System.out.println("Осталось " + deleteElements.size() + " элементов. Элементы содержат следующий текст: " + String.join(", ", textElements));
        deleteElements.get(2).click();
        System.out.println("Осталось " + deleteElements.size() + " элементов. Элементы содержат следующий текст: " + String.join(", ", textElements));
        deleteElements.get(0).click();
        System.out.println("Осталось " + deleteElements.size() + " элементов. Элементы содержат следующий текст: " + String.join(", ", textElements));
    }


    @DisplayName("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, " +
            "вывести на экран текст после перехода на страницу статуса 200.")

    @Test
    void verifyStatusCode200() {
        statusCodeButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/status_codes"));
        $x("//a[@href=\"status_codes/200\"]").click();
        String fullText = $x("//p").getText();
        String statusText = fullText.split("\\.")[0] + ".";
        System.out.println(statusText);
    }


    @DisplayName("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, " +
            "вывести на экран текст после перехода на страницу статуса 301.")

    @Test
    void verifyStatusCode301() {
        statusCodeButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/status_codes"));
        $x("//a[@href=\"status_codes/301\"]").click();
        String fullText = $x("//p").getText();
        String statusText = fullText.split("\\.")[0] + ".";
        System.out.println(statusText);
    }


    @DisplayName("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, " +
            "вывести на экран текст после перехода на страницу статуса 404.")

    @Test
    void verifyStatusCode404() {
        statusCodeButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/status_codes"));
        $x("//a[@href=\"status_codes/404\"]").click();
        String fullText = $x("//p").getText();
        String statusText = fullText.split("\\.")[0] + ".";
        System.out.println(statusText);
    }


    @DisplayName("Перейти на страницу Status Codes. Кликнуть на каждый статус в новом тестовом методе, " +
            "вывести на экран текст после перехода на страницу статуса 500.")

    @Test
    void verifyStatusCode500() {
        statusCodeButton.click();
        webdriver().shouldHave(url("https://the-internet.herokuapp.com/status_codes"));
        $x("//a[@href=\"status_codes/500\"]").click();
        String fullText = $x("//p").getText();
        String statusText = fullText.split("\\.")[0] + ".";
        System.out.println(statusText);
    }


    @AfterEach
    void tearDown() {
        getWebDriver().quit();
    }
}
