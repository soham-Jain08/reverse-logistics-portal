// ReturnHub - small helpers used on every page
document.addEventListener('DOMContentLoaded', function () {

  // 1. Success messages fade away after a few seconds
  document.querySelectorAll('.auto-dismiss').forEach(function (el) {
    setTimeout(function () {
      if (window.bootstrap) {
        bootstrap.Alert.getOrCreateInstance(el).close();
      } else {
        el.remove();
      }
    }, 6000);
  });

  // 2. Demo account buttons on the login page fill the form
  document.querySelectorAll('.demo-fill').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var user = document.getElementById('username');
      var pass = document.getElementById('password');
      if (user && pass) {
        user.value = btn.dataset.user;
        pass.value = btn.dataset.pass;
        user.focus();
      }
    });
  });

  // 3. Ask before doing something that cannot be undone
  document.querySelectorAll('form[data-confirm]').forEach(function (form) {
    form.addEventListener('submit', function (event) {
      if (!window.confirm(form.dataset.confirm)) {
        event.preventDefault();
      }
    });
  });

  // 4. Stop double submits
  document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function (event) {
      if (event.defaultPrevented) { return; }
      var btn = form.querySelector('button[type="submit"]');
      if (btn && !btn.dataset.keepEnabled) {
        setTimeout(function () { btn.disabled = true; }, 0);
      }
    });
  });
});
