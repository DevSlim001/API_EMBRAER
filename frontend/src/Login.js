import './index.css';
import aviaobackground from './aviaobackground.jpg'
import aviaologo from './aviaologo.png'
import Cadastro from './Cadastro';
import { BrowserRouter as Router, Route, Link } from "react-router-dom";


function Login(){
    return(
        <div className = "Login">
            <div id ="panel-left">
                <img src={aviaologo} width='50px'/>
                <h4 className="text">  Slim Aircraft Manual Composer</h4>
                <hr/>
                <img id="foto-perf" src={aviaologo} width= '150px'/>
                <h1 className="text">Slim</h1><br/>

                <div id="login-inputs">
                    <h6 className="text">E-mail</h6><br/>
                    <input type='email'/><br/><br/>

                    <h6 className="text">Senha</h6><br/>
                    <input type='password'/><br/><br/>

                    <input type='checkbox'/>
                    <h6 className="text link">Continuar conectado</h6><br/><br/>

                    <a href="#"><h6 className="text link">Esqueceu a senha?</h6></a>
                    <button onClick="">Login</button>
                </div>

            </div>
        </div>
    );
  
    
}

export default Login;