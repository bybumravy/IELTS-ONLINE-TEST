package web.ielts.Tips.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import web.ielts.Test.model.Test;

public interface TipsRepository extends MongoRepository<Test, String>, PagingAndSortingRepository<Test, String> {

}