import DialogShell from "./DialogShell";

export default function ConfirmDialog({
  open,
  title = "Confirm action",
  message,
  confirmLabel = "Confirm",
  cancelLabel = "Cancel",
  variant = "primary",
  onConfirm,
  onCancel,
}) {
  if (!open) return null;

  return (
    <DialogShell
      onClose={onCancel}
      labelledBy="confirmation-dialog-title"
      describedBy="confirmation-dialog-message"
      panelClassName="max-w-sm p-5 sm:p-8"
    >
      <div className="space-y-5 sm:space-y-6">
        <h2
          id="confirmation-dialog-title"
          className="text-center text-xl font-bold text-slate-700 sm:text-2xl dark:text-slate-100"
        >
          {title}
        </h2>
        <p
          id="confirmation-dialog-message"
          className="text-center leading-relaxed text-slate-600 dark:text-slate-300"
        >
          {message}
        </p>

        <div className="flex flex-col-reverse gap-3 pt-2 sm:flex-row sm:justify-end">
          <button
            type="button"
            onClick={onCancel}
            className="button-base button-secondary"
          >
            {cancelLabel}
          </button>
          <button
            type="button"
            onClick={onConfirm}
            className={`button-base ${
              variant === "danger" ? "button-danger" : "button-primary"
            }`}
          >
            {confirmLabel}
          </button>
        </div>
      </div>
    </DialogShell>
  );
}
