# Design system

PlanIT uses a restrained neumorphic visual language built on a shared set of CSS variables and component classes in `frontend/src/index.css`. Components should consume these primitives instead of defining individual colors or shadow values.

## Principles

- Use one surface color per theme and communicate hierarchy through spacing, borders, and restrained shadows.
- Reserve violet for navigation state, focus, and primary actions.
- Use Lucide icons consistently. Avoid mixing icon libraries or emoji with interface controls.
- Use DMS for interface text and Ruigslay only for the PlanIT wordmark.
- Maintain a minimum interactive target size of 44 by 44 pixels.
- Every interaction must work with touch, keyboard, and pointer input.

## Responsive layout

The interface supports a minimum viewport width of 320 pixels.

| Range | Layout behavior |
| --- | --- |
| Base | Single-column content, navigation drawer, stacked actions, bottom-aligned dialogs |
| `sm` (640 px) | Three dashboard cards, horizontal dialog actions, larger spacing |
| `lg` (1024 px) | Persistent 288 px sidebar and desktop header |
| `2xl` (1536 px) | Task content and calendar share the main content row |

Do not introduce fixed component widths without a fluid `width: 100%` and an appropriate `max-width`. Use `min-width: 0` on flex and grid children that contain user-generated text.

## Shared primitives

| Class | Purpose |
| --- | --- |
| `neo-surface` | Raised content surface |
| `neo-inset` | Recessed content surface or selected state |
| `neo-control` | Interactive raised control with pressed states |
| `form-control` | Inputs and selects |
| `field-label` | Form labels |
| `button-base` | Shared button dimensions and typography |
| `button-primary` | Primary actions |
| `button-secondary` | Neutral actions |
| `button-danger` | Destructive actions |
| `icon-button` | Accessible square icon control |
| `dialog-overlay` | Modal backdrop and viewport containment |
| `dialog-panel` | Responsive modal surface |

## Themes

Theme values live in `:root` and `html.dark`. The theme switch applies the `.dark` class, updates `data-theme`, persists the selection in local storage, and updates the browser theme color. New components must use the shared variables or include both light and dark Tailwind variants.

## Accessibility

- Associate every form label with its control.
- Label icon-only buttons with `aria-label`.
- Expose active navigation with `aria-current` and toggle state with `aria-pressed` or `aria-expanded`.
- Use the shared dialog shell for modal semantics, Escape handling, scroll locking, focus containment, and focus restoration.
- Preserve visible keyboard focus and reduced-motion behavior.

## Verification matrix

Review every main view and dialog in both themes at these representative sizes:

- 360 × 800: compact phone
- 768 × 1024: tablet portrait
- 1024 × 768: tablet landscape / small desktop
- 1440 × 900: standard desktop
- 1920 × 1080: wide desktop

The CI suite verifies behavior, linting, and the production build. Visual changes should additionally be reviewed in a real browser at the viewport matrix above.
