// Draws the QR code on the printable return label
document.addEventListener('DOMContentLoaded', function () {
  var box = document.getElementById('qrcode');
  if (box && window.QRCode) {
    new QRCode(box, { text: box.dataset.code, width: 110, height: 110 });
  }
});
