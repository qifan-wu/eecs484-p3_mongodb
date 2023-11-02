// Query 5
// Find the oldest friend for each user who has a friend. For simplicity,
// use only year of birth to determine age, if there is a tie, use the
// one with smallest user_id. You may find query 2 and query 3 helpful.
// You can create selections if you want. Do not modify users collection.
// Return a javascript object : key is the user_id and the value is the oldest_friend id.
// You should return something like this (order does not matter):
// {user1:userx1, user2:userx2, user3:userx3,...}

function oldest_friend(dbname) {
    db = db.getSiblingDB(dbname);

    let results = {};

    db.createCollection("flat");
    db.users.aggregate([{$unwind: "$friends"}]).forEach(user => {db.flat.insert({"user_id": user.user_id, "friends": user.friends});
                                                              db.flat.insert({"user_id": user.friends, "friends": user.user_id})});

    db.flat.find().forEach(user => {
        if (!(user.user_id in results)){
            results[user.user_id] = user.friends;
        }
        db.users.find({"user_id": user.friends}).forEach(possible => {
            if (results[user.user_id] == possible.user_id){
                return;
            }
            db.users.find({"user_id": results[user.user_id]}).forEach(oldest => {
                if (possible.YOB == oldest.YOB) {
                    if(possible.user_id > oldest.user_id) {
                        results[user.user_id] = oldest.user_id;
                    }
                    else {
                        results[user.user_id] = possible.user_id;
                    }
                }
                else {
                    if (possible.YOB < oldest.YOB) {
                        results[user.user_id] = possible.user_id;
                    }
                    else {
                        results[user.user_id] = oldest.user_id;
                    }
                }
            })
        })
    })

    return results;
}
