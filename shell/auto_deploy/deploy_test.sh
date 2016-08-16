#!/bin/bash

SSH_HOST=yd_test
# {“test”:"测试环境","prod":"生产环境"}
ENV=$2
#cms jar file
CMS_JAR_NAME=yaodian-cms-0.0.1-SNAPSHOT.jar
#weixin_server jar file
WEIXINSERVER_JAR_NAME=yaodian-weixin-server-0.0.1-SNAPSHOT.jar
#crontab job jar file
CRONTAB_JOB_JAR_NAME=yaodian-cronjob-0.0.1-SNAPSHOT.jar
#cms jar paht
CMS_JAR_PATH=/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-cms/target
#weixin_server jar path
WEIXINSERVER_JAR_PATH=/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-weixin-server/target
#crontab job jar path
CRONTAB_JOB_JAR_PATH=/Users/lizebin/work/yao_dian/svn/1/src/yaodian-root/yaodian-cronjob/target
#ROOT_PATH
ROOT_PATH=/home/yaodian
#backup path
BACKUP_PATH=$ROOT_PATH/backup
#当前时间
CURRENT_DATE_TIME=$(date "+%Y%m%d%H%M%S")

ssh $SSH_HOST << EOF

cd $BACKUP_PATH

mkdir $CURRENT_DATE_TIME

cp $ROOT_PATH/$CMS_JAR_NAME $BACKUP_PATH/$CURRENT_DATE_TIME
cp $ROOT_PATH/$WEIXINSERVER_JAR_NAME $BACKUP_PATH/$CURRENT_DATE_TIME
cp $ROOT_PATH/$CRONTAB_JOB_JAR_NAME $BACKUP_PATH/$CURRENT_DATE_TIME

exit
EOF

scp $CMS_JAR_PATH/$CMS_JAR_NAME $SSH_HOST:$ROOT_PATH/
scp $WEIXINSERVER_JAR_PATH/$WEIXINSERVER_JAR_NAME $SSH_HOST:$ROOT_PATH/
scp $CRONTAB_JOB_JAR_PATH/$CRONTAB_JOB_JAR_NAME $SSH_HOST:$ROOT_PATH/

echo done!