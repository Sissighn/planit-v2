const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";
const API_URL = `${BASE_URL}/api/tasks`;

async function jsonOrThrow(res) {
  if (!res.ok) {
    let info;
    try {
      info = await res.json();
    } catch {
      info = null;
    }
    const err = new Error(info?.message || `HTTP ${res.status}`);
    err.info = info;
    err.status = res.status;
    throw err;
  }

  // ⭐ Handle 204 No Content explicitly
  if (res.status === 204) {
    return null;
  }

  const text = await res.text();
  return text ? JSON.parse(text) : null;
}

export async function getTasks(params = {}) {
  const url = new URL(API_URL);
  Object.entries(params).forEach(([k, v]) => {
    if (v != null) url.searchParams.set(k, v);
  });
  const res = await fetch(url);
  return jsonOrThrow(res);
}

export async function addTask(task) {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(task),
  });
  return jsonOrThrow(res);
}

export async function updateTask(id, updates, method = "PUT") {
  const res = await fetch(`${API_URL}/${id}`, {
    method,
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(updates),
  });
  return jsonOrThrow(res);
}

export async function deleteTask(id) {
  const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  return jsonOrThrow(res);
}

export async function archiveTask(id) {
  const res = await fetch(`${API_URL}/${id}/archive`, { method: "POST" });
  return jsonOrThrow(res);
}

export async function getArchivedTasks() {
  const res = await fetch(`${API_URL}/archive`);
  return jsonOrThrow(res);
}

export async function clearCompleted() {
  const res = await fetch(`${API_URL}/clear-completed`, { method: "DELETE" });
  return jsonOrThrow(res);
}

export async function sortTasksBy(by = "priority") {
  const res = await fetch(`${API_URL}/sorted?by=${encodeURIComponent(by)}`);
  return jsonOrThrow(res);
}

// ------------------------------------------------------------
//  RECURRING: completed instances
// ------------------------------------------------------------

export async function getCompletedInstances(taskId) {
  const res = await fetch(`${API_URL}/${taskId}/completed-instances`);
  return jsonOrThrow(res); // returns ["2025-02-15", ...]
}

export async function markInstanceCompleted(taskId, date) {
  const res = await fetch(`${API_URL}/${taskId}/complete/${date}`, {
    method: "POST",
  });
  return jsonOrThrow(res);
}

// ------------------------------------------------------------
//  RECURRING: delete logic
// ------------------------------------------------------------

export async function deleteOneOccurrence(taskId, date) {
  const res = await fetch(`${API_URL}/${taskId}/exclude/${date}`, {
    method: "POST",
  });
  return jsonOrThrow(res);
}

export async function deleteFutureOccurrences(taskId, date) {
  const res = await fetch(`${API_URL}/${taskId}/delete-future/${date}`, {
    method: "PATCH",
  });
  return jsonOrThrow(res);
}

export async function deleteSeries(taskId) {
  const res = await fetch(`${API_URL}/${taskId}/delete-series`, {
    method: "DELETE",
  });
  return jsonOrThrow(res);
}

// ------------------------------------------------------------
//  GROUPS
// ------------------------------------------------------------

export async function getGroups() {
  const res = await fetch(`${BASE_URL}/api/groups`);
  return jsonOrThrow(res);
}

export async function addGroup(groupData) {
  const res = await fetch(`${BASE_URL}/api/groups`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(groupData),
  });
  return jsonOrThrow(res);
}

export async function updateGroup(id, groupData) {
  const res = await fetch(`${BASE_URL}/api/groups/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(groupData),
  });
  return jsonOrThrow(res);
}

export async function deleteGroup(id) {
  const res = await fetch(`${BASE_URL}/api/groups/${id}`, { method: "DELETE" });
  return jsonOrThrow(res);
}
