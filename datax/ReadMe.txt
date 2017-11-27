1.datax 具体环境配置，详见官网地址:https://github.com/alibaba/DataX
2.{datax_home}/bin/execute.sh 启动脚本可配置动态参数，后台运行并打印info.log日志
3.修改 config.json 文件配置:
    3.1.适用于mysql与mysql数据库间的数据传输
    3.2.column字段值只能写全表字段，不能采用"*"
    3.3.修改传输的数据库地址用户名地址，表名
