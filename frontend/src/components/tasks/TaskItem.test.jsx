/**
 * @jest-environment jsdom
 */
import React from "react";
import { render, fireEvent, screen } from "@testing-library/react";
import TaskItem from "./TaskItem";

jest.mock("../../services/api.js", () => ({
  getGroups: jest.fn().mockResolvedValue([]),
}));

function createTask(overrides = {}) {
  return {
    id: "1",
    title: "Test Task",
    priority: "MEDIUM",
    done: false,
    repeatFrequency: "NONE",
    deadline: "2025-01-01",
    ...overrides,
  };
}

describe("TaskItem Component", () => {
  test("renders task title and priority", () => {
    const task = createTask();
    render(
      <TaskItem
        task={task}
        isActive={false}
        onToggle={() => {}}
        onDelete={() => {}}
        onArchive={() => {}}
        onEdit={() => {}}
        onSelect={() => {}}
      />
    );

    expect(screen.getByText("Test Task")).toBeInTheDocument();
    expect(screen.getByText("(MEDIUM)")).toBeInTheDocument();
  });

  test("calls onSelect when task is clicked", () => {
    const task = createTask();
    const onSelect = jest.fn();

    render(
      <TaskItem
        task={task}
        isActive={false}
        onToggle={() => {}}
        onDelete={() => {}}
        onArchive={() => {}}
        onEdit={() => {}}
        onSelect={onSelect}
      />
    );

    fireEvent.click(screen.getByText("Test Task"));
    expect(onSelect).toHaveBeenCalledWith("1");
  });

  test("calls onToggle when done button is clicked", () => {
    const task = createTask();
    const onToggle = jest.fn();

    render(
      <TaskItem
        task={task}
        isActive={false}
        onToggle={onToggle}
        onDelete={() => {}}
        onArchive={() => {}}
        onEdit={() => {}}
        onSelect={() => {}}
      />
    );

    const toggleButton = screen.getByRole("button", { name: "Mark as done" });
    fireEvent.click(toggleButton);

    expect(onToggle).toHaveBeenCalledWith("1");
  });

  test("shows action panel when isActive=true", () => {
    const task = createTask();

    render(
      <TaskItem
        task={task}
        isActive={true}
        onToggle={() => {}}
        onDelete={() => {}}
        onArchive={() => {}}
        onEdit={() => {}}
        onSelect={() => {}}
      />
    );

    // Look for Edit, Archive, Delete buttons
    expect(screen.getByText("Edit")).toBeInTheDocument();
    expect(screen.getByText("Archive")).toBeInTheDocument();
    expect(screen.getByText("Delete")).toBeInTheDocument();
  });

  test("clicking delete opens confirmation dialog", () => {
    const task = createTask();

    render(
      <TaskItem
        task={task}
        isActive={true}
        onToggle={() => {}}
        onDelete={() => {}}
        onArchive={() => {}}
        onEdit={() => {}}
        onSelect={() => {}}
      />
    );

    fireEvent.click(screen.getByText("Delete"));

    expect(
      screen.getByText("Do you really want to delete this task?")
    ).toBeInTheDocument();
  });
});
