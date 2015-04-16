package com.yjt.gcss.test;
import java.io.BufferedReader;
 import java.io.BufferedWriter;     
 import java.io.File;     
 import java.io.FileFilter;     
 import java.io.FileInputStream;     
 import java.io.FileOutputStream;     
 import java.io.IOException;     
 import java.io.InputStream;     
 import java.io.InputStreamReader;     
 import java.io.OutputStream;     
 import java.io.OutputStreamWriter;     
 import java.io.Reader;     
 import java.io.UnsupportedEncodingException;     
 import java.io.Writer;     
 
 public class EncodeConverter { 
     // ԭ�ļ�Ŀ¼����Ҫ�����������޸�     
     private static String srcDir = "D:\\EncodingConveter\\src";
     // ת����Ĵ��Ŀ¼����Ҫ�����������޸�      
     private static String desDir = "D:\\EncodingConveter\\dest";
     // Դ�ļ�����     
     private static String srcEncode = "GBK";
     // ����ļ�����     
     private static String desEncode = "UTF-8";
            
     // ������ļ�����,������    
     private static FileFilter filter = new FileFilter() {     
         public boolean accept(File pathname) {     
        	 return true;     
             /*// ֻ����Ŀ¼ ���� .java�ļ�     
             if (pathname.isDirectory()     
                     || (pathname.isFile() && pathname.getName().endsWith(     
                             ".java")))     
             else    
                 return false;     */
         }     
     };     
            
     /**   
      * @param file   
      */    
     public static void readDir(File file)     
     {     
 //      �Թ�������Ϊ���� 
         File[] files = file.listFiles(filter);    
         for (File subFile : files) {     
             // ����Ŀ��Ŀ¼     
             if (subFile.isDirectory()) {     
                 File file3 = new File(desDir + subFile.getAbsolutePath().substring(srcDir.length()));     
                 if (!file3.exists()) {     
                     file3.mkdir();     
                 }     
                 file3 = null;     
                 readDir(subFile);     
             } else {     
                 System.err.println("һԴ�ļ���\t"+subFile.getAbsolutePath() + "\nĿ���ļ���\t" + (desDir + subFile.getAbsolutePath().substring(srcDir.length())));     
                 System.err.println("-----------------------------------------------------------------");     
                 try {     
                     convert(subFile.getAbsolutePath(), desDir + subFile.getAbsolutePath().substring(srcDir.length()), srcEncode, desEncode);     
                 } catch (UnsupportedEncodingException e) {     
                     e.printStackTrace();     
                 } catch (IOException e) {     
                     e.printStackTrace();     
                 }     
             }     
         }     
     }     
            
     /**   
      *    
      * @param infile    Դ�ļ�·��   
      * @param outfile   ����ļ�·��   
      * @param from  Դ�ļ�����   
      * @param to    Ŀ���ļ�����   
      * @throws IOException   
      * @throws UnsupportedEncodingException   
      */    
     public static void convert(String infile, String outfile, String from,     
             String to) throws IOException, UnsupportedEncodingException {     
         // set up byte streams     
         InputStream in;     
         if (infile != null)     
             in = new FileInputStream(infile);     
         else    
             in = System.in;     
         OutputStream out;     
         if (outfile != null)     
             out = new FileOutputStream(outfile);     
         else    
             out = System.out;     
       
         // Use default encoding if no encoding is specified.     
         if (from == null)     
             from = System.getProperty("file.encoding");     
         if (to == null)     
             to = System.getProperty("file.encoding");     
       
         // Set up character stream     
         Reader r = new BufferedReader(new InputStreamReader(in, from));     
         Writer w = new BufferedWriter(new OutputStreamWriter(out, to));     
       
         // Copy characters from input to output. The InputStreamReader     
         // converts from the input encoding to Unicode,, and the     
         // OutputStreamWriter     
         // converts from Unicode to the output encoding. Characters that cannot     
         // be     
         // represented in the output encoding are output as '?'     
         char[] buffer = new char[4096];     
         int len;     
         while ((len = r.read(buffer)) != -1)     
             w.write(buffer, 0, len);     
         r.close();     
         w.flush();     
         w.close();     
     }     
            
       
     public static void main(String[] args) {     
         // ����Ŀ���ļ���     
         File desFile = new File(desDir);     
         if (!desFile.exists()) {     
             desFile.mkdir();     
         }     
         desFile = null;     
       
         File srcFile = new File(srcDir);     
         // ��ȡĿ¼ ѭ��ת���ļ�     
         readDir(srcFile);     
         srcFile = null;     
         System.out.println("���");
     }     
 }