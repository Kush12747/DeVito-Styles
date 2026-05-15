import { useState } from 'react'
import { Outlet } from "react-router-dom";
import Navbar from './components/Navbar/Navbar';
import './App.css'

function App() {
    const [loggedInUser, setLoggedInUser] = useState(null)

    return (
    <div>
        <Navbar />
        <Outlet context={{ loggedInUser, setLoggedInUser }} />
    </div>
    )
}

export default App
