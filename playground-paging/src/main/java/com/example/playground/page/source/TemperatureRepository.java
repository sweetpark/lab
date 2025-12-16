package com.example.playground.page.source;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TemperatureRepository extends JpaRepository<Temperature, Long> {

    @Query(value = "SELECT t.* "
            + "FROM temperature t "
            + "ORDER BY t.update_time DESC "
            + "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Temperature> findByPage(@Param("limit") int limit,
                                 @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) "
            + "FROM ( "
            + "  SELECT t.* FROM temperature t "
            + "  ORDER BY t.update_time DESC "
            + "  LIMIT :limit OFFSET :offset "
            + ") AS page_result",
            nativeQuery = true)
    Integer rangePageCnt(@Param("limit") int limit,
                        @Param("offset") int offset);


    @Query(value= "SELECT COUNT(*) "
            + "FROM Temperature t")
    Integer getTotCnt();

    @Query(value = "SELECT * "
            +"FROM temperature t "
            +"WHERE  t.id > :lastIndex "
            +"ORDER BY t.id "
            +"LIMIT :limit",
            nativeQuery = true
        )
    List<Temperature> cursorPage(@Param("lastIndex") Long lastIndex,
                                 @Param("limit") Integer limit);
}

