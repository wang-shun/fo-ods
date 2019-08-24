#TradeCore on Web

#Dev Setup
#Upgrade to latest npm
sudo npm install -g npm@5.2.0
sudo npm install -g npm@latest
sudo npm i -g npm

#Rules for coding it right for the first time
#https://eslint.org/docs/user-guide/getting-started
npm install eslint
eslint --init

#Install all necessary tooling to get going
sudo npm install -g firebase-tools
#sudo npm install -g firebase-admin
sudo npm install -g firebase-functions
#npm i --save firebase-functions

#Get Firebase Credentials from your GCP Firebase Account
firebase login

#Get The Undelying project hooked to the exact Firebase project from available ones
firebase use default
firebase use --add

#Install all dependencies for the project
cd functions
npm install --save firebase-functions@latest
npm install

cd ..

# Lauch the project locally on port 5000
firebase serve

# Launch the project on a specific port say 8081
firebase serve -p 8081

# to only emulate functions
firebase serve --only functions 

# to emulate both functions and hosting
firebase serve --only functions,hosting 

# To deploy the code to external hosting
# firebase deploy

# To Disable the External Hosting INSTANTLY!
# firebase hosting:disable

## Some Debugging Help for NodeJS

which node
/usr/local/bin/node
which npm
/usr/local/bin/npm
        npm -v
5.3.0
node -v
v6.11.2

-- Local Debug won't work
v8.4.0


sudo rm -rf /usr/local/lib/node_modules/
        sudo rm -rf functions/node_modules

Install Node 6.11.1
npm install -g npm@5.2.0

nvm ls
nvm uninstall ***

sudo npm uninstall npm -g

sudo rm -rf /usr/local/lib/node_modules/
sudo rm -rf functions/node_modules
sudo rm -rf /usr/local/{lib/node{,/.npm,_modules},bin,share/man}/npm*
sudo rm -rf /usr/local/lib/node_modules
sudo rm -rf /usr/local/include/node_modules
sudo rm -rf /usr/local/bin/node
sudo rm -rf /usr/local/bin/node-debug
sudo rm -rf /usr/local/bin/node-gyp
sudo rm -rf ~/.npm
sudo rm -rf ~/.node-gyp
sudo rm -rf ~/.node_repl_history
sudo rm -rf /usr/local/share/man/man1/node*
sudo rm -rf /usr/local/share/man/man1/npm*
sudo rm -rf /usr/local/lib/dtrace/node.d 
sudo rm -rf /opt/local/bin/node
sudo rm -rf /opt/local/include/node
sudo rm -rf /opt/local/lib/node_modules
sudo rm -rf /usr/local/share/doc/node
sudo rm -rf /usr/local/share/systemtap/tapset/node.stp
sudo rm -rf /Users/Madhav/.nvm/versions/node/v*

nvm install 6.11.1

sudo npm i -g firebase-tools --unsafe-

If you're upgrading @google-cloud/functions-emulator, these
are the recommended upgrade steps:

1.  Stop the currently running emulator, if any:

        functions stop

2.  Uninstall the current emulator, if any:

        npm uninstall -g @google-cloud/functions-emulator

3.  Install the new version of the emulator:

        npm install -g @google-cloud/functions-emulator

If you have trouble after upgrading, try deleting the config
directory found in:

sudo killall -9 node
Then restart the emulator. You can also check for any renegade
Node.js emulator processes that may need to be killed:

    ps aux | grep node


#### Deployment

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
functions.config().ir.lcm.url

## SIT
firebase functions:config:set ir_lcm.url='35.195.236.201:3000'
firebase functions:config:set eq_lcm.url='35.195.249.183:3000'
firebase functions:config:set xa_store.url='104.155.34.173:3002'
firebase functions:config:set ir_balcm.url='130.211.48.239:3001'
firebase functions:config:set eq_balcm.url='35.190.223.18:3001'
firebase deploy -P sit --only functions

## UAT
firebase functions:config:set ir.lcm.url=146.148.126.11:3001
firebase functions:config:set eq.lcm.url=35.187.89.197:3001
firebase functions:config:set xa.store.url=35.195.216.51:3002
firebase functions:config:set ir.balcm.url=35.187.18.217:3001
firebase functions:config:set eq.balcm.url=35.195.150.8:3001
firebase deploy -P uat --only functions