import { fireEvent, render, screen } from "@testing-library/react";
import DialogShell from "./DialogShell";

describe("DialogShell", () => {
  test("exposes dialog semantics and closes with Escape", () => {
    const onClose = jest.fn();
    const { unmount } = render(
      <DialogShell onClose={onClose} labelledBy="dialog-title">
        <h2 id="dialog-title">Example dialog</h2>
        <button type="button">Continue</button>
      </DialogShell>
    );

    expect(
      screen.getByRole("dialog", { name: "Example dialog" })
    ).toHaveAttribute("aria-modal", "true");
    expect(document.body.style.overflow).toBe("hidden");

    fireEvent.keyDown(document, { key: "Escape" });
    expect(onClose).toHaveBeenCalledTimes(1);

    unmount();
    expect(document.body.style.overflow).toBe("");
  });
});
