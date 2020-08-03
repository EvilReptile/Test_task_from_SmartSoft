package cham.TestProjectFromSmartSoft.repo;

import cham.TestProjectFromSmartSoft.data.Users;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepo extends CrudRepository<Users, Long> {
    Users findByUsername(String username);
}
