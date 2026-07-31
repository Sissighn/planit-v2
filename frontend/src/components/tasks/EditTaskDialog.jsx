import { useState, useEffect } from "react";
import { createPortal } from "react-dom";
import RepeatSection from "./RepeatSection";
import { getGroups } from "../../services/api.js";

export default function EditTaskDialog({ task, onEdit, onClose }) {
  // ------------------------------
  // State
  // ------------------------------
  const [title, setTitle] = useState("");
  const [deadline, setDeadline] = useState("");
  const [time, setTime] = useState("");
  const [priority, setPriority] = useState("");
  const [groups, setGroups] = useState([]);
  const [groupId, setGroupId] = useState("");

  const [repeatFrequency, setRepeatFrequency] = useState("NONE");
  const [repeatDays, setRepeatDays] = useState([]);
  const [repeatInterval, setRepeatInterval] = useState(1);
  const [repeatUntil, setRepeatUntil] = useState("");

  const [startDate, setStartDate] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    getGroups()
      .then(setGroups)
      .catch((error) => console.error("Failed to load groups:", error));
  }, []);

  // ------------------------------
  // Load data from task
  // ------------------------------
  useEffect(() => {
    if (!task) return;

    setTitle(task.title || "");
    setDeadline(task.deadline || "");
    setTime(task.time || "");
    setPriority(task.priority || "");
    setGroupId(task.groupId?.toString() || "");

    setRepeatFrequency(task.repeatFrequency || "NONE");
    setRepeatDays(
      task.repeatDays
        ? Array.isArray(task.repeatDays)
          ? task.repeatDays
          : task.repeatDays.split(",")
        : []
    );

    setRepeatInterval(task.repeatInterval || 1);
    setRepeatUntil(task.repeatUntil || "");
    setStartDate(task.startDate || "");
  }, [task]);

  // ------------------------------
  // Submit handler
  // ------------------------------
  const handleSubmit = async (e) => {
    e.preventDefault();
    setErrorMessage("");
    setIsSubmitting(true);

    try {
      await onEdit({
        ...task,
        title,
        deadline: deadline || null,
        time: time || null,
        priority: priority || null,
        groupId: groupId || null,
        repeatFrequency,
        repeatDays: repeatDays.join(","),
        repeatInterval,
        repeatUntil: repeatUntil || null,
        startDate: repeatFrequency !== "NONE" ? startDate || null : null,
      });
    } catch (error) {
      setErrorMessage(error.message || "The task could not be saved.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // ------------------------------
  // Styles
  // ------------------------------
  const inputClass =
    "w-full px-4 py-2.5 rounded-xl bg-slate-100 text-slate-700 shadow-[inset_3px_3px_7px_#d1d9e6,_inset_-3px_-3px_7px_#ffffff] focus:outline-none focus:ring-2 focus:ring-purple-400 transition-shadow duration-200 " +
    "dark:bg-slate-800 dark:text-slate-300 dark:shadow-[inset_3px_3px_7px_#0f172a,_inset_-3px_-3px_7px_#334155]";

  const labelClass = "text-sm font-medium text-purple-700 dark:text-purple-400";

  const secondaryButton =
    "px-5 py-2.5 rounded-xl bg-slate-100 text-purple-800 font-medium shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-purple-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]";

  const primaryButton =
    "px-5 py-2.5 rounded-xl bg-purple-500 text-white font-medium shadow-[5px_5px_10px_#d1d9e6] hover:bg-purple-600 transition-all dark:bg-purple-600 dark:shadow-[5px_5px_10px_#0f172a] dark:hover:bg-purple-700";

  // ------------------------------
  // Dialog
  // ------------------------------
  const dialog = (
    <div
      className="fixed inset-0 z-[9999] flex items-center justify-center 
                 bg-slate-900/10 backdrop-blur-sm dark:bg-slate-900/30"
      onClick={(e) => e.target === e.currentTarget && onClose()}
    >
      <form
        onSubmit={handleSubmit}
        className="bg-slate-100 rounded-3xl shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] w-[560px] max-h-[85vh] flex flex-col overflow-hidden animate-modalPop dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]"
        onClick={(e) => e.stopPropagation()}
      >
        {/* HEADER */}
        <div className="px-8 py-6 border-b border-slate-200/80 dark:border-slate-700/80">
          <h2 className="text-2xl font-semibold text-purple-700 text-center dark:text-purple-400">
            Edit Task
          </h2>
        </div>

        {/* CONTENT */}
        <div className="px-8 py-6 space-y-4 overflow-y-auto">
          {/* Title */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className={inputClass}
              required
            />
          </div>

          {/* Deadline */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Deadline</label>
            <input
              type="date"
              value={deadline}
              onChange={(e) => setDeadline(e.target.value)}
              className={inputClass}
            />
          </div>

          {/* Time */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Time</label>
            <input
              type="time"
              value={time}
              onChange={(e) => setTime(e.target.value)}
              className={inputClass}
            />
          </div>

          {/* Priority */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Priority</label>
            <select
              value={priority}
              onChange={(e) => setPriority(e.target.value)}
              className={inputClass}
            >
              <option value="">No priority</option>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>

          {/* Group */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Group</label>
            <select
              value={groupId}
              onChange={(e) => setGroupId(e.target.value)}
              className={inputClass}
            >
              <option value="">No group</option>
              {groups.map((group) => (
                <option key={group.id} value={group.id}>
                  {group.name}
                </option>
              ))}
            </select>
          </div>

          {/* Repeat section */}
          <RepeatSection
            repeatFrequency={repeatFrequency}
            setRepeatFrequency={setRepeatFrequency}
            repeatDays={repeatDays}
            setRepeatDays={setRepeatDays}
            repeatInterval={repeatInterval}
            setRepeatInterval={setRepeatInterval}
            repeatUntil={repeatUntil}
            setRepeatUntil={setRepeatUntil}
          />

          {/* Start date */}
          {repeatFrequency !== "NONE" && (
            <div className="flex flex-col gap-1">
              <label className={labelClass}>Start date</label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className={inputClass}
              />
            </div>
          )}
        </div>

        {/* FOOTER */}
        <div className="px-8 py-4 border-t border-slate-200/80 flex justify-end gap-4 dark:border-slate-700/80">
          {errorMessage && (
            <p role="alert" className="mr-auto text-sm text-red-600">
              {errorMessage}
            </p>
          )}
          <button type="button" onClick={onClose} className={secondaryButton}>
            Cancel
          </button>

          <button
            type="submit"
            className={primaryButton}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Saving…" : "Save"}
          </button>
        </div>
      </form>
    </div>
  );

  return createPortal(dialog, document.body);
}
