package com.blockchain.service; // 建議放在 service 包下

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.blockchain.dto.NurseCertificationRequestDTO;
import com.blockchain.dto.NurseCertificationResponseDTO;
import com.blockchain.entity.NurseCertifications;
import com.blockchain.entity.NurseInfo;
import com.blockchain.entity.Subject;
import com.blockchain.repository.NurseCertificationRepository;
import com.blockchain.repository.NurseInfoRepository; // 假設您有這個 Repository
import com.blockchain.repository.SubjectRepository; // 假設您有這個 Repository

@Service
public class NurseCertificationService {

    private final NurseCertificationRepository nurseCertificationRepository;
    private final NurseInfoRepository nurseInfoRepository; // 注入 NurseInfoRepository
    private final SubjectRepository subjectRepository; // 注入 SubjectRepository

    @Autowired
    public NurseCertificationService(NurseCertificationRepository nurseCertificationRepository,
            NurseInfoRepository nurseInfoRepository,
            SubjectRepository subjectRepository) {
        this.nurseCertificationRepository = nurseCertificationRepository;
        this.nurseInfoRepository = nurseInfoRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional
    public NurseCertificationResponseDTO createNurseCertification(NurseCertificationRequestDTO requestDTO) {
        // 1. 查找關聯的 NurseInfo 和 Subject 實體
        NurseInfo nurseInfo = nurseInfoRepository.findById(requestDTO.getNurseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nurse not found with ID: " + requestDTO.getNurseId()));

        Subject subject = subjectRepository.findById(requestDTO.getSubjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Subject not found with ID: " + requestDTO.getSubjectId()));
        // 2. 創建 NurseCertifications 實體
        NurseCertifications nurseCertification = new NurseCertifications();
        nurseCertification.setNurseInfo(nurseInfo);
        nurseCertification.setSubject(subject);
        nurseCertification.setStartTime(requestDTO.getStartTime());
        nurseCertification.setEndTime(requestDTO.getEndTime());
        nurseCertification.setPoints(requestDTO.getPoints());

        // 3. 保存到資料庫
        NurseCertifications savedCertification = nurseCertificationRepository.save(nurseCertification);

        // 4. 將實體轉換為 DTO 並返回
        return convertToDto(savedCertification);
    }

    @Transactional(readOnly = true)
    public List<NurseCertificationResponseDTO> getAllNurseCertifications() {
        return nurseCertificationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public NurseCertificationResponseDTO getNurseCertificationById(Long id) {
        return nurseCertificationRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nurse Certification not found with ID: " + id));
    }

    @Transactional
    public NurseCertificationResponseDTO updateNurseCertification(Long id, NurseCertificationRequestDTO requestDTO) {
        NurseCertifications existingCertification = nurseCertificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nurse Certification not found with ID: " + id));

        // 更新關聯實體（如果需要）
        if (!existingCertification.getNurseInfo().getId().equals(requestDTO.getNurseId())) {
            NurseInfo newNurseInfo = nurseInfoRepository.findById(requestDTO.getNurseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Nurse not found with ID: " + requestDTO.getNurseId()));
            existingCertification.setNurseInfo(newNurseInfo);
        }
        if (!existingCertification.getSubject().getId().equals(requestDTO.getSubjectId())) {
            Subject newSubject = subjectRepository.findById(requestDTO.getSubjectId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Subject not found with ID: " + requestDTO.getSubjectId()));
            existingCertification.setSubject(newSubject);
        }

        // 更新其他屬性
        existingCertification.setStartTime(requestDTO.getStartTime());
        existingCertification.setEndTime(requestDTO.getEndTime());
        existingCertification.setPoints(requestDTO.getPoints());

        NurseCertifications updatedCertification = nurseCertificationRepository.save(existingCertification);
        return convertToDto(updatedCertification);
    }

    @Transactional
    public void deleteNurseCertification(Long id) {
        if (!nurseCertificationRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nurse Certification not found with ID: " + id);
        }
        nurseCertificationRepository.deleteById(id);
    }

    // 將實體轉換為 DTO 的輔助方法
    private NurseCertificationResponseDTO convertToDto(NurseCertifications entity) {
        String nurseName = Optional.ofNullable(entity.getNurseInfo())
                .map(NurseInfo::getName)
                .orElse("Unknown Nurse");
        String category = Optional.ofNullable(entity.getSubject())
                .map(Subject::getCategory)
                .orElse("Unknown Subject");
        String subjectName = Optional.ofNullable(entity.getSubject())
                .map(Subject::getSubjectName)
                .orElse("Unknown Subject");

        return new NurseCertificationResponseDTO(
                entity.getId(),
                entity.getNurseInfo().getId(),
                nurseName,
                entity.getSubject().getId(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getPoints(),
                category,
                subjectName);
    }
}