import { useState, useEffect, useRef } from "react";
import { Folder, Tag, MoreVertical, Check, Plus } from "lucide-react";
import {
  getGroups,
  addGroup,
  deleteGroup,
  updateGroup,
} from "../../services/api";
import ConfirmDialog from "../common/ConfirmDialog";

export default function GroupList({ onSelectGroup, selectedCategoryId }) {
  const [groups, setGroups] = useState([]);
  const [newGroupName, setNewGroupName] = useState("");
  const [showInput, setShowInput] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [editingName, setEditingName] = useState("");
  const [menuOpenId, setMenuOpenId] = useState(null);
  const listRef = useRef(null);
  const renameInputRef = useRef(null);
  const [confirmOpen, setConfirmOpen] = useState(false);
  const [itemToDelete, setItemToDelete] = useState(null);

  // --- Styles ---
  const inputClass = "form-control flex-1 !px-3 !py-2 text-sm";
  const neumorphicButton =
    "neo-control flex h-10 w-10 shrink-0 items-center justify-center rounded-lg text-brand";
  const menuItemClass =
    "block w-full px-4 py-2.5 text-left text-sm text-slate-700 transition-colors hover:bg-slate-200/70 dark:text-slate-200 dark:hover:bg-slate-700/70";

  // Load groups on mount
  useEffect(() => {
    getGroups()
      .then(setGroups)
      .catch((err) => console.error("Failed to load groups:", err));
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (!listRef.current) return;
      if (!listRef.current.contains(e.target)) {
        setMenuOpenId(null);
        setEditingId(null);
        setShowInput(false);
        setEditingName("");
        setNewGroupName("");
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    if (editingId && renameInputRef.current) {
      renameInputRef.current.focus();
    }
  }, [editingId]);

  // Add new group
  const handleAddGroup = async () => {
    if (!newGroupName.trim()) return;
    try {
      const created = await addGroup({ name: newGroupName });
      setGroups([...groups, created]);
      setNewGroupName("");
      setShowInput(false);
    } catch (err) {
      console.error("Error adding group:", err);
      alert("Failed to add group. See console for details.");
    }
  };

  // Delete group
  const handleDeleteRequest = (id) => {
    setItemToDelete(id);
    setConfirmOpen(true);
  };

  const confirmDelete = async () => {
    if (!itemToDelete) return;
    try {
      await deleteGroup(itemToDelete);
      setGroups(groups.filter((g) => g.id !== itemToDelete));
    } catch (err) {
      console.error("Error deleting group:", err);
      alert("Failed to delete group. See console for details.");
    } finally {
      setItemToDelete(null);
      setConfirmOpen(false);
    }
  };

  // Rename group
  const handleRename = async (id) => {
    const name = editingName.trim();
    if (!name) return;

    try {
      await updateGroup(id, { id, name });
      setGroups((prev) => prev.map((g) => (g.id === id ? { ...g, name } : g)));
      setEditingId(null);
      setEditingName("");
    } catch (err) {
      console.error("Error renaming group:", err);
      alert("Failed to rename group. See console for details.");
    }
  };

  return (
    <div ref={listRef}>
      {/* 🔹 Header */}
      <div className="flex justify-between items-center mb-3">
        <div className="flex items-center gap-2">
          <Folder size={20} className="text-brand" />
          <h2 className="text-base font-bold text-slate-700 dark:text-slate-200">
            Categories
          </h2>
        </div>

        <button
          type="button"
          className="min-h-11 rounded-lg px-2 text-sm font-semibold text-brand hover:text-violet-500"
          onClick={() => setShowInput(!showInput)}
          aria-expanded={showInput}
        >
          {showInput ? "Cancel" : "+ New"}
        </button>
      </div>

      {/* Add new group input */}
      {showInput && (
        <div className="flex gap-2 mb-4 animate-modalPop duration-300">
          <input
            type="text"
            value={newGroupName}
            onChange={(e) => setNewGroupName(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                handleAddGroup();
              }
            }}
            className={inputClass}
            placeholder="Enter the name"
            aria-label="New category name"
            autoFocus
          />
          <button
            type="button"
            onClick={handleAddGroup}
            className={neumorphicButton}
            aria-label="Add category"
          >
            <Plus size={18} />
          </button>
        </div>
      )}

      {/* Group list */}
      <ul className="space-y-2">
        {groups.map((g) => (
          <li
            key={g.id}
            className={`group flex min-w-0 items-center justify-between rounded-xl px-1 py-1 text-slate-700 transition-colors dark:text-slate-200 ${
              selectedCategoryId === g.id
                ? "bg-violet-100/70 dark:bg-violet-500/15"
                : "hover:bg-violet-100/40 dark:hover:bg-slate-700/50"
            }`}
          >
            {editingId === g.id ? (
              <div className="flex min-w-0 flex-1 items-center gap-2">
                <input
                  ref={renameInputRef}
                  value={editingName}
                  onChange={(e) => setEditingName(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") {
                      e.preventDefault();
                      handleRename(g.id);
                    } else if (e.key === "Escape") {
                      setEditingId(null);
                    }
                  }}
                  className={inputClass}
                  aria-label={`Rename ${g.name}`}
                />

                <button
                  type="button"
                  onClick={() => handleRename(g.id)}
                  className={neumorphicButton}
                  aria-label={`Save category name for ${g.name}`}
                >
                  <Check size={18} className="text-green-600" />
                </button>
              </div>
            ) : (
              <>
                <button
                  type="button"
                  onClick={() => onSelectGroup(g.id)}
                  className="flex min-h-11 min-w-0 flex-1 items-center gap-2 rounded-lg px-2 text-left"
                  aria-pressed={selectedCategoryId === g.id}
                >
                  <Tag size={16} className="shrink-0 text-brand" />
                  <span className="truncate">{g.name}</span>
                </button>

                {/* Context Menu (3 dots) */}
                <div className="relative">
                  <button
                    type="button"
                    onClick={() =>
                      setMenuOpenId(menuOpenId === g.id ? null : g.id)
                    }
                    className="flex h-11 w-11 items-center justify-center rounded-full text-muted transition-opacity hover:bg-slate-200/70 focus:opacity-100 lg:opacity-0 lg:group-hover:opacity-100 dark:hover:bg-slate-700"
                    aria-label={`Actions for ${g.name}`}
                    aria-expanded={menuOpenId === g.id}
                  >
                    <MoreVertical size={18} />
                  </button>

                  {menuOpenId === g.id && (
                    <div className="neo-surface absolute right-0 z-10 mt-1 w-32 overflow-hidden rounded-xl border border-[var(--color-border)]">
                      <button
                        type="button"
                        onClick={() => {
                          setEditingId(g.id);
                          setEditingName(g.name);
                          setMenuOpenId(null);
                        }}
                        className={`${menuItemClass} rounded-t-xl`}
                      >
                        Rename
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDeleteRequest(g.id)}
                        className={`${menuItemClass} text-red-600 rounded-b-xl`}
                      >
                        Delete
                      </button>
                    </div>
                  )}
                </div>
              </>
            )}
          </li>
        ))}
      </ul>

      <ConfirmDialog
        open={confirmOpen}
        title="Delete Category"
        message="Are you sure you want to delete this category? All tasks within it will be un-categorized."
        variant="danger"
        confirmLabel="Delete"
        onConfirm={confirmDelete}
        onCancel={() => {
          setConfirmOpen(false);
          setItemToDelete(null);
        }}
      />
    </div>
  );
}
