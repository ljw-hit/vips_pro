/*
 * Tomas Popela, 2012
 * VIPS - Visual Internet Page Segmentation
 * Module - Vips.java
 */

package org.fit.vips;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.text.html.CSS;

import org.fit.cssbox.css.CSSNorm;
import org.fit.cssbox.css.DOMAnalyzer;
import org.fit.cssbox.css.NormalOutput;
import org.fit.cssbox.demo.ImageRenderer;
import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DefaultDOMSource;
import org.fit.cssbox.io.DefaultDocumentSource;
import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.layout.BrowserCanvas;
import org.fit.cssbox.layout.BrowserConfig;
import org.fit.cssbox.layout.Viewport;
import org.w3c.dom.Document;

/**
 * Vision-based Page Segmentation algorithm
 * @author Tomas Popela
 *
 */
public class Vips {
	private URL _url = null;
	private DOMAnalyzer _domAnalyzer = null;
	private BrowserCanvas _browserCanvas = null;
	private Viewport _viewport = null;

	private boolean _graphicsOutput = false;
	private boolean _outputToFolder = false;
	private boolean _outputEscaping = true;
	private int _pDoC = 11;
	private String _filename = "";
	private	int sizeTresholdWidth = 350;
	private	int sizeTresholdHeight = 400;

	private PrintStream originalOut = null;
	long startTime = 0;
	long endTime = 0;

	/**
	 * Default constructor
	 */
	public Vips()
	{
	}

	/**
	 * Enables or disables graphics output of VIPS algorithm.
	 * @param enable True for enable, otherwise false.
	 */
	public void enableGraphicsOutput(boolean enable)
	{
		_graphicsOutput = enable;
	}

	/**
	 * Enables or disables creation of new directory for every algorithm run.
	 * @param enable True for enable, otherwise false.
	 */
	public void enableOutputToFolder(boolean enable)
	{
		_outputToFolder = enable;
	}

	/**
	 * Enables or disables output XML character escaping.
	 * @param enable True for enable, otherwise false.
	 */
	public void enableOutputEscaping(boolean enable)
	{
		_outputEscaping = enable;
	}

	/**
	 * Sets permitted degree of coherence (pDoC) value.
	 * @param value pDoC value.
	 */
	public void setPredefinedDoC(int value)
	{
		if (value <= 0 || value > 11)
		{
			System.err.println("pDoC value must be between 1 and 11! Not " + value + "!");
			return;
		}
		else
		{
			_pDoC = value;
		}
	}

	/**
	 * Sets web page's URL
	 * @param url Url
	 * @throws MalformedURLException
	 */
	public void setUrl(String url)
	{
		try
		{
			if (url.startsWith("http://") || url.startsWith("https://"))
				_url = new URL(url);
			else
				_url = new URL("http://" + url);
		}
		catch (Exception e)
		{
			System.err.println("Invalid address: " + url);
		}
	}

	/**
	 * Parses a builds DOM tree from page source.
	 * @param urlStream Input stream with page source.
	 */
	private void getDomTree(URL urlStream)
	{
		DocumentSource docSource = null;
		try
		{
			docSource = new DefaultDocumentSource(urlStream);
			System.out.print(docSource.toString());
			DOMSource parser = new DefaultDOMSource(docSource);

			Document domTree = parser.parse();
			System.out.print(domTree.toString());
			_domAnalyzer = new DOMAnalyzer(domTree, _url);
			_domAnalyzer.attributesToStyles();
			_domAnalyzer.addStyleSheet(null, CSSNorm.stdStyleSheet(), DOMAnalyzer.Origin.AGENT);
			_domAnalyzer.addStyleSheet(null, CSSNorm.userStyleSheet(), DOMAnalyzer.Origin.AGENT);
			_domAnalyzer.addStyleSheet(null,CSSNorm.formsStyleSheet(),DOMAnalyzer.Origin.AGENT);

			_domAnalyzer.getStyleSheets();
		}
		catch (Exception e)
		{
			System.err.print(e.getMessage());
		}
	}

	/**
	 * Gets page's viewport
	 */
	private void getViewport() {
		Dimension dim = new java.awt.Dimension(1200, 1200);
		_browserCanvas = new BrowserCanvas(_domAnalyzer.getRoot(),
				_domAnalyzer, _url);
		System.out.println(System.getProperty("user.dir"));
//        File file = new File(System.getProperty("user.dir")+"/out.html");
//        try {
//			PrintWriter printWriter = new PrintWriter(file);
//			NormalOutput normalOutput = new NormalOutput(_domAnalyzer.getRoot());
//			normalOutput.dumpTo(printWriter);
//		}catch (Exception e){
//              e.printStackTrace();
//		}

		_browserCanvas.getConfig().setDefaultFont(Font.SERIF,"宋体");
		_browserCanvas.getConfig().setDefaultFont(Font.SANS_SERIF, "Arial");
		_browserCanvas.getConfig().setDefaultFont(Font.MONOSPACED, "Courier New");
		_browserCanvas.getConfig().setImageLoadTimeout(2000);
		_browserCanvas.getConfig().setClipViewport(true);
		//_browserCanvas.getConfig().setReplaceImagesWithAlt(true);


		_browserCanvas.createLayout(dim);

		_viewport = _browserCanvas.getViewport();

	}

	/**
	 * Exports rendered page to image.
	 */
	private void exportPageToImage()
	{
		try
		{
			String filename = System.getProperty("user.dir") + "/pageCompele.png";
			String filename2 = System.getProperty("user.dir") + "/page.png";
			//System.out.println("start...");
            String url = _url.toString();
            ArrayList<String> urlList = new ArrayList<>();
            urlList.add(url);
            Spider spider = new Spider(urlList);
            spider.urlToScreenShot(filename);


			BufferedImage page = _browserCanvas.getImage();
			ImageIO.write(page,"png",new File(filename2));
			//System.out.println("OK");

		} catch (Exception e)
		{
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Generates folder filename
	 * @return Folder filename
	 */
	private String generateFolderName()
	{
		String outputFolder = "";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		outputFolder += "result"+"\\";
		outputFolder += sdf.format(cal.getTime());
		outputFolder += "_";
		outputFolder += VipsTester.logo+"_"+VipsTester.type;
		return outputFolder;
	}

	/**
	 * Performs page segmentation.
	 */
	private void performSegmentation()
	{

		startTime = System.nanoTime();
		int numberOfIterations = 10;
		int pageWidth = _viewport.getWidth();
		int pageHeight = _viewport.getHeight();

		//if (_graphicsOutput)
		exportPageToImage();

		VipsSeparatorGraphicsDetector detector;
		VipsParser vipsParser = new VipsParser(_viewport);
		VisualStructureConstructor constructor = new VisualStructureConstructor(_pDoC);
		constructor.setGraphicsOutput(_graphicsOutput);
		endTime = System.nanoTime();
		long diff = endTime-startTime;
		System.out.println("预处理" + diff + " ns; " +
				(diff / 1000000.0) + " ms; " +
				(diff / 1000000000.0) + " sec");
		for (int iterationNumber = 1; iterationNumber < numberOfIterations+1; iterationNumber++)
		{
			startTime = System.nanoTime();
			detector = new VipsSeparatorGraphicsDetector(pageWidth, pageHeight);

			//visual blocks detection
			vipsParser.setSizeTresholdHeight(sizeTresholdHeight);
			vipsParser.setSizeTresholdWidth(sizeTresholdWidth);

			vipsParser.parse();

			VipsBlock vipsBlocks = vipsParser.getVipsBlocks();
			endTime = System.nanoTime();
			diff = endTime-startTime;
			System.out.println("视觉块提取时间" + diff + " ns; " +
					(diff / 1000000.0) + " ms; " +
					(diff / 1000000000.0) + " sec");

			startTime = System.nanoTime();

			if (iterationNumber == 1)
			{
				if (_graphicsOutput)
				{
					// in first round we'll export global separators
					detector.setVipsBlock(vipsBlocks);
					detector.fillPool();
					detector.saveToImage("blocks" + iterationNumber);
					detector.setCleanUpSeparators(0);
					detector.detectHorizontalSeparators();
					detector.detectVerticalSeparators();
					//detector.exportHorizontalSeparatorsToImage();
					//detector.exportVerticalSeparatorsToImage();
					detector.exportAllToImage();
				}

				// visual structure construction
				constructor.setVipsBlocks(vipsBlocks);
				constructor.setPageSize(pageWidth, pageHeight);
			}
			else
			{
				vipsBlocks = vipsParser.getVipsBlocks();
				constructor.updateVipsBlocks(vipsBlocks);

				if (_graphicsOutput)
				{
					detector.setVisualBlocks(constructor.getVisualBlocks());
					detector.fillPool();
					detector.saveToImage("blocks" + iterationNumber);
				}
			}
			endTime = System.nanoTime();
			diff = endTime-startTime;
			System.out.println("分割符探测" + diff + " ns; " +
					(diff / 1000000.0) + " ms; " +
					(diff / 1000000000.0) + " sec");
			startTime = System.nanoTime();
			// visual structure construction
			constructor.constructVisualStructure();
			endTime = System.nanoTime();
			diff = endTime-startTime;
			System.out.println("视觉块重构" + diff + " ns; " +
					(diff / 1000000.0) + " ms; " +
					(diff / 1000000000.0) + " sec");
			// prepare tresholds for next iteration
			if (iterationNumber <= 5 )
			{
				sizeTresholdHeight -= 50;
				sizeTresholdWidth -= 50;

			}
			if (iterationNumber == 6)
			{
				sizeTresholdHeight = 100;
				sizeTresholdWidth = 100;
			}
			if (iterationNumber == 7)
			{
				sizeTresholdHeight = 80;
				sizeTresholdWidth = 80;
			}
			if (iterationNumber == 8)
			{
				sizeTresholdHeight = 40;
				sizeTresholdWidth = 10;
			}
			if (iterationNumber == 9)
			{
				sizeTresholdHeight = 1;
				sizeTresholdWidth = 1;
			}

		startTime = System.nanoTime();
		}
				//constructor.normalizeSeparatorsSoftMax();
		constructor.normalizeSeparatorsMinMax();

		VisualStructure.checkLeaf(constructor.getVisualStructure());
		VipsOutput vipsOutput = new VipsOutput(_pDoC);

		vipsOutput.setEscapeOutput(_outputEscaping);
		vipsOutput.setOutputFileName(_filename);
		vipsOutput.VisualBlocksToImage(constructor.getVisualStructure());
		if(_graphicsOutput) {
			vipsOutput.saveToImage("VIPS");
		}
		vipsOutput.writeXML(constructor.getVisualStructure(), _viewport);

		endTime = System.nanoTime();

		diff = endTime - startTime;

		System.out.println("视觉特征输出模块" + diff + " ns; " +
				(diff / 1000000.0) + " ms; " +
				(diff / 1000000000.0) + " sec");
	}


	/**
	 * Starts segmentation on given address
	 * @param url
	 */
	public void startSegmentation(String url)
	{
		setUrl(url);

		startSegmentation();
	}

	/**
	 * Restores stdout
	 */
	private void restoreOut()
	{
		if (originalOut != null)
		{
			System.setOut(originalOut);
		}
	}

	/**
	 * Redirects stdout to nowhere
	 */
	private void redirectOut()
	{
		originalOut = System.out;
		System.setOut(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException
			{

			}
		}));
	}

	/**
	 * Starts visual segmentation of page
	 * @throws Exception
	 */
	public void startSegmentation()
	{
		try
		{
			_url.openConnection();

			redirectOut();

			getDomTree(_url);
			startTime = System.nanoTime();
			getViewport();
			restoreOut();

			String outputFolder = "";
			String oldWorkingDirectory = "";
			String newWorkingDirectory = "";

			if (_outputToFolder)
			{
				outputFolder = generateFolderName();
				System.out.println(outputFolder);
//				if(!new File("result").mkdir()){
//					System.err.println("folder result error");
//				}
				File result = new File("result");
				if(!result.exists()){
					result.mkdir();
				}

				if (!new File(outputFolder).mkdir() || !new File(outputFolder+"/subPage").mkdir())
				{
					System.err.println("Something goes wrong during directory creation!");
				}
				else
				{
					oldWorkingDirectory = System.getProperty("user.dir");
					newWorkingDirectory += oldWorkingDirectory + "/" + outputFolder + "/";
					System.setProperty("user.dir", newWorkingDirectory);
				}
			}

			performSegmentation();

			if (_outputToFolder)
				System.setProperty("user.dir", oldWorkingDirectory);
		}
		catch (Exception e)
		{
			System.err.println("Something's wrong!");
			e.printStackTrace();
		}
	}

	public void setOutputFileName(String filename)
	{
		if (!filename.equals(""))
		{
			_filename = filename;
		}
		else
		{
			System.out.println("Invalid filename!");
		}
	}
}
