import { useEffect } from 'react';

const TransactionHistoryHeightAdjuster = ({ showTransactionHistory, initialized }) => {
  useEffect(() => {
    const mainPageElement = document.querySelector('.main-page');
    const assetsTableElement = document.querySelector('.assets-table');

    if (mainPageElement) {
      if (assetsTableElement) {
        let assetsTableHeight = 0;

        if (showTransactionHistory) {
          assetsTableHeight = assetsTableElement.getBoundingClientRect().height * 1.15;
        } else if (initialized) {
          assetsTableHeight = assetsTableElement.getBoundingClientRect().height * 0.7;
        }

        mainPageElement.style.height = `calc(100vh + ${assetsTableHeight}px)`;
      } else {
        mainPageElement.style.height = '100vh';
      }
    }
  }, [showTransactionHistory, initialized]);

  return null;
};

export default TransactionHistoryHeightAdjuster;
