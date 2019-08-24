export YOUR_TOPIC_NAME=dltpubsubtopic
export YOUR_SUBSCRIPTION_NAME=dltsubscription
export YOUR_TOKEN=we_eat_for_life
export YOUR_APP_ID=fo-ods
export SERVICE=dltloader
echo on
gcloud beta pubsub topics create $YOUR_TOPIC_NAME


gcloud beta pubsub subscriptions create $YOUR_SUBSCRIPTION_NAME \
    --topic $YOUR_TOPIC_NAME \
    --ack-deadline 600


gcloud beta pubsub subscriptions create tradecore-viewserver \
    --topic $YOUR_TOPIC_NAME \
    --ack-deadline 600