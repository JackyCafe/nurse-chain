package com.blockchain.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blockchain.entity.NurseInfo;
import com.blockchain.service.NurseInfoService;

@RestController
@RequestMapping("/api/nurseinfo")
public class NurseInfoController {

    @Autowired
    private NurseInfoService service;

    // POST /api/nurseinfo
    // 創建新的護士資訊
    @PostMapping
    public ResponseEntity<NurseInfo> createNurseInfo(@RequestBody NurseInfo nurseInfo) {
        // 為了簡單起見，這裡直接使用 NurseInfo 實體作為請求體。
        // 在實際應用中，通常會使用 DTO (Data Transfer Object) 來接收請求資料。
        NurseInfo createdNurse = service.increaseNurseInfo(
                nurseInfo.getIdentyNo(),
                nurseInfo.getUser(),
                nurseInfo.getName(),
                nurseInfo.getPasswd() // 注意：密碼通常不應該直接這樣傳遞和儲存
        );
        return new ResponseEntity<>(createdNurse, HttpStatus.CREATED); // 返回 201 Created
    }

    // GET /api/nurseinfo
    // 獲取所有護士資訊
    @GetMapping
    public ResponseEntity<List<NurseInfo>> getAllNurseInfo() {
        List<NurseInfo> nurses = service.getAll();
        return new ResponseEntity<>(nurses, HttpStatus.OK); // 返回 200 OK
    }

    // GET /api/nurseinfo/{id}
    // 根據 ID 獲取護士資訊
    @GetMapping("/{id}")
    public ResponseEntity<NurseInfo> getNurseInfoById(@PathVariable("id") long id) {
        Optional<NurseInfo> nurse = service.getById(id);
        return nurse.map(value -> new ResponseEntity<>(value, HttpStatus.OK)) // 如果找到，返回 200 OK
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); // 如果沒找到，返回 404 Not Found
    }

    // PUT /api/nurseinfo/{id}
    // 更新護士資訊 (完整的替換式更新)
    @PutMapping("/{id}")
    public ResponseEntity<NurseInfo> updateNurseInfo(@PathVariable("id") long id, @RequestBody NurseInfo nurseInfo) {
        // 你在 Service 中提供的 update 方法，如果找不到會返回 null
        NurseInfo updatedNurse = service.update(id, nurseInfo);
        if (updatedNurse != null) {
            return new ResponseEntity<>(updatedNurse, HttpStatus.OK); // 返回 200 OK
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果沒找到，返回 404 Not Found
    }

    // DELETE /api/nurseinfo/{id}
    // 根據 ID 刪除護士資訊
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteNurseInfo(@PathVariable("id") long id) {
        try {
            service.deleteById(id); // 調用 Service 中的刪除方法
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 返回 204 No Content
        } catch (Exception e) {
            // 這裡可以根據異常類型返回不同的 HTTP 狀態碼
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // 返回 500 Internal Server Error
        }
    }

}
