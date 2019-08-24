#export PUBSUB_TOPIC=lifeloaderpubsubtopic
#export PUBSUB_TOPIC=ultpullpubsubtopic
#export PUBSUB_VERIFICATION_TOKEN=we_eat_for_life
export PROJECT=fo-ods


export ULT_SUB_ID=sit-ir-ult-feed-lcm-sub
export DLT_PUBSUB_TOPIC=sit-ir-lcm-feed
export GRPC_PORT=3000
export CRUD_HOST=104.155.34.173
export CRUD_PORT=3002
export RUNULTPUB_SUB=Y
export SUBSCRIBER_THREADS=2
#java -jar target/tradecore-lcm-0.0.1-SNAPSHOT.jar

java -cp target/tradecore-lcm-0.0.1-SNAPSHOT.jar  com.maplequad.fo.ods.tradecore.lcm.app.LifeCycleManagementApp