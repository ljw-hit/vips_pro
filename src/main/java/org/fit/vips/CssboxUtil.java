package org.fit.vips;

import org.fit.cssbox.demo.ImageRenderer;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CssboxUtil {

    public static final int WIDTH = 1200;
    public static final int HEIGHT = 1200;

    /**
     * 图片生成到指定路径
     * 支持 file ftp http https
     *
     * @param url  html地址
     * @param path 生成文件地址
     * @throws IOException
     * @throws SAXException
     */
    public static void toPath(String url, String path) throws IOException, SAXException, InterruptedException {
        toPath(url, path, WIDTH, HEIGHT);
    }

    /**
     * 图片生成到指定路径
     * 支持 file ftp http https
     *
     * @param url  html地址
     * @param path 生成文件地址
     * @throws IOException
     * @throws SAXException
     */
    public static void toPath(String url, String path, int width, int height) throws IOException, SAXException, InterruptedException {
        ImageRenderer render = new ImageRenderer();
        FileOutputStream out = new FileOutputStream(new File(path));
        render.setWindowSize(new Dimension(width, height), false);
        //Thread.sleep(10000);
        render.renderURL(url, out, ImageRenderer.Type.PNG);
        out.close();
    }

    /**
     * 图片生成到byte数组
     * 支持 file ftp http https
     *
     * @param url html地址
     * @throws IOException
     * @throws SAXException
     */
    public static byte[] toByte(String url) throws IOException, SAXException {
        return toByte(url, WIDTH, HEIGHT);
    }

    /**
     * 图片生成到byte数组
     * 支持 file ftp http https
     *
     * @param url html地址
     * @throws IOException
     * @throws SAXException
     */
    public static byte[] toByte(String url, int width, int height) throws IOException, SAXException {
        ImageRenderer render = new ImageRenderer();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        render.setWindowSize(new Dimension(2000, 2000), true);
        render.renderURL(url, stream, ImageRenderer.Type.PNG);
        byte[] bytes = stream.toByteArray();
        stream.close();
        return bytes;
    }

    public static void main(String[] args) {
        try {
            toPath("http://jwc.hit.edu.cn/","test\\page.png");
        }
        catch (Exception e){

        }
    }
}