
import argparse
from Setting.SystemConfig import *
from google.cloud import pubsub_v1

def deletePubSubSet():
    for topic, sub_set in pubsubConfig.items():
        try:
            delete_topic(PROJECT_ID,topic)
        except:
            print("topic {0} not deleted".format(topic))

        for sub in sub_set:
            try:
                delete_subscription(PROJECT_ID,sub["subname"])
            except :
                print("Sub {0} not deleted".format(sub["subname"]))
    return

def createPubSubSet():
    for topic, sub_set in pubsubConfig.items():
        try:
            create_topic(PROJECT_ID,topic)
        except :
            print("topic {0} failed to create".format(sub["subname"]))

        for sub in sub_set:
            try:
                create_subscription(PROJECT_ID,topic,sub["subname"],  sub["ack"] )
            except :
                print("Sub {0} failed to create".format(sub["subname"]))

    return

def checkPubSub():
    checkOK=True
    topicLst=list_topics(PROJECT_ID)
    publisher = pubsub_v1.PublisherClient()
    subscriber = pubsub_v1.SubscriberClient()

    for topic, sub_set in pubsubConfig.items():

        topic_path = publisher.topic_path(PROJECT_ID, topic)
        if(topic_path in topicLst):
            #check subscription
            subLst = list_subscriptions(PROJECT_ID,topic)
            for sub in sub_set:

                subscription_path = subscriber.subscription_path(PROJECT_ID, sub["subname"])
                if(subscription_path not in subLst):
                    print("Sub {0} lost in topic {1}".format(sub,topic))
                    checkOK=False
        else:
            print("Topic {0} not found".format( topic))
            checkOK=False

    if(checkOK):
        print("Pub/sub set checked OK!")
        return 0
    else:
        print("Pub/sub set checked failed!")
        return -1

def create_topic(project, topic_name):
    """Create a new Pub/Sub topic."""
    publisher = pubsub_v1.PublisherClient()
    topic_path = publisher.topic_path(project, topic_name)

    topic = publisher.create_topic(topic_path)

    print('Topic created: {}'.format(topic))


def delete_topic(project, topic_name):
    """Deletes an existing Pub/Sub topic."""
    publisher = pubsub_v1.PublisherClient()
    topic_path = publisher.topic_path(project, topic_name)

    publisher.delete_topic(topic_path)

    print('Topic deleted: {}'.format(topic_path))



def create_subscription(project, topic_name, subscription_name,ack):
    """Create a new pull subscription on the given topic."""
    subscriber = pubsub_v1.SubscriberClient()
    topic_path = subscriber.topic_path(project, topic_name)
    subscription_path = subscriber.subscription_path(
        project, subscription_name)

    subscription = subscriber.create_subscription(
        subscription_path, topic_path,ack_deadline_seconds=ack)

    print('Subscription created: {}'.format(subscription))


def delete_subscription(project, subscription_name):
    """Deletes an existing Pub/Sub topic."""
    subscriber = pubsub_v1.SubscriberClient()
    subscription_path = subscriber.subscription_path(
        project, subscription_name)

    subscriber.delete_subscription(subscription_path)

    print('Subscription deleted: {}'.format(subscription_path))


def list_topics(project):
    topicLst=[]
    """Lists all Pub/Sub topics in the given project."""
    publisher = pubsub_v1.PublisherClient()
    project_path = publisher.project_path(project)

    for topic in publisher.list_topics(project_path):
        topicLst.append(topic.name)
    return topicLst

def list_subscriptions(project, topic_name):
    """Lists all subscriptions for a given topic."""
    subList=[]
    subscriber = pubsub_v1.SubscriberClient()
    topic_path = subscriber.topic_path(project, topic_name)

    for subscription in subscriber.list_subscriptions(topic_path):
        subList.append(subscription.name)
    return subList