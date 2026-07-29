import { useState } from 'react'
import { Outlet } from "react-router-dom";
import Navbar from './components/Navbar/Navbar';
import ChatBot from './components/Chatbot/ChatBot';
import ScrollToTop from './components/ScrollToTop';

import './App.css'

function App() {
    const [loggedInUser, setLoggedInUser] = useState(() => {
        const savedUser = localStorage.getItem("loggedInUser")

        return savedUser ? JSON.parse(savedUser) : null
    })

    return (
    <div>
        <ScrollToTop />
        <Navbar loggedInUser={loggedInUser} />
        <Outlet context={{ loggedInUser, setLoggedInUser }} />
        <ChatBot />
    </div>
    )
}

export default App;
