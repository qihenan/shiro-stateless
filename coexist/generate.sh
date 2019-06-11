#!/bin/bash
echo 'generate start-------------->>>>>'

cmd_mvn=" -Dmybatis.generator.overwrite=true mybatis-generator:generate"
cmd_generate=" -Dmybatis.generator.configurationFile"


generateFile=("src/main/resources/generatorConfig.xml")
echo ${#generateFile}

for s in ${generateFile[@]}
do
     cmd=""

     echo ""
	 echo ""
     echo "generate------>>> $s"

 if [ -z "$s" ]
 then
      echo $cmd_mvn
      cmd="$cmd_mvn"
 else
      echo $cmd_generate
      cmd="$cmd_mvn $cmd_generate=$s"
 fi


    echo $cmd

    mvn $cmd

	echo ""
	echo ""

done

echo " "
echo "========================finish=========================="
