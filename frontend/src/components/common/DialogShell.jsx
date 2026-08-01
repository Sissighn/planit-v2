import { useEffect } from "react";
import { createPortal } from "react-dom";

export default function DialogShell({
  children,
  onClose,
  labelledBy,
  describedBy,
  panelAs = "div",
  panelClassName = "",
  ...panelProps
}) {
  useEffect(() => {
    const previousOverflow = document.body.style.overflow;
    const handleKeyDown = (event) => {
      if (event.key === "Escape") onClose?.();
    };

    document.body.style.overflow = "hidden";
    document.addEventListener("keydown", handleKeyDown);

    return () => {
      document.body.style.overflow = previousOverflow;
      document.removeEventListener("keydown", handleKeyDown);
    };
  }, [onClose]);

  const Panel = panelAs;

  return createPortal(
    <div
      className="dialog-overlay"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose?.();
      }}
    >
      <Panel
        role="dialog"
        aria-modal="true"
        aria-labelledby={labelledBy}
        aria-describedby={describedBy}
        className={`dialog-panel animate-modalPop ${panelClassName}`}
        onMouseDown={(event) => event.stopPropagation()}
        {...panelProps}
      >
        {children}
      </Panel>
    </div>,
    document.body
  );
}
