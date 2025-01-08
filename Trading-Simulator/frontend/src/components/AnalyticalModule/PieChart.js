import React from 'react';
import { PieChart, Pie, Cell, Text } from 'recharts';

const RADIAN = Math.PI / 180;

const data = [
  { name: 'Strong Sell', value: 20, color: '#d91400' },
  { name: 'Sell', value: 20, color: '#da8b83' },
  { name: 'Neutral', value: 10, color: '#d8d9d8' },
  { name: 'Buy', value: 20, color: '#8dc5a3' },
  { name: 'Strong Buy', value: 20, color: '#007c32' },
];

const cx = 150;
const cy = 150;
const iR = 80;
const oR = 100;


const getNeedleAngle = (signal) => {
  const signalAngles = {
    'Strong Sell': 10,
    'Sell': 30,
    'Neutral': 50,
    'Buy': 70,
    'Strong Buy': 90,
  };
  return signalAngles[signal] || 0;
};


const needle = (signal, cx, cy, iR, oR, color) => {
  const ang = 180.0 * (1 - getNeedleAngle(signal) / 100);
  const length = (iR + 2 * oR) / 3;
  const sin = Math.sin(-RADIAN * ang);
  const cos = Math.cos(-RADIAN * ang);
  const r = 5;
  const x0 = cx + 5;
  const y0 = cy;
  const xba = x0 + r * sin;
  const yba = y0 - r * cos;
  const xbb = x0 - r * sin;
  const ybb = y0 + r * cos;
  const xp = x0 + length * cos;
  const yp = y0 + length * sin;

  return [
    <circle key="circle" cx={x0} cy={y0} r={r} fill={color} stroke="none" />,
    <path
      key="path"
      d={`M${xba} ${yba}L${xbb} ${ybb} L${xp} ${yp} L${xba} ${yba}`}
      stroke="#none"
      fill={color}
    />,
  ];
};

const PieChartWithNeedle = ({ overallSignal }) => {
  return (
    <div style={{ width: '400px', height: '300px', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
      <PieChart width={300} height={300}>
        <Pie
          dataKey="value"
          startAngle={180}
          endAngle={0}
          data={data}
          cx={cx}
          cy={cy}
          innerRadius={iR}
          outerRadius={oR}
          fill="#8884d8"
          stroke="none"
        >
          {data.map((entry, index) => (
            <Cell key={`cell-${index}`} fill={entry.color} />
          ))}
        </Pie>
        {needle(overallSignal, cx, cy, iR, oR, '#d0d000')}
        {data.map((entry, index) => {
          const angle = 180 * (1 - (entry.value / 100));
          const radius = (iR + oR) / 2;
          const x = cx + radius * Math.cos(RADIAN * angle);
          const y = cy + radius * Math.sin(RADIAN * angle);

          return (
            <Text
              key={index}
              x={x}
              y={y}
              textAnchor="middle"
              fill="#000000"
              fontSize={12}
              fontWeight="bold"
            >
              {entry.name}
            </Text>
          );
        })}
      </PieChart>
    </div>
  );
};

export default PieChartWithNeedle;
