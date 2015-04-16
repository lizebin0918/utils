BEGIN
START TRANSACTION;
	/*获取成功笔数*/
	SELECT @suc_cnt:= COUNT(*),@suc_amt:= IFNULL(SUM(CAST(YKJE as UNSIGNED)),0) from UI_PLDS where FILE_ID = INPUT_FILE_ID and `STATUS` = 'S';
	/*获取失败笔数*/
	/*
	SQL语句有误，如果“应扣金额”里面包含字母，交易的状态也是“E”，这时程序会报错。
	modified by lizb(2014-11-04)
	SELECT @fail_cnt:= COUNT(*),@fail_amt:= IFNULL(SUM(YKJE),0) from UI_PLDS where FILE_ID = INPUT_FILE_ID and `STATUS` in ('F','E');
	*/
	SELECT @zbs:=FILE_HEAD_ZBS, @zje:=FILE_HEAD_ZJE from UI_FILE_LOG as uiFileLog where FILE_ID = INPUT_FILE_ID;
	/*失败笔数 = 总笔数 - 成功笔数*/
	SELECT @fail_cnt:= COUNT(*) from UI_PLDS where FILE_ID = INPUT_FILE_ID and `STATUS` in ('F','E');
	/*失败金额 = 总金额 - 成功金额*/
	SELECT @fail_amt:= @zje - @suc_amt;
	update `UI_FILE_LOG` as uiFileLog 
	SET uiFileLog.FAIL_AMT = @fail_amt,uiFileLog.SUC_AMT = @suc_amt,
	uiFileLog.FAIL_CNT = @fail_cnt,uiFileLog.SUC_CNT = @suc_cnt where uiFileLog.FILE_ID = INPUT_FILE_ID;
	
	IF(@zbs = (@suc_cnt+@fail_cnt)) THEN UPDATE UI_FILE_LOG as uiFileLog set uiFileLog.status = 'S' WHERE uiFileLog.status = 'A' 
	AND uiFileLog.FILE_STATUS = 'S' 
	AND uiFileLog.FILE_ID = INPUT_FILE_ID;
	END IF;
	SELECT COUNT(*) INTO OUTPUT_FINISHED_COUNT FROM UI_FILE_LOG WHERE `STATUS` = 'S' AND FILE_STATUS = 'S' AND FILE_ID = INPUT_FILE_ID;
COMMIT;
END