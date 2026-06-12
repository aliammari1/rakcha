const functions = require("firebase-functions");
const admin = require("firebase-admin");
const { cinemaConcierge } = require("./concierge");

admin.initializeApp();

exports.onUserDeleted = functions.auth.user().onDelete(async (user) => {
  const firestore = admin.firestore();
  await firestore.collection("users").doc(user.uid).delete();
});

// AI cinema concierge / film recommender (LangGraph + @langchain/anthropic).
exports.cinemaConcierge = cinemaConcierge;
