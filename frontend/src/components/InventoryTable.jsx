import { useMemo } from 'react';

const statusColors = {
  fresh: 'bg-emerald-100 text-emerald-700',
  expiring_soon: 'bg-yellow-100 text-yellow-700',
  expired: 'bg-red-100 text-red-700',
};

function InventoryTableComponent({ items = [], onDeleteItem }) {
  const rows = items.length ? items : [];
  
  // Show only first 50 items on mobile for better performance, all on desktop
  const isMobile = typeof window !== 'undefined' && window.innerWidth < 768;
  const displayItems = isMobile ? rows.slice(0, 50) : rows;

  return (
    <div className="overflow-hidden rounded-2xl border border-emerald-200 bg-white shadow-sm">
      <div className="border-b border-emerald-100 bg-gradient-to-r from-emerald-50 to-lime-50 px-5 py-4">
        <h3 className="text-lg font-semibold text-slate-800">Inventory</h3>
      </div>
      <div className="overflow-x-auto">
        <table className="min-w-full text-left text-sm">
          <thead className="bg-emerald-50 text-emerald-800">
            <tr>
              <th className="px-5 py-3 font-medium">Product</th>
              <th className="px-5 py-3 font-medium">Category</th>
              <th className="px-5 py-3 font-medium">Qty</th>
              <th className="px-5 py-3 font-medium">Cost</th>
              <th className="px-5 py-3 font-medium">Expiry</th>
              <th className="px-5 py-3 font-medium">Status</th>
              <th className="px-5 py-3 font-medium text-right">Action</th>
            </tr>
          </thead>
          <tbody>
            {displayItems.map((item) => (
              <tr key={item.id} className="border-t border-slate-200 hover:bg-emerald-50/40">
                <td className="px-5 py-3 text-slate-800 font-medium">{item.name}</td>
                <td className="px-5 py-3 text-slate-600">{item.category}</td>
                <td className="px-5 py-3 text-slate-600">{item.qty}</td>
                <td className="px-5 py-3 text-slate-600">₹{item.cost || 0}</td>
                <td className="px-5 py-3 text-slate-600">{item.expiry}</td>
                <td className="px-5 py-3">
                  <span className={`inline-flex rounded-full px-2.5 py-1 text-xs font-semibold capitalize ${statusColors[item.status] || statusColors.fresh}`}>
                    {item.status?.replace('_', ' ') || 'fresh'}
                  </span>
                </td>
                <td className="px-5 py-3 text-right">
                  <button
                    type="button"
                    onClick={() => onDeleteItem?.(item.id)}
                    className="rounded-lg border border-red-200 bg-red-50 px-2.5 py-1.5 text-xs font-semibold text-red-600 transition hover:bg-red-100"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {rows.length > displayItems.length && (
          <div className="px-5 py-3 text-xs text-slate-500 text-center bg-slate-50 border-t border-slate-200">
            Showing {displayItems.length} of {rows.length} items
          </div>
        )}
      </div>
    </div>
  );
}

export default function InventoryTable(props) {
  return <InventoryTableComponent {...props} />;
}
