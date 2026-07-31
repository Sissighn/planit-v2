import {
  useCallback,
  useEffect,
  useState,
  forwardRef,
  useImperativeHandle,
} from "react";
import TaskList from "../tasks/TaskList";
import AddTaskDialog from "../tasks/AddTaskDialog";
import EditTaskDialog from "../tasks/EditTaskDialog";
import Dashboard from "./Dashboard";

import {
  getTasks,
  addTask,
  updateTask,
  deleteTask,
  archiveTask,
  getArchivedTasks,
  markInstanceCompleted,
  deleteOneOccurrence,
  deleteFutureOccurrences,
  deleteSeries,
} from "../../services/api.js";

const AppContent = forwardRef(function AppContent(
  { onTasksUpdate, selectedCategoryId },
  ref
) {
  const [tasks, setTasks] = useState([]);
  const [selectedTaskId, setSelectedTaskId] = useState(null);
  const [editingTask, setEditingTask] = useState(null);
  const [showAddDialog, setShowAddDialog] = useState(false);
  const [showEditDialog, setShowEditDialog] = useState(false);
  const [presetDate, setPresetDate] = useState(null);
  const [showArchive, setShowArchive] = useState(false);
  const [archivedCount, setArchivedCount] = useState(0);

  // ---------------------------------------------------------
  // LOAD TASKS
  // ---------------------------------------------------------
  const fetchTasksState = useCallback(async (archived = false) => {
    try {
      const data = archived ? await getArchivedTasks() : await getTasks();
      setTasks(data);
      onTasksUpdate?.(data);
    } catch (e) {
      console.error("Failed to load tasks:", e);
    }
  }, [onTasksUpdate]);

  const fetchArchivedCount = useCallback(async () => {
    try {
      const data = await getArchivedTasks();
      setArchivedCount(data.length);
    } catch (error) {
      console.error("Failed to load archived task count:", error);
    }
  }, []);

  useEffect(() => {
    fetchTasksState(false);
    fetchArchivedCount();
  }, [fetchArchivedCount, fetchTasksState]);

  // ---------------------------------------------------------
  // PUBLIC METHODS FOR CALENDAR VIEW
  // ---------------------------------------------------------
  useImperativeHandle(ref, () => ({
    openAdd: () => {
      setPresetDate(null);
      setShowAddDialog(true);
    },
    quickAdd: (date) => {
      setPresetDate(date);
      setShowAddDialog(true);
    },
    quickEdit: (task) => {
      setEditingTask(task);
      setShowEditDialog(true);
    },
    quickDelete: async (id) => {
      await handleDelete(id);
    },
    quickArchive: async (id) => {
      await handleArchive(id);
    },
    showArchiveView: async () => {
      setShowArchive(true);
      await fetchTasksState(true);
    },
    showHomeView: async () => {
      setShowArchive(false);
      await fetchTasksState(false);
    },
    quickDeleteOne: (task, date) => quickDeleteOne(task, date),
    quickDeleteFuture: (task, date) => quickDeleteFuture(task, date),
    quickDeleteSeries: (id) => quickDeleteSeries(id),
    isArchiveView: () => showArchive,
  }));

  // ---------------------------------------------------------
  // ADD
  // ---------------------------------------------------------
  const handleAdd = async (taskData) => {
    try {
      await addTask(taskData);
      await fetchTasksState(showArchive);
      setShowAddDialog(false);
      setPresetDate(null);
    } catch (error) {
      console.error("Failed to add task:", error);
      throw error;
    }
  };

  // ---------------------------------------------------------
  // EDIT
  // ---------------------------------------------------------
  const handleEdit = async (updatedTask) => {
    try {
      await updateTask(updatedTask.id, updatedTask, "PUT");
      await fetchTasksState(showArchive);
      setShowEditDialog(false);
      setEditingTask(null);
    } catch (error) {
      console.error("Failed to edit task:", error);
      throw error;
    }
  };

  // ---------------------------------------------------------
  // ARCHIVE
  // ---------------------------------------------------------
  const handleArchive = async (id) => {
    try {
      await archiveTask(id);
      await fetchTasksState();
      await fetchArchivedCount();
    } catch (error) {
      console.error("Failed to archive task:", error);
    }
  };

  // ---------------------------------------------------------
  // DELETE
  // ---------------------------------------------------------
  const handleDelete = async (id) => {
    try {
      await deleteTask(id);
      await fetchTasksState(showArchive);
      if (selectedTaskId === id) setSelectedTaskId(null);
    } catch (error) {
      console.error("Failed to delete task:", error);
    }
  };

  // ---------------------------------------------------------
  // DELETE ONE OCCURRENCE
  // ---------------------------------------------------------
  const quickDeleteOne = async (task, date) => {
    try {
      await deleteOneOccurrence(task.id, date);
      await fetchTasksState(showArchive);
    } catch (error) {
      console.error("Failed to delete task occurrence:", error);
    }
  };

  // ---------------------------------------------------------
  // DELETE FUTURE OCCURRENCES
  // ---------------------------------------------------------
  const quickDeleteFuture = async (task, date) => {
    try {
      await deleteFutureOccurrences(task.id, date);
      await fetchTasksState(showArchive);
    } catch (error) {
      console.error("Failed to delete future task occurrences:", error);
    }
  };

  // ---------------------------------------------------------
  // DELETE WHOLE SERIES
  // ---------------------------------------------------------
  const quickDeleteSeries = async (id) => {
    try {
      await deleteSeries(id);
      await fetchTasksState(showArchive);
    } catch (error) {
      console.error("Failed to delete recurring task series:", error);
    }
  };

  // ---------------------------------------------------------
  // TOGGLE DONE ✅ KORRIGIERT
  // ---------------------------------------------------------
  const handleToggle = async (id) => {
    const task = tasks.find((t) => t.id === id);
    if (!task) return;

    try {
      // ⭐ RECURRING TASK → mark instance done
      if (task.repeatFrequency && task.repeatFrequency !== "NONE") {
        // Use nextOccurrence if available, otherwise today
        const dateToComplete =
          task.nextOccurrence || new Date().toISOString().split("T")[0];

        await markInstanceCompleted(task.id, dateToComplete);
      }
      // ⭐ ONE-TIME TASK → toggle done via PUT
      else {
        const updatedDone = !task.done;
        await updateTask(task.id, { done: updatedDone }, "PUT");
      }

      // ⭐ ALWAYS refresh tasks afterwards
      await fetchTasksState(showArchive);
    } catch (e) {
      console.error("Toggle error:", e);
    }
  };

  // ---------------------------------------------------------
  // DASHBOARD STATS
  // ---------------------------------------------------------
  const visibleTasks = tasks.filter((task) => {
    const belongsToCurrentView = showArchive ? task.archived : !task.archived;
    const belongsToCategory = selectedCategoryId
      ? task.groupId === Number(selectedCategoryId)
      : true;

    return belongsToCurrentView && belongsToCategory;
  });

  const stats = !showArchive
    ? {
        dueToday: tasks.filter((t) => {
          if (t.archived) return false;
          const today = new Date().toISOString().split("T")[0];

          if (t.repeatFrequency && t.repeatFrequency !== "NONE") {
            return t.nextOccurrence === today;
          }

          return t.deadline && t.deadline.startsWith(today);
        }).length,

        completedThisWeek: tasks.filter((t) => {
          if (!t.done || t.archived) return false;
          const updated = new Date(t.updatedAt || Date.now());
          const now = new Date();
          const weekAgo = new Date(now.setDate(now.getDate() - 7));
          return updated >= weekAgo;
        }).length,

        archived: archivedCount,
      }
    : null;

  // ---------------------------------------------------------
  // RENDER
  // ---------------------------------------------------------
  return (
    <div>
      <div className="max-w-3xl mx-auto w-full flex flex-col gap-8">
        {stats && <Dashboard stats={stats} />}

        <TaskList
          tasks={visibleTasks}
          onToggle={handleToggle}
          onDelete={handleDelete}
          onArchive={handleArchive}
          onEdit={handleEdit}
          onSelect={setSelectedTaskId}
          selectedTask={selectedTaskId}
        />
      </div>

      {/* ADD DIALOG */}
      {showAddDialog && (
        <AddTaskDialog
          onAdd={handleAdd}
          onClose={() => setShowAddDialog(false)}
          presetDate={presetDate}
        />
      )}

      {/* EDIT DIALOG */}
      {showEditDialog && editingTask && (
        <EditTaskDialog
          task={editingTask}
          onEdit={handleEdit}
          onClose={() => {
            setShowEditDialog(false);
            setEditingTask(null);
          }}
        />
      )}
    </div>
  );
});

export default AppContent;
