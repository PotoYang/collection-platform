jarname=`ls *.jar|head -1`
pid=`ps -ef|grep ${jarname}|grep -v "grep"|awk '{print $2}'`
echo $pid
kill $pid
ps -ef|grep ${jarname}.jar