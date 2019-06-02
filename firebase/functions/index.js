const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()
const db = admin.firestore();




// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

exports.updateUserCount = functions.firestore
	.document('/readers/{userId}').onCreate((snap,context) => {
		
		console.log('new user created');

		var userTotal = db.collection('readers').doc('user_count')

		console.log(userTotal["user_total"]);
		var newTotal = userTotal["user_total"];
		// var newTotal = userTotal.get().then(function(value) {
		// 	console.log(value.user_total)

		// });
		newTotal++; 
		userTotal.update({user_total : newTotal});
		return userTotal;
	});

