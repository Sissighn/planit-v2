import React, { useEffect, useState } from "react";
import { createPortal } from "react-dom";

export default function ConfirmDialog({
  open,
  title = "Confirm Action",
  message,
  confirmLabel = "Confirm",
  cancelLabel = "Cancel",
  variant = "primary",
  onConfirm,
  onCancel,
}) {
  const [visible, setVisible] = useState(open);

  useEffect(() => {
    if (open) setVisible(true);
  }, [open]);

  const handleClose = () => {
    setVisible(false);
    setTimeout(onCancel, 200); // wait for fade-out
  };

  if (!visible && !open) return null;

  const secondaryButton =
    "px-5 py-2.5 rounded-xl bg-slate-100 text-purple-800 font-medium shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-purple-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]";

  const confirmButtonClasses =
    variant === "danger"
      ? "bg-red-500 hover:bg-red-600"
      : "bg-purple-500 hover:bg-purple-600 dark:bg-purple-600 dark:hover:bg-purple-700";

  const dialog = (
    <div
      className={`fixed inset-0 z-[9999] flex items-center justify-center 
                  transition-opacity duration-200 
                  ${open ? "opacity-100" : "opacity-0"} 
                  bg-slate-900/10 backdrop-blur-sm dark:bg-slate-900/30`}
    >
      <div className="bg-slate-100 rounded-3xl shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] p-8 w-96 space-y-6 transition-all duration-300 animate-modalPop dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]">
        <h2 className="text-2xl font-semibold text-slate-700 text-center dark:text-slate-200">
          {title}
        </h2>
        <p className="text-slate-700 text-center leading-relaxed dark:text-slate-300">
          {message}
        </p>

        <div className="flex justify-end gap-4 pt-4">
          <button onClick={handleClose} className={secondaryButton}>
            {cancelLabel}
          </button>
          <button
            onClick={() => {
              setVisible(false);
              setTimeout(onConfirm, 200);
            }}
            className={`px-5 py-2.5 rounded-xl text-white font-medium shadow-[5px_5px_10px_#d1d9e6] transition-all ${confirmButtonClasses} dark:shadow-[5px_5px_10px_#0f172a]`}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  );

  return createPortal(dialog, document.body);
}
