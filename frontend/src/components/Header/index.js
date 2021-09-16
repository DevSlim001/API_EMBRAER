import React from 'react';
import aviaologo from './../../images/aviaologo.png'
import "./index.css"

function Header(props) {
    return (
        <header id={props.id}>
            <img src={aviaologo} width='50px' alt="LogoAvião"/>
            <h4 className="text"> Slim Aircraft Manual Composer</h4>
        </header>
    );
}

export default Header