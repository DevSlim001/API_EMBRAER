import './index.css';
import aviaologo from './../../images/aviaologo.png'
import auth from './../../js/Usuario/Auth';
import esqueciSenha from '../../js/Usuario/EsqueciSenha';
import Header from './../../components/Header';


import { Modal,Button, Alert, Spinner } from 'react-bootstrap'
import { React, useState } from 'react'

import { useHistory } from 'react-router-dom';

import $ from 'jquery';


function Login(){
    const hist = useHistory();

    const [showModal, setShowModal] = useState(false);
    const handleCloseModal = () => setShowModal(false);
    const handleShowModal = () => setShowModal(true);
    const [showMsgLogin, setShowMsgLogin] = useState(false);


async function handleSubmit(e){
    e.preventDefault();
    let senha = $("#senha").val()
    let email = $("#email").val()

    await auth(email,senha).then((res)=>{
        if(res.status!==200){
            setShowMsgLogin(true)
            let alert = `${res.data.errors[0]}`
            $("#msgLogin").html(alert)
            setTimeout(() => {
                setShowMsgLogin(false)
            }, 5000);
            
        }
        else{
            localStorage.setItem("token",res.data.token)
            hist.push("/home");
        }
    })

}

async function handleEsqueceuSenha(e){
    e.preventDefault();

    let spinner = $("#spinner-esqueci-senha")
    let btn = $("#btn-recupera-senha")
    let msg = $("#msgModal")
    btn.attr("disabled",true)
    spinner.css("display","inline-block")

    let email = $("#email-recuperacao").val()
    await esqueciSenha(email).then((res)=>{
        spinner.css("display","none")
        if(res.status!==204){
            msg.html(`<h4>✗ ${res.data.errors[0]}</h4>`);
            setTimeout(() => {
                msg.html("")
                btn.attr("disabled",false)
            }, 4000);
        }
        else{
            msg.html(`<h4>✓Senha enviada para seu email.</h4>`)
            setTimeout(() => {
                handleCloseModal()
                msg.html("")
            }, 4000);
        }
    })  
}

    return(
        <div className = "Login">
            <div id ="panel-left">

                <Header />
                <hr/>
                <div className="mb-3">
                    <center>
                        <img id="foto-perf" src={aviaologo} width= '100em' alt="Logo"/><br/>
                        <h1 className="text">Slim</h1><br/>
                    </center>
                    
                </div>
                <div className="form-div">
                    {showMsgLogin &&
                        <Alert id="msgLogin" key="alertLogin" variant="danger" onClose={() => setShowMsgLogin(false)} dismissible></Alert>
                    }
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
                        <Button type="button" variant="dark" onClick={handleShowModal}>Esqueceu sua senha?</Button>

                    </form>
                </div>
            </div>
            <Modal show={showModal} onHide={handleCloseModal}>
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
                    <Button variant="secondary" onClick={handleCloseModal}>Voltar</Button>
                    <Button type="button" id="btn-recupera-senha" variant="primary" onClick={handleEsqueceuSenha}>
                    <Spinner id="spinner-esqueci-senha" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                    Confirmar
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Login;