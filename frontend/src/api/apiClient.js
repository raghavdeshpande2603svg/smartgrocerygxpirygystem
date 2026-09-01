// Detect API base URL from current host for mobile/network access
const getAPIBaseURL = () => {
  if (import.meta.env.VITE_API_BASE_URL) {
    return import.meta.env.VITE_API_BASE_URL;
  }
  // Use the same host as the frontend but with port 8080
  const protocol = window.location.protocol;
  const hostname = window.location.hostname;
  return `${protocol}//${hostname}:8080/api`;
};

const API_BASE_URL = getAPIBaseURL();

export async function uploadBill(file) {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('userId', '1');

  const response = await fetch(`${API_BASE_URL}/bills/upload`, {
    method: 'POST',
    body: formData,
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok || data.success === false) {
    throw new Error(data.message || 'Upload failed');
  }

  return data;
}

export async function saveInventoryItem(item) {
  const response = await fetch(`${API_BASE_URL}/inventory`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      userId: 1,
      name: item.name,
      category: item.category,
      qty: item.qty,
      unit: item.unit || 'pcs',
      cost: item.cost || 0,
      expiry: item.expiry,
      status: item.status || 'fresh',
    }),
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok || data.success === false) {
    throw new Error(data.message || 'Save failed');
  }

  return data;
}

export async function getInventory(status = '') {
  const url = status ? `${API_BASE_URL}/inventory?status=${status}` : `${API_BASE_URL}/inventory`;
  const response = await fetch(url);
  if (!response.ok) {
    throw new Error('Inventory fetch failed');
  }
  return response.json();
}

export async function getDashboardSummary() {
  const response = await fetch(`${API_BASE_URL}/dashboard/summary`);
  if (!response.ok) {
    throw new Error('Dashboard summary fetch failed');
  }
  return response.json();
}
