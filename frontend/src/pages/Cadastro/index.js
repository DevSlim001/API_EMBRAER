import './index.css';
import { React, useState } from 'react'
import createUser from '../../js/Usuario/CreateUser';

import { Button, Form, Spinner, Alert } from 'react-bootstrap'

import $ from 'jquery';

function Cadastro(){
    const [showMsgCadastroSucesso, setShowMsgCadastroSucesso] = useState(false);
    const [showMsgCadastroErro, setShowMsgCadastroErro] = useState(false);


    async function handleCadastro(e){
        e.preventDefault()
        let spinner = $("#spinner-cadastro")
        let btn = $("#btn-cadastro")
        btn.attr("disabled",true)
        spinner.css("display","inline-block")
        let usuario = {};
        $.each($('#form-cadastro').serializeArray(), function(i, field) {
            usuario[field.name] = field.value;
        });
        await createUser(usuario).then((res)=>{
            spinner.css("display","none")
            if(res.status!==201){
                setShowMsgCadastroErro(true)
                let msg = $("#msgCadastroErro")
                msg.html(`<h6>✗ ${res.data.errors[0]}</h6>`)
                btn.attr("disabled",false)
                setTimeout(() => {
                    setShowMsgCadastroErro(false)
                    msg.html(``)
                }, 5000);
            }else{
                setShowMsgCadastroSucesso(true)
                let msg = $("#msgCadastroSucesso")
                msg.html(`<h6>✓Cadastro realizado com sucesso.</h6>`)
                btn.attr("disabled",false)
                setTimeout(() => {
                    setShowMsgCadastroSucesso(false)
                    msg.html(``)
                }, 5000);
            }
        })

    }
    return(
        <div id="form-div">
            <Form id="form-cadastro" onSubmit={handleCadastro}>
                {showMsgCadastroSucesso &&
                    <Alert id="msgCadastroSucesso"  key="alertCadastroSucesso" onClose={() => setShowMsgCadastroSucesso(false)}></Alert>
                }
                {showMsgCadastroErro &&
                    <Alert id="msgCadastroErro" variant="danger" key="alertCadastroErro" onClose={() => setShowMsgCadastroErro(false)}></Alert>
                }
                <Form.Group className="mb-3" controlId="email">
                    <Form.Label>Email</Form.Label>
                    <Form.Control type="email" name="email" placeholder="email@email.com" required/>
                </Form.Group>
                <Form.Group className="mb-3" controlId="nome">
                    <Form.Label>Nome</Form.Label>
                    <Form.Control type="text" name="nome" placeholder="Fulano" required/>
                </Form.Group>
                <Form.Group className="mb-3" controlId="role">
                    <Form.Label>Função</Form.Label>
                    <Form.Select aria-label="" defaultValue="" name="role" required>
                        <option value="" disabled >Selecione a função do usuário</option>
                        <option value="USER">Usuário</option>
                        <option value="ADMIN">Administrador</option>
                    </Form.Select>
                </Form.Group>
                <Button id="btn-cadastro" variant="primary" type="submit">
                    <Spinner id="spinner-cadastro" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                    Cadastrar
                </Button>
            </Form>
        </div>
    );
}

export default Cadastro;