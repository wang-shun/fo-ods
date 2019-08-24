export DLT_PUBSUB_FAILQUEUE_TOPIC=sit-ir-dlt-err
export PUBSUB_TOPIC=sit-ir-ult-feed
#export PUBSUB_TOPIC=dlttestupdatepullpubsubtopic
export INSTANCEID=tradecore-stats
export DATABASEID=statsdb2
export DLT_PUBSUB_SUBCRIBE=sit-ir-lcm-feed-dlt-sub
export SIM_UPSTREAM=true
export IGNORE_ERROR=true
export SUCCESS_RATE=0.9
java -cp target/tradecore-dlt-1.0-SNAPSHOT.jar foods.dlt.stats.StatsMain fo-ods dltsubscription tradecore-stats statsdb2 true