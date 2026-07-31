import daisyui from "daisyui";

export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      borderRadius: {
        soft: "1.25rem",
      },
    },
  },
  plugins: [daisyui],
  daisyui: {
    themes: ["light", "dark"],
    styled: true,
    base: true, // Basis-Styles für DaisyUI
    utils: true, // Utility-Klassen aktivieren
    logs: false,
  },
};
