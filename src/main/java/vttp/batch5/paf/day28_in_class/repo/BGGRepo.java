package vttp.batch5.paf.day28_in_class.repo;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation.ProjectionOperationBuilder;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;

@Repository
public class BGGRepo {

    @Autowired
    private MongoTemplate template;

    // db.games.aggregate([
    // { $match: { name: { $regex: name, $options: 'i' } } },
    // { $project: { name: 1, ranking: 1, image: 1, _id: 0 } },
    // { $sort: { ranking: -1 } },
    // { $limit: 3 }
    // ])

    public List<Document> findGamesByName(String name) {

        // create the aggregation stage
        Criteria criteria = Criteria.where("name").regex(name, "i");
        MatchOperation matchName = Aggregation.match(criteria);

        // project wanted attributes
        ProjectionOperation projectFields = Aggregation.project("name", "ranking", "image");

        // sort by ranking
        SortOperation sortByRanking = Aggregation.sort(Sort.Direction.ASC, "ranking");

        // take top 3 results
        LimitOperation getTopThree = Aggregation.limit(3);

        // create the pipeline
        Aggregation pipeline = Aggregation.newAggregation(matchName, projectFields, sortByRanking, getTopThree);

        // Run the aggregation
        AggregationResults<Document> result = template.aggregate(pipeline, "games", Document.class);

        return result.getMappedResults();
    }

    // db.comments.aggregate([
    // {
    // $group: {
    // _id : '$user',
    // comments: {
    // $push:{
    // gid: '$gid',
    // text: '$c_text'
    // }
    // }
    // }
    // }
    // ])
    public List<Document> groupCommentsByUser() {

        GroupOperation groupByUser = Aggregation.group("user")
                .push(
                        new BasicDBObject()
                                .append("gid", "$gid")
                                .append("text", "$c_text"))
                .as("comments");

        LimitOperation take3 = Aggregation.limit(3);

        Aggregation pipeline = Aggregation.newAggregation(groupByUser, take3);

        return template.aggregate(pipeline, "comments", Document.class).getMappedResults();
    }

    // db.comments.aggregate([
    // {
    // $match: {user: 'Gamesrosco'}
    // },
    // {
    // $lookup: {
    // from: 'games',
    // foreignField: 'gid',
    // localField: 'gid',
    // as: 'game',
    // pipeline: [{$sort:{ratings:1}}]
    // }
    // },
    // {
    // $unwind: "$game"
    // },
    // {
    // $sort: {"rating" : -1}
    // },
    // {
    // $group: {
    // _id: "$user",
    // reviews: {$push: {
    // game : "$game.name" ,
    // comments: "$c_text" ,
    // ratings:"$rating" }}
    // }
    // },
    // {
    // $project: {
    // _id: 0,
    // user: "$_id",
    // reviews: {$slice: ["$reviews",3]}
    // }
    // }
    // ])
    public List<Document> getUserReviews(Integer limit, String username) {
        MatchOperation findReviewByUsername = Aggregation.match(Criteria.where("user").regex(username, "i"));
        LookupOperation joinGame = Aggregation.lookup("games", "gid", "gid", "game");
        UnwindOperation unwindGame = Aggregation.unwind("game");
        SortOperation sortByRating = Aggregation.sort(Sort.Direction.DESC, "rating");
        GroupOperation groupByUsername = Aggregation.group("user")
                .push(
                        new BasicDBObject()
                                .append("game", "$game.name") // Game name
                                .append("comment", "$c_text") // Comment
                                .append("rating", "$rating") // Rating
                ).as("reviews");
        ProjectionOperationBuilder project = Aggregation.project()
                .and("reviews").slice(limit);

        Aggregation pipeline = Aggregation.newAggregation(findReviewByUsername, joinGame, unwindGame, sortByRating,
                groupByUsername, project);
        AggregationResults<Document> results = template.aggregate(pipeline, "comments", Document.class);

        return results.getMappedResults();
    }
}
