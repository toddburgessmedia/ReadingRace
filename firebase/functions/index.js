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

		// const newTotal = 0;
		var userTotal = db.collection('readers').doc('user_count');

		return userTotal.get()
  			.then(doc => {
    			if (!doc.exists) {
					  console.log('No such document!');
					  return null;
    			} else {
					  console.log('Document data:', doc.data());
					  console.log(doc.data.user_total);
					  var newTotal = doc.data().user_total;
					  newTotal++
					  console.log(newTotal);
					  return userTotal.update({user_total : newTotal});
				}
  			})
  			.catch(err => {
    			console.log('Error getting document', err);
  			});

		
		
	});

	exports.deleteUserCount = functions.firestore
	.document('/readers/{userId}').onDelete((snap,context) => {
		
		console.log('new user deleted');

		// const newTotal = 0;
		var userTotal = db.collection('readers').doc('user_count');

		return userTotal.get()
  			.then(doc => {
    			if (!doc.exists) {
					  console.log('No such document!');
					  return null;
    			} else {
					  console.log('Document data:', doc.data());
					  console.log(doc.data.user_total);
					  var newTotal = doc.data().user_total;
					  newTotal--;
					  console.log(newTotal);
					  return userTotal.update({user_total : newTotal});
				}
  			})
  			.catch(err => {
    			console.log('Error getting document', err);
  			});

		
		
	});

