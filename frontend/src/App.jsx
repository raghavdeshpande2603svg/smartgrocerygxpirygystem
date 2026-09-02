import { useEffect, useMemo, useState, useCallback, useRef } from 'react';
import * as XLSX from 'xlsx';
import StatCard from './components/StatCard';
import UploadBillPanel from './components/UploadBillPanel';
import InventoryTable from './components/InventoryTable';
import DashboardChart from './components/DashboardChart';
import AddItemModal from './components/AddItemModal';
import PurchasePage from './components/PurchasePage';

const STORAGE_KEY = 'smart-grocery-inventory-v1';
const PURCHASES_STORAGE_KEY = 'smart-grocery-purchases-v1';
const STORAGE_DEBOUNCE_MS = 1000; // Write to localStorage only every 1 second

const defaultInventory = [
  { id: 1, name: 'Amul Milk', category: 'Dairy', qty: 2, cost: 90, expiry: '2026-09-04', status: 'expiring_soon', month: 'Aug' },
  { id: 2, name: 'Tomato', category: 'Vegetables', qty: 3, cost: 45, expiry: '2026-09-06', status: 'fresh', month: 'Aug' },
  { id: 3, name: 'Rice', category: 'Grains', qty: 5, cost: 120, expiry: '2026-08-28', status: 'expired', month: 'Jul' },
  { id: 4, name: 'Banana', category: 'Fruits', qty: 4, cost: 70, expiry: '2026-09-10', status: 'fresh', month: 'Sep' },
  { id: 5, name: 'Spinach', category: 'Vegetables', qty: 2, cost: 50, expiry: '2026-08-30', status: 'expiring_soon', month: 'Sep' },
  { id: 6, name: 'Cucumber', category: 'Vegetables', qty: 3, cost: 60, expiry: '2026-09-08', status: 'fresh', month: 'Aug' },
];

const monthlyPurchaseData = [
  { month: 'Jun', Vegetables: 180, Fruits: 120, Dairy: 90, Grains: 75 },
  { month: 'Jul', Vegetables: 220, Fruits: 150, Dairy: 105, Grains: 95 },
  { month: 'Aug', Vegetables: 260, Fruits: 180, Dairy: 120, Grains: 110 },
];

export default function App() {
  const [activeView, setActiveView] = useState('dashboard');
  const [uploadResult, setUploadResult] = useState(null);
  const [showOCRResult, setShowOCRResult] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [inventory, setInventory] = useState(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      return saved ? JSON.parse(saved) : defaultInventory;
    } catch {
      return defaultInventory;
    }
  });
  const storageTimeoutRef = useRef(null);

  useEffect(() => {
    // Debounce localStorage writes for better performance
    if (storageTimeoutRef.current) {
      clearTimeout(storageTimeoutRef.current);
    }
    storageTimeoutRef.current = setTimeout(() => {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(inventory));
    }, STORAGE_DEBOUNCE_MS);

    return () => {
      if (storageTimeoutRef.current) {
        clearTimeout(storageTimeoutRef.current);
      }
    };
  }, [inventory]);

  const dashboardStats = useMemo(() => {
    const totalItems = inventory.length;
    const totalCost = inventory.reduce((sum, item) => sum + (Number(item.cost) || 0), 0);
    const expiringSoon = inventory.filter((item) => item.status === 'expiring_soon').length;
    const expired = inventory.filter((item) => item.status === 'expired').length;
    const fresh = inventory.filter((item) => item.status === 'fresh').length;

    return {
      totalItems,
      totalCost,
      expiringSoon,
      expired,
      fresh,
      avgCost: totalItems ? (totalCost / totalItems).toFixed(0) : 0,
    };
  }, [inventory]);

  if (activeView === 'purchase') {
    return <PurchasePage onBack={() => setActiveView('dashboard')} />;
  }

  function addInventoryFromUpload(result) {
    const items = result?.items || [];
    if (!items.length) return;

    const mappedItems = items.map((item, index) => ({
      id: `${Date.now()}-${index}`,
      name: item.productName || item.name || 'Unknown item',
      category: item.category || 'Other',
      qty: Number(item.quantity) || 1,
      cost: Number(item.amount || item.price || item.total || 0) || Math.max((Number(item.quantity) || 1) * 30, 25),
      expiry: new Date(Date.now() + (item.defaultShelfLifeDays || 7) * 24 * 60 * 60 * 1000).toISOString().slice(0, 10),
      status: item.category === 'Dairy' ? 'expiring_soon' : item.category === 'Vegetables' ? 'fresh' : 'fresh',
      month: 'Aug',
    }));

    setInventory((prev) => {
      const merged = [...mappedItems, ...prev];
      return merged;
    });
  }

  function handleUploadComplete(result) {
    setUploadResult(result);
    addInventoryFromUpload(result);
  }

  function handleManualAdd(item) {
    const itemToSave = {
      ...item,
      cost: Number(item.cost) || 0,
      month: item.month || 'Aug',
    };

    setInventory((prev) => [itemToSave, ...prev]);
  }

  function handleDeleteItem(id) {
    setInventory((prev) => prev.filter((item) => item.id !== id));
  }

  function handleExportInventory() {
    const blob = new Blob([JSON.stringify(inventory, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = 'smart-grocery-inventory.json';
    anchor.click();
    URL.revokeObjectURL(url);
  }

  function handleResetInventory() {
    setInventory(defaultInventory);
  }

  function handleBackupInventory() {
    let purchaseData = null;
    try {
      const savedPurchases = localStorage.getItem(PURCHASES_STORAGE_KEY);
      purchaseData = savedPurchases ? JSON.parse(savedPurchases) : null;
    } catch {
      purchaseData = null;
    }

    const vendors = purchaseData?.vendors || [];
    const orders = purchaseData?.orders || [];
    const vendorNames = new Map(vendors.map((vendor) => [vendor.id, vendor.name]));
    const workbook = XLSX.utils.book_new();

    XLSX.utils.book_append_sheet(workbook, XLSX.utils.json_to_sheet(inventory), 'Inventory');
    XLSX.utils.book_append_sheet(workbook, XLSX.utils.json_to_sheet(vendors), 'Vendors');
    XLSX.utils.book_append_sheet(workbook, XLSX.utils.json_to_sheet(orders.map((order) => ({
      ...order,
      vendorName: vendorNames.get(order.vendorId) || 'Unknown vendor',
    }))), 'Purchase Orders');
    XLSX.utils.book_append_sheet(workbook, XLSX.utils.json_to_sheet([{
      exportedAt: new Date().toISOString(),
      inventoryItems: inventory.length,
      vendors: vendors.length,
      purchaseOrders: orders.length,
    }]), 'Backup Info');

    XLSX.writeFile(workbook, 'smart-grocery-backup.xlsx');
  }

  function handleImportInventory(event) {
    const file = event.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = () => {
      try {
        const parsed = JSON.parse(String(reader.result));
        const importedItems = Array.isArray(parsed) ? parsed : parsed.items || [];
        if (!Array.isArray(importedItems) || !importedItems.length) return;
        setInventory(importedItems);
        if (!Array.isArray(parsed) && parsed.purchaseData) {
          localStorage.setItem(PURCHASES_STORAGE_KEY, JSON.stringify(parsed.purchaseData));
        }
      } catch {
        // Ignore invalid file content.
      }
    };
    reader.readAsText(file);
    event.target.value = '';
  }

  return (
    <div className="min-h-screen overflow-x-hidden bg-[radial-gradient(circle_at_top,_#f6fff2,_#eefaf0_30%,_#edf7ef_100%)] p-3 text-slate-800 sm:p-6">
      <div className="mx-auto max-w-7xl">
        <header className="mb-6 flex flex-col items-start gap-4 rounded-[28px] border border-emerald-200 bg-white/80 p-4 shadow-[0_18px_45px_rgba(34,197,94,0.08)] backdrop-blur-sm sm:flex-row sm:items-center sm:justify-between sm:p-5">
          <div className="min-w-0">
            <p className="text-sm font-semibold uppercase tracking-[0.28em] text-emerald-700">Smart Grocery</p>
            <h1 className="mt-2 text-2xl font-bold text-slate-800 sm:text-3xl">Expiry Management Dashboard</h1>
          </div>
          <div className="flex w-full flex-wrap items-center gap-2 sm:w-auto sm:justify-end">
            <button
              type="button"
              onClick={() => setActiveView('purchase')}
              className="rounded-xl border border-teal-200 bg-teal-50 px-3 py-2 text-xs font-semibold text-teal-800 transition hover:bg-teal-100"
            >
              Purchase
            </button>
            <button
              type="button"
              onClick={handleExportInventory}
              className="rounded-xl border border-emerald-200 bg-white px-3 py-2 text-xs font-semibold text-emerald-700 transition hover:bg-emerald-50"
            >
              Export
            </button>
            <button
              type="button"
              onClick={handleBackupInventory}
              className="rounded-xl border border-amber-200 bg-amber-50 px-3 py-2 text-xs font-semibold text-amber-700 transition hover:bg-amber-100"
            >
              Backup
            </button>
            <label className="cursor-pointer rounded-xl border border-sky-200 bg-sky-50 px-3 py-2 text-xs font-semibold text-sky-700 transition hover:bg-sky-100">
              Import
              <input type="file" accept="application/json" className="hidden" onChange={handleImportInventory} />
            </label>
            <button
              type="button"
              onClick={handleResetInventory}
              className="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-xs font-semibold text-red-700 transition hover:bg-red-100"
            >
              Reset
            </button>
            <button
              type="button"
              onClick={() => setIsModalOpen(true)}
              className="w-full rounded-xl bg-gradient-to-r from-emerald-600 to-lime-500 px-4 py-2.5 text-sm font-semibold text-white shadow-lg shadow-emerald-500/20 transition hover:translate-y-[-1px] hover:shadow-xl sm:w-auto"
            >
              + Add Item
            </button>
          </div>
        </header>

        <section className="mb-6 grid grid-cols-2 gap-3 sm:gap-4 md:grid-cols-4">
          <StatCard title="Total Items" value={String(dashboardStats.totalItems)} tone="green" />
          <StatCard title="Monthly Spend" value={`₹${dashboardStats.totalCost}`} tone="leaf" />
          <StatCard title="Expiring Soon" value={String(dashboardStats.expiringSoon)} tone="yellow" />
          <StatCard title="Avg Cost" value={`₹${dashboardStats.avgCost}`} tone="orange" />
        </section>

        <section className="grid gap-6 md:grid-cols-[1.2fr_0.8fr]">
          <UploadBillPanel onUploadComplete={handleUploadComplete} />
          <div className="rounded-2xl border border-emerald-200 bg-white p-5 shadow-sm">
            <h3 className="mb-4 text-lg font-semibold text-slate-800">Notifications</h3>
            <ul className="space-y-3 text-sm text-slate-600">
              <li className="rounded-xl bg-yellow-50 p-3 text-yellow-700">Milk expires in 2 days</li>
              <li className="rounded-xl bg-red-50 p-3 text-red-700">Spinach expired yesterday</li>
              <li className="rounded-xl bg-emerald-50 p-3 text-emerald-700">Tomatoes are still fresh</li>
            </ul>
            <div className="mt-5 rounded-xl bg-emerald-50 p-3 text-sm text-emerald-800">
              <p className="font-semibold">Budget snapshot</p>
              <p className="mt-1">This month’s spend: ₹{dashboardStats.totalCost}</p>
            </div>
            {uploadResult && (
              <div className="mt-5 rounded-xl bg-slate-50 p-3 text-xs text-slate-700">
                <button 
                  type="button"
                  onClick={() => setShowOCRResult(!showOCRResult)}
                  className="font-semibold text-slate-800 hover:text-slate-900 cursor-pointer"
                >
                  {showOCRResult ? '▼' : '▶'} OCR Result ({uploadResult?.items?.length || 0} items)
                </button>
                {showOCRResult && (
                  <pre className="mt-2 whitespace-pre-wrap text-xs overflow-auto max-h-60">{JSON.stringify(uploadResult, null, 2)}</pre>
                )}
              </div>
            )}
          </div>
        </section>

        <section className="mt-6 grid gap-6 md:grid-cols-[1.3fr_0.7fr]">
          <InventoryTable items={inventory} onDeleteItem={handleDeleteItem} />
          <DashboardChart data={monthlyPurchaseData} />
        </section>
      </div>

      <AddItemModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSubmit={handleManualAdd} />
    </div>
  );
}
