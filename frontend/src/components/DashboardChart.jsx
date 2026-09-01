import { useState, memo } from 'react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

const sixMonthData = [
  { label: 'Jan', Vegetables: 120, Fruits: 90, Dairy: 80, Grains: 70 },
  { label: 'Feb', Vegetables: 150, Fruits: 100, Dairy: 85, Grains: 72 },
  { label: 'Mar', Vegetables: 180, Fruits: 120, Dairy: 90, Grains: 78 },
  { label: 'Apr', Vegetables: 170, Fruits: 130, Dairy: 95, Grains: 84 },
  { label: 'May', Vegetables: 210, Fruits: 145, Dairy: 110, Grains: 96 },
  { label: 'Jun', Vegetables: 240, Fruits: 160, Dairy: 120, Grains: 105 },
];

const weeklyData = [
  { label: 'W1', Vegetables: 45, Fruits: 38, Dairy: 30, Grains: 22 },
  { label: 'W2', Vegetables: 52, Fruits: 42, Dairy: 35, Grains: 28 },
  { label: 'W3', Vegetables: 60, Fruits: 48, Dairy: 40, Grains: 32 },
  { label: 'W4', Vegetables: 70, Fruits: 58, Dairy: 45, Grains: 36 },
  { label: 'W5', Vegetables: 75, Fruits: 62, Dairy: 52, Grains: 38 },
  { label: 'W6', Vegetables: 82, Fruits: 68, Dairy: 56, Grains: 40 },
];

const dailyData = [
  { label: 'Mon', Vegetables: 18, Fruits: 15, Dairy: 12, Grains: 10 },
  { label: 'Tue', Vegetables: 22, Fruits: 18, Dairy: 14, Grains: 12 },
  { label: 'Wed', Vegetables: 20, Fruits: 17, Dairy: 13, Grains: 11 },
  { label: 'Thu', Vegetables: 26, Fruits: 21, Dairy: 15, Grains: 13 },
  { label: 'Fri', Vegetables: 30, Fruits: 24, Dairy: 19, Grains: 15 },
  { label: 'Sat', Vegetables: 34, Fruits: 28, Dairy: 22, Grains: 18 },
];

const yearlyData = [
  { label: '2019', Vegetables: 900, Fruits: 750, Dairy: 680, Grains: 560 },
  { label: '2020', Vegetables: 1050, Fruits: 820, Dairy: 740, Grains: 620 },
  { label: '2021', Vegetables: 1280, Fruits: 980, Dairy: 860, Grains: 710 },
  { label: '2022', Vegetables: 1460, Fruits: 1100, Dairy: 920, Grains: 790 },
  { label: '2023', Vegetables: 1600, Fruits: 1180, Dairy: 980, Grains: 840 },
  { label: '2024', Vegetables: 1760, Fruits: 1240, Dairy: 1040, Grains: 900 },
];

const timelineMap = {
  day: dailyData,
  week: weeklyData,
  month: sixMonthData,
  year: yearlyData,
};

function DashboardChart() {
  const [range, setRange] = useState('month');
  const chartData = timelineMap[range] || sixMonthData;
  const isMobile = typeof window !== 'undefined' && window.innerWidth < 768;
  const chartHeight = isMobile ? 250 : 288; // 288px = h-72

  const title = {
    day: 'Daily Purchase Trends',
    week: 'Weekly Purchase Trends',
    month: '6-Month Purchase Trends',
    year: 'Yearly Purchase Trends',
  }[range];

  return (
    <div className="rounded-2xl border border-emerald-200 bg-white p-5 shadow-sm">
      <div className="mb-4 flex items-center justify-between gap-3">
        <h3 className="text-lg font-semibold text-slate-800">{title}</h3>
        <div className="flex gap-2 rounded-full bg-emerald-50 p-1">
          {['day', 'week', 'month', 'year'].map((item) => (
            <button
              key={item}
              type="button"
              onClick={() => setRange(item)}
              className={`rounded-full px-2.5 py-1 text-xs font-semibold capitalize transition ${
                range === item ? 'bg-emerald-600 text-white' : 'text-emerald-700 hover:bg-white'
              }`}
            >
              {item}
            </button>
          ))}
        </div>
      </div>

      <div style={{ height: chartHeight }}>
        <ResponsiveContainer width="100%" height="100%">
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" stroke="#dbe7dc" />
            <XAxis dataKey="label" stroke="#4b5563" />
            <YAxis stroke="#4b5563" />
            <Tooltip formatter={(value) => [`₹${value}`, 'Spend']} />
            <Legend />
            <Bar dataKey="Vegetables" fill="#22c55e" radius={[6, 6, 0, 0]} />
            <Bar dataKey="Fruits" fill="#84cc16" radius={[6, 6, 0, 0]} />
            <Bar dataKey="Dairy" fill="#f59e0b" radius={[6, 6, 0, 0]} />
            <Bar dataKey="Grains" fill="#f97316" radius={[6, 6, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
      <div className="mt-4 rounded-xl bg-emerald-50 p-3 text-sm text-emerald-800">
        <strong>Cost factor:</strong> spending is tracked by category across the selected timeline.
      </div>
    </div>
  );
}

export default memo(DashboardChart);
