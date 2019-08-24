export YOUR_TOPIC_NAME=dlttestupdatepullpubsubtopic
export YOUR_SUBSCRIPTION_NAME=dlttestupdatepullsubscription
export YOUR_TOKEN=we_eat_for_life
export YOUR_APP_ID=fo-ods


gcloud beta pubsub topics create $YOUR_TOPIC_NAME
#echo $SERVICE
#echo $SERVICE$YOUR_SUBSCRIPTION_NAME
#echo https://$SERVICE-dot-$YOUR_APP_ID.appspot.com/pubsub/push?token=$YOUR_TOKEN \

gcloud beta pubsub subscriptions create $SERVICE_$YOUR_SUBSCRIPTION_NAME \
    --topic $YOUR_TOPIC_NAME \
    --ack-deadline 600
