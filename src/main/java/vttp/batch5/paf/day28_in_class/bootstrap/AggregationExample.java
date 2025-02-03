package vttp.batch5.paf.day28_in_class.bootstrap;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import vttp.batch5.paf.day28_in_class.repo.BGGRepo;

@Component
public class AggregationExample implements CommandLineRunner {

    @Autowired
    private BGGRepo bggRepo;

    @Override
    public void run(String... args) {

        List<Document> results = bggRepo.findGamesByName("carcassonne");
        System.out.println(results);
    }

}
