// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

contract CertificationRegistry {
    struct Certification {
        uint256 nurseId;
        uint256 subjectId;
        string nurseName;
        string subjectName;
        uint256 points;
    }

    mapping(uint256 => Certification) public certifications;

    event CertificationAdded(
        uint256 indexed certificationId,
        uint256 nurseId,
        string nurseName
    );

    address private owner;

    constructor() {
        owner = msg.sender;
    }

    modifier onlyOwner() {
        require(msg.sender == owner, "Only the owner can perform this action");
        _;
    }

    function addCertification(
        uint256 _certificationId,
        uint256 _nurseId,
        uint256 _subjectId,
        string calldata _nurseName,
        string calldata _subjectName,
        uint256 _points
    ) external onlyOwner {
        require(certifications[_certificationId].nurseId == 0, "Certification already exists");

        certifications[_certificationId] = Certification({
            nurseId: _nurseId,
            subjectId: _subjectId,
            nurseName: _nurseName,
            subjectName: _subjectName,
            points: _points
        });

        emit CertificationAdded(_certificationId, _nurseId, _nurseName);
    }

    function getCertification(uint256 _certificationId)
        external
        view
        returns (uint256 nurseId, uint256 subjectId, string memory nurseName, string memory subjectName, uint256 points)
    {
        Certification storage cert = certifications[_certificationId];
        return (cert.nurseId, cert.subjectId, cert.nurseName, cert.subjectName, cert.points);
    }
}
