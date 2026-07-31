import { useState, useEffect } from "react";
import { Sun, Moon } from "lucide-react";

export default function ThemeSwitch() {
  // Initialize state from localStorage or default to 'light'
  const [theme, setTheme] = useState(() => {
    const savedTheme = localStorage.getItem("theme");
    if (savedTheme) return savedTheme;
    // If no theme is saved, check system preference
    if (window.matchMedia("(prefers-color-scheme: dark)").matches) {
      return "dark";
    }
    return "light";
  });

  // Apply theme to the document and save to localStorage
  useEffect(() => {
    const root = document.documentElement;
    // Set the data-theme attribute for custom CSS (like the calendar)
    root.setAttribute("data-theme", theme);
    // Add/remove the .dark class for Tailwind's dark mode
    if (theme === "dark") {
      root.classList.add("dark");
    } else {
      root.classList.remove("dark");
    }
    localStorage.setItem("theme", theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(theme === "light" ? "dark" : "light");
  };

  return (
    <button
      onClick={toggleTheme}
      className="w-14 h-8 rounded-full p-1 flex items-center transition-colors duration-300 
                 bg-slate-100 shadow-[inset_3px_3px_5px_#d1d9e6,_inset_-3px_-3px_5px_#ffffff]
                 dark:bg-slate-800 dark:shadow-[inset_3px_3px_5px_#0f172a,_inset_-3px_-3px_5px_#334155]"
    >
      <div
        className={`w-6 h-6 rounded-full flex items-center justify-center transform transition-transform duration-300
                  bg-slate-100 shadow-[3px_3px_6px_#d1d9e6,_-3px_-3px_6px_#ffffff]
                  dark:bg-slate-700 dark:shadow-[3px_3px_6px_#0f172a,_-3px_-3px_6px_#334155]
                  ${theme === "dark" ? "translate-x-6" : "translate-x-0"}`}
      >
        {theme === "dark" ? (
          <Moon size={14} className="text-purple-300" />
        ) : (
          <Sun size={14} className="text-yellow-500" />
        )}
      </div>
    </button>
  );
}
