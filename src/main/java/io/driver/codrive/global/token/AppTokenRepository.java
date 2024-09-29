package io.driver.codrive.global.token;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppTokenRepository extends CrudRepository<AppToken, String> {
}