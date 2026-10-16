/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        rathore: {
          saffron: "#EA580C",
          orange: "#F97316",
          blue: "#1E3A8A",
          navy: "#0F172A",
          gold: "#D97706",
          green: "#059669",
          purple: "#9333EA",
        }
      },
      fontFamily: {
        devanagari: ['"Noto Sans Devanagari"', 'Poppins', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
