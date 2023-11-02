// Query 4
// Find user pairs (A,B) that meet the following constraints:
// i) user A is male and user B is female
// ii) their Year_Of_Birth difference is less than year_diff
// iii) user A and B are not friends
// iv) user A and B are from the same hometown city
// The following is the schema for output pairs:
// [
//      [user_id1, user_id2],
//      [user_id1, user_id3],
//      [user_id4, user_id2],
//      ...
//  ]
// user_id is the field from the users collection. Do not use the _id field in users.
// Return an array of arrays.

function suggest_friends(year_diff, dbname) {
    db = db.getSiblingDB(dbname);

    let pairs = [];
    
    db.users.find().forEach(male => {
        if (male.gender == "male") {
            db.users.find().forEach(female => {
                if (female.gender == "female" && Math.abs(male.YOB - female.YOB) < year_diff && male.hometown.city == female.hometown.city) {
                    let pair = [male.user_id, female.user_id];
                    if (male.user_id < female.user_id) {
                        if (male.friends.indexOf(female.user_id) == -1) {
                            pairs.push(pair);
                        }
                    }
                    else if (female.user_id < male.user_id) {
                        if (female.friends.indexOf(male.user_id) == -1) {
                            pairs.push(pair);
                        }
                    }
                }
            });
        }
    });

    return pairs;
}
