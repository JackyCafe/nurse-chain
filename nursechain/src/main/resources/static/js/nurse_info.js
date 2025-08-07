const API_URL = 'http://120.110.115.123:8081/api/nurseinfo'; // 您的 API 端點
const nurseForm = document.getElementById('nurseForm');
const nurseTableBody = document.querySelector('#nurseTable tbody');
const messageDiv = document.getElementById('message');
const resetFormBtn = document.getElementById('resetFormBtn');

// 顯示訊息
function showMessage(msg, type = 'success') {
    messageDiv.textContent = msg;
    messageDiv.className = `message ${type}`;
    setTimeout(() => {
        messageDiv.textContent = '';
        messageDiv.className = 'message';
    }, 3000); // 3 秒後消失
}

// --- Read (獲取所有護理師) ---
async function fetchNurses() {
    try {
        const response = await fetch(API_URL);
        if (!response.ok) {
            throw new Error(`HTTP 錯誤! 狀態: ${response.status}`);
        }
        const nurses = await response.json();
        nurses.forEach(nurse => {
            console.log(`ID: ${nurse.user}, 身分證號: ${nurse.identyNo}`);
        });
        displayNurses(nurses);
    } catch (error) {
        console.error('獲取護理師時發生錯誤:', error);
        showMessage('無法載入護理師資訊。', 'error');
    }
}

// 顯示護理師在表格中
function displayNurses(nurses) {
    nurseTableBody.innerHTML = ''; // 清空現有列表
    if (nurses.length === 0) {
        nurseTableBody.innerHTML = '<tr><td colspan="5" style="text-align: center;">沒有護理師資訊。</td></tr>';
        return;
    }
    nurses.forEach(nurse => {
        const row = nurseTableBody.insertRow();
        row.dataset.id = nurse.id; // 將ID儲存在行上，方便後續操作

        row.insertCell().textContent = nurse.id;
        row.insertCell().textContent = nurse.identyNo;
        row.insertCell().textContent = nurse.user;
        row.insertCell().textContent = nurse.name;

        const actionsCell = row.insertCell();
        const editButton = document.createElement('button');
        editButton.textContent = '編輯';
        editButton.className = 'btn-secondary';
        editButton.onclick = () => loadNurseForEdit(nurse);
        actionsCell.appendChild(editButton);

        const deleteButton = document.createElement('button');
        deleteButton.textContent = '刪除';
        deleteButton.className = 'btn-danger';
        deleteButton.style.marginLeft = '5px';
        deleteButton.onclick = () => deleteNurse(nurse.id);
        actionsCell.appendChild(deleteButton);
    });
}

// --- Create/Update (新增/更新護理師) ---
nurseForm.addEventListener('submit', async (event) => {
    event.preventDefault(); // 阻止表單預設提交行為

    const id = document.getElementById('nurseId').value;
    const identyNo = document.getElementById('identyNo').value;
    const user = document.getElementById('user').value;
    const name = document.getElementById('name').value;
    const passwd = document.getElementById('passwd').value;

    const nurseData = {
        identyNo,
        user,
        name,
        passwd
    };

    let url = API_URL;
    let method = 'POST';

    if (id) { // 如果ID存在，執行更新操作
        url = `${API_URL}/${id}`; // 假設更新API是 PUT /api/nurseinfo/{id}
        method = 'PUT';
        nurseData.id = parseInt(id); // 更新操作通常也需要ID在RequestBody中
    }

    try {
        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(nurseData),
        });

        if (response.ok) {
            const result = await response.json();
            showMessage(`護理師資訊 ${id ? '更新' : '新增'} 成功！`);
            nurseForm.reset(); // 清空表單
            fetchNurses(); // 重新載入列表
        } else {
            const errorText = await response.text();
            throw new Error(`操作失敗: ${response.status} - ${errorText}`);
        }
    } catch (error) {
        console.error('保存護理師時發生錯誤:', error);
        showMessage(`保存護理師時發生錯誤: ${error.message}`, 'error');
    }
});

// --- Load for Edit (載入護理師資料到表單) ---
function loadNurseForEdit(nurse) {
    // console.error(nurse);
    document.getElementById('nurseId').value = nurse.id;
    document.getElementById('identyNo').value = nurse.identyNo;
    document.getElementById('user').value = nurse.user;
    document.getElementById('name').value = nurse.name;
    document.getElementById('passwd').value = nurse.passwd; // 密碼通常不會回傳，這裡僅為範例
    showMessage(`正在編輯護理師 ID: ${nurse.id}`);
}

// --- Delete (刪除護理師) ---
async function deleteNurse(id) {
    if (!confirm(`確定要刪除 ID 為 ${id} 的護理師嗎？`)) {
        return;
    }
    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
        });

        if (response.ok) {
            showMessage(`護理師 ID: ${id} 刪除成功！`);
            fetchNurses(); // 重新載入列表
        } else {
            const errorText = await response.text();
            throw new Error(`刪除失敗: ${response.status} - ${errorText}`);
        }
    } catch (error) {
        console.error('刪除護理師時發生錯誤:', error);
        showMessage(`刪除護理師時發生錯誤: ${error.message}`, 'error');
    }
}

// 重置表單按鈕事件
resetFormBtn.addEventListener('click', () => {
    nurseForm.reset();
    document.getElementById('nurseId').value = ''; // 清空 ID 欄位
    showMessage('表單已重置。');
});

// 頁面載入時自動獲取護理師列表
document.addEventListener('DOMContentLoaded', fetchNurses);