export default function RepeatSection({
  repeatFrequency,
  setRepeatFrequency,
  repeatDays,
  setRepeatDays,
  repeatInterval,
  setRepeatInterval,
  repeatUntil,
  setRepeatUntil,
}) {
  const toggleDay = (day) => {
    setRepeatDays((prev) =>
      prev.includes(day) ? prev.filter((d) => d !== day) : [...prev, day]
    );
  };

  const inputClass =
    "w-full px-4 py-2.5 rounded-xl bg-slate-100 text-slate-700 shadow-[inset_3px_3px_7px_#d1d9e6,_inset_-3px_-3px_7px_#ffffff] focus:outline-none focus:ring-2 focus:ring-purple-400 transition-shadow duration-200 " +
    "dark:bg-slate-800 dark:text-slate-300 dark:shadow-[inset_3px_3px_7px_#0f172a,_inset_-3px_-3px_7px_#334155]";

  const labelClass = "text-sm font-medium text-purple-700 dark:text-purple-400";

  const dayButtonBase =
    "px-3 py-1.5 rounded-lg text-sm font-medium transition-all";
  const dayButtonActive =
    "bg-slate-100 text-purple-600 shadow-[inset_3px_3px_6px_#d1d9e6,_inset_-3px_-3px_6px_#ffffff] dark:bg-slate-800 dark:text-purple-400 dark:shadow-[inset_3px_3px_6px_#0f172a,_inset_-3px_-3px_6px_#334155]";
  const dayButtonInactive =
    "bg-slate-100 text-purple-800 shadow-[3px_3px_6px_#d1d9e6,_-3px_-3px_6px_#ffffff] hover:text-purple-600 dark:bg-slate-800 dark:text-purple-300 dark:shadow-[3px_3px_6px_#0f172a,_-3px_-3px_6px_#334155] dark:hover:text-purple-400";

  return (
    <div className="space-y-4">
      {/* Repeat dropdown */}
      <div className="flex flex-col gap-1">
        <label className={labelClass}>Repeat</label>
        <select
          value={repeatFrequency}
          onChange={(e) => setRepeatFrequency(e.target.value)}
          className={inputClass}
        >
          <option value="NONE">No repeat</option>
          <option value="DAILY">Daily</option>
          <option value="WEEKLY">Weekly</option>
          <option value="MONTHLY">Monthly</option>
          <option value="YEARLY">Yearly</option>
        </select>
      </div>

      {/* Weekly buttons */}
      {repeatFrequency === "WEEKLY" && (
        <div className="flex flex-wrap gap-2 mt-1">
          {["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"].map((day) => (
            <button
              type="button"
              key={day}
              onClick={() => toggleDay(day)}
              className={`${dayButtonBase} ${
                repeatDays.includes(day) ? dayButtonActive : dayButtonInactive
              }`}
            >
              {day}
            </button>
          ))}
        </div>
      )}

      {/* Interval + Until in one row */}
      {repeatFrequency !== "NONE" && (
        <div className="grid grid-cols-2 gap-4">
          {/* Interval */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Repeat every (interval)</label>
            <input
              type="number"
              min="1"
              value={repeatInterval}
              onChange={(e) => setRepeatInterval(Number(e.target.value))}
              className={inputClass}
            />
          </div>

          {/* Until */}
          <div className="flex flex-col gap-1">
            <label className={labelClass}>Until (optional)</label>
            <input
              type="date"
              value={repeatUntil}
              onChange={(e) => setRepeatUntil(e.target.value)}
              className={inputClass}
            />
          </div>
        </div>
      )}
    </div>
  );
}
