from kubernetes import client, config
import subprocess
import os

templ="gcloud container clusters get-credentials ${container}"

background=None

def startbkserver(containerName,port):
    initCmd=templ.replace("${container}",containerName)
    os.system(initCmd)

    #subprocess.Popen(["kubectl","proxy -p", str(port)])
    command = "kubectl proxy -p "+str(port) +"&"
    global background
    background=subprocess.Popen(command, shell=True)

    return

def main():

    startbkserver("sit-ir-tradecore-lcm",9001)


    # Configs can be set in Configuration class directly or using helper
    # utility. If no argument provided, the config will be loaded from
    # default location.
    config.load_kube_config()


    v1 = client.CoreV1Api()
    print("Listing pods with their IPs:")
    ret = v1.list_pod_for_all_namespaces(watch=False)
    for i in ret.items:
        print("%s\t%s\t%s" %
              (i.status.pod_ip, i.metadata.namespace, i.metadata.name))

    background.pid=background.pid+1
    background.kill()

if __name__ == '__main__':
    main()