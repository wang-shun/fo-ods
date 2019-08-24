"""
BEFORE RUNNING:
---------------
1. If not already done, enable the Google Container Engine API
   and check the quota for your project at
   https://console.developers.google.com/apis/api/container
2. This sample uses Application Default Credentials for authentication.
   If not already done, install the gcloud CLI from
   https://cloud.google.com/sdk and run
   `gcloud beta auth application-default login`.
   For more information, see
   https://developers.google.com/identity/protocols/application-default-credentials
3. Install the Python client library for Google APIs by running
   `pip install --upgrade google-api-python-client`
"""
from pprint import pprint

from googleapiclient import discovery
from oauth2client.client import GoogleCredentials

from GKEOPT import ClusterOpt


credentials = GoogleCredentials.get_application_default()

service = discovery.build('container', 'v1', credentials=credentials)

# The Google Developers Console [project ID or project
# number](https://support.google.com/cloud/answer/6158840).
project_id = 'fo-ods'  # TODO: Update placeholder value.

# The name of the Google Compute Engine [zone](/compute/docs/zones#available)
# to return operations for.
zone = 'europe-west1-c'  # TODO: Update placeholder value.

request = service.projects().zones().getServerconfig(projectId=project_id, zone=zone)
response = request.execute()

# TODO: Change code below to process the `response` dict:
pprint(response)