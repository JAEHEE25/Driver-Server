package io.driver.codrive.modules.codeblock.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeblockRepository extends JpaRepository<Codeblock, Long> {
}
