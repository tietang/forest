#! /bin/sh

cur_dir=$(dirname $0)
#JAVA_OPTS="-Xmx4000M -Xms4000M -Xmn600M -XX:PermSize=500M -XX:MaxPermSize=500M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0 "  
# for log:JAVA_OPTS="-Xmx4000M -Xms4000M -Xmn600M -XX:PermSize=500M -XX:MaxPermSize=500M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=0 -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:log/gc.log"  
JAVA_OPTS="-Xms2048m -Xmx2048m -XX:NewSize=256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -server"

PROCESS=fengfei.shard.performance.WriteReadMain

case "$1" in
  'start')
    mkdir -p $cur_dir/logs
    nohup java -cp $cur_dir/../config/:$cur_dir/../lib/* $PROCESS 10 200 0 30000000 30000000 > $cur_dir/logs/out.log 2>&1 &
    echo "start finish"
     ;;

  'stop')
    ps -ef| grep $PROCESS|grep java|awk '{print $2}' | xargs kill -9
    echo "Example stopped."
    ;;

   'info')

    ps -ef| grep $PROCESS|grep java
    ;;

  'restart')
    ps -ef| grep $PROCESS|grep java|awk '{print $2}' | xargs kill -9
    echo "Example stopped."

     nohup java -cp $cur_dir/../config/:$cur_dir/../lib/* $PROCESS  > $cur_dir/logs/out.log 2>&1 &
    echo "Example restart."
    ;;
  *)
    basename=`basename "$0"`
    echo "Usage: $basename  {start|stop|restart}  [ Example options ]"
    exit 1
    ;;


esac

