const apiUrl = 'http://120.110.115.123:8081/api/nursecertifications';
const subjectApiUrl = 'http://120.110.115.123:8081/api/subjects'; // 獲取所有科目數據
const tableBody = document.querySelector('#certificationsTable tbody');
const loadingDiv = document.getElementById('loading');
const messageDiv = document.getElementById('message');
const certificationForm = document.getElementById('certificationForm');
const submitBtn = document.getElementById('submitBtn');
const certificationIdInput = document.getElementById('certificationId');

const categorySelect = document.getElementById('categorySelect'); // 新增類別下拉選單
const subjectSelect = document.getElementById('subjectSelect'); // 科目下拉選單
const subjectIdInput = document.getElementById('subjectId'); // 隱藏的 subjectId 輸入框
const unitInput = document.getElementById('unit'); // 單位輸入框
const pointsInput = document.getElementById('points'); // 積分輸入框，用於自動帶出

let allSubjects = []; // 儲存所有科目數據

// 輔助函數：顯示訊息
function showMessage(msg, type = 'success') {
    messageDiv.textContent = msg;
    messageDiv.className = type;
    setTimeout(() => {
        messageDiv.textContent = '';
        messageDiv.className = '';
    }, 300000);
}

// 輔助函數：清除表單
function clearForm() {
    certificationForm.reset();
    certificationIdInput.value = '';
    categorySelect.value = ''; // 清空類別選擇
    subjectSelect.innerHTML = '<option value=>請先選擇類別</option>'; // 重置科目下拉選單
    subjectSelect.disabled = true; // 禁用科目下拉選單
    subjectIdInput.value = '';
    unitInput.value = '';
    pointsInput.value = ''; // 清空積分
    submitBtn.textContent = '新增證書';
    submitBtn.style.backgroundColor = '#28a745';
}

// 載入所有科目數據 (包括 category 和 unit)
async function loadAllSubjectsData() {
    try {
        const response = await fetch(subjectApiUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        allSubjects = await response.json(); // 儲存所有科目數據

        // 提取所有不重複的 category
        const categories = [...new Set(allSubjects.map(s => s.category))];
        categorySelect.innerHTML = '<option value=>請選擇類別</option>';
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            option.textContent = category;
            categorySelect.appendChild(option);
        });

    } catch (error) {
        console.error('獲取科目數據時出錯:', error);
        showMessage('載入科目數據失敗！', 'error');
    }
}

// 根據選擇的 category 填充 subject 下拉選單
categorySelect.addEventListener('change', function () {
    const selectedCategory = this.value;
    subjectSelect.innerHTML = '<option value=>請選擇科目</option>';
    subjectIdInput.value = ''; // 清空科目ID
    unitInput.value = ''; // 清空單位
    pointsInput.value = ''; // 清空積分

    if (selectedCategory) {
        subjectSelect.disabled = false; // 啟用科目下拉選單
        const filteredSubjects = allSubjects.filter(s => s.category === selectedCategory);
        filteredSubjects.forEach(subject => {
            const option = document.createElement('option');
            option.value = subject.id;
            option.textContent = subject.subjectName;
            subjectSelect.appendChild(option);
        });
    } else {
        subjectSelect.disabled = true; // 禁用科目下拉選單
        subjectSelect.innerHTML = '<option value=>請先選擇類別</option>';
    }
});

// 處理 subject 選擇變化，自動帶出 unit 和 points
subjectSelect.addEventListener('change', function () {
    const selectedSubjectId = this.value;
    subjectIdInput.value = selectedSubjectId; // 更新隱藏的 subjectId

    if (selectedSubjectId) {
        const selectedSubject = allSubjects.find(s => s.id == selectedSubjectId);
        if (selectedSubject) {
            unitInput.value = selectedSubject.unit || '';
            pointsInput.value = selectedSubject.points || ''; // 自動帶出積分
        }
    } else {
        unitInput.value = '';
        pointsInput.value = '';
    }
});

// 載入護士證書列表
async function loadCertifications() {
    loadingDiv.style.display = 'block';
    tableBody.innerHTML = '';

    try {
        const response = await fetch(apiUrl);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        loadingDiv.style.display = 'none';

        if (data && data.length > 0) {
            data.forEach(cert => {
                const row = tableBody.insertRow();
                row.setAttribute('data-id', cert.id);
                row.insertCell().textContent = cert.id;
                row.insertCell().textContent = cert.nurseId;
                row.insertCell().textContent = cert.nurseName;
                row.insertCell().textContent = cert.subjectId;
                row.insertCell().textContent = cert.subjectName;
                row.insertCell().textContent = cert.startTime ? new Date(cert.startTime).toLocaleString() : '';
                row.insertCell().textContent = cert.endTime ? new Date(cert.endTime).toLocaleString() : '';
                row.insertCell().textContent = cert.points;
                row.insertCell().textContent = cert.unit; // 顯示 unit

                const actionsCell = row.insertCell();
                const editButton = document.createElement('button');
                editButton.textContent = '編輯';
                editButton.className = 'btn-edit';
                editButton.onclick = () => editCertification(cert);
                actionsCell.appendChild(editButton);

                const deleteButton = document.createElement('button');
                deleteButton.textContent = '刪除';
                deleteButton.className = 'btn-delete';
                deleteButton.onclick = () => deleteCertification(cert.id);
                actionsCell.appendChild(deleteButton);

                const toBlockChain = document.createElement('button');
                toBlockChain.textContent = '上鏈';
                toBlockChain.className = 'btn-toBlockChain';
                toBlockChain.onclick = () => toBlockchain(cert.id);
                actionsCell.appendChild(toBlockChain);
            });
        } else {
            const row = tableBody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 10;
            cell.textContent = '沒有找到護士證書數據。';
            cell.style.textAlign = 'center';
        }
    } catch (error) {
        console.error('獲取護士證書數據時出錯:', error);
        loadingDiv.textContent = '載入數據失敗，請檢查控制台。';
        loadingDiv.style.color = 'red';
    }
}

// 表單提交處理 (新增或更新)
certificationForm.addEventListener('submit', async function (event) {
    event.preventDefault();

    const id = certificationIdInput.value;
    const nurseId = document.getElementById('nurseId').value;
    const selectedSubjectId = subjectIdInput.value; // 從隱藏的輸入框獲取 subjectId
    const startTime = document.getElementById('startTime').value;
    const endTime = document.getElementById('endTime').value;
    const points = document.getElementById('points').value;

    if (!selectedSubjectId) {
        showMessage('請選擇一個科目！', 'error');
        return;
    }

    const certificationData = {
        nurseId: parseInt(nurseId),
        subjectId: parseInt(selectedSubjectId),
        startTime: startTime,
        endTime: endTime,
        points: parseFloat(points)
    };

    let response;
    try {
        console.log(certificationData)
        if (id) {
            response = await fetch(`${apiUrl}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(certificationData)
            });
            if (response.ok) {
                showMessage('證書更新成功！');
            } else {
                throw new Error(`更新失敗: ${response.statusText}`);
            }
        } else {
            response = await fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(certificationData)
            });
            if (response.ok) {
                showMessage('證書新增成功！');
            } else {
                throw new Error(`新增失敗: ${response.statusText}`);
            }
        }
        clearForm();
        loadCertifications();
    } catch (error) {
        console.error('操作失敗:', error);
        showMessage(error.message, 'error');
    }
});

// 編輯功能：將數據填充到表單
async function editCertification(cert) {
    certificationIdInput.value = cert.id;
    document.getElementById('nurseId').value = cert.nurseId;

    // 載入所有科目數據以確保下拉選單已填充
    await loadAllSubjectsData();

    // 設定 category 下拉選單的值
    const selectedSubject = allSubjects.find(s => s.id == cert.subjectId);
    if (selectedSubject) {
        categorySelect.value = selectedSubject.category;
        // 觸發 category 的 change 事件來填充 subjectSelect
        categorySelect.dispatchEvent(new Event('change'));

        // 設定 subject 下拉選單的值
        subjectSelect.value = cert.subjectId;
        subjectIdInput.value = cert.subjectId; // 同步隱藏的 subjectId
        unitInput.value = selectedSubject.unit || ''; // 自動帶出 unit
        pointsInput.value = selectedSubject.points || ''; // 自動帶出積分
    } else {
        // 如果找不到科目，則清空相關欄位
        clearForm();
        showMessage('編輯的科目不存在，請重新選擇。', 'error');
    }

    document.getElementById('startTime').value = cert.startTime ? new Date(cert.startTime).toISOString().slice(0, 16) : '';
    document.getElementById('endTime').value = cert.endTime ? new Date(cert.endTime).toISOString().slice(0, 16) : '';
    document.getElementById('points').value = cert.points;

    submitBtn.textContent = '更新證書';
    submitBtn.style.backgroundColor = '#007bff';
}

// 刪除功能 (保持不變)
async function deleteCertification(id) {
    if (confirm(`確定要刪除 ID 為 ${id} 的證書嗎？`)) {
        try {
            const response = await fetch(`${apiUrl}/${id}`, {
                method: 'DELETE'
            });
            if (response.ok) {
                showMessage('證書刪除成功！');
                loadCertifications();
            } else {
                throw new Error(`刪除失敗: ${response.statusText}`);
            }
        } catch (error) {
            console.error('刪除操作失敗:', error);
            showMessage(error.message, 'error');
        }
    }
}

// 假設的 toBlockchain 函數 (保持不變)
async function toBlockchain(certificationId) {
    if (!certificationId) {
        showMessage('請選擇一個證書進行上鏈操作！', 'error');
        return;
    }
    showMessage(`正在處理 ID ${certificationId} 的證書上鏈...`, 'info');
    try {
        // 使用 fetch API 向您的 Blockchain Controller 發送請求
        // 您可以選擇使用 GET 請求 (傳遞路徑變數或查詢參數) 或 POST 請求 (傳遞請求體)
        // 這裡示範使用 POST 請求，並在請求體中傳遞 JSON 資料

        const blockchainApiUrl = 'http://120.110.115.123:8081/api/blockchain/certify'; // 請替換為您的實際區塊鏈 Controller 端點

        const response = await fetch(blockchainApiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ certificationId: certificationId }) // 將證書ID作為 JSON 傳遞
        });

        if (!response.ok) {
            // 處理非 2xx 狀態碼的響應
            const errorText = await response.text();
            throw new Error(`上鏈失敗: ${response.status} - ${errorText}`);
        }

        const result = await response.json(); // 假設 Controller 會返回 JSON 響應
        showMessage(`證書 ID ${certificationId} 上鏈成功！交易 Hash: ${result.transactionHash}`, 'success');
        console.log('上鏈成功響應:', result);

        // 您可能需要重新載入證書列表或更新UI以反映上鏈狀態

    } catch (error) {
        console.error('上鏈操作失敗:', error);
        showMessage(`上鏈失敗: ${error.message}`, 'error');
    }

}

// 頁面載入時執行
document.addEventListener('DOMContentLoaded', async () => {
    await loadAllSubjectsData(); // 先載入所有科目數據（包括類別和名稱）
    await loadCertifications(); // 再載入證書列表
});