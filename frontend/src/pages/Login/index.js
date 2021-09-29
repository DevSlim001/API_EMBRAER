import './index.css';
import aviaologo from './../../images/aviaologo.png'
import auth from './../../js/Usuario/Auth';
import esqueciSenha from './../../js/Usuario/EsqueciSenha';


import { Modal,Button, Alert, Spinner, Container, Row, Col, Form } from 'react-bootstrap'
import { React, useState } from 'react'

import { useHistory } from 'react-router-dom';

import $ from 'jquery';


function Login(){
    const hist = useHistory();

    const [showModal, setShowModal] = useState(false);
    const handleCloseModal = () => setShowModal(false);
    const handleShowModal = () => setShowModal(true);
    const [showMsgLogin, setShowMsgLogin] = useState(false);
    const [msgModalLogin, setMsgModalLogin] = useState("");
    const [msgAlertLogin, setMsgAlertLogin] = useState("");

    

    const [showSpinnerEsqueciSenha, setShowSpinnerEsqueciSenha] = useState(false);



async function handleSubmit(e){
    e.preventDefault();
    let senha = $("#senha").val()
    let email = $("#email").val()
    let conectado = $("#conectado").is(':checked')
    
    await auth(email,senha,conectado).then((res)=>{
        if(res.status!==200){
            setShowMsgLogin(true)
            setMsgAlertLogin(`${res.data.errors[0]}`)
            setTimeout(() => {
                setShowMsgLogin(false)
            }, 5500);
            
        }
        else{
            localStorage.setItem("token",res.data.token)
            localStorage.setItem("manterConectado",conectado)
            hist.push("/home");
        }
    })

}

async function handleEsqueceuSenha(e){
    e.preventDefault();
    let btn = $("#btn-recupera-senha")
    btn.attr("disabled",true)
    setShowSpinnerEsqueciSenha(true)
    let email = $("#email-recuperacao").val()
    await esqueciSenha(email).then((res)=>{
        setShowSpinnerEsqueciSenha(false)
        if(res.status!==204){
            setMsgModalLogin(`✗ ${res.data.errors[0]}`)
            setTimeout(() => {
                btn.attr("disabled",false)
            }, 4000);
        }
        else{
            setMsgModalLogin(`✓ Senha enviada para seu email.`)
            setTimeout(() => {
                handleCloseModal()
            }, 4000);
        }
    })  
}
    return(
        <Container fluid id="login">
            <Row>
                <Col sm={6} id="col-form-login">
                    <Row>
                        <center>
                            <img id="foto-perf" src={aviaologo} width= '100em' alt="Logo"/><br/>
                            <h1 className="text">AirDocs</h1>
                            
                        </center>
                    </Row>
                    <Row xs={2}>

                            <Form onSubmit={handleSubmit}>
                                {showMsgLogin &&
                                    <Alert id="msgLogin" key="alertLogin" variant="danger" onClose={() => setShowMsgLogin(false)} dismissible>{msgAlertLogin}</Alert>
                                }
                                <Form.Group className="mb-3" controlId="email">
                                  <Form.Label>Email</Form.Label>
                                  <Form.Control type="email" placeholder="usuario@email.com" />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="senha">
                                  <Form.Label>Senha</Form.Label>
                                  <Form.Control type="password" placeholder="Senha" />
                                </Form.Group>
                                <Form.Group className="mb-3" value={true} controlId="conectado">
                                  <Form.Check type="checkbox" label="Continuar conectado?" />
                                </Form.Group>
                                <br />
                                <div className="d-grid gap-2">
                                    <Button type="submit" id="btn-submit" variant="primary">Login</Button>
                                    <Button type="button" variant="dark"  onClick={handleShowModal}>Esqueceu sua senha?</Button>
                                </div>
                            </Form>
                    </Row>
                </Col>
            </Row>
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
                    <span id="msgModal">{msgModalLogin}</span>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseModal}>Voltar</Button>
                    <Button type="button" id="btn-recupera-senha" variant="primary" onClick={handleEsqueceuSenha}>
                        {showSpinnerEsqueciSenha &&
                            <Spinner id="spinner-esqueci-senha" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                        }
                        Confirmar
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
}

export default Login;