#!/bin/ksh

#****************************************************************
#
# (C) Copyright Talent Information Solutions Ltd. 2014.11.11
#
# File name:	file_ftp2dgyc.sh
# Project:	CFBS
#
# Aim:		To put the files to the dgyc server.
#
# Creation Date:	2014.11.11
# Origin Author:	Li Yuan
#
# Version:      %I%
#
# Modification History
#
# Reference     Date            Modified By     Reason
# ---------     ----            -----------     ------
#
#****************************************************************

#
#	ftp constant variable
#
FTP_ADDR="31.1.4.16"
FTP_USER=dgyc
FTP_PWD=dgyc1113
#FTP_ADDR="56.16.32.193"
#FTP_USER=administrator
#FTP_PWD=1

#
#	input variable
#
LOC_SND_DIR=$1
LOC_TMP_DIR=$2
FILE_NAME=$3
RMT_SND_DIR=$4
RMT_TMP_DIR=$5
PID_NUMBER=$6
TMP_FILE_SUFFIX=$7
FTP_SUCCESS_MSG="^226 "
FTP_INFORMATION_LOG=$LOC_TMP_DIR/"ftp_information_"$PID_NUMBER".log"
#
#	check the file
#
cd $LOC_SND_DIR

ls -l $FILE_NAME$TMP_FILE_SUFFIX >/dev/null 2>&1
if [ "$?" = "1" ]
then
	exit 1
fi


#
#	copy the orignial file to a new file
#	with ".tmp" in the name.
#
TMP_FILE_NAME=$FILE_NAME$TMP_FILE_SUFFIX

#
#	put the file
#
ftp -inv $FTP_ADDR <<! >> $FTP_INFORMATION_LOG
user $FTP_USER $FTP_PWD
bin
cd $RMT_TMP_DIR
put $TMP_FILE_NAME
ls -l
rename $RMT_TMP_DIR/$TMP_FILE_NAME $RMT_SND_DIR/$FILE_NAME
close
quit
!
if grep "$FTP_SUCCESS_MSG" $FTP_INFORMATION_LOG ;then
	#if ftp transfer success,it will remove temp file 
	rm $TMP_FILE_NAME
else
	#if ftp transfer failure,it will rename temp file to file
	mv $TMP_FILE_NAME $FILE_NAME
fi

if [ -f $FTP_INFORMATION_LOG ]
then
	rm -rf $FTP_INFORMATION_LOG
fi
#****************************************************************
#
#		End of file
#
#****************************************************************
