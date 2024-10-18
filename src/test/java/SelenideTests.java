import com.codeborne.selenide.*;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.url;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;

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
            checkboxSecond = $x("//form[@id='checkboxes']/input[@type='checkbox'][2]"),
            select = $x("//select[@id=\"dropdown\"]"),
            input = $x("//input"),
            notificationElement = $x("//div[@id='flash']"),
            addElement = $x("//button[@onclick=\"addElement()\"]");


    @BeforeAll
    static void init() {
            Configuration.browser = "chrome";
            Configuration.remote = "http://localhost:4444/wd/hub";
    }

    @BeforeEach
    void setup() {
        Configuration.browser = "chrome";
        open("https://the-internet.herokuapp.com/");
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide()
                .screenshots(true)
                .savePageSource(true));
    }


    @ParameterizedTest(name = "Checkboxes. Порядок нажатия: {0}")
    @ValueSource(strings = {"forward", "back"})
    void checkCheckboxAndStateVerification(String order) {
        step("Переход на страницу Checkboxes и проверка url", () -> {
            checkboxesButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/checkboxes"));
        });
        if (order.equals("forward")) {
            step("Нажатие чекбоксов в порядке  checkbox 1, checkbox 2", () -> {
                checkboxFirst.click();
                checkboxFirst.shouldBe(checked);
                checkboxSecond.click();
                checkboxSecond.shouldNotBe(checked);
            });
        } else {
            step("Нажатие чекбоксов в порядке  checkbox 2, checkbox 1", () -> {
                checkboxSecond.click();
                checkboxSecond.shouldNotBe(checked);
                checkboxFirst.click();
                checkboxFirst.shouldBe(checked);
            });
        }
        step("Вывод в консоль состояние чекбоксов", () -> {
            System.out.println("Checkbox 1 is checked: " + checkboxFirst.getAttribute("checked"));
            System.out.println("Checkbox 2 is checked: " + checkboxSecond.getAttribute("checked"));
        });
    }


    @CsvSource({
            "1, Option 1",
            "2, Option 2"
    })
    @ParameterizedTest(name = "Dropdown При выборе значения {0} состояние - {1}")
    void dropdownSelectionAndTextOutput(int i, String value) {
        step("Переход на страницу Dropdown и проверка url", () -> {
            dropdownButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/dropdown"));
        });
        step("Выбор dropDown", () -> {
            select.selectOption(i);
        });
        step("Проверка dropDown", () -> {
            select.should(Condition.text(value));
            System.out.println("текст после выбора первой опции: " + select.getText());
        });
    }


    @RepeatedTest(value = 10, name = "Disappearing Elements Попытка {currentRepetition}")
    void disappearingElementsVisibility() {
        step("Переход на страницу Disappearing Elements и проверка url", () -> {
            disappearingElementsButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/disappearing_elements"));
        });
        step("Проверка количества элементов на странице", () -> {
            ElementsCollection elements = $$x("//ul/li");
            elements.should(CollectionCondition.size(5));
            System.out.println("Страница содержит 5 элементов");
        });
    }


    @TestFactory
    List<DynamicTest> inputFieldRandomNumber() {
        step("Переход на страницу Inputs и проверка url", () -> {
            inputButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/inputs"));
        });
        List<DynamicTest> tests = new ArrayList<>();
        String[] negativeInputs = {"abc", "@#$%", "\n", " ", "йцуке"};
        for (String invalidInput : negativeInputs) {
            tests.add(DynamicTest.dynamicTest("Inputs Негативный тест: Попытка ввести '" + invalidInput + "'", () -> {
                input.clear();
                input.sendKeys(invalidInput);
                input.shouldHave(value(""));
            }));
        }
        tests.add(DynamicTest.dynamicTest(
                "Inputs Проверка удаления пробелов", () -> {
                    String testData = " 1 121  ";
                    input.clear();
                    input.sendKeys(testData);
                    input.shouldHave(value("1121"));
                }
        ));
        tests.add(DynamicTest.dynamicTest(
                "Inputs Проверка сохранения точки", () -> {
                    String testData = "23.23";
                    input.clear();
                    input.sendKeys(testData);
                    input.shouldHave(value("23.23"));
                }
        ));
        tests.add(DynamicTest.dynamicTest(
                "Inputs Проверка удаления /", () -> {
                    String testData = "1/2";
                    input.clear();
                    input.sendKeys(testData);
                    input.shouldHave(value("12"));
                }
        ));
        tests.add(DynamicTest.dynamicTest(
                "Inputs Проверка ввода числа", () -> {
                    String testData = "102";
                    input.clear();
                    input.sendKeys(testData);
                    input.shouldHave(value("102"));
                }
        ));
        tests.add(DynamicTest.dynamicTest(
                "Inputs Проверка сохранения ведущих и замыкающих нулей", () -> {
                    String testData = "001.00000001000";
                    input.clear();
                    input.sendKeys(testData);
                    input.shouldHave(value("001.00000001000"));
                }
        ));
        return tests;
    }


    @ParameterizedTest(name = "Hover на картинке: {0}")
    @ValueSource(ints = {3, 1, 2})
    void verifyHoverTextOnImages(int target) {
        step("Переход на страницу Hovers и проверка url", () -> {
            hoversButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/hovers"));
        });
        step("Проверка текста", () -> {
            SelenideElement img = $x("//div[@class=\"figure\"][" + target + "]");
            img.hover();
            img.shouldHave(text("name: user" + target + "\nView profile"));
            System.out.println("Текст при наведении на изображение " + target + ": " + img.getText());
        });
    }


    @RepeatedTest(value = 5, name = "Notification Message Попытка {currentRepetition}")
    void verifyActionSuccessfulNotification() {
        step("Переход на страницу Notification Message и проверка url", () -> {
            notificationButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/notification_message_rendered"));
        });
        step("Проверка всплывающего уведомления, должно быть Successfull", () -> {
            notificationElement.shouldHave(text("Action successful"));
        });
    }


    @TestFactory
    List<DynamicTest> verifyAddRemoveElementsFunctionality() {
        step("Переход на страницу Add/Remove Elements и проверка url", () -> {
            addRemoveButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/add_remove_elements/"));
        });
        return Arrays.asList(
                        new Object[][]{
                                {2, 1},
                                {5, 2},
                                {1, 3}
                        }
                ).stream()
                .map(data -> DynamicTest.dynamicTest(
                        "Add/Remove Elements Количество созданий " + data[0] + " количество удалений " + data[1],
                        () -> {
                            refresh();
                            int addCount = (int) data[0];
                            int removeCount = (int) data[1];
                            for (int i = 1; i <= addCount; i++) {
                                addElement.click();
                                ElementsCollection buttons = $$x("//div/button[@class=\"added-manually\"]");
                                buttons.should(CollectionCondition.size(i));
                            }
                            for (int i = 0; i < removeCount; i++) {
                                ElementsCollection buttons = $$x("//div/button[@class=\"added-manually\"]");
                                buttons.should(CollectionCondition.size(addCount - i));
                                buttons.get(0).click();
                            }
                        }
                ))
                .collect(Collectors.toList());
    }


    @CsvSource({
            "200, This page returned a 200 status code.",
            "301, This page returned a 301 status code.",
            "404, This page returned a 404 status code.",
            "500, This page returned a 500 status code."
    })
    @ParameterizedTest(name = "При переходе на страницу статус кода {0} текст - {1}")
    void verifyStatusCode(int code, String status) {
        step("Переход на страницу Status Codes и проверка url", () -> {
            statusCodeButton.click();
            webdriver().shouldHave(url("https://the-internet.herokuapp.com/status_codes"));
        });
        step("Проверка текста", () -> {
            $x("//a[@href=\"status_codes/" + code + "\"]").click();
            $x("//div[@id=\"content\"]").should(Condition.text(status));
            System.out.println(status);
        });
    }


    @AfterEach
    void tearDown() {
        getWebDriver().quit();
    }
}
