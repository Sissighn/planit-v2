import { createPortal } from "react-dom";

export default function DeleteRecurringDialog({
  date,
  onDeleteOne,
  onDeleteFuture,
  onDeleteSeries,
  onClose,
}) {
  const dialog = (
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center 
                 bg-slate-900/10 backdrop-blur-sm dark:bg-slate-900/30"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <div
        className="bg-slate-100 rounded-3xl shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] p-8 w-[420px] space-y-6 animate-modalPop dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 className="text-2xl font-semibold text-slate-700 text-center dark:text-slate-200">
          Delete recurring task
        </h2>

        <p className="text-slate-600 text-center dark:text-slate-400">
          This task repeats. What do you want to delete?
          <br />
          <span className="text-purple-600 font-medium dark:text-purple-400">
            {date}
          </span>
        </p>

        <div className="space-y-4">
          <button
            className="w-full px-5 py-3 rounded-xl bg-slate-100 text-slate-700 font-medium shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-slate-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]"
            onClick={onDeleteOne}
          >
            Delete only this event
          </button>

          <button
            className="w-full px-5 py-3 rounded-xl bg-slate-100 text-slate-700 font-medium shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-slate-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]"
            onClick={onDeleteFuture}
          >
            Delete this and all future events
          </button>

          <button
            className="w-full px-5 py-3 rounded-xl bg-red-500 text-white font-medium shadow-[5px_5px_10px_#d1d9e6] transition-all hover:bg-red-600 dark:shadow-[5px_5px_10px_#0f172a]"
            onClick={onDeleteSeries}
          >
            Delete entire series
          </button>
        </div>

        <div className="flex justify-center pt-2">
          <button
            className="px-5 py-2.5 rounded-xl bg-slate-100 text-slate-700 font-medium shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-slate-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]"
            onClick={onClose}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );

  return createPortal(dialog, document.body);
}
