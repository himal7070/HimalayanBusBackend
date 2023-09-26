import './App.css';

import React from 'react';
import './styles/LoginSignup.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import LoginSignup from "./components/LoginSignup";


function App() {
  return (
    <div className="App">
      <header className="App-header">
        {/*<img src={logo} className="App-logo" alt="logo" />*/}
        <LoginSignup />

      </header>
    </div>
  );
}

export default App;
