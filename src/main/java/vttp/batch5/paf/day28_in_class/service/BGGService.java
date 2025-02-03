package vttp.batch5.paf.day28_in_class.service;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.batch5.paf.day28_in_class.repo.BGGRepo;

@Service
public class BGGService {

    @Autowired
    private BGGRepo bggRepo;

    public List<Document> findGamesByName(String name) {
        return bggRepo.findGamesByName(name);
    }

    public List<Document> groupCommentsByUser() {
        return bggRepo.groupCommentsByUser();
    }

    public List<Document> getUserReviews(Integer limit, String username) {
        return bggRepo.getUserReviews(limit, username);
    }
}
