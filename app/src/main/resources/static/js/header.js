// 알림 모달 열기
function openHeaderNotificationModal() {
  document.getElementById('headerNotificationModal').style.display = 'block';
}

// 알림 모달 닫기
function closeHeaderNotificationModal() {
  document.getElementById('headerNotificationModal').style.display = 'none';
}

// 페이지가 로드되면 저장된 알림을 불러옵니다.
document.addEventListener('DOMContentLoaded', function () {
  loadHeaderNotifications();

  // "전체 삭제" 버튼에 이벤트 리스너를 추가합니다.
  const deleteAllNotificationsButton = document.getElementById(
      'deleteAllNotificationsButton');
  if (deleteAllNotificationsButton) {
    deleteAllNotificationsButton.addEventListener('click', function () {
      fetch('/member/notifications/deleteAll', {
        method: 'POST',
      })
      .then(response => {
        if (!response.ok) {
          return Promise.reject(
              'Error deleting notifications: ' + response.statusText);
        }
        // 성공적으로 삭제되었을 경우, UI에서도 알림을 제거합니다.
        const notiContainer = document.querySelector("#notificationList");
        while (notiContainer.firstChild) {
          notiContainer.removeChild(notiContainer.firstChild);
        }
      })
      .catch(error => {
        console.error(error);
      });
    });
  }
});

function loadHeaderNotifications() {
  fetch('/member/headerNotifications', {
    method: 'GET',
  })
  .then(response => {
    if (!response.ok) {
      return Promise.reject(
          'Error loading notifications: ' + response.statusText);
    }
    return response.json();
  })
  .then(notifications => {
    const notiContainer = document.querySelector("#notificationList");
    notifications.forEach(notification => {
      const newNoti = document.createElement("li");
      newNoti.textContent = notification.content;
      notiContainer.appendChild(newNoti);
    });
  })
  .catch(error => {
    console.error(error);
  });
}

window.addEventListener('load', function () {
  const alarmIcon = document.getElementById('alarmIcon');
  if (!alarmIcon) {
    console.error('alarmIcon does not exist!');
    return;
  }

  alarmIcon.addEventListener('click', function () {
    openHeaderNotificationModal();
  });

  const eventSource = new EventSource('/member/notifications/stream');
  eventSource.addEventListener('alarm', function (event) {
    const data = JSON.parse(event.data);
    const notiContainer = document.querySelector("#notificationList");
    const newNoti = document.createElement("li");
    newNoti.textContent = data.content;
    if (notiContainer.firstChild) {
      notiContainer.insertBefore(newNoti, notiContainer.firstChild);
    } else {
      notiContainer.appendChild(newNoti);
    }
  });
  eventSource.onerror = function (event) {
    console.error('EventSource failed:', event);
    eventSource.close();
  };
});
