from Setting.SystemConfig import *
from Setting.clustertemplate import  *
from pprint import pprint
from copy import deepcopy
import time
from googleapiclient import discovery
from oauth2client.client import GoogleCredentials

credentials = GoogleCredentials.get_application_default()

service = discovery.build('container', 'v1', credentials=credentials)


def cleanClusters():
    start_time = time.time()
    for cluster_id,value in ClusterConfig.items():
        print ("Clean up {0}".format(cluster_id))

        request = service.projects().zones().clusters().delete(projectId=PROJECT_ID, zone=ZONE, clusterId=cluster_id)
        try:
            response = request.execute()
            #pprint(response)
        except Exception as e:
            print ("{0} cluster not found".format(cluster_id))

    #check existence
    finish=False
    while ( not finish ):
        finish=True
        for cluster_id,value in ClusterConfig.items():
            #check each server
            try:
                res = getClusters(cluster_id)
                if(res is not None):
                    finish=False
            except:
                print ("cluster {0} deleted".format(cluster_id))

        time.sleep(5)
    end_time = time.time()
    print("--- Clean cluster finished in {0} seconds ---".format(end_time - start_time))
    return

def getClusters(cluster_id):

    request = service.projects().zones().clusters().get(projectId=PROJECT_ID, zone=ZONE, clusterId=cluster_id)
    response = request.execute()
    return response


def createClusters():
    start_time = time.time()

    for cluster_id,value in ClusterConfig.items():
        createCluster(cluster_id,value["numpod"])

    #check existence
    finish=False
    while ( not finish ):
        finish=True
        for cluster_id,value in ClusterConfig.items():
            #check each server
            try:
                res = getClusters(cluster_id)
                if(res['status']!='RUNNING'):
                    finish=False
                    print ("cluster {0} not yet run".format(cluster_id))
            except:
                print ("cluster {0} not yet found".format(cluster_id))
                finish=False

        time.sleep(5)

    end_time = time.time()
    print("--- Create cluster finished in {0} seconds ---".format(end_time - start_time))
    return


def createCluster(name,numofNode):
    reqbody={}
    config = deepcopy(cluster_request_template)
    config['name']=name
    config['zone']=ZONE
    config['initialNodeCount']=numofNode
    reqbody["cluster"]=config
    request = service.projects().zones().clusters().create(projectId=PROJECT_ID, zone=ZONE, body=reqbody)
    response = request.execute()


    return response