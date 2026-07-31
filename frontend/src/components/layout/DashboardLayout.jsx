import { useRef, useState } from "react";
import Sidebar from "./Sidebar";
import AppContent from "./AppContent";
import CalendarView from "../view/CalendarView";
import ThemeSwitch from "../common/ThemeSwitch";

export default function DashboardLayout() {
  const appRef = useRef(null);
  const [activeView, setActiveView] = useState("home");

  // Global State – wird NUR von AppContent aktualisiert
  const [tasks, setTasks] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(null);

  // Filter für Kategorien
  const visibleTasks = selectedCategoryId
    ? tasks.filter((t) => t.groupId === Number(selectedCategoryId))
    : tasks;

  // Sidebar actions
  const handleAddClick = () => appRef.current?.openAdd?.();

  const handleHomeClick = async () => {
    await appRef.current?.showHomeView?.();
    setActiveView("home");
  };

  const handleArchiveClick = async () => {
    await appRef.current?.showArchiveView?.();
    setActiveView("archive");
  };

  const handleCalendarClick = async () => {
    await appRef.current?.showHomeView?.();
    setActiveView("calendar");
  };

  const handleSelectGroup = async (groupId) => {
    // Allow toggling selection
    setSelectedCategoryId((prev) => (prev === groupId ? null : groupId));
    // Switch to home view if a category is selected and we are not there
    if (activeView !== "home") {
      await appRef.current?.showHomeView?.();
      setActiveView("home");
    }
  };

  return (
    <div className="flex min-h-screen">
      <Sidebar
        onAddClick={handleAddClick}
        onArchiveClick={handleArchiveClick}
        onHomeClick={handleHomeClick}
        onCalendarClick={handleCalendarClick}
        onSelectGroup={handleSelectGroup}
        activeView={activeView}
        selectedCategoryId={selectedCategoryId}
      />

      {/* Main area */}
      <div className="flex-1 flex flex-col bg-slate-100 dark:bg-slate-800">
        <header className="p-6 flex justify-between items-center bg-slate-100 shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155]">
          <div>
            <h1 className="text-3xl font-dms text-slate-700 tracking-wide dark:text-slate-200">
              <strong>Dashboard Overview</strong>
            </h1>
            <p className="text-slate-500 mt-1 dark:text-slate-400">
              {activeView === "archive"
                ? "Viewing archived tasks"
                : activeView === "calendar"
                ? "Calendar view"
                : selectedCategoryId
                ? "Tasks in selected category"
                : "Your active tasks at a glance"}
            </p>
          </div>
          <ThemeSwitch />
        </header>

        <main className="flex-1 p-8 space-y-8">
          {/* Home + Archive */}
          <div
            className={
              activeView === "calendar"
                ? "hidden"
                : "grid gap-6 grid-cols-1 lg:grid-cols-[2fr_1.3fr] items-start"
            }
          >
            <div className="relative">
              <AppContent
                ref={appRef}
                onTasksUpdate={setTasks}
                selectedCategoryId={selectedCategoryId}
              />
            </div>

            {activeView === "home" && (
              <div className="bg-slate-100 rounded-3xl shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] p-6 w-full sm:min-h-[400px] transition-all dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]">
                <CalendarView
                  tasks={visibleTasks}
                  onQuickAdd={(date) => appRef.current?.quickAdd(date)}
                  onQuickEdit={(task) => appRef.current?.quickEdit(task)}
                  onQuickDelete={(id) => appRef.current?.quickDelete(id)}
                  onQuickArchive={(id) => appRef.current?.quickArchive(id)}
                  onQuickDeleteOne={(t, d) =>
                    appRef.current?.quickDeleteOne(t, d)
                  }
                  onQuickDeleteFuture={(t, d) =>
                    appRef.current?.quickDeleteFuture(t, d)
                  }
                  onQuickDeleteSeries={(id) =>
                    appRef.current?.quickDeleteSeries(id)
                  }
                />
              </div>
            )}
          </div>

          {/* Fullscreen calendar */}
          {activeView === "calendar" && (
            <div className="bg-slate-100 rounded-3xl shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] p-6 w-full min-h-[650px] dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]">
              <CalendarView
                tasks={visibleTasks}
                onQuickAdd={(date) => appRef.current?.quickAdd(date)}
                onQuickEdit={(task) => appRef.current?.quickEdit(task)}
                onQuickDelete={(id) => appRef.current?.quickDelete(id)}
                onQuickArchive={(id) => appRef.current?.quickArchive(id)}
                onQuickDeleteOne={(t, d) =>
                  appRef.current?.quickDeleteOne(t, d)
                }
                onQuickDeleteFuture={(t, d) =>
                  appRef.current?.quickDeleteFuture(t, d)
                }
                onQuickDeleteSeries={(id) =>
                  appRef.current?.quickDeleteSeries(id)
                }
              />
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
