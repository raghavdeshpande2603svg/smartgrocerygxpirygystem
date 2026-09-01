import { memo } from 'react';

function StatCard({ title, value, tone = 'green' }) {
  const tones = {
    green: 'bg-emerald-100 text-emerald-700',
    leaf: 'bg-lime-100 text-lime-700',
    yellow: 'bg-yellow-100 text-yellow-700',
    red: 'bg-red-100 text-red-700',
    orange: 'bg-orange-100 text-orange-700',
  };

  return (
    <div className="rounded-2xl border border-emerald-200 bg-white p-5 shadow-[0_12px_30px_rgba(22,163,74,0.08)]">
      <div className="flex items-center justify-between">
        <p className="text-sm text-slate-500">{title}</p>
        <span className={`rounded-full px-2 py-1 text-xs font-semibold ${tones[tone] || tones.green}`}>
          Live
        </span>
      </div>
      <h3 className="mt-4 text-3xl font-bold text-slate-800">{value}</h3>
    </div>
  );
}

export default memo(StatCard);
