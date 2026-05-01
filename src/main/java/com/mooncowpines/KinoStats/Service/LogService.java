package com.mooncowpines.KinoStats.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Repository.LogRepository;

@Service
public class LogService {
    
    @Autowired
    LogRepository logRepository;

    public List<Log> getLogs(){
        return logRepository.findAll();
    }

    public List<Log> getLogsByUserId(Long id){
        return logRepository.findByUserId(id);
    }

    public Optional<Log> getLogById(Long id){
        return logRepository.findById(id);
    }

    public void addLog(Log log){
        logRepository.save(log);
    }

    public void updateLog(Log log){
        logRepository.save(log);
    }

    public void deleteLogById(long id){
        logRepository.deleteById(id);
    }
}
