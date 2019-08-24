import os

PROJECT_ID = 'fo-ods'
ZONE = 'europe-west1-c'
REGION = "europe-west1"

ClusterConfig={


    "uat-eq-tradecore-service":{"numpod":8},
    "uat-fx-tradecore-service":{"numpod":8},
    "uat-ir-tradecore-service":{"numpod":8},


    "uat-xa-tradecore-store":{"numpod":3},
    "uat-xa-tradecore-service":{"numpod":2}
}

JavaDir="../../sandbox"

SpannerDBConfig= {
    "INSTANCENAME":"uat-xa-tradecore",
    "NODES":"2",
    "DES":"uat-xa-tradecore",
    "REGIONSPANNER":"regional-europe-west1",
    "DBID":"tradecore",
    "DBSCHEMA":"resource/tbl-schema",
    "SpannerCreationTemplate":"gcloud spanner instances create ${INSTANCENAME} --config=${REGIONSPANNER} --description=${DES} --nodes=${NODES}",
    "SpannerDeleteTemplate":"gcloud spanner instances delete ${INSTANCENAME}"
}



pubsubConfig={
    #Equity
    "uat-eqt-ult-feed":[ {"subname":"uat-eqt-ult-feed-lcm-sub","ack":600} ],
    "uat-eqt-dlt-err":[ {"subname":"uat-eqt-dlt-err-sub","ack":600} ],
    "uat-eqt-lcm-feed":[ {"subname":"uat-eqt-lcm-feed-dlt-sub","ack":600} ,
                        {"subname":"uat-eqt-lcm-feed-vs-sub","ack":600},
                        {"subname":"uat-eqt-lcm-feed-dist-sub","ack":600}],
    #Fixed INcome
    "uat-ird-ult-feed":[ {"subname":"uat-ird-ult-feed-lcm-sub","ack":600} ],
    "uat-ird-dlt-err":[ {"subname":"uat-ird-dlt-err-sub","ack":600} ],
    "uat-ird-lcm-feed":[ {"subname":"uat-ird-lcm-feed-dlt-sub","ack":600} ,
                        {"subname":"uat-ird-lcm-feed-vs-sub","ack":600},
                        {"subname":"uat-ird-lcm-feed-dist-sub","ack":600}],
    #FX
    "uat-fxd-ult-feed":[ {"subname":"uat-fxd-ult-feed-lcm-sub","ack":600} ],
    "uat-fxd-dlt-err":[ {"subname":"uat-fxd-dlt-err-sub","ack":600} ],
    "uat-fxd-lcm-feed":[ {"subname":"uat-fxd-lcm-feed-dlt-sub","ack":600} ,
                        {"subname":"uat-fxd-lcm-feed-vs-sub","ack":600},
                        {"subname":"uat-fxd-lcm-feed-dist-sub","ack":600}]
}

gcloudContainerLoginTempl="gcloud container clusters get-credentials ${clusername} --zone "+ZONE+" --project " + PROJECT_ID

DockerDeployTemplate="gcloud container builds submit --tag ${image} ."

artifactDockerConfig={
    "tradecore-store":[("uat-xa-tradecore-store","eu.gcr.io/"+PROJECT_ID+"/tradecore-store:v1","src/main/docker/Dockerfile")],
    "lifeloaderdummy":[("uat-xa-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-ult:v1","src/main/docker/Dockerfile")],
    "tradecore-lcm":[("uat-eq-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-lcm:v1","src/main/docker/Dockerfile"),
                     ("uat-fx-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-lcm:v1","src/main/docker/Dockerfile"),
                     ("uat-ir-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-lcm:v1","src/main/docker/Dockerfile")
                     ],
    "tradecore-dlt": [("uat-eq-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-dlt:v1","src/main/docker/Dockerfile"),
                      ("uat-fx-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-dlt:v1","src/main/docker/Dockerfile"),
                      ("uat-ir-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-dlt:v1","src/main/docker/Dockerfile")
                      ],
    "tradecore-vs": [("uat-eq-tradecore-vs","eu.gcr.io/"+PROJECT_ID+"/tradecore-vs:v1","src/main/docker/Dockerfile"),
                     ("uat-fx-tradecore-vs","eu.gcr.io/"+PROJECT_ID+"/tradecore-vs:v1","src/main/docker/Dockerfile"),
                     ("uat-ir-tradecore-vs","eu.gcr.io/"+PROJECT_ID+"/tradecore-vs:v1","src/main/docker/Dockerfile")
                     ],
    "tradecore-balcm":[("uat-eq-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-balcm:v1","src/main/docker/Dockerfile"),
                       ("uat-fx-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-balcm:v1","src/main/docker/Dockerfile"),
                       ("uat-ir-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-balcm:v1","src/main/docker/Dockerfile")
                       ],
    "tradecore-md" : [("uat-eq-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-md:v1","src/main/docker/Dockerfile"),
                      ("uat-fx-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-md:v1","src/main/docker/Dockerfile"),
                      ("uat-ir-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-md:v1","src/main/docker/Dockerfile")
                      ],
    "tradecore-failqueue":[("uat-eq-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-failqueue:v1","src/main/docker/Dockerfile"),
                           ("uat-ir-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-failqueue:v1","src/main/docker/Dockerfile")
                           ],
    "tradecore-bridge":[("uat-eq-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-balcm:v1","src/main/docker/Dockerfile"),
                        ("uat-fx-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-balcm:v1","src/main/docker/Dockerfile"),
                        ("uat-ir-tradecore-service","eu.gcr.io/"+PROJECT_ID+"/tradecore-balcm:v1","src/main/docker/Dockerfile")
                        ]
}



#gcloud container clusters get-credentials uat-ir-tradecore-service --zone europe-west1-c --project fo-ods

