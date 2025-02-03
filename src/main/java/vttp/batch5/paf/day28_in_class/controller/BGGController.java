package vttp.batch5.paf.day28_in_class.controller;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vttp.batch5.paf.day28_in_class.service.BGGService;

@RestController
public class BGGController {

    @Autowired
    private BGGService bggService;

    @GetMapping("/api/comments/{username}")
    public ResponseEntity<?> getReviewsByUser(@PathVariable String username,
            @RequestParam(required = false, defaultValue = "3") Integer limit) {
        List<Document> userReviews = bggService.getUserReviews(limit, username);

        if (userReviews.size() <= 0) {
            return ResponseEntity.status(404).body("Results not found");
        }

        return ResponseEntity.ok(userReviews);
    }
}
