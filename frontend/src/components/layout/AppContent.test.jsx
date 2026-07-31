import React, { createRef } from "react";
import { act, render, screen, waitFor } from "@testing-library/react";
import AppContent from "./AppContent";
import { getArchivedTasks, getTasks } from "../../services/api.js";

jest.mock("../../services/api.js", () => ({
  getTasks: jest.fn(),
  addTask: jest.fn(),
  updateTask: jest.fn(),
  deleteTask: jest.fn(),
  archiveTask: jest.fn(),
  getArchivedTasks: jest.fn(),
  markInstanceCompleted: jest.fn(),
  deleteOneOccurrence: jest.fn(),
  deleteFutureOccurrences: jest.fn(),
  deleteSeries: jest.fn(),
}));

describe("AppContent", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    getArchivedTasks.mockResolvedValue([]);
  });

  test("filters the task list by the selected category", async () => {
    getTasks.mockResolvedValue([
      { id: "task-1", title: "Work task", groupId: 1, archived: false },
      { id: "task-2", title: "Private task", groupId: 2, archived: false },
    ]);

    render(<AppContent selectedCategoryId={1} />);

    expect(await screen.findByText("Work task")).toBeInTheDocument();
    expect(screen.queryByText("Private task")).not.toBeInTheDocument();
  });

  test("shows archived tasks in the archive view", async () => {
    const ref = createRef();
    getTasks.mockResolvedValue([]);
    getArchivedTasks.mockResolvedValue([
      { id: "archived-1", title: "Archived task", archived: true },
    ]);

    render(<AppContent ref={ref} />);
    await waitFor(() => expect(ref.current).not.toBeNull());

    await act(async () => {
      await ref.current.showArchiveView();
    });

    expect(await screen.findByText("Archived task")).toBeInTheDocument();
  });
});
