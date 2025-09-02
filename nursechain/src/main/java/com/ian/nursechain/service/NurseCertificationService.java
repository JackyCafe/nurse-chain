package com.ian.nursechain.service; // 建議放在 service 包下

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ian.nursechain.dto.NurseCertificationRequestDTO;
import com.ian.nursechain.dto.NurseCertificationResponseDTO;
import com.ian.nursechain.entity.NurseCertifications;
import com.ian.nursechain.entity.NurseInfo;
import com.ian.nursechain.entity.Subject;
import com.ian.nursechain.repository.NurseCertificationRepository;
import com.ian.nursechain.repository.NurseInfoRepository;
import com.ian.nursechain.repository.SubjectRepository;

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
                return NurseCertificationResponseDTO.fromEntity(savedCertification);
        }

        @Transactional(readOnly = true)
        public List<NurseCertificationResponseDTO> getAllNurseCertifications() {
                return nurseCertificationRepository.findAll().stream()
                                .map(NurseCertificationResponseDTO::fromEntity)
                                .collect(Collectors.toList());
        }

        /**
     * 根據護士ID獲取其所有證照清單
     * @param nurseId 護士的ID
     * @return 該護士的所有證照清單DTO
     */
    @Transactional(readOnly = true)
    public List<NurseCertificationResponseDTO> getNurseCertificationsByNurseId(Long nurseId) {
        return nurseCertificationRepository.findByNurseInfoId(nurseId).stream()
                .map(NurseCertificationResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

        @Transactional(readOnly = true)
        public NurseCertificationResponseDTO getNurseCertificationById(Long id) {
                return nurseCertificationRepository.findById(id)
                                .map(NurseCertificationResponseDTO::fromEntity)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Nurse Certification not found with ID: " + id));
        }

        @Transactional
        public NurseCertificationResponseDTO updateNurseCertification(Long id,
                        NurseCertificationRequestDTO requestDTO) {
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
                return NurseCertificationResponseDTO.fromEntity(updatedCertification);
        }

        @Transactional
        public void deleteNurseCertification(Long id) {
                if (!nurseCertificationRepository.existsById(id)) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                        "Nurse Certification not found with ID: " + id);
                }
                nurseCertificationRepository.deleteById(id);
        }

}