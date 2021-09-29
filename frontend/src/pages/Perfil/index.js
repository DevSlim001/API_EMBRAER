import './index.css';
import { React, useState, useEffect } from 'react'

import { Button, Form, Spinner, Alert } from 'react-bootstrap'
import getUser from './../../js/Usuario/GetUser'
import patchUpdateUser from './../../js/Usuario/PatchUpdate'
import updateSenha from './../../js/Usuario/UpdateSenha'

import $ from 'jquery';

function Perfil(){
    const [showMsgPerfilSucesso, setShowMsgPerfilSucesso] = useState(false);
    const [showMsgPerfilErro, setShowMsgPerfilErro] = useState(false);
    const [msgAlert, setMsgAlert] = useState("");

    const [usuario, setUsuario] = useState({nome:"",role:"",email:""});
    const [showSpinnerPerfilInfo, setShowSpinnerPerfilInfo] = useState(false);
    const [showSpinnerPerfilSenha, setShowSpinnerPerfilSenha] = useState(false);



    const handleUpdateInfo = (e) => {
        e.preventDefault()
        let btn = $("#btn-perfil-info")
        btn.attr("disabled",true)

        setShowSpinnerPerfilInfo(true)
        const form = $("#form-perfil-info")
        let usuarioForm = form.serializeArray()
        let nome = usuarioForm[0].value
        let email = usuarioForm[1].value
        let usuarioAtualizado = []

        if(nome&&(nome!==usuario.nome)){
            usuarioAtualizado.push({"op":"replace","path":`/nome`,"value":nome})
        }
        if(email&&(email!==usuario.email)){
            usuarioAtualizado.push({"op":"replace","path":`/email`,"value":email})
        }
        
        if(usuarioAtualizado.length>0){
            patchUpdateUser(usuarioAtualizado).then((res) => {
                if(res.status!==200){
                    setShowSpinnerPerfilInfo(false)
                    setShowMsgPerfilErro(true)
                    setMsgAlert(`✗ ${res.data.errors[0]}`)
                    btn.attr("disabled",false)
                    setTimeout(() => {
                        setShowMsgPerfilErro(false)
                    }, 5500);
                } else {
                    setShowSpinnerPerfilInfo(false)
                    setShowMsgPerfilSucesso(true)
                    setMsgAlert('✓ Informações atualizadas com sucesso')
                    localStorage.setItem("token",res.data.token)
                    btn.attr("disabled",false)
                    setTimeout(() => {
                        setShowMsgPerfilSucesso(false)
                    }, 5000);
                }
            })
        }
        
    }
    const handleUpdateSenha = (e) => {
        e.preventDefault()
        let btn = $("#btn-perfil-senha")
        btn.attr("disabled",true)

        setShowSpinnerPerfilSenha(true)
        const form = $("#form-perfil-senha")
        let senhaForm = form.serializeArray()
        let senha = senhaForm[0].value
        let confirma_senha = senhaForm[1].value
        
        if(senha&&(senha===confirma_senha)){
            let data = {senha:senha}
            updateSenha(data).then((res) => {
                console.log(res)
                if(res.status!==204){
                    setShowSpinnerPerfilSenha(false)
                    setShowMsgPerfilErro(true)
                    setMsgAlert(`✗ ${res.data.errors[0]}`)
                    btn.attr("disabled",false)
                    setTimeout(() => {
                        setShowMsgPerfilErro(false)
                    }, 5500)
                } else {
                    setShowSpinnerPerfilSenha(false)
                    setShowMsgPerfilSucesso(true)
                    setMsgAlert('✓ Senha atualizada com sucesso')
                    btn.attr("disabled",false)
                    setTimeout(() => {
                        setShowMsgPerfilSucesso(false)
                    }, 5000)
                }
            })
        } else {
            setShowSpinnerPerfilSenha(false)
            setShowMsgPerfilErro(true)
            setMsgAlert(`✗ As senhas não se coincidem.`)
            btn.attr("disabled",false)
            setTimeout(() => {
                setShowMsgPerfilErro(false)
            }, 5500)
        }
    }

    const loadUser = () => {
        getUser().then((res) => {
            setUsuario(res.data)
        }).catch((err) => {
            console.log(err)
        })
    }

    useEffect(() => {
        loadUser()
    }, [])

    return(
        <div id="form-div">
            <Form id="form-perfil-info" onSubmit={handleUpdateInfo}>
                {showMsgPerfilSucesso &&
                    <Alert id="msgPerfilSucesso"  key="alertPerfilSucesso" onClose={() => setShowMsgPerfilSucesso(false)}>{msgAlert}</Alert>
                }
                {showMsgPerfilErro &&
                    <Alert id="msgPerfilErro" variant="danger" key="alertPerfilErro" onClose={() => setShowMsgPerfilErro(false)}>{msgAlert}</Alert>
                }
                <Form.Group className="mb-3" controlId="nome">
                    <Form.Label>Nome</Form.Label>
                    <Form.Control defaultValue={usuario.nome} type="text" name="nome" placeholder="Fulano" />
                </Form.Group>
                <Form.Group className="mb-3" controlId="email">
                    <Form.Label>Email</Form.Label>
                    <Form.Control defaultValue={usuario.email} type="email" name="email" placeholder="email@email.com" />
                </Form.Group>
                <Button id="btn-perfil-info" variant="primary" type="submit">
                    {showSpinnerPerfilInfo &&
                        <Spinner id="spinner-perfil-info" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                    }
                    Atualizar
                </Button>
            </Form>
            
            <Form id="form-perfil-senha" onSubmit={handleUpdateSenha}>
                <Form.Group className="mb-3" controlId="senha">
                    <Form.Label>Nova senha</Form.Label>
                    <Form.Control type="password" name="senha" />
                </Form.Group>
                <Form.Group className="mb-3" controlId="confirma-senha">
                    <Form.Label>Confirme sua senha</Form.Label>
                    <Form.Control type="password" name="confirma-senha" />
                </Form.Group>
                <Button id="btn-perfil-senha" variant="primary" type="submit">
                    {showSpinnerPerfilSenha &&
                        <Spinner id="spinner-perfil-senha" as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                    }
                    Atualizar senha
                </Button>
            </Form>
        </div>
    );
}

export default Perfil;