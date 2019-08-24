'use strict';

// Initializes IRSTradeCompr.
function BulkAmend() {
  this.checkSetup();
  this.initFirebase();
  this.initApp();
}

// Checks that the Firebase SDK has been correctly setup and configured.
BulkAmend.prototype.checkSetup = function () {
  if (!window.firebase || !(firebase.app instanceof Function) || !firebase.app().options) {
    window.alert('You have not configured and imported the Firebase SDK. ' +
      'Make sure you go through the codelab setup instructions and make ' +
      'sure you are running the codelab using `firebase serve`');
  }
};

// Sets up shortcuts to Firebase features and initiate firebase auth.
BulkAmend.prototype.initFirebase = function() {
  // Shortcuts to Firebase SDK features.
  this.auth = firebase.auth();
  this.database = firebase.database();
  this.storage = firebase.storage();
};

BulkAmend.prototype.initApp = function(){
  let fullGridRef = sessionStorage.getItem("fullGridRef");
  let bulkAmendGrid = new BulkAmendGrid();
  bulkAmendGrid.init(fullGridRef+'');
  bulkAmendGrid.render("bulkAmendGrid");  

};

window.onload = function() {
  window.BulkAmend = new BulkAmend();
};
