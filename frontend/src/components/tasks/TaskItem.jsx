import React, { useState } from "react";
import { Archive, CheckCircle2, Pencil, Trash2 } from "lucide-react";
import ConfirmDialog from "../common/ConfirmDialog";
import EditTaskDialog from "./EditTaskDialog";

export default function TaskItem({
  task,
  isActive,
  onToggle,
  onDelete,
  onArchive,
  onEdit,
  onSelect,
}) {
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [confirmMeta, setConfirmMeta] = useState(null);
  const [editOpen, setEditOpen] = useState(false);

  const priorityColor =
    task.priority === "HIGH"
      ? "text-red-500"
      : task.priority === "LOW"
      ? "text-blue-500"
      : task.priority === "MEDIUM"
      ? "text-yellow-500"
      : "text-slate-400";

  const displayDate =
    task.repeatFrequency && task.repeatFrequency !== "NONE"
      ? task.nextOccurrence
      : task.deadline;

  const displayTime = task.time ? task.time : null;

  const isDone =
    task.repeatFrequency && task.repeatFrequency !== "NONE" ? false : task.done;

  const openConfirm = (type) => {
    setConfirmMeta({
      type,
      title: type === "delete" ? "Delete Task" : "Archive Task",
      message:
        type === "delete"
          ? "Do you really want to delete this task?"
          : "Do you want to archive this task?",
      confirmLabel: type === "delete" ? "Delete" : "Archive",
      variant: type === "delete" ? "danger" : "primary",
    });
    setConfirmOpen(true);
  };

  const baseItemStyle =
    "bg-slate-100 dark:bg-slate-800 rounded-2xl p-4 mb-3 transition-all duration-300";
  const activeItemStyle =
    "shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]";
  const doneItemStyle =
    "shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]";

  const actionButton =
    "flex-1 flex items-center justify-center gap-2 px-4 py-2.5 rounded-lg transition-all text-sm font-medium shadow-[3px_3px_6px_#d1d9e6,_-3px_-3px_6px_#ffffff] hover:shadow-[inset_3px_3px_6px_#d1d9e6,_inset_-3px_-3px_6px_#ffffff] dark:shadow-[3px_3px_6px_#0f172a,_-3px_-3px_6px_#334155] dark:hover:shadow-[inset_3px_3px_6px_#0f172a,_inset_-3px_-3px_6px_#334155]";

  return (
    <div
      className={`${baseItemStyle} ${isDone ? doneItemStyle : activeItemStyle}`}
    >
      <div className="flex justify-between items-center">
        {/* CLICK TO ACTIVATE TASK */}
        <div
          onClick={() => onSelect(task.id)}
          className="flex-1 cursor-pointer select-none pr-4"
        >
          <span
            className={`text-lg font-medium ${
              isDone
                ? "line-through text-slate-500 dark:text-slate-400"
                : "text-slate-800 dark:text-slate-200"
            }`}
          >
            {task.title}
          </span>

          {(task.priority || displayDate) && (
            <span className="ml-2 inline-flex items-center gap-2 text-slate-500 dark:text-slate-400 text-sm">
              {task.priority && (
                <span className={priorityColor}>({task.priority})</span>
              )}

              {displayDate && (
                <span className="text-slate-500">
                  {new Date(displayDate).toLocaleDateString("de-DE", {
                    day: "2-digit",
                    month: "short",
                  })}
                </span>
              )}

              {displayTime && (
                <span className="text-slate-500 dark:text-slate-400 text-sm ">
                  • {displayTime}
                </span>
              )}
            </span>
          )}
        </div>

        {/* DONE BUTTON */}
        <button
          onClick={() => onToggle(task.id)}
          aria-label={isDone ? "Mark as not done" : "Mark as done"}
          className={`p-2 rounded-full transition-all ${
            isDone
              ? "text-green-500 shadow-[inset_3px_3px_6px_#d1d9e6,_inset_-3px_-3px_6px_#ffffff] dark:shadow-[inset_3px_3px_6px_#0f172a,_inset_-3px_-3px_6px_#334155]"
              : "text-slate-400 shadow-[3px_3px_6px_#d1d9e6,_-3px_-3px_6px_#ffffff] hover:shadow-[inset_3px_3px_6px_#d1d9e6,_inset_-3px_-3px_6px_#ffffff] dark:text-slate-500 dark:shadow-[3px_3px_6px_#0f172a,_-3px_-3px_6px_#334155] dark:hover:shadow-[inset_3px_3px_6px_#0f172a,_inset_-3px_-3px_6px_#334155]"
          }`}
        >
          <CheckCircle2 size={24} />
        </button>
      </div>

      {/* ACTION PANEL */}
      <div
        className={`
          transition-all ease-in-out duration-300 overflow-hidden
          ${isActive ? "max-h-32 mt-4 opacity-100" : "max-h-0 mt-0 opacity-0"}
        `}
      >
        <div
          className="
            flex items-center gap-3 p-2
            bg-slate-100 rounded-xl
            shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]
          "
        >
          <button
            onClick={() => setEditOpen(true)}
            className={`${actionButton} text-slate-700 hover:text-purple-700 dark:text-slate-300 dark:hover:text-purple-400`}
          >
            <Pencil size={16} />
            <span>Edit</span>
          </button>

          <button
            onClick={() => openConfirm("archive")}
            className={`${actionButton} text-slate-700 hover:text-purple-700 dark:text-slate-300 dark:hover:text-purple-400`}
          >
            <Archive size={16} />
            <span>Archive</span>
          </button>

          <button
            onClick={() => openConfirm("delete")}
            className={`${actionButton} text-rose-500 hover:text-rose-700 dark:hover:text-rose-400`}
          >
            <Trash2 size={16} />
            <span>Delete</span>
          </button>
        </div>
      </div>

      {/* DIALOGS */}
      <ConfirmDialog
        open={confirmOpen}
        title={confirmMeta?.title}
        message={confirmMeta?.message}
        confirmLabel={confirmMeta?.confirmLabel}
        variant={confirmMeta?.variant}
        onConfirm={() => {
          if (confirmMeta.type === "delete") onDelete(task.id);
          if (confirmMeta.type === "archive") onArchive(task.id);
          setConfirmOpen(false);
          onSelect(null);
        }}
        onCancel={() => setConfirmOpen(false)}
      />

      {editOpen && (
        <EditTaskDialog
          task={task}
          onEdit={async (updated) => {
            await onEdit(updated);
            onSelect(null);
            setEditOpen(false);
          }}
          onClose={() => {
            setEditOpen(false);
            onSelect(null);
          }}
        />
      )}
    </div>
  );
}
