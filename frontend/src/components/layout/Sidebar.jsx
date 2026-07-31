import { Archive, CalendarDays, Home, PlusCircle } from "lucide-react";
import Greeting from "../view/Greeting";
import GroupList from "../groups/GroupList";

export default function Sidebar({
  onHomeClick,
  onAddClick,
  onCalendarClick,
  onArchiveClick,
  onSelectGroup,
  activeView,
  selectedCategoryId,
}) {
  const baseButton =
    "flex items-center gap-3 text-purple-800 dark:text-purple-300 w-full transition-all duration-200 rounded-xl p-3 font-cormorant text-lg";
  const neumorphicStyle =
    "bg-slate-100 shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] hover:text-purple-600 dark:bg-slate-800 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:text-purple-400";
  const activeStyle =
    "bg-slate-100 shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] text-purple-700 font-semibold dark:bg-slate-800 dark:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155] dark:text-purple-400";

  return (
    <div
      className="w-72 bg-slate-100 min-h-screen p-6 flex flex-col gap-6 
                 border-r border-slate-200/80 dark:bg-slate-800 dark:border-slate-700/80"
    >
      <h1 className="font-ruigslay text-soft-purple text-5xl tracking-wide mb-2">
        <strong>PlanIT</strong>
      </h1>

      <Greeting />

      {/* Menu */}
      <div className="mt-4 flex flex-col items-start gap-4 border-t border-slate-200/80 pt-6 dark:border-slate-700/80">
        {/* Home */}
        <button
          onClick={onHomeClick}
          className={`${baseButton} ${
            activeView === "home" ? activeStyle : neumorphicStyle
          }`}
        >
          <Home size={22} />
          <span>Home</span>
        </button>

        {/* Add */}
        <button
          onClick={onAddClick}
          className={`${baseButton} ${neumorphicStyle}`}
        >
          <PlusCircle size={22} />
          <span>Add Task</span>
        </button>

        {/* Calendar */}
        <button
          onClick={onCalendarClick}
          className={`${baseButton} ${
            activeView === "calendar" ? activeStyle : neumorphicStyle
          }`}
          aria-current={activeView === "calendar" ? "page" : undefined}
        >
          <CalendarDays size={22} />
          <span>Calendar</span>
        </button>

        {/* Archive */}
        <button
          onClick={onArchiveClick}
          className={`${baseButton} ${
            activeView === "archive" ? activeStyle : neumorphicStyle
          }`}
        >
          <Archive size={22} />
          <span>Archive</span>
        </button>
      </div>

      {/* Groups */}
      <div className="mt-2 border-t border-slate-200/80 pt-6 dark:border-slate-700/80">
        <GroupList
          onSelectGroup={onSelectGroup}
          selectedCategoryId={selectedCategoryId}
        />
      </div>
    </div>
  );
}
