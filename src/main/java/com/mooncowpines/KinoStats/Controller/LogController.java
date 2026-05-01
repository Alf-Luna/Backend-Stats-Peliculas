package com.mooncowpines.KinoStats.Controller;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Service.LogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/v1/logs")
public class LogController {
    
    @Autowired
    private LogService logService;

    @GetMapping("/all")
    public ResponseEntity<?> getByUserId(@RequestBody Long userId){
        return ResponseEntity.ok(logService.getLogsByUserId(userId));
    }

    @GetMapping("/log")
    public ResponseEntity<?> getById(@RequestBody Long id){
        Optional<Log> log = logService.getLogById(id);

        if (log.isPresent()){
            return ResponseEntity.ok()
                        .header("Header", "Values")
                        .body(log.get());
        }
        else {
            Map<String, String> errorBody = new HashMap<>();
            errorBody.put("message", "Registro no encontrado");
            errorBody.put("status", "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLog(@RequestBody Log log){
        URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(log.getId())
                        .toUri();

        return ResponseEntity.created(location).body(log);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateLog(@RequestBody Log log){
        Log newLog = new Log();
        newLog.setId(log.getId());
        newLog.setDate(log.getDate());
        newLog.setReview(log.getReview() != null ? log.getReview() : "");
        newLog.setRating(log.getRating());
        newLog.setMovieId(log.getMovieId());
        newLog.setUserId(log.getUserId());
        newLog.setFirstWatch(log.getFirstWatch());

        logService.addLog(newLog);
        return ResponseEntity.ok(newLog);
    }
}
