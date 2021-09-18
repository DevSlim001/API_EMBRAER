import React from 'react';
import aviaologo from './../../images/aviaologo.png'
import "./index.css"



function Header(props) {

    return (
        <header id={props.id}>
            <img src={aviaologo} id="logo-header" alt="LogoAvião"/>
            <h4> Slim Manual Composer</h4>

        </header>
    );
}

export default Header