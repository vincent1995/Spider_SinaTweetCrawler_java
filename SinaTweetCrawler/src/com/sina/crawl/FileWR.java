package com.sina.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

/**
 * * * * * * * * * * * * * * * * * * * * * *
 *
 * @author DianaCody
 * @version 1.0
 * @filename FileWR.java
 * @note Write and Read from files
 * @since 2014-09-27 15:23:28
 * * * * * * * * * * * * * * * * * * * * * *
 */

public class FileWR {
    /**
     * 读取文本，每行存储在一个String中
     *
     * @param path
     * @return
     * @throws IOException
     */
    public static Vector<String> getLines(String path) throws IOException {
        Vector<String> lines = new Vector<String>();
        File f = new File(path); //"e:/tweet/validIPs.txt"
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        String s;
        while ((s = br.readLine()) != null) {
            lines.add(s);
        }
        br.close();
        return lines;
    }

    /**
     * 将多个字符串写入到一个文件
     *
     * @param vector
     * @param savePath
     * @throws IOException
     */
    public static void write(Vector<String> vector, String savePath) throws IOException {
        File f = new File(savePath);
        if (!f.exists()) {// 如果文件不存在，则创建该文件
            f.createNewFile();
        }
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < vector.size(); i++) {
            bw.write(vector.get(i) + "\r\n");
        }
        bw.close();
    }

    /**
     * 将单个字符串写入到文件
     *
     * @param s
     * @param savePath
     * @throws IOException
     */
    public static void write(String s, String savePath) throws IOException {
        File f = new File(savePath);
        if (!f.exists())
            f.createNewFile();
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(s);
        bw.close();
    }

//	/** 由html文件得到微博 */
//	public static String html2String(String htmlPath) throws IOException {
//		String html = "";
//		File f = new File(htmlPath);
//		FileReader fr = new FileReader(f);
//		BufferedReader br = new BufferedReader(fr);
//		String s;
//		while((s=br.readLine()) != null) {
//			html += s;
//		}
//		br.close();
//		return html;
//	}
//
//	/** 把某关键字搜索到的微博写到txt文件里去 */
//	public static void writeVector(Vector<String> vector, String savePath) throws IOException {
//		File f = new File(savePath);
//		FileWriter fw = new FileWriter(f);
//		BufferedWriter bw = new BufferedWriter(fw);
//		for(int i=0; i<vector.size(); i++) {
//			bw.write(vector.get(i) + "\r\n");
//		}
//		bw.close();
//	}
}
