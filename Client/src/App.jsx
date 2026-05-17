import { useState } from 'react'
import { Outlet } from "react-router-dom";
import Navbar from './components/Navbar/Navbar';
import './App.css'

function App() {
    const [loggedInUser, setLoggedInUser] = useState(() => {
        const savedUser = localStorage.getItem("loggedInUser")

        return savedUser ? JSON.parse(savedUser) : null
    })

    return (
    <div>
        <Navbar loggedInUser={loggedInUser} />
        <Outlet context={{ loggedInUser, setLoggedInUser }} />
    </div>
    )
}

export default App
