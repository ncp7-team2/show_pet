// JS (profile.js)
function openFollowerModal() {
  var modal = document.getElementById("followerModal");
  modal.style.display = "block";

  window.onclick = function (event) {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  }
}

function closeFollowerModal() {
  var modal = document.getElementById("followerModal");
  modal.style.display = "none";
}

function openFollowingModal() {
  var modal = document.getElementById("followingModal");
  modal.style.display = "block";

  window.onclick = function (event) {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  }
}

function closeFollowingModal() {
  var modal = document.getElementById("followingModal");
  modal.style.display = "none";
}

function openLikeModal() {
  var modal = document.getElementById("likeModal");
  modal.style.display = "block";

  window.onclick = function (event) {
    if (event.target == modal) {
      modal.style.display = "none";
    }
  }
}

function closeLikeModal() {
  var modal = document.getElementById("likeModal");
  modal.style.display = "none";
}

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
