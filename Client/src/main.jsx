import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import App from './App';

// Pages
import Home from './components/pages/Home';
import About from './components/pages/About';
import Contact from './components/pages/Contact';
import Services from './components/pages/Services';
import Login from './components/pages/Login';
import Register from './components/pages/Register';
import Profile from './components/pages/Profile';
import AdminDashboard from './components/pages/AdminDashboard';
import FormSubmit from './components/pages/FormSubmit';
import InitialPage from './components/pages/InitialPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        index: true,
        element: <Home />
      },
      {
        path: 'about',
        element: <About />
      },
      {
        path: 'contact',
        element: <Contact />        
      },
      {
        path: 'services',
        element: <Services />
      },
      {
        path: 'login',
        element: <Login />
      },
      {
        path: 'register',
        element: <Register />
      },
      {
        path: 'profile',
        element: <Profile />
      },
      {
        path: 'admin',
        element: <AdminDashboard />
      },
      {
        path: 'submit',
        element: <FormSubmit />
      },
      {
        path: 'initial',
        element: <InitialPage />
      }
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <RouterProvider router={router}/>
  </React.StrictMode>,
)
