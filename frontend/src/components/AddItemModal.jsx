import { useMemo, useState } from 'react';

const defaultForm = {
  name: '',
  category: 'Dairy',
  quantity: '1',
  unit: 'pcs',
  cost: '0',
  expiryDate: '',
  status: 'fresh',
};

export default function AddItemModal({ isOpen, onClose, onSubmit }) {
  const [form, setForm] = useState(defaultForm);

  const today = useMemo(() => new Date().toISOString().slice(0, 10), []);

  function handleChange(event) {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    if (!form.name.trim()) return;

    onSubmit({
      id: Date.now(),
      name: form.name.trim(),
      category: form.category,
      qty: Number(form.quantity) || 1,
      unit: form.unit,
      cost: Number(form.cost) || 0,
      expiry: form.expiryDate || today,
      status: form.status,
    });

    setForm(defaultForm);
    onClose();
  }

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="w-full max-w-xl rounded-2xl bg-white p-6 shadow-xl">
        <div className="mb-5 flex items-center justify-between">
          <h3 className="text-xl font-semibold text-slate-800">Add item manually</h3>
          <button
            type="button"
            onClick={onClose}
            className="text-sm font-medium text-slate-500 hover:text-slate-700"
          >
            Close
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="mb-1 block text-sm font-medium text-slate-700">Product name</label>
            <input
              name="name"
              value={form.name}
              onChange={handleChange}
              placeholder="Milk, Onion, Rice..."
              className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
            />
          </div>

          <div className="grid gap-4 md:grid-cols-2">
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Category</label>
              <select
                name="category"
                value={form.category}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
              >
                <option value="Dairy">Dairy</option>
                <option value="Vegetables">Vegetables</option>
                <option value="Grains">Grains</option>
                <option value="Fruits">Fruits</option>
                <option value="Beverages">Beverages</option>
                <option value="Snacks">Snacks</option>
                <option value="Frozen">Frozen</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Status</label>
              <select
                name="status"
                value={form.status}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
              >
                <option value="fresh">Fresh</option>
                <option value="expiring_soon">Expiring soon</option>
                <option value="expired">Expired</option>
              </select>
            </div>
          </div>

          <div className="grid gap-4 md:grid-cols-4">
            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Quantity</label>
              <input
                type="number"
                min="1"
                step="0.1"
                name="quantity"
                value={form.quantity}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Unit</label>
              <input
                name="unit"
                value={form.unit}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Cost</label>
              <input
                type="number"
                min="0"
                step="0.01"
                name="cost"
                value={form.cost}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
              />
            </div>

            <div>
              <label className="mb-1 block text-sm font-medium text-slate-700">Expiry date</label>
              <input
                type="date"
                name="expiryDate"
                value={form.expiryDate}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-3 py-2 focus:border-indigo-500 focus:outline-none"
              />
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={onClose}
              className="rounded-xl border border-slate-300 px-4 py-2 text-sm font-semibold text-slate-600"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="rounded-xl bg-indigo-600 px-4 py-2 text-sm font-semibold text-white hover:bg-indigo-500"
            >
              Save item
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
