import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import Login from './Login';
import Cadastro from './Cadastro';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";



ReactDOM.render(
  <React.StrictMode>
    <Login />
  </React.StrictMode>,
  document.getElementById('root')
);

reportWebVitals();
