package com.ro.integration.aloha;

import com.opencsv.CSVWriter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import io.github.bonigarcia.wdm.WebDriverManager;


public class App9700Runner {
    private static final String PROPERTIES_FILE = "./settings.properties";
    private static final String KEY_BASE_URL = "base.url";
    private static final String KEY_USERNAME = "user.name";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_COMPANY = "company";
    private static final String KEY_DOWNLOAD_PATH = "download.path";

    private static final String BASE_URL;
    private static final String LOGIN_URL;
    private static final String USER_NAME;
    private static final String PASSWORD;
    private static final String COMPANY;

    private static final String DOWNLOAD_PATH;
    private static File DOWNLOAD_DIR;
    private static String yesterdayStringPath;

    static {
        BASE_URL = PropertyReader.getPropertiesFromFile(PROPERTIES_FILE).getProperty(KEY_BASE_URL);
        LOGIN_URL = BASE_URL + "login.do";
        USER_NAME = PropertyReader.getPropertiesFromFile(PROPERTIES_FILE).getProperty(KEY_USERNAME);
        PASSWORD = PropertyReader.getPropertiesFromFile(PROPERTIES_FILE).getProperty(KEY_PASSWORD);
        COMPANY = PropertyReader.getPropertiesFromFile(PROPERTIES_FILE).getProperty(KEY_COMPANY);
        DOWNLOAD_PATH = PropertyReader.getPropertiesFromFile(PROPERTIES_FILE).getProperty(KEY_DOWNLOAD_PATH);

        // make download dir with date based naming
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd");
        String timezone = System.getProperty("timezone");
        if (timezone == null || timezone.trim().equals("")) {
            timezone = "Asia/Kolkata";
        }
        Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone(timezone));
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        yesterdayStringPath=format.format(yesterday.getTime());
        DOWNLOAD_DIR = new File(DOWNLOAD_PATH);
        if (!DOWNLOAD_DIR.exists()) {
            DOWNLOAD_DIR.mkdirs();
        }

        System.out.println("All files will be written to: " + DOWNLOAD_DIR.getAbsolutePath());
    }

    public static void main(String[] args) throws Exception {
//        FirefoxProfile profile = new FirefoxProfile();
//        profile.setPreference("browser.download.folderList", 2);
//        profile.setPreference("browser.download.dir", DOWNLOAD_DIR.getAbsolutePath());
//        profile.setPreference("browser.download.alertOnEXEOpen", false);
//        profile.setPreference("browser.helperApps.neverAsksaveToDisk", "application/csv,text/csv");
//        profile.setPreference("browser.download.manager.showWhenStarting", false);
//        profile.setPreference("browser.download.manager.focusWhenStarting", false);
//        profile.setPreference("browser.helperApps.alwaysAsk.force", false);
//        profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
//        profile.setPreference("browser.download.manager.closeWhenDone", false);
//        profile.setPreference("browser.download.manager.showAlertOnComplete", false);
//        profile.setPreference("browser.download.manager.useWindow", false);
//        profile.setPreference("browser.download.manager.showWhenStarting", false);
//        profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
//        profile.setPreference("pdfjs.disabled", true);
//        profile.setAcceptUntrustedCertificates(true);
//        profile.setPreference("security.enable_java", true);
//        profile.setPreference("plugin.state.java", 2);
//        FirefoxDriver driver = new FirefoxDriver(profile);
//        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");





        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(5,TimeUnit.SECONDS);

        login(driver);
        System.out.println("Waiting ...");
        Thread.sleep(10000L);
        yesterdayChecksReport(driver);
        driver.quit();
    }

    private static void login(WebDriver driver) {
        driver.get(LOGIN_URL);

        driver.findElement(By.id("login-username")).clear();
        driver.findElement(By.id("login-username")).sendKeys(USER_NAME);

        driver.findElement(By.id("login-password")).clear();
        driver.findElement(By.id("login-password")).sendKeys(PASSWORD);
        driver.manage().timeouts().implicitlyWait(60L, TimeUnit.SECONDS);
        driver.findElement(By.name("b_LogIn")).click();
    }

    private static void yesterdayChecksReport(WebDriver driver) throws InterruptedException {


        File file = new File(DOWNLOAD_DIR+"\\"+yesterdayStringPath+".csv");

        try {
                // create FileWriter object with file as parameter
                FileWriter outputfile = new FileWriter(file);

                // create CSVWriter object filewriter object as parameter
                CSVWriter writer = new CSVWriter(outputfile);

                // create a List which contains String array
                List<String[]> data = new ArrayList<String[]>();




            driver.findElement(By.id("ext-gen38")).click();
            Thread.sleep(1000L);
            WebElement guestDetailsTab= driver.findElement(By.id("ext-gen43")).findElements(By.className("x-tree-node")).get(2).findElements(By.tagName("div")).get(0);
            guestDetailsTab.click();
            Thread.sleep(7000L);

            driver.switchTo().frame(driver.findElement(By.id("if-msh-id-600")));


            WebElement e1= driver.findElement(By.id("checkDetailView"));
            e1.findElements(By.tagName("span")).get(0).click();
            Thread.sleep(200L);
          //  WebElement element= driver.findElement(By.cssSelector(".current-date"));
    //        driver.findElement(By.xpath("//div[@class='day-box current-date current-month']/parent::td/parent::tr/preceding-sibling::tr/descendant::td")).click();
    //        Thread.sleep(2000L);
    //        System.out.println("testtt");
    //        Thread.sleep(2000L);

            try {
                driver.findElement(By.xpath("//div[@class='day-box current-date current-month']/parent::td/preceding-sibling::td[1]")).click();
            }catch (Exception ex){
                // if the day is the first of the month we have to click the left arrow to switch to the previous month from the calendar
                driver.findElement(By.xpath("//ul[@class='dropdown-menu ncr-datepicker ng-pristine ng-untouched ng-valid ng-valid-date-disabled']/descendant::button[@class='btn btn-default btn-sm pull-left']")).click();
                Thread.sleep(200L);
                driver.findElement(By.xpath("//ul[@class='dropdown-menu ncr-datepicker ng-pristine ng-untouched ng-valid ng-valid-date-disabled']/descendant::div[contains(@class,'current-month')][last()]")).click();
            }

            List<WebElement> rows= driver.findElements(By.xpath("//div[@class='cdv-checklist']/div/div"));

            for(WebElement e : rows){
                e.click();

                String dateTime="";
                try{
                    String date =driver.findElement(By.xpath("//div[@class='cdv-check-content']//table[2]/tbody/tr[1]/td[3]")).getText();
                    String time=driver.findElement(By.xpath("//div[@class='cdv-check-content']//table[2]/tbody/tr[2]/td[2]")).getText();
                    dateTime=date+" "+time;
                }catch (Exception exception){

                }
                String referenceNumber="";
                try {
                    referenceNumber=driver.findElement(By.xpath("//div[@class='cdv-check-content']//table[2]/tbody/tr[2]/td[3]")).getText();

                }catch (Exception exception){

                }

                String tableName="";
                try {
                    tableName=driver.findElement(By.xpath("//div[@class='cdv-check-content']//table[2]/tbody/tr[4]/td[2]")).getText().replaceAll("Table ","TAB#");
                }catch (Exception exception){

                }

                String empoyeeName="";

                try {
                    empoyeeName=driver.findElement(By.xpath("//div[@class='cdv-check-content']//table[2]/tbody/tr[1]/td[2]")).getText();
                }catch (Exception exception){

                }
                String totalPrice="0";
                try {
                    totalPrice=driver.findElement(By.xpath("//td[contains(text(), 'Total')]/following-sibling::td")).getText();
                }catch (Exception exception){

                }
                String cover=driver.findElement(By.xpath("//div[@class='cdv-check-content']//table[2]/tbody/tr[5]/td[2]")).getText();
                String currency="AED";
                String quantity="1";

                String paymentMethods="";
                try {

                    List<WebElement> paymentMethodElements=driver.findElements(By.xpath("//td[contains(text(), 'Total')]/parent::tr/following::tr/td[not(contains(@colspan, '2')) and not(contains(@align, 'right'))]"));

                    for (int i=0;i<paymentMethodElements.size();i++){



                        WebElement paymentMethodElement  = paymentMethodElements.get(i);

                        if(paymentMethods.contains(paymentMethodElement.getText()))
                            continue;
                        if(paymentMethodElement.getText().contains("Tip"))
                            continue;
                        paymentMethods+=paymentMethodElement.getText();

                        if(i<paymentMethodElements.size()-1)
                            paymentMethods+=" /";


                    }
                }catch (Exception exception){

                }

                List<WebElement> checkItemsElements=driver.findElements(By.xpath("//td[contains(text(), 'Subtotal')]/parent::tr/preceding-sibling::tr/td[not(contains(@colspan, '2'))]"));

                for (int i=0;i<checkItemsElements.size();i+=2) {

                    try {
                        String itemName= checkItemsElements.get(i).getText();
                        String price= checkItemsElements.get(i+1).getText();

                        data.add(new String[]{dateTime,itemName,quantity,price,price,tableName,paymentMethods,referenceNumber,currency,"",totalPrice,cover});
                    }catch (Exception exception){

                    }


                }

                Thread.sleep(1000L);


            }
            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");

//        JavascriptExecutor js= (JavascriptExecutor) driver;
//        js.executeScript("document.getElementById('checkDetailView').getElementByClass('ng-pristine').click();");

        //        driver.get(BASE_URL + "ta/taConfigureAction.do?method=runMainPageTA&taName=CheckSummary&portletId=2005");
//        Thread.sleep(10000L);
//        driver.switchTo().parentFrame();
//        System.out.println("click run");
//        Thread.sleep(5000L);
//        driver.switchTo().parentFrame();
//        driver.findElement(By.id("Run")).click();
//        System.out.println("get master report");
//        driver.switchTo().parentFrame();
//        driver.switchTo().frame("resultFrame");
//        System.out.println(driver.getPageSource());
//        Thread.sleep(5000L);
//        System.out.println("get all divs");
//        List<WebElement> divsUnderTDs = driver.findElements(By.xpath("//div[@style='text-decoration:underline;cursor:pointer;']"));
//        System.out.println("we found " + divsUnderTDs.size() + " divs");
//
//        List<String> guestChecksIds = new ArrayList<>();
//
//        for (WebElement div : divsUnderTDs) {
//            String data = div.getText();
//            String guestCheck = div.getAttribute("onclick");
//            if (guestCheck != null) {
//                int check = guestCheck.indexOf("drilldownAction");
//                if (check != -1) {
//                    check = guestCheck.indexOf("reportID=TACheckDetailHist");
//                    if (check != -1) {
//                        check = guestCheck.indexOf("guestcheckid=", check);
//                        String guestCheckId = guestCheck.substring(check + 13, guestCheck.indexOf("'", check));
//                        System.out.println(data + "_" + guestCheckId);
//                        guestChecksIds.add(data + "_" + guestCheckId);
//                    }
//                }
//            }
//        }
//
//        for (String guestChecksId : guestChecksIds) {
//            String[] data1 = guestChecksId.split("_");
//            String guestCheck = data1[1];
//            String check1 = data1[0];
//            driver.get(BASE_URL + "ta/taReportRunAction.do?method=run&reportID=TACheckDetailHist&guestcheckid=" + guestCheck + "&guestcheckposref=" + check1);
//            driver.manage().timeouts().implicitlyWait(60L, TimeUnit.SECONDS);
//            writeFile(driver.getPageSource(), new File(DOWNLOAD_DIR + "/gc_" + check1 + "_" + guestCheck + ".html"));
//            System.out.println("gc_" + check1 + "_" + guestCheck + ".html  File done !");
//        }
    }

    private static void writeFile(String content, File downloadDest) {
        try {
            FileWriter e = new FileWriter(downloadDest);
            BufferedWriter writer = new BufferedWriter(e);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
