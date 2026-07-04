package com.mooncowpines.KinoStats.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mooncowpines.KinoStats.DTO.LogRequestDTO;
import com.mooncowpines.KinoStats.Service.LogService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("api/v1/logs")
public class LogController {
    
    @Autowired
    private LogService logService;

    @GetMapping("/all/{id}")
    public ResponseEntity<?> getByUserId(@PathVariable Long id){
        return ResponseEntity.ok(logService.getLogsByUserId(id));
    }

    @GetMapping("/log/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return logService.getLogById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addLog(@RequestBody LogRequestDTO request){
        logService.addLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLog(@PathVariable Long id, @RequestBody LogRequestDTO request) {
        logService.updateLog(id, request);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable Long id){
        logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }

    // @PostMapping("/bulk")
    // public ResponseEntity<Void> createBulk(@RequestBody List<LogRequestDTO> logs) {
    //     logs.forEach(logService::addLog);
    //     return ResponseEntity.ok().build();
    // }
}
