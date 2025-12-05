document.addEventListener("DOMContentLoaded", () => {

    /* ============================
       게임 선택 로그 (옵션)
    ============================ */
    const gameSelect = document.getElementById("gameSlug");
    if (gameSelect) {
        gameSelect.addEventListener("change", () => {
            console.log("선택된 게임 slug:", gameSelect.value);
        });
    }

    /* ============================
       이미지 업로드 요소
    ============================ */
    const dropZone = document.getElementById("dropZone");
    const fileInput = document.getElementById("fileInput");
    const preview = document.getElementById("filePreview");

    // 서버로 보낼 파일 리스트
    let filesArray = [];

    /* ============================
       파일 추가 함수
    ============================ */
    function addFiles(newFiles) {
        for (const file of newFiles) {

            // 이미지 파일만 허용
            if (!file.type.startsWith("image/")) continue;

            filesArray.push(file);
            const url = URL.createObjectURL(file);

            const wrapper = document.createElement("div");
            wrapper.className = "file-preview-item";

            const img = document.createElement("img");
            img.src = url;

            const removeBtn = document.createElement("button");
            removeBtn.className = "file-remove-btn";
            removeBtn.innerText = "×";

            // 삭제 버튼 동작
            removeBtn.addEventListener("click", () => {
                wrapper.remove();
                filesArray = filesArray.filter(f => f !== file);
                URL.revokeObjectURL(url);
            });

            wrapper.appendChild(img);
            wrapper.appendChild(removeBtn);
            preview.appendChild(wrapper);
        }
    }

    /* ============================
       클릭 업로드
    ============================ */
    dropZone.addEventListener("click", () => fileInput.click());
    fileInput.addEventListener("change", (e) => addFiles(e.target.files));

    /* ============================
       드래그 앤 드롭 업로드
    ============================ */
    dropZone.addEventListener("dragover", (e) => {
        e.preventDefault();
        dropZone.classList.add("dragover");
    });

    dropZone.addEventListener("dragleave", () => {
        dropZone.classList.remove("dragover");
    });

    dropZone.addEventListener("drop", (e) => {
        e.preventDefault();
        dropZone.classList.remove("dragover");
        addFiles(e.dataTransfer.files);
    });

    /* ============================
       Ctrl + V 이미지 붙여넣기 (방법 1 적용)
       → 이미지 붙여넣기 시 텍스트 입력 차단
    ============================ */
    document.addEventListener("paste", (e) => {

        const items = e.clipboardData.items;
        if (!items) return;

        let hasImage = false;

        for (const item of items) {
            if (item.type.indexOf("image") !== -1) {
                const file = item.getAsFile();
                if (file) {
                    addFiles([file]);
                    hasImage = true;
                }
            }
        }

        // 이미지 붙여넣기 시 textarea의 기본 붙여넣기(텍스트 입력) 차단
        if (hasImage) {
            e.preventDefault();
        }
    });

    /* ============================
       폼 제출 시 실제 input.files 에 재할당하여 서버 전송
    ============================ */
    const form = document.querySelector("form");

    form.addEventListener("submit", () => {
        const dataTransfer = new DataTransfer();
        filesArray.forEach(file => dataTransfer.items.add(file));
        fileInput.files = dataTransfer.files;  // 실제 서버로 보내는 파일 목록
    });

});
