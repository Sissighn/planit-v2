import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import deLocale from "@fullcalendar/core/locales/de";
import { useState, useEffect } from "react";
import { HiDotsVertical, HiPlus } from "react-icons/hi";
import DeleteRecurringDialog from "../tasks/DeleteRecurringDialog";

import { getCompletedInstances } from "../../services/api.js";

// Neumorphic styles for FullCalendar
const calendarStyles = `
  /* General container */
  .fc {
    --fc-bg: #f1f5f9;
    --fc-shadow-dark: #d1d9e6;
    --fc-shadow-light: #ffffff;
    --fc-text-primary: #475569; /* slate-600 */
    --fc-text-secondary: #64748b; /* slate-500 */
    --fc-button-text: #5b21b6; /* purple-700 */
    --fc-today-bg: #f3e8ff; /* purple-100 */
    --fc-today-text: #5b21b6; /* purple-700 */
    background-color: transparent;
  }
  [data-theme="dark"] .fc {
    --fc-bg: #1e293b; /* slate-800 */
    --fc-shadow-dark: #0f172a;
    --fc-shadow-light: #334155;
    --fc-text-primary: #cbd5e1; /* slate-300 */
    --fc-text-secondary: #94a3b8; /* slate-400 */
    --fc-button-text: #a78bfa; /* purple-400 */
    --fc-today-bg: #334155; /* slate-700 */
    --fc-today-text: #c4b5fd; /* purple-300 */
  }

  /* Toolbar */
  .fc .fc-toolbar.fc-header-toolbar {
    margin-bottom: 1.5rem;
  }
  .fc .fc-toolbar-title {
    color: var(--fc-text-primary);
    font-size: 1.5rem;
    font-weight: 600;
  }

  /* Prev/Next/Today Buttons */
  .fc .fc-button {
    background: var(--fc-bg);
    border: none;
    box-shadow: 5px 5px 10px var(--fc-shadow-dark), -5px -5px 10px var(--fc-shadow-light);
    transition: all 0.2s ease-in-out;
    color: var(--fc-button-text);
    border-radius: 0.75rem;
    padding: 0.5rem 1rem;
    height: auto;
  }
  .fc .fc-button .fc-icon {
    vertical-align: middle;
  }
  .fc .fc-button:hover, .fc .fc-button:focus, .fc .fc-button:active {
    outline: none;
    box-shadow: inset 5px 5px 10px var(--fc-shadow-dark), inset -5px -5px 10px var(--fc-shadow-light);
  }
  .fc .fc-button-primary:disabled {
    opacity: 0.6;
    box-shadow: 5px 5px 10px var(--fc-shadow-dark), -5px -5px 10px var(--fc-shadow-light);
  }

  /* Day Header (MON, TUE...) */
  .fc .fc-col-header-cell {
    border: none;
    padding-bottom: 0.75rem;
  }
  .fc .fc-col-header-cell-cushion {
    color: var(--fc-text-secondary);
    text-decoration: none;
  }

  /* Table borders */
  .fc-theme-standard .fc-scrollgrid, .fc-theme-standard th, .fc-theme-standard td {
    border: none;
  }

  /* Day Grid Cells */
  .fc .fc-daygrid-day {
    background-color: transparent;
    padding: 4px;
  }
  .fc .fc-daygrid-day-frame {
    background-color: var(--fc-bg);
    border-radius: 1rem;
    box-shadow: inset 4px 4px 8px var(--fc-shadow-dark), inset -4px -4px 8px var(--fc-shadow-light);
    min-height: 90px;
    padding: 0.5rem;
    transition: all 0.2s ease-in-out;
  }

  /* Today's cell */
  .fc .fc-day-today .fc-daygrid-day-frame {
    background-color: var(--fc-today-bg);
    box-shadow: 5px 5px 10px var(--fc-shadow-dark), -5px -5px 10px var(--fc-shadow-light);
  }
  .fc .fc-day-today .fc-daygrid-day-number {
    font-weight: 700;
    color: var(--fc-today-text);
  }

  /* Day Number */
  .fc .fc-daygrid-day-number {
    color: var(--fc-text-primary);
    text-decoration: none;
  }
`;

export default function CalendarView({
  tasks = [],
  onQuickAdd,
  onQuickEdit,
  onQuickDelete,
  onQuickArchive,
  onQuickDeleteOne,
  onQuickDeleteFuture,
  onQuickDeleteSeries,
}) {
  const [events, setEvents] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [dayEvents, setDayEvents] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [openMenuIndex, setOpenMenuIndex] = useState(null);

  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [deleteTarget, setDeleteTarget] = useState(null);

  // -----------------------------------------------------
  // DATE CLICK → OPEN POPUP
  // -----------------------------------------------------
  const handleDateClick = (info) => {
    const dateStr = info.dateStr;
    const eventsOnDay = events.filter((ev) => ev.date === dateStr);

    setSelectedDate(dateStr);
    setDayEvents(eventsOnDay);
    setShowModal(true);
    setOpenMenuIndex(null);
  };

  // -----------------------------------------------------
  // BUILD EVENTS ARRAY (Calendar rendering)
  // -----------------------------------------------------
  useEffect(() => {
    if (!Array.isArray(tasks)) return;

    const today = new Date();
    const oneYearLater = new Date();
    oneYearLater.setFullYear(today.getFullYear() + 1);

    // Softer colors for neumorphism style
    const colorDone = "#a7f3d0"; // tailwind green-200
    const colorPurple = "#d8b4fe"; // tailwind purple-300
    const colorPink = "#fbcfe8"; // tailwind pink-200
    const colorYellow = "#fef08a"; // tailwind yellow-200
    const colorBlue = "#bfdbfe"; // tailwind blue-200

    async function buildEvents() {
      const completedMap = {};

      // 1) LOAD COMPLETED INSTANCES FROM BACKEND
      await Promise.all(
        tasks.map(async (t) => {
          if (t.repeatFrequency && t.repeatFrequency !== "NONE") {
            const arr = await getCompletedInstances(t.id);
            completedMap[t.id] = Array.isArray(arr) ? arr : [];
          } else {
            completedMap[t.id] = [];
          }
        })
      );

      const built = [];

      for (const t of tasks) {
        const excludedSet = new Set(
          (t.excludedDates || "")
            .split(",")
            .map((s) => s.trim())
            .filter(Boolean)
        );

        const isExcluded = (iso) => excludedSet.has(iso);

        const repeatDaysList = Array.isArray(t.repeatDays)
          ? t.repeatDays
          : (t.repeatDays || "").split(",").filter(Boolean);

        const end = t.repeatUntil ? new Date(t.repeatUntil) : oneYearLater;
        const completedDates = completedMap[t.id] || [];

        const start = t.startDate
          ? new Date(t.startDate)
          : t.deadline
          ? new Date(t.deadline)
          : new Date(today);

        // -----------------------------------------------------
        // ONE-TIME TASK
        // -----------------------------------------------------
        if (!t.repeatFrequency || t.repeatFrequency === "NONE") {
          if (t.deadline && !isExcluded(t.deadline)) {
            built.push({
              taskId: t.id,
              date: t.deadline,
              title: t.title,
              color: t.done ? colorDone : colorPurple,
              task: t,
            });
          }
          continue;
        }

        // -----------------------------------------------------
        // RECURRING TASKS
        // -----------------------------------------------------
        const freq = t.repeatFrequency;
        const interval = Number(t.repeatInterval) || 1;

        // DAILY
        if (freq === "DAILY") {
          let d = new Date(start);
          while (d <= end) {
            const iso = d.toISOString().split("T")[0];
            if (!isExcluded(iso)) {
              built.push({
                taskId: t.id,
                date: iso,
                title: t.title,
                color: completedDates.includes(iso) ? colorDone : colorPurple,
                task: t,
              });
            }
            d.setDate(d.getDate() + interval);
          }
        }

        // WEEKLY
        if (freq === "WEEKLY") {
          const dayCode = {
            MON: 1,
            TUE: 2,
            WED: 3,
            THU: 4,
            FRI: 5,
            SAT: 6,
            SUN: 0,
          };

          let d = new Date(start);

          while (d <= end) {
            const iso = d.toISOString().split("T")[0];
            const weekday = Object.keys(dayCode).find(
              (k) => dayCode[k] === d.getDay()
            );

            if (repeatDaysList.includes(weekday) && !isExcluded(iso)) {
              built.push({
                taskId: t.id,
                date: iso,
                title: t.title,
                color: completedDates.includes(iso) ? colorDone : colorPink,
                task: t,
              });
            }

            d.setDate(d.getDate() + 1);
          }
        }

        // MONTHLY
        if (freq === "MONTHLY") {
          let d = new Date(start);

          while (d <= end) {
            const iso = d.toISOString().split("T")[0];

            if (d.getDate() === start.getDate() && !isExcluded(iso)) {
              built.push({
                taskId: t.id,
                date: iso,
                title: t.title,
                color: completedDates.includes(iso) ? colorDone : colorYellow,
                task: t,
              });
            }

            d.setMonth(d.getMonth() + interval);
          }
        }

        // YEARLY
        if (freq === "YEARLY") {
          let d = new Date(start);

          while (d <= end) {
            const iso = d.toISOString().split("T")[0];
            if (!isExcluded(iso)) {
              built.push({
                taskId: t.id,
                date: iso,
                title: t.title,
                color: completedDates.includes(iso) ? colorDone : colorBlue,
                task: t,
              });
            }
            d.setFullYear(d.getFullYear() + interval);
          }
        }
      }

      setEvents(built);
    }

    buildEvents();
  }, [tasks]);

  // -----------------------------------------------------
  // DELETE LOGIC
  // -----------------------------------------------------
  const handleDeleteClick = (ev) => {
    const task = ev.task;
    const isRecurring =
      task?.repeatFrequency && task.repeatFrequency !== "NONE";

    if (!isRecurring) {
      setShowModal(false);
      setOpenMenuIndex(null);
      onQuickDelete?.(task.id);
      return;
    }

    setDeleteTarget({ task, date: ev.date });
    setShowDeleteDialog(true);
  };

  // -----------------------------------------------------
  // RENDER
  // -----------------------------------------------------
  return (
    <>
      <style>{calendarStyles}</style>
      <FullCalendar
        locale={deLocale}
        firstDay={1}
        plugins={[dayGridPlugin, interactionPlugin]}
        initialView="dayGridMonth"
        events={events}
        dateClick={handleDateClick}
        aspectRatio={1.2}
        height="auto"
        headerToolbar={{
          start: "prev,next today",
          center: "title",
          end: "",
        }}
      />

      {/* POPUP */}
      {showModal && (
        <div
          className="fixed inset-0 bg-slate-900/10 backdrop-blur-sm flex justify-center items-center z-[99999] dark:bg-slate-900/30"
          onClick={() => {
            setShowModal(false);
            setOpenMenuIndex(null);
          }}
        >
          <div
            className="bg-slate-100 rounded-3xl shadow-[8px_8px_16px_#d1d9e6,_-8px_-8px_16px_#ffffff] p-6 w-[380px] space-y-4 animate-modalPop dark:bg-slate-800 dark:shadow-[8px_8px_16px_#0f172a,_-8px_-8px_16px_#334155]"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex justify-between items-center">
              <h2 className="text-lg font-semibold text-slate-700 dark:text-slate-200">
                {selectedDate}
              </h2>

              <button
                onClick={() => {
                  setShowModal(false);
                  onQuickAdd?.(selectedDate);
                }}
                className="p-2 rounded-full bg-slate-100 text-slate-600 shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-slate-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]"
              >
                <HiPlus size={18} />
              </button>
            </div>

            {dayEvents.length === 0 && (
              <p className="text-slate-500 text-sm text-center mt-2 dark:text-slate-400">
                No tasks for this day.
              </p>
            )}

            <div className="space-y-3">
              {dayEvents.map((ev, i) => (
                <div
                  key={i}
                  className="p-3 rounded-xl bg-slate-100 shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] relative dark:bg-slate-800 dark:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]"
                >
                  <div className="flex justify-between items-start">
                    <p className="font-medium text-slate-700 text-sm dark:text-slate-300">
                      {ev.title}
                    </p>

                    <div className="relative">
                      <button
                        onClick={() =>
                          setOpenMenuIndex(openMenuIndex === i ? null : i)
                        }
                        className="p-1 hover:bg-slate-200/70 rounded-full text-slate-500 dark:text-slate-400 dark:hover:bg-slate-700"
                      >
                        <HiDotsVertical size={18} />
                      </button>

                      {openMenuIndex === i && (
                        <div className="absolute right-0 mt-2 w-32 bg-slate-100 rounded-xl shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] text-sm z-[999999] dark:bg-slate-800 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155]">
                          <button
                            onClick={() => {
                              setShowModal(false);
                              setOpenMenuIndex(null);
                              onQuickEdit?.(ev.task);
                            }}
                            className="w-full text-left px-4 py-2.5 text-slate-600 hover:bg-slate-200/70 rounded-t-xl dark:text-slate-300 dark:hover:bg-slate-700"
                          >
                            Edit
                          </button>

                          <button
                            onClick={() => {
                              setShowModal(false);
                              setOpenMenuIndex(null);
                              onQuickArchive?.(ev.task.id);
                            }}
                            className="w-full text-left px-4 py-2.5 text-slate-600 hover:bg-slate-200/70 dark:text-slate-300 dark:hover:bg-slate-700"
                          >
                            Archive
                          </button>

                          <button
                            onClick={() => handleDeleteClick(ev)}
                            className="w-full text-left px-4 py-2.5 text-red-500 hover:bg-slate-200/70 rounded-b-xl dark:hover:bg-slate-700"
                          >
                            Delete
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="flex justify-end mt-4">
              <button
                onClick={() => {
                  setShowModal(false);
                  setOpenMenuIndex(null);
                }}
                className="px-5 py-2.5 rounded-xl bg-slate-100 text-slate-700 font-medium shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] transition-all hover:shadow-[inset_5px_5px_10px_#d1d9e6,_inset_-5px_-5px_10px_#ffffff] dark:bg-slate-800 dark:text-slate-300 dark:shadow-[5px_5px_10px_#0f172a,_-5px_-5px_10px_#334155] dark:hover:shadow-[inset_5px_5px_10px_#0f172a,_inset_-5px_-5px_10px_#334155]"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* DELETE RECURRING DIALOG */}
      {showDeleteDialog && deleteTarget && (
        <DeleteRecurringDialog
          date={deleteTarget.date}
          onClose={() => {
            setShowDeleteDialog(false);
            setDeleteTarget(null);
            setOpenMenuIndex(null);
          }}
          onDeleteOne={() => {
            onQuickDeleteOne?.(deleteTarget.task, deleteTarget.date);
            setShowDeleteDialog(false);
            setShowModal(false);
            setDeleteTarget(null);
            setOpenMenuIndex(null);
          }}
          onDeleteFuture={() => {
            onQuickDeleteFuture?.(deleteTarget.task, deleteTarget.date);
            setShowDeleteDialog(false);
            setShowModal(false);
            setDeleteTarget(null);
            setOpenMenuIndex(null);
          }}
          onDeleteSeries={() => {
            onQuickDeleteSeries?.(deleteTarget.task.id);
            setShowDeleteDialog(false);
            setShowModal(false);
            setDeleteTarget(null);
            setOpenMenuIndex(null);
          }}
        />
      )}
    </>
  );
}
