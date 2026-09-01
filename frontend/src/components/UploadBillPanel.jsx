import { useState } from 'react';
import { uploadBill } from '../api/apiClient';

export default function UploadBillPanel({ onUploadComplete }) {
  const [dragActive, setDragActive] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  async function handleFiles(fileList) {
    const file = fileList?.[0];
    if (!file) return;

    if (!['image/jpeg', 'image/png', 'application/pdf'].includes(file.type) && !/\.(jpg|jpeg|png|pdf)$/i.test(file.name)) {
      setError('Unsupported file type. Please upload JPG, PNG, or PDF.');
      return;
    }

    try {
      setUploading(true);
      setError('');
      setSuccess('');
      const result = await uploadBill(file);
      if (result?.success === false) {
        throw new Error(result?.message || 'Unable to process bill');
      }
      onUploadComplete?.(result);
      setSuccess(result?.message || 'Bill processed successfully.');
    } catch (err) {
      setError(err?.message || 'Unable to upload bill. Please try again.');
    } finally {
      setUploading(false);
    }
  }

  return (
    <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-6 shadow-sm">
      <div
        onDragOver={(e) => {
          e.preventDefault();
          setDragActive(true);
        }}
        onDragLeave={() => setDragActive(false)}
        onDrop={(e) => {
          e.preventDefault();
          setDragActive(false);
          handleFiles(e.dataTransfer.files);
        }}
        className={`rounded-2xl border-2 border-dashed p-8 text-center transition ${
          dragActive ? 'border-indigo-500 bg-indigo-50' : 'border-slate-300 bg-slate-50'
        }`}
      >
        <p className="text-lg font-semibold text-slate-700">Upload grocery bill</p>
        <p className="mt-2 text-sm text-slate-500">Drag and drop JPG, PNG or PDF files here</p>
        <input
          type="file"
          accept=".jpg,.jpeg,.png,.pdf"
          className="mt-4 block w-full text-sm text-slate-600 file:mr-4 file:rounded-full file:border-0 file:bg-indigo-600 file:px-4 file:py-2 file:text-sm file:font-semibold file:text-white"
          onChange={(e) => handleFiles(e.target.files)}
        />
        {uploading && <p className="mt-4 text-sm text-indigo-600">Processing bill...</p>}
        {error && <p className="mt-4 text-sm text-red-600">{error}</p>}
        {success && <p className="mt-4 text-sm text-emerald-600">{success}</p>}
      </div>
    </div>
  );
}
