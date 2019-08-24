// Import the Firebase SDK for Google Cloud Functions.
const functions = require('firebase-functions');
const admin = require('firebase-admin');

// Adds a message that authenticates users.
exports.userAuthentication = functions.auth.user().onCreate((event) => {
    const user = event.data;
    const fullName = user.displayName || 'Anonymous';
    if (user.email.includes('@maplequad.com')) {
        console.log('A new valid user signed in ' + fullName);
    } else {
        console.log('A non maplequad user attempted to sign in ' + fullName);
        // admin.auth().deleteUser(user.uid)
            admin.auth().updateUser(user.uid, {
                disabled: true,
            })
            .then(function() {
                console.log('Successfully disabled user');
            })
            .catch(function(error) {
                console.log('Error disabling user:', error);
            });
    }
});
