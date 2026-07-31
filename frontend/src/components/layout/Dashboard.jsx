export default function Dashboard({ stats }) {
  // Neumorphism works best when the element and the background are the same color.
  // Ensure the parent container of this component has a background like `bg-slate-100`.
  const neumorphicCardStyle =
    "flex flex-col items-center justify-center text-center p-4 rounded-3xl bg-slate-100 shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] transition-shadow duration-300 hover:shadow-[inset_8px_8px_16px_#d1d9e6,_inset_-8px_-8px_16px_#ffffff] dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155] dark:hover:shadow-[inset_8px_8px_16px_#0f172a,_inset_-8px_-8px_16px_#334155]";

  return (
    <div className="w-full mb-6">
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        {/* Tasks due today */}
        <div className={neumorphicCardStyle}>
          <span className="text-3xl mb-1">📅</span>
          <p className="text-lg font-cormorant font-bold text-slate-700 dark:text-slate-200">
            <strong>{stats.dueToday} tasks</strong>
          </p>
          <p className="text-sm text-slate-500 mt-1 dark:text-slate-400">
            Due today
          </p>
        </div>

        {/* Completed this week */}
        <div className={neumorphicCardStyle}>
          <span className="text-3xl mb-1">✅</span>
          <p className="text-lg font-cormorant font-bold text-slate-700 dark:text-slate-200">
            <strong>{stats.completedThisWeek} done</strong>
          </p>
          <p className="text-sm text-slate-500 mt-1 dark:text-slate-400">
            This week
          </p>
        </div>

        {/* Archived tasks */}
        <div className={neumorphicCardStyle}>
          <span className="text-3xl mb-1">🗂️</span>
          <p className="text-lg font-cormorant font-bold text-slate-700 dark:text-slate-200">
            <strong>{stats.archived} archived</strong>
          </p>
          <p className="text-sm text-slate-500 mt-1 dark:text-slate-400">
            Total archived
          </p>
        </div>
      </div>
    </div>
  );
}
