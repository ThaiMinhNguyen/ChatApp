const admin = require('firebase-admin');
require('dotenv').config();

const serviceAccount = require('../serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: `https://${process.env.FIREBASE_PROJECT_ID}.firebaseio.com`
});

const db = admin.firestore();
const messaging = admin.messaging();

module.exports = { admin, db, messaging };