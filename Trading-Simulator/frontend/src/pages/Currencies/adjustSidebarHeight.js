export const adjustSidebarHeight = () => {
  const tableContainer = document.querySelector('.main-container');
  const sidebar = document.querySelector('.sidebar_main');

  if (tableContainer && sidebar) {
    sidebar.style.height = `${tableContainer.offsetHeight}px`;
  }
};
