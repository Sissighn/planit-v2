import { useState, useEffect, useRef } from "react";
import { Folder, Tag, MoreVertical, Check, Plus } from "lucide-react";
import {
  getGroups,
  addGroup,
  deleteGroup,
  updateGroup,
} from "../../services/api";
import ConfirmDialog from "../common/ConfirmDialog";

export default function GroupList({ onSelectGroup }) {
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
  const inputClass =
    "flex-1 p-2 rounded-lg bg-slate-100 text-slate-700 text-sm placeholder-slate-400 shadow-[inset_3px_3px_5px_#d1d9e6,_inset_-3px_-3px_5px_#ffffff] focus:outline-none focus:ring-1 focus:ring-purple-400 transition-all";
  const neumorphicButton =
    "p-2 rounded-lg bg-slate-100 text-purple-800 shadow-[3px_3px_6px_#d1d9e6,_-3px_-3px_6px_#ffffff] transition-all hover:shadow-[inset_3px_3px_6px_#d1d9e6,_inset_-3px_-3px_6px_#ffffff] active:shadow-[inset_3px_3px_6px_#d1d9e6,_inset_-3px_-3px_6px_#ffffff]";
  const menuItemClass =
    "block w-full text-left px-4 py-2.5 text-sm text-slate-700 hover:bg-slate-200/70";

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
          <Folder size={20} className="text-purple-800" />
          <h2 className="font-cormorant text-slate-700 text-lg">Categories</h2>
        </div>

        <button
          className="text-purple-600 text-sm font-semibold hover:text-purple-800"
          onClick={() => setShowInput(!showInput)}
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
            autoFocus
          />
          <button onClick={handleAddGroup} className={neumorphicButton}>
            <Plus size={18} />
          </button>
        </div>
      )}

      {/* Group list */}
      <ul className="space-y-2">
        {groups.map((g) => (
          <li
            key={g.id}
            className="group flex justify-between items-center px-2 py-1 rounded-md hover:bg-purple-100/40 text-slate-700 transition-all"
          >
            {editingId === g.id ? (
              <div className="flex items-center gap-2 flex-1">
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
                />

                <button
                  onClick={() => handleRename(g.id)}
                  className={neumorphicButton}
                >
                  <Check size={18} className="text-green-600" />
                </button>
              </div>
            ) : (
              <>
                <div
                  onClick={() => onSelectGroup(g.id)}
                  className="flex items-center gap-2 flex-1 cursor-pointer"
                >
                  <Tag size={16} className="text-purple-700" />
                  <span>{g.name}</span>
                </div>

                {/* Context Menu (3 dots) */}
                <div className="relative">
                  <button
                    onClick={() =>
                      setMenuOpenId(menuOpenId === g.id ? null : g.id)
                    }
                    className="p-1 rounded-full text-slate-500 hover:bg-slate-200/70 opacity-0 group-hover:opacity-100 transition-opacity"
                  >
                    <MoreVertical size={18} />
                  </button>

                  {menuOpenId === g.id && (
                    <div className="absolute right-0 mt-1 w-32 bg-slate-100 rounded-xl shadow-[5px_5px_10px_#d1d9e6,_-5px_-5px_10px_#ffffff] z-10">
                      <button
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
