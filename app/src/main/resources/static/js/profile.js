const myPostsButton = document.getElementById('myPostsButton');
const likedPostsButton = document.getElementById('likedPostsButton');
const bookmarkedPostsButton = document.getElementById('bookmarkedPostsButton');

const myPosts = document.getElementById('myPosts');
const likedPosts = document.getElementById('likedPosts');
const bookmarkedPosts = document.getElementById('bookmarkedPosts');

myPostsButton.addEventListener('click', () => {
  myPosts.style.display = 'block';
  likedPosts.style.display = 'none';
  bookmarkedPosts.style.display = 'none';
});

likedPostsButton.addEventListener('click', () => {
  myPosts.style.display = 'none';
  likedPosts.style.display = 'block';
  bookmarkedPosts.style.display = 'none';
});

bookmarkedPostsButton.addEventListener('click', () => {
  myPosts.style.display = 'none';
  likedPosts.style.display = 'none';
  bookmarkedPosts.style.display = 'block';
});

// 팔로워 목록 모달창 열기
function openFollowerModal() {
  var modal = document.getElementById("followerModal");
  modal.style.display = "block";
}

// 팔로워 목록 모달창 닫기
function closeFollowerModal() {
  var modal = document.getElementById("followerModal");
  modal.style.display = "none";
}

// 팔로잉 목록 모달창 열기
function openFollowingModal() {
  var modal = document.getElementById("followingModal");
  modal.style.display = "block";
}

// 팔로잉 목록 모달창 닫기
function closeFollowingModal() {
  var modal = document.getElementById("followingModal");
  modal.style.display = "none";
}