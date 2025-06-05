package web.ielts.Test.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import web.ielts.Test.model.Test;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface TestRepository extends MongoRepository<Test, String>, PagingAndSortingRepository<Test, String> {

}