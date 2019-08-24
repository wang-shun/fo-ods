import os
from Setting.SystemConfig import *
from Setting.SystemConfig import JavaDir
import subprocess
from subprocess import call


def buildJavaModule():
    os.chdir(JavaDir)
    command="mvn clean install -DskipTests"
    #ret = subprocess.call(command)
    #mavenProcess = subprocess.Popen('mvn clean install -DskipTests', stdin =     subprocess.PIPE, stdout = subprocess.PIPE, stderr = subprocess.PIPE)
    # use forwarder streams
    #print (mavenProcess.stdout.read(-1))
    #print (mavenProcess.stdout.read(-1))
    #print("Build Java return: {0}".format(ret))
    ret=os.system(command)
    print("Build Java return: {0}".format(ret))
    return ret

def switchCluster(cluster):
    cmd= gcloudContainerLoginTempl
    cmd = cmd.replace("${clusername}",cluster)
    os.system(cmd)

from shutil import copyfile

def deployDocker(OrgDirectory):
    #myJavaDir=os.path.join(OrgDirectory, JavaDir)
    #OrgDirectory = "/Users/dexter/sandbox/fo-ods/sandbox/" #os.path.dirname(os.path.join(os.path.realpath(__file__)))
    myJavaDir=JavaDir
    #os.chdir(JavaDir)
    for service, configlst in artifactDockerConfig.items():
        for config in configlst:
            os.chdir(OrgDirectory)
            cluster=config[0]
            image=config[1]
            print("deploy {0} service into cluster {1} with image {2}".format(service, cluster,image))
            prjdir = os.path.join(OrgDirectory,service)
            dockerFile=os.path.join(prjdir,config[2])

            os.chdir(prjdir)
            copyfile(dockerFile,os.path.join(prjdir,"Dockerfile"))
            cmd = DockerDeployTemplate
            cmd=cmd.replace("${image}",image)
            #login cluster
            switchCluster(cluster)
            os.system(cmd)

