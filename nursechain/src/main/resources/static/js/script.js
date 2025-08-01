const BASE_API_URL = 'http://120.110.115.123:8081';

const apiUrl = `${BASE_API_URL}/api/nursecertifications`;
const subjectApiUrl = `${BASE_API_URL}/api/subjects`; // 獲取所有科目數據
const blockchainApiUrl = `${BASE_API_URL}/api/blockchain/certify`; // 區塊鏈 Controller 端點

const tableBody = document.querySelector('#certificationsTable tbody');
const loadingDiv = document.getElementById('loading');
const messageDiv = document.getElementById('message'); // 主要訊息區域

// 確保有 blockchainContainer 元素來顯示區塊，如果沒有，請在您的 HTML 中添加
const blockchainContainer = document.getElementById('blockchainContainer');
if (!blockchainContainer) {
    console.warn("Element with ID 'blockchainContainer' not found. Block visualization might not work.");
    // 您可以在這裡創建一個預設容器，或者要求用戶在 HTML 中添加
    // document.body.appendChild(Object.assign(document.createElement('div'), { id: 'blockchainContainer' }));
}

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
// msg: 訊息內容
// type: 訊息類型 ('success', 'error', 'info')
// append: 是否追加訊息 (true) 或替換訊息 (false)
// autoClearDuration: 自動清除訊息的時間 (毫秒)。0 表示不自動清除。
function showMessage(msg, type = 'success', append = false, autoClearDuration = 3000) {
    const p = document.createElement('p'); // 創建一個新的段落元素來顯示訊息
    p.textContent = msg;
    p.className = type; // 添加類型類別用於樣式

    if (!append) {
        messageDiv.innerHTML = ''; // 清空舊訊息
    }
    messageDiv.appendChild(p); // 追加訊息或替換後的首條訊息
    messageDiv.style.display = 'block'; // 確保訊息區域可見

    if (autoClearDuration > 0) {
        // 如果需要自動清除，則只清除當前這條新追加的訊息
        setTimeout(() => {
            if (messageDiv.contains(p)) {
                p.remove();
                // 如果移除後沒有其他訊息，則隱藏容器
                if (messageDiv.children.length === 0) {
                    messageDiv.style.display = 'none';
                }
            }
        }, autoClearDuration);
    }
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

// 載入護理師證書列表
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
                // 新增 data-certification-id 屬性以便「一鍵上鏈」功能使用
                row.setAttribute('data-certification-id', cert.id);
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

                // const toBlockChainButton = document.createElement('button'); // 修改變數名避免衝突
                // toBlockChainButton.textContent = '上鏈';
                // toBlockChainButton.className = 'btn-toBlockChain'; // 保持 class 名稱一致
                // toBlockChainButton.onclick = () => toBlockchain(cert.id, true); // 單獨上鏈時傳入 true，表示重定向
                // actionsCell.appendChild(toBlockChainButton);
            });
            let totalPoints = 0;
            data.forEach(cert => {
                // ... (現有的行插入邏輯)
                totalPoints += cert.points || 0; // 累計積分
            });
            const requiredPoints = 120; // 6年應修120點
            const pointsNeeded = requiredPoints - totalPoints;

            // 顯示累計積分和所需積分的提示
            showMessage(
                `目前累計積分: ${totalPoints.toFixed(1)} 點。` +
                `6年應修 ${requiredPoints} 點，還需 ${pointsNeeded.toFixed(1)} 點。`,
                pointsNeeded <= 0 ? 'success' : 'info', // 如果達到目標，顯示成功訊息
                true, // 追加訊息
                0 // 不自動清除
            );

        } else {
            const row = tableBody.insertRow();
            const cell = row.insertCell();
            cell.colSpan = 10;
            cell.textContent = '沒有找到護理師證書數據。';
            cell.style.textAlign = 'center';
        }
    } catch (error) {
        console.error('獲取護理師證書數據時出錯:', error);
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
        showMessage('請選擇一個科目！', 'error'); // 替換顯示
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
        console.log(certificationData);
        if (id) {
            response = await fetch(`${apiUrl}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(certificationData)
            });
            if (response.ok) {
                showMessage('證書更新成功！'); // 替換顯示
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
                showMessage('證書新增成功！'); // 替換顯示
            } else {
                throw new Error(`新增失敗: ${response.statusText}`);
            }
        }
        clearForm();
        loadCertifications();
    } catch (error) {
        console.error('操作失敗:', error);
        showMessage(error.message, 'error'); // 替換顯示
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
        showMessage('編輯的科目不存在，請重新選擇。', 'error'); // 替換顯示
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
                showMessage('證書刪除成功！'); // 替換顯示
                loadCertifications();
            } else {
                throw new Error(`刪除失敗: ${response.statusText}`);
            }
        } catch (error) {
            console.error('刪除操作失敗:', error);
            showMessage(error.message, 'error'); // 替換顯示
        }
    }
}

/**
 * 將單個證書數據上鏈到區塊鏈
 * @param {number} certificationId - 要上鏈的證書 ID
 * @param {boolean} [redirectAfterCompletion=false] - 是否在成功後重定向到區塊鏈模擬頁面
 * @returns {Promise<object|null>} - 返回一個 Promise，解析為成功響應物件或 null (失敗)
 */
async function toBlockchain(certificationId, redirectAfterCompletion = false) {
    if (!certificationId) {
        if (redirectAfterCompletion) { // 只有單獨操作才顯示這個錯誤
            showMessage('無效的證書 ID！', 'error');
        }
        return null;
    }

    const requestBody = {
        certificationId: certificationId
    };

    try {
        const response = await fetch(blockchainApiUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(`上鏈失敗: ${response.status} - ${errorData.message || JSON.stringify(errorData)}`);
        }

        const result = await response.json();
        console.log(`證書 ID ${certificationId} 上鏈成功響應:`, result);

        // 將 result 物件傳遞給 addNewBlockToDisplay 函數來更新區塊鏈可視化
        // 這會觸發網頁上新區塊的顯示
        addNewBlockToDisplay(result);



        if (redirectAfterCompletion) {
            // 單獨上鏈成功時才重定向並清除原訊息
            showMessage(`證書 ID ${certificationId} 上鏈成功！區塊哈希: ${result.blockHash}`, 'success');
            const queryParams = new URLSearchParams();
            if (result.timestamp) queryParams.append('timestamp', result.timestamp);
            if (result.blockHash) queryParams.append('blockHash', result.blockHash);
            if (result.merkleRoot) queryParams.append('merkleRoot', result.merkleRoot);
            if (result.nonce) queryParams.append('nonce', result.nonce);

            // 請替換為您的區塊鏈模擬網站的實際 URL
            const blockchainSimulationUrl = `${BASE_API_URL}/blockchain-simulation.html?${queryParams.toString()}`;
            window.location.href = blockchainSimulationUrl;
        }
        return result; // 返回成功結果
    } catch (error) {
        console.error('上鏈操作失敗:', error);
        if (redirectAfterCompletion) { // 只有單獨操作才顯示這個錯誤
            showMessage(`上鏈失敗: ${error.message}`, 'error');
        }
        return null; // 返回 null 表示失敗
    }

}

const GENESIS_BLOCK_DATA = {
    version: 'v1',
    previousHash: '0', // 創世塊的前一個區塊哈希通常是 '0' 或 NULL
    timestamp: new Date('2025/7/19 9:27:01').getTime(), // 使用您圖片中的時間戳
    difficulty: 1,
    nonce: 0, // 創世塊的 nonce
    merkleRoot: 'e3b0c44298fc1c149afbf4c8996b92427a4e464b934ca4b991b7852b855', // 您的圖片中的 Merkle root
    transactions: [], // 創世塊通常沒有交易，或只有特殊的創世交易
    // 其他圖片中顯示的創世區塊資訊
    blockHash: '844571e06527950110ffe8542f89754c8d6b6c65ae05954b2aaeeb215ca1e1599' // 您的圖片中的區塊哈希
};

// 載入創世塊到顯示器
function loadGenesisBlock() {
    const blockchainContainer = document.getElementById('blockchainContainer');
    if (blockchainContainer) {
        const genesisBlockElement = createBlockElement(GENESIS_BLOCK_DATA);
        // 您可以給創世塊添加一個特定的 class 來區分樣式
        genesisBlockElement.classList.add('genesis-block');
        // 將創世塊添加到容器的最開始
        blockchainContainer.prepend(genesisBlockElement);
    }
}

/**
 * 實現「一鍵上鏈」所有證書的功能，依序處理
 */
async function toBlockchainAllCertifications() {
    const allCertRows = document.querySelectorAll('#certificationsTable tbody tr[data-certification-id]');
    if (allCertRows.length === 0) {
        showMessage('沒有可上鏈的證書。', 'info'); // 替換顯示
        return;
    }

    // 清空並初始化訊息區域，然後追加總體進度訊息
    messageDiv.innerHTML = ''; // 清空所有訊息
    showMessage(`開始一鍵上鏈所有 ${allCertRows.length} 筆證書...`, 'info', true, 0); // 持續顯示，不自動清除

    const toBlockchainAllBtn = document.getElementById('toBlockchainAllBtn');
    toBlockchainAllBtn.disabled = true; // 禁用按鈕防止重複點擊

    let successfulUploads = 0;
    let failedUploads = 0;

    for (const row of allCertRows) {
        const certificationId = parseInt(row.dataset.certificationId);
        if (isNaN(certificationId)) {
            console.warn('跳過無效的證書 ID 行:', row);
            continue;
        }

        row.style.backgroundColor = '#e0f7fa'; // 淺藍色表示處理中
        showMessage(`正在上鏈證書 ID: ${certificationId}...`, 'info', true, 0); // 追加實時進度

        // 呼叫 toBlockchain 函數，設置 redirectAfterCompletion 為 false
        const result = await toBlockchain(certificationId, false); // result 會是 object 或 null

        // 移除單一區塊數據顯示的程式碼，因為 addNewBlockToDisplay 已經處理了視覺化

        if (result) {
            successfulUploads++;
            row.style.backgroundColor = '#e8f5e9'; // 淺綠色表示成功
            const btn = row.querySelector('.btn-toBlockChain');
            if (btn) {
                btn.textContent = '已上鏈';
                btn.disabled = true;
                btn.style.backgroundColor = '#4CAF50';
            }
            // 不需要再次呼叫 addNewBlockToDisplay(result) 因為 toBlockchain 內部已經呼叫了
            showMessage(`ID ${certificationId} 上鏈成功！`, 'success', true, 0); // 追加成功的訊息
        } else {
            failedUploads++;
            row.style.backgroundColor = '#ffebee'; // 淺紅色表示失敗
            const btn = row.querySelector('.btn-toBlockChain');
            if (btn) {
                btn.textContent = '上鏈失敗';
                btn.disabled = false; // 失敗的可以考慮允許再次嘗試
                btn.style.backgroundColor = '#f44336';
            }
            // 不需要在這裡呼叫 addNewBlockToDisplay(result)，因為是失敗的情況
            showMessage(`ID ${certificationId} 上鏈失敗！`, 'error', true, 0); // 追加失敗的訊息
        }

        // 添加一個小延遲，避免請求過於頻繁
        await new Promise(resolve => setTimeout(resolve, 500)); // 延遲 500 毫秒
    }

    // 所有處理完成後的總結訊息
    showMessage(`所有證書上鏈完成。成功: ${successfulUploads} 筆，失敗: ${failedUploads} 筆。`, 'info', true, 5000); // 顯示5秒
    toBlockchainAllBtn.disabled = false;
    // 禁用所有編輯和刪除按鈕
    document.querySelectorAll('.btn-edit, .btn-delete').forEach(button => {
        button.disabled = true;
        button.style.opacity = '0.5'; // 可選：視覺上表示禁用
        button.style.cursor = 'not-allowed';
    });
    // 重新啟用按鈕
}

// 頁面載入時執行
document.addEventListener('DOMContentLoaded', async () => {
    //  loadGenesisBlock();

    await loadAllSubjectsData(); // 先載入所有科目數據（包括類別和名稱）
    await loadCertifications(); // 再載入證書列表

    // 為「一鍵上鏈所有證書」按鈕添加事件監聽器
    const toBlockchainAllBtn = document.getElementById('toBlockchainAllBtn');
    if (toBlockchainAllBtn) {
        toBlockchainAllBtn.addEventListener('click', toBlockchainAllCertifications);
    }
});

// 假設這是您從後端獲取的新區塊數據
function createBlockElement(blockData) {
    const blockCard = document.createElement('div');
    blockCard.className = 'block-card';

    // 檢查 blockData 是否為空或無效，以避免錯誤
    if (!blockData) {
        blockCard.innerHTML = `<h3>錯誤：區塊數據無效</h3><p>無法顯示區塊資訊。</p>`;
        return blockCard;
    }

    // 根據您的數據結構填充區塊內容
    // 這裡的 blockData 應該是 BlockchainCertifyResponse DTO 的內容
    blockCard.innerHTML = `
        <h3>上鏈證書區塊</h3>
        <p><strong>區塊哈希:</strong> ${blockData.blockHash || 'N/A'}</p>
        <p><strong>Merkle Root:</strong> ${blockData.merkleRoot || 'N/A'}</p>
        <p><strong>時間戳記:</strong> ${blockData.timestamp ? new Date(blockData.timestamp).toLocaleString() : 'N/A'}</p>
        <p><strong>隨機數 (Nonce):</strong> ${blockData.nonce !== undefined ? blockData.nonce : 'N/A'}</p>
        <p><strong>證書 ID:</strong> ${blockData.certificationId || 'N/A'}</p>
        <p><strong>訊息:</strong> ${blockData.message || 'N/A'}</p>
        <button onclick="controlBlock('${blockData.blockHash}')">控制</button>
    `;
    return blockCard;
}

// 當您成功上鏈並獲取到新區塊數據後，呼叫此函數
function addNewBlockToDisplay(blockData) {

    // 確保 blockchainContainer 存在
    if (blockchainContainer) {
        const newBlockElement = createBlockElement(blockData);
        blockchainContainer.appendChild(newBlockElement);
        // 可選：滾動到最新區塊
        blockchainContainer.scrollLeft = blockchainContainer.scrollWidth;
    } else {
        console.error("無法將區塊添加到顯示器，因為 'blockchainContainer' 元素不存在。");
    }
}

// 這是 controlBlock 函數的假設實現，如果它在您的代碼中被定義
function controlBlock(blockHash) {
    alert(`控制區塊哈希: ${blockHash}`);
    // 在這裡添加您控制區塊的邏輯
}