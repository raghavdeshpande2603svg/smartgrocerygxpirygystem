import { useMemo, useState } from 'react';
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';

const PURCHASES_KEY = 'smart-grocery-purchases-v1';
const today = new Date().toISOString().slice(0, 10);

const starterData = {
  vendors: [
    { id: 'v1', name: 'Green Valley Farms', contact: 'Raj Kumar', phone: '+91 98765 43210', billPhoto: '' },
    { id: 'v2', name: 'Daily Dairy Co.', contact: 'Meera Shah', phone: '+91 99887 66554', billPhoto: '' },
  ],
  orders: [
    { id: 'po-1008', vendorId: 'v1', date: today, amount: 1840, status: 'Received', item: 'Vegetables and fruits', qty: 24, unit: 'kg', billPhoto: '' },
    { id: 'po-1007', vendorId: 'v2', date: today, amount: 1260, status: 'Received', item: 'Milk and paneer', qty: 18, unit: 'packets', billPhoto: '' },
    { id: 'po-1006', vendorId: 'v1', date: '2026-08-20', amount: 980, status: 'Pending', item: 'Rice and grains', qty: 12, unit: 'kg', billPhoto: '' },
  ],
};

function readPurchases() {
  try {
    return JSON.parse(localStorage.getItem(PURCHASES_KEY)) || starterData;
  } catch {
    return starterData;
  }
}

function currency(value) {
  return `₹${Number(value || 0).toLocaleString('en-IN')}`;
}

export default function PurchasePage({ onBack }) {
  const [authenticated, setAuthenticated] = useState(false);
  const [credentials, setCredentials] = useState({ id: '', password: '' });
  const [loginError, setLoginError] = useState('');
  const [data, setData] = useState(readPurchases);
  const [selectedVendorId, setSelectedVendorId] = useState(data.vendors[0]?.id || '');
  const [period, setPeriod] = useState('month');
  const [showVendorForm, setShowVendorForm] = useState(false);
  const [vendorForm, setVendorForm] = useState({ name: '', contact: '', phone: '' });
  const [orderForm, setOrderForm] = useState({ item: '', qty: '', unit: 'kg', amount: '', date: today, status: 'Received' });
  const [notice, setNotice] = useState('');

  const selectedVendor = data.vendors.find((vendor) => vendor.id === selectedVendorId) || data.vendors[0];
  const vendorOrders = data.orders.filter((order) => order.vendorId === selectedVendor?.id);

  const totals = useMemo(() => {
    const now = new Date();
    const filtered = data.orders.filter((order) => {
      const orderDate = new Date(order.date);
      if (period === 'day') return order.date === today;
      if (period === 'year') return orderDate.getFullYear() === now.getFullYear();
      return orderDate.getFullYear() === now.getFullYear() && orderDate.getMonth() === now.getMonth();
    });
    return { amount: filtered.reduce((sum, order) => sum + Number(order.amount || 0), 0), count: filtered.length };
  }, [data.orders, period]);

  const vendorChart = useMemo(() => data.vendors.map((vendor) => ({
    name: vendor.name.length > 16 ? `${vendor.name.slice(0, 16)}...` : vendor.name,
    amount: data.orders.filter((order) => order.vendorId === vendor.id).reduce((sum, order) => sum + Number(order.amount || 0), 0),
  })), [data]);

  function persist(nextData) {
    setData(nextData);
    localStorage.setItem(PURCHASES_KEY, JSON.stringify(nextData));
  }

  function handleLogin(event) {
    event.preventDefault();
    if (credentials.id === 'admin' && credentials.password === 'admin') {
      setAuthenticated(true);
      setLoginError('');
    } else {
      setLoginError('Use the provided admin credentials to continue.');
    }
  }

  function handleAddVendor(event) {
    event.preventDefault();
    if (!vendorForm.name.trim()) return;
    const vendor = { ...vendorForm, id: `v-${Date.now()}`, billPhoto: '' };
    persist({ ...data, vendors: [...data.vendors, vendor] });
    setSelectedVendorId(vendor.id);
    setVendorForm({ name: '', contact: '', phone: '' });
    setShowVendorForm(false);
    setNotice('Vendor profile created.');
  }

  function handleBillPhoto(event) {
    const file = event.target.files?.[0];
    if (!file || !selectedVendor) return;
    const reader = new FileReader();
    reader.onload = () => {
      const vendors = data.vendors.map((vendor) => vendor.id === selectedVendor.id ? { ...vendor, billPhoto: String(reader.result) } : vendor);
      persist({ ...data, vendors });
      setNotice('Bill photo saved to this vendor profile.');
    };
    reader.readAsDataURL(file);
    event.target.value = '';
  }

  function handleAddOrder(event) {
    event.preventDefault();
    if (!selectedVendor || !orderForm.item.trim() || !orderForm.amount) return;
    const order = { ...orderForm, id: `po-${Date.now()}`, vendorId: selectedVendor.id, amount: Number(orderForm.amount), qty: Number(orderForm.qty || 0), billPhoto: selectedVendor.billPhoto };
    persist({ ...data, orders: [order, ...data.orders] });
    setOrderForm({ item: '', qty: '', unit: 'kg', amount: '', date: today, status: 'Received' });
    setNotice('Purchase order added to the vendor ledger.');
  }

  if (!authenticated) {
    return (
      <main className="min-h-screen overflow-x-hidden bg-[#102a2a] px-4 py-6 text-slate-100 sm:px-6 sm:py-10">
        <div className="mx-auto flex min-h-[80vh] max-w-md items-center">
          <form onSubmit={handleLogin} className="w-full border border-teal-800 bg-[#173737] p-5 shadow-2xl shadow-teal-950/40 sm:p-8">
            <button type="button" onClick={onBack} className="mb-8 text-sm text-teal-300 hover:text-white sm:mb-12">← Back to dashboard</button>
            <p className="text-xs font-bold uppercase tracking-[0.3em] text-amber-300">Purchase control</p>
            <h1 className="mt-3 text-3xl font-semibold tracking-tight sm:text-4xl">Vendor ledger</h1>
            <p className="mt-3 text-sm leading-6 text-teal-100/70">Manage supplier profiles, bill evidence, purchase orders, and spend analysis in one place.</p>
            <label className="mt-8 block text-sm text-teal-100/80">Admin ID<input value={credentials.id} onChange={(event) => setCredentials({ ...credentials, id: event.target.value })} className="mt-2 w-full border border-teal-700 bg-[#102a2a] px-4 py-3 text-white outline-none focus:border-amber-300" /></label>
            <label className="mt-4 block text-sm text-teal-100/80">Password<input type="password" value={credentials.password} onChange={(event) => setCredentials({ ...credentials, password: event.target.value })} className="mt-2 w-full border border-teal-700 bg-[#102a2a] px-4 py-3 text-white outline-none focus:border-amber-300" /></label>
            {loginError && <p className="mt-3 text-sm text-rose-300">{loginError}</p>}
            <button type="submit" className="mt-6 w-full bg-amber-300 px-4 py-3 font-bold text-[#102a2a] transition hover:bg-amber-200">Open Purchase</button>
            <p className="mt-5 text-center text-xs text-teal-100/50">Demo access: admin / admin</p>
          </form>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen overflow-x-hidden bg-[#f3f6f1] px-3 py-4 text-slate-800 sm:px-6 sm:py-5 lg:px-10">
      <div className="mx-auto max-w-7xl">
        <header className="flex flex-wrap items-end justify-between gap-4 border-b border-slate-300 pb-6">
          <div className="min-w-0"><button type="button" onClick={onBack} className="mb-5 text-sm font-semibold text-teal-700 hover:text-teal-900">← Dashboard</button><p className="text-xs font-bold uppercase tracking-[0.28em] text-teal-700">Procurement workspace</p><h1 className="mt-2 text-3xl font-semibold tracking-tight sm:text-4xl">Purchase</h1><p className="mt-2 text-slate-500">A clear view of who you buy from and where the money goes.</p></div>
          <button type="button" onClick={() => setAuthenticated(false)} className="w-full border border-slate-300 bg-white px-4 py-2 text-sm font-semibold hover:bg-slate-50 sm:w-auto">Lock section</button>
        </header>

        <section className="mt-6 grid gap-4 md:grid-cols-3">
          {[['Spend today', data.orders.filter((order) => order.date === today).reduce((sum, order) => sum + Number(order.amount || 0), 0)], ['Spend this month', period === 'month' ? totals.amount : data.orders.filter((order) => new Date(order.date).getMonth() === new Date().getMonth()).reduce((sum, order) => sum + Number(order.amount || 0), 0)], ['Spend this year', data.orders.filter((order) => new Date(order.date).getFullYear() === new Date().getFullYear()).reduce((sum, order) => sum + Number(order.amount || 0), 0)]].map(([label, value]) => <div key={label} className="border border-slate-200 bg-white p-5"><p className="text-sm text-slate-500">{label}</p><p className="mt-2 text-3xl font-semibold text-teal-900">{currency(value)}</p></div>)}
        </section>

        <section className="mt-6 grid gap-6 md:grid-cols-[280px_1fr]">
          <aside className="border border-slate-200 bg-white p-4">
            <div className="flex items-center justify-between"><h2 className="font-semibold">Vendors</h2><button type="button" onClick={() => setShowVendorForm(!showVendorForm)} className="text-xl text-teal-700" title="Add vendor">+</button></div>
            {showVendorForm && <form onSubmit={handleAddVendor} className="mt-4 space-y-2 border-y border-slate-200 py-4"><input required placeholder="Vendor name" value={vendorForm.name} onChange={(event) => setVendorForm({ ...vendorForm, name: event.target.value })} className="w-full border border-slate-300 px-3 py-2 text-sm" /><input placeholder="Contact person" value={vendorForm.contact} onChange={(event) => setVendorForm({ ...vendorForm, contact: event.target.value })} className="w-full border border-slate-300 px-3 py-2 text-sm" /><input placeholder="Phone" value={vendorForm.phone} onChange={(event) => setVendorForm({ ...vendorForm, phone: event.target.value })} className="w-full border border-slate-300 px-3 py-2 text-sm" /><button className="w-full bg-teal-800 px-3 py-2 text-sm font-semibold text-white">Save vendor</button></form>}
            <div className="mt-4 space-y-2">{data.vendors.map((vendor) => <button type="button" key={vendor.id} onClick={() => setSelectedVendorId(vendor.id)} className={`w-full border-l-4 p-3 text-left ${selectedVendor?.id === vendor.id ? 'border-amber-400 bg-amber-50' : 'border-transparent bg-slate-50 hover:bg-slate-100'}`}><p className="font-semibold">{vendor.name}</p><p className="mt-1 text-xs text-slate-500">{vendor.contact || 'No contact added'}</p></button>)}</div>
          </aside>

          {selectedVendor && <div className="space-y-6">
            <section className="grid gap-6 lg:grid-cols-[1fr_300px]">
              <div className="border border-slate-200 bg-white p-4 sm:p-6"><div className="flex flex-wrap items-start justify-between gap-4"><div className="min-w-0"><p className="text-xs font-bold uppercase tracking-[0.2em] text-teal-700">Vendor profile</p><h2 className="mt-2 break-words text-2xl font-semibold">{selectedVendor.name}</h2><p className="mt-2 break-words text-sm text-slate-500">{selectedVendor.contact || 'Contact not added'} · {selectedVendor.phone || 'Phone not added'}</p></div><label className="w-full cursor-pointer bg-teal-800 px-4 py-2 text-center text-sm font-semibold text-white hover:bg-teal-700 sm:w-auto">Upload bill photo<input type="file" accept="image/*" className="hidden" onChange={handleBillPhoto} /></label></div><div className="mt-6 grid gap-4 sm:grid-cols-3"><div className="bg-slate-50 p-4"><p className="text-xs text-slate-500">Vendor total</p><p className="mt-1 text-xl font-semibold">{currency(vendorOrders.reduce((sum, order) => sum + Number(order.amount || 0), 0))}</p></div><div className="bg-slate-50 p-4"><p className="text-xs text-slate-500">Purchase orders</p><p className="mt-1 text-xl font-semibold">{vendorOrders.length}</p></div><div className="bg-slate-50 p-4"><p className="text-xs text-slate-500">Latest purchase</p><p className="mt-1 text-xl font-semibold">{vendorOrders[0]?.date || 'None'}</p></div></div></div>
              <div className="flex min-h-[190px] items-center justify-center overflow-hidden border border-slate-200 bg-slate-100">{selectedVendor.billPhoto ? <img src={selectedVendor.billPhoto} alt={`${selectedVendor.name} bill`} className="h-full max-h-64 w-full object-contain" /> : <p className="px-6 text-center text-sm text-slate-500">No bill photo attached to this profile.</p>}</div>
            </section>

            <section className="border border-slate-200 bg-white p-4 sm:p-6"><div className="flex flex-wrap items-center justify-between gap-3"><div><h2 className="text-xl font-semibold">Manual stock taken form</h2><p className="mt-1 text-sm text-slate-500">Create a purchase order and keep the stock receipt tied to this vendor.</p></div>{notice && <p className="text-sm font-semibold text-teal-700">{notice}</p>}</div><form onSubmit={handleAddOrder} className="mt-5 grid gap-3 md:grid-cols-6"><input required placeholder="Product or stock taken" value={orderForm.item} onChange={(event) => setOrderForm({ ...orderForm, item: event.target.value })} className="min-w-0 border border-slate-300 px-3 py-2 text-sm md:col-span-2" /><input type="number" min="0" placeholder="Qty" value={orderForm.qty} onChange={(event) => setOrderForm({ ...orderForm, qty: event.target.value })} className="min-w-0 border border-slate-300 px-3 py-2 text-sm" /><select value={orderForm.unit} onChange={(event) => setOrderForm({ ...orderForm, unit: event.target.value })} className="min-w-0 border border-slate-300 px-3 py-2 text-sm"><option>kg</option><option>litres</option><option>packets</option><option>pcs</option></select><input required type="number" min="0" placeholder="Amount" value={orderForm.amount} onChange={(event) => setOrderForm({ ...orderForm, amount: event.target.value })} className="min-w-0 border border-slate-300 px-3 py-2 text-sm" /><input type="date" value={orderForm.date} onChange={(event) => setOrderForm({ ...orderForm, date: event.target.value })} className="min-w-0 border border-slate-300 px-3 py-2 text-sm" /><button type="submit" className="w-full bg-amber-400 px-4 py-2 text-sm font-bold text-slate-900 hover:bg-amber-300 md:col-span-6 md:w-auto md:justify-self-end">Add purchase order</button></form></section>

            <section className="grid gap-6 lg:grid-cols-[1.1fr_0.9fr]"><div className="overflow-hidden border border-slate-200 bg-white"><div className="border-b border-slate-200 p-5"><h2 className="text-xl font-semibold">Purchase orders</h2></div><div className="overflow-x-auto"><table className="min-w-full text-left text-sm"><thead className="bg-slate-50 text-slate-500"><tr><th className="px-5 py-3">Order</th><th className="px-5 py-3">Date</th><th className="px-5 py-3">Stock</th><th className="px-5 py-3">Amount</th></tr></thead><tbody>{vendorOrders.map((order) => <tr key={order.id} className="border-t border-slate-100"><td className="px-5 py-3 font-semibold">{order.id}</td><td className="px-5 py-3 text-slate-500">{order.date}</td><td className="px-5 py-3">{order.item} <span className="text-slate-400">({order.qty} {order.unit})</span></td><td className="px-5 py-3 font-semibold">{currency(order.amount)}</td></tr>)}</tbody></table></div></div><div className="border border-slate-200 bg-white p-5"><div className="flex flex-wrap items-center justify-between gap-3"><h2 className="text-xl font-semibold">Spend analysis</h2><div className="flex gap-1 bg-slate-100 p-1">{['day', 'month', 'year'].map((item) => <button type="button" key={item} onClick={() => setPeriod(item)} className={`px-3 py-1 text-xs font-semibold capitalize ${period === item ? 'bg-teal-800 text-white' : 'text-slate-600'}`}>{item}</button>)}</div></div><p className="mt-4 text-3xl font-semibold text-teal-900">{currency(totals.amount)} <span className="text-sm font-normal text-slate-500">/ {totals.count} orders</span></p><div className="mt-4 h-56"><ResponsiveContainer width="100%" height="100%"><BarChart data={vendorChart} margin={{ left: 0, right: 12 }}><CartesianGrid strokeDasharray="3 3" stroke="#dbe4df" /><XAxis dataKey="name" tick={{ fontSize: 10 }} /><YAxis tick={{ fontSize: 10 }} /><Tooltip formatter={(value) => [currency(value), 'Spend']} /><Legend /><Bar dataKey="amount" name="Purchase amount" fill="#0f766e" /></BarChart></ResponsiveContainer></div></div></section>
          </div>}
        </section>
      </div>
    </main>
  );
}
