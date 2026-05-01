package com.mooncowpines.KinoStats.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mooncowpines.KinoStats.Model.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long>{
    List<Log> findByUserId(Long userId);
}
