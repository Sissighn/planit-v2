import { useState, useEffect, useMemo } from "react";
import TaskItem from "./TaskItem";

export default function TaskList({
  tasks,
  onToggle,
  onDelete,
  onArchive,
  onEdit,
  selectedTask,
  onSelect,
}) {
  const [activeTaskId, setActiveTaskId] = useState(null);

  const safeTasks = useMemo(
    () => (Array.isArray(tasks) ? tasks : []),
    [tasks]
  );

  useEffect(() => {
    setActiveTaskId(selectedTask ?? null);
  }, [selectedTask]);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (!e.target.closest(".task-item-container")) {
        setActiveTaskId(null);
        onSelect?.(null);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [onSelect]);

  useEffect(() => {
    if (!safeTasks.some((t) => t.id === activeTaskId)) {
      setActiveTaskId(null);
      onSelect?.(null);
    }
  }, [safeTasks, activeTaskId, onSelect]);

  return (
    <div
      className="bg-slate-100 rounded-3xl p-6 
                 shadow-[inset_8px_8px_16px_#d1d9e6,_inset_-8px_-8px_16px_#ffffff] dark:bg-slate-800 
                 dark:shadow-[inset_8px_8px_16px_#0f172a,_inset_-8px_-8px_16px_#334155] min-h-[200px] flex flex-col justify-center"
    >
      {safeTasks.length > 0 ? (
        safeTasks
          .slice()
          .sort((a, b) => {
            const da =
              a.repeatFrequency && a.repeatFrequency !== "NONE"
                ? a.nextOccurrence
                : a.deadline;

            const db =
              b.repeatFrequency && b.repeatFrequency !== "NONE"
                ? b.nextOccurrence
                : b.deadline;

            if (!da && !db) return 0;
            if (!da) return 1;
            if (!db) return -1;

            return da.localeCompare(db);
          })
          .map((task) => (
            <div key={task.id} className="task-item-container">
              <TaskItem
                task={task}
                isActive={activeTaskId === task.id}
                onToggle={onToggle}
                onDelete={(id) => {
                  onDelete(id);
                  setActiveTaskId(null);
                  onSelect?.(null);
                }}
                onArchive={(id) => {
                  onArchive(id);
                  setActiveTaskId(null);
                  onSelect?.(null);
                }}
                onEdit={onEdit}
                onSelect={(id) => {
                  const newId = activeTaskId === id ? null : id;
                  setActiveTaskId(newId);
                  onSelect?.(newId);
                }}
              />
            </div>
          ))
      ) : (
        <p className="text-center text-slate-500 dark:text-slate-400">
          No tasks here. Add one to get started!
        </p>
      )}
    </div>
  );
}
