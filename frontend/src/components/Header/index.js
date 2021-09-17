import React from 'react';
import aviaologo from './../../images/aviaologo.png'
import "./index.css"
import { FaSignOutAlt } from 'react-icons/fa'
import { useHistory } from 'react-router-dom';



function Header(props) {
    const hist = useHistory();
    const logout = () =>(
        localStorage.removeItem("token"),
        hist.push("/")
        
    )
    return (
        <header id={props.id}>
            <img src={aviaologo} id="logo-header" alt="LogoAvião"/>
            <h4> Slim Manual Composer</h4>
            <FaSignOutAlt onClick={logout} id="icone-logout" />
        </header>
    );
}

export default Header