// Query 8
// Find the city average friend count per user using MapReduce.

let city_average_friendcount_mapper = function () {
    // TODO: Implement the map function
    emit(this.hometown.city, {user_count: 1, friends_count: this.friends.length})
};

let city_average_friendcount_reducer = function (key, values) {
    // TODO: Implement the reduce function
    let reduceVal = {user_count: 0, friends_count: 0};
    for (var i = 0; i < values.length; i++) {
        reduceVal.user_count += values[i].user_count;
        reduceVal.friends_count += values[i].friends_count;
    }
    return reduceVal;
};

let city_average_friendcount_finalizer = function (key, reduceVal) {
    // We've implemented a simple forwarding finalize function. This implementation
    // is naive: it just forwards the reduceVal to the output collection.
    // TODO: Feel free to change it if needed.
    return reduceVal.friends_count / reduceVal.user_count;
};
