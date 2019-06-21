const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp()
const db = admin.firestore();


exports.updateUserCount = functions.firestore
	.document('/readers/{userId}').onCreate((snap,context) => {
		
		console.log('new user created');

		// const newTotal = 0;
		var userTotal = db.collection('readers').doc('user_count');

		
		return db.runTransaction(t => {
			return t.get(userTotal)
			  .then(doc => {
				var newTotal = doc.data().user_total + 1;
				t.update(userTotal, {user_total: newTotal});
				return Promise.resolve('it worked and was saved');
			  });
		  }).then(result => {
			console.log('Transaction success!');
			return null;
		  }).catch(err => {
			console.log('Transaction failure:', err);
		  });
		
});

exports.deleteUserCount = functions.firestore
	.document('/readers/{userId}').onDelete((snap,context) => {
		
		console.log('new user deleted');

		// const newTotal = 0;
		var userTotal = db.collection('readers').doc('user_count');

		return db.runTransaction(t => {
			return t.get(userTotal)
			  .then(doc => {
				var newTotal = doc.data().user_total - 1;
				t.update(userTotal, {user_total: newTotal});
				return Promise.resolve('it worked and was saved');
			  });
		  }).then(result => {
			console.log('Transaction success!');
			return null;
		  }).catch(err => {
			console.log('Transaction failure:', err);
		  });

		
		
});

exports.sendNewBook = functions.firestore.document('/books/{bookId}').onCreate((snap,context) => {

	const payload = {
		notification: {
			title : 'New Book Started',
			body: 'Someone started reading a new book'
		}
	};

	return admin.messaging().sendToTopic('newbook',payload);
		
});