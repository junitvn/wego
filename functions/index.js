const functions = require("firebase-functions");
const admin = require("firebase-admin");
const _ = require("lodash");
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
  const addGroupChat = db
    .collection("groups")
    .doc(code)
    .set({
      trip_id: code,
      members: data.members,
      name: data.name
    });
  return Promise.all([addTrip, addActiveTrip, addGroupChat])
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
  let promises = [];
  let trip = {};
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
            trip = doc.data();
            const members = trip.members;
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
          if (results.length > 1) {
            calculateDistance(trip, results);
          }
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

const calculateDistance = (trip, userLocations) => {
  const { start_point, trip_setting, code, last_time_send_notification } = trip;
  let results = [];
  userLocations.forEach(userLocation => {
    userLocation.distance = getDistance(
      start_point.location,
      userLocation.location
    );
    results.push(userLocation);
  });
  results.sort((a, b) => {
    return a.distance - b.distance;
  });
  const minDistance = results[1].distance - results[0].distance;
  const lastMember = results[0];
  const topic = "DI" + code;
  const payload = {
    data: {
      last_member: JSON.stringify(lastMember)
    }
  };

  let time_stamp = new Date().getTime();
  const timeToRepeat = trip_setting.time_to_repeat * 60000;
  const isTimeOver = last_time_send_notification
    ? time_stamp - last_time_send_notification > timeToRepeat
    : true;
  if (
    minDistance > trip_setting.min_distance &&
    trip_setting.receive_notification &&
    isTimeOver
  ) {
    const updateLastTimeSendNotification = db
      .collection("trips")
      .doc(code)
      .update({
        last_time_send_notification: time_stamp
      });
    const sendNotification = admin
      .messaging()
      .sendToTopic(topic, payload)
      .then(response => {
        console.log("Send message to ", topic);
        return;
      })
      .catch(e => {
        console.log(e);
      });
    return Promise.all([sendNotification, updateLastTimeSendNotification])
      .then(() => {
        console.log("Send noti and update time");
        return;
      })
      .catch(e => {
        console.log(e);
      });
  } else return;
};

const rad = x => {
  return (x * Math.PI) / 180;
};

const getDistance = (p1, p2) => {
  let R = 6378137; // Earth’s mean radius in meter
  let dLat = rad(p2.lat - p1.lat);
  let dLong = rad(p2.lng - p1.lng);
  let a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(rad(p1.lat)) *
      Math.cos(rad(p2.lat)) *
      Math.sin(dLong / 2) *
      Math.sin(dLong / 2);
  let c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  let d = R * c;
  return d; // returns the distance in meter
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

const onWriteInvitation = (change, context) => {
  const dataChanged = change.after.data();
  if (dataChanged.status === "invited") {
    const topic = "IN" + dataChanged.receiver_id;
    const payload = {
      data: {
        invitation: JSON.stringify(dataChanged)
      }
    };
    return admin
      .messaging()
      .sendToTopic(topic, payload)
      .then(response => {
        return;
      })
      .catch(e => {
        console.log(e);
      });
  } else {
    rteturn;
  }
};

const onWriteEvent = (change, context) => {
  let diffComing, diffComingReverse, diffWaiting, diffWaitingReverse;
  let difference = "";
  if (change.before.data()) {
    diffComing = _.differenceBy(
      change.before.data().coming_users,
      change.after.data().coming_users
    );
    diffComingReverse = _.differenceBy(
      change.after.data().coming_users,
      change.before.data().coming_users
    );

    diffWaiting = _.differenceBy(
      change.before.data().waiting_users,
      change.after.data().waiting_users
    );
    diffWaitingReverse = _.differenceBy(
      change.after.data().waiting_users,
      change.before.data().waiting_users
    );
    const diff = getDiff(
      diffComing,
      diffComingReverse,
      diffWaiting,
      diffWaitingReverse
    );
    if (diff.length !== 0) {
      difference = diff[0];
    }
  }

  const dataChanged = change.after.data();
  const active_trip = dataChanged.user.active_trip;
  let topic = active_trip;
  let payload = {
    data: {
      user: JSON.stringify(dataChanged.user),
      event: JSON.stringify(dataChanged),
      difference
    }
  };
  return admin
    .messaging()
    .sendToTopic(topic, payload)
    .then(response => {
      return;
    })
    .catch(e => {
      console.log(e);
    });
};
const getDiff = (dif1, dif2, dif3, dif4) => {
  let diff = [];
  if (dif1.length !== 0) {
    diff = dif1;
    return diff;
  }
  if (dif2.length !== 0) {
    diff = dif2;
    return diff;
  }
  if (dif3.length !== 0) {
    diff = dif3;
    return diff;
  }
  if (dif4.length !== 0) {
    diff = dif4;
    return diff;
  }
  return diff;
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

const getGroupByUser = (req, res) => {
  const user = req.body;
  const { my_trips } = user;
  let promises = [];
  my_trips.forEach(tripId => {
    const p = db
      .collection("groups")
      .doc(tripId)
      .get();
    promises.push(p);
  });
  return Promise.all(promises)
    .then(groupSnaphots => {
      let results = [];
      groupSnaphots.forEach(groupSnap => {
        if (groupSnap.exists) {
          results.push(groupSnap.data());
        } else {
          console.log("No such document!");
          res.status(404).send([]);
        }
      });
      res.status(200).send(results);
      return;
    })
    .catch(e => {
      console.log("Get groups fail ", e);
    });
};

const sendGroupMessage = (req, res) => {
  let message = req.body;
  message.time_stamp = admin.firestore.FieldValue.serverTimestamp();
  const changeLastMessage = db
    .collection("groups")
    .doc(message.group_id)
    .update({
      last_message: message
    });

  const addGroupMessage = db.collection("group_message").add(message);

  const topic = "GM" + message.group_id;
  const payload = {
    data: {
      message: JSON.stringify(message)
    }
  };
  const sendNotification = admin
    .messaging()
    .sendToTopic(topic, payload)
    .then(response => {
      console.log("Send message to ", topic);
      return;
    })
    .catch(e => {
      console.log(e);
    });

  return Promise.all([changeLastMessage, addGroupMessage, sendNotification])
    .then(() => {
      res.status(200).send(true);
      return;
    })
    .catch(e => {
      console.log("Send group message fail: ", e);
    });
};

const sendUserMessage = (req, res) => {
  let message = req.body;
  message.time_stamp = admin.firestore.FieldValue.serverTimestamp();
  const changeLastMessage = db
    .collection("user_channel")
    .doc(message.channel_id)
    .update({
      last_message: message
    });

  const addGroupMessage = db.collection("user_message").add(message);

  const topic = "UM" + message.channel_id;

  const payload = {
    data: {
      message: JSON.stringify(message)
    }
  };

  const sendNotification = admin
    .messaging()
    .sendToTopic(topic, payload)
    .then(response => {
      console.log("Send message to ", topic);
      return;
    })
    .catch(e => {
      console.log(e);
    });

  return Promise.all([changeLastMessage, addGroupMessage, sendNotification])
    .then(() => {
      res.status(200).send(true);
      return;
    })
    .catch(e => {
      console.log("Send user message fail: ", e);
    });
};

const createUserChannel = (req, res) => {
  let requestChannel = req.body;
  const { member_uid, members, user_id } = requestChannel;
  let other_id;
  if (member_uid[0] === user_id) {
    other_id = member_uid[1];
  } else {
    other_id = member_uid[0];
  }
  return db
    .collection("user_channel")
    .where("member_uid", "array-contains", user_id)
    .get()
    .then(snapshot => {
      if (snapshot.empty) {
        console.log("No matching documents.");
        //Create and return new
        db.collection("user_channel")
          .add(requestChannel)
          .then(ref => {
            requestChannel.channel_id = ref.id;
            res.status(200).send(requestChannel);
            return;
          })
          .catch(e => {
            console.log("Create channel FAIL, ", e);
          });
        return;
      }
      let isExistedChannel = false;
      let existedChannel = {};
      snapshot.forEach(doc => {
        console.log(doc.id, "=>", doc.data());
        if (_.indexOf(member_uid, other_id) !== -1) {
          isExistedChannel = true;
          existedChannel = doc.data();
          existedChannel.channel_id = doc.id;
        }
      });
      if (isExistedChannel) {
        res.status(200).send(existedChannel);
        return;
      } else {
        db.collection("user_channel")
          .add(requestChannel)
          .then(ref => {
            requestChannel.channel_id = ref.id;
            res.status(200).send(requestChannel);
            return;
          })
          .catch(e => {
            console.log("Create channel FAIL, ", e);
          });
      }
      return;
    })
    .catch(err => {
      res.status(404).send({});
      console.log("Error getting documents", err);
    });
};

const searchUserByName = (req, res) => {
  const query = _.trim(req.body.name);
  const id = req.body.uid;
  let results = [];
  return db
    .collection("users")
    .get()
    .then(snapshot => {
      if (snapshot.empty) {
        console.log("No matching documents.");
        return;
      }
      snapshot.forEach(doc => {
        console.log(doc.id, "=>", doc.data());
        const user = doc.data();
        const userName = _.toLower(user.name);
        if (
          _.includes(convertString(userName), convertString(query)) &&
          user.uid !== id
        ) {
          results.push(user);
        }
      });
      res.status(200).send(results);
      return;
    })
    .catch(err => {
      console.log("Error getting documents", err);
    });
};

const inviteFriend = (req, res) => {
  const invitation = req.body;
  return db
    .collection("invitations")
    .add(invitation)
    .then(() => {
      res.status(200).send(true);
      return;
    })
    .catch(e => {
      console.log(e);
    });
};

function convertString(str) {
  str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
  str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
  str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
  str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
  str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
  str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
  str = str.replace(/đ/g, "d");
  return str;
}

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
  getGroupByUser: functions.https.onRequest(getGroupByUser),
  sendGroupMessage: functions.https.onRequest(sendGroupMessage),
  sendUserMessage: functions.https.onRequest(sendUserMessage),
  createUserChannel: functions.https.onRequest(createUserChannel),
  searchUserByName: functions.https.onRequest(searchUserByName),
  getAllEvent: functions.https.onRequest(getAllEvent),
  inviteFriend: functions.https.onRequest(inviteFriend),
  onWriteEvent: functions.firestore
    .document("events/{eventId}")
    .onWrite(onWriteEvent),
  onWriteInvitation: functions.firestore
    .document("invitations/{invitationId}")
    .onWrite(onWriteInvitation)
};
