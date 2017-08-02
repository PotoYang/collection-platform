jarname=$(ls *.jar)
nohup java -jar $jarname > nohup.out 2>&1 &
