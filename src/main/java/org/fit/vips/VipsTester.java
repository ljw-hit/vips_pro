/*
 * Tomas Popela, 2012
 * VIPS - Visual Internet Page Segmentation
 * Module - VipsTester.java
 */

package org.fit.vips;

import java.io.*;
import java.util.ArrayList;

/**
 * VIPS API example application.
 * @author Tomas Popela
 *
 */
public class VipsTester {

	static public String logo = "";
	static public String type = "";
	/**
	 * Main function
	 * @param args Internet address of web page.
	 */
	public static void main(String args[])
	{
		// we've just one argument - web address of page
		if (args.length != 1)
		{
			System.err.println("We've just only one argument - web address of page!");
			System.exit(0);
		}

		String url = args[0];
		//String filename = args[0];
		try
		{
			Vips vips = new Vips();
			// disable graphics output
			vips.enableGraphicsOutput(false);
			// disable output to separate folder (no necessary, it's default value is false)
			vips.enableOutputToFolder(true);
			// set permitted degree of coherence
			vips.setPredefinedDoC(8);
			// start segmentation on page
//			File file = new File(filename);
//			InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
//			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//			String lineTxt;
//			while ((lineTxt = bufferedReader.readLine()) != null){
//				String[] parts = lineTxt.split(" ");
//				logo = parts[0];
//				type = parts[1];
//				vips.startSegmentation(parts[2]);
//			}
			vips.startSegmentation(url);
		} catch (Exception e)

		{
			e.printStackTrace();
		}
	}
}