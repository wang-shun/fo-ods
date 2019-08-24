
export YOUR_TOKEN=we_eat_for_life

curl -H "Content-Type: application/json" -i --data @sample_message.json ":8080/pubsub/push?token=$YOUR_TOKEN"