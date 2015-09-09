#!/bin/ksh

#****************************************************************
#
# (C) Copyright Talent Information Solutions Ltd. 2014.11.11
#
# File name:	dgyc_main.sh
# Project:	CFBS
#
# Aim:		To get and put the files to the dgyc fep.
#
# Creation Date:	2014.11.11	
# Origin Author:	Li Yuan	
#
# Version:	%I%
#
# Modification History
#
# Reference	Date		Modified By	Reason
# ---------	----		-----------	------
#
#****************************************************************

#
#	file directory
#
SCRIPT_DIR=/tools

#update1
LOC_SND_DIR=/si/usr/dgyc/fsend
LOC_TMP_DIR=/si/usr/dgyc/tmp
RMT_SND_DIR=/
RMT_TMP_DIR=/
CURRENT_DATE_TIME=$(date "+%Y%m%d%H%M%S")
PID_NUMBER=$$$CURRENT_DATE_TIME
TMP_FILE_SUFFIX=".tmp"
FILE_NAME_FILTER_REGULATION="QRYTYP*"

#----------------------------------------------------------------
#
#	send the txn file
#
#----------------------------------------------------------------

#
#	get the txn file list
#
cd $LOC_SND_DIR

rm -f $LOC_TMP_DIR/file.lst.$PID_NUMBER > /dev/null 2>&1
#search "QRYTYP*" file not directory and output filename
find . -maxdepth 1 -not -type d -type f -name 'QRYTYP*' -printf "%f\n" | grep -v '.tmp'$ | while read LINE
	do
		/usr/sbin/lsof |grep $LINE |grep -v lsof|grep -v grep > /dev/null 2>&1
		if [ "$?" = "1" ]
		then
			mv $LINE $LINE$TMP_FILE_SUFFIX
			echo $LINE >> $LOC_TMP_DIR/file.lst.$PID_NUMBER
			#crontab task will send a file only.
			break
		fi
	done
if [ ! -f "$LOC_TMP_DIR/file.lst.$PID_NUMBER" ] 
then 
	exit 1
fi
FILE_SIZE=`ls -l $LOC_TMP_DIR/file.lst.$PID_NUMBER | awk '{ print $5 }'`
if [ $FILE_SIZE -eq 0 ]
then
	rm -f $LOC_TMP_DIR/file.lst.$PID_NUMBER > /dev/null 2>&1
fi


#
#	get the file name and put
#
if [ "$FILE_SIZE" != "0" ]
then
	cat $LOC_TMP_DIR/file.lst.$PID_NUMBER | while read LINE
	do
		#update3
		/usr/sbin/lsof |grep $LINE$TMP_FILE_SUFFIX |grep -v lsof|grep -v grep > /dev/null 2>&1
		if [ "$?" = "1" ]
		then
			$SCRIPT_DIR/file_ftp2dgyc_for_crontab_second_5.sh $LOC_SND_DIR $LOC_TMP_DIR $LINE $RMT_SND_DIR $RMT_TMP_DIR $PID_NUMBER $TMP_FILE_SUFFIX
		fi
	done
fi
if [ -f "$LOC_TMP_DIR/file.lst.$PID_NUMBER" ]
then
	rm $LOC_TMP_DIR/file.lst.$PID_NUMBER
fi
#****************************************************************
#
#		End of file
#
#****************************************************************
