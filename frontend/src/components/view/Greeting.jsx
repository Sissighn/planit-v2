import { useEffect, useState } from "react";

export default function Greeting() {
  const [greeting, setGreeting] = useState("");

  useEffect(() => {
    const updateGreeting = () => {
      const hour = new Date().getHours();
      if (hour < 12) setGreeting("Good Morning!");
      else if (hour < 18) setGreeting("Good Afternoon!");
      else if (hour < 22) setGreeting("Good Evening!");
      else setGreeting("Good Night!");
    };

    updateGreeting();
    const interval = setInterval(updateGreeting, 1000 * 60 * 10); // alle 10 Minuten prüfen
    return () => clearInterval(interval);
  }, []);

  return (
    <p className="text-lg font-amsterdam text-black/80 mt-1 ml-1 dark:text-slate-300/80">
      {greeting}
    </p>
  );
}
