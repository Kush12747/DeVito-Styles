import React from 'react';
import ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import App from './App';

// Pages
import About from './components/pages/About';
import Contact from './components/pages/Contact';
import Services from './components/AppointmentService/Services';
import Login from './components/pages/Login';
import Register from './components/pages/Register';
import Profile from './components/Users_Profile/Profile';
import AdminDashboard from './components/Admin/AdminDashboard';
import FormSubmit from './components/pages/FormSubmit';
import InitialPage from './components/pages/InitialPage';
import Landing from './components/pages/Landing';
import BookingPage from './components/AppointmentService/BookingPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        index: true,
        element: <Landing />
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
      },
      {
        path: 'book/:serviceId',
        element: <BookingPage />
      }
    ]
  }
])

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <RouterProvider router={router}/>
  </React.StrictMode>,
)
