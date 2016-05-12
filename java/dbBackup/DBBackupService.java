package com.yjt.gcss.timerTask.dbBackup;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;



@Component("dBBackupService")
public class DBBackupService {
	private Logger logger = Logger.getLogger(this.getClass());
	@Value("${dbBackup.ftp.host}")
	private String ftpHost;
	@Value("${dbBackup.ftp.port}")
	private int ftpPort;
	@Value("${dbBackup.ftp.userid}")
	private String ftpUserid;
	@Value("${dbBackup.ftp.password}")
	private String ftpPassword;
	@Value("${dbBackup.ftp.remotePath}")
	private String ftpRemotePath;
	@Value("${gcss.sundayDBBackup.FileName}")
	private String sundayFileName;
	public Message<File> processBackupService(Message<File> message){
		File file=message.getPayload();
		if(file.getName().startsWith(sundayFileName)){
			FTPClient ftpClient = new FTPClient();
			try{
				ftpClient.connect(ftpHost,ftpPort);
				ftpClient.login(ftpUserid,ftpPassword);
				ftpClient.enterLocalPassiveMode();
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				//设置远程目录
				ftpClient.changeWorkingDirectory(ftpRemotePath);
				 
				FTPFile[] fs = ftpClient.listFiles(); 
				if (fs!=null && fs.length>0) {
				    for(int i=0;i<fs.length;i++){
				        if (fs[i].getName().startsWith(sundayFileName)) {
				            ftpClient.deleteFile(fs[i].getName());
				        }
				    }
				}
				logger.info("清除上周日备份文件成功！");
			}catch (Exception e) {
				logger.error("清除上周日备份文件失败!", e);
	        } finally {
	        	 try {
	                 ftpClient.disconnect();
	             } catch (IOException e) {
	                 logger.error("关闭ftp客户端失败", e);
	             }
	       }
		}
		logger.info("备份文件传输至远程备份机!");
		return message;
	}

}
