import './index.css';
import aviaologo from './../../images/aviaologo.png'
import auth from '../../js/Usuario/Auth';
import esqueciSenha from '../../js/Usuario/EsqueciSenha';

import { Modal,Button } from 'react-bootstrap'
import { useState } from 'react'

import $ from 'jquery';


function Login(){

    const [show, setShow] = useState(false);
    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

async function handleSubmit(e){
    e.preventDefault();
    let senha = document.getElementById("senha").value
    let email = document.getElementById("email").value
    await auth(email,senha).then((res)=>{
        if(res.status!==200){

        }
    })

}

async function handleEsqueceuSenha(e){
    e.preventDefault();
    $("#btn-recupera-senha").attr("disabled",true)
    let email = document.getElementById("email-recuperacao").value

    await esqueciSenha(email).then((res)=>{
        if(res.status!==204){
            $("#msgModal").html(`<h3>✗ ${res.data.errors[0]}</h3>`);
            setTimeout(() => {
                $("#msgModal").html("")
                $("#btn-recupera-senha").attr("disabled",false)
            }, 5000);

        }
        else{
            $("#msgModal").html(`<h3>✓Senha enviada para seu email.</h3>`)
            setTimeout(() => {
                handleClose()
                $("#msgModal").html("")

            }, 5000);
        }
    })  
}

    return(
        <div className = "Login">
            <div id ="panel-left">
                <img src={aviaologo} width='50px' alt="LogoAvião"/>
                <h4 className="text">  Slim Aircraft Manual Composer</h4>
                <hr/>
                <div className="mb-3">
                    <center>
                        <img id="foto-perf" src={aviaologo} width= '100px' alt="Logo"/><br/>
                        <h1 className="text">Slim</h1><br/>
                    </center>
                    
                </div>
                <div className="form-div">
                    <form className="row g-3" onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">Email</label>
                            <input type="email" className="form-control" id="email" placeholder="usuario@email.com" />
                        </div>
                        <div className="mb-3">
                            <label htmlFor="senha" className="form-label">Senha</label>
                            <input type="password" className="form-control" id="senha" />
                        </div>
                        <div className="mb-3 form-check col-auto">
                            <input className="form-check-input" type="checkbox" value="" id="conectado" />
                            <label className="form-check-label" htmlFor="conectado">Continuar conectado?</label>
                        </div>
                        <Button type="submit" variant="primary">Login</Button>
                        
                        <br />
                        <Button type="button" variant="dark" onClick={handleShow}>Esqueceu sua senha?</Button>

                    </form>
                </div>
            </div>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                  <Modal.Title>Recuperação de senha</Modal.Title>
                </Modal.Header>
                <Modal.Body id="modal-body">
                    <form className="row g-3">
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">Entre com o email para recuperação de senha.</label>
                            <input type="email" className="form-control" id="email-recuperacao" placeholder="usuario@email.com" />
                        </div>
                    </form>
                    <span id="msgModal"></span>
                </Modal.Body>
                <Modal.Footer>
                  <Button variant="secondary" onClick={handleClose}>Voltar</Button>
                  <Button type="button" id="btn-recupera-senha" variant="primary" onClick={handleEsqueceuSenha}>Confirmar</Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Login;