const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();

///* Update profile picture */
const createProfile = (userRecord, context) => {
  const { email, phoneNumber, uid, providerData, displayName } = userRecord;
  let photoURL = userRecord.photoURL;
  if (providerData[0].providerId === "facebook.com") {
    photoURL = photoURL + "?height=200";
  }
  const user = admin.auth();
  const createUser = db
    .collection("users")
    .doc(uid)
    .set({
      name: displayName,
      is_first_time: true,
      uid,
      email,
      phone_number: phoneNumber,
      photo_url: photoURL
    })
    .catch(console.error);
  const updatePhotoUrl = user.updateUser(userRecord.uid, {
    photo_url: photoURL
  });
  const createUserLocationPromise = db
    .collection("user_location")
    .doc(uid)
    .set({
      uid,
      time_stamp: admin.firestore.FieldValue.serverTimestamp(),
      user: {},
      status: "online",
      event_status: "going"
    })
    .then(() => {
      return;
    })
    .catch(e => {
      console.log("Update user location error, ", e);
    });
  return Promise.all([updatePhotoUrl, createUser, createUserLocationPromise])
    .then(() => {
      console.log("Create user");
      return;
    })
    .catch(err => {
      console.log("Error create user", err);
    });
};

//update user
const updateUser = (req, res) => {
  const user = req.body;
  const updateUserLocationPromise = db
    .collection("user_location")
    .doc(user.uid)
    .update({
      user
    })
    .then(() => {
      return;
    })
    .catch(e => {
      console.log("Update user location error, ", e);
    });
  const updateUserPromise = db
    .collection("users")
    .doc(user.uid)
    .update({
      name: user.name ? user.name : "",
      photo_url: user.photo_url ? user.photo_url : "",
      phone_number: user.phone_number ? user.phone_number : "",
      is_first_time: false
    });
  return Promise.all([updateUserPromise, updateUserLocationPromise])
    .then(() => {
      console.log("Update user " + user.uid);
      res.status(200).send(user);
      return;
    })
    .catch(e => {
      res.status(500).send({});
      console.log("Update user error: ", e);
    });
};

//init user location
const initUserLocation = (req, res) => {
  let user_location = req.body;
  console.log("Body user location", user_location);
  user_location.time_stamp = admin.firestore.FieldValue.serverTimestamp();
  return db
    .collection("user_location")
    .doc(user_location.uid)
    .update(user_location)
    .then(() => {
      res.status(200).send(user_location);
      return;
    })
    .catch(e => {
      res.status(400).send({});
      console.log("Init location e: ", e);
    });
};

const updateStatus = (req, res) => {
  const status = req.body.status;
  const uid = req.body.uid;
  console.log("update status", req.body);
  return db
    .collection("user_location")
    .doc(uid) /*  */
    .update({
      status,
      time_stamp: admin.firestore.FieldValue.serverTimestamp()
    })
    .then(() => {
      console.log("Update status user " + uid + " to " + status);
      res.status(200).send(true);
      return;
    })
    .catch(e => {
      res.status(500).send(false);
      console.log("Update user error: ", e);
    });
};

// /* Create a new trip */
const createTrip = (req, res) => {
  let data = req.body;
  let code = data.code;
  const addTrip = db
    .collection("trips")
    .doc(code)
    .set(data)
    .then(() => {
      return;
    })
    .catch(err => {
      console.log("err", err);
    });
  const addActiveTrip = db
    .collection("users")
    .doc(data.creator_id)
    .update({
      active_trip: data.code,
      my_trips: admin.firestore.FieldValue.arrayUnion(data.code)
    })
    .then(() => {
      return;
    })
    .catch(err => {
      console.log("err", err);
    });
  return Promise.all([addTrip, addActiveTrip])
    .then(() => {
      console.log("Created trip with code: ", data.code);
      res.status(200).send(data);
      return;
    })
    .catch(err => {
      console.log("all err", err);
    });
};

// get my trips
const getMyTrips = (req, res) => {
  const uid = req.body.uid;
  let promises = [];
  let usersRef = db.collection("users").doc(uid);
  return usersRef
    .get()
    .then(doc => {
      if (doc.exists) {
        const user = doc.data();
        let myTrips = [];
        if (user.my_trips) {
          myTrips = user.my_trips;
          myTrips.forEach(tripCode => {
            console.log("object", tripCode);
            const p = db
              .collection("trips")
              .doc(tripCode)
              .get();
            promises.push(p);
          });
        }
      } else {
        console.log("No such doc");
      }
      return Promise.all(promises);
    })
    .then(tripSnapshots => {
      let results = [];
      tripSnapshots.forEach(tripSnap => {
        if (tripSnap.exists) {
          results.push(tripSnap.data());
        }
      });
      console.log("My trips", results);
      res.status(200).send(results);
      console.log("No such trip doc");
      return;
    })
    .catch(e => {
      console.log("Error when get my trips ", e);
    });
};

//get list members
const getListMember = (req, res) => {
  const code = req.body.active_trip;
  let tripsRef = db.collection("trips").doc(code);
  let promises = [];
  return tripsRef
    .get()
    .then(doc => {
      if (doc.exists) {
        const members = doc.data().members;
        members.forEach(uid => {
          const p = db
            .collection("user_location")
            .doc(uid)
            .get();
          promises.push(p);
        });
      } else {
        console.log("No member");
      }
      return Promise.all(promises);
    })
    .then(userSnapshots => {
      let results = [];
      userSnapshots.forEach(userSnap => {
        if (userSnap.exists) {
          results.push(userSnap.data());
        } else {
          console.log("No such document!");
        }
      });
      console.log("res", results);
      res.status(200).send(results);
      return;
    })
    .catch(err => {
      console.log("get list members err", err);
    });
};

//get trip by code
const getTrip = (req, res) => {
  const code = req.body.active_trip;
  console.log(req.body);
  return db
    .collection("trips")
    .doc(code)
    .get()
    .then(doc => {
      if (doc.exists) {
        const data = doc.data();
        res.status(200).send(data);
      } else {
        console.log("No such doccument");
        res.status(404).send({});
      }
      return;
    })
    .catch(e => {
      console.log(e);
    });
};

///* Update location */
const updateLocation = (req, res) => {
  let data = req.body;
  console.log("data in update location, ", data);
  let promises = [];
  /* active_trip: 'as',
  location: { lat: 20.963739341813092, lng: 105.76690766775546 },
  photo_url: 'https://graph.facebook.com/1991011394332580/picture?height=200',
  uid: 'GbrbYtIHWcN9fyszrFUkj45qvEz2' */
  let userLocationRef = db.collection("user_location").doc(data.user.uid);
  let tripsRef = db.collection("trips").doc(data.user.active_trip);
  return userLocationRef
    .update({
      location: data.location
    })
    .then(() => {
      tripsRef
        .get()
        .then(doc => {
          if (doc.exists) {
            const members = doc.data().members;
            members.forEach(uid => {
              const p = db
                .collection("user_location")
                .doc(uid)
                .get();
              promises.push(p);
            });
          } else {
            console.log("No member");
          }
          return Promise.all(promises);
        })
        .then(userSnapshots => {
          let results = [];
          userSnapshots.forEach(userSnap => {
            if (userSnap.exists) {
              results.push(userSnap.data());
            } else {
              console.log("No such document!");
            }
          });
          res.status(200).send(results);
          return;
        })
        .catch(err => {
          console.log("get list members err", err);
        });
      return;
    })
    .catch(err => {
      console.log("err", err);
    });
};

// /* Join trip */
const joinTrip = (req, res) => {
  const user = req.body;
  const code = user.active_trip;
  let tripsRef = db.collection("trips").doc(code);
  let userRef = db.collection("users").doc(user.uid);
  return tripsRef
    .update({
      members: admin.firestore.FieldValue.arrayUnion(user.uid)
    })
    .then(() => {
      console.log("added member");
      userRef
        .update({
          active_trip: code,
          my_trips: admin.firestore.FieldValue.arrayUnion(code)
        })
        .then(snapshot => {
          return user;
        })
        .catch(err => {
          console.log("err", err);
          res.status(404).send(false);
        });
      res.status(200).send(true);
      return;
    })
    .catch(err => {
      res.status(404).send(false);
      console.log("err", err);
    });
};

const switchTrip = (req, res) => {
  const user = req.body;
  const active_trip = user.active_trip;
  const uid = user.uid;
  const switchInUserLocation = db
    .collection("user_location")
    .doc(uid)
    .update({
      "user.active_trip": active_trip
    });
  const switchInUser = db
    .collection("users")
    .doc(uid)
    .update({
      active_trip
    });
  return Promise.all([switchInUser, switchInUserLocation])
    .then(() => {
      console.log("Switch to " + active_trip);
      res.status(200).send(user);
      return;
    })
    .catch(e => {
      res.status(404).send({});
      console.log("err" + e);
    });
};

const addEventToUserLocation = (req, res) => {
  const userLocation = req.body;
  let new_event_statuses = [];
  const { uid, event_statuses } = userLocation;
  const eventStatus = event_statuses[0];
  const userLocationRef = db.collection("user_location").doc(uid);
  return userLocationRef.get().then(doc => {
    if (doc.event_statuses && doc.event_statuses.length > 0) {
      new_event_statuses = doc.event_statuses;
      let isExisted = false;
      new_event_statuses.forEach(e => {
        if (e.trip_id === eventStatus.trip_id) {
          e.status = eventStatus.status;
          isExisted = true;
        }
      });
      if (isExisted === false) {
        new_event_statuses.push(eventStatus);
      }
    } else {
      new_event_statuses.push(eventStatus);
    }
    return userLocationRef
      .update({
        event_statuses: new_event_statuses
      })
      .then(() => {
        res.status(200).send(true);
        return;
      })
      .catch(e => {
        console.log("add coming event to user error ", e);
        res.status(404).send(false);
      });
  });
};

const onWriteEvent = (change, context) => {
  console.log(context.params);
  console.log(change.after.data());
  const dataChanged = change.after.data();
  const active_trip = dataChanged.user.active_trip;
  let topic = active_trip;
  let payload = {
    data: {
      user: JSON.stringify(dataChanged.user),
      event: JSON.stringify(dataChanged)
    }
  };
  return admin
    .messaging()
    .sendToTopic(topic, payload)
    .then(response => {
      console.log("Send ok", response);
      return;
    })
    .catch(e => {
      console.log(e);
    });
};

const createEvent = (req, res) => {
  let event = req.body;
  event.time_stamp = admin.firestore.FieldValue.serverTimestamp();
  return db
    .collection("events")
    .add(event)
    .then(docRef => {
      console.log("Created event ", docRef.id);
      event.event_id = docRef.id;
      res.status(200).send(event);
      return;
    })
    .catch(e => {
      res.status(404).send({});
      console.log(e);
    });
};

const updateEvent = (req, res) => {
  const event = req.body;
  return db
    .collection("events")
    .doc(event.event_id)
    .update(event)
    .then(() => {
      console.log("Updated events");
      res.status(200).send(event);
      return;
    })
    .catch(e => {
      console.log("Update event error: ", e);
      res.status(404).send({});
    });
};

const outTrip = (req, res) => {
  let userLocation = req.body;
  let user = userLocation.user;
  let { uid, active_trip, name: selectedTrip } = user;
  if (active_trip === selectedTrip) {
    active_trip = "";
  }
  let eventPromise;
  if (userLocation.event_statuses && userLocation.event_statuses.length > 0) {
    userLocation.event_statuses.forEach(e => {
      if (e.trip_id === selectedTrip) {
        eventPromise = db
          .collection("events")
          .doc(e.event.event_id)
          .update({
            coming_users: admin.firestore.FieldValue.arrayRemove(uid),
            waiting_users: admin.firestore.FieldValue.arrayRemove(uid)
          });
      }
    });
  }

  const removeMyTrip = db
    .collection("users")
    .doc(uid)
    .update({
      active_trip,
      my_trips: admin.firestore.FieldValue.arrayRemove(selectedTrip)
    })
    .then(() => {
      console.log("Updated my trips");
      return;
    })
    .catch(e => {
      console.log("Remove my trips", e);
    });

  let new_event_statuses = [];
  const updateUserLocationPromise = db
    .collection("user_location")
    .doc(uid)
    .get()
    .then(doc => {
      if (doc.event_statuses) {
        new_event_statuses = doc.event_statuses;
        if (new_event_statuses.length > 0) {
          let isExisted = false;
          let removeIndex = 0;
          for (let index = 0; index < new_event_statuses.length; index++) {
            const e = event_statuses[index];
            if (e.trip_id === selectedTrip) {
              isExisted = true;
              removeIndex = index;
            }
          }
          if (isExisted === true) {
            new_event_statuses.splice(removeIndex, 1);
          }
        }
      }
      return db
        .collection("user_location")
        .doc(uid)
        .update({
          event_statuses: new_event_statuses,
          user: {
            active_trip
          }
        })
        .then(() => {
          return;
        })
        .catch(e => {
          console.log("add coming event to user error ", e);
        });
    });

  const updateThisTrip = db
    .collection("trips")
    .doc(selectedTrip)
    .update({
      members: admin.firestore.FieldValue.arrayRemove(uid)
    })
    .then(() => {
      console.log("Updated this trip");
      return;
    })
    .catch(e => {
      console.log("update this trip err", e);
    });

  return Promise.all([
    removeMyTrip,
    updateThisTrip,
    updateUserLocationPromise,
    eventPromise
  ])
    .then(() => {
      console.log(uid, " out ", selectedTrip);
      res.status(200).send(true);
      return;
    })
    .catch(e => {
      console.log("update this trip err", e);
      res.status(404).send(false);
    });
};

const updateTrip = (req, res) => {
  const trip = req.body;
  return db
    .collection("trips")
    .doc(trip.code)
    .update(trip)
    .then(() => {
      console.log("Update trip to " + trip.name);
      res.status(200).send(trip);
      return;
    })
    .catch(e => {
      console.log(e);
      res.status(404).send({});
    });
};

const getAllEvent = (req, res) => {
  const user = req.body;
  const { active_trip, uid } = user;
  let results = [];
  return db
    .collection("events")
    .where("trip_id", "==", active_trip)
    .where("user_id", "==", uid)
    .get()
    .then(userSnap => {
      if (userSnap.empty) {
        console.log("No matching documents.");
        return;
      }

      userSnap.forEach(doc => {
        console.log(doc.id, "=>", doc.data());
        results.push(doc.data());
      });
      res.status(200).send(results);
      return;
    })
    .catch(e => {
      console.log(e);
      res.status(404).send({});
    });
};

module.exports = {
  authOnCreate: functions.auth.user().onCreate(createProfile),
  createTrip: functions.https.onRequest(createTrip),
  updateLocation: functions.https.onRequest(updateLocation),
  initUserLocation: functions.https.onRequest(initUserLocation),
  joinTrip: functions.https.onRequest(joinTrip),
  getMyTrips: functions.https.onRequest(getMyTrips),
  getTrip: functions.https.onRequest(getTrip),
  updateUser: functions.https.onRequest(updateUser),
  updateStatus: functions.https.onRequest(updateStatus),
  getListMember: functions.https.onRequest(getListMember),
  switchTrip: functions.https.onRequest(switchTrip),
  createEvent: functions.https.onRequest(createEvent),
  updateTrip: functions.https.onRequest(updateTrip),
  updateEvent: functions.https.onRequest(updateEvent),
  addEventToUserLocation: functions.https.onRequest(addEventToUserLocation),
  outTrip: functions.https.onRequest(outTrip),
  getAllEvent: functions.https.onRequest(getAllEvent),
  onWriteEvent: functions.firestore
    .document("events/{eventId}")
    .onWrite(onWriteEvent)
};
