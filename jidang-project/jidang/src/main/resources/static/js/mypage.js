// 간단한 유저 정보 (필요하면 여기 값만 바꿔도 됨)
const userInfo = {
  nickname: "GameMaster",
  email: "gamer@example.com",
  joinDate: "2024년 1월 15일",
  //followers: 234,
  //following: 89,
  posts: 42,
  selectedBadge: "legend",
};

document.addEventListener("DOMContentLoaded", function () {
  // 프로필 정보 세팅 (HTML에 이미 써놔서 굳이 안 써도 되지만 예시로 둠)
  const nicknameEl = document.getElementById("nickname");
  const emailEl = document.getElementById("email");
  const joinDateEl = document.getElementById("joinDate");
  //const followersEl = document.getElementById("followers");
  //const followingEl = document.getElementById("following");
  const postsEl = document.getElementById("posts");
  const avatarInitialEl = document.getElementById("avatarInitial");

  if (nicknameEl) nicknameEl.textContent = userInfo.nickname;
  if (emailEl) emailEl.textContent = userInfo.email;
  if (joinDateEl) joinDateEl.textContent = "가입일: " + userInfo.joinDate;
  //if (followersEl) followersEl.textContent = userInfo.followers;
  //if (followingEl) followingEl.textContent = userInfo.following;
  if (postsEl) postsEl.textContent = userInfo.posts;
  if (avatarInitialEl) avatarInitialEl.textContent = userInfo.nickname[0] || "G";

  // 칭호 선택 버튼
  const badgeButtons = document.querySelectorAll(".badge-item");
  badgeButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      const badgeId = btn.getAttribute("data-badge-id");
      userInfo.selectedBadge = badgeId;

      // 모든 배지에서 선택 클래스 제거 & "현재 선택 중" 문구 제거
      badgeButtons.forEach((b) => {
        b.classList.remove("badge-selected");
        const textEl = b.querySelector(".badge-selected-text");
        if (textEl) {
          b.removeChild(textEl);
        }
      });

      // 현재 선택된 배지에 클래스/문구 추가
      btn.classList.add("badge-selected");
      const selectedText = document.createElement("p");
      selectedText.className = "badge-selected-text";
      selectedText.textContent = "현재 선택 중";
      btn.appendChild(selectedText);
    });
  });

  // 로그아웃 버튼 (임시 동작)
  const logoutButton = document.getElementById("logoutButton");
  if (logoutButton) {
    logoutButton.addEventListener("click", (e) => {
      e.preventDefault();
      // 실제로는 세션 삭제 / 로그아웃 API 호출 필요
      alert("로그아웃 되었습니다. (데모 동작)");
      window.location.href = "/"; // 메인으로 이동
    });
  }
});
