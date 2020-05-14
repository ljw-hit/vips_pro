package org.fit.vips;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Spider {

    private ArrayList<String> urlList;

    public Spider(){
        urlList = new ArrayList<String>();
    }

    public Spider(ArrayList<String> urlList){
        this.urlList = urlList;
    }


    public  static  byte[] takeScreenshot(WebDriver webDriver) throws IOException{

        TakesScreenshot takesScreenshot = (TakesScreenshot) webDriver;
        return takesScreenshot.getScreenshotAs(OutputType.BYTES);
    }

    public  static BufferedImage createElementImage(WebDriver driver) throws IOException {
       BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(takeScreenshot(driver)));
        return bufferedImage;
    }


    public void urlToScreenShot(String fileName) throws IOException {
        //选用谷歌浏览器驱动
        System.setProperty("webdriver.chrome.driver", "F:\\vips_java-master\\src\\main\\resources\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless");
        options.addArguments("--hide-scrollbars");
        options.addArguments("--window-size=1200,1200");
        ChromeDriver chromeDriver = new ChromeDriver(options);

        for(String url:urlList) {
            chromeDriver.get(url);

            BufferedImage bufferedImage = createElementImage(chromeDriver);
            File file = new File(fileName);
            ImageIO.write(bufferedImage,"png",file);
        }

        chromeDriver.close();
    }
    public static void main(String[] args) throws IOException {
        ArrayList<String> urlList = new ArrayList<String>();
        //urlList.add("http://www.baidu.com");
        urlList.add("https://www.tsinghua.edu.cn/");
        //urlList.add("https://www.51test.net/kaoyan/beijing/chengjichaxun/");
        Spider spider = new Spider(urlList);
        //spider.urlToScreenShot();
    }
}
