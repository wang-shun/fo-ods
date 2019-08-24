#gcloud spanner instances create test-instance --config=regional-us-central1 --description="Test Instance" --nodes=1
from Setting.SystemConfig import *
import os
import argparse
from google.cloud import spanner
from myresource.tblschema import *

def createSpannerDB():
    m = SpannerDBConfig
    templ=m["SpannerDeleteTemplate"]
    templ=templ.replace("${INSTANCENAME}",m["INSTANCENAME"])
    ret=os.system(templ)
    print("Delete Spanner return: {0}".format(ret))

    templ = m["SpannerCreationTemplate"]
    templ=templ.replace("${INSTANCENAME}",m["INSTANCENAME"])
    templ=templ.replace("${REGIONSPANNER}",m["REGIONSPANNER"])
    templ=templ.replace("${NODES}",m["NODES"])
    templ=templ.replace("${DES}",m["DES"])



    print (templ)
    ret=os.system(templ)
    print("Build Spanner return: {0}".format(ret))

    create_database(m["INSTANCENAME"],m["DBID"])

    return ret



def create_database(instance_id, database_id):
    """Creates a database and tables for sample data."""
    spanner_client = spanner.Client()
    instance = spanner_client.instance(instance_id)


    database = instance.database(database_id, ddl_statements=schema)

    operation = database.create()

    print('Waiting for operation to complete...')
    operation.result()

    print('Created database {} on instance {}'.format(
        database_id, instance_id))