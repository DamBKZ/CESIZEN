window.addEventListener('load', () => {
  const loader = document.getElementById('app-loader');

  if (!loader) {
    return;
  }

  loader.style.opacity = '0';
  loader.style.visibility = 'hidden';

  window.setTimeout(() => {
    loader.remove();
  }, 300);
});
