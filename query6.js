// Query 6
// Find the average friend count per user.
// Return a decimal value as the average user friend count of all users in the users collection.

function find_average_friendcount(dbname) {
    db = db.getSiblingDB(dbname);

    let result = db.users.aggregate([
        {
            $project: {
                _id: 0,
                friendCount: {
                    $size: {
                        $ifNull: ["$friends", []]
                    }
                }
            }
        },
        {
            $group: {
                _id: null,
                averageFriends: {
                    $avg: "$friendCount"
                }
            }
        }
    ]).toArray();

    if (result.length > 0) {
        return result[0].averageFriends;
    } else {
        return 0;
    }
}
