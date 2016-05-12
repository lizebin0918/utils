package com.yjt.gcss.timerTask;

import java.io.File;
/**
 * 备份文件发送gateway
 * @author caib 2015-04-21
 *
 */
public interface DBBackupGateway {
	
	public void submitBackupFile(File file);
}
