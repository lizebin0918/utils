#!/bin/ksh

#if the script startuped by crontab,
#it will ececute dgyc_main_for_cronb_second_5.sh per 5 seconeds.
BASE_HOME=/tools
interval_second=5
let interval_count=60/interval_second
script_file=$BASE_HOME/dgyc_main_for_cronb_second_5.sh
for((i=1;i<=${interval_count};i++));do
  ${script_file} 2>/dev/null &
  sleep ${interval_second}
done
