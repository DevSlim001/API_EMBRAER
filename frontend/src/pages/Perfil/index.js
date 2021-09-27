import './index.css';
import { React, useState } from 'react'

import { Button, Form, Spinner, Alert } from 'react-bootstrap'

import $ from 'jquery';

function Perfil(){
    const [showMsgPerfilSucesso, setShowMsgPerfilSucesso] = useState(false);
    const [showMsgPerfilErro, setShowMsgPerfilErro] = useState(false);
    const handleUpdate = () => {

    }

    return(
        <div id="form-div">
            <Form id="form-perfil" onSubmit={handleUpdate}>
                {showMsgPerfilSucesso &&
                    <Alert id="msgPerfilSucesso"  key="alertPerfilSucesso" onClose={() => setShowMsgPerfilSucesso(false)}></Alert>
                }
                {showMsgPerfilErro &&
                    <Alert id="msgPerfilErro" variant="danger" key="alertPerfilErro" onClose={() => setShowMsgPerfilErro(false)}></Alert>
                }
                <Form.Group className="mb-3" controlId="email">
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" name="email" placeholder="email@email.com" required/>
                </Form.Group>
                <Form.Group className="mb-3" controlId="nome">
                    <Form.Label>Nome</Form.Label>
                    <Form.Control type="text" name="nome" placeholder="Fulano" required/>
                </Form.Group>
                <Button id="btn-perfil" variant="primary" type="submit">
                    <Spinner id="spinner-perfil" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                    Cadastrar
                </Button>
            </Form>
        </div>
    );
}

export default Perfil;