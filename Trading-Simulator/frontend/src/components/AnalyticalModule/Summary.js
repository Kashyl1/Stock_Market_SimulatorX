import React from 'react';
import PieChartWithNeedle from './PieChart';

const Summary = ({ movingAveragesSummary, analyticalSummary }) => {
  const determineOverallSignal = (buyCount, sellCount) => {
    if (buyCount > sellCount) {
      if (buyCount >= 10) return 'Strong Buy';
      return 'Buy';
    } else if (sellCount > buyCount) {
      if (sellCount >= 10) return 'Strong Sell';
      return 'Sell';
    } else {
      return 'Neutral';
    }
  };

  const countMovingAveragesSignals = (summary) => {
    let buyCount = summary.buyCount || 0;
    let sellCount = summary.sellCount || 0;
    return { buyCount, sellCount };
  };

  const countAnalyticalSignals = (summary) => {
    let buyCount = summary.Buy || 0;
    let sellCount = summary.Sell || 0;
    return { buyCount, sellCount };
  };

  const movingAveragesCounts = countMovingAveragesSignals(movingAveragesSummary);
  const analyticalCounts = countAnalyticalSignals(analyticalSummary);


  const totalBuyCount = movingAveragesCounts.buyCount + analyticalCounts.buyCount;
  const totalSellCount = movingAveragesCounts.sellCount + analyticalCounts.sellCount;


  const overallSignal = determineOverallSignal(totalBuyCount, totalSellCount);

 return (
     <div className="analytical-module">
       <h2 className="table-title">Summary:</h2>

       <div className="summary-container">
         <div className="summary">
           <p><strong>Overall Signal: {overallSignal}</strong></p>
         </div>
         <div className="chart-container">
           <PieChartWithNeedle overallSignal={overallSignal} />
         </div>
       </div>

       <table className="indicator-table">
         <thead>
           <tr>
             <th>Category</th>
             <th>Buy</th>
             <th>Sell</th>
           </tr>
         </thead>
         <tbody>
           <tr>
             <td>Moving Averages</td>
             <td>{movingAveragesCounts.buyCount}</td>
             <td>{movingAveragesCounts.sellCount}</td>
           </tr>
           <tr>
             <td>Technical Indicators</td>
             <td>{analyticalCounts.buyCount}</td>
             <td>{analyticalCounts.sellCount}</td>
           </tr>
         </tbody>
       </table>
     </div>
   );
 };

 export default Summary;