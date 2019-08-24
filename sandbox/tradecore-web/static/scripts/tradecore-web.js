'use strict';

// Initializes TradeCoreWeb.
function TradeCoreWeb() {
  this.checkSetup();

  // Shortcuts to DOM Elements.
  this.userPic = document.getElementById('user-pic');
  this.userName = document.getElementById('user-name');
  this.signInButton = document.getElementById('sign-in');
  this.signOutButton = document.getElementById('sign-out');
  this.signInSnackbar = document.getElementById('must-signin-snackbar');

  // Saves message on form submit.
  //this.messageForm.addEventListener('submit', this.saveMessage.bind(this));
  this.signOutButton.addEventListener('click', this.signOut.bind(this));
  this.signInButton.addEventListener('click', this.signIn.bind(this));

  // Toggle for the button.
  var buttonTogglingHandler = this.toggleButton.bind(this);

  this.initFirebase();
}

// Sets up shortcuts to Firebase features and initiate firebase auth.
TradeCoreWeb.prototype.initFirebase = function() {
  // Shortcuts to Firebase SDK features.
  this.auth = firebase.auth();
  this.database = firebase.database();
  this.storage = firebase.storage();
  // Initiates Firebase auth and listen to auth state changes.
  this.auth.onAuthStateChanged(this.onAuthStateChanged.bind(this));
};

// Loads watchlist and listens for upcoming ones.
TradeCoreWeb.prototype.loadMessages = function() {
};

// Saves a new message on the Firebase DB.
TradeCoreWeb.prototype.saveMessage = function(e) {
  e.preventDefault();
  // Check that the user entered a message and is signed in.
  if (this.checkSignedInWithMessage()) {
    var currentUser = this.auth.currentUser;
  }
};

// Sets the URL of the given img element with the URL of the image stored in Cloud Storage.
TradeCoreWeb.prototype.setImageUrl = function(imageUri, imgElement) {
  imgElement.src = imageUri;

  // TODO(DEVELOPER): If image is on Cloud Storage, fetch image URL and set img element's src.
};

// Saves a new message containing an image URI in Firebase.
// This first saves the image in Firebase storage.
TradeCoreWeb.prototype.saveImageMessage = function(event) {
  event.preventDefault();
  
  // Check if the user is signed-in
  if (this.checkSignedInWithMessage()) {
  var currentUser = this.auth.currentUser;
    // TODO(DEVELOPER): Upload image to Firebase storage and add message.

  }
};

// Signs-in TradeCore on Web.
TradeCoreWeb.prototype.signIn = function() {
  // Sign in Firebase using popup auth and Google as the identity provider.
  var provider = new firebase.auth.GoogleAuthProvider();
  this.auth.signInWithPopup(provider);
};

// Signs-out of TradeCore on Web.
TradeCoreWeb.prototype.signOut = function() {
  // Sign out of Firebase.
  this.auth.signOut();
  document.getElementById('preLogInTradeCoreContainer').style.display = 'block';
  document.getElementById('tradeCoreContainer').style.display = 'none';

};

// Triggers when the auth state change for instance when the user signs-in or signs-out.
TradeCoreWeb.prototype.onAuthStateChanged = function(user) {
  //alert(JSON.stringify(user, null, 4));
  if (user) { // User is signed in!
    //alert(JSON.stringify(user,null,4));
    // Get profile pic and user's name from the Firebase user object.

    var profilePicUrl = user.photoURL;
    var userName = user.displayName;   
    sessionStorage.setItem("userName", userName);

    // Set the user's profile pic and name.
    this.userPic.style.backgroundImage = 'url(' + profilePicUrl + ')';
    this.userName.textContent = userName;

    // Show user's profile and sign-out button.
    this.userName.removeAttribute('hidden');
    this.userPic.removeAttribute('hidden');
    this.signOutButton.removeAttribute('hidden');

    // Hide sign-in button.
    this.signInButton.setAttribute('hidden', 'true');

    // We load currently existing chant messages.
    //this.loadMessages();

    // We save the Firebase Messaging Device token and enable notifications.
    //TODO UNDO//this.saveMessagingDeviceToken();
    this.initApp();
  } else { // User is signed out!
    // Hide user's profile and sign-out button.
    this.userName.setAttribute('hidden', 'true');
    this.userPic.setAttribute('hidden', 'true');
    this.signOutButton.setAttribute('hidden', 'true');

    // Show sign-in button.
    this.signInButton.removeAttribute('hidden');
  }
};

// Returns true if user is signed-in. Otherwise false and displays a message.
TradeCoreWeb.prototype.checkSignedInWithMessage = function() {
  if (this.auth.currentUser) {
    return true;
  }

  // Display a message to the user using a Toast.
  var data = {
    message: 'You must sign-in first',
    timeout: 2000
  };
  this.signInSnackbar.MaterialSnackbar.showSnackbar(data);
  return false;
};

// Resets the given MaterialTextField.
TradeCoreWeb.resetMaterialTextfield = function(element) {
  element.value = '';
  element.parentNode.MaterialTextfield.boundUpdateClassesHandler();
};

// Template for messages.
TradeCoreWeb.MESSAGE_TEMPLATE =
    '<div class="message-container">' +
      '<div class="spacing"><div class="pic"></div></div>' +
      '<div class="message"></div>' +
      '<div class="name"></div>' +
    '</div>';

// A loading image URL.
TradeCoreWeb.LOADING_IMAGE_URL = 'https://www.google.com/images/spin-32.gif';

// Displays a Message in the UI.
TradeCoreWeb.prototype.displayMessage = function(key, name, text, picUrl, imageUri) {
  var div = document.getElementById(key);
};

// Enables or disables the submit button depending on the values of the input
// fields.
TradeCoreWeb.prototype.toggleButton = function() {
  if (this.messageInput.value) {
    this.submitButton.removeAttribute('disabled');
  } else {
    this.submitButton.setAttribute('disabled', 'true');
  }
};

function tradeBooking(bookingOption){

  //let bookingOption = document.getElementById('cboTradeBooking').value;
  if(bookingOption.match('EQT')){
    alert('Select the Symbol, Right Click and Select your option to Book');
  }
  else if (bookingOption.match('IRS')){
    openIRSBookingForm();
  }
  else if (bookingOption.match('FXF')) {
    alert('Coming Very Soon..');
  } 
  else if (bookingOption.match('STR')) {
    alert('Coming Soon..');
  }
}

function changeExchange() {
  
  let exchangeCode = document.getElementById('cboExchangeCode').value;
  
  if(exchangeCode.match('LSE')){
    document.getElementById('exchangeName').innerHTML = "London Stock Exchange";
    document.getElementById('currencyCode').innerHTML = "GBP";
  }
  else if(exchangeCode.match('NASDAQ')){
    document.getElementById('exchangeName').innerHTML = "Nasdaq Stock Market";
    document.getElementById('currencyCode').innerHTML = "USD";
  }
  else if(exchangeCode.match('JSE')){
    document.getElementById('exchangeName').innerHTML = "Japan Exchange Group";
    document.getElementById('currencyCode').innerHTML = "JPY";
  }
  else if(exchangeCode.match('DE')){
    document.getElementById('exchangeName').innerHTML = "Deutsche Börse";
    document.getElementById('currencyCode').innerHTML = "EUR";
  }
  sessionStorage.setItem("currencyCode", document.getElementById('currencyCode').innerHTML);
  sessionStorage.setItem("exchangeCode", document.getElementById('cboExchangeCode').value);
}

// Checks that the Firebase SDK has been correctly setup and configured.
TradeCoreWeb.prototype.checkSetup = function() {
  if (!window.firebase || !(firebase.app instanceof Function) || !firebase.app().options) {
    window.alert('You have not configured and imported the Firebase SDK. ' +
        'Make sure you go through the codelab setup instructions and make ' +
        'sure you are running the codelab using `firebase serve`');
  }
};

TradeCoreWeb.prototype.render = function(){
  document.getElementById('preLogInTradeCoreContainer').style.display = 'none';
  document.getElementById('tradeCoreContainer').style.display = 'block';
}

TradeCoreWeb.prototype.initApp = function(){
    this.render();

    let exchangeService = new ExchangeService();
    exchangeService.init();

    let stockHistoricalChart = new StockHistoricalChart();
    stockHistoricalChart.init("historyGraph");

    let stockDetailPanel = new StockDetailPanel();
    stockDetailPanel.init(exchangeService, stockHistoricalChart);

    let priceChangesGrid = new PriceChangesGrid();
    priceChangesGrid.init(exchangeService, stockDetailPanel, document.getElementById('cboExchangeCode').value);
    sessionStorage.setItem("currencyCode", "GBP");
    sessionStorage.setItem("exchangeCode", document.getElementById('cboExchangeCode').value);

    let equitiesTradeBlotterGrid = new EquitiesTradeBlotterGrid();
    equitiesTradeBlotterGrid.init();

    let irsTradeBlotterGrid = new IrsTradeBlotterGrid();
    irsTradeBlotterGrid.init();

    let fxMarketData = new FxMarketData();
    fxMarketData.init();

    let eqMarketData = new EqMarketData();
    eqMarketData.init();

    priceChangesGrid.render("priceChangesGrid");
    equitiesTradeBlotterGrid.render("equitiesTradeBlotterGrid");
    irsTradeBlotterGrid.render("irsTradeBlotterGrid");
    fxMarketData.render("fxMarketDataGrid");
    eqMarketData.render("eqMarketDataGrid");
  };

// eg Sep 4, 2017 4:06:07 PM gets converted to 20170904160607
function monthToComparableNumber(date) {
  if (date === undefined || date === null || date.length > 24) {
    return null;
  }

  var monthMap = new Map();
  monthMap.set('Jan', 1);
  monthMap.set('Feb', 2);
  monthMap.set('Mar', 3);
  monthMap.set('Apr', 4);
  monthMap.set('May', 5);
  monthMap.set('Jun', 6);
  monthMap.set('Jul', 7);
  monthMap.set('Aug', 8);
  monthMap.set('Sep', 9);
  monthMap.set('Oct', 10);
  monthMap.set('Nov', 11);
  monthMap.set('Dec', 12);

  let dateArray = date.split(' ');
  let dayNumber = dateArray[1].split(',')[0];
  let timeArray = dateArray[3].split(':');

  var monthNumber = monthMap.get(dateArray[0]);
  let yearNumber = dateArray[2];
  let hourNumber = timeArray[0];
  if (dateArray[4] == 'PM') {
    if (parseInt(hourNumber) < 12) {
      hourNumber = parseInt(hourNumber) + 12;
    }
  }
  let minNumber = timeArray[1];
  let secNumber = timeArray[2];

  var result = (yearNumber * 100000000) + (monthNumber * 1000000) + (dayNumber * 10000) + (hourNumber * 100) + (minNumber * 1) + (secNumber);
  return result;
};

window.onload = function() {
  window.TradeCoreWeb = new TradeCoreWeb();
};
