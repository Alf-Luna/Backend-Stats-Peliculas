package com.mooncowpines.KinoStats.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mooncowpines.KinoStats.DTO.LogRequestDTO;
import com.mooncowpines.KinoStats.Model.Log;
import com.mooncowpines.KinoStats.Service.LogService;
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
    public ResponseEntity<?> addLog(@RequestBody LogRequestDTO request){
        logService.addLog(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLog(@PathVariable Long id, @RequestBody LogRequestDTO request) {
        logService.updateLog(id, request);
        return ResponseEntity.ok(request);
    }
}
