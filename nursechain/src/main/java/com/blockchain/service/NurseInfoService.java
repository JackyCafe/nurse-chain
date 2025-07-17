package com.blockchain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blockchain.entity.NurseInfo;
import com.blockchain.repository.NurseInfoRepository;

@Service
public class NurseInfoService {
    @Autowired
    NurseInfoRepository nurses;

    // Create
    public NurseInfo increaseNurseInfo(String identyNo, String user, String name, String passwd) {
        NurseInfo info = new NurseInfo(identyNo, user, name, passwd);
        return nurses.save(info);
    }

    // read
    public List<NurseInfo> getAll() {
        return nurses.findAll();
    }

    public Optional<NurseInfo> getById(long id) {
        return nurses.findById(id);
    }

    public NurseInfo update(long id, NurseInfo updateNurse) {
        Optional<NurseInfo> existingNurseOptional = nurses.findById(id);
        if (existingNurseOptional.isPresent()) {
            NurseInfo existingNurse = existingNurseOptional.get();
            existingNurse.setIdentyNo(updateNurse.getIdentyNo());
            existingNurse.setUser(updateNurse.getUser());
            existingNurse.setName(updateNurse.getName());
            return nurses.save(existingNurse); // 保存更新後的實體
        }
        return null;
    }

    public void deleteById(long id) {
        nurses.deleteById(id);
    }

}
