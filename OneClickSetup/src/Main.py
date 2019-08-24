from GKEOPT.ClusterOpt import *
from GKEOPT.buildModule import *
from GKEOPT.SpannerCreation import  *
from GKEOPT.pubsubcreation import *
import os
from pprint import pprint
import sys

import os


path = os.path.dirname(os.path.realpath(__file__))

JavaDir=os.path.join(path, JavaDir)
print("Working on directory:{0} Java Project dir:{1}".format(path,JavaDir))




userOption=False
line =input("Clean cluster confirm [Y/y] or [N/n]")
if line!="Y" and line!="y" :
    print("skip clean cluster")
else:
    userOption=True
if(userOption):
    cleanClusters()

userOption=False
line=input ("Create cluster confirm [Y/y] or [N/n]")
if line!="Y" and line!="y" :
    print("skip build cluster")
else:
    userOption=True
if( userOption):
    createClusters()
    print("Clusters creation finish!")
    for cluster_id,value in ClusterConfig.items():
        print("Cluster {0} created ok with nodes {1} !".format(cluster_id,value["numpod"]))

line=input ("Run Java module built confirm [Y/y] or [N/n]")
userOption=False
if line!="Y" and line!="y" :
    print("skip build java module")
else:
    userOption=True

if( userOption):
    buildJavaModule()
    deployDocker(JavaDir)


userOption=False
line =input("rebuild pub/sub set confirm [Y/y] or [N/n]")
if line!="Y" and line!="y" :
    print("skip rebuild pub/sub")
else:
    userOption=True
if(userOption):
    deletePubSubSet()
    createPubSubSet()
    checkPubSub()

userOption=False
line =input("Create Spanner instance confirm [Y/y] or [N/n]")
if line!="Y" and line!="y" :
    print("skip Spanner creation")
else:
    userOption=True
if(userOption):
    createSpannerDB()
