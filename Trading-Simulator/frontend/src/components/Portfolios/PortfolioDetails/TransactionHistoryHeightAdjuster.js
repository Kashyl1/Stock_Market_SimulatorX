
import { useEffect } from 'react';

const TransactionHistoryHeightAdjuster = ({ showTransactionHistory }) => {
  useEffect(() => {
    const mainPageElement = document.querySelector('.main-page');

    if (showTransactionHistory && mainPageElement) {

      mainPageElement.style.height = 'calc(100vh + 700px)';
    } else if (mainPageElement) {

      mainPageElement.style.height = '100vh';
    }
  }, [showTransactionHistory]);

  return null;
};

export default TransactionHistoryHeightAdjuster;
